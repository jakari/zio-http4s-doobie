package example.models

import io.circe.generic.auto._, io.circe.syntax._

case class TaskModel(id: String, name: String) extends TaskData
case class CreateTask(name: String) extends TaskData

trait TaskData {
  def name: String
}
