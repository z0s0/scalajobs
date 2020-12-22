package scalajobs.dao

import org.testcontainers.containers.PostgreSQLContainer
import scalajobs.configuration.{DbConfig, DbConnection}
import scalajobs.model.Organization
import zio.{Has, UIO, URLayer, ZIO, ZLayer}
import zio.test.{DefaultRunnableSpec, Gen, checkM, suite, testM}
import zio.blocking.{Blocking, effectBlocking}
import zio.logging.slf4j.Slf4jLogger
import zio.test.Assertion._
import zio.test.TestAspect._
import zio.test.environment.{TestClock, TestEnvironment}
import zio.test._
import zio.test.magnolia._

object OrganizationDaoSpec extends DefaultRunnableSpec {
  val container = ZLayer.fromAcquireRelease(effectBlocking {
    val cont = new PostgreSQLContainer("12")
    cont.start()
    cont
  }.orDie)(cont => effectBlocking(cont.stop()).orDie)

  val config: URLayer[Has[PostgreSQLContainer[Nothing]], Has[DbConfig]] =
    ZLayer.fromService { cont =>
      DbConfig(
        url = cont.getJdbcUrl,
        user = cont.getUsername,
        password = cont.getPassword
      )
    }

  val loggingLayer = Slf4jLogger.makeWithAnnotationsAsMdc(Nil)

  val env =
    ZLayer.requires[Blocking] >+>
      container >+>
      config >>>
      DbConnection.transactorLive >>>
      (OrganizationDao.live)

  val genOrganization = DeriveGen[Organization]

  def spec =
    suite("OrganizationDaoSpec")(testM("list returns list of organizations") {
      checkM(Gen.vectorOf(genOrganization)) { users =>
        ???
      }
    })
}
