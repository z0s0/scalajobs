package scalajobs.model

import java.util.UUID

import io.circe.generic.JsonCodec

@JsonCodec final case class Organization(id: Option[UUID],
                                         name: String,
                                         description: String)
