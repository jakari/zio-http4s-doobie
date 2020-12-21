package example.repository


import example.models.{CreateTask, TaskModel}
import zio._
import doobie.implicits._
import example.database.DBAL

object ExampleRepository {
  trait Service {
    def create(task: CreateTask): Task[Int]
    def list: Task[List[TaskModel]]
  }

  val live: ZLayer[DBAL, Nothing, ExampleRepository] = ZLayer.fromFunction( db =>
    new Service {
      def create(task: CreateTask): Task[Int] = db.get.createGenerated[Int](sql"INSERT INTO example(name) values(${task.name})")
      def list: Task[List[TaskModel]] = db.get.query[TaskModel](sql"SELECT id, name FROM task")
    }
  )
}

object examples {
  def list: RIO[ExampleRepository, List[TaskModel]] = ZIO.accessM(_.get.list)
  def create(task: CreateTask): RIO[ExampleRepository, Int] = ZIO.accessM(_.get.create(task))
}
