package scalajobs.model

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.deriveDecoder
import scala.util.control.NoStackTrace
import io.circe.syntax._

sealed trait Error extends NoStackTrace

sealed trait DbError extends Error
sealed trait ClientError extends Error

object DbError {
  final case class Conflict(reason: String) extends DbError
  final case class Invalid(reason: String) extends DbError
  final case object Disaster extends DbError
}

object ClientError {
  implicit val jsonEncoder: Encoder[ClientError] = Encoder.instance {
    case Invalid(reason) => Map("reason" -> reason).asJson
    case Disaster =>
      Map("reason" -> "Something went wrong. Please retry later").asJson
  }
  implicit val jsonDecoder: Decoder[ClientError] = deriveDecoder

  final case class Invalid(reason: String) extends ClientError
  final case object Disaster extends ClientError
}
