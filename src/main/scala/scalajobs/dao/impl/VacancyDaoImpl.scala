package scalajobs.dao.impl

import java.time.LocalDateTime
import java.util.UUID

import doobie.util.transactor.Transactor
import scalajobs.dao.VacancyDao
import scalajobs.model.{Currency, OfficePresence, Vacancy}
import zio.Task
import doobie._
import doobie.implicits._
import zio.interop.catz._
import doobie.postgres._
import doobie.postgres.implicits._
import scalajobs.dao.impl.VacancyDaoImpl.VacancyRow

object VacancyDaoImpl {
  final case class VacancyRow(id: UUID,
                              description: String,
//                              organizationId: UUID,
//                              salaryFrom: Int,
//                              salaryTo: Int,
//                              currency: Int,
//                              expiresAt: String,
//                              contactEmail: String,
//                              link: String,
//                              officePresence: String
  ) {
    def toVacancy: Vacancy =
      Vacancy(
        id = Some(id),
        description = description,
        organizationId = UUID.randomUUID(),
        salaryFrom = 200,
        salaryTo = 900,
        currency = Currency.EUR,
        expiresAt = LocalDateTime.now(),
        contactEmail = "",
        link = "",
        officePresence = OfficePresence.Flexible
      )
  }
}
final class VacancyDaoImpl(tr: Transactor[Task]) extends VacancyDao.Service {
  override def list: Task[Vector[Vacancy]] =
    sql"""
         select id, description
         from vacancies
       """
      .query[VacancyRow]
      .map(_.toVacancy)
      .to[Vector]
      .transact(tr)

  override def get(id: UUID): Task[Option[Vacancy]] =
    sql"""
       select id, description, organization_id, salary_from, salary_to, currency, expires_at, contact_email, link, office_presence
       from vacancies
       where id = $id
       """
      .query[VacancyRow]
      .option
      .map(_.map(_.toVacancy))
      .transact(tr)

}
