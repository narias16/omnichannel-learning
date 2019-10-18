package co.edu.eafit.dis.content.impl

import akka.Done
import co.edu.eafit.dis.content.api.Content
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag, AggregateEventTagger, PersistentEntity}
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import play.api.libs.json.{Format, Json}

import scala.collection.immutable.Seq

class ContentEntity extends PersistentEntity {
  override type Command = ContentCommand[_]
  override type Event = ContentEvent
  override type State = Content

  override def initialState: State = Content(null, null, -1, null, -1, null, null, -1)

  override def behavior: Behavior = {
    case Content(_, _, _, _, _, _, _, _) => Actions().onCommand[CreateContent, Done] {
      case (CreateContent(content), ctx, _) =>
        ctx.thenPersist(
          ContentCreated(content)
        ) { _ =>
          ctx.reply(Done)
        }
    }.onEvent {
      case(ContentCreated(content), _) => content
    }
  }
}

/**
  * STATE
  * The current state held by the persistent entity
  * Represented by the API Content object.
  * */


/**
  * EVENTS
  * This interface defines all the events that the ContentEntity supports.
  * */

sealed trait ContentEvent extends AggregateEvent[ContentEvent] {
  override def aggregateTag: AggregateEventTagger[ContentEvent] = ContentEvent.Tag
}

object ContentEvent {
  val Tag: AggregateEventTag[ContentEvent] = AggregateEventTag[ContentEvent]
}

/**
  * An event that represents a save in  [[co.edu.eafit.dis.content.api.Content]]
  * */
case class ContentCreated(content: Content) extends ContentEvent

object ContentCreated {
  implicit val format: Format[ContentCreated] = Json.format
}

/**
  * COMMANDS
  * This interface defines all the commands that the ContentEntity supports.
  * */
sealed trait ContentCommand[R] extends ReplyType[R]

/**
  * A command to create the content object
  *
  * It has a reply type of [[akka.Done]], which is sent back to the caller
  * when all the events emitted by this command are successfully persisted.
  */

case class CreateContent(content: Content) extends ContentCommand[Done]

object CreateContent {
  implicit val format: Format[CreateContent] = Json.format
}



/**
  * EXCEPTIONS
  * */
case class ContentNotFoundException(msg: String) extends Throwable

object ContentNotFoundException {
  implicit val format: Format[ContentNotFoundException] = Json.format
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

object ContentSerializeRegistry extends JsonSerializerRegistry {
  override def serializers: Seq[JsonSerializer[_]] = Seq(
    JsonSerializer[Content],
    JsonSerializer[ContentCreated],
    JsonSerializer[CreateContent],
    JsonSerializer[ContentNotFoundException]
  )
}