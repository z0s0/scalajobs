package scalajobs.model

import java.util.UUID
import java.time.LocalDateTime
import io.circe.Encoder
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

object Vacancy {
  implicit val encoder: Encoder[Vacancy] = deriveEncoder
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

final case class VacancyParams(id: Option[UUID],
                               description: String,
                               organizationId: UUID,
                               salaryFrom: Int,
                               salaryTo: Int)

sealed trait GetVacancyResponse
object GetVacancyResponse {
  case class Found(vacancy: Vacancy) extends GetVacancyResponse
  case object NotFound extends GetVacancyResponse
}

sealed trait CreateVacancyResponse

object CreateVacancyResponse {
  case class Created(vacancy: Vacancy) extends CreateVacancyResponse
  case object Invalid extends CreateVacancyResponse
}
