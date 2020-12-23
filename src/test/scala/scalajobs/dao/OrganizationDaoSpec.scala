package scalajobs.dao

import org.testcontainers.containers.PostgreSQLContainer
import scalajobs.Migrations
import scalajobs.configuration.{DbConfig, DbConnection}
import scalajobs.model.Organization
import scalajobs.model.dbParams.OrganizationDbParams
import zio.{Has, UIO, URLayer, ZIO, ZLayer}
import zio.test.{DefaultRunnableSpec, Gen, checkM, suite, testM}
import zio.blocking.{Blocking, effectBlocking}
import zio.logging.slf4j.Slf4jLogger
import zio.random.Random
import zio.test.Assertion._
import zio.test.TestAspect._
import zio.test._
import zio.test.magnolia._
import zio.test.Gen.anyString

object OrganizationDaoSpec extends DefaultRunnableSpec {
  val container = ZLayer.fromAcquireRelease(effectBlocking {
    val cont = new PostgreSQLContainer("postgres:12")
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
      config >+>
      Migrations.live >+>
      Migrations.afterMigrations >+>
      DbConnection.transactorLive >>>
      (OrganizationDao.live)

  val genOrganization = DeriveGen[Organization]
  val genOrgDbParams: Gen[Random with Sized, OrganizationDbParams] =
    anyString.noShrink.map(OrganizationDbParams)

  def spec =
    suite("OrganizationDaoSpec")(
      testM("create ") {
        checkM(Gen.vectorOf(genOrgDbParams)) { orgsParams =>
          val names = for {
            organizations <- ZIO.foreach(orgsParams)(OrganizationDao.create)
          } yield organizations.map(_.get.name)

          assertM(names)(hasSameElements(orgsParams.map(_.name)))
        }
      },
      suite("get")(testM("existing org") {
        checkM(Gen.vectorOfBounded(1, 11)(genOrgDbParams)) { orgsParams =>
          for {
            someOrgs <- ZIO.foreach(orgsParams)(OrganizationDao.create)
            headOrg = someOrgs.head.get
            res <- assertM(OrganizationDao.get(headOrg.id.get))(
              isSome(equalTo(headOrg))
            )
          } yield res
        }
      })
    ).@@(after(OrganizationDao.deleteAll))
      .provideSomeLayerShared[Environment](env.orDie)

}
