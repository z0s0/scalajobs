package scalajobs.model

import io.circe.{Decoder, Encoder}
import io.circe.syntax.EncoderOps

sealed trait OfficePresence
object OfficePresence {
  final case object Remote extends OfficePresence
  final case object Office extends OfficePresence
  final case object Flexible extends OfficePresence

  implicit val encoder: Encoder[OfficePresence] = Encoder.instance {
    case Remote   => "remote".asJson
    case Office   => "office".asJson
    case Flexible => "flexible".asJson
  }

  implicit val decoder: Decoder[OfficePresence] = Decoder[String].emap {
    case "remote"   => Right(OfficePresence.Remote)
    case "office"   => Right(OfficePresence.Office)
    case "flexible" => Right(OfficePresence.Flexible)
    case _          => Left("unknown office presence")
  }

  def toString(officePresence: OfficePresence): String =
    officePresence match {
      case Remote   => "remote"
      case Office   => "office"
      case Flexible => "flexible"
    }

  def fromString(str: String): OfficePresence =
    str match {
      case "remote"   => OfficePresence.Remote
      case "office"   => OfficePresence.Office
      case "flexible" => OfficePresence.Flexible
    }
}
