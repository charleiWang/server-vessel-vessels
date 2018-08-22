package es.redmic.vesselscommands.streams;

import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;

import es.redmic.brokerlib.alert.AlertService;
import es.redmic.brokerlib.avro.common.Event;
import es.redmic.brokerlib.avro.common.EventError;
import es.redmic.commandslib.streaming.common.StreamConfig;
import es.redmic.commandslib.streaming.streams.EventSourcingStreams;
import es.redmic.vesselslib.dto.VesselTypeDTO;
import es.redmic.vesselslib.events.vesseltype.VesselTypeEventTypes;
import es.redmic.vesselslib.events.vesseltype.common.VesselTypeEvent;
import es.redmic.vesselslib.events.vesseltype.create.VesselTypeCreatedEvent;
import es.redmic.vesselslib.events.vesseltype.delete.DeleteVesselTypeCancelledEvent;
import es.redmic.vesselslib.events.vesseltype.update.UpdateVesselTypeCancelledEvent;
import es.redmic.vesselslib.events.vesseltype.update.VesselTypeUpdatedEvent;

public class VesselTypeEventStreams extends EventSourcingStreams {

	public VesselTypeEventStreams(StreamConfig config, AlertService alertService) {
		super(config, alertService);
		logger.info("Arrancado servicio de streaming para event sourcing de VesselType con Id: " + this.serviceId);
		init();
	}

	@Override
	protected void processCreatedStream(KStream<String, Event> vesselTypeEvents) {

		// Stream filtrado por eventos de confirmación al crear
		KStream<String, Event> createConfirmedEvents = vesselTypeEvents
				.filter((id, event) -> (VesselTypeEventTypes.CREATE_CONFIRMED.toString().equals(event.getType())));

		// Stream filtrado por eventos de petición de crear
		KStream<String, Event> createRequestEvents = vesselTypeEvents
				.filter((id, event) -> (VesselTypeEventTypes.CREATE.toString().equals(event.getType())));

		// Join por id, mandando a kafka el evento de éxito
		createConfirmedEvents.join(createRequestEvents,
				(confirmedEvent, requestEvent) -> getCreatedEvent(confirmedEvent, requestEvent),
				JoinWindows.of(windowsTime)).to(topic);
	}

	private Event getCreatedEvent(Event confirmedEvent, Event requestEvent) {

		assert requestEvent.getType().equals(VesselTypeEventTypes.CREATE);

		assert confirmedEvent.getType().equals(VesselTypeEventTypes.CREATE_CONFIRMED);

		if (!isSameSession(confirmedEvent, requestEvent)) {
			return null;
		}

		VesselTypeDTO vesselType = ((VesselTypeEvent) requestEvent).getVesselType();

		logger.info("Enviando evento VesselTypeCreatedEvent para: " + confirmedEvent.getAggregateId());

		VesselTypeCreatedEvent successfulEvent = new VesselTypeCreatedEvent().buildFrom(confirmedEvent);
		successfulEvent.setVesselType(vesselType);
		return successfulEvent;
	}

	@Override
	protected void processUpdatedStream(KStream<String, Event> vesselTypeEvents) {

		// Stream filtrado por eventos de confirmación al modificar
		KStream<String, Event> updateConfirmedEvents = vesselTypeEvents
				.filter((id, event) -> (VesselTypeEventTypes.UPDATE_CONFIRMED.toString().equals(event.getType())));

		// Stream filtrado por eventos de petición de modificar
		KStream<String, Event> updateRequestEvents = vesselTypeEvents
				.filter((id, event) -> (VesselTypeEventTypes.UPDATE.toString().equals(event.getType())));

		// Join por id, mandando a kafka el evento de éxito
		updateConfirmedEvents.join(updateRequestEvents,
				(confirmedEvent, requestEvent) -> getUpdatedEvent(confirmedEvent, requestEvent),
				JoinWindows.of(windowsTime)).to(topic);
	}

	private Event getUpdatedEvent(Event confirmedEvent, Event requestEvent) {

		assert requestEvent.getType().equals(VesselTypeEventTypes.UPDATE);

		assert confirmedEvent.getType().equals(VesselTypeEventTypes.UPDATE_CONFIRMED);

		if (!isSameSession(confirmedEvent, requestEvent)) {
			return null;
		}

		logger.debug("Creando evento de modificado exitoso para VesselType");

		VesselTypeDTO vesselType = ((VesselTypeEvent) requestEvent).getVesselType();

		logger.info("Enviando evento VesselTypeUpdatedEvent para: " + confirmedEvent.getAggregateId());

		VesselTypeUpdatedEvent successfulEvent = new VesselTypeUpdatedEvent().buildFrom(confirmedEvent);
		successfulEvent.setVesselType(vesselType);
		return successfulEvent;
	}

