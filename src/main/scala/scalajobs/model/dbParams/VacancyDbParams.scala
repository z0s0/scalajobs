package scalajobs.model.dbParams

import java.util.UUID

final case class VacancyDbParams(description: String,
                                 salaryFrom: Int,
                                 salaryTo: Int,
                                 organizationId: UUID)
