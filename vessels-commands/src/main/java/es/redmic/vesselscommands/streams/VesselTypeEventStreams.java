package es.redmic.vesselscommands.streams;

import java.util.concurrent.TimeUnit;

import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;

import es.redmic.brokerlib.alert.AlertService;
import es.redmic.brokerlib.avro.common.Event;
import es.redmic.brokerlib.avro.common.EventError;
import es.redmic.commandslib.statestore.StreamConfig;
import es.redmic.commandslib.streams.EventStreams;
import es.redmic.vesselslib.dto.VesselTypeDTO;
import es.redmic.vesselslib.events.vesseltype.VesselTypeEventType;
import es.redmic.vesselslib.events.vesseltype.common.VesselTypeEvent;
import es.redmic.vesselslib.events.vesseltype.create.VesselTypeCreatedEvent;
import es.redmic.vesselslib.events.vesseltype.delete.DeleteVesselTypeCancelledEvent;
import es.redmic.vesselslib.events.vesseltype.update.UpdateVesselTypeCancelledEvent;
import es.redmic.vesselslib.events.vesseltype.update.VesselTypeUpdatedEvent;

public class VesselTypeEventStreams extends EventStreams {

	private AlertService alertService;

	public VesselTypeEventStreams(StreamConfig config, AlertService alertService) {
		super(config);
		this.alertService = alertService;
		logger.info("Arrancado servicio de compensación de errores de edición de VesselType con Id: " + this.serviceId);
		init();
	}

	@Override
	protected void processCreatedStream(KStream<String, Event> vesselTypeEvents) {

		// Stream filtrado por eventos de confirmación al crear
		KStream<String, Event> createConfirmedEvents = vesselTypeEvents.filter(
				(id, event) -> (VesselTypeEventType.CREATE_VESSELTYPE_CONFIRMED.toString().equals(event.getType())));

		// Stream filtrado por eventos de petición de crear
		KStream<String, Event> createRequestEvents = vesselTypeEvents
				.filter((id, event) -> (VesselTypeEventType.CREATE_VESSELTYPE.toString().equals(event.getType())));

		// Join por id, mandando a kafka el evento de éxito
		createConfirmedEvents.join(createRequestEvents,
				(confirmedEvent, requestEvent) -> getCreatedEvent(confirmedEvent, requestEvent),
				JoinWindows.of(TimeUnit.SECONDS.toMillis(60))).to(topic);
	}

	private Event getCreatedEvent(Event confirmedEvent, Event requestEvent) {

		logger.debug("Creando evento de creado exitoso");

		if (!isSameSession(confirmedEvent, requestEvent)) {
			String message = "Recibido evento de petición con id de sessión diferente al evento de confirmación para item "
					+ confirmedEvent.getAggregateId();
			logger.error(message);
			alertService.errorAlert(confirmedEvent.getAggregateId(), message);
			return null;
		}

		if (!(requestEvent.getType().equals(VesselTypeEventType.CREATE_VESSELTYPE.name()))) {
			logger.error("Se esperaba un evento de petición de tipo CREATE para VesselType.");
			return null;
		}

		VesselTypeDTO vesselType = ((VesselTypeEvent) requestEvent).getVesselType();

		if (confirmedEvent.getType().equals(VesselTypeEventType.CREATE_VESSELTYPE_CONFIRMED.name())) {

			logger.info("Enviando evento VesselTypeCreatedEvent para: " + confirmedEvent.getAggregateId());

			VesselTypeCreatedEvent successfulEvent = new VesselTypeCreatedEvent().buildFrom(confirmedEvent);
			successfulEvent.setVesselType(vesselType);
			return successfulEvent;
		} else {
			logger.error("Se esperaba un evento de confirmación de tipo CREATE para VesselType.");
			return null;
		}
	}

	@Override
	protected void processUpdatedStream(KStream<String, Event> vesselTypeEvents) {

		// Stream filtrado por eventos de confirmación al modificar
		KStream<String, Event> updateConfirmedEvents = vesselTypeEvents.filter(
				(id, event) -> (VesselTypeEventType.UPDATE_VESSELTYPE_CONFIRMED.toString().equals(event.getType())));

		// Stream filtrado por eventos de petición de modificar
		KStream<String, Event> updateRequestEvents = vesselTypeEvents
				.filter((id, event) -> (VesselTypeEventType.UPDATE_VESSELTYPE.toString().equals(event.getType())));

		// Join por id, mandando a kafka el evento de éxito
		updateConfirmedEvents.join(updateRequestEvents,
				(confirmedEvent, requestEvent) -> getUpdatedEvent(confirmedEvent, requestEvent),
				JoinWindows.of(TimeUnit.SECONDS.toMillis(60))).to(topic);
	}

