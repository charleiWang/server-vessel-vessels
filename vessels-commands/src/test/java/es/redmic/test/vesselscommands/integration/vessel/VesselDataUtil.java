package es.redmic.test.vesselscommands.integration.vessel;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.joda.time.DateTime;

import es.redmic.exception.common.ExceptionType;
import es.redmic.test.vesselscommands.integration.vesseltype.VesselTypeDataUtil;
import es.redmic.vesselslib.dto.vessel.VesselDTO;
import es.redmic.vesselslib.dto.vesseltype.VesselTypeDTO;
import es.redmic.vesselslib.events.vessel.VesselEventTypes;
import es.redmic.vesselslib.events.vessel.create.CreateVesselConfirmedEvent;
import es.redmic.vesselslib.events.vessel.create.CreateVesselEvent;
import es.redmic.vesselslib.events.vessel.create.CreateVesselFailedEvent;
import es.redmic.vesselslib.events.vessel.create.VesselCreatedEvent;
import es.redmic.vesselslib.events.vessel.delete.DeleteVesselConfirmedEvent;
import es.redmic.vesselslib.events.vessel.delete.DeleteVesselEvent;
import es.redmic.vesselslib.events.vessel.delete.DeleteVesselFailedEvent;
import es.redmic.vesselslib.events.vessel.delete.VesselDeletedEvent;
import es.redmic.vesselslib.events.vessel.update.UpdateVesselConfirmedEvent;
import es.redmic.vesselslib.events.vessel.update.UpdateVesselEvent;
import es.redmic.vesselslib.events.vessel.update.UpdateVesselFailedEvent;
import es.redmic.vesselslib.events.vessel.update.VesselUpdatedEvent;

public abstract class VesselDataUtil {

	public final static String PREFIX = "vessel-mmsi-", USER = "1";

	public static CreateVesselEvent getCreateEvent(Integer mmsi) {

		CreateVesselEvent event = new CreateVesselEvent();
		event.setAggregateId(PREFIX + mmsi);
		event.setDate(DateTime.now());
		event.setId(UUID.randomUUID().toString());
		event.setType(VesselEventTypes.CREATE);
		event.setVersion(1);
		event.setUserId(USER);
		event.setSessionId("sessionIdA");
		event.setVessel(getVessel(mmsi));

		return event;
	}

	public static CreateVesselConfirmedEvent getCreateVesselConfirmedEvent(Integer mmsi) {

		return new CreateVesselConfirmedEvent().buildFrom(getCreateEvent(mmsi));
	}

	public static VesselCreatedEvent getVesselCreatedEvent(Integer mmsi) {

		VesselCreatedEvent vesselCreatedEvent = new VesselCreatedEvent().buildFrom(getCreateEvent(mmsi));

		vesselCreatedEvent.setVessel(getVessel(mmsi));

		return vesselCreatedEvent;
	}

	public static CreateVesselFailedEvent getCreateVesselFailedEvent(Integer mmsi) {

		CreateVesselFailedEvent createVesselFailedEvent = new CreateVesselFailedEvent().buildFrom(getCreateEvent(mmsi));

		createVesselFailedEvent.setExceptionType(ExceptionType.ITEM_ALREADY_EXIST_EXCEPTION.name());
		Map<String, String> arguments = new HashMap<>();
		arguments.put("A", "B");
		createVesselFailedEvent.setArguments(arguments);

		return createVesselFailedEvent;
	}

	public static UpdateVesselEvent getUpdateEvent(Integer mmsi) {

		UpdateVesselEvent event = new UpdateVesselEvent();
		event.setAggregateId(PREFIX + mmsi);
		event.setDate(DateTime.now());
		event.setId(UUID.randomUUID().toString());
		event.setType(VesselEventTypes.UPDATE);
		event.setVersion(2);
		event.setUserId(USER);
		event.setSessionId("sessionIdB");
		event.setVessel(getVessel(mmsi));

		return event;
	}

	public static UpdateVesselConfirmedEvent getUpdateVesselConfirmedEvent(Integer mmsi) {

		return new UpdateVesselConfirmedEvent().buildFrom(getUpdateEvent(mmsi));
	}

	public static VesselUpdatedEvent getVesselUpdatedEvent(Integer mmsi) {

		VesselUpdatedEvent vesselUpdatedEvent = new VesselUpdatedEvent().buildFrom(getUpdateEvent(mmsi));

		vesselUpdatedEvent.setVessel(getVessel(mmsi));

		return vesselUpdatedEvent;
	}

	public static UpdateVesselFailedEvent getUpdateVesselFailedEvent(Integer mmsi) {

		UpdateVesselFailedEvent updateVesselFailedEvent = new UpdateVesselFailedEvent().buildFrom(getUpdateEvent(mmsi));

		updateVesselFailedEvent.setExceptionType(ExceptionType.ITEM_NOT_FOUND.name());
		Map<String, String> arguments = new HashMap<>();
		arguments.put("A", "B");
		updateVesselFailedEvent.setArguments(arguments);

		return updateVesselFailedEvent;
	}

	public static DeleteVesselEvent getDeleteEvent(Integer mmsi) {

		DeleteVesselEvent event = new DeleteVesselEvent();
		event.setAggregateId(PREFIX + mmsi);
		event.setDate(DateTime.now());
		event.setId(UUID.randomUUID().toString());
		event.setType(VesselEventTypes.DELETE);
		event.setVersion(3);
		event.setUserId(USER);
		event.setSessionId("sessionIdC");
		return event;
	}

	public static DeleteVesselConfirmedEvent getDeleteVesselConfirmedEvent(Integer mmsi) {

		return new DeleteVesselConfirmedEvent().buildFrom(getDeleteEvent(mmsi));
	}

	public static VesselDeletedEvent getVesselDeletedEvent(Integer mmsi) {

		return new VesselDeletedEvent().buildFrom(getDeleteEvent(mmsi));
	}

	public static DeleteVesselFailedEvent getDeleteVesselFailedEvent(Integer mmsi) {

		DeleteVesselFailedEvent deleteVesselFailedEvent = new DeleteVesselFailedEvent().buildFrom(getDeleteEvent(mmsi));

		deleteVesselFailedEvent.setExceptionType(ExceptionType.DELETE_ITEM_EXCEPTION.name());
		Map<String, String> arguments = new HashMap<>();
		// arguments.put("A", "B");
		deleteVesselFailedEvent.setArguments(arguments);

		return deleteVesselFailedEvent;
	}

	public static VesselDTO getVessel(Integer mmsi) {

		VesselDTO vessel = new VesselDTO();
		vessel.setId(PREFIX + mmsi);
		vessel.setMmsi(mmsi);
		vessel.setName("Avatar");
		vessel.setImo(1234);
		vessel.setBeam(30.2);
		vessel.setLength(230.5);
		vessel.setCallSign("23e2");

		VesselTypeDTO vesselType = new VesselTypeDTO();
		vesselType.setCode("70");
		vesselType.setId(VesselTypeDataUtil.PREFIX + "70");
		vesselType.setName("Cargo, all ships of this type");
		vesselType.setName_en("Cargo, all ships of this type");
		vessel.setType(vesselType);

		return vessel;
	}

}
