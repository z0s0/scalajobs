package scalajobs

import org.http4s.server.Router
import scalajobs.api.OrganizationsRoutes.OrganizationsRoutes
import scalajobs.api.VacanciesRoutes.VacanciesRoutes
import scalajobs.configuration.ApiConfig
import zio.{Has, Task, UIO, ZIO}
import zio.console._
import zio.interop.catz._
import org.http4s.implicits._
import cats.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import scalajobs.configuration.Configuration.AllConfigs
import zio.clock.Clock
import zio.interop.catz.implicits.ioTimer

object Main {
  def main(args: Array[String]): Unit = {
    type AppEnv = VacanciesRoutes with OrganizationsRoutes with AllConfigs
    val program = for {
      apiConf <- ZIO.access[Has[ApiConfig]](_.get)
      vR <- ZIO.access[VacanciesRoutes](_.get.route)
      oR <- ZIO.access[OrganizationsRoutes](_.get.route)
      httpApp = Router("/api/v1" -> vR.combineK(oR)).orNotFound
      _ <- UIO(println(httpApp))

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
      putStrLn(cause.toString)
    }

    val layered = program
      .provideLayer(DI.live)

    runtime.unsafeRun(layered.orDie)
  }
}
