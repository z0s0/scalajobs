package scalajobs.model.form

import cats.data.Validated
import io.circe.generic.JsonCodec
import scalajobs.model.Form

@JsonCodec
final case class OrganizationForm(name: Option[String],
                                  description: Option[String],
                                  captcha: Option[String])
    extends Form {
  override def validate =
    Validated
      .cond(
        name.getOrElse("").length > 1,
        "",
        List("Organization name is not provided or too short")
      )
      .combine(
        Validated
          .cond(
            description.getOrElse("").length > 2,
            "",
            List("Description is not provided or too short")
          )
      ).combine(
        Validated.cond(
          captcha.getOrElse("").length > 1,
          "",
          List("Captcha must be provided")
        )
    )
}
