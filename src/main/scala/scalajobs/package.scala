import org.http4s.HttpRoutes
import zio.RIO
import zio.clock.Clock

package object scalajobs {
  type Routes = HttpRoutes[RIO[Clock, *]]
}
