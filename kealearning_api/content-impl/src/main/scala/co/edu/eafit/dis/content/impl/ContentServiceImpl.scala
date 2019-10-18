package co.edu.eafit.dis.content.impl

import java.util.UUID

import akka.Done
import co.edu.eafit.dis.content.api.{Content, ContentData, ContentService}
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry

import scala.concurrent.Future

class ContentServiceImpl(persistentEntityRegistry: PersistentEntityRegistry) extends ContentService {
  override def newContent(): ServiceCall[ContentData, Done] = { contentData =>
    val id = UUID.randomUUID().toString

    val ref = persistentEntityRegistry.refFor[ContentEntity](id)

    val content: Content = contentData match {
      case ContentData(format: String, size: Int, url: String, duration: Int, interactivity: String,
      resourceType: String, interactivityLevel: Int) =>
        Content(id, format, size, url, duration, interactivity, resourceType, interactivityLevel)
    }

    ref.ask(CreateContent(content))

    Future.successful(Done)
  }
}