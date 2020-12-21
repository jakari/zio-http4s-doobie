package example.database

import doobie.util.fragment.Fragment
import doobie.implicits._
import doobie.syntax.ConnectionIOOps
import doobie.util.Read
import zio.interop.catz._
import zio._
import doobie.util.compat.FactoryCompat._
import doobie.util.update.Update0

object DBAL {
  class Service(c: Connection.Service) {
    def query[B: Read](queryString: Fragment): Task[List[B]] = transact {
      queryString
        .stripMargin
        .query[B]
        .to[List]
    }

    def queryOne[B: Read](queryString: Fragment): Task[B] = transact {
      queryString
        .stripMargin
        .query[B]
        .unique
    }

    def create(queryString: Fragment): Task[Int] = transact {
      queryString
        .stripMargin
        .update
        .run
    }

    def alter(queryString: Fragment): Task[Int] = transact {
      queryString
        .stripMargin
        .update
        .run
    }

    def alter(queryString: String): Task[Int] = transact {
      Update0(queryString, None)
        .run
    }

    def createGenerated[B: Read](queryString: Fragment): Task[B] =
      transact {
        for {
          _ <- queryString.stripMargin.update.run
            id <- sql"select lastval()".query[B].unique
        } yield id
      }

    private def transact[R, A](f: => ConnectionIOOps[A]): URIO[R, A] =
      f.transact(c.xa).orDie
  }

  val live: URLayer[Connection, DBAL] = ZLayer.fromService(new DBAL.Service(_))
}
