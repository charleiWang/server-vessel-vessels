package es.redmic.vesselslib.events.vesseltype;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import es.redmic.brokerlib.avro.common.Event;
import es.redmic.vesselslib.events.vesseltype.create.CreateVesselTypeFailedEvent;
import es.redmic.vesselslib.events.vesseltype.delete.DeleteVesselTypeFailedEvent;
import es.redmic.vesselslib.events.vesseltype.update.UpdateVesselTypeFailedEvent;

public class VesselTypeEventFactory {

	private static Logger logger = LogManager.getLogger();

	public static Event getEvent(Event source, String type, String exceptionType,
			Map<String, String> exceptionArguments) {

		if (type.equals(VesselTypeEventTypes.CREATE_FAILED)) {

			logger.info("No se pudo crear Vessel type en la vista");
			CreateVesselTypeFailedEvent failedEvent = new CreateVesselTypeFailedEvent().buildFrom(source);
			failedEvent.setExceptionType(exceptionType);
			failedEvent.setArguments(exceptionArguments);

			return failedEvent;
		}
		if (type.equals(VesselTypeEventTypes.UPDATE_FAILED)) {

			logger.info("No se pudo modificar Vessel type en la vista");
			UpdateVesselTypeFailedEvent failedEvent = new UpdateVesselTypeFailedEvent().buildFrom(source);
			failedEvent.setExceptionType(exceptionType);
			failedEvent.setArguments(exceptionArguments);

			return failedEvent;
		}
		if (type.equals(VesselTypeEventTypes.DELETE_FAILED)) {

			logger.info("No se pudo eliminar Vessel type de la vista");
			DeleteVesselTypeFailedEvent failedEvent = new DeleteVesselTypeFailedEvent().buildFrom(source);
			failedEvent.setExceptionType(exceptionType);
			failedEvent.setArguments(exceptionArguments);

			return failedEvent;
		}
		return null;
	}

	public static Event getEvent(Event source, String type) {
		return null;
	}

	/*-public static CreateVesselTypeFailedEvent getCreateVesselTypeFailedEvent(Event event, String exceptionType,
			Map<String, String> exceptionArguments) {
	
		logger.info("No se pudo crear Vessel type en la vista");
		CreateVesselTypeFailedEvent failedEvent = new CreateVesselTypeFailedEvent().buildFrom(event);
		failedEvent.setExceptionType(exceptionType);
		failedEvent.setArguments(exceptionArguments);
	
		return failedEvent;
	}
	
	public static UpdateVesselTypeFailedEvent getUpdateVesselTypeFailedEvent(Event event, String exceptionType,
			Map<String, String> exceptionArguments) {
	
		logger.info("No se pudo modificar Vessel type en la vista");
		UpdateVesselTypeFailedEvent failedEvent = new UpdateVesselTypeFailedEvent().buildFrom(event);
		failedEvent.setExceptionType(exceptionType);
		failedEvent.setArguments(exceptionArguments);
	
		return failedEvent;
	}
	
	public static DeleteVesselTypeFailedEvent getDeleteVesselTypeFailedEvent(Event event, String exceptionType,
			Map<String, String> exceptionArguments) {
	
		logger.info("No se pudo eliminar Vessel type de la vista");
		DeleteVesselTypeFailedEvent failedEvent = new DeleteVesselTypeFailedEvent().buildFrom(event);
		failedEvent.setExceptionType(exceptionType);
		failedEvent.setArguments(exceptionArguments);
	
		return failedEvent;
	}-*/
}
