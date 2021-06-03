package scalajobs.model.form

import io.circe.generic.JsonCodec

@JsonCodec
final case class CaptchaVerificationResponse(
                                              success: Boolean,
                                              challenge_ts: Option[String],
                                              hostname: Option[String],
                                              `error-codes`: Option[List[String]]
                                            )