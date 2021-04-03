package scalajobs.dao

import java.time.LocalDateTime
import java.util.UUID

import scalajobs.Migrations
import scalajobs.configuration.DbConnection
import scalajobs.model.DbError.{Conflict, Invalid}
import scalajobs.model.VacancyFilter
import scalajobs.model.dbParams.{OrganizationDbParams, VacancyDbParams}
import scalajobs.support.PostgreSQLContainerLayer
import zio.{ZIO, ZLayer}
import zio.blocking.Blocking
import zio.logging.slf4j.Slf4jLogger
import zio.test._
import zio.test.Assertion._

object VacancyDaoSpec extends DefaultRunnableSpec {
  val loggingLayer = Slf4jLogger.makeWithAnnotationsAsMdc(Nil)

  val env =
    ZLayer.requires[Blocking] >+>
      PostgreSQLContainerLayer.live >+>
      Migrations.live >+>
      Migrations.afterMigrations >+>
      (DbConnection.transactorLive ++ loggingLayer) >>>
      VacancyDao.live ++ OrganizationDao.live

  def vacancyDbParamsByOrg(orgId: UUID) =
    VacancyDbParams(
      description = "some desc",
      salaryFrom = 10000,
      salaryTo = 40000,
      organizationId = orgId,
      currency = "EUR",
      officePresence = "remote",
      expiresAt = LocalDateTime.of(2030, 3, 12, 12, 0, 0, 9),
      contactEmail = "email@mail.ru",
      link = "https://href.ru"
    )

  private def createOrganization =
    OrganizationDao.create(OrganizationDbParams("name", "desc"))

  def spec =
    suite("VacancyDaoSpec")(
      suite("list")(
        testM("empty if no vacancies present") {
          for {
            list <- VacancyDao.list(List[VacancyFilter]())
          } yield assert(list)(isEmpty)
        },
        testM("empty if vacancies unapproved") {
          for {
            org <- createOrganization
            _ <- VacancyDao.create(vacancyDbParamsByOrg(org.id))
            list <- VacancyDao.list(List[VacancyFilter]())
          } yield assert(list)(isEmpty)
        },
        testM("can filter vacancies by company") {
          for {
            org <- createOrganization
            vacancy <- VacancyDao.create(vacancyDbParamsByOrg(org.id))
            _ <- VacancyDao.approve(vacancy.id)
            list <- VacancyDao.list(
              List[VacancyFilter](VacancyFilter.Company(org.id))
            )
          } yield assert(list.length)(equalTo(1))
        },
      ),
      suite("create")(
        testM("creates unapproved vacancy") {
          for {
            org <- createOrganization
            vacancy <- VacancyDao.create(vacancyDbParamsByOrg(org.id))
            listVacancies <- VacancyDao.list(List[VacancyFilter]())
          } yield {
            assert(vacancy.organization.id)(equalTo(org.id)) &&
            assert(listVacancies)(isEmpty)
          }
        },
        testM("cannot create vacancy of wrong organization") {
          for {
            effect <- VacancyDao
              .create(vacancyDbParamsByOrg(UUID.randomUUID()))
              .run
          } yield
            assert(effect)(
              fails(equalTo(Invalid("organization does not exist")))
            )
        }
      ),
    ).provideCustomLayer(env.orDie)
}
