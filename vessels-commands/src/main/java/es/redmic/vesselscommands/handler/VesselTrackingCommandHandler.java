package es.redmic.vesselscommands.handler;

import java.util.concurrent.CompletableFuture;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import es.redmic.brokerlib.alert.AlertService;
import es.redmic.commandslib.commands.CommandHandler;
import es.redmic.commandslib.streaming.common.StreamConfig;
import es.redmic.commandslib.streaming.common.StreamConfig.Builder;
import es.redmic.exception.factory.ExceptionFactory;
import es.redmic.vesselscommands.aggregate.VesselTrackingAggregate;
import es.redmic.vesselscommands.commands.vesseltracking.CreateVesselTrackingCommand;
import es.redmic.vesselscommands.commands.vesseltracking.DeleteVesselTrackingCommand;
import es.redmic.vesselscommands.commands.vesseltracking.UpdateVesselTrackingCommand;
import es.redmic.vesselscommands.config.UserService;
import es.redmic.vesselscommands.statestore.VesselTrackingStateStore;
import es.redmic.vesselscommands.streams.VesselTrackingEventStreams;
import es.redmic.vesselslib.dto.tracking.VesselTrackingDTO;
import es.redmic.vesselslib.events.vesseltracking.VesselTrackingEventFactory;
import es.redmic.vesselslib.events.vesseltracking.VesselTrackingEventTypes;
import es.redmic.vesselslib.events.vesseltracking.common.VesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.create.CreateVesselTrackingCancelledEvent;
import es.redmic.vesselslib.events.vesseltracking.create.CreateVesselTrackingEnrichedEvent;
import es.redmic.vesselslib.events.vesseltracking.create.CreateVesselTrackingFailedEvent;
import es.redmic.vesselslib.events.vesseltracking.create.VesselTrackingCreatedEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.DeleteVesselTrackingCancelledEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.DeleteVesselTrackingConfirmedEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.DeleteVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.VesselTrackingDeletedEvent;
import es.redmic.vesselslib.events.vesseltracking.update.UpdateVesselTrackingCancelledEvent;
import es.redmic.vesselslib.events.vesseltracking.update.UpdateVesselTrackingEnrichedEvent;
import es.redmic.vesselslib.events.vesseltracking.update.VesselTrackingUpdatedEvent;

@Component
@KafkaListener(topics = "${broker.topic.vessel-tracking}")
public class VesselTrackingCommandHandler extends CommandHandler {

	@Value("${spring.kafka.properties.schema.registry.url}")
	private String schemaRegistry;

	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServers;

	@Value("${broker.topic.vessel-tracking}")
	private String vesselTrackingTopic;

	@Value("${broker.state.store.vesseltracking.dir}")
	private String stateStoreVesselTrackingDir;

	@Value("${broker.state.store.vesseltracking.id}")
	private String vesselTrackingIdConfig;

	@Value("${broker.stream.events.vesseltracking.id}")
	private String vesselTrackingEventsStreamId;

	@Value("${broker.topic.vessel}")
	private String vesselTopic;

	@Value("${broker.topic.realtime.tracking.vessels}")
	private String realtimeTrackingVesselsTopic;

	@Value("${stream.windows.time.ms}")
	private Long streamWindowsTime;

	private final String REDMIC_PROCESS = "REDMIC_PROCESS";

	private VesselTrackingStateStore vesselTrackingStateStore;

	@Autowired
	UserService userService;

	@Autowired
	AlertService alertService;

	public VesselTrackingCommandHandler() {
	}

	@PostConstruct
	private void setUp() {

		// @formatter:off
		
		Builder config = StreamConfig.Builder
			.bootstrapServers(bootstrapServers)
			.schemaRegistry(schemaRegistry)
			.stateStoreDir(stateStoreVesselTrackingDir)
			.topic(vesselTrackingTopic);
		
		vesselTrackingStateStore = new VesselTrackingStateStore(
				config
					.serviceId(vesselTrackingIdConfig)
					.build(), alertService);

		new VesselTrackingEventStreams(
				config
					.serviceId(vesselTrackingEventsStreamId)
					.windowsTime(streamWindowsTime)
					.build(), vesselTopic, realtimeTrackingVesselsTopic, alertService);
		// @formatter:on
	}

	public VesselTrackingDTO save(CreateVesselTrackingCommand cmd) {

		VesselTrackingAggregate agg = new VesselTrackingAggregate(vesselTrackingStateStore);

		logger.debug("Procesando CreateVesselCommand");

		// Se procesa el comando, obteniendo el evento generado
		VesselTrackingEvent event = agg.process(cmd);

		// Si no se genera evento significa que no se debe aplicar
		if (event == null) {
			return null;
		}

		event.setUserId(userService.getUserId());

		// Se aplica el evento
		agg.apply(event);

		// Crea la espera hasta que se responda con evento completado
		CompletableFuture<VesselTrackingDTO> completableFuture = getCompletableFeature(event.getSessionId(),
				agg.getVesselTracking());

		// Emite evento para enviar a kafka
		publishToKafka(event, vesselTrackingTopic);

		// Obtiene el resultado cuando se resuelva la espera
		return getResult(event.getSessionId(), completableFuture);
	}

