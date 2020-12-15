package scalajobs.model

import java.util.UUID
import java.time.LocalDateTime
import io.circe.{Encoder, Decoder}
import io.circe.syntax.EncoderOps
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

sealed trait Currency
object Currency {
  case object USD extends Currency
  case object RUB extends Currency
  case object EUR extends Currency

  implicit val encoder: Encoder[Currency] = Encoder.instance {
    case USD => "USD".asJson
    case RUB => "RUB".asJson
    case EUR => "EUR".asJson
  }

  implicit val decoder: Decoder[Currency] = deriveDecoder

  def toString(currency: Currency): String =
    currency match {
      case USD => "USD"
      case RUB => "RUB"
      case EUR => "EUR"
    }

  def fromString(str: String): Currency =
    str match {
      case "EUR" => Currency.EUR
      case "USD" => Currency.USD
      case "RUB" => Currency.RUB
    }
}

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

object Vacancy {
  implicit val encoder: Encoder[Vacancy] = deriveEncoder
}
final case class Vacancy(id: Option[UUID],
                         description: String,
                         organization: Organization,
                         salaryFrom: Int,
                         salaryTo: Int,
                         currency: Currency,
                         expiresAt: LocalDateTime,
                         contactEmail: String,
                         link: String,
                         officePresence: OfficePresence)

sealed trait VacancyFilter
object VacancyFilter {
  final case class SalaryFrom(amount: Int) extends VacancyFilter
  final case class SalaryTo(amount: Int) extends VacancyFilter
  final case class Actual(actual: Boolean) extends VacancyFilter
}
