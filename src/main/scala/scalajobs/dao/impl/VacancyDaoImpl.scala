package scalajobs.dao.impl

import java.time.LocalDateTime
import java.util.UUID

import doobie.util.transactor.Transactor
import scalajobs.dao.VacancyDao
import scalajobs.model.{
  Currency,
  DbError,
  OfficePresence,
  Organization,
  Vacancy,
  VacancyFilter
}
import zio.{IO, Task}
import doobie._
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.implicits.javatime._
import zio.interop.catz._
import scalajobs.dao.impl.VacancyDaoImpl.VacancyRow
import scalajobs.model.DbError.Disaster
import scalajobs.model.dbParams.VacancyDbParams

object VacancyDaoImpl {
  final case class VacancyRow(id: UUID,
                              description: String,
                              salaryFrom: Int,
                              salaryTo: Int,
                              currency: String,
                              expiresAt: LocalDateTime,
                              contactEmail: String,
                              link: String,
                              officePresence: String,
                              organizationName: String,
                              organizationId: UUID,
                              organizationDesc: String) {
    def toVacancy: Vacancy =
      Vacancy(
        id = id,
        description = description,
        organization = Organization(
          id = Some(organizationId),
          organizationName,
          organizationDesc
        ),
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
    val baseSql = SQL.selectVacancySQL ++ fr"WHERE 1 = 1"

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

  override def get(id: UUID): Task[Option[Vacancy]] = SQL.get(id).transact(tr)

  override def create(params: VacancyDbParams): IO[DbError, Vacancy] =
    SQL
      .insert(params)
      .flatMap(SQL.get)
      .transact(tr)
      .foldM(_ => IO.fail(Disaster), vacOpt => IO.succeed(vacOpt.get))

  def deleteAll: Task[Int] = SQL.deleteAll.transact(tr)

  private object SQL {
    def get(id: UUID): ConnectionIO[Option[Vacancy]] =
      (selectVacancySQL ++ fr"WHERE v.id = $id")
        .query[VacancyRow]
        .map(_.toVacancy)
        .option

    def insert(params: VacancyDbParams): ConnectionIO[UUID] =
      sql"""
        INSERT INTO vacancies(
          description,
          salary_from,
          salary_to,
          organization_id,
          currency,
          office_presence,
          expires_at,
          contact_email,
          link,
          created_at,
          updated_at
        ) VALUES (
          ${params.description},
          ${params.salaryFrom},
          ${params.salaryTo},
          ${params.organizationId},
          ${params.currency},
          ${params.officePresence},
          NOW() + interval '14 days',
          ${params.contactEmail},
          ${params.link},
          NOW(),
          NOW()
        );  
      """.update
        .withGeneratedKeys[UUID]("id")
        .compile
        .lastOrError

    def selectVacancySQL: Fragment =
      sql"""
         SELECT v.id,
                v.description,
                v.salary_from,
                v.salary_to,
                v.currency,
                v.expires_at,
                v.contact_email,
                v.link,
                v.office_presence, 
                o.name,  
                o.id,
                o.description
         FROM vacancies v 
         JOIN organizations o ON v.organization_id = o.id
       """

    def deleteAll: ConnectionIO[Int] = sql"delete from vacancies".update.run
  }
}
