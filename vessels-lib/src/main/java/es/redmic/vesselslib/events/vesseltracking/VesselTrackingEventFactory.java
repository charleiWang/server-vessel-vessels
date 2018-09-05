package es.redmic.vesselslib.events.vesseltracking;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import es.redmic.brokerlib.avro.common.Event;
import es.redmic.brokerlib.avro.common.EventError;
import es.redmic.exception.common.ExceptionType;
import es.redmic.exception.common.InternalException;
import es.redmic.vesselslib.dto.tracking.VesselTrackingDTO;
import es.redmic.vesselslib.events.vessel.common.VesselEvent;
import es.redmic.vesselslib.events.vesseltracking.common.VesselTrackingCancelledEvent;
import es.redmic.vesselslib.events.vesseltracking.common.VesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.create.CreateVesselTrackingCancelledEvent;
import es.redmic.vesselslib.events.vesseltracking.create.CreateVesselTrackingEnrichedEvent;
import es.redmic.vesselslib.events.vesseltracking.create.CreateVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.create.CreateVesselTrackingFailedEvent;
import es.redmic.vesselslib.events.vesseltracking.create.VesselTrackingCreatedEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.DeleteVesselTrackingCancelledEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.DeleteVesselTrackingCheckFailedEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.DeleteVesselTrackingCheckedEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.DeleteVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.DeleteVesselTrackingFailedEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.VesselTrackingDeletedEvent;
import es.redmic.vesselslib.events.vesseltracking.partialupdate.vessel.UpdateVesselInVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.update.UpdateVesselTrackingCancelledEvent;
import es.redmic.vesselslib.events.vesseltracking.update.UpdateVesselTrackingEnrichedEvent;
import es.redmic.vesselslib.events.vesseltracking.update.UpdateVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.update.UpdateVesselTrackingFailedEvent;
import es.redmic.vesselslib.events.vesseltracking.update.VesselTrackingUpdatedEvent;

public class VesselTrackingEventFactory {

	private static Logger logger = LogManager.getLogger();

	public static Event getEvent(Event source, String type) {

		if (type.equals(VesselTrackingEventTypes.DELETE)) {

			logger.info("Creando evento DeleteVesselTrackingEvent para: " + source.getAggregateId());

			return new DeleteVesselTrackingEvent().buildFrom(source);
		}

		if (type.equals(VesselTrackingEventTypes.DELETE_CHECKED)) {

			logger.info("Creando evento DeleteVesselTrackingCheckedEvent para: " + source.getAggregateId());

			return new DeleteVesselTrackingCheckedEvent().buildFrom(source);
		}

		if (type.equals(VesselTrackingEventTypes.DELETED)) {

			logger.info("Creando evento VesselTrackingDeletedEvent para: " + source.getAggregateId());

			return new VesselTrackingDeletedEvent().buildFrom(source);
		}

		logger.error("Tipo de evento no soportado");
		throw new InternalException(ExceptionType.INTERNAL_EXCEPTION);
	}

	//////////////

	public static Event getEvent(Event source, String type, VesselTrackingDTO vesselTracking) {

		VesselTrackingEvent successfulEvent = null;

		if (type.equals(VesselTrackingEventTypes.CREATE_ENRICHED)) {

			logger.info("Creando evento CreateVesselTrackingEnrichedEvent para: " + source.getAggregateId());

			successfulEvent = new CreateVesselTrackingEnrichedEvent().buildFrom(source);
		}

		if (type.equals(VesselTrackingEventTypes.UPDATE_ENRICHED)) {

			logger.info("Creando evento UpdateVesselTrackingEnrichedEvent para: " + source.getAggregateId());

			successfulEvent = new UpdateVesselTrackingEnrichedEvent().buildFrom(source);
		}

		if (type.equals(VesselTrackingEventTypes.CREATE)) {

			logger.info("Creando evento CreateVesselTrackingEvent para: " + source.getAggregateId());
			successfulEvent = new CreateVesselTrackingEvent().buildFrom(source);
		}

		if (type.equals(VesselTrackingEventTypes.UPDATE)) {

			logger.info("Creando evento UpdateVesselTrackingEvent para: " + source.getAggregateId());
			successfulEvent = new UpdateVesselTrackingEvent().buildFrom(source);
		}

		if (type.equals(VesselTrackingEventTypes.CREATED)) {

			logger.info("Creando evento VesselTrackingCreatedEvent para: " + source.getAggregateId());
			successfulEvent = new VesselTrackingCreatedEvent().buildFrom(source);
		}

		if (type.equals(VesselTrackingEventTypes.UPDATED)) {

			logger.info("Creando evento VesselTrackingUpdatedEvent para: " + source.getAggregateId());
			successfulEvent = new VesselTrackingUpdatedEvent().buildFrom(source);
		}

		if (successfulEvent != null) {
			successfulEvent.setVesselTracking(vesselTracking);
			return successfulEvent;
		} else {
			logger.error("Tipo de evento no soportado");
			throw new InternalException(ExceptionType.INTERNAL_EXCEPTION);
		}
	}

