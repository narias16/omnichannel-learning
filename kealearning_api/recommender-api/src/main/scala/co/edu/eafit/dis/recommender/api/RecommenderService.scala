package co.edu.eafit.dis.recommender.api

import akka.Done
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceAcl, ServiceCall}
import play.api.libs.json.{Format, Json}
import co.edu.eafit.dis.context.api.{ContextRegistryString, ContextService}
import com.lightbend.lagom.scaladsl.api.transport.Method

// TODO read side para re entrenar
// TODO Servicios de recomendacion input -> output
// TODO Guardar un row en el dataset

object RecommenderService {
  val TOPIC_NAME = "recommender"
}

trait RecommenderService extends Service {
  def saveRating(): ServiceCall[Rating, Done]

  override def descriptor: Descriptor = {
    import Service._
    named("/recommend")
      .withCalls(
        restCall(method = Method.POST, pathPattern = "/recommend/rated", saveRating())
      )
      .withAutoAcl(autoAcl = true)
  }
}

case class Rating(user_id: String, content_id: String, rating: Int)

object Rating {
  implicit val format: Format[Rating] = Json.format
}

case class ContextRating(user_id: String, content_id: String, rating: Int, context: ContextRegistryString)

object ContextRating {
  implicit val format: Format[ContextRating] = Json.format
}