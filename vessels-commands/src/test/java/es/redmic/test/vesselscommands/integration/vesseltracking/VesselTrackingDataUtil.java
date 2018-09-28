package es.redmic.test.vesselscommands.integration.vesseltracking;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.geotools.geometry.jts.JTSFactoryFinder;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;

import es.redmic.exception.common.ExceptionType;
import es.redmic.test.vesselscommands.integration.vessel.VesselDataUtil;
import es.redmic.test.vesselscommands.integration.vesseltype.VesselTypeDataUtil;
import es.redmic.vesselslib.dto.tracking.VesselTrackingDTO;
import es.redmic.vesselslib.dto.tracking.VesselTrackingPropertiesDTO;
import es.redmic.vesselslib.dto.vessel.VesselDTO;
import es.redmic.vesselslib.dto.vesseltype.VesselTypeDTO;
import es.redmic.vesselslib.events.vesseltracking.VesselTrackingEventTypes;
import es.redmic.vesselslib.events.vesseltracking.create.CreateVesselTrackingConfirmedEvent;
import es.redmic.vesselslib.events.vesseltracking.create.CreateVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.create.CreateVesselTrackingFailedEvent;
import es.redmic.vesselslib.events.vesseltracking.create.EnrichCreateVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.create.VesselTrackingCreatedEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.DeleteVesselTrackingConfirmedEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.DeleteVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.DeleteVesselTrackingFailedEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.VesselTrackingDeletedEvent;
import es.redmic.vesselslib.events.vesseltracking.update.EnrichUpdateVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.update.UpdateVesselTrackingConfirmedEvent;
import es.redmic.vesselslib.events.vesseltracking.update.UpdateVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.update.UpdateVesselTrackingFailedEvent;
import es.redmic.vesselslib.events.vesseltracking.update.VesselTrackingUpdatedEvent;

public abstract class VesselTrackingDataUtil {

	public final static String PREFIX = "vesseltracking-mmsi-tstamp-", USER = "1";

	public static CreateVesselTrackingEvent getCreateEvent(Integer mmsi, String tstamp) {

		CreateVesselTrackingEvent event = new CreateVesselTrackingEvent();
		event.setAggregateId(PREFIX + mmsi + "-" + tstamp);
		event.setDate(DateTime.now());
		event.setId(UUID.randomUUID().toString());
		event.setType(VesselTrackingEventTypes.CREATE);
		event.setVersion(1);
		event.setUserId(USER);
		event.setSessionId("sessionIdA");
		event.setVesselTracking(getVesselTracking(mmsi, tstamp));

		return event;
	}

	public static EnrichCreateVesselTrackingEvent getEnrichCreateVesselTrackingEvent(Integer mmsi, String tstamp) {

		CreateVesselTrackingEvent createEvent = getCreateEvent(mmsi, tstamp);

		EnrichCreateVesselTrackingEvent event = new EnrichCreateVesselTrackingEvent().buildFrom(createEvent);
		event.setVesselTracking(createEvent.getVesselTracking());

		return event;
	}

	public static VesselTrackingCreatedEvent getCreateVesselTrackingEvent(Integer mmsi, String tstamp) {

		CreateVesselTrackingEvent createEvent = getCreateEvent(mmsi, tstamp);

		VesselTrackingCreatedEvent event = new VesselTrackingCreatedEvent().buildFrom(createEvent);
		event.setVesselTracking(createEvent.getVesselTracking());
		return event;
	}

	public static CreateVesselTrackingConfirmedEvent getCreateVesselTrackingConfirmedEvent(Integer mmsi,
			String tstamp) {

		return new CreateVesselTrackingConfirmedEvent().buildFrom(getCreateEvent(mmsi, tstamp));
	}

	public static VesselTrackingCreatedEvent getVesselTrackingCreatedEvent(Integer mmsi, String tstamp) {

		CreateVesselTrackingEvent createEvent = getCreateEvent(mmsi, tstamp);

		VesselTrackingCreatedEvent event = new VesselTrackingCreatedEvent().buildFrom(createEvent);
		event.setVesselTracking(createEvent.getVesselTracking());
		return event;
	}

	public static CreateVesselTrackingFailedEvent getCreateVesselTrackingFailedEvent(Integer mmsi, String tstamp) {

		CreateVesselTrackingFailedEvent createVesselTrackingFailedEvent = new CreateVesselTrackingFailedEvent()
				.buildFrom(getCreateEvent(mmsi, tstamp));

		createVesselTrackingFailedEvent.setExceptionType(ExceptionType.ITEM_ALREADY_EXIST_EXCEPTION.name());
		Map<String, String> arguments = new HashMap<>();
		arguments.put("A", "B");
		createVesselTrackingFailedEvent.setArguments(arguments);

		return createVesselTrackingFailedEvent;
	}

	// update

