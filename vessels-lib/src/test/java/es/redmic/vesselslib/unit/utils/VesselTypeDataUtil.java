package es.redmic.vesselslib.unit.utils;

import java.util.HashMap;
import java.util.Map;

import es.redmic.vesselslib.dto.VesselTypeDTO;
import es.redmic.vesselslib.events.vesseltype.VesselTypeEventType;
import es.redmic.vesselslib.events.vesseltype.create.CreateVesselTypeCancelledEvent;
import es.redmic.vesselslib.events.vesseltype.create.CreateVesselTypeConfirmedEvent;
import es.redmic.vesselslib.events.vesseltype.create.CreateVesselTypeEvent;
import es.redmic.vesselslib.events.vesseltype.create.CreateVesselTypeFailedEvent;
import es.redmic.vesselslib.events.vesseltype.create.VesselTypeCreatedEvent;
import es.redmic.vesselslib.events.vesseltype.delete.DeleteVesselTypeCancelledEvent;
import es.redmic.vesselslib.events.vesseltype.delete.DeleteVesselTypeConfirmedEvent;
import es.redmic.vesselslib.events.vesseltype.delete.DeleteVesselTypeEvent;
import es.redmic.vesselslib.events.vesseltype.delete.DeleteVesselTypeFailedEvent;
import es.redmic.vesselslib.events.vesseltype.delete.VesselTypeDeletedEvent;
import es.redmic.vesselslib.events.vesseltype.update.UpdateVesselTypeCancelledEvent;
import es.redmic.vesselslib.events.vesseltype.update.UpdateVesselTypeConfirmedEvent;
import es.redmic.vesselslib.events.vesseltype.update.UpdateVesselTypeEvent;
import es.redmic.vesselslib.events.vesseltype.update.UpdateVesselTypeFailedEvent;
import es.redmic.vesselslib.events.vesseltype.update.VesselTypeUpdatedEvent;

public abstract class VesselTypeDataUtil {

	public final static String PREFIX = "vesseltype-code-", CODE = "70", USER = "1";

	// Create

	public static CreateVesselTypeEvent getCreateEvent() {

		CreateVesselTypeEvent event = new CreateVesselTypeEvent();
		event.setAggregateId(PREFIX + CODE);
		event.setType(VesselTypeEventType.CREATE_VESSELTYPE.name());
		event.setVersion(1);
		event.setUserId(USER);
		event.setVesselType(getVesselType());

		return event;
	}

	public static CreateVesselTypeConfirmedEvent getCreateVesselTypeConfirmedEvent() {

		CreateVesselTypeConfirmedEvent event = new CreateVesselTypeConfirmedEvent().buildFrom(getCreateEvent());
		event.setType(VesselTypeEventType.CREATE_VESSELTYPE_CONFIRMED.name());
		return event;
	}

	public static VesselTypeCreatedEvent getVesselTypeCreatedEvent() {

		VesselTypeCreatedEvent event = new VesselTypeCreatedEvent().buildFrom(getCreateEvent());
		event.setType(VesselTypeEventType.VESSELTYPE_CREATED.name());
		event.setVesselType(getVesselType());
		return event;
	}

	public static CreateVesselTypeFailedEvent getCreateVesselTypeFailedEvent() {

		CreateVesselTypeFailedEvent event = new CreateVesselTypeFailedEvent().buildFrom(getCreateEvent());
		event.setType(VesselTypeEventType.CREATE_VESSELTYPE_FAILED.name());
		event.setExceptionType("ItemAlreadyExist");
		return event;
	}

	public static CreateVesselTypeCancelledEvent getCreateVesselTypeCancelledEvent() {

		CreateVesselTypeCancelledEvent event = new CreateVesselTypeCancelledEvent().buildFrom(getCreateEvent());
		event.setType(VesselTypeEventType.CREATE_VESSELTYPE_CANCELLED.name());
		event.setExceptionType("ItemAlreadyExist");
		return event;
	}

	// Update

	public static UpdateVesselTypeEvent getUpdateEvent() {

		UpdateVesselTypeEvent event = new UpdateVesselTypeEvent();
		event.setAggregateId(PREFIX + CODE);
		event.setType(VesselTypeEventType.UPDATE_VESSELTYPE.name());
		event.setVersion(2);
		event.setUserId(USER);
		event.setVesselType(getVesselType());
		return event;
	}

