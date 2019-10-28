package co.edu.eafit.dis.content.impl

import java.util.UUID

import akka.{Done, NotUsed}
import co.edu.eafit.dis.content.api.{Content, ContentData, ContentService}
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.{PersistentEntityRegistry, ReadSide}
import com.lightbend.lagom.scaladsl.persistence.cassandra.{CassandraReadSide, CassandraSession}
import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global
class ContentServiceImpl(persistentEntityRegistry: PersistentEntityRegistry,
                         session: CassandraSession,
                         cassandraReadSide: CassandraReadSide,
                         readSide: ReadSide) extends ContentService {

  override def newContent(): ServiceCall[ContentData, Done] = { contentData =>
    val id = UUID.randomUUID().toString

    val ref = persistentEntityRegistry.refFor[ContentEntity](id)

    val content: Content = contentData match {
      case ContentData(title: String, courseId: String, format: String, size: Int, url: String, duration: Int,
      interactivity: String, resourceType: String, interactivityLevel: Int) =>
        Content(id, title, courseId, format, size, url, duration, interactivity, resourceType, interactivityLevel)
    }

    ref.ask(CreateContent(content))

    Future.successful(Done)
  }

  /**
    * Read side
    * */
  override def courseObjects(courseId: String): ServiceCall[NotUsed, Seq[Content]] =
    ServiceCall { _ =>
      session
        .selectAll(s"SELECT * from content WHERE courseId = '$courseId' ALLOW FILTERING")
        .map { rows =>
          rows.map { row =>
            Content(row.getString("id"),
              row.getString("title"),
              row.getString("courseId"),
              row.getString("format"),
              row.getInt("size"),
              row.getString("url"),
              row.getInt("duration"),
              row.getString("interactivity"),
              row.getString("resourceType"),
              row.getInt("interactivityLevel"))
          }
        }
    }
}