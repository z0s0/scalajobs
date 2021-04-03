package scalajobs.model

import java.util.UUID

import io.circe.generic.JsonCodec

@JsonCodec final case class Organization(id: UUID,
                                         name: String,
                                         description: String)
