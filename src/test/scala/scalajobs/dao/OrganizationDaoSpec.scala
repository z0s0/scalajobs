import scalajobs.Migrations
import scalajobs.configuration.DbConnection
import scalajobs.dao.OrganizationDao
import scalajobs.model.DbError.Conflict
import scalajobs.model.dbParams.OrganizationDbParams
import scalajobs.support.PostgreSQLContainerLayer
import zio.blocking.Blocking
import zio.{ZIO, ZLayer}
import zio.test.DefaultRunnableSpec
import zio.test._
import zio.test.Assertion._

object OrganizationDaoSpec extends DefaultRunnableSpec {

  val env =
    ZLayer.requires[Blocking] >+>
      PostgreSQLContainerLayer.live >+>
      Migrations.live >+>
      Migrations.afterMigrations >+>
      DbConnection.transactorLive >>>
      OrganizationDao.live

  override def spec = {
    suite("OrganizationDao")(
      suite("list")(testM("returns list of organizations") {
        val params = List(
          OrganizationDbParams("name1", "desc1"),
          OrganizationDbParams("name2", "desc2")
        )

        for {
          _ <- ZIO.foreach_(params)(OrganizationDao.create)
          res <- assertM(OrganizationDao.list.map(_.length))(
            equalTo(params.length)
          )
        } yield res
      }),
      suite("create")(testM("creates new organization if name is free") {
        val params = OrganizationDbParams("freeName", "desc")

        for {
          id <- OrganizationDao.create(params).map(_.id)
          organization <- OrganizationDao.get(id).map(_.get)
        } yield {
          assert(organization.name)(equalTo(params.name)) &&
          assert(organization.description)(equalTo(params.description))
        }
      }, testM("returns Conflict if name is occupied") {
        val params1 = OrganizationDbParams("name", "desc")
        val paramsWithTakenName =
          OrganizationDbParams(params1.name, "another desc")
        val errorMsg = s"Organization with name ${params1.name} already exists"

        for {
          _ <- OrganizationDao.create(params1)
          eff <- OrganizationDao.create(paramsWithTakenName).run
        } yield assert(eff)(fails(equalTo(Conflict(errorMsg))))
      })
    ).provideCustomLayer(env.orDie)
  }
}
