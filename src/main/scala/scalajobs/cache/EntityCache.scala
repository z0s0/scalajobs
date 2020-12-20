package scalajobs.cache

trait EntityCache[F[_], Id, E] {
  def get(id: Id): F[Option[E]]
  def set(entity: E): F[Unit]
}