	public static UpdateVesselTypeConfirmedEvent getUpdateVesselTypeConfirmedEvent() {

		UpdateVesselTypeConfirmedEvent event = new UpdateVesselTypeConfirmedEvent().buildFrom(getUpdateEvent());
		event.setType(VesselTypeEventType.UPDATE_VESSELTYPE_CONFIRMED.name());
		return event;
	}

	public static VesselTypeUpdatedEvent getVesselTypeUpdatedEvent() {

		VesselTypeUpdatedEvent event = new VesselTypeUpdatedEvent().buildFrom(getUpdateEvent());
		event.setType(VesselTypeEventType.VESSELTYPE_UPDATED.name());
		event.setVesselType(getVesselType());
		return event;
	}

	public static UpdateVesselTypeFailedEvent getUpdateVesselTypeFailedEvent() {

		UpdateVesselTypeFailedEvent event = new UpdateVesselTypeFailedEvent().buildFrom(getUpdateEvent());
		event.setType(VesselTypeEventType.UPDATE_VESSELTYPE_FAILED.name());
		event.setExceptionType("ItemNotFound");
		Map<String, String> arguments = new HashMap<>();
		arguments.put("a", "b");
		event.setArguments(arguments);
		return event;
	}

	public static UpdateVesselTypeCancelledEvent getUpdateVesselTypeCancelledEvent() {

		UpdateVesselTypeCancelledEvent event = new UpdateVesselTypeCancelledEvent().buildFrom(getUpdateEvent());
		event.setType(VesselTypeEventType.UPDATE_VESSELTYPE_FAILED.name());
		event.setVesselType(getVesselType());
		event.setExceptionType("ItemNotFound");
		Map<String, String> arguments = new HashMap<>();
		arguments.put("a", "b");
		event.setArguments(arguments);
		return event;
	}

	// Delete

	public static DeleteVesselTypeEvent getDeleteEvent() {

		DeleteVesselTypeEvent event = new DeleteVesselTypeEvent();
		event.setAggregateId(PREFIX + CODE);
		event.setType(VesselTypeEventType.DELETE_VESSELTYPE.name());
		event.setVersion(3);
		event.setUserId(USER);
		return event;
	}

	public static DeleteVesselTypeConfirmedEvent getDeleteVesselTypeConfirmedEvent() {

		DeleteVesselTypeConfirmedEvent event = new DeleteVesselTypeConfirmedEvent().buildFrom(getDeleteEvent());
		event.setAggregateId(PREFIX + CODE);
		event.setType(VesselTypeEventType.DELETE_VESSELTYPE_CONFIRMED.name());
		event.setVersion(3);

		return event;
	}

	public static VesselTypeDeletedEvent getVesselTypeDeletedEvent() {

		VesselTypeDeletedEvent event = new VesselTypeDeletedEvent().buildFrom(getDeleteEvent());
		event.setType(VesselTypeEventType.VESSELTYPE_DELETED.name());
		return event;
	}

	public static DeleteVesselTypeFailedEvent getDeleteVesselTypeFailedEvent() {

		DeleteVesselTypeFailedEvent event = new DeleteVesselTypeFailedEvent().buildFrom(getDeleteEvent());
		event.setType(VesselTypeEventType.DELETE_VESSELTYPE_FAILED.name());
		event.setExceptionType("ItemNotFound");
		return event;
	}

	public static DeleteVesselTypeCancelledEvent getDeleteVesselTypeCancelledEvent() {

		DeleteVesselTypeCancelledEvent event = new DeleteVesselTypeCancelledEvent().buildFrom(getDeleteEvent());
		event.setType(VesselTypeEventType.DELETE_VESSELTYPE_CONFIRMED.name());
		event.setVesselType(getVesselType());
		event.setExceptionType("ItemNotFound");
		return event;
	}

	///////////////////

	public static VesselTypeDTO getVesselType() {

		VesselTypeDTO vesselType = new VesselTypeDTO();
		vesselType.setCode(CODE);
		vesselType.setId(PREFIX + CODE);
		vesselType.setName("Cargo, all ships of this type");
		vesselType.setName_en("Cargo, all ships of this type");

		return vesselType;
	}

}