	@Override
	protected void processFailedChangeStream(KStream<String, Event> vesselTypeEvents) {

		// Stream filtrado por eventos de creaciones y modificaciones correctos (solo el
		// último que se produzca por id)
		KStream<String, Event> successEvents = vesselTypeEvents
				.filter((id, event) -> (VesselTypeEventTypes.CREATED.toString().equals(event.getType())
						|| VesselTypeEventTypes.UPDATED.toString().equals(event.getType())));

		processUpdateFailedStream(vesselTypeEvents, successEvents);

		processDeleteFailedStream(vesselTypeEvents, successEvents);

	}

	protected void processUpdateFailedStream(KStream<String, Event> vesselTypeEvents,
			KStream<String, Event> successEvents) {

		// Stream filtrado por eventos de fallo al modificar
		KStream<String, Event> failedEvents = vesselTypeEvents
				.filter((id, event) -> (VesselTypeEventTypes.UPDATE_FAILED.toString().equals(event.getType())));

		KTable<String, Event> successEventsTable = successEvents.groupByKey().reduce((aggValue, newValue) -> newValue);

		// Join por id, mandando a kafka el evento de compensación
		failedEvents
				.join(successEventsTable,
						(failedEvent, lastSuccessEvent) -> getUpdateCancelledEvent(failedEvent, lastSuccessEvent))
				.to(topic);
	}

	protected void processDeleteFailedStream(KStream<String, Event> vesselTypeEvents,
			KStream<String, Event> successEvents) {

		// Stream filtrado por eventos de fallo al borrar
		KStream<String, Event> failedEvents = vesselTypeEvents
				.filter((id, event) -> (VesselTypeEventTypes.DELETE_FAILED.toString().equals(event.getType())));

		KTable<String, Event> successEventsTable = successEvents.groupByKey().reduce((aggValue, newValue) -> newValue);

		// Join por id, mandando a kafka el evento de compensación
		failedEvents
				.join(successEventsTable,
						(failedEvent, lastSuccessEvent) -> getDeleteCancelledEvent(failedEvent, lastSuccessEvent))
				.to(topic);
	}

	private Event getUpdateCancelledEvent(Event failedEvent, Event lastSuccessEvent) {

		assert failedEvent.getType().equals(VesselTypeEventTypes.UPDATE_FAILED);

		assert lastSuccessEvent.getType().equals(VesselTypeEventTypes.CREATED)
				|| lastSuccessEvent.getType().equals(VesselTypeEventTypes.UPDATED);

		VesselTypeDTO vesselType = ((VesselTypeEvent) lastSuccessEvent).getVesselType();

		EventError eventError = (EventError) failedEvent;

		logger.info("Enviando evento UpdateVesselTypeCancelledEvent para: " + failedEvent.getAggregateId());

		UpdateVesselTypeCancelledEvent cancelledEvent = new UpdateVesselTypeCancelledEvent().buildFrom(failedEvent);
		cancelledEvent.setVesselType(vesselType);
		cancelledEvent.setExceptionType(eventError.getExceptionType());
		cancelledEvent.setArguments(eventError.getArguments());
		return cancelledEvent;
	}

	private Event getDeleteCancelledEvent(Event failedEvent, Event lastSuccessEvent) {

		assert failedEvent.getType().equals(VesselTypeEventTypes.DELETE_FAILED);

		assert lastSuccessEvent.getType().equals(VesselTypeEventTypes.CREATED)
				|| lastSuccessEvent.getType().equals(VesselTypeEventTypes.UPDATED);

		VesselTypeDTO vesselType = ((VesselTypeEvent) lastSuccessEvent).getVesselType();

		EventError eventError = (EventError) failedEvent;

		logger.info("Enviar evento DeleteVesselTypeCancelledEvent para: " + failedEvent.getAggregateId());

		DeleteVesselTypeCancelledEvent cancelledEvent = new DeleteVesselTypeCancelledEvent().buildFrom(failedEvent);
		cancelledEvent.setVesselType(vesselType);
		cancelledEvent.setExceptionType(eventError.getExceptionType());
		cancelledEvent.setArguments(eventError.getArguments());
		return cancelledEvent;
	}

	@Override
	protected void processPostUpdateStream(KStream<String, Event> events) {
	}
}
