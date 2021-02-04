package example.database

import doobie.util.Read
import doobie.util.fragment.Fragment
import zio.RIO

object transacted {
  def query[B: Read](queryString: Fragment): RIO[Transaction, List[B]] = transact(db.query(queryString))
  def queryOne[B: Read](queryString: Fragment): RIO[Transaction, B] =  transact(db.queryOne(queryString))
  def create(queryString: Fragment): RIO[Transaction, Int] = transact(db.create(queryString))
  def alter(queryString: Fragment): RIO[Transaction, Int] = transact(db.alter(queryString))
  def createGenerated[B: Read](queryString: Fragment): RIO[Transaction, B] = transact(db.createGenerated(queryString))
}
