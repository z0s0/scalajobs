package scalajobs.model

import java.util.UUID
import java.time.LocalDateTime
import io.circe.generic.JsonCodec

@JsonCodec
final case class Vacancy(id: UUID,
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
