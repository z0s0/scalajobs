package scalajobs

import org.http4s.server.Router
import scalajobs.api.OrganizationsRoutes.OrganizationsRoutes
import scalajobs.api.VacanciesRoutes.VacanciesRoutes
import scalajobs.configuration.ApiConfig
import zio.{Has, RIO, ZIO}
import zio.console._
import zio.interop.catz._
import cats.implicits._
import org.http4s.HttpRoutes
import org.http4s.syntax.kleisli._
import org.http4s.server.blaze.BlazeServerBuilder
import org.slf4j.LoggerFactory
import scalajobs.api.{Docs, OrganizationsRoutes}
import scalajobs.configuration.Configuration.AllConfigs
import zio.clock.Clock
import zio.interop.catz.implicits.ioTimer
import sttp.tapir.swagger.http4s.SwaggerHttp4s

object Main {
  private val log = LoggerFactory.getLogger("RuntimeReporter")

  def main(args: Array[String]): Unit = {
    type AppEnv = Clock with OrganizationsRoutes with AllConfigs
    val program = for {
      apiConf <- ZIO.access[Has[ApiConfig]](_.get)
      oR <- ZIO.access[OrganizationsRoutes](_.get)
      _ <- ZIO.runtime[AppEnv].flatMap { implicit rts =>
        val swaggerRoutes: HttpRoutes[RIO[Clock, *]] =
          new SwaggerHttp4s(Docs.yaml).routes[RIO[Clock, *]]

        BlazeServerBuilder[RIO[Clock, *]]
          .bindHttp(apiConf.port, apiConf.endpoint)
          .withHttpApp(Router("/api/v1" -> (swaggerRoutes <+> oR)).orNotFound)
          .serve
          .compile
          .drain
      }
    } yield ()

    val runtime = zio.Runtime.default.withReportFailure { cause =>
      if (cause.died) log.error(cause.prettyPrint)
    }

    val layered = program
      .provideCustomLayer(DI.live)
      .tapError(err => putStrLn(s"Execution failed with: $err"))

    runtime.unsafeRun(layered.orDie)
  }
}
