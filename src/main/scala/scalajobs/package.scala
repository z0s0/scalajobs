import org.http4s.HttpRoutes
import sttp.client.SttpBackend
import sttp.client.asynchttpclient.WebSocketHandler
import zio.{RIO, Task}
import zio.clock.Clock

package object scalajobs {
  type Routes = HttpRoutes[RIO[Clock, *]]
  type SttpClientService = SttpBackend[Task, Nothing, WebSocketHandler]
}
