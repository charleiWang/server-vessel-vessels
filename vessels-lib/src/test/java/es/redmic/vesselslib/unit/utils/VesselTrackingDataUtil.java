package es.redmic.vesselslib.unit.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.geotools.geometry.jts.JTSFactoryFinder;
import org.joda.time.DateTime;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;

import es.redmic.vesselslib.dto.tracking.VesselTrackingDTO;
import es.redmic.vesselslib.dto.tracking.VesselTrackingPropertiesDTO;
import es.redmic.vesselslib.dto.vessel.VesselDTO;
import es.redmic.vesselslib.dto.vesseltype.VesselTypeDTO;
import es.redmic.vesselslib.events.vesseltracking.VesselTrackingEventTypes;
import es.redmic.vesselslib.events.vesseltracking.create.CreateVesselTrackingCancelledEvent;
import es.redmic.vesselslib.events.vesseltracking.create.CreateVesselTrackingConfirmedEvent;
import es.redmic.vesselslib.events.vesseltracking.create.CreateVesselTrackingEnrichedEvent;
import es.redmic.vesselslib.events.vesseltracking.create.CreateVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.create.CreateVesselTrackingFailedEvent;
import es.redmic.vesselslib.events.vesseltracking.create.EnrichCreateVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.create.VesselTrackingCreatedEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.CheckDeleteVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.DeleteVesselTrackingCancelledEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.DeleteVesselTrackingCheckFailedEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.DeleteVesselTrackingCheckedEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.DeleteVesselTrackingConfirmedEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.DeleteVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.DeleteVesselTrackingFailedEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.VesselTrackingDeletedEvent;
import es.redmic.vesselslib.events.vesseltracking.partialupdate.vessel.UpdateVesselInVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.update.EnrichUpdateVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.update.UpdateVesselTrackingCancelledEvent;
import es.redmic.vesselslib.events.vesseltracking.update.UpdateVesselTrackingConfirmedEvent;
import es.redmic.vesselslib.events.vesseltracking.update.UpdateVesselTrackingEnrichedEvent;
import es.redmic.vesselslib.events.vesseltracking.update.UpdateVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.update.UpdateVesselTrackingFailedEvent;
import es.redmic.vesselslib.events.vesseltracking.update.VesselTrackingUpdatedEvent;

public abstract class VesselTrackingDataUtil {

	public final static String PREFIX = "vesseltracking-mmsi-tstamp-", MMSI = "1234", USER = "1";
	private static final long TSTAMP = new DateTime().getMillis();

	// create

	public static CreateVesselTrackingEvent getCreateEvent() {

		CreateVesselTrackingEvent event = new CreateVesselTrackingEvent();
		event.setAggregateId(PREFIX + MMSI);
		event.setType(VesselTrackingEventTypes.CREATE);
		event.setVersion(1);
		event.setUserId(USER);
		event.setVesselTracking(getVesselTracking());

		return event;
	}

	public static EnrichCreateVesselTrackingEvent getEnrichCreateVesselTrackingEvent() {

		EnrichCreateVesselTrackingEvent event = new EnrichCreateVesselTrackingEvent().buildFrom(getCreateEvent());
		event.setVesselTracking(getVesselTracking());

		return event;
	}

	public static CreateVesselTrackingEnrichedEvent getCreateVesselTrackingEnrichedEvent() {

		CreateVesselTrackingEnrichedEvent event = new CreateVesselTrackingEnrichedEvent().buildFrom(getCreateEvent());
		event.setVesselTracking(getVesselTracking());
		return event;
	}

	public static CreateVesselTrackingConfirmedEvent getCreateConfirmedEvent() {

		CreateVesselTrackingConfirmedEvent event = new CreateVesselTrackingConfirmedEvent().buildFrom(getCreateEvent());
		event.setType(VesselTrackingEventTypes.CREATE_CONFIRMED);
		return event;
	}

	public static VesselTrackingCreatedEvent getCreatedEvent() {

		VesselTrackingCreatedEvent event = new VesselTrackingCreatedEvent().buildFrom(getCreateEvent());
		event.setType(VesselTrackingEventTypes.CREATED);
		event.setVesselTracking(getVesselTracking());
		return event;
	}

