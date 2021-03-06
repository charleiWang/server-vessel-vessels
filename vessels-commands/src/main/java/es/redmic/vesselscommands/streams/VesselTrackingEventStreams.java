package es.redmic.vesselscommands.streams;

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

import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;

import es.redmic.brokerlib.alert.AlertService;
import es.redmic.brokerlib.avro.common.Event;
import es.redmic.brokerlib.avro.common.EventError;
import es.redmic.brokerlib.avro.common.EventTypes;
import es.redmic.commandslib.streaming.common.StreamConfig;
import es.redmic.commandslib.streaming.streams.EventSourcingStreams;
import es.redmic.vesselscommands.commands.vesseltracking.CreateVesselTrackingCommand;
import es.redmic.vesselslib.dto.tracking.VesselTrackingDTO;
import es.redmic.vesselslib.events.vessel.VesselEventTypes;
import es.redmic.vesselslib.events.vessel.common.VesselEvent;
import es.redmic.vesselslib.events.vesseltracking.VesselTrackingEventFactory;
import es.redmic.vesselslib.events.vesseltracking.VesselTrackingEventTypes;
import es.redmic.vesselslib.events.vesseltracking.common.VesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.create.CreateVesselTrackingEnrichedEvent;
import es.redmic.vesselslib.events.vesseltracking.create.EnrichCreateVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.update.UpdateVesselTrackingEnrichedEvent;

public class VesselTrackingEventStreams extends EventSourcingStreams {

	private String vesselTopic;

	private String realtimeTrackingVesselsTopic;

	private GlobalKTable<String, Event> vessel;

	private KStream<String, VesselTrackingDTO> realtimeTracking;

	private final String REDMIC_PROCESS = "REDMIC_PROCESS";

	public VesselTrackingEventStreams(StreamConfig config, String vesselTopic, String realtimeTrackingVesselsTopic,
			AlertService alertService) {
		super(config, alertService);
		this.vesselTopic = vesselTopic + snapshotTopicSuffix;
		this.realtimeTrackingVesselsTopic = realtimeTrackingVesselsTopic;
		init();
	}

	/**
	 * 
	 * @see es.redmic.commandslib.streaming.streams.EventSourcingStreams#
	 *      createExtraStreams()
	 */
	@Override
	protected void createExtraStreams() {

		vessel = builder.globalTable(vesselTopic);

		realtimeTracking = builder.stream(realtimeTrackingVesselsTopic);
	}

	/**
	 * Reenvía eventos finales a topic de snapshot
	 */
	@Override
	protected void forwardSnapshotEvents(KStream<String, Event> events) {

		events.filter((id, event) -> (VesselTrackingEventTypes.isSnapshot(event.getType()))).to(snapshotTopic);
	}

	/**
	 * Función que a partir de los eventos de tipo CreateEnrich y globalKTable de
	 * las relaciones, enriquece el item antes de mandarlo a crear
	 * 
	 */

	@Override
	protected void processEnrichCreateSteam(KStream<String, Event> events) {

		KStream<String, Event> enrichCreateEvents = events
				.filter((id, event) -> (EventTypes.ENRICH_CREATE.equals(event.getType())))
				.selectKey((k, v) -> getVesselIdFromVesselTracking(v));

		enrichCreateEvents
				.leftJoin(vessel, (k, v) -> k,
						(enrichCreateEvent, vesselEvent) -> getEnrichCreateResultEvent(enrichCreateEvent, vesselEvent))
				.selectKey((k, v) -> v.getAggregateId()).to(topic);
	}

	private Event getEnrichCreateResultEvent(Event enrichCreateEvents, Event vesselEvent) {

		CreateVesselTrackingEnrichedEvent event = (CreateVesselTrackingEnrichedEvent) VesselTrackingEventFactory
				.getEvent(enrichCreateEvents, VesselTrackingEventTypes.CREATE_ENRICHED,
						((VesselTrackingEvent) enrichCreateEvents).getVesselTracking());

		if (vesselEvent != null && !vesselEvent.getType().equals(VesselEventTypes.DELETED)) {
			((VesselTrackingEvent) event).getVesselTracking().getProperties()
					.setVessel(((VesselEvent) vesselEvent).getVessel());
		} else {

			String error = "Intentando enriquecer " + enrichCreateEvents.getAggregateId()
					+ " con un elemento que no existe";

			logger.warn(error);
			// alertService.warnAlert("No se puedo enriquecer " +
			// enrichCreateEvents.getAggregateId(), error);
		}

		return event;
	}