	private Event getUpdatedEvent(Event confirmedEvent, Event requestEvent) {

		logger.debug("Creando evento de modificado exitoso para VesselType");

		if (!isSameSession(confirmedEvent, requestEvent)) {
			String message = "Recibido evento de petición con id de sessión diferente al evento de confirmación para item "
					+ confirmedEvent.getAggregateId();
			logger.error(message);
			alertService.errorAlert(confirmedEvent.getAggregateId(), message);
			return null;
		}

		if (!(requestEvent.getType().equals(VesselTypeEventType.UPDATE_VESSELTYPE.name()))) {
			logger.error("Se esperaba un evento de petición de UPDATE para VesselType.");
			return null;
		}

		VesselTypeDTO vesselType = ((VesselTypeEvent) requestEvent).getVesselType();

		if (confirmedEvent.getType().equals(VesselTypeEventType.UPDATE_VESSELTYPE_CONFIRMED.name())) {

			logger.info("Enviando evento VesselTypeUpdatedEvent para: " + confirmedEvent.getAggregateId());

			VesselTypeUpdatedEvent successfulEvent = new VesselTypeUpdatedEvent().buildFrom(confirmedEvent);
			successfulEvent.setVesselType(vesselType);
			return successfulEvent;
		} else {
			logger.error("Se esperaba un evento de confirmación de tipo UPDATE para VesselType.");
			return null;
		}
	}

	@Override
	protected void processFailedChangeStream(KStream<String, Event> vesselTypeEvents) {

		// Stream filtrado por eventos de fallo al modificar y borrar
		KStream<String, Event> failedEvents = vesselTypeEvents
				.filter((id, event) -> (VesselTypeEventType.UPDATE_VESSELTYPE_FAILED.toString().equals(event.getType())
						|| VesselTypeEventType.DELETE_VESSELTYPE_FAILED.toString().equals(event.getType())));

		// Stream filtrado por eventos de creaciones y modificaciones correctos (solo el
		// último que se produzca por id)

		KStream<String, Event> successEvents = vesselTypeEvents
				.filter((id, event) -> (VesselTypeEventType.VESSELTYPE_CREATED.toString().equals(event.getType())
						|| VesselTypeEventType.VESSELTYPE_UPDATED.toString().equals(event.getType())));

		KTable<String, Event> successEventsTable = successEvents.groupByKey().reduce((aggValue, newValue) -> newValue);

		// Join por id, mandando a kafka el evento de compensación
		failedEvents.join(successEventsTable,
				(failedEvent, lastSuccessEvent) -> getCancelledEvent(failedEvent, lastSuccessEvent)).to(topic);
	}

	private Event getCancelledEvent(Event failedEvent, Event lastSuccessEvent) {

		logger.debug("Creando evento de cancelación");

		if (!(lastSuccessEvent.getType().equals(VesselTypeEventType.VESSELTYPE_CREATED.name())
				|| lastSuccessEvent.getType().equals(VesselTypeEventType.VESSELTYPE_UPDATED.name()))) {
			logger.error("Se esperaba un evento satisfactorio de tipo CREATED o UPDATED.");
			return null;
		}

		VesselTypeDTO vesselType = ((VesselTypeEvent) lastSuccessEvent).getVesselType();

		EventError eventError = (EventError) failedEvent;

		if (failedEvent.getType().equals(VesselTypeEventType.UPDATE_VESSELTYPE_FAILED.name())) {

			logger.info("Enviando evento UpdateVesselTypeCancelledEvent para: " + failedEvent.getAggregateId());

			UpdateVesselTypeCancelledEvent cancelledEvent = new UpdateVesselTypeCancelledEvent().buildFrom(failedEvent);
			cancelledEvent.setVesselType(vesselType);
			cancelledEvent.setExceptionType(eventError.getExceptionType());
			cancelledEvent.setArguments(eventError.getArguments());
			return cancelledEvent;

		} else if (failedEvent.getType().equals(VesselTypeEventType.DELETE_VESSELTYPE_FAILED.name())) {

			logger.info("Enviar evento DeleteVesselTypeCancelledEvent para: " + failedEvent.getAggregateId());

			DeleteVesselTypeCancelledEvent cancelledEvent = new DeleteVesselTypeCancelledEvent().buildFrom(failedEvent);
			cancelledEvent.setVesselType(vesselType);
			cancelledEvent.setExceptionType(eventError.getExceptionType());
			cancelledEvent.setArguments(eventError.getArguments());
			return cancelledEvent;
		} else {
			logger.error("Se esperaba un evento fallido de tipo UPDATE o DELETE para VesselType.");
			return null;
		}
	}

	@Override
	protected void processPostUpdateStream(KStream<String, Event> events) {
	}
}
