// Client for the DefReas API server. No caching is performed, and all data
// returned by methods should be treated as immutable.
export class Client {
  constructor(baseURL) {
    this.baseURL = baseURL;
  }

  send(req, body, onSuccess, onError) {
    req.onload = () => {
      if (req.status !== 200) {
        onError(new Error(`Bad response: ${req.status} ${req.statusText}`));
        return;
      }

      onSuccess(JSON.parse(req.response));
    }

    req.onerror = () => {
      onError(new Error(`Failed to make request.`));
    }

    if (body === null) {
      req.send();
    } else {
      req.send(body);
    }
  }

  get(route, onSuccess, onError) {
    let url = this.baseURL + "/" + route;
    let req = new XMLHttpRequest();
    req.open("GET", url);

    this.send(req, null, onSuccess, onError);
  }

  post(route, body, onSuccess, onError) {
    let url = this.baseURL + "/" + route;
    let req = new XMLHttpRequest();
    req.open("POST", url);
    req.setRequestHeader("Content-Type", "application/json");
    
    this.send(req, body, onSuccess, onError);
  }

  // Fetches an instance of the task with the given id.
  getTask(id, onSuccess, onError) {
    this.get("test", task => { // TODO
      let inputSchema, outputSchema;
      try {
        inputSchema = Schema.fromObject(task.inputSchema);
        outputSchema = Schema.fromObject(task.outputSchema);
      } catch(err) {
        onError(err);
        return;
      }

      onSuccess(new Task(
        task.id,
        task.description,
        inputSchema,
        outputSchema
      ));
    }, onError);
  }

  // Runs the given task.
  runTask(task, input, onSuccess, onError) {
    if (!task.inputSchema.conforms(input)) {
      onError(new Error(`Input value for task ${task.id} was malformed.`));
      return;
    }

    this.post("test", JSON.stringify(input.toObject()), value => {
      try {
        onSuccess(task.outputSchema.decode(value));
      } catch(err) {
        onError(err);
      }
    }, onError);
  }
}

// The types of values that can be passed as inputs/outputs to tasks.
const ValueTypes = Object.freeze({
  BOOLEAN: "boolean",
  INTEGER: "integer",
  STRING: "string",
  SERIAL: "serial",
  PAIR: "pair",
});

// Values represent the inputs/outputs to tasks on the RPC server.
// TODO use a type system, these typeof checks are tedious.
class Value {
  constructor(type) {
    this.type = type;
  }
}

export class BooleanValue extends Value {
  constructor(value) {
    if (typeof value !== "boolean") {
      throw new Error(`Constructor argument for BooleanValue should be boolean: ${typeof value}`);
    }

    super(ValueTypes.BOOLEAN);
    this.value = value;
  }

  toObject() {
    return this.value;
  }
}

export class IntegerValue extends Value {
  constructor(value) {
    if (typeof value !== "number") {
      throw new Error(`Constructor argument for IntegerValue should be number: ${typeof value}`);
    }

    super(ValueTypes.INTEGER);
    this.value = value;
  }

  toObject() {
    return this.value;
  }
}

export class StringValue extends Value {
  constructor(value) {
    if (typeof value !== "string") {
      throw new Error(`Constructor argument for StringValue should be string: ${typeof value}`);
    }

    super(ValueTypes.STRING);
    this.value = value;
  }

  toObject() {
    return this.value;
  }
}

export class PairValue extends Value {
  constructor(fst, snd) {
    if (!(fst instanceof Value)) {
      throw new Error(`First constructor argument for PairValue should be a value: ${typeof fst}`);
    }

    if (!(snd instanceof Value)) {
      throw new Error(`First constructor argument for PairValue should be a value: ${typeof snd}`);
    }

    super(ValueTypes.PAIR);
    this.fst = fst;
    this.snd = snd;
  }

  toObject() {
    return {
      fst: this.fst.toObject(),
      snd: this.snd.toObject()
    };
  }
}

export class SerialValue extends Value {
  constructor(serial, value) {
    if (typeof serial !== "string") {
      throw new Error(`First constructor argument for SerialValue should be string: ${typeof serial}`);
    }

    if (!(value instanceof StringValue)) {
      throw new Error(`Second constructor argument for SerialValue should be a StringValue: ${value}`);
    }

    super(ValueTypes.SERIAL);
    this.serial = serial;
    this.value = value;
  }

  toObject() {
    return {
      serial: this.serial,
      value: this.value.toObject()
    };
  }
}

// A schema describes the kinds of values a task accepts as input/output.
class Schema {
  constructor(type, description) {
    this.type = type;
    this.description = description;
  }

  static fromObject(obj) {
    switch(obj.type) {
      case ValueTypes.BOOLEAN:
      case ValueTypes.INTEGER:
      case ValueTypes.STRING:
        return new PrimitiveSchema(obj.type, obj.description);

      case ValueTypes.PAIR:
        return new PairSchema(
          obj.description,
          Schema.fromObject(obj.fst),
          Schema.fromObject(obj.snd)
        );

      case ValueTypes.SERIAL:
        return new SerialSchema(
          obj.description,
          obj.serial,
          Schema.fromObject(obj.value)
        );

      default:
        throw new Error(`Given object is not a known schema type: ${obj}`);
        break;
    }
  }
}

class PrimitiveSchema extends Schema {
  constructor(type, description) {
    super(type, description);
  }

  conforms(value) {
    return typeof value.value === this.type;
  }

  decode(obj) {
    switch(this.type) {
      case ValueTypes.BOOLEAN:
        return new BooleanValue(obj);

      case ValueTypes.STRING:
        return new StringValue(obj);

      case ValueTypes.INTEGER:
        return new IntegerValue(obj);

      default:
        // TODO: Use a type system, this is gross.
        throw new Error("IMPOSSIBLE SITUATION");
    }
  }
}

class PairSchema extends Schema {
  constructor(description, fstSchema, sndSchema) {
    super(ValueTypes.PAIR, description);

    this.fstSchema = fstSchema;
    this.sndSchema = sndSchema;
  }

  conforms(value) {
    if (value.type !== this.type) {
      return false;
    }

    return this.fstSchema.conforms(value.fst) 
      && this.sndSchema.conforms(value.snd);
  }

  decode(obj) {
    return new PairValue(
      this.fstSchema.decode(obj.fst),
      this.sndSchema.decode(obj.snd)
    )
  }
}

class SerialSchema extends Schema {
  constructor(description, serial, valueSchema) {
    super(ValueTypes.SERIAL, description);

    this.serial = serial;
    this.valueSchema = valueSchema;
  }

  conforms(value) {
    if (value.type !== this.type) {
      return false;
    }

    return this.valueSchema.conforms(value.value);
  }

  decode(obj) {
    return new SerialValue(obj.serial, this.valueSchema.decode(obj.value));
  }
}

// A task that can be run on the RPC server. Input values for the task can
// be constructed from the input schema, and returned values will conform to
// the specification in the output schema.
class Task {
  constructor(id, description, inputSchema, outputSchema) {
    this.id = id;
    this.description = description;
    this.inputSchema = inputSchema;
    this.outputSchema = outputSchema;
  }
}
