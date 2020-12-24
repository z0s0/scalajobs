package scalajobs.model.dbParams

import java.time.LocalDateTime
import java.util.UUID

final case class VacancyDbParams(description: String,
                                 salaryFrom: Int,
                                 salaryTo: Int,
                                 organizationId: UUID,
                                 currency: String,
                                 officePresence: String,
                                 expiresAt: LocalDateTime,
                                 contactEmail: String,
                                 link: String)
