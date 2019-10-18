package co.edu.eafit.dis.context.impl

import akka.Done
import com.datastax.driver.core.{BoundStatement, PreparedStatement}
import com.lightbend.lagom.scaladsl.persistence.{AggregateEventTag, EventStreamElement, ReadSideProcessor}
import com.lightbend.lagom.scaladsl.persistence.cassandra.{CassandraReadSide, CassandraSession}

import scala.concurrent.{ExecutionContext, Future, Promise}

class ContextEntityProcessor(session: CassandraSession, readSide: CassandraReadSide)
                            (implicit ec: ExecutionContext)
                              extends ReadSideProcessor[ContextEvent] {

  override def buildHandler(): ReadSideProcessor.ReadSideHandler[ContextEvent] = {
    readSide
      .builder[ContextEvent]("contextoffset")
      .setGlobalPrepare(createTable)
      .setPrepare(_ => prepareWriteContext())
      .setEventHandler[ContextRegistrySaved](processContextRegistrySaved)
      .build()
  }

  override def aggregateTags: Set[AggregateEventTag[ContextEvent]] =
    Set(ContextEvent.Tag)

  private def createTable(): Future[Done] =
    session.executeCreateTable(
      "CREATE TABLE IF NOT EXISTS context(user_id TEXT, timestamp TEXT, ruido TEXT, luz TEXT, loc TEXT, conectividad TEXT, move TEXT, channel TEXT, PRIMARY KEY (user_id))"
    )

  private val writeContextPromise                     = Promise[PreparedStatement] // initialized in prepare
  private def writeContext: Future[PreparedStatement] = writeContextPromise.future

  private def prepareWriteContext(): Future[Done] = {
    val f = session.prepare(
      "INSERT INTO context (user_id, timestamp, ruido, luz, loc, conectividad, move, channel) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
    )

    writeContextPromise.completeWith(f)
    f.map(_ => Done)
  }

  private def processContextRegistrySaved(eventElement: EventStreamElement[ContextRegistrySaved]):
                                                                    Future[List[BoundStatement]] = {
    writeContext.map { ps =>
      val bindWriteContext = ps.bind()

      bindWriteContext.setString("user_id", eventElement.event.user_id)
      bindWriteContext.setString("timestamp", eventElement.event.ctx.timestamp)
      bindWriteContext.setString("ruido", eventElement.event.ctx.ruido)
      bindWriteContext.setString("luz", eventElement.event.ctx.luz)
      bindWriteContext.setString("loc", eventElement.event.ctx.loc)
      bindWriteContext.setString("conectividad", eventElement.event.ctx.conectividad)
      bindWriteContext.setString("move", eventElement.event.ctx.move)
      bindWriteContext.setString("channel", eventElement.event.ctx.channel)

      List(bindWriteContext)
    }
  }
}