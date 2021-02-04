package example.repository


import example.models.{CreateExample, Example}
import zio._
import doobie.implicits._
import example.database.{db, Transaction}

object ExampleRepository {
  trait Service {
    def create(task: CreateExample): Task[Example]
    def list: Task[List[Example]]
  }

  val live: ZLayer[Transaction, Nothing, ExampleRepository] = ZLayer.fromService( t =>
    new Service {
      def create(example: CreateExample): Task[Example] = for {
        id <- t.transact {
          db.createGenerated[Int](sql"INSERT INTO example(name) values(${example.name})")
        }
      } yield Example(id, example)

      def list: Task[List[Example]] = t.transact {
        db.query[Example](sql"SELECT id, name FROM example")
      }
    }
  )
}

object examples {
  def list: RIO[ExampleRepository, List[Example]] = ZIO.accessM(_.get.list)
  def create(example: CreateExample): RIO[ExampleRepository, Example] = ZIO.accessM(_.get.create(example))
}
