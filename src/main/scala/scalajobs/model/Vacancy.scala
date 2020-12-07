package scalajobs.model

import java.util.UUID
import java.time.LocalDateTime

sealed trait Currency
object Currency {
  case object USD extends Currency
  case object RUB extends Currency
  case object EUR extends Currency
}

sealed trait OfficePresence
object OfficePresence {
  case object Remote extends OfficePresence
  case object Office extends OfficePresence
  case object Flexible extends OfficePresence
}

final case class Vacancy(id: Option[UUID],
                         description: String,
                         organizationId: UUID,
                         salaryFrom: Int,
                         salaryTo: Int,
                         currency: Currency,
                         expiresAt: LocalDateTime,
                         contactEmail: String,
                         link: String,
                         officePresence: OfficePresence)
