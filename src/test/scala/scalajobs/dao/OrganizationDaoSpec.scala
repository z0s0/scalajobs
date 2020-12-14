package scalajobs.dao

import org.testcontainers.containers.PostgreSQLContainer
import scalajobs.configuration.DbConfig
import scalajobs.model.Organization
import zio.{Has, URLayer, ZLayer}
import zio.test.{DefaultRunnableSpec, Gen, checkM, suite, testM}
import zio.blocking.effectBlocking
import zio.test.magnolia.DeriveGen

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

  val genOrganization = DeriveGen[Organization]

  def spec =
    suite("OrganizationDaoSpec")(testM("list returns list of organizations") {
      ???
    })
}
