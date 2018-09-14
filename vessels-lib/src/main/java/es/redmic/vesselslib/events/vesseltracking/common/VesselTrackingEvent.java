package es.redmic.vesselslib.events.vesseltracking.common;

import org.apache.avro.Schema;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.fasterxml.jackson.annotation.JsonIgnore;

import es.redmic.brokerlib.avro.common.Event;
import es.redmic.vesselslib.dto.tracking.VesselTrackingDTO;

public abstract class VesselTrackingEvent extends Event {

	private VesselTrackingDTO vesselTracking;

	public VesselTrackingEvent(String type) {
		super(type);
	}

	public VesselTrackingDTO getVesselTracking() {
		return vesselTracking;
	}

	public void setVesselTracking(VesselTrackingDTO vesselTracking) {
		this.vesselTracking = vesselTracking;
	}

	@JsonIgnore
	@Override
	public Object get(int field$) {
		switch (field$) {
		case 0:
			return getVesselTracking();
		case 1:
			return getAggregateId();
		case 2:
			return getVersion();
		case 3:
			return getType();
		case 4:
			return getDate().getMillis();
		case 5:
			return getSessionId();
		case 6:
			return getUserId();
		case 7:
			return getId();
		default:
			throw new org.apache.avro.AvroRuntimeException("Bad index");
		}
	}

	@JsonIgnore
	@Override
	public void put(int field$, Object value$) {
		switch (field$) {
		case 0:
			vesselTracking = (VesselTrackingDTO) value$;
			break;
		case 1:
			setAggregateId(value$.toString());
			break;
		case 2:
			setVersion((java.lang.Integer) value$);
			break;
		case 3:
			setType(value$.toString());
			break;
		case 4:
			setDate(new DateTime(value$, DateTimeZone.UTC));
			break;
		case 5:
			setSessionId(value$.toString());
			break;
		case 6:
			setUserId(value$.toString());
			break;
		case 7:
			setId(value$.toString());
			break;
		default:
			throw new org.apache.avro.AvroRuntimeException("Bad index");
		}
	}

	@JsonIgnore
	public static String getVesselTrackingEventSchema() {

		return "{\"name\":\"vesselTracking\", \"type\": " + VesselTrackingDTO.SCHEMA$.toString() + "}";
	}

	@JsonIgnore
	@Override
	public Schema getSchema() {
		return null;
	}
}
