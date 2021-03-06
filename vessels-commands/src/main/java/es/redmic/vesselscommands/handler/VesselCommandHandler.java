package es.redmic.vesselscommands.handler;

/*-
 * #%L
 * Vessels-management
 * %%
 * Copyright (C) 2019 REDMIC Project / Server
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
import es.redmic.vesselscommands.aggregate.VesselAggregate;
import es.redmic.vesselscommands.commands.vessel.CreateVesselCommand;
import es.redmic.vesselscommands.commands.vessel.DeleteVesselCommand;
import es.redmic.vesselscommands.commands.vessel.UpdateVesselCommand;
import es.redmic.vesselscommands.config.UserService;
import es.redmic.vesselscommands.statestore.VesselStateStore;
import es.redmic.vesselscommands.streams.VesselEventStreams;
import es.redmic.vesselslib.dto.vessel.VesselDTO;
import es.redmic.vesselslib.events.vessel.VesselEventFactory;
import es.redmic.vesselslib.events.vessel.VesselEventTypes;
import es.redmic.vesselslib.events.vessel.common.VesselEvent;
import es.redmic.vesselslib.events.vessel.create.CreateVesselCancelledEvent;
import es.redmic.vesselslib.events.vessel.create.CreateVesselEnrichedEvent;
import es.redmic.vesselslib.events.vessel.create.CreateVesselFailedEvent;
import es.redmic.vesselslib.events.vessel.create.VesselCreatedEvent;
import es.redmic.vesselslib.events.vessel.delete.CheckDeleteVesselEvent;
import es.redmic.vesselslib.events.vessel.delete.DeleteVesselCancelledEvent;
import es.redmic.vesselslib.events.vessel.delete.DeleteVesselCheckFailedEvent;
import es.redmic.vesselslib.events.vessel.delete.DeleteVesselCheckedEvent;
import es.redmic.vesselslib.events.vessel.delete.DeleteVesselConfirmedEvent;
import es.redmic.vesselslib.events.vessel.delete.VesselDeletedEvent;
import es.redmic.vesselslib.events.vessel.update.UpdateVesselCancelledEvent;
import es.redmic.vesselslib.events.vessel.update.UpdateVesselEnrichedEvent;
import es.redmic.vesselslib.events.vessel.update.VesselUpdatedEvent;

@Component
@KafkaListener(topics = "${broker.topic.vessel}")
public class VesselCommandHandler extends CommandHandler {

	@Value("${spring.kafka.properties.schema.registry.url}")
	private String schemaRegistry;

	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServers;

	@Value("${broker.topic.vessel}")
	private String vesselTopic;

	@Value("${broker.topic.vessel.type.updated}")
	private String vesselTypeUpdatedTopic;

	@Value("${broker.topic.vessels.agg.by.vesseltype}")
	private String vesselsAggByVesselTypeTopic;

	@Value("${broker.state.store.vessels.dir}")
	private String stateStoreVesselsDir;

	@Value("${broker.state.store.vessels.id}")
	private String vesselsIdConfig;

	@Value("${broker.stream.events.vessels.id}")
	private String vesselsEventsStreamId;

	@Value("${broker.topic.vessel-type}")
	private String vesselTypeTopic;

	@Value("${broker.topic.realtime.vessels}")
	private String realtimeVesselsTopic;

	@Value("${stream.windows.time.ms}")
	private Long streamWindowsTime;

	private final String REDMIC_PROCESS = "REDMIC_PROCESS";

	private VesselStateStore vesselStateStore;

	@Autowired
	UserService userService;

	@Autowired
	AlertService alertService;

	public VesselCommandHandler() {
	}

	@PostConstruct
	private void setUp() {

		// @formatter:off
		
		Builder config = StreamConfig.Builder
			.bootstrapServers(bootstrapServers)
			.schemaRegistry(schemaRegistry)
			.stateStoreDir(stateStoreVesselsDir)
			.topic(vesselTopic);
		
		vesselStateStore = new VesselStateStore(
				config
					.serviceId(vesselsIdConfig)
					.build(), alertService);

		new VesselEventStreams(
				config
					.serviceId(vesselsEventsStreamId)
					.windowsTime(streamWindowsTime)
					.build(), vesselTypeTopic, vesselsAggByVesselTypeTopic,
						vesselTypeUpdatedTopic, realtimeVesselsTopic, alertService);
		// @formatter:on
	}

	public VesselDTO save(CreateVesselCommand cmd) {

		VesselAggregate agg = new VesselAggregate(vesselStateStore);

		logger.debug("Procesando CreateVesselCommand");

		// Se procesa el comando, obteniendo el evento generado
		VesselEvent event = agg.process(cmd);

		// Si no se genera evento significa que no se debe aplicar
		if (event == null) {
			return null;
		}

		event.setUserId(userService.getUserId());

		// Se aplica el evento
		agg.apply(event);

		// Crea la espera hasta que se responda con evento completado
		CompletableFuture<VesselDTO> completableFuture = getCompletableFeature(event.getSessionId(), agg.getVessel());

		// Emite evento para enviar a kafka
		publishToKafka(event, vesselTopic);

		// Obtiene el resultado cuando se resuelva la espera
		return getResult(event.getSessionId(), completableFuture);
	}

	public VesselDTO update(String id, UpdateVesselCommand cmd) {

		VesselAggregate agg = new VesselAggregate(vesselStateStore);

		// Se procesa el comando, obteniendo el evento generado
		VesselEvent event = agg.process(cmd);

		// Si no se genera evento significa que no se va a aplicar
		if (event == null)
			return null;

		event.setUserId(userService.getUserId());

		// Si no existen excepciones, se aplica el comando
		agg.apply(event);

		// Crea la espera hasta que se responda con evento completado
		CompletableFuture<VesselDTO> completableFuture = getCompletableFeature(event.getSessionId(), agg.getVessel());

		// Emite evento para enviar a kafka
		publishToKafka(event, vesselTopic);

		// Obtiene el resultado cuando se resuelva la espera
		return getResult(event.getSessionId(), completableFuture);
	}

	public VesselDTO update(String id, DeleteVesselCommand cmd) {

		VesselAggregate agg = new VesselAggregate(vesselStateStore);
		agg.setAggregateId(id);

		// Se procesa el comando, obteniendo el evento generado
		CheckDeleteVesselEvent event = agg.process(cmd);

		// Si no se genera evento significa que no se va a aplicar
		if (event == null)
			return null;

		event.setUserId(userService.getUserId());

		// Si no existen excepciones, se aplica el comando
		agg.apply(event);

		// Crea la espera hasta que se responda con evento completado
		CompletableFuture<VesselDTO> completableFuture = getCompletableFeature(event.getSessionId(), agg.getVessel());

		// Emite evento para enviar a kafka
		publishToKafka(event, vesselTopic);

		// Obtiene el resultado cuando se resuelva la espera
		return getResult(event.getSessionId(), completableFuture);
	}

	@KafkaHandler
	private void listen(CreateVesselEnrichedEvent event) {

		publishToKafka(VesselEventFactory.getEvent(event, VesselEventTypes.CREATE, event.getVessel()), vesselTopic);
	}

	@KafkaHandler
	private void listen(VesselCreatedEvent event) {

		logger.debug("Vessel creado " + event.getAggregateId());

		// El evento Creado se envió desde el stream

		if (!event.getUserId().equals(REDMIC_PROCESS)) {
			resolveCommand(event.getSessionId());
		}
	}

	@KafkaHandler
	private void listen(UpdateVesselEnrichedEvent event) {

		publishToKafka(VesselEventFactory.getEvent(event, VesselEventTypes.UPDATE, event.getVessel()), vesselTopic);
	}

	@KafkaHandler
	private void listen(VesselUpdatedEvent event) {

		logger.debug("Vessel modificado " + event.getAggregateId());

		// Envía los editados satisfactoriamente para tenerlos en cuenta en el
		// postupdate
		// publishToKafka(event, vesselUpdatedTopic);

		// El evento Modificado se envió desde el stream

		if (!event.getUserId().equals(REDMIC_PROCESS)) {
			resolveCommand(event.getSessionId());
		}
	}

	@KafkaHandler
	private void listen(DeleteVesselCheckedEvent event) {

		publishToKafka(VesselEventFactory.getEvent(event, VesselEventTypes.DELETE), vesselTopic);
	}

	@KafkaHandler
	private void listen(DeleteVesselConfirmedEvent event) {

		publishToKafka(VesselEventFactory.getEvent(event, VesselEventTypes.DELETED), vesselTopic);
	}

	@KafkaHandler
	private void listen(VesselDeletedEvent event) {

		logger.debug("Vessel eliminado " + event.getAggregateId());

		if (!event.getUserId().equals(REDMIC_PROCESS)) {
			resolveCommand(event.getSessionId());
		}
	}

	@KafkaHandler
	private void listen(CreateVesselFailedEvent event) {

		publishToKafka(VesselEventFactory.getEvent(event, VesselEventTypes.CREATE_CANCELLED, event.getExceptionType(),
				event.getArguments()), vesselTopic);
	}

	@KafkaHandler
	private void listen(CreateVesselCancelledEvent event) {

		logger.debug("Error creando Vessel " + event.getAggregateId());

		if (!event.getUserId().equals(REDMIC_PROCESS)) {
			resolveCommand(event.getSessionId(),
					ExceptionFactory.getException(event.getExceptionType(), event.getArguments()));
		}
	}

	@KafkaHandler
	private void listen(UpdateVesselCancelledEvent event) {

		logger.debug("Error modificando Vessel " + event.getAggregateId());

		// El evento Cancelled se envía desde el stream

		if (!event.getUserId().equals(REDMIC_PROCESS)) {
			resolveCommand(event.getSessionId(),
					ExceptionFactory.getException(event.getExceptionType(), event.getArguments()));
		}
	}

	@KafkaHandler
	private void listen(DeleteVesselCheckFailedEvent event) {

		publishToKafka(VesselEventFactory.getEvent(event, VesselEventTypes.DELETE_CANCELLED, event.getExceptionType(),
				event.getArguments()), vesselTopic);
	}

	@KafkaHandler
	private void listen(DeleteVesselCancelledEvent event) {

		logger.debug("Error eliminando Vessel " + event.getAggregateId());

		// El evento Cancelled se envía desde el stream

		if (!event.getUserId().equals(REDMIC_PROCESS)) {
			resolveCommand(event.getSessionId(),
					ExceptionFactory.getException(event.getExceptionType(), event.getArguments()));
		}
	}
}
