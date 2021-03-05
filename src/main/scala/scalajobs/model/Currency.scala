package scalajobs.model

import io.circe.{Decoder, Encoder}
import io.circe.syntax.EncoderOps

sealed trait Currency
object Currency {
  final case object USD extends Currency
  final case object RUB extends Currency
  final case object EUR extends Currency
  final case object THB extends Currency

  implicit val encoder: Encoder[Currency] = Encoder.instance(toString(_).asJson)

  implicit val decoder: Decoder[Currency] = Decoder[String].emap {
    case "USD" => Right(USD)
    case "RUB" => Right(RUB)
    case "EUR" => Right(EUR)
    case "THB" => Right(THB)
    case _     => Left("unknown currency")
  }

  def toString(currency: Currency): String =
    currency match {
      case USD => "USD"
      case RUB => "RUB"
      case EUR => "EUR"
      case THB => "THB"
    }

  def fromString(str: String): Currency =
    str match {
      case "EUR" => Currency.EUR
      case "USD" => Currency.USD
      case "RUB" => Currency.RUB
    }
}
