package co.edu.eafit.dis.context.api

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}
import com.lightbend.lagom.scaladsl.api.deser.MessageSerializer._
import play.api.libs.json.{Format, Json}


object ContextService {
  val TOPIC_NAME = "context"
}

/**
  * The context service interface.
  * <p>
  * This describes everything that Lagom needs to know about how to serve and
  * consume the ContextService.
  */
trait ContextService extends Service {
  /**
    * curl http://localhost:9000/api/context/:user_id/save
    */
  def saveContextRegistry(user_id: String): ServiceCall[RawContextRegistry, Done]

  /**
    * curl http://localhost:9000/api/context/:user_id
    */
  def getContextObject(user_id: String): ServiceCall[NotUsed, List[ContextRegistryString]]

  /**
    * curl http://localhost:9000/api/context/:user_id
    * */
  def getUserContext(user_id: String): ServiceCall[NotUsed, Seq[ContextRegistryString]]

  /**
    * curl http://localhost:9000/api/context
    * */
  def allContext: ServiceCall[NotUsed, Seq[ContextRegistryString]]


  override final def descriptor: Descriptor = {
    import Service._
    // @formatter:off
    named("/context")
      .withCalls(
        restCall(method = Method.POST, pathPattern = "/context/:id/save", saveContextRegistry _),
        pathCall("/context/:id", getUserContext _),
        restCall(method = Method.GET, pathPattern = "/context", allContext)
        //restCall(method = Method.GET, pathPattern = "/context/:id", getUserContext _)
      )
      .withAutoAcl(autoAcl = true)
    // @formatter:on
  }
}

/**
  * The context object class.
  */
// TODO hacer read-side para el contexto
// TODO - Connect frontend service
// TODO - Add noise and accelerometer and connectivity from client
// TODO - recommendation micro service
// TODO - front end service
case class RawContextRegistry(timestamp: String,
                           ruido: Double, luz: Double, lat: Double, lon: Double,
                           conectividad: String, acc: Double, canal: String)

object RawContextRegistry {
  implicit val format: Format[RawContextRegistry] = Json.format
}

case class ContextRegistryString(timestamp: String,
                                 ruido: String, luz: String, loc: String,
                                 conectividad: String, move: String, channel: String)

object ContextRegistryString {
  implicit val format: Format[ContextRegistryString] = Json.format
}

case class ContextRegistry(timestamp: WeekSection,
                              ruido: Level, luz: Level, loc: Location,
                              conectividad: Level, move: Move, channel: Channel)

object ContextRegistry {
  implicit val format: Format[ContextRegistry] = Json.format
}


sealed abstract class WeekSection
case class Weekday(weekday: String) extends WeekSection
case class Weekend(weekend: String) extends WeekSection

object WeekSection {
  implicit val format: Format[WeekSection] = Json.format
}

object Weekday {
  implicit val format: Format[Weekday] = Json.format
}

object Weekend {
  implicit val format: Format[Weekend] = Json.format
}

sealed abstract class Level
case class Alto(alto: String) extends Level
case class Medio(medio: String) extends Level
case class Bajo(bajo: String) extends Level
case class UndefinedLevel(undefined: String) extends Level

object Level {
  implicit val format: Format[Level] = Json.format
}

object Alto {
  implicit val format: Format[Alto] = Json.format
}

object Medio {
  implicit val format: Format[Medio] = Json.format
}

object Bajo {
  implicit val format: Format[Bajo] = Json.format
}

object UndefinedLevel {
  implicit val format: Format[UndefinedLevel] = Json.format
}

sealed abstract class Location
case class House(house: String) extends Location
case class School(school: School) extends Location
case class UndefinedLocation(undefined: String) extends Location

object Location {
  implicit val format: Format[Location] = Json.format
}

object House {
  implicit val format: Format[House] = Json.format
}

object School {
  implicit val format: Format[School] = Json.format
}

object UndefinedLocation {
  implicit val format: Format[UndefinedLocation] = Json.format
}

sealed abstract class Move
case class Moving(moving: String) extends Move
case class Quiet(quiet: String) extends Move
case class UndefinedMove(undefined: String) extends Move

object Move {
  implicit val format: Format[Move] = Json.format
}

object Moving {
  implicit val format: Format[Moving] = Json.format
}

object Quiet {
  implicit val format: Format[Quiet] = Json.format
}

object UndefinedMove {
  implicit val format: Format[UndefinedMove] = Json.format
}

sealed abstract class Channel
case class Email(email: String) extends Channel
case class Web(web: String) extends Channel
case class UndefinedChannel(undefined: String) extends Channel

object Channel {
  implicit val format: Format[Channel] = Json.format
}

object Email {
  implicit val format: Format[Email] = Json.format
}

object Web {
  implicit val format: Format[Web] = Json.format
}

object UndefinedChannel {
  implicit val format: Format[UndefinedChannel] = Json.format
}