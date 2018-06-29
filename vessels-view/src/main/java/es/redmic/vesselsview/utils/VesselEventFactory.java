package es.redmic.vesselsview.utils;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import es.redmic.brokerlib.avro.common.Event;
import es.redmic.vesselslib.events.vessel.create.CreateVesselFailedEvent;
import es.redmic.vesselslib.events.vessel.delete.DeleteVesselFailedEvent;
import es.redmic.vesselslib.events.vessel.update.UpdateVesselFailedEvent;

public abstract class VesselEventFactory {

	private static Logger logger = LogManager.getLogger();

	public static CreateVesselFailedEvent getCreateVesselFailedEvent(Event event, String exceptionType,
			Map<String, String> exceptionArguments) {

		logger.info("No se pudo crear Vessel en la vista");
		CreateVesselFailedEvent failedEvent = new CreateVesselFailedEvent().buildFrom(event);
		failedEvent.setExceptionType(exceptionType);
		failedEvent.setArguments(exceptionArguments);

		return failedEvent;
	}

	public static UpdateVesselFailedEvent getUpdateVesselFailedEvent(Event event, String exceptionType,
			Map<String, String> exceptionArguments) {

		logger.info("No se pudo modificar Vessel en la vista");
		UpdateVesselFailedEvent failedEvent = new UpdateVesselFailedEvent().buildFrom(event);
		failedEvent.setExceptionType(exceptionType);
		failedEvent.setArguments(exceptionArguments);

		return failedEvent;
	}

	public static DeleteVesselFailedEvent getDeleteVesselFailedEvent(Event event, String exceptionType,
			Map<String, String> exceptionArguments) {

		logger.info("No se pudo eliminar Vessel de la vista");
		DeleteVesselFailedEvent failedEvent = new DeleteVesselFailedEvent().buildFrom(event);
		failedEvent.setExceptionType(exceptionType);
		failedEvent.setArguments(exceptionArguments);

		return failedEvent;
	}
}
