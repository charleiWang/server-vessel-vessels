package es.redmic.vesselslib.events.vessel.common;

import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.fasterxml.jackson.annotation.JsonIgnore;

import es.redmic.brokerlib.avro.common.EventError;
import es.redmic.vesselslib.dto.vessel.VesselDTO;

public abstract class VesselCancelledEvent extends EventError {

	private VesselDTO vessel;

	public VesselCancelledEvent(String type) {
		super(type);
	}

	public VesselDTO getVessel() {
		return vessel;
	}

	public void setVessel(VesselDTO vessel) {
		this.vessel = vessel;
	}

	@Override
	public Object get(int field$) {
		switch (field$) {
		case 0:
			return getVessel();
		case 1:
			return getExceptionType();
		case 2:
			return getArguments();
		case 3:
			return getAggregateId();
		case 4:
			return getVersion();
		case 5:
			return getType();
		case 6:
			return getDate().getMillis();
		case 7:
			return getSessionId();
		case 8:
			return getUserId();
		case 9:
			return getId();
		default:
			throw new org.apache.avro.AvroRuntimeException("Bad index");
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void put(int field$, Object value$) {
		switch (field$) {
		case 0:
			vessel = (es.redmic.vesselslib.dto.vessel.VesselDTO) value$;
			break;
		case 1:
			setExceptionType(value$.toString());
			break;
		case 2:
			setArguments((Map) value$);
			break;
		case 3:
			setAggregateId(value$.toString());
			break;
		case 4:
			setVersion((java.lang.Integer) value$);
			break;
		case 5:
			setType(value$.toString());
			break;
		case 6:
			setDate(new DateTime(value$, DateTimeZone.UTC));
			break;
		case 7:
			setSessionId(value$.toString());
			break;
		case 8:
			setUserId(value$.toString());
			break;
		case 9:
			setId(value$.toString());
			break;
		default:
			throw new org.apache.avro.AvroRuntimeException("Bad index");
		}
	}

	@JsonIgnore
	public static String getVesselEventSchema() {

		return "{\"name\":\"vessel\", \"type\":" + VesselDTO.SCHEMA$.toString() + "}";
	}
}
