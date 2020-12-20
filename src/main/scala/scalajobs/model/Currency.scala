package scalajobs.model

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.deriveDecoder
import io.circe.syntax.EncoderOps

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
