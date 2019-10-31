package co.edu.eafit.dis.content.impl

import akka.Done
import com.datastax.driver.core.{BoundStatement, PreparedStatement}
import com.lightbend.lagom.scaladsl.persistence.{AggregateEventTag, EventStreamElement, ReadSideProcessor}
import com.lightbend.lagom.scaladsl.persistence.cassandra.{CassandraReadSide, CassandraSession}

import scala.concurrent.{ExecutionContext, Future, Promise}

class ContentEntityProcessor(session: CassandraSession, readSide: CassandraReadSide)
                            (implicit ec: ExecutionContext) extends ReadSideProcessor[ContentEvent] {

  override def buildHandler(): ReadSideProcessor.ReadSideHandler[ContentEvent] = readSide
    .builder[ContentEvent]("contentoffset")
    .setGlobalPrepare(createTable)
    .setPrepare(_ => prepareWriteContent())
    .setEventHandler[ContentCreated](processContentCreated)
    .build()

  override def aggregateTags: Set[AggregateEventTag[ContentEvent]] = Set(ContentEvent.Tag)

  private def createTable(): Future[Done] =
    session.executeCreateTable("CREATE TABLE IF NOT EXISTS content(id TEXT, title TEXT, courseId TEXT, format TEXT, size INT, url TEXT, duration INT, interactivity TEXT, resourceType TEXT, interactivityLevel INT, PRIMARY KEY(id))")

  private val writeContentPromise = Promise[PreparedStatement]
  private def writeContent: Future[PreparedStatement] = writeContentPromise.future

  private def prepareWriteContent(): Future[Done] = {
    val f = session.prepare(
      "INSERT INTO content (id, title, courseId, format, size, url, duration, interactivity, resourceType, interactivityLevel) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"

    )
    writeContentPromise.completeWith(f)
    f.map(_ => Done)
  }

  private def processContentCreated(eventStreamElement: EventStreamElement[ContentCreated]):
                                                                                      Future[List[BoundStatement]] = {
    writeContent.map { ps =>
      val bindWriteContent = ps.bind()
      val content = eventStreamElement.event.content
      bindWriteContent.setString("id", content.id)
      bindWriteContent.setString("title", content.title)
      bindWriteContent.setString("courseId", content.courseId)
      bindWriteContent.setString("format", content.format)
      bindWriteContent.setInt("size", content.size)
      bindWriteContent.setString("url", content.url)
      bindWriteContent.setInt("duration", content.duration)
      bindWriteContent.setString("interactivity", content.interactivity)
      bindWriteContent.setString("resourceType", content.resourceType)
      bindWriteContent.setInt("interactivityLevel", content.interactivityLevel)

      List(bindWriteContent)
    }
  }
}
