import scalajobs.Migrations
import scalajobs.configuration.DbConnection
import scalajobs.dao.OrganizationDao
import scalajobs.model.dbParams.OrganizationDbParams
import scalajobs.support.PostgreSQLContainerLayer
import zio.blocking.Blocking
import zio.{UIO, ZIO, ZLayer}
import zio.test.magnolia.DeriveGen
import zio.test.DefaultRunnableSpec

import zio.test._
import zio.test.Assertion._
import zio.test.TestAspect.after

object OrganizationDaoSpec extends DefaultRunnableSpec {

  val env =
    ZLayer.requires[Blocking] >+>
      PostgreSQLContainerLayer.live >+>
      Migrations.live >+>
      Migrations.afterMigrations >+>
      DbConnection.transactorLive >>>
      OrganizationDao.live

  val organizationParams = DeriveGen[OrganizationDbParams]

  override def spec = {
    suite("OrganizationDao")(
      suite("list")(testM("returns list of organizations") {
        checkM(Gen.vectorOfBounded(1, 10)(organizationParams)) { listParams =>
          for {
            _ <- ZIO.foreach_(listParams)(OrganizationDao.create)
            res <- assertM(OrganizationDao.list.map(_.length))(
              equalTo(listParams.length)
            )
          } yield res
        }
      }).@@(after(OrganizationDao.deleteAll)),
      suite("create")(testM("creates new organization if name is free") {
        assertM(UIO(12))(equalTo(12))
      }, testM("returns Conflict if name is occupied") {
        assertM(UIO(12))(equalTo(12))
      })
    ).provideCustomLayer(env.orDie)
  }
}
