package scalajobs

import org.http4s.server.Router
import scalajobs.api.OrganizationsRoutes.OrganizationsRoutes
import scalajobs.api.VacanciesRoutes.VacanciesRoutes
import scalajobs.configuration.ApiConfig
import zio.{Has, Task, ZIO}
import zio.console._
import zio.interop.catz._
import org.http4s.implicits._
import cats.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.slf4j.LoggerFactory
import scalajobs.configuration.Configuration.AllConfigs
import zio.interop.catz.implicits.ioTimer

object Main {
  private val log = LoggerFactory.getLogger("RuntimeReporter")

  def main(args: Array[String]): Unit = {
    type AppEnv = VacanciesRoutes with OrganizationsRoutes with AllConfigs
    val program = for {
      apiConf <- ZIO.access[Has[ApiConfig]](_.get)
      vR <- ZIO.access[VacanciesRoutes](_.get.route)
      oR <- ZIO.access[OrganizationsRoutes](_.get.route)
      httpApp = Router("/api/v1" -> vR.combineK(oR)).orNotFound
      _ <- ZIO.runtime[AppEnv].flatMap { implicit rts =>
        BlazeServerBuilder[Task]
          .bindHttp(apiConf.port, apiConf.endpoint)
          .withHttpApp(httpApp)
          .serve
          .compile
          .drain
      }
    } yield ()

    val runtime = zio.Runtime.default.withReportFailure { cause =>
      if (cause.died) log.error(cause.prettyPrint)
    }

    val layered = program
      .provideLayer(DI.live)
      .tapError(err => putStrLn(s"Execution failed with: $err"))

    runtime.unsafeRun(layered.orDie)
  }
}
