package example

import zio.Has

package object database {
  type Transaction = Has[Transaction.Service]
  type Connection = Has[Connection.Service]
}
