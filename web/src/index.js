// NOTE: This is a prototype. At some point we'll want to build a more 
// sophisticated editor, and this will need to be refactored into a React 
// app (or whatever other framework we go for).
import * as CodeMirror from "codemirror";
import "codemirror/lib/codemirror.css";
import "codemirror/addon/hint/show-hint.css";
import "codemirror/addon/hint/show-hint";

// Language server plugin for Codemirror.
import "lsp-editor-adapter/lib/codemirror-lsp.css";
import { LspWsConnection, CodeMirrorAdapter } from "lsp-editor-adapter";

// RPC client for the DefReas API server.
import * as Rpc from "./lib/rpc.js";

// TODO TESTING REMOVE ME //
let client = new Rpc.Client("http://localhost:8080");
client.getTask("id", 
  task => {
    let value = task.inputSchema.decode({
      fst: true,
      snd: {
        serial: "sb_std",
        value: "true"
      }
    });

    client.runTask(task, value, console.log, console.log);
  }
, console.log);
////////////////////////////

// +---------------------------+
// |        INITIALISATION       |
// +-----------------------------+

// Element IDs for embedding various things.
const editorID = "editor";
const selectID = "select";
const responseID = "text";

// Endpoint for various kinds of requests.
const apiServerURL = "http://localhost:8080";
const languageServerURL = "ws://localhost:8081/defreas";

let editor = CodeMirror(
  document.getElementById(editorID),
  {
    value: "A -> B | C",
    gutters: ["CodeMirror-lsp"],
    lineNumbers: true,
  }
);

// Configuration for the language server proxy.
let connectionOptions = {
  // LSP proxy, can be run with npm scripts from the "web" folder.
  serverUri: languageServerURL,
  documentText: () => editor.getValue(),
  languageId: "defreas",

  // Document URIs for our language server, currently irrelevant as it uses
  // a virtual file system for editing, different one per process.
  rootUri: "file:///defreas/web",
  documentUri: "file:///defreas/web/knowledge_base.defreas",
};

// Connection to the language server proxy.
let conn = new LspWsConnection(connectionOptions)
  .connect(new WebSocket(connectionOptions.serverUri));

// Configuration on the underlying editor.
let adapter = new CodeMirrorAdapter(conn, {
  quickSuggestionsDelay: 100,
}, editor);

// Initialise the tasks in the select bar.
document.getElementById(selectID).innerHTML = fetchTaskEndpoints()
  .map(e => "<option value=\""+e.endpoint+"\">"+e.name+"<\option>")
  .join("\n");

// +-----------------------------+
// |        FUNCTIONALITY        |
// +-----------------------------+

window.runTask = function() {
  let taskEndpoint = document.getElementById(selectID).value;
  if (taskEndpoint == "") {
    alert("No task was selected.");
    return;
  }

  let body = JSON.stringify(
    {
      syntaxID: "standard", // TODO: fetch from API!
      context: {
        knowledgeBase: editor.getValue().split("\n")
      }
    }
  );

  let req = new XMLHttpRequest();
  req.open("POST", taskEndpoint);
  req.setRequestHeader("Content-Type", "application/json");
  req.send(body);

  req.onload = (_) => {
    let res = JSON.parse(req.response);
    if (res.err != null) {
      setResponseText("<b>Error:</b> " + res.err);
    } else {
      // TODO: handle different task output types - need to inspect tasks.
      setResponseText("<b>Result:</b> " + res.context.knowledgeBase);
    }
  };
}

// +-----------------------------+
// |            UTIL             |
// +-----------------------------+

// Fetches and constructs a list of task API endpoints.
function fetchTaskEndpoints() {
  return [
    // TODO: fetch from API, for the demo we just hardcode.
    {
      name: "to_cnf",
      endpoint: apiServerURL + "/tasks/stdlib_prop/propositional_logic/to_cnf",
    }
  ];
}

function setResponseText(text) {
  document.getElementById(responseID).innerHTML = "<i>" + text + "</i>";
}
