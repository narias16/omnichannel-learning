package co.edu.eafit.dis.context.impl

import akka.{Done, NotUsed}
import co.edu.eafit.dis.context.api._
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.{PersistentEntityRegistry, ReadSide}
import java.util.Calendar

import com.lightbend.lagom.scaladsl.persistence.cassandra.{CassandraReadSide, CassandraSession}

import scala.concurrent.ExecutionContext.Implicits.global


/**
  * Implementation of the [[ContextService]].
  */
class ContextServiceImpl(persistentEntityRegistry: PersistentEntityRegistry,
                         session: CassandraSession,
                         cassandraReadSide: CassandraReadSide,
                         readSide: ReadSide) extends ContextService {

  /**
    * Write side
    * */
  override def saveContextRegistry(user_id: String): ServiceCall[RawContextRegistry, Done] = { ctx =>
    val ref = persistentEntityRegistry.refFor[ContextEntity](user_id)

    // Map numbers to classes
    val classifiedContext = mapping(ctx)

    ref.ask(SaveContextRegistry(user_id, classifiedContext))
  }

  override def getContextObject(user_id: String): ServiceCall[NotUsed, List[ContextRegistryString]] = { _ =>
    val ref = persistentEntityRegistry.refFor[ContextEntity](user_id)

    ref.ask(GetContentObject(user_id))
  }

  /**
    * Read side
    **/
  override def getUserContext(user_id: String): ServiceCall[NotUsed, Seq[ContextRegistryString]] =
    ServiceCall { _ =>
      session
        .selectAll(s"SELECT * FROM context WHERE user_id = '$user_id'")
        .map { rows =>
          rows.map { row =>
            ContextRegistryString(
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

  override def allContext: ServiceCall[NotUsed, Seq[ContextRegistryString]] =
    ServiceCall { _ =>
      session
        .selectAll("SELECT * FROM context")
        .map { rows =>
          rows.map { row =>
            ContextRegistryString(
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

  // TODO add the corner cases
  def mapping(rawContextRegistry: RawContextRegistry): ContextRegistryString = rawContextRegistry match {
      case RawContextRegistry(timestamp: String,
                              ruido: Double, luz: Double, lat: Double, lon: Double,
                              conectividad: String, acc: Double, canal: String) =>
        def ruidoToLevel(ruido: Double): String =
          if(ruido < 2) "bajo"
          else if (ruido > 6) "alto"
          else "medio"

        def timestampToWeekSection(timestamp: String): String = {
          val cal: Calendar = Calendar.getInstance()
          cal.setTimeInMillis(timestamp.toLong)
          if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
            cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) "weekend"
          else "weekday"
        }

        def luzToLevel(luz: Double): String =
          if(luz < 0) "undefined"
          else if (luz < 2) "bajo"
          else if (luz > 6) "alto"
          else "medio"

        // TODO connect with users service
        def latLonToLocation(lat: Double, lon: Double): String = "universidad" // "otro" - "casa"

        def conectividadToLevel(conectividad: String): String = conectividad match {
          case "slow-2g" => "bajo"
          case "2g" => "bajo"
          case "3g" => "medio"
          case "4g" => "alto"
          case _ => "medio"
        }

        def accToMove(acc: Double): String =
          if (acc < 0) "undefined"
          else if (acc > 0) "movimiento"
          else "quieto"

        def channelToChannel(canal: String): String = canal match {
          case "web" => "web"
          case "email" => "correo"
          case _ => "undefined"
        }

        ContextRegistryString(timestampToWeekSection(timestamp), ruidoToLevel(ruido),
          luzToLevel(luz), latLonToLocation(lat, lon), conectividadToLevel(conectividad), accToMove(acc),
            channelToChannel(canal)
        )
    }
}