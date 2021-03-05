package scalajobs.model

import java.util.UUID
import java.time.LocalDateTime

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveEncoder, deriveDecoder}

object Vacancy {
  implicit val encoder: Encoder[Vacancy] = deriveEncoder
  implicit lazy val decoder: Decoder[Vacancy] = deriveDecoder
}
final case class Vacancy(id: Option[UUID],
                         description: String,
                         organization: Organization,
                         salaryFrom: Int,
                         salaryTo: Int,
                         currency: Currency,
                         expiresAt: LocalDateTime,
                         contactEmail: String,
                         link: String,
                         officePresence: OfficePresence)

sealed trait VacancyFilter
object VacancyFilter {
  final case class SalaryFrom(amount: Int) extends VacancyFilter
  final case class SalaryTo(amount: Int) extends VacancyFilter
  final case class Actual(actual: Boolean) extends VacancyFilter
}

sealed trait GetVacancyResponse
object GetVacancyResponse {
  final case class Found(vacancy: Vacancy) extends GetVacancyResponse
  final case object NotFound extends GetVacancyResponse
}

sealed trait CreateVacancyResponse

object CreateVacancyResponse {
  final case class Created(vacancy: Vacancy) extends CreateVacancyResponse
  final case object Invalid extends CreateVacancyResponse
}