	/**
	 * Función que a partir del evento de confirmación de la vista y del evento
	 * create (petición de creación), si todo es correcto, genera evento created
	 */

	@Override
	protected Event getCreatedEvent(Event confirmedEvent, Event requestEvent) {

		assert requestEvent.getType().equals(VesselTrackingEventTypes.CREATE);

		assert confirmedEvent.getType().equals(VesselTrackingEventTypes.CREATE_CONFIRMED);

		if (!isSameSession(confirmedEvent, requestEvent)) {
			return null;
		}

		VesselTrackingDTO vesselTracking = ((VesselTrackingEvent) requestEvent).getVesselTracking();

		return VesselTrackingEventFactory.getEvent(confirmedEvent, VesselTrackingEventTypes.CREATED, vesselTracking);
	}

	/**
	 * Función que a partir de los eventos de tipo UpdateEnrich y globalKTable de
	 * las relaciones, enriquece el item antes de mandarlo a modificar
	 * 
	 */

	@Override
	protected void processEnrichUpdateSteam(KStream<String, Event> events) {

		KStream<String, Event> enrichUpdateEvents = events
				.filter((id, event) -> (EventTypes.ENRICH_UPDATE.equals(event.getType())))
				.selectKey((k, v) -> getVesselIdFromVesselTracking(v));

		enrichUpdateEvents.leftJoin(vessel, (k, v) -> k,
				(enrichUpdateEvent, vesselTypeEvent) -> getEnrichUpdateResultEvent(enrichUpdateEvent, vesselTypeEvent))
				.selectKey((k, v) -> v.getAggregateId()).to(topic);
	}

	private Event getEnrichUpdateResultEvent(Event enrichUpdateEvents, Event vesselEvent) {

		UpdateVesselTrackingEnrichedEvent event = (UpdateVesselTrackingEnrichedEvent) VesselTrackingEventFactory
				.getEvent(enrichUpdateEvents, VesselTrackingEventTypes.UPDATE_ENRICHED,
						((VesselTrackingEvent) enrichUpdateEvents).getVesselTracking());

		if (vesselEvent != null && !vesselEvent.getType().equals(VesselEventTypes.DELETED)) {
			((VesselTrackingEvent) event).getVesselTracking().getProperties()
					.setVessel(((VesselEvent) vesselEvent).getVessel());
		} else {

			String error = "Intentando enriquecer " + enrichUpdateEvents.getAggregateId()
					+ " con un elemento que no existe";

			logger.warn(error);
			// alertService.warnAlert("No se puedo enriquecer " +
			// enrichUpdateEvents.getAggregateId(), error);
		}

		return event;
	}

	/**
	 * Función que a partir del evento de confirmación de la vista y del evento
	 * update (petición de modificación), si todo es correcto, genera evento updated
	 */

	@Override
	protected Event getUpdatedEvent(Event confirmedEvent, Event requestEvent) {

		assert requestEvent.getType().equals(VesselTrackingEventTypes.UPDATE);

		assert confirmedEvent.getType().equals(VesselTrackingEventTypes.UPDATE_CONFIRMED);

		if (!isSameSession(confirmedEvent, requestEvent)) {
			return null;
		}

		VesselTrackingDTO vesselTracking = ((VesselTrackingEvent) requestEvent).getVesselTracking();

		return VesselTrackingEventFactory.getEvent(confirmedEvent, VesselTrackingEventTypes.UPDATED, vesselTracking);
	}

	/**
	 * Comprueba si vessel tracking está referenciado en otro servicio
	 */
	@Override
	protected void processDeleteStream(KStream<String, Event> events) {
	}

