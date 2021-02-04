package example.database

import doobie._
import doobie.`enum`.TransactionIsolation
import doobie.implicits._
import zio._
import zio.interop.catz._

object Transaction {
  class Service(connection: Connection.Service) {
    def transact[R, A](f: ConnectionIO[A]): RIO[R, A] = transact(TransactionIsolation.TransactionSerializable)(f)

    def transact[R, A](isolationLevel: TransactionIsolation)
      (f: ConnectionIO[A]): RIO[R, A] = {
        val transaction = for {
          _ <- HC.setTransactionIsolation(isolationLevel)
          n <- f
        } yield n

        transaction.transact(connection.xa)
      }
  }

  def live: ZLayer[Connection, Nothing, Transaction] = ZLayer.fromService( connection => new Service(connection) )
}

object transact {
  def apply[A](f: ConnectionIO[A]): RIO[Transaction, A] = ZIO.accessM(_.get.transact(f))
  def apply[A](isolationLevel: TransactionIsolation)(f: ConnectionIO[A]): RIO[Transaction, A] = ZIO.accessM(_.get.transact(isolationLevel)(f))
}
