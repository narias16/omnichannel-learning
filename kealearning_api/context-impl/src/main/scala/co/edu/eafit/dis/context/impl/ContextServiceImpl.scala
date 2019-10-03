package co.edu.eafit.dis.context.impl

import akka.{Done, NotUsed}
import co.edu.eafit.dis.context.api._
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import java.util.Calendar

/**
  * Implementation of the [[ContextService]].
  */
class ContextServiceImpl(persistentEntityRegistry: PersistentEntityRegistry) extends ContextService {

  override def saveContextRegistry(user_id: String): ServiceCall[RawContextRegistry, Done] = { ctx =>
    val ref = persistentEntityRegistry.refFor[ContextEntity](user_id)

    // Parse numbers to classes
    val classifiedContext = mapping(ctx)

    ref.ask(SaveContextRegistry(classifiedContext))
  }

  override def getContextObject(user_id: String): ServiceCall[NotUsed, List[ContextRegistry]] = { _ =>
    val ref = persistentEntityRegistry.refFor[ContextEntity](user_id)

    ref.ask(GetContentObject(user_id))
  }

  // TODO add the corner cases
  def mapping(rawContextRegistry: RawContextRegistry): ContextRegistry = rawContextRegistry match {
      case RawContextRegistry(timestamp: String,
                              ruido: Double, luz: Double, lat: Double, lon: Double,
                              conectividad: String, acc: Double, canal: String) =>
        def ruidoToLevel(ruido: Double): Level =
          if(ruido < 2) Bajo("bajo")
          else if (ruido > 6) Alto("alto")
          else Medio("medio")

        def timestampToWeekSection(timestamp: String): WeekSection = {
          val cal: Calendar = Calendar.getInstance()
          cal.setTimeInMillis(timestamp.toLong)
          if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
            cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) Weekend("weekend")
          else Weekday("weekday")
        }

        def luzToLevel(luz: Double): Level =
          if (luz < 2) Bajo("bajo")
          else if (luz > 6) Alto("alto")
          else Medio("medio")

        def latLonToLocation(lat: Double, lon: Double): Location = House("house") // TODO connect with users service

        def conectividadToLevel(conectividad: String): Level = conectividad match {
          case "slow-2g" => Bajo("bajo")
          case "2g" => Bajo("bajo")
          case "3g" => Medio("medio")
          case "4g" => Alto("alto")
          case _ => Bajo("medio")
        }

        def accToMove(acc: Double): Move =
          if (acc > 0) Moving("moving")
          else Quiet("quiet")

        def channelToChannel(canal: String): Channel = canal match {
          case "web" => Web("web")
          case "email" => Email("email")
          case _ => UndefinedChannel("undefined")
        }


        ContextRegistry(timestampToWeekSection(timestamp), ruidoToLevel(ruido),
          luzToLevel(luz), latLonToLocation(lat, lon), conectividadToLevel(conectividad), accToMove(acc),
            channelToChannel(canal)
        )
    }
}