	public static CreateVesselTrackingFailedEvent getCreateVesselTrackingFailedEvent() {

		CreateVesselTrackingFailedEvent event = new CreateVesselTrackingFailedEvent().buildFrom(getCreateEvent());
		event.setType(VesselTrackingEventTypes.CREATE_FAILED);
		event.setExceptionType("ItemAlreadyExist");
		return event;
	}

	public static CreateVesselTrackingCancelledEvent getCreateVesselTrackingCancelledEvent() {

		CreateVesselTrackingCancelledEvent event = new CreateVesselTrackingCancelledEvent().buildFrom(getCreateEvent());
		event.setType(VesselTrackingEventTypes.CREATE_CANCELLED);
		event.setExceptionType("ItemAlreadyExist");
		return event;
	}

	// update

	public static UpdateVesselTrackingEvent getUpdateEvent() {

		UpdateVesselTrackingEvent event = new UpdateVesselTrackingEvent();
		event.setAggregateId(PREFIX + MMSI);
		event.setType(VesselTrackingEventTypes.UPDATE);
		event.setVersion(2);
		event.setUserId(USER);
		event.setVesselTracking(getVesselTracking());

		return event;
	}

	public static EnrichUpdateVesselTrackingEvent getEnrichUpdateVesselTrackingEvent() {

		EnrichUpdateVesselTrackingEvent event = new EnrichUpdateVesselTrackingEvent().buildFrom(getCreateEvent());
		event.setVesselTracking(getVesselTracking());

		return event;
	}

	public static UpdateVesselTrackingEnrichedEvent getUpdateVesselTrackingEnrichedEvent() {

		UpdateVesselTrackingEnrichedEvent event = new UpdateVesselTrackingEnrichedEvent().buildFrom(getUpdateEvent());
		event.setVesselTracking(getVesselTracking());
		return event;
	}

	public static UpdateVesselTrackingConfirmedEvent getUpdateVesselTrackingConfirmedEvent() {

		UpdateVesselTrackingConfirmedEvent event = new UpdateVesselTrackingConfirmedEvent().buildFrom(getUpdateEvent());
		event.setType(VesselTrackingEventTypes.UPDATE_CONFIRMED);
		return event;
	}

	public static VesselTrackingUpdatedEvent getVesselTrackingUpdatedEvent() {

		VesselTrackingUpdatedEvent event = new VesselTrackingUpdatedEvent().buildFrom(getUpdateEvent());
		event.setType(VesselTrackingEventTypes.UPDATED);
		event.setVesselTracking(getVesselTracking());
		return event;
	}

	public static UpdateVesselTrackingFailedEvent getUpdateVesselTrackingFailedEvent() {

		UpdateVesselTrackingFailedEvent event = new UpdateVesselTrackingFailedEvent().buildFrom(getUpdateEvent());
		event.setType(VesselTrackingEventTypes.UPDATE_FAILED);
		event.setExceptionType("ItemNotFound");
		Map<String, String> arguments = new HashMap<>();
		arguments.put("a", "b");
		event.setArguments(arguments);
		return event;
	}

	public static UpdateVesselTrackingCancelledEvent getUpdateVesselTrackingCancelledEvent() {

		UpdateVesselTrackingCancelledEvent event = new UpdateVesselTrackingCancelledEvent().buildFrom(getUpdateEvent());
		event.setType(VesselTrackingEventTypes.UPDATE_CANCELLED);
		event.setVesselTracking(getVesselTracking());
		event.setExceptionType("ItemNotFound");
		Map<String, String> arguments = new HashMap<>();
		arguments.put("a", "b");
		event.setArguments(arguments);
		return event;
	}

	// delete

	public static DeleteVesselTrackingEvent getDeleteEvent() {

		DeleteVesselTrackingEvent event = new DeleteVesselTrackingEvent();
		event.setAggregateId(PREFIX + MMSI);
		event.setType(VesselTrackingEventTypes.DELETE);
		event.setVersion(3);
		event.setUserId(USER);
		return event;
	}

