package es.redmic.test.vesselscommands.integration.vesseltype;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.joda.time.DateTime;

import es.redmic.exception.common.ExceptionType;
import es.redmic.vesselslib.dto.vesseltype.VesselTypeDTO;
import es.redmic.vesselslib.events.vesseltype.VesselTypeEventTypes;
import es.redmic.vesselslib.events.vesseltype.create.CreateVesselTypeConfirmedEvent;
import es.redmic.vesselslib.events.vesseltype.create.CreateVesselTypeEvent;
import es.redmic.vesselslib.events.vesseltype.create.CreateVesselTypeFailedEvent;
import es.redmic.vesselslib.events.vesseltype.create.VesselTypeCreatedEvent;
import es.redmic.vesselslib.events.vesseltype.delete.DeleteVesselTypeConfirmedEvent;
import es.redmic.vesselslib.events.vesseltype.delete.DeleteVesselTypeEvent;
import es.redmic.vesselslib.events.vesseltype.delete.DeleteVesselTypeFailedEvent;
import es.redmic.vesselslib.events.vesseltype.delete.VesselTypeDeletedEvent;
import es.redmic.vesselslib.events.vesseltype.update.UpdateVesselTypeConfirmedEvent;
import es.redmic.vesselslib.events.vesseltype.update.UpdateVesselTypeEvent;
import es.redmic.vesselslib.events.vesseltype.update.UpdateVesselTypeFailedEvent;
import es.redmic.vesselslib.events.vesseltype.update.VesselTypeUpdatedEvent;

public abstract class VesselTypeDataUtil {

	public final static String PREFIX = "vesseltype-code-", USER = "REDMIC_PROCESS";

	public static CreateVesselTypeEvent getCreateEvent(String code) {

		CreateVesselTypeEvent event = new CreateVesselTypeEvent();
		event.setAggregateId(PREFIX + code);
		event.setDate(DateTime.now());
		event.setId(UUID.randomUUID().toString());
		event.setType(VesselTypeEventTypes.CREATE);
		event.setVersion(1);
		event.setUserId(USER);
		event.setSessionId("sessionIdA");
		event.setVesselType(getVesselType(code));

		return event;
	}

	public static CreateVesselTypeConfirmedEvent getCreateVesselTypeConfirmedEvent(String code) {

		return new CreateVesselTypeConfirmedEvent().buildFrom(getCreateEvent(code));
	}

	public static VesselTypeCreatedEvent getVesselTypeCreatedEvent(String code) {

		VesselTypeCreatedEvent event = new VesselTypeCreatedEvent().buildFrom(getCreateEvent(code));

		event.setVesselType(getVesselType(code));

		return event;
	}

	public static CreateVesselTypeFailedEvent getCreateVesselTypeFailedEvent(String code) {

		CreateVesselTypeFailedEvent createVesselTypeFailedEvent = new CreateVesselTypeFailedEvent()
				.buildFrom(getCreateEvent(code));

		createVesselTypeFailedEvent.setExceptionType(ExceptionType.ITEM_ALREADY_EXIST_EXCEPTION.name());
		Map<String, String> arguments = new HashMap<>();
		arguments.put("A", "B");
		createVesselTypeFailedEvent.setArguments(arguments);

		return createVesselTypeFailedEvent;
	}

	public static UpdateVesselTypeEvent getUpdateEvent(String code) {

		UpdateVesselTypeEvent event = new UpdateVesselTypeEvent();
		event.setAggregateId(PREFIX + code);
		event.setDate(DateTime.now());
		event.setId(UUID.randomUUID().toString());
		event.setType(VesselTypeEventTypes.UPDATE);
		event.setVersion(2);
		event.setUserId(USER);
		event.setSessionId("sessionIdB");
		event.setVesselType(getVesselType(code));

		return event;
	}

	public static UpdateVesselTypeConfirmedEvent getUpdateVesselTypeConfirmedEvent(String code) {

		return new UpdateVesselTypeConfirmedEvent().buildFrom(getUpdateEvent(code));
	}

	public static VesselTypeUpdatedEvent getVesselTypeUpdatedEvent(String code) {

		VesselTypeUpdatedEvent vesselTypeUpdatedEvent = new VesselTypeUpdatedEvent().buildFrom(getUpdateEvent(code));

		vesselTypeUpdatedEvent.setVesselType(getVesselType(code));

		return vesselTypeUpdatedEvent;
	}

	public static UpdateVesselTypeFailedEvent getUpdateVesselTypeFailedEvent(String code) {

		UpdateVesselTypeFailedEvent updateVesselTypeFailedEvent = new UpdateVesselTypeFailedEvent()
				.buildFrom(getUpdateEvent(code));

		updateVesselTypeFailedEvent.setExceptionType(ExceptionType.ITEM_NOT_FOUND.name());
		Map<String, String> arguments = new HashMap<>();
		arguments.put("A", "B");
		updateVesselTypeFailedEvent.setArguments(arguments);

		return updateVesselTypeFailedEvent;
	}

	public static DeleteVesselTypeEvent getDeleteEvent(String code) {

		DeleteVesselTypeEvent event = new DeleteVesselTypeEvent();
		event.setAggregateId(PREFIX + code);
		event.setDate(DateTime.now());
		event.setId(UUID.randomUUID().toString());
		event.setType(VesselTypeEventTypes.DELETE);
		event.setVersion(3);
		event.setUserId(USER);
		event.setSessionId("sessionIdC");
		return event;
	}

	public static DeleteVesselTypeConfirmedEvent getDeleteVesselTypeConfirmedEvent(String code) {

		return new DeleteVesselTypeConfirmedEvent().buildFrom(getDeleteEvent(code));
	}

	public static VesselTypeDeletedEvent getVesselTypeDeletedEvent(String code) {

		return new VesselTypeDeletedEvent().buildFrom(getDeleteEvent(code));
	}

	public static DeleteVesselTypeFailedEvent getDeleteVesselTypeFailedEvent(String code) {

		DeleteVesselTypeFailedEvent deleteVesselTypeFailedEvent = new DeleteVesselTypeFailedEvent()
				.buildFrom(getDeleteEvent(code));

		deleteVesselTypeFailedEvent.setExceptionType(ExceptionType.DELETE_ITEM_EXCEPTION.name());
		Map<String, String> arguments = new HashMap<>();
		// arguments.put("A", "B");
		deleteVesselTypeFailedEvent.setArguments(arguments);

		return deleteVesselTypeFailedEvent;
	}

	public static VesselTypeDTO getVesselType(String code) {

		VesselTypeDTO vesselType = new VesselTypeDTO();
		vesselType.setCode(code);
		vesselType.setId(PREFIX + code);
		vesselType.setName("Cargo, all ships of this type");
		vesselType.setName_en("Cargo, all ships of this type");

		return vesselType;
	}

}
