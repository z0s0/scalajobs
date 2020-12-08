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
import zio.blocking.Blocking
import zio.clock.Clock
import zio.interop.catz.implicits.ioTimer
import zio.logging.slf4j.Slf4jLogger

object Main {
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
      putStrLn(cause.toString)
    }

    val logging = Slf4jLogger.makeWithAnnotationsAsMdc(Nil)

    val layered = program
      .provideLayer(DI.live)
      .provideSomeLayer[Blocking with Clock](logging)
      .tapError(err => putStrLn(s"Execution failed with: $err"))

    runtime.unsafeRun(layered.orDie)
  }
}
