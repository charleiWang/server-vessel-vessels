package es.redmic.vesselscommands.commands;

import java.util.concurrent.CompletableFuture;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import es.redmic.brokerlib.alert.AlertService;
import es.redmic.commandslib.commands.CommandHandler;
import es.redmic.commandslib.statestore.StreamConfig;
import es.redmic.exception.factory.ExceptionFactory;
import es.redmic.vesselscommands.aggregate.VesselTypeAggregate;
import es.redmic.vesselscommands.config.UserService;
import es.redmic.vesselscommands.statestore.VesselTypeStateStore;
import es.redmic.vesselscommands.streams.VesselTypeEventStreams;
import es.redmic.vesselslib.dto.VesselTypeDTO;
import es.redmic.vesselslib.events.vesseltype.create.CreateVesselTypeCancelledEvent;
import es.redmic.vesselslib.events.vesseltype.create.CreateVesselTypeEvent;
import es.redmic.vesselslib.events.vesseltype.create.CreateVesselTypeFailedEvent;
import es.redmic.vesselslib.events.vesseltype.create.VesselTypeCreatedEvent;
import es.redmic.vesselslib.events.vesseltype.delete.DeleteVesselTypeCancelledEvent;
import es.redmic.vesselslib.events.vesseltype.delete.DeleteVesselTypeConfirmedEvent;
import es.redmic.vesselslib.events.vesseltype.delete.DeleteVesselTypeEvent;
import es.redmic.vesselslib.events.vesseltype.delete.VesselTypeDeletedEvent;
import es.redmic.vesselslib.events.vesseltype.update.UpdateVesselTypeCancelledEvent;
import es.redmic.vesselslib.events.vesseltype.update.UpdateVesselTypeEvent;
import es.redmic.vesselslib.events.vesseltype.update.VesselTypeUpdatedEvent;

@Component
@KafkaListener(topics = "${broker.topic.vessel-type}")
public class VesselTypeCommandHandler extends CommandHandler {

	@Value("${spring.kafka.properties.schema.registry.url}")
	protected String schemaRegistry;

	@Value("${spring.kafka.bootstrap-servers}")
	protected String bootstrapServers;

	@Value("${broker.topic.vessel-type}")
	private String vessel_type_topic;

	@Value("${broker.state.store.vesseltypes.dir}")
	private String state_store_vesseltypes_dir;

	@Value("${broker.state.store.vesseltypes.id}")
	private String vesseltypes_id_config;

	@Value("${broker.stream.events.vesseltypes.id}")
	private String vesseltypes_events_stream_id;

	@Value("${stream.windows.time.ms}")
	private Long streamWindowsTime;

	private VesselTypeStateStore vesselTypeStateStore;

	@Autowired
	UserService userService;

	@Autowired
	AlertService alertService;

	public VesselTypeCommandHandler() {

	}

	@PostConstruct
	private void setUp() {

		// @formatter:off
		
		vesselTypeStateStore = new VesselTypeStateStore(
				StreamConfig.Builder
					.bootstrapServers(bootstrapServers)
					.schemaRegistry(schemaRegistry)
					.serviceId(vesseltypes_id_config)
					.stateStoreDir(state_store_vesseltypes_dir)
					.topic(vessel_type_topic)
					.build(), alertService);

		new VesselTypeEventStreams(StreamConfig.Builder
				.bootstrapServers(bootstrapServers)
				.schemaRegistry(schemaRegistry)
				.serviceId(vesseltypes_events_stream_id)
				.stateStoreDir(state_store_vesseltypes_dir)
				.topic(vessel_type_topic)
				.windowsTime(streamWindowsTime)
				.build(), alertService);
		
		// @formatter:on
	}

	public VesselTypeDTO save(CreateVesselTypeCommand cmd) {

		VesselTypeAggregate agg = new VesselTypeAggregate(vesselTypeStateStore);

		// Se procesa el comando, obteniendo el evento generado
		logger.debug("Procesando CreateVesselTypeCommand");

		CreateVesselTypeEvent event = agg.process(cmd);

		// Si no se genera evento significa que no se debe aplicar
		if (event == null)
			return null;

		event.setUserId(userService.getUserId());

		// Se aplica el evento
		agg.apply(event);

		logger.debug("Aplicado evento: " + event.getType());

		// Crea la espera hasta que se responda con evento completado
		CompletableFuture<VesselTypeDTO> completableFuture = getCompletableFeature(event.getSessionId(),
				agg.getVesselType());

		// Emite evento para enviar a kafka
		publishToKafka(event, vessel_type_topic);

		// Obtiene el resultado cuando se resuelva la espera
		return getResult(event.getSessionId(), completableFuture);
	}

