package scalajobs

import org.http4s.server.Router
import scalajobs.configuration.ApiConfig
import zio.{Has, RIO, UIO, ZIO}
import cats.implicits._
import org.http4s.HttpRoutes
import org.http4s.syntax.kleisli._
import org.http4s.server.middleware.CORS
import org.http4s.server.blaze.BlazeServerBuilder
import org.slf4j.LoggerFactory
import scalajobs.api.{Docs, Routes}
import scalajobs.configuration.Configuration.AllConfigs
import scalajobs.service.Layer.Services
import zio.clock.Clock
import sttp.tapir.swagger.http4s.SwaggerHttp4s
import zio.interop.catz._
import zio.zmx.prometheus.PrometheusClient

object Main {
  type AppEnv = Clock with Services with AllConfigs with Has[PrometheusClient]

  private val log = LoggerFactory.getLogger("RuntimeReporter")
  private val corsConfig = CORS.DefaultCORSConfig

  def main(args: Array[String]): Unit = {
    val program = for {
      apiConf <- ZIO.access[Has[ApiConfig]](_.get)
      _ <- runHttp(Routes.routes, apiConf)
    } yield ()

    val runtime = zio.Runtime.default.withReportFailure { cause =>
      if (cause.died) log.error(cause.prettyPrint)
    }

    val layered = program.provideCustomLayer(DI.live)

    runtime.unsafeRun(layered.orDie)
  }

  private def runHttp[R <: Clock](routes: HttpRoutes[RIO[R, *]],
                                  apiConf: ApiConfig) = {
    type Task[T] = RIO[R, T]

    ZIO.runtime[R].flatMap { implicit rt =>
      val swagger = new SwaggerHttp4s(Docs.yaml).routes[Task]
      val finalRoutes = CORS(routes <+> swagger, corsConfig)

      BlazeServerBuilder[Task]
        .bindHttp(apiConf.port, "localhost")
        .withHttpApp(Router("/" -> finalRoutes).orNotFound)
        .serve
        .compile[Task, Task, cats.effect.ExitCode]
        .drain
    }
  }
}