	public static CheckDeleteVesselTrackingEvent getCheckDeleteVesselTrackingEvent() {

		return new CheckDeleteVesselTrackingEvent().buildFrom(getDeleteEvent());
	}

	public static DeleteVesselTrackingCheckedEvent getDeleteVesselTrackingCheckedEvent() {

		return new DeleteVesselTrackingCheckedEvent().buildFrom(getDeleteEvent());
	}

	public static DeleteVesselTrackingCheckFailedEvent getDeleteVesselTrackingCheckFailedEvent() {

		DeleteVesselTrackingCheckFailedEvent event = new DeleteVesselTrackingCheckFailedEvent()
				.buildFrom(getDeleteEvent());
		event.setExceptionType("ItemIsReferenced");
		Map<String, String> arguments = new HashMap<>();
		arguments.put("a", "b");
		event.setArguments(arguments);
		return event;
	}

	public static DeleteVesselTrackingConfirmedEvent getDeleteVesselTrackingConfirmedEvent() {

		DeleteVesselTrackingConfirmedEvent event = new DeleteVesselTrackingConfirmedEvent().buildFrom(getDeleteEvent());
		event.setType(VesselTrackingEventTypes.DELETE_CONFIRMED);
		return event;
	}

	public static VesselTrackingDeletedEvent getVesselTrackingDeletedEvent() {

		VesselTrackingDeletedEvent event = new VesselTrackingDeletedEvent().buildFrom(getDeleteEvent());
		event.setType(VesselTrackingEventTypes.DELETED);
		return event;
	}

	public static DeleteVesselTrackingFailedEvent getDeleteVesselTrackingFailedEvent() {

		DeleteVesselTrackingFailedEvent event = new DeleteVesselTrackingFailedEvent().buildFrom(getDeleteEvent());
		event.setType(VesselTrackingEventTypes.DELETE_FAILED);
		event.setExceptionType("ItemNotFound");
		return event;
	}

	public static DeleteVesselTrackingCancelledEvent getDeleteVesselTrackingCancelledEvent() {

		DeleteVesselTrackingCancelledEvent event = new DeleteVesselTrackingCancelledEvent().buildFrom(getDeleteEvent());
		event.setType(VesselTrackingEventTypes.DELETE_CANCELLED);
		event.setVesselTracking(getVesselTracking());
		event.setExceptionType("ItemNotFound");
		return event;
	}

	// UpdateVesselInVesselTracking

	public static UpdateVesselInVesselTrackingEvent getUpdateVesselInVesselTrackingEvent() {

		UpdateVesselInVesselTrackingEvent event = new UpdateVesselInVesselTrackingEvent();
		event.setAggregateId(PREFIX + MMSI);
		event.setType(VesselTrackingEventTypes.UPDATE_VESSEL);
		event.setVersion(2);
		event.setUserId(USER);
		event.setVessel(VesselDataUtil.getVessel());
		return event;
	}

	//////////////////////

	public static VesselTrackingDTO getVesselTracking() {

		VesselTrackingDTO vesselTracking = new VesselTrackingDTO();

		VesselDTO vessel = new VesselDTO();
		vessel.setId(VesselDataUtil.PREFIX + MMSI);
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

		vesselTracking.setId(PREFIX + MMSI + TSTAMP);
		vesselTracking.setUuid(UUID.randomUUID().toString());

		Point geometry = JTSFactoryFinder.getGeometryFactory().createPoint(new Coordinate(44.56433, 37.94388));
		vesselTracking.setGeometry(geometry);

		VesselTrackingPropertiesDTO properties = new VesselTrackingPropertiesDTO();
		vesselTracking.setProperties(properties);

		properties.setActivity("r.1.8.22");
		properties.setVessel(vessel);
		properties.setDate(DateTime.now());

		properties.setCog(23.3);
		properties.setSog(23.3);
		properties.setHeading(12);
		properties.setNavStat(33);
		properties.setDest("Santa Cruz de Tenerife");
		properties.setEta("00:00 00:00");
		properties.setQFlag("0");
		properties.setVFlag("N");

		return vesselTracking;
	}

}
