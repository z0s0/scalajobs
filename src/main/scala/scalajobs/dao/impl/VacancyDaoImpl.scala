package scalajobs.dao.impl

import java.time.LocalDateTime
import java.util.UUID

import doobie.util.transactor.Transactor
import scalajobs.dao.VacancyDao
import scalajobs.model.{Currency, OfficePresence, Vacancy, VacancyFilter}
import zio.Task
import doobie._
import doobie.implicits._
import doobie.implicits.javatime._
import zio.interop.catz._
import doobie.postgres._
import doobie.postgres.implicits._
import scalajobs.dao.impl.VacancyDaoImpl.VacancyRow

object VacancyDaoImpl {
  final case class VacancyRow(id: UUID,
                              description: String,
//                              organizationId: UUID,
                              salaryFrom: Int,
                              salaryTo: Int,
                              currency: String,
                              expiresAt: LocalDateTime,
                              contactEmail: String,
                              link: String,
                              officePresence: String) {
    def toVacancy: Vacancy =
      Vacancy(
        id = Some(id),
        description = description,
        organizationId = UUID.randomUUID(),
        salaryFrom = salaryFrom,
        salaryTo = salaryTo,
        currency = Currency.fromString(currency),
        expiresAt = expiresAt,
        contactEmail = contactEmail,
        link = link,
        officePresence = OfficePresence.fromString(officePresence)
      )
  }
}
final class VacancyDaoImpl(tr: Transactor[Task]) extends VacancyDao.Service {
  override def list(filters: List[VacancyFilter]): Task[Vector[Vacancy]] = {
    val baseSql = sql"""
         SELECT v.id, v.description, v.salary_from, v.salary_to, v.currency, v.expires_at, v.contact_email, v.link, v.office_presence
         FROM vacancies v 
         WHERE 1 = 1
       """

    val sql = filters.foldLeft(baseSql) {
      case (acc, VacancyFilter.SalaryTo(amount)) =>
        acc ++ fr"AND v.salary_to <= $amount"
      case (acc, VacancyFilter.SalaryFrom(amount)) =>
        acc ++ fr"AND v.salary_from >= $amount"
      case (acc, VacancyFilter.Actual(flag)) =>
        if (flag) acc ++ fr"AND v.expires_at > NOW()" else acc
    }

    sql
      .query[VacancyRow]
      .map(_.toVacancy)
      .to[Vector]
      .transact(tr)
  }

  override def get(id: UUID): Task[Option[Vacancy]] = {
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

}
