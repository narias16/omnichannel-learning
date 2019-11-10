package co.edu.eafit.dis.recommender.api

import akka.{Done, NotUsed}
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

  def allRating(): ServiceCall[NotUsed, Seq[FlatRating]]

  override def descriptor: Descriptor = {
    import Service._
    named("/recommend")
      .withCalls(
        restCall(method = Method.POST, pathPattern = "/recommend/rated", saveRating()),
        restCall(method = Method.GET, pathPattern = "/recommend/get", allRating())
      )
      .withAutoAcl(autoAcl = true)
      .withAcls(ServiceAcl.forMethodAndPathRegex(Method.OPTIONS, "/recommend/*"))
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

case class FlatRating(user_id: String, content_id: String, rating: Int, timestamp: String,
                      ruido: String, luz: String, loc: String,
                      conectividad: String, move: String, channel: String)

object FlatRating {
  implicit val format: Format[FlatRating] = Json.format // TODO change the formatting from JSON to CSV
}