	public static UpdateVesselTrackingEvent getUpdateEvent(Integer mmsi, String tstamp) {

		UpdateVesselTrackingEvent event = new UpdateVesselTrackingEvent();
		event.setAggregateId(PREFIX + mmsi + "-" + tstamp);
		event.setDate(DateTime.now());
		event.setId(UUID.randomUUID().toString());
		event.setType(VesselTrackingEventTypes.UPDATE);
		event.setVersion(2);
		event.setUserId(USER);
		event.setSessionId("sessionIdB");
		event.setVesselTracking(getVesselTracking(mmsi, tstamp));

		return event;
	}

	public static EnrichUpdateVesselTrackingEvent getEnrichUpdateVesselTrackingEvent(Integer mmsi, String tstamp) {

		UpdateVesselTrackingEvent updateEvent = getUpdateEvent(mmsi, tstamp);

		EnrichUpdateVesselTrackingEvent event = new EnrichUpdateVesselTrackingEvent().buildFrom(updateEvent);
		event.setVesselTracking(updateEvent.getVesselTracking());
		return event;

	}

	public static UpdateVesselTrackingConfirmedEvent getUpdateVesselTrackingConfirmedEvent(Integer mmsi,
			String tstamp) {
		return new UpdateVesselTrackingConfirmedEvent().buildFrom(getUpdateEvent(mmsi, tstamp));
	}

	public static VesselTrackingUpdatedEvent getVesselTrackingUpdatedEvent(Integer mmsi, String tstamp) {

		UpdateVesselTrackingEvent updateEvent = getUpdateEvent(mmsi, tstamp);

		VesselTrackingUpdatedEvent event = new VesselTrackingUpdatedEvent().buildFrom(updateEvent);
		event.setVesselTracking(updateEvent.getVesselTracking());
		return event;
	}

	public static UpdateVesselTrackingFailedEvent getUpdateVesselTrackingFailedEvent(Integer mmsi, String tstamp) {

		UpdateVesselTrackingFailedEvent event = new UpdateVesselTrackingFailedEvent()
				.buildFrom(getUpdateEvent(mmsi, tstamp));

		event.setExceptionType(ExceptionType.ITEM_NOT_FOUND.name());
		Map<String, String> arguments = new HashMap<>();
		arguments.put("A", "B");
		event.setArguments(arguments);

		return event;
	}

	// delete

	public static DeleteVesselTrackingEvent getDeleteEvent(Integer mmsi, String tstamp) {

		DeleteVesselTrackingEvent event = new DeleteVesselTrackingEvent();
		event.setAggregateId(PREFIX + mmsi + "-" + tstamp);
		event.setType(VesselTrackingEventTypes.DELETE);
		event.setVersion(3);
		event.setUserId(USER);
		return event;
	}

	public static DeleteVesselTrackingConfirmedEvent getDeleteVesselTrackingConfirmedEvent(Integer mmsi,
			String tstamp) {

		return new DeleteVesselTrackingConfirmedEvent().buildFrom(getDeleteEvent(mmsi, tstamp));
	}

	public static VesselTrackingDeletedEvent getVesselTrackingDeletedEvent(Integer mmsi, String tstamp) {

		VesselTrackingDeletedEvent event = new VesselTrackingDeletedEvent().buildFrom(getDeleteEvent(mmsi, tstamp));
		return event;
	}

	public static DeleteVesselTrackingFailedEvent getDeleteVesselTrackingFailedEvent(Integer mmsi, String tstamp) {

		DeleteVesselTrackingFailedEvent event = new DeleteVesselTrackingFailedEvent()
				.buildFrom(getDeleteEvent(mmsi, tstamp));

		event.setExceptionType(ExceptionType.DELETE_ITEM_EXCEPTION.name());
		Map<String, String> arguments = new HashMap<>();
		// arguments.put("A", "B");
		event.setArguments(arguments);
		return event;
	}

	public static VesselTrackingDTO getVesselTracking(Integer mmsi, String tstamp) {

		VesselTrackingDTO vesselTracking = new VesselTrackingDTO();

		VesselDTO vessel = new VesselDTO();
		vessel.setId(VesselDataUtil.PREFIX + mmsi);
		vessel.setMmsi(Integer.valueOf(mmsi));
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

		vesselTracking.setId(PREFIX + mmsi + "-" + tstamp);
		vesselTracking.setUuid(UUID.randomUUID().toString());

		Point geometry = JTSFactoryFinder.getGeometryFactory().createPoint(new Coordinate(44.56433, 37.94388));
		vesselTracking.setGeometry(geometry);

		VesselTrackingPropertiesDTO properties = new VesselTrackingPropertiesDTO();
		vesselTracking.setProperties(properties);

		properties.setActivity("22");
		properties.setVessel(vessel);
		properties.setDate(new DateTime(DateTimeZone.UTC).toDateTimeISO());

		properties.setCog(23.3);
		properties.setSog(23.3);
		properties.setHeading(12);
		properties.setNavStat(33);
		properties.setDest("Santa Cruz de Tenerife");
		properties.setEta("00:00 00:00");

		return vesselTracking;
	}

}
