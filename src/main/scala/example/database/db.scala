package example.database

import doobie.implicits._
import doobie.util.Read
import doobie.util.fragment.Fragment

object db {
  def query[B: Read](queryString: Fragment): doobie.ConnectionIO[List[B]] =
    queryString
      .stripMargin
      .query[B]
      .to[List]

  def queryOne[B: Read](queryString: Fragment): doobie.ConnectionIO[B] =
    queryString
      .stripMargin
      .query[B]
      .unique

  def create(queryString: Fragment): doobie.ConnectionIO[Int] =
    queryString
      .stripMargin
      .update
      .run

  def alter(queryString: Fragment): doobie.ConnectionIO[Int] =
    queryString
      .stripMargin
      .update
      .run
  def createGenerated[B: Read](queryString: Fragment): doobie.ConnectionIO[B] =
    for {
      _ <- queryString.stripMargin.update.run
        id <- sql"select lastval()".query[B].unique
    } yield id
}
