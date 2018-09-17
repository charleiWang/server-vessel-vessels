package es.redmic.vesselslib.events.vesseltype;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import es.redmic.brokerlib.avro.common.Event;
import es.redmic.brokerlib.avro.common.EventError;
import es.redmic.exception.common.ExceptionType;
import es.redmic.exception.common.InternalException;
import es.redmic.vesselslib.dto.vesseltype.VesselTypeDTO;
import es.redmic.vesselslib.events.vesseltype.common.VesselTypeCancelledEvent;
import es.redmic.vesselslib.events.vesseltype.common.VesselTypeEvent;
import es.redmic.vesselslib.events.vesseltype.create.CreateVesselTypeCancelledEvent;
import es.redmic.vesselslib.events.vesseltype.create.CreateVesselTypeConfirmedEvent;
import es.redmic.vesselslib.events.vesseltype.create.CreateVesselTypeFailedEvent;
import es.redmic.vesselslib.events.vesseltype.create.VesselTypeCreatedEvent;
import es.redmic.vesselslib.events.vesseltype.delete.DeleteVesselTypeCancelledEvent;
import es.redmic.vesselslib.events.vesseltype.delete.DeleteVesselTypeCheckFailedEvent;
import es.redmic.vesselslib.events.vesseltype.delete.DeleteVesselTypeCheckedEvent;
import es.redmic.vesselslib.events.vesseltype.delete.DeleteVesselTypeConfirmedEvent;
import es.redmic.vesselslib.events.vesseltype.delete.DeleteVesselTypeEvent;
import es.redmic.vesselslib.events.vesseltype.delete.DeleteVesselTypeFailedEvent;
import es.redmic.vesselslib.events.vesseltype.delete.VesselTypeDeletedEvent;
import es.redmic.vesselslib.events.vesseltype.update.UpdateVesselTypeCancelledEvent;
import es.redmic.vesselslib.events.vesseltype.update.UpdateVesselTypeConfirmedEvent;
import es.redmic.vesselslib.events.vesseltype.update.UpdateVesselTypeFailedEvent;
import es.redmic.vesselslib.events.vesseltype.update.VesselTypeUpdatedEvent;

public class VesselTypeEventFactory {

	private static Logger logger = LogManager.getLogger();

	public static Event getEvent(Event source, String type) {

		if (type.equals(VesselTypeEventTypes.DELETE)) {

			logger.info("Creando evento DeleteVesselTypeEvent para: " + source.getAggregateId());
			return new DeleteVesselTypeEvent().buildFrom(source);
		}

		if (type.equals(VesselTypeEventTypes.DELETE_CHECKED)) {

			logger.info("Creando evento DeleteVesselTypeCheckedEvent para: " + source.getAggregateId());
			return new DeleteVesselTypeCheckedEvent().buildFrom(source);
		}

		if (type.equals(VesselTypeEventTypes.CREATE_CONFIRMED)) {

			logger.info("Creando evento CreateVesselTypeConfirmedEvent para: " + source.getAggregateId());

			return new CreateVesselTypeConfirmedEvent().buildFrom(source);
		}

		if (type.equals(VesselTypeEventTypes.UPDATE_CONFIRMED)) {

			logger.info("Creando evento UpdateVesselTypeConfirmedEvent para: " + source.getAggregateId());

			return new UpdateVesselTypeConfirmedEvent().buildFrom(source);
		}

		if (type.equals(VesselTypeEventTypes.DELETE_CONFIRMED)) {

			logger.info("Creando evento DeleteVesselTypeConfirmedEvent para: " + source.getAggregateId());

			return new DeleteVesselTypeConfirmedEvent().buildFrom(source);
		}

		if (type.equals(VesselTypeEventTypes.DELETED)) {

			logger.info("Creando evento VesselTypeDeletedEvent para: " + source.getAggregateId());
			return new VesselTypeDeletedEvent().buildFrom(source);
		}

		logger.error("Tipo de evento no soportado");
		throw new InternalException(ExceptionType.INTERNAL_EXCEPTION);
	}

	public static Event getEvent(Event source, String type, VesselTypeDTO vesselType) {

		VesselTypeEvent successfulEvent = null;

		if (type.equals(VesselTypeEventTypes.CREATED)) {
			logger.info("Creando evento VesselTypeCreatedEvent para: " + source.getAggregateId());
			successfulEvent = new VesselTypeCreatedEvent().buildFrom(source);
		}

		if (type.equals(VesselTypeEventTypes.UPDATED)) {
			logger.info("Creando evento VesselTypeUpdatedEvent para: " + source.getAggregateId());
			successfulEvent = new VesselTypeUpdatedEvent().buildFrom(source);
		}

		if (successfulEvent != null) {
			successfulEvent.setVesselType(vesselType);
			return successfulEvent;
		} else {
			logger.error("Tipo de evento no soportado");
			throw new InternalException(ExceptionType.INTERNAL_EXCEPTION);
		}
	}

	public static Event getEvent(Event source, String type, String exceptionType,
			Map<String, String> exceptionArguments) {

		EventError failedEvent = null;

		if (type.equals(VesselTypeEventTypes.CREATE_FAILED)) {

			logger.info("No se pudo crear Vessel type en la vista");
			failedEvent = new CreateVesselTypeFailedEvent().buildFrom(source);
		}
		if (type.equals(VesselTypeEventTypes.UPDATE_FAILED)) {

			logger.info("No se pudo modificar Vessel type en la vista");
			failedEvent = new UpdateVesselTypeFailedEvent().buildFrom(source);
		}
		if (type.equals(VesselTypeEventTypes.DELETE_FAILED)) {

			logger.info("No se pudo eliminar Vessel type de la vista");
			failedEvent = new DeleteVesselTypeFailedEvent().buildFrom(source);
		}

		if (type.equals(VesselTypeEventTypes.DELETE_CHECK_FAILED)) {

			logger.info("Checkeo de eliminación fallido, el item está referenciado");
			failedEvent = new DeleteVesselTypeCheckFailedEvent().buildFrom(source);
		}

		if (type.equals(VesselTypeEventTypes.CREATE_CANCELLED)) {

			logger.info("Enviando evento CreateVesselTypeCancelledEvent para: " + source.getAggregateId());
			failedEvent = new CreateVesselTypeCancelledEvent().buildFrom(source);
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

	public static Event getEvent(Event source, String type, VesselTypeDTO vesselType, String exceptionType,
			Map<String, String> exceptionArguments) {

		VesselTypeCancelledEvent cancelledEvent = null;

		if (type.equals(VesselTypeEventTypes.UPDATE_CANCELLED)) {

			logger.info("Creando evento UpdateVesselTypeCancelledEvent para: " + source.getAggregateId());
			cancelledEvent = new UpdateVesselTypeCancelledEvent().buildFrom(source);
		}

		if (type.equals(VesselTypeEventTypes.DELETE_CANCELLED)) {

			logger.info("Creando evento DeleteVesselTypeCancelledEvent para: " + source.getAggregateId());
			cancelledEvent = new DeleteVesselTypeCancelledEvent().buildFrom(source);
		}

		if (cancelledEvent != null) {

			cancelledEvent.setVesselType(vesselType);
			cancelledEvent.setExceptionType(exceptionType);
			cancelledEvent.setArguments(exceptionArguments);
			return cancelledEvent;

		} else {

			logger.error("Tipo de evento no soportado");
			throw new InternalException(ExceptionType.INTERNAL_EXCEPTION);
		}
	}
}
