package es.redmic.test.vesselscommands.integration.vesseltracking;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.geotools.geometry.jts.JTSFactoryFinder;
import org.joda.time.DateTime;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;

import es.redmic.exception.common.ExceptionType;
import es.redmic.test.vesselscommands.integration.vesseltype.VesselTypeDataUtil;
import es.redmic.vesselslib.dto.tracking.VesselTrackingDTO;
import es.redmic.vesselslib.dto.tracking.VesselTrackingPropertiesDTO;
import es.redmic.vesselslib.dto.vessel.VesselDTO;
import es.redmic.vesselslib.dto.vesseltype.VesselTypeDTO;
import es.redmic.vesselslib.events.vessel.create.CreateVesselFailedEvent;
import es.redmic.vesselslib.events.vesseltracking.VesselTrackingEventTypes;
import es.redmic.vesselslib.events.vesseltracking.create.CreateVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.create.EnrichCreateVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.create.VesselTrackingCreatedEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.DeleteVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.VesselTrackingDeletedEvent;
import es.redmic.vesselslib.events.vesseltracking.update.UpdateVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.update.VesselTrackingUpdatedEvent;
import es.redmic.vesselslib.unit.utils.VesselDataUtil;

public abstract class VesselTrackingDataUtil {

	public final static String PREFIX = "vesseltracking-mmsi-tstamp-", USER = "1";

	public static CreateVesselTrackingEvent getCreateEvent(Integer MMSI) {

		String TSTAMP = String.valueOf(new DateTime().getMillis());

		CreateVesselTrackingEvent event = new CreateVesselTrackingEvent();
		event.setAggregateId(PREFIX + MMSI + TSTAMP);
		event.setDate(DateTime.now());
		event.setId(UUID.randomUUID().toString());
		event.setType(VesselTrackingEventTypes.CREATE);
		event.setVersion(1);
		event.setUserId(USER);
		event.setSessionId("sessionIdA");
		event.setVesselTracking(getVesselTracking(MMSI, TSTAMP));

		return event;
	}

	public static EnrichCreateVesselTrackingEvent getEnrichCreateVesselTrackingEvent(Integer mmsi) {

		CreateVesselTrackingEvent createEvent = getCreateEvent(mmsi);

		EnrichCreateVesselTrackingEvent event = new EnrichCreateVesselTrackingEvent().buildFrom(createEvent);
		event.setVesselTracking(createEvent.getVesselTracking());

		return event;
	}

	public static VesselTrackingCreatedEvent getCreateVesselTrackingEvent(Integer mmsi) {

		CreateVesselTrackingEvent createEvent = getCreateEvent(mmsi);

		VesselTrackingCreatedEvent event = new VesselTrackingCreatedEvent().buildFrom(createEvent);
		event.setVesselTracking(createEvent.getVesselTracking());
		return event;
	}

	public static VesselTrackingCreatedEvent getVesselTrackingCreatedEvent(Integer mmsi) {

		CreateVesselTrackingEvent createEvent = getCreateEvent(mmsi);

		VesselTrackingCreatedEvent event = new VesselTrackingCreatedEvent().buildFrom(createEvent);
		event.setVesselTracking(createEvent.getVesselTracking());
		return event;
	}

	public static CreateVesselFailedEvent getCreateVesselFailedEvent(Integer mmsi) {

		CreateVesselFailedEvent createVesselFailedEvent = new CreateVesselFailedEvent().buildFrom(getCreateEvent(mmsi));

		createVesselFailedEvent.setExceptionType(ExceptionType.ITEM_ALREADY_EXIST_EXCEPTION.name());
		Map<String, String> arguments = new HashMap<>();
		arguments.put("A", "B");
		createVesselFailedEvent.setArguments(arguments);

		return createVesselFailedEvent;
	}

	// update

	public static UpdateVesselTrackingEvent getUpdateEvent(Integer MMSI) {

		String TSTAMP = String.valueOf(new DateTime().getMillis());

		UpdateVesselTrackingEvent event = new UpdateVesselTrackingEvent();
		event.setAggregateId(PREFIX + MMSI + TSTAMP);
		event.setDate(DateTime.now());
		event.setId(UUID.randomUUID().toString());
		event.setType(VesselTrackingEventTypes.UPDATE);
		event.setVersion(2);
		event.setUserId(USER);
		event.setSessionId("sessionIdB");
		event.setVesselTracking(getVesselTracking(MMSI, TSTAMP));

		return event;
	}

	public static VesselTrackingUpdatedEvent getVesselTrackingUpdatedEvent(Integer mmsi) {

		UpdateVesselTrackingEvent updateEvent = getUpdateEvent(mmsi);

		VesselTrackingUpdatedEvent event = new VesselTrackingUpdatedEvent().buildFrom(updateEvent);
		event.setVesselTracking(updateEvent.getVesselTracking());
		return event;
	}

	// delete

	public static DeleteVesselTrackingEvent getDeleteEvent(Integer MMSI) {

		String TSTAMP = String.valueOf(new DateTime().getMillis());

		DeleteVesselTrackingEvent event = new DeleteVesselTrackingEvent();
		event.setAggregateId(PREFIX + MMSI + TSTAMP);
		event.setType(VesselTrackingEventTypes.DELETE);
		event.setVersion(3);
		event.setUserId(USER);
		return event;
	}

	public static VesselTrackingDeletedEvent getVesselTrackingDeletedEvent(Integer MMSI) {

		VesselTrackingDeletedEvent event = new VesselTrackingDeletedEvent().buildFrom(getDeleteEvent(MMSI));
		return event;
	}

	public static VesselTrackingDTO getVesselTracking(Integer MMSI, String TSTAMP) {

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

		properties.setActivityId("22");
		properties.setVessel(vessel);
		properties.setDate(DateTime.now());

		properties.setCog(23.3);
		properties.setSog(23.3);
		properties.setHeading(12);
		properties.setNavStat(33);
		properties.setDest("Santa Cruz de Tenerife");
		properties.setEta("00:00 00:00");

		return vesselTracking;
	}

}