	public VesselTypeDTO update(String id, UpdateVesselTypeCommand cmd) {

		VesselTypeAggregate agg = new VesselTypeAggregate(vesselTypeStateStore);

		// Se procesa el comando, obteniendo el evento generado
		UpdateVesselTypeEvent event = agg.process(cmd);

		// Si no se genera evento significa que no se va a aplicar
		if (event == null)
			return null;

		event.setUserId(userService.getUserId());

		// Si no existen excepciones, se aplica el comando
		agg.apply(event);

		// Crea la espera hasta que se responda con evento completado
		CompletableFuture<VesselTypeDTO> completableFuture = getCompletableFeature(event.getSessionId(),
				agg.getVesselType());

		// Emite evento para enviar a kafka
		publishToKafka(event, vessel_type_topic);

		// Obtiene el resultado cuando se resuelva la espera
		return getResult(event.getSessionId(), completableFuture);
	}

	public VesselTypeDTO update(String id, DeleteVesselTypeCommand cmd) {

		VesselTypeAggregate agg = new VesselTypeAggregate(vesselTypeStateStore);
		agg.setAggregateId(id);

		// Se procesa el comando, obteniendo el evento generado
		DeleteVesselTypeEvent event = agg.process(cmd);

		// Si no se genera evento significa que no se va a aplicar
		if (event == null)
			return null;

		event.setUserId(userService.getUserId());

		// Si no existen excepciones, se aplica el comando
		agg.apply(event);

		// Crea la espera hasta que se responda con evento completado
		CompletableFuture<VesselTypeDTO> completableFuture = getCompletableFeature(event.getSessionId(),
				agg.getVesselType());

		// Emite evento para enviar a kafka
		publishToKafka(event, vessel_type_topic);

		// Obtiene el resultado cuando se resuelva la espera
		return getResult(event.getSessionId(), completableFuture);
	}

	public VesselTypeDTO getVesselType(VesselTypeDTO type) {

		VesselTypeAggregate vesselTypeAggregate = new VesselTypeAggregate(vesselTypeStateStore);

		return vesselTypeAggregate.getVesselTypeFromStateStore(type);
	}

	@KafkaHandler
	private void listen(VesselTypeCreatedEvent event) {

		logger.info("VesselType creado " + event.getAggregateId());

		// El evento Creado se envía desde el stream

		resolveCommand(event.getSessionId());
	}

	@KafkaHandler
	private void listen(VesselTypeUpdatedEvent event) {

		logger.info("VesselType modificado " + event.getAggregateId());

		// El evento Modificado se envía desde el stream

		resolveCommand(event.getSessionId());
	}

	@KafkaHandler
	private void listen(DeleteVesselTypeConfirmedEvent event) {

		logger.info("Enviando evento VesselTypeDeletedEvent para: " + event.getAggregateId());

		publishToKafka(new VesselTypeDeletedEvent().buildFrom(event), vessel_type_topic);
	}

	@KafkaHandler
	private void listen(VesselTypeDeletedEvent event) {

		logger.info("VesselType eliminado " + event.getAggregateId());

		resolveCommand(event.getSessionId());
	}

	@KafkaHandler
	private void listen(CreateVesselTypeFailedEvent event) {

		logger.info("Enviando evento CreateVesselTypeCancelledEvent para: " + event.getAggregateId());

		CreateVesselTypeCancelledEvent evt = new CreateVesselTypeCancelledEvent().buildFrom(event);
		evt.setExceptionType(event.getExceptionType());
		evt.setArguments(event.getArguments());

		publishToKafka(evt, vessel_type_topic);
	}

	@KafkaHandler
	private void listen(CreateVesselTypeCancelledEvent event) {

		logger.info("Error creando VesselType " + event.getAggregateId());

		resolveCommand(event.getSessionId(),
				ExceptionFactory.getException(event.getExceptionType(), event.getArguments()));
	}

	@KafkaHandler
	private void listen(UpdateVesselTypeCancelledEvent event) {

		logger.info("Error modificando VesselType " + event.getAggregateId());

		// El evento Cancelled se envía desde el stream

		resolveCommand(event.getSessionId(),
				ExceptionFactory.getException(event.getExceptionType(), event.getArguments()));
	}

	@KafkaHandler
	private void listen(DeleteVesselTypeCancelledEvent event) {

		logger.info("Error eliminando VesselType " + event.getAggregateId());

		// El evento Cancelled se envía desde el stream

		resolveCommand(event.getSessionId(),
				ExceptionFactory.getException(event.getExceptionType(), event.getArguments()));
	}
}
