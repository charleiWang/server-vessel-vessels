package es.redmic.vesselslib.events.vesseltype.common;

import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.fasterxml.jackson.annotation.JsonIgnore;

import es.redmic.brokerlib.avro.common.EventError;
import es.redmic.vesselslib.dto.VesselTypeDTO;

public abstract class VesselTypeCancelledEvent extends EventError {

	private VesselTypeDTO vesselType;

	public VesselTypeCancelledEvent(String type) {
		super(type);
		this.setVesselType(vesselType);
	}

	public VesselTypeDTO getVesselType() {
		return vesselType;
	}

	public void setVesselType(VesselTypeDTO vesselType) {
		this.vesselType = vesselType;
	}

	@Override
	public Object get(int field$) {
		switch (field$) {
		case 0:
			return getVesselType();
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
			vesselType = (VesselTypeDTO) value$;
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
	public static String getVesselTypeEventSchema() {
		// @formatter:off
		return "{\"name\":\"vesselType\",\"type\":{ \"type\":\"record\",\"name\":\"VesselTypeDTO\","
				+ "\"namespace\":\"es.redmic.vesselslib.dto\",\"fields\":["
				+ "{\"name\":\"code\",\"type\":\"string\"},"
				+ "{\"name\":\"name\",\"type\":\"string\"},"
				+ "{\"name\":\"name_en\",\"type\":\"string\"},"
				+ "{\"name\":\"id\",\"type\":\"string\"}]}}";
		// @formatter:on
	}
}
