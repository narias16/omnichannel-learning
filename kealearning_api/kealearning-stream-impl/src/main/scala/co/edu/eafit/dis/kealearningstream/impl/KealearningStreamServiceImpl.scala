package co.edu.eafit.dis.kealearningstream.impl

import com.lightbend.lagom.scaladsl.api.ServiceCall
import co.edu.eafit.dis.kealearningstream.api.KealearningStreamService
import co.edu.eafit.dis.kealearning.api.KealearningService

import scala.concurrent.Future

/**
  * Implementation of the KealearningStreamService.
  */
class KealearningStreamServiceImpl(kealearningService: KealearningService) extends KealearningStreamService {
  def stream = ServiceCall { hellos =>
    Future.successful(hellos.mapAsync(8)(kealearningService.hello(_).invoke()))
  }
}