	//////////////

	public static Event getEvent(Event source, Event trigger, String type) {

		if (type.equals(VesselTrackingEventTypes.UPDATE_VESSEL)) {

			logger.info("Creando evento UpdateVesselInVesselTrackingEvent para: " + source.getAggregateId());

			UpdateVesselInVesselTrackingEvent requestEvent = new UpdateVesselInVesselTrackingEvent();
			requestEvent.setAggregateId(source.getAggregateId());
			requestEvent.setUserId(trigger.getUserId());
			requestEvent.setVersion(source.getVersion() + 1);
			requestEvent.setVessel(((VesselEvent) trigger).getVessel());
			return requestEvent;
		}

		logger.error("Tipo de evento no soportado");
		throw new InternalException(ExceptionType.INTERNAL_EXCEPTION);
	}

	/////////////////

	public static Event getEvent(Event source, String type, String exceptionType,
			Map<String, String> exceptionArguments) {

		EventError failedEvent = null;

		if (type.equals(VesselTrackingEventTypes.CREATE_FAILED)) {

			logger.info("No se pudo crear VesselTracking en la vista");
			failedEvent = new CreateVesselTrackingFailedEvent().buildFrom(source);
		}

		if (type.equals(VesselTrackingEventTypes.UPDATE_FAILED)) {

			logger.info("No se pudo modificar VesselTracking en la vista");
			failedEvent = new UpdateVesselTrackingFailedEvent().buildFrom(source);
		}

		if (type.equals(VesselTrackingEventTypes.DELETE_FAILED)) {

			logger.info("No se pudo eliminar VesselTracking de la vista");
			failedEvent = new DeleteVesselTrackingFailedEvent().buildFrom(source);
		}

		if (type.equals(VesselTrackingEventTypes.DELETE_CHECK_FAILED)) {

			logger.info("Checkeo de eliminación fallido, el item está referenciado");
			failedEvent = new DeleteVesselTrackingCheckFailedEvent().buildFrom(source);
		}

		if (type.equals(VesselTrackingEventTypes.CREATE_CANCELLED)) {

			logger.info("Enviando evento CreateVesselTrackingCancelledEvent para: " + source.getAggregateId());
			failedEvent = new CreateVesselTrackingCancelledEvent().buildFrom(source);
		}

		if (failedEvent != null) {

			failedEvent.setExceptionType(exceptionType);
			failedEvent.setArguments(exceptionArguments);
			return failedEvent;

		} else {
			logger.error("Tipo de evento no soportado");
			throw new InternalException(ExceptionType.INTERNAL_EXCEPTION);
		}
	}

	////////////////////

	public static Event getEvent(Event source, String type, VesselTrackingDTO vesselTracking, String exceptionType,
			Map<String, String> exceptionArguments) {

		VesselTrackingCancelledEvent cancelledEvent = null;

		if (type.equals(VesselTrackingEventTypes.UPDATE_CANCELLED)) {

			logger.info("Creando evento UpdateVesselTrackingCancelledEvent para: " + source.getAggregateId());
			cancelledEvent = new UpdateVesselTrackingCancelledEvent().buildFrom(source);
		}

		if (type.equals(VesselTrackingEventTypes.DELETE_CANCELLED)) {

			logger.info("Creando evento DeleteVesselTrackingCancelledEvent para: " + source.getAggregateId());
			cancelledEvent = new DeleteVesselTrackingCancelledEvent().buildFrom(source);
		}

		if (cancelledEvent != null) {

			cancelledEvent.setVesselTracking(vesselTracking);
			cancelledEvent.setExceptionType(exceptionType);
			cancelledEvent.setArguments(exceptionArguments);
			return cancelledEvent;

		} else {

			logger.error("Tipo de evento no soportado");
			throw new InternalException(ExceptionType.INTERNAL_EXCEPTION);
		}
	}
}
