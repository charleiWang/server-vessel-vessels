package es.redmic.vesselscommands.streams;

import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;

import es.redmic.brokerlib.alert.AlertService;
import es.redmic.brokerlib.avro.common.Event;
import es.redmic.brokerlib.avro.common.EventError;
import es.redmic.brokerlib.avro.common.EventTypes;
import es.redmic.commandslib.streaming.common.StreamConfig;
import es.redmic.commandslib.streaming.streams.EventSourcingStreams;
import es.redmic.vesselslib.dto.tracking.VesselTrackingDTO;
import es.redmic.vesselslib.events.vessel.VesselEventTypes;
import es.redmic.vesselslib.events.vessel.common.VesselEvent;
import es.redmic.vesselslib.events.vesseltracking.VesselTrackingEventFactory;
import es.redmic.vesselslib.events.vesseltracking.VesselTrackingEventTypes;
import es.redmic.vesselslib.events.vesseltracking.common.VesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.create.CreateVesselTrackingEnrichedEvent;
import es.redmic.vesselslib.events.vesseltracking.partialupdate.vessel.UpdateVesselInVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.update.UpdateVesselTrackingEnrichedEvent;

public class VesselTrackingEventStreams extends EventSourcingStreams {

	private String vesselTopic;

	private GlobalKTable<String, Event> vessel;

	public VesselTrackingEventStreams(StreamConfig config, String vesselTopic, AlertService alertService) {
		super(config, alertService);
		this.vesselTopic = vesselTopic + snapshotTopicSuffix;

		logger.info("Arrancado servicio de streaming para event sourcing de Vessel tracking con Id: " + this.serviceId);
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

	/**
	 * Función que a partir del último evento correcto + el evento de edición
	 * parcial + la confirmación de la vista, envía evento modificado.
	 */
	@Override
	protected void processPartialUpdatedStream(KStream<String, Event> vesselTrackingEvents,
			KStream<String, Event> updateConfirmedEvents) {

		// Stream filtrado por eventos de petición de modificar vessel en vesselTracking
		KStream<String, Event> updateRequestEvents = vesselTrackingEvents
				.filter((id, event) -> (VesselTrackingEventTypes.UPDATE_VESSEL.equals(event.getType())));

		// Join por id, mandando a kafka el evento de éxito
		KStream<String, UpdateVesselInVesselTrackingEvent> partialUpdateEvent = updateConfirmedEvents.join(
				updateRequestEvents,
				(confirmedEvent, requestEvent) -> isSameSession(confirmedEvent, requestEvent)
						? (UpdateVesselInVesselTrackingEvent) requestEvent
						: null,
				JoinWindows.of(windowsTime));

		// Stream filtrado por eventos de creaciones y modificaciones correctos (solo el
		// último que se produzca por id)
		KStream<String, Event> successEvents = vesselTrackingEvents
				.filter((id, event) -> (VesselTrackingEventTypes.CREATED.equals(event.getType())
						|| VesselTrackingEventTypes.UPDATED.equals(event.getType())));

		KTable<String, Event> successEventsTable = successEvents.groupByKey().reduce((aggValue, newValue) -> newValue);

		// Join por id, mandando a kafka el evento de compensación
		partialUpdateEvent.join(successEventsTable, (partialUpdateConfirmEvent,
				lastSuccessEvent) -> getUpdatedEventFromPartialUpdate(partialUpdateConfirmEvent, lastSuccessEvent))
				.to(topic);
	}

	/**
	 * Función que a partir del último evento correcto + el evento de edición
	 * parcial + la confirmación de la vista, si todo es correcto, genera evento
	 * updated
	 */

	private Event getUpdatedEventFromPartialUpdate(UpdateVesselInVesselTrackingEvent partialUpdateConfirmEvent,
			Event lastSuccessEvent) {

		assert VesselTrackingEventTypes.isSnapshot(lastSuccessEvent.getType());

		assert partialUpdateConfirmEvent.getType().equals(VesselTrackingEventTypes.UPDATE_VESSEL);

		VesselTrackingDTO vesselTracking = ((VesselTrackingEvent) lastSuccessEvent).getVesselTracking();
		vesselTracking.getProperties().setVessel(partialUpdateConfirmEvent.getVessel());

		return VesselTrackingEventFactory.getEvent(partialUpdateConfirmEvent, VesselTrackingEventTypes.UPDATED,
				vesselTracking);
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
}
