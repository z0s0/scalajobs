package scalajobs.dao

import scalajobs.Migrations
import scalajobs.configuration.DbConnection
import scalajobs.model.dbParams.OrganizationDbParams
import scalajobs.support.PostgreSQLContainerLayer
import zio.{ZIO, ZLayer}
import zio.test.{DefaultRunnableSpec, Gen, checkM, suite, testM}
import zio.blocking.Blocking
import zio.random.Random
import zio.test.Assertion._
import zio.test.TestAspect._
import zio.test._
import zio.test.magnolia._
import zio.test.Gen.anyString

object OrganizationDaoSpec extends DefaultRunnableSpec {
  val env =
    ZLayer.requires[Blocking] >+>
      PostgreSQLContainerLayer.live >+>
      Migrations.live >+>
      Migrations.afterMigrations >+>
      DbConnection.transactorLive >>>
      (OrganizationDao.live)

  val genOrgDbParams: Gen[Random with Sized, OrganizationDbParams] =
    anyString.noShrink.map(OrganizationDbParams)

  def spec =
    suite("OrganizationDaoSpec")(
      suite("list")(testM("list") {
        checkM(Gen.vectorOfBounded(1, 10)(genOrgDbParams)) { orgParams =>
          for {
            createdOrgs <- ZIO.foreach(orgParams)(OrganizationDao.create)
            orgsNames = createdOrgs.map(_.get.name)
            res <- assertM(OrganizationDao.list.map(_.map(_.name)))(
              hasSameElements(orgsNames)
            )
          } yield res
        }
      }).@@(after(OrganizationDao.deleteAll)),
      suite("create")(testM("creates organizations") {
        checkM(Gen.vectorOf(genOrgDbParams)) { orgsParams =>
          val names = for {
            organizations <- ZIO.foreach(orgsParams)(OrganizationDao.create)
          } yield organizations.map(_.get.name)

          assertM(names)(hasSameElements(orgsParams.map(_.name)))
        }
      }).@@(after(OrganizationDao.deleteAll)),
      suite("get")(
        testM("existing org") {
          checkM(Gen.vectorOfBounded(1, 11)(genOrgDbParams)) { orgsParams =>
            for {
              someOrgs <- ZIO.foreach(orgsParams)(OrganizationDao.create)
              headOrg = someOrgs.head.get
              res <- assertM(OrganizationDao.get(headOrg.id.get))(
                isSome(equalTo(headOrg))
              )
            } yield res
          }
        },
        testM("unknown org") {
          checkM(Gen.vectorOfBounded(1, 11)(genOrgDbParams), Gen.anyUUID) {
            (orgsParamsList, anyUUID) =>
              for {
                _ <- ZIO.foreach_(orgsParamsList)(OrganizationDao.create)
                res <- assertM(OrganizationDao.get(anyUUID))(isNone)
              } yield res
          }
        }
      ).@@(after(OrganizationDao.deleteAll))
    ).provideSomeLayerShared[Environment](env.orDie)

}
