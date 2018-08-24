package es.redmic.vesselscommands.streams;

import org.apache.kafka.streams.kstream.KStream;

import es.redmic.brokerlib.alert.AlertService;
import es.redmic.brokerlib.avro.common.Event;
import es.redmic.brokerlib.avro.common.EventError;
import es.redmic.commandslib.streaming.common.StreamConfig;
import es.redmic.commandslib.streaming.streams.EventSourcingStreams;
import es.redmic.vesselslib.dto.vesseltype.VesselTypeDTO;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.redmic.commandslib.streaming.streams.EventSourcingStreams#
	 * createExtraStreams()
	 */

	@Override
	protected void createExtraStreams() {
		// No existen streams extra necesarios

	}

	/*
	 * Función que apartir del evento de confirmación de la vista y del evento
	 * create (petición de creación), si todo es correcto, genera evento created
	 */

	@Override
	protected Event getCreatedEvent(Event confirmedEvent, Event requestEvent) {

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

	/*
	 * Función que apartir del evento de confirmación de la vista y del evento
	 * update (petición de modificación), si todo es correcto, genera evento updated
	 */

	@Override
	protected Event getUpdatedEvent(Event confirmedEvent, Event requestEvent) {

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

	/*
	 * Función que a partir del último evento correcto + el evento de edición
	 * parcial + la confirmación de la vista, si todo es correcto, genera evento
	 * updated
	 */

	@Override
	protected void processPartialUpdatedStream(KStream<String, Event> vesselTypeEvents,
			KStream<String, Event> updateConfirmedEvents) {
	}

	/*
	 * Función que a partir del evento fallido y el último evento correcto, genera
	 * evento UpdateCancelled
	 */

	@Override
	protected Event getUpdateCancelledEvent(Event failedEvent, Event lastSuccessEvent) {

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

	/*
	 * Función que a partir del evento fallido y el último evento correcto, genera
	 * evento DeleteFailed
	 */

	@Override
	protected Event getDeleteCancelledEvent(Event failedEvent, Event lastSuccessEvent) {

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

	/*
	 * Función para procesar modificaciones de referencias
	 */

	@Override
	protected void processPostUpdateStream(KStream<String, Event> events) {

		// En este caso no hay modificación de relaciones
	}
}
