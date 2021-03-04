package scalajobs.cache

import scalajobs.cache.OrganizationCache.OrganizationCache
import scalajobs.cache.VacancyCache.VacancyCache
import zio.ULayer

object Layer {
  type Caches = VacancyCache with OrganizationCache

  val live: ULayer[Caches] = VacancyCache.live ++ OrganizationCache.live
}
