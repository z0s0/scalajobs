package scalajobs.model

import io.circe.generic.JsonCodec

@JsonCodec
final case class Tag(id: Int, name: String)