	@Override
	protected void processPartialUpdatedStream(KStream<String, Event> vesselTrackingEvents,
			KStream<String, Event> updateConfirmedEvents) {

		// En este caso no existe modificación parcial de vesselTracking vía stream
	}

	/**
	 * Función que a partir del evento fallido y el último evento correcto, genera
	 * evento UpdateCancelled
	 */

	@Override
	protected Event getUpdateCancelledEvent(Event failedEvent, Event lastSuccessEvent) {

		assert VesselTrackingEventTypes.isSnapshot(lastSuccessEvent.getType());

		assert failedEvent.getType().equals(VesselTrackingEventTypes.UPDATE_FAILED);

		VesselTrackingDTO vesselTracking = ((VesselTrackingEvent) lastSuccessEvent).getVesselTracking();

		EventError eventError = (EventError) failedEvent;

		alertService.errorAlert("UpdateVesselTrackingCancelledEvent para: " + failedEvent.getAggregateId(),
				eventError.getExceptionType() + " " + eventError.getArguments());

		return VesselTrackingEventFactory.getEvent(failedEvent, VesselTrackingEventTypes.UPDATE_CANCELLED,
				vesselTracking, eventError.getExceptionType(), eventError.getArguments());
	}

	/**
	 * Función que a partir del evento fallido y el último evento correcto, genera
	 * evento DeleteFailed
	 */

	@Override
	protected Event getDeleteCancelledEvent(Event failedEvent, Event lastSuccessEvent) {

		assert VesselTrackingEventTypes.isSnapshot(lastSuccessEvent.getType());

		assert failedEvent.getType().equals(VesselTrackingEventTypes.DELETE_FAILED);

		VesselTrackingDTO vesselTracking = ((VesselTrackingEvent) lastSuccessEvent).getVesselTracking();

		EventError eventError = (EventError) failedEvent;

		return VesselTrackingEventFactory.getEvent(failedEvent, VesselTrackingEventTypes.DELETE_CANCELLED,
				vesselTracking, eventError.getExceptionType(), eventError.getArguments());
	}

	public static String getVesselIdFromVesselTracking(Event evt) {

		return ((VesselTrackingEvent) evt).getVesselTracking().getProperties().getVessel().getId();
	}

	@Override
	protected void processPostUpdateStream(KStream<String, Event> events) {
		// En series temporales no se hace postUpdate
	}

	@Override
	protected void processExtraStreams(KStream<String, Event> events, KStream<String, Event> snapshotEvents) {

		createTrackingFromRealtimeTrackingVessel(realtimeTracking, events);
	}

	private void createTrackingFromRealtimeTrackingVessel(KStream<String, VesselTrackingDTO> realTimeTracking,
			KStream<String, Event> events) {

		KTable<String, Event> table = events.groupByKey().reduce((aggValue, newValue) -> newValue);

		realTimeTracking.leftJoin(table,
				(vesselTrackingDTO, vesselTrackingEvent) -> getCreateTrackingFromRealtimeTrackingVessel(
						vesselTrackingDTO, vesselTrackingEvent))
				.filter((k, v) -> (v != null)).to(topic);
	}

	private Event getCreateTrackingFromRealtimeTrackingVessel(VesselTrackingDTO vesselTrackingDTO,
			Event vesselTrackingEvent) {

		if (vesselTrackingEvent == null) {

			vesselTrackingDTO = new CreateVesselTrackingCommand(vesselTrackingDTO).getVesselTracking();

			EnrichCreateVesselTrackingEvent evt = new EnrichCreateVesselTrackingEvent(vesselTrackingDTO);
			evt.setAggregateId(vesselTrackingDTO.getId());
			evt.setVersion(1);
			evt.setUserId(REDMIC_PROCESS);
			return evt;
		}

		logger.info("Descartando tracking ya que existe un evento de tipo " + vesselTrackingEvent.getType()
				+ " para este id " + vesselTrackingEvent.getAggregateId());
		return null;
	}
}
