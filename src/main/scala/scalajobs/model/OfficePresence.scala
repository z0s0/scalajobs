package scalajobs.model

import io.circe.Encoder
import io.circe.syntax.EncoderOps

sealed trait OfficePresence
object OfficePresence {
  case object Remote extends OfficePresence
  case object Office extends OfficePresence
  case object Flexible extends OfficePresence

  implicit val encoder: Encoder[OfficePresence] = Encoder.instance {
    case Remote   => "remote".asJson
    case Office   => "office".asJson
    case Flexible => "flexible".asJson
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
