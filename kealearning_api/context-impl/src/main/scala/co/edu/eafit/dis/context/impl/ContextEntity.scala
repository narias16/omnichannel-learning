package co.edu.eafit.dis.context.impl

import java.time.LocalDateTime

import akka.Done
import co.edu.eafit.dis.context.api.ContextRegistry
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag, PersistentEntity}
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import play.api.libs.json.{Format, Json}

import scala.collection.immutable.Seq

class ContextEntity extends PersistentEntity {
  override type Command = ContextCommand[_]
  override type Event = ContextEvent
  override type State = ContextObjectState

  override def initialState: ContextObjectState =
    ContextObjectState("0", List.empty, LocalDateTime.now().toString)

  override def behavior: Behavior = {
    case ContextObjectState(_, _, _) => Actions().onCommand[SaveContextRegistry, Done] {

      // Command handler for the [[SaveContextRegistry]] command
      case (SaveContextRegistry(contextRegistry), ctx, _) =>
        ctx.thenPersist(
          ContextRegistrySaved(contextRegistry)
        ) { _ =>
          ctx.reply(Done)
        }

    }.onReadOnlyCommand[GetContentObject, List[ContextRegistry]] {
      case (GetContentObject(_), ctx, state) =>
        ctx.reply(state.registry)

    }.onEvent {
      // Event handler for the [[ContextRegistrySaved]] event
      case (ContextRegistrySaved(contextRegistry), state) =>
        ContextObjectState(state.id, contextRegistry :: state.registry, LocalDateTime.now().toString)
    }
  }
}

/**
  * STATE
  *
  * The current state held by the persistent entity.
  */
case class ContextObjectState(id: String, registry: List[ContextRegistry], timestamp: String)

object ContextObjectState {

  /**
    * Format for the [[ContextObjectState]] state.
    *
    * Persisted entities get snapshot'd every configured number of events. This
    * means the state gets stored to the database, so that when the entity gets
    * loaded, you don't need to replay all the events, just the ones since the
    * snapshot. Hence, a JSON format needs to be declared so that it can be
    * serialized and deserialized when storing to and from the database.
    */

  implicit val format: Format[ContextObjectState] = Json.format
}

/**
  * EVENTS
  *
  * This interface defines all the events that the ContextEntity supports.
  */
sealed trait ContextEvent extends AggregateEvent[ContextEvent] {
  def aggregateTag: AggregateEventTag[ContextEvent] = ContextEvent.Tag
}

object ContextEvent {
  val Tag: AggregateEventTag[ContextEvent] = AggregateEventTag[ContextEvent]
}

/**
  * An event that represents a save in [[ContextObjectState]].
  */
case class ContextRegistrySaved(ctx: ContextRegistry) extends ContextEvent

object ContextRegistrySaved {

  /**
    * Format for the context object saved event.
    *
    * Events get stored and loaded from the database, hence a JSON format
    * needs to be declared so that they can be serialized and deserialize.
    */
  implicit val format: Format[ContextRegistrySaved] = Json.format
}

/**
  * COMMANDS
  *
  * This interface defines all the commands that the ContextEntity supports.
  */
sealed trait ContextCommand[R] extends ReplyType[R]

/**
  * A command to save the context object
  *
  * It has a reply type of [[Done]], which is sent back to the caller
  * when all the events emitted by this command are successfully persisted.
  */
case class SaveContextRegistry(ctx: ContextRegistry) extends ContextCommand[Done]

object SaveContextRegistry {

  /**
    * Format for the [[SaveContextRegistry]] command.
    *
    * Persistent entities get sharded across the cluster. This means commands
    * may be sent over the network to the node where the entity lives if the
    * entity is not on the same node that the command was issued from. To do
    * that, a JSON format needs to be declared so the command can be serialized
    * and deserialized.
    */
  implicit val format: Format[SaveContextRegistry] = Json.format
}

/***
  * A command that represents a read only for the [[ContextObjectState]]
  *
  * It has a reply type of List[ContextRegistry]
  * */
case class GetContentObject(id: String) extends ContextCommand[List[ContextRegistry]]

object GetContentObject {
  implicit val format: Format[GetContentObject] = Json.format
}

/**
  * Akka serialization, used by both persistence and remoting, needs to have
  * serializers registered for every type serialized or deserialized. While it's
  * possible to use any serializer you want for Akka messages, out of the box
  * Lagom provides support for JSON, via this registry abstraction.
  *
  * The serializers are registered here, and then provided to Lagom in the
  * application loader.
  */
object ContextSerializerRegistry extends JsonSerializerRegistry {
  override def serializers: Seq[JsonSerializer[_]] = Seq(
    JsonSerializer[SaveContextRegistry],
    JsonSerializer[ContextRegistrySaved],
    JsonSerializer[ContextObjectState],
  )
}
