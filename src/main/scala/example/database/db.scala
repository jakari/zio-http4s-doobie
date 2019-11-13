package example.database

import doobie.util.fragment.Fragment
import doobie.implicits._
import doobie.util.Read
import zio.interop.catz._
import zio._
import doobie.util.compat.FactoryCompat._
import doobie.util.update.Update0

object db {
  def query[B: Read](queryString: Fragment): RIO[Connection, List[B]] =
    ZIO.accessM[Connection](c => queryString
        .stripMargin
        .query[B]
        .to[List]
        .transact(c.connection.xa).orDie
    )
  def queryOne[B: Read](queryString: Fragment): RIO[Connection, B] =
    ZIO.accessM[Connection](c => queryString
        .stripMargin
        .query[B]
        .unique
        .transact(c.connection.xa).orDie
    )
  def alter(queryString: Fragment): RIO[Connection, Int] =
    ZIO.accessM[Connection](c => queryString
        .stripMargin
        .update
        .run
        .transact(c.connection.xa).orDie
    )
  def alter(queryString: String): RIO[Connection, Int] =
    ZIO.accessM[Connection](c => Update0(queryString, None)
        .run
        .transact(c.connection.xa).orDie
    )

  def createGenerated[B: Read](queryString: Fragment): RIO[Connection, B] =
    ZIO.accessM[Connection](c => {
      val update = for {
        _ <- queryString.stripMargin.update.run
        id <- sql"select lastval()".query[B].unique
      } yield id

      update.transact(c.connection.xa).orDie
    })

  def create(queryString: Fragment): RIO[Connection, Int] =
    ZIO.accessM[Connection](c => queryString
      .stripMargin
      .update
      .run
      .transact(c.connection.xa)
      .orDie
    )
}
