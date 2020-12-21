package scalajobs.model.form

import java.util.UUID

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

final case class VacancyForm(id: Option[UUID],
                             description: Option[String],
                             organizationId: Option[UUID],
                             salaryFrom: Option[Int],
                             salaryTo: Option[Int])

object VacancyForm {
  implicit val jsonDecoder: Decoder[VacancyForm] = deriveDecoder
}
