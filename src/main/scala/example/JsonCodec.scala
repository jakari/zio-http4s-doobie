package example

import java.sql.Timestamp

import cats.effect.Sync
import io.circe.{Decoder, Encoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.{EntityDecoder, EntityEncoder}

object JsonCodec {
  implicit def jsonDecoder[F[_]: Sync, A: Decoder]: EntityDecoder[F, A] = jsonOf[F, A]
  implicit def jsonEncoder[F[_], A: Encoder]: EntityEncoder[F, A] = jsonEncoderOf[F, A]


  implicit def encodeInstant: Encoder[Timestamp] = Encoder.encodeString.contramap[Timestamp](_.toInstant.toString)
  implicit def decodeInstant: Decoder[Timestamp] = Decoder.decodeString.emap { str =>
    Right(new Timestamp(0))
  }
}
