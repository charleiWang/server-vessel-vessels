package es.redmic.vesselslib.unit.utils;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;

import es.redmic.vesselslib.dto.vessel.VesselDTO;
import es.redmic.vesselslib.dto.vesseltype.VesselTypeDTO;
import es.redmic.vesselslib.events.vessel.VesselEventTypes;
import es.redmic.vesselslib.events.vessel.create.CreateVesselCancelledEvent;
import es.redmic.vesselslib.events.vessel.create.CreateVesselConfirmedEvent;
import es.redmic.vesselslib.events.vessel.create.CreateVesselEvent;
import es.redmic.vesselslib.events.vessel.create.CreateVesselFailedEvent;
import es.redmic.vesselslib.events.vessel.create.VesselCreatedEvent;
import es.redmic.vesselslib.events.vessel.delete.DeleteVesselCancelledEvent;
import es.redmic.vesselslib.events.vessel.delete.DeleteVesselConfirmedEvent;
import es.redmic.vesselslib.events.vessel.delete.DeleteVesselEvent;
import es.redmic.vesselslib.events.vessel.delete.DeleteVesselFailedEvent;
import es.redmic.vesselslib.events.vessel.delete.VesselDeletedEvent;
import es.redmic.vesselslib.events.vessel.partialupdate.vesseltype.UpdateVesselTypeInVesselEvent;
import es.redmic.vesselslib.events.vessel.update.UpdateVesselCancelledEvent;
import es.redmic.vesselslib.events.vessel.update.UpdateVesselConfirmedEvent;
import es.redmic.vesselslib.events.vessel.update.UpdateVesselEvent;
import es.redmic.vesselslib.events.vessel.update.UpdateVesselFailedEvent;
import es.redmic.vesselslib.events.vessel.update.VesselUpdatedEvent;

public abstract class VesselDataUtil {

	public final static String PREFIX = "vessel-mmsi-", MMSI = "1234", USER = "1";

	// create

	public static CreateVesselEvent getCreateEvent() {

		CreateVesselEvent event = new CreateVesselEvent();
		event.setAggregateId(PREFIX + MMSI);
		event.setType(VesselEventTypes.CREATE);
		event.setVersion(1);
		event.setUserId(USER);
		event.setVessel(getVessel());

		return event;
	}

	public static CreateVesselConfirmedEvent getCreateConfirmedEvent() {

		CreateVesselConfirmedEvent event = new CreateVesselConfirmedEvent().buildFrom(getCreateEvent());
		event.setType(VesselEventTypes.CREATE_CONFIRMED);
		return event;
	}

	public static VesselCreatedEvent getCreatedEvent() {

		VesselCreatedEvent event = new VesselCreatedEvent().buildFrom(getCreateEvent());
		event.setType(VesselEventTypes.CREATED);
		event.setVessel(getVessel());
		return event;
	}

	public static CreateVesselFailedEvent getCreateVesselFailedEvent() {

		CreateVesselFailedEvent event = new CreateVesselFailedEvent().buildFrom(getCreateEvent());
		event.setType(VesselEventTypes.CREATE_FAILED);
		event.setExceptionType("ItemAlreadyExist");
		return event;
	}

	public static CreateVesselCancelledEvent getCreateVesselCancelledEvent() {

		CreateVesselCancelledEvent event = new CreateVesselCancelledEvent().buildFrom(getCreateEvent());
		event.setType(VesselEventTypes.CREATE_CANCELLED);
		event.setExceptionType("ItemAlreadyExist");
		return event;
	}

	// update

	public static UpdateVesselEvent getUpdateEvent() {

		UpdateVesselEvent event = new UpdateVesselEvent();
		event.setAggregateId(PREFIX + MMSI);
		event.setType(VesselEventTypes.UPDATE);
		event.setVersion(2);
		event.setUserId(USER);
		event.setVessel(getVessel());

		return event;
	}

	public static UpdateVesselConfirmedEvent getUpdateVesselConfirmedEvent() {

		UpdateVesselConfirmedEvent event = new UpdateVesselConfirmedEvent().buildFrom(getUpdateEvent());
		event.setType(VesselEventTypes.UPDATE_CONFIRMED);
		return event;
	}

