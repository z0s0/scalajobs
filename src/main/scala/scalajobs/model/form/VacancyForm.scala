package scalajobs.model.form

import scalajobs.model.{Form, Helpers}
import io.circe.generic.JsonCodec
import cats.data.Validated
import cats.syntax.semigroup._

@JsonCodec
final case class VacancyForm(description: Option[String],
                             title: Option[String],
                             organizationId: Option[String],
                             salaryFrom: Option[Int],
                             salaryTo: Option[Int],
                             currency: Option[String],
                             officePresence: Option[String],
                             expiresAt: Option[String],
                             contactEmail: Option[String],
                             link: Option[String])
    extends Form {
  override def validate = {
    def emptyFieldError(fieldName: String) =
      List(s"${fieldName} is not provided or empty")

    Validated
      .cond(
        description.getOrElse("").length > 10,
        "",
        emptyFieldError("description")
      ) |+| Validated
      .cond(
        positive(salaryFrom),
        "",
        List("salary must be present and positive")
      ) |+| Validated
      .cond(positive(salaryTo), "", List("salary must be present and positive")) |+| Validated
      .cond(
        salaryTo.getOrElse(0) >= salaryFrom.getOrElse(0),
        "",
        List("Max salary must be greater than min salary")
      ) |+| Validated
      .cond(
        Helpers.isValidUUID(organizationId.getOrElse("")),
        "",
        List("Invalid OrganizationID")
      ) |+| Validated.cond(
      Helpers.isValidDateTime(expiresAt.getOrElse("")),
      "",
      List("expiresAt must be present and conform ISO8601")
    )
  }
}