	public VesselTrackingDTO update(String id, UpdateVesselTrackingCommand cmd) {

		VesselTrackingAggregate agg = new VesselTrackingAggregate(vesselTrackingStateStore);

		// Se procesa el comando, obteniendo el evento generado
		VesselTrackingEvent event = agg.process(cmd);

		// Si no se genera evento significa que no se va a aplicar
		if (event == null)
			return null;

		event.setUserId(userService.getUserId());

		// Si no existen excepciones, se aplica el comando
		agg.apply(event);

		// Crea la espera hasta que se responda con evento completado
		CompletableFuture<VesselTrackingDTO> completableFuture = getCompletableFeature(event.getSessionId(),
				agg.getVesselTracking());

		// Emite evento para enviar a kafka
		publishToKafka(event, vesselTrackingTopic);

		// Obtiene el resultado cuando se resuelva la espera
		return getResult(event.getSessionId(), completableFuture);
	}

	public VesselTrackingDTO update(String id, DeleteVesselTrackingCommand cmd) {

		VesselTrackingAggregate agg = new VesselTrackingAggregate(vesselTrackingStateStore);
		agg.setAggregateId(id);

		// Se procesa el comando, obteniendo el evento generado
		DeleteVesselTrackingEvent event = agg.process(cmd);

		// Si no se genera evento significa que no se va a aplicar
		if (event == null)
			return null;

		event.setUserId(userService.getUserId());

		// Si no existen excepciones, se aplica el comando
		agg.apply(event);

		// Crea la espera hasta que se responda con evento completado
		CompletableFuture<VesselTrackingDTO> completableFuture = getCompletableFeature(event.getSessionId(),
				agg.getVesselTracking());

		// Emite evento para enviar a kafka
		publishToKafka(event, vesselTrackingTopic);

		// Obtiene el resultado cuando se resuelva la espera
		return getResult(event.getSessionId(), completableFuture);
	}

	@KafkaHandler
	private void listen(CreateVesselTrackingEnrichedEvent event) {

		publishToKafka(
				VesselTrackingEventFactory.getEvent(event, VesselTrackingEventTypes.CREATE, event.getVesselTracking()),
				vesselTrackingTopic);
	}

	@KafkaHandler
	private void listen(VesselTrackingCreatedEvent event) {

		logger.debug("VesselTracking creado " + event.getAggregateId());

		// El evento Creado se envió desde el stream

		if (!event.getUserId().equals(REDMIC_PROCESS)) {
			resolveCommand(event.getSessionId());
		}
	}

	@KafkaHandler
	private void listen(UpdateVesselTrackingEnrichedEvent event) {

		publishToKafka(
				VesselTrackingEventFactory.getEvent(event, VesselTrackingEventTypes.UPDATE, event.getVesselTracking()),
				vesselTrackingTopic);
	}

	@KafkaHandler
	private void listen(VesselTrackingUpdatedEvent event) {

		logger.debug("VesselTracking modificado " + event.getAggregateId());

		// El evento Modificado se envió desde el stream

		if (!event.getUserId().equals(REDMIC_PROCESS)) {
			resolveCommand(event.getSessionId());
		}
	}

	@KafkaHandler
	private void listen(DeleteVesselTrackingConfirmedEvent event) {

		publishToKafka(VesselTrackingEventFactory.getEvent(event, VesselTrackingEventTypes.DELETED),
				vesselTrackingTopic);
	}

	@KafkaHandler
	private void listen(VesselTrackingDeletedEvent event) {

		logger.debug("VesselTracking eliminado " + event.getAggregateId());

		if (!event.getUserId().equals(REDMIC_PROCESS)) {
			resolveCommand(event.getSessionId());
		}
	}

	@KafkaHandler
	private void listen(CreateVesselTrackingFailedEvent event) {

		publishToKafka(VesselTrackingEventFactory.getEvent(event, VesselTrackingEventTypes.CREATE_CANCELLED,
				event.getExceptionType(), event.getArguments()), vesselTrackingTopic);
	}

	@KafkaHandler
	private void listen(CreateVesselTrackingCancelledEvent event) {

		logger.debug("Error creando VesselTracking " + event.getAggregateId());

		if (!event.getUserId().equals(REDMIC_PROCESS)) {
			resolveCommand(event.getSessionId(),
					ExceptionFactory.getException(event.getExceptionType(), event.getArguments()));
		}
	}

	@KafkaHandler
	private void listen(UpdateVesselTrackingCancelledEvent event) {

		logger.debug("Error modificando VesselTracking " + event.getAggregateId());

		// El evento Cancelled se envía desde el stream

		if (!event.getUserId().equals(REDMIC_PROCESS)) {
			resolveCommand(event.getSessionId(),
					ExceptionFactory.getException(event.getExceptionType(), event.getArguments()));
		}
	}

	@KafkaHandler
	private void listen(DeleteVesselTrackingCancelledEvent event) {

		logger.debug("Error eliminando VesselTracking " + event.getAggregateId());

		// El evento Cancelled se envía desde el stream

		if (!event.getUserId().equals(REDMIC_PROCESS)) {
			resolveCommand(event.getSessionId(),
					ExceptionFactory.getException(event.getExceptionType(), event.getArguments()));
		}
	}
}
