package co.edu.eafit.dis.content.api

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceAcl, ServiceCall}
import play.api.libs.json.{Format, Json}


object ContentService {
  val TOPIC_NAME = "content"
}

trait ContentService extends Service {
  /**
    * curl http://localhost:9000/api/context/content/new
    */
  def newContent(): ServiceCall[ContentData, Done]

  /**
    * curl http://localhost:9000/api/content/content/:course_id
    */
  def courseObjects(courseId: String): ServiceCall[NotUsed, Seq[Content]]

  override def descriptor: Descriptor = {
    import Service._
    // @formatter:off
    named("content")
      .withCalls(
        restCall(method = Method.POST, "/content/new", newContent()),
        restCall(method = Method.GET, "/content/:course_id", courseObjects _)
      )
      .withAutoAcl(autoAcl = true)
      .withAcls(ServiceAcl.forMethodAndPathRegex(Method.OPTIONS, "/content/[^/]*"))
    // @formatter:on
  }
}

case class Content(id: String,
                   title: String,
                   courseId: String,
                   format: String,
                   size: Int,
                   url: String,
                   duration: Int,
                   interactivity: String,
                   resourceType: String,
                   interactivityLevel: Int)

object Content {
  implicit val format: Format[Content] = Json.format
}

case class ContentData(title: String,
                       courseId: String,
                       format: String,
                       size: Int,
                       url: String,
                       duration: Int,
                       interactivity: String,
                       resourceType: String,
                       interactivityLevel: Int)

object ContentData {
  implicit val format: Format[ContentData] = Json.format
}