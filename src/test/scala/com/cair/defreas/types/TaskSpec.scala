package com.cair.defreas.types

import org.scalatest.flatspec._
import org.scalatest.matchers._

class TaskSpec extends AnyFlatSpec with should.Matchers {
  // Example logic AST for testing.
  case class TestLogic() {}
  implicit val testLogicInstance: Logic[TestLogic] =
    new Logic[TestLogic] {
      val id = "test_logic"
    }

  "A Task" should "be creatable with implicit Input/Output instances" in {
    import TaskInstances._

    val task = new Task[TestLogic, List[TestLogic], Boolean](
      "return_true", _ => true)
  }

  it should "be runnable existentially with a TaskHandler" in {
    import TaskInstances._

    val task = new Task[TestLogic, Boolean, Boolean](
      "logical_not", !_)

    var inputCtx = new TaskContext[TestLogic]()
    var outputCtx = new TaskContext[TestLogic]()
    inputCtx.bool = Option(false)
    task.unwrap(
      new TaskHandler[TestLogic] {
        def handle[A, B](task: Task[TestLogic, A, B]) = 
          outputCtx = task.run(inputCtx) // TODO use ValueSet
      })

    outputCtx.bool shouldBe a [Some[_]]
    outputCtx.bool.get shouldBe true
  }
}
