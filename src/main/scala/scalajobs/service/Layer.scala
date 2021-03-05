package scalajobs.service

import scalajobs.cache.Layer.Caches
import scalajobs.dao.Layer.Daos
import scalajobs.service.OrganizationService.OrganizationService
import scalajobs.service.VacancyService.VacancyService
import zio.ZLayer

object Layer {
  type Services = OrganizationService with VacancyService

  val live
    : ZLayer[Caches with Daos, Nothing, Services] = OrganizationService.live ++ VacancyService.live
}
