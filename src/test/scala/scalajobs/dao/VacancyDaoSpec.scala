package scalajobs.dao

import scalajobs.Migrations
import scalajobs.configuration.DbConnection
import scalajobs.model.VacancyFilter
import scalajobs.model.dbParams.VacancyDbParams
import scalajobs.support.PostgreSQLContainerLayer
import zio.{ZIO, ZLayer}
import zio.blocking.Blocking
import zio.logging.slf4j.Slf4jLogger
import zio.random.Random
import zio.test._
import zio.test.Assertion._
import zio.test.TestAspect.after
import zio.test.Gen._
import zio.test.magnolia.DeriveGen

object VacancyDaoSpec extends DefaultRunnableSpec {
  val loggingLayer = Slf4jLogger.makeWithAnnotationsAsMdc(Nil)

  val env =
    ZLayer.requires[Blocking] >+>
      PostgreSQLContainerLayer.live >+>
      Migrations.live >+>
      Migrations.afterMigrations >+>
      (DbConnection.transactorLive ++ loggingLayer) >>>
      VacancyDao.live

//  val genVacancyDbParams: Gen[Random with Sized, VacancyDbParams] =
//    VacancyDbParams(
//      description = anyString,
//      salaryFrom = anyInt.filter(_ > 0),
//      salaryTo = anyInt.filter(_ > 0),
//      currency = anyString,
//      officePresence = anyString,
//      link = anyString,
//      contactEmail = anyString,
//      organizationId = anyUUID,
//      expiresAt = anyLocalDateTime
//    )

  val genVacancyDbParams = DeriveGen[VacancyDbParams]

  def spec =
    suite("VacancyDaoSpec")(
      suite("list")(
        testM("shows list of all vacancies unless filters provided") {
          checkM(Gen.vectorOfBounded(1, 10)(genVacancyDbParams)) { listParams =>
            val emptyFilters = List[VacancyFilter]()
            for {
              _ <- ZIO.foreach_(listParams)(VacancyDao.create)
              res <- assertM(VacancyDao.list(emptyFilters).map(_.length))(
                equalTo(listParams.length)
              )
            } yield res
          }
        }
      ).@@(after(VacancyDao.deleteAll))
    ).provideSomeLayerShared[Environment](env.orDie)
}
