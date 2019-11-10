package co.edu.eafit.dis.recommender.impl

import co.edu.eafit.dis.recommender.api.RecommenderService
import co.edu.eafit.dis.context.api.ContextService

import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaComponents
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.playjson.JsonSerializerRegistry
import com.lightbend.lagom.scaladsl.server.{LagomApplication, LagomApplicationContext, LagomApplicationLoader, LagomServer}
import com.lightbend.lagom.scaladsl.api.{Descriptor, ServiceLocator}
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents

import com.softwaremill.macwire._

import play.api.libs.ws.ahc.AhcWSComponents
import play.api.mvc.EssentialFilter
import play.filters.cors.CORSComponents

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
  with AhcWSComponents
  with CORSComponents {

  // CORS configuration
  override val httpFilters: Seq[EssentialFilter] = Seq(corsFilter)

  // Bind the service that this service provides
  override lazy val lagomServer: LagomServer = serverFor[RecommenderService](wire[RecommenderServiceImpl])

  // Register the JSON serializer registry
  override lazy val jsonSerializerRegistry: JsonSerializerRegistry = RecommenderSerializerRegistry

  /** Implement a ServiceClient for an internal call to [[ContextService]] */
  lazy val contextService = serviceClient.implement[ContextService]

  // Register the recommender persistent entity
  persistentEntityRegistry.register(wire[RecommenderEntity])

  // Register any ReadSideProcessor's if any
  readSide.register(wire[RecommenderProcessor])
}