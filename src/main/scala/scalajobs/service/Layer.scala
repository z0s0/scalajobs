package scalajobs.service

import scalajobs.service.CaptchaValidator.CaptchaValidator
import scalajobs.service.OrganizationService.OrganizationService
import scalajobs.service.VacancyService.VacancyService

object Layer {
  type Services = VacancyService with OrganizationService with CaptchaValidator

  val live = VacancyService.live ++ OrganizationService.live ++ CaptchaValidator.live
}
