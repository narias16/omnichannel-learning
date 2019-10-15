package co.edu.eafit.dis.content.api

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}
import play.api.libs.json.{Format, Json}


object ContentService {
  val TOPIC_NAME = "content"
}

trait ContentService extends Service {

  def newContent(): ServiceCall[ContentData, Done]

  override def descriptor: Descriptor = {
    import Service._
    // @formatter:off
    named("content")
      .withCalls(
        restCall(method = Method.POST, "/content/new/", newContent())
      )
      .withAutoAcl(autoAcl = true)
    // @formatter:on
  }
}

case class Content(id: String,
                   format: String,
                   size: Int,
                   url: String,
                   duration: Int,
                   interactivity: String,
                   resourceType: String,
                   interactivityLevel: Int
                  )

object Content {
  implicit val format: Format[Content] = Json.format
}

case class ContentData(format: String,
                       size: Int,
                       url: String,
                       duration: Int,
                       interactivity: String,
                       resourceType: String,
                       interactivityLevel: Int
                      )

object ContentData {
  implicit val format: Format[ContentData] = Json.format
}