package scalajobs.model

import java.util.UUID

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

object Organization {
  implicit val jsonEncoder: Encoder[Organization] = deriveEncoder
  implicit val jsonDecoder: Decoder[Organization] = deriveDecoder
}

final case class Organization(id: Option[UUID], name: String)
