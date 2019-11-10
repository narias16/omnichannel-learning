package co.edu.eafit.dis.recommender.impl

import java.util.UUID

import akka.{Done, NotUsed}
import co.edu.eafit.dis.context.api.{ContextRegistryString, ContextService}
import co.edu.eafit.dis.recommender.api.{ContextRating, FlatRating, Rating, RecommenderService}
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.{PersistentEntityRegistry, ReadSide}
import com.lightbend.lagom.scaladsl.persistence.cassandra.{CassandraReadSide, CassandraSession}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RecommenderServiceImpl(persistentEntityRegistry: PersistentEntityRegistry,
                             contextService: ContextService,
                             session: CassandraSession,
                             cassandraReadSide: CassandraReadSide,
                             readSide: ReadSide) extends RecommenderService {

  override def saveRating(): ServiceCall[Rating, Done] = ServiceCall { rating =>
    val result: Future[Seq[ContextRegistryString]] =
      contextService.getUserContext(rating.user_id).invoke(NotUsed)

    result map { response =>
      val id = UUID.randomUUID().toString
      val ref = persistentEntityRegistry.refFor[RecommenderEntity](id)
      ref.ask(SaveRating(rating.user_id, rating.content_id, rating.rating, response.head))
    } map (_ => Done)
  }


  /**
    * Read side
    **/
  override def allRating(): ServiceCall[NotUsed, Seq[FlatRating]] = ServiceCall { _ =>
    session
      .selectAll("SELECT * from rating")
      .map {rows =>
        rows.map { row =>
          FlatRating(
            row.getString("user_id"),
            row.getString("content_id"),
            row.getInt("rating"),
            row.getString("timestamp"),
            row.getString("ruido"),
            row.getString("luz"),
            row.getString("loc"),
            row.getString("conectividad"),
            row.getString("move"),
            row.getString("channel")
          )
        }
      }
  }
}