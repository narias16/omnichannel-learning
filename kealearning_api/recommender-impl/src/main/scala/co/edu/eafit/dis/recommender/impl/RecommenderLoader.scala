package co.edu.eafit.dis.recommender.impl

import co.edu.eafit.dis.recommender.api.RecommenderService
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaComponents
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.playjson.JsonSerializerRegistry
import com.lightbend.lagom.scaladsl.server.{LagomApplication, LagomApplicationContext, LagomApplicationLoader, LagomServer}
import com.softwaremill.macwire._
import play.api.libs.ws.ahc.AhcWSComponents
import co.edu.eafit.dis.context.api.ContextService
import com.lightbend.lagom.scaladsl.api.{Descriptor, ServiceLocator}
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents

class RecommenderLoader extends LagomApplicationLoader {
  override def load(context: LagomApplicationContext): LagomApplication =
    new RecommenderApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new RecommenderApplication(context) with LagomDevModeComponents

  override def describeService: Option[Descriptor] = Some(readDescriptor[RecommenderService])
}

abstract class RecommenderApplication(context: LagomApplicationContext) extends LagomApplication(context)
  with CassandraPersistenceComponents
  with LagomKafkaComponents
  with AhcWSComponents {

  // Bind the service that this service provides
  override lazy val lagomServer: LagomServer = serverFor[RecommenderService](wire[RecommenderServiceImpl])

  // Register the JSON serializer registry
  override lazy val jsonSerializerRegistry: JsonSerializerRegistry = RecommenderSerializerRegistry

  /** Implement a ServiceClient for an internal call to [[ContextService]] */
  lazy val contextService = serviceClient.implement[ContextService]

  // Register the recommender persistent entity
  persistentEntityRegistry.register(wire[RecommenderEntity])

  // Register any ReadSideProcessor's if any
  //readSide.register(wire[RecommenderEntityProcessor])
}