package example

import zio.Has

package object database {
  type DBAL = Has[DBAL.Service]
  type Connection = Has[Connection.Service]
}