	public static VesselUpdatedEvent getVesselUpdatedEvent() {

		VesselUpdatedEvent event = new VesselUpdatedEvent().buildFrom(getUpdateEvent());
		event.setType(VesselEventTypes.UPDATED);
		event.setVessel(getVessel());
		return event;
	}

	public static UpdateVesselFailedEvent getUpdateVesselFailedEvent() {

		UpdateVesselFailedEvent event = new UpdateVesselFailedEvent().buildFrom(getUpdateEvent());
		event.setType(VesselEventTypes.UPDATE_FAILED);
		event.setExceptionType("ItemNotFound");
		Map<String, String> arguments = new HashMap<>();
		arguments.put("a", "b");
		event.setArguments(arguments);
		return event;
	}

	public static UpdateVesselCancelledEvent getUpdateVesselCancelledEvent() {

		UpdateVesselCancelledEvent event = new UpdateVesselCancelledEvent().buildFrom(getUpdateEvent());
		event.setType(VesselEventTypes.UPDATE_CANCELLED);
		event.setVessel(getVessel());
		event.setExceptionType("ItemNotFound");
		Map<String, String> arguments = new HashMap<>();
		arguments.put("a", "b");
		event.setArguments(arguments);
		return event;
	}

	// delete

	public static DeleteVesselEvent getDeleteEvent() {

		DeleteVesselEvent event = new DeleteVesselEvent();
		event.setAggregateId(PREFIX + MMSI);
		event.setType(VesselEventTypes.DELETE);
		event.setVersion(3);
		event.setUserId(USER);
		return event;
	}

	public static DeleteVesselConfirmedEvent getDeleteVesselConfirmedEvent() {

		DeleteVesselConfirmedEvent event = new DeleteVesselConfirmedEvent().buildFrom(getDeleteEvent());
		event.setType(VesselEventTypes.DELETE_CONFIRMED);
		return event;
	}

	public static VesselDeletedEvent getVesselDeletedEvent() {

		VesselDeletedEvent event = new VesselDeletedEvent().buildFrom(getDeleteEvent());
		event.setType(VesselEventTypes.DELETED);
		return event;
	}

	public static DeleteVesselFailedEvent getDeleteVesselFailedEvent() {

		DeleteVesselFailedEvent event = new DeleteVesselFailedEvent().buildFrom(getDeleteEvent());
		event.setType(VesselEventTypes.DELETE_FAILED);
		event.setExceptionType("ItemNotFound");
		return event;
	}

	public static DeleteVesselCancelledEvent getDeleteVesselCancelledEvent() {

		DeleteVesselCancelledEvent event = new DeleteVesselCancelledEvent().buildFrom(getDeleteEvent());
		event.setType(VesselEventTypes.DELETE_CANCELLED);
		event.setVessel(getVessel());
		event.setExceptionType("ItemNotFound");
		return event;
	}

	// UpdateVesselTypeInVessel

	public static UpdateVesselTypeInVesselEvent getUpdateVesselTypeInVesselEvent() {

		UpdateVesselTypeInVesselEvent event = new UpdateVesselTypeInVesselEvent();
		event.setAggregateId(PREFIX + MMSI);
		event.setType(VesselEventTypes.UPDATE_VESSELTYPE);
		event.setVersion(2);
		event.setUserId(USER);
		event.setVesselType(VesselTypeDataUtil.getVesselType());
		return event;
	}

	//////////////////////

	public static VesselDTO getVessel() {

		VesselDTO vessel = new VesselDTO();
		vessel.setId(PREFIX + MMSI);
		vessel.setMmsi(Integer.valueOf(MMSI));
		vessel.setName("Avatar");
		vessel.setImo(1234);
		vessel.setBeam(30.2);
		vessel.setLength(230.5);
		vessel.setCallSign("23e2");
		vessel.setInserted(DateTime.now());
		vessel.setUpdated(DateTime.now());

		VesselTypeDTO vesselType = new VesselTypeDTO();
		vesselType.setCode("70");
		vesselType.setId(VesselTypeDataUtil.PREFIX + "70");
		vesselType.setName("Cargo, all ships of this type");
		vesselType.setName_en("Cargo, all ships of this type");
		vessel.setType(vesselType);

		return vessel;
	}

}
