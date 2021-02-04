package example.helpers

import zio._
import doobie.implicits._
import doobie.util.fragment.Fragment
import example.database.{Transaction, db}
import example.database.{transact => dbTransact, transacted => dbTransacted}


trait DatabaseSuite {
  def resetDatabase: ZIO[Transaction, Throwable, Any] = ZIO.accessM { t =>
    t.get.transact(
      for {
        tables <- getTables
        _ <- truncateTables(tables)
      } yield ()
    )
  }

  private def getTables: doobie.ConnectionIO[List[String]] = db.query[String](sql"""SELECT table_name FROM information_schema.tables
                          | WHERE
                          |  table_schema NOT IN ('pg_catalog', 'information_schema')
                          |  AND table_name NOT IN('migration_versions')""")

  private def truncateTables(tables: List[String]) = {
    if (tables.isEmpty) Fragment.empty.update.run
    else db.alter(
      fr"TRUNCATE " ++ tables
        .map(n => Fragment.const(n))
        .reduce((a, b) => a ++ fr0"," ++ b)
    )
  }

  def intersperse[E](x: E, xs:Seq[E]): Seq[E] = (x, xs) match {
    case (_, Nil)     => Nil
    case (_, Seq(x))  => Seq(x)
    case (sep, y::ys) => y+:sep+:intersperse(sep, ys)
  }

  def transact: dbTransact.type = dbTransact
  val transacted: dbTransacted.type = dbTransacted
}
