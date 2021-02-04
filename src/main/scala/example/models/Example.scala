package example.models

import io.circe.generic.auto._
import io.circe.syntax._

case class Example(id: Long, name: String) extends TaskData
object Example {
  def apply(id: Long, taskData: TaskData): Example = Example(id, taskData.name)
}

case class CreateExample(name: String) extends TaskData

trait TaskData {
  def name: String
}
