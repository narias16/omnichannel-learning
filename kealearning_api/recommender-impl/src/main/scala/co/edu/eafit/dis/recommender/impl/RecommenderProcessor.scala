package co.edu.eafit.dis.recommender.impl


import com.datastax.driver.core.{BoundStatement, PreparedStatement}
import akka.Done
import com.lightbend.lagom.scaladsl.persistence.cassandra.{CassandraReadSide, CassandraSession}
import com.lightbend.lagom.scaladsl.persistence.{AggregateEventTag, EventStreamElement, ReadSideProcessor}

import scala.concurrent.{ExecutionContext, Future, Promise}

class RecommenderProcessor(session: CassandraSession, readSide: CassandraReadSide)
                          (implicit ec: ExecutionContext)
                            extends ReadSideProcessor[RecommenderEvent] {

  override def buildHandler(): ReadSideProcessor.ReadSideHandler[RecommenderEvent] = {
    readSide
      .builder[RecommenderEvent]("recommenderoffset")
      .setGlobalPrepare(createTable)
      .setPrepare(_ => prepareWriteRating())
      .setEventHandler[RatingSaved](processRatingSaved)
      .build()
  }

  override def aggregateTags: Set[AggregateEventTag[RecommenderEvent]] = Set(AggregateEventTag.apply(RecommenderEvent.Tag.tag))

  private def createTable(): Future[Done] =
    session.executeCreateTable(
      "CREATE TABLE IF NOT EXISTS rating(user_id TEXT, content_id TEXT, rating INT, timestamp TEXT, ruido TEXT, luz TEXT, loc TEXT, conectividad TEXT, move TEXT, channel TEXT, PRIMARY KEY (user_id, content_id))"
    )

  private val writeRatingPromise = Promise[PreparedStatement] // initialized in prepare
  private def writeRating: Future[PreparedStatement] = writeRatingPromise.future

  private def prepareWriteRating(): Future[Done] = {
    val future = session.prepare(
      "INSERT INTO rating(user_id, content_id, rating, timestamp, ruido, luz, loc, conectividad, move, channel) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
    )
    writeRatingPromise.completeWith(future)

    future map(_ => Done)
  }

  private def processRatingSaved(eventElement: EventStreamElement[RatingSaved]): Future[List[BoundStatement]] =
    writeRating map { ps =>
      val bindWriteRating = ps.bind()

      bindWriteRating.setString("user_id",      eventElement.event.user_id)
      bindWriteRating.setString("content_id",   eventElement.event.content_id)
      bindWriteRating.setInt("rating",          eventElement.event.rating)
      bindWriteRating.setString("timestamp",    eventElement.event.context.timestamp)
      bindWriteRating.setString("ruido",        eventElement.event.context.ruido)
      bindWriteRating.setString("luz",          eventElement.event.context.luz)
      bindWriteRating.setString("loc",          eventElement.event.context.loc)
      bindWriteRating.setString("conectividad", eventElement.event.context.conectividad)
      bindWriteRating.setString("move",         eventElement.event.context.move)
      bindWriteRating.setString("channel",      eventElement.event.context.channel)

      List(bindWriteRating)
    }
}