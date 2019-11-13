package example.models

case class Task(id: String, name: String) extends TaskData
case class CreateTask(name: String) extends TaskData

trait TaskData {
  def name: String
}
