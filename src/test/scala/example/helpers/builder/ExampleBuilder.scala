package example.helpers.builder

import doobie.implicits._
import example.database.{db, Transaction}
import zio.{RIO, ZIO}
import example.models.Example

object ExampleBuilder {
  var COUNT = 0
}

case class ExampleBuilder(
  name: Option[String] = None
) {
  def name(name: String): ExampleBuilder = copy(name = Some(name))

  def build: RIO[Transaction, Example] = ZIO.accessM { t =>
    ExampleBuilder.COUNT += 1

    val exampleName: String = name.getOrElse("example-" + ExampleBuilder.COUNT)

    t.get.transact {
      for {
        id <- db.createGenerated[Long](sql"""insert into example(name) values($exampleName)""")
      } yield Example(id, exampleName)
    }
  }
}
