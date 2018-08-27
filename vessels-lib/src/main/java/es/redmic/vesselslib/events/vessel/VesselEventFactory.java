package es.redmic.vesselslib.events.vessel;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import es.redmic.brokerlib.avro.common.Event;
import es.redmic.vesselslib.events.vessel.create.CreateVesselFailedEvent;
import es.redmic.vesselslib.events.vessel.delete.DeleteVesselFailedEvent;
import es.redmic.vesselslib.events.vessel.update.UpdateVesselFailedEvent;
import es.redmic.vesselslib.events.vesseltype.VesselTypeEventTypes;

public class VesselEventFactory {

	private static Logger logger = LogManager.getLogger();

	public static Event getEvent(Event source, String type, String exceptionType,
			Map<String, String> exceptionArguments) {

		if (type.equals(VesselTypeEventTypes.CREATE_FAILED)) {

			logger.info("No se pudo crear Vessel en la vista");
			CreateVesselFailedEvent failedEvent = new CreateVesselFailedEvent().buildFrom(source);
			failedEvent.setExceptionType(exceptionType);
			failedEvent.setArguments(exceptionArguments);

			return failedEvent;
		}
		if (type.equals(VesselTypeEventTypes.UPDATE_FAILED)) {

			logger.info("No se pudo modificar Vessel en la vista");
			UpdateVesselFailedEvent failedEvent = new UpdateVesselFailedEvent().buildFrom(source);
			failedEvent.setExceptionType(exceptionType);
			failedEvent.setArguments(exceptionArguments);

			return failedEvent;
		}
		if (type.equals(VesselTypeEventTypes.DELETE_FAILED)) {

			logger.info("No se pudo eliminar Vessel de la vista");
			DeleteVesselFailedEvent failedEvent = new DeleteVesselFailedEvent().buildFrom(source);
			failedEvent.setExceptionType(exceptionType);
			failedEvent.setArguments(exceptionArguments);

			return failedEvent;
		}
		return null;
	}

	public static Event getEvent(Event source, String type) {
		return null;
	}

	/*-public static CreateVesselFailedEvent getCreateVesselFailedEvent(Event event, String exceptionType,
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
	}-*/
}
