package example.repository

import example.database.{Connection, db}
import example.models._
import zio.{RIO, ZIO}
import doobie.implicits._

trait ExampleRepository extends Connection {
  def exampleRepository: ExampleRepository.Service =  new ExampleRepository.Service {
    def create(task: CreateTask) =
      db.createGenerated[Int](sql"INSERT INTO task(name) values(${task.name})")
    def list = db.query[Task](sql"SELECT id, name FROM task")
  }
}

object ExampleRepository {
  trait Service {
    def create(task: CreateTask): ZIO[Connection, Throwable, Int]
    def list: RIO[Connection, List[Task]]
  }
}

object examples {
  def list: RIO[ExampleRepository, List[Task]] = ZIO.accessM(_.exampleRepository.list)
  def create(task: CreateTask): RIO[ExampleRepository, Int] = ZIO.accessM(_.exampleRepository.create(task))
}
