package scalajobs.api

import sttp.tapir.ztapir._
import zio.zmx.prometheus.PrometheusClient

object MetricsRoutes {
  private val get = Docs.metrics.zServerLogic {_ => PrometheusClient.snapshot.map(_.value) }

  val routes = List(get)
}
