package co.edu.eafit.dis.recommender.impl

import akka.Done
import co.edu.eafit.dis.context.api.ContextRegistryString
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag, AggregateEventTagger, PersistentEntity}
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import play.api.libs.json.{Format, Json}

import scala.collection.immutable.Seq

// TODO read side for RecommenderService
class RecommenderEntity extends PersistentEntity {
  override type Command = RecommenderCommand[_]
  override type Event = RecommenderEvent
  override type State = RecommenderObjectState

  override def initialState: RecommenderObjectState =
    RecommenderObjectState("", "", 0, ContextRegistryString("", "", "", "", "", "", ""))

  override def behavior: Behavior = {
    case RecommenderObjectState(_, _, _, _) => Actions().onCommand[SaveRating, Done] {
      // Command handler for the SaveRating command
      case (SaveRating(user_id: String, content_id: String, rating: Int, context: ContextRegistryString), ctx, _) =>
        ctx.thenPersist(
          RatingSaved(user_id, content_id, rating, context)
        ) { _ =>
          ctx.reply(Done)
        }
    }.onEvent {
      // Event handler for the RatingSaved event
      case(RatingSaved(user_id, content_id, rating, context), _) =>
        RecommenderObjectState(user_id, content_id, rating, context)
    }
  }
}

/**
  * STATE
  * The current state held by the persistent entity
  * Represented by the API [[co.edu.eafit.dis.recommender.api.ContextRating]] object.
  * */
case class RecommenderObjectState(user_id: String, content_id: String, rating: Int, context: ContextRegistryString)

object RecommenderObjectState {
  /**
    * Format for the [[RecommenderObjectState]] state.
    *
    * Persisted entities get snapshot'd every configured number of events. This
    * means the state gets stored to the database, so that when the entity gets
    * loaded, you don't need to replay all the events, just the ones since the
    * snapshot. Hence, a JSON format needs to be declared so that it can be
    * serialized and deserialized when storing to and from the database.
    */

  implicit val format: Format[RecommenderObjectState] = Json.format
}

/**
  * EVENTS
  *
  * This interface defines all the events that the RecommenderEntity supports.
  */
sealed trait RecommenderEvent extends AggregateEvent[RecommenderEvent] {
  override def aggregateTag: AggregateEventTag[RecommenderEvent] = RecommenderEvent.Tag
}

object RecommenderEvent {
  val Tag: AggregateEventTag[RecommenderEvent] = AggregateEventTag[RecommenderEvent]
}

/**
  * An event that represents a save in [[co.edu.eafit.dis.recommender.impl.RecommenderObjectState]].
  */
case class RatingSaved(user_id: String, content_id: String, rating: Int, context: ContextRegistryString)
  extends RecommenderEvent

object RatingSaved {
  /**
    * Format for the rating saved event.
    *
    * Events get stored and loaded from the database, hence a JSON format
    * needs to be declared so that they can be serialized and deserialize.
    */
  implicit val format: Format[RatingSaved] = Json.format
}

/**
  * COMMANDS
  *
  * This interface defines all the commands that the [[co.edu.eafit.dis.recommender.impl.RecommenderEntity]] supports.
  */

sealed trait RecommenderCommand[R] extends ReplyType[R]

/**
  * A command to save the rating object
  *
  * It has a reply type of [[akka.Done]], which is sent back to the caller
  * when all the events emitted by this command are successfully persisted.
  */

case class SaveRating(user_id: String, content_id: String, rating: Int, context: ContextRegistryString)
  extends RecommenderCommand[Done]

object SaveRating {
  /**
    * Format for the [[SaveRating]] command.
    *
    * Persistent entities get sharded across the cluster. This means commands
    * may be sent over the network to the node where the entity lives if the
    * entity is not on the same node that the command was issued from. To do
    * that, a JSON format needs to be declared so the command can be serialized
    * and deserialized.
    */
  implicit val format: Format[SaveRating] = Json.format
}

// TODO read only for recommender

/**
  * Akka serialization, used by both persistence and remoting, needs to have
  * serializers registered for every type serialized or deserialized. While it's
  * possible to use any serializer you want for Akka messages, out of the box
  * Lagom provides support for JSON, via this registry abstraction.
  *
  * The serializers are registered here, and then provided to Lagom in the
  * application loader.
  */

object RecommenderSerializerRegistry extends JsonSerializerRegistry {
  override def serializers: Seq[JsonSerializer[_]] = Seq(
    JsonSerializer[SaveRating],
    JsonSerializer[RatingSaved],
    JsonSerializer[RecommenderObjectState],
  )
}