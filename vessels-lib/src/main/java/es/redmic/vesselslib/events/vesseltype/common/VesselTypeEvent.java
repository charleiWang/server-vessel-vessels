package es.redmic.vesselslib.events.vesseltype.common;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.fasterxml.jackson.annotation.JsonIgnore;

import es.redmic.brokerlib.avro.common.Event;
import es.redmic.vesselslib.dto.VesselTypeDTO;

public abstract class VesselTypeEvent extends Event {

	private VesselTypeDTO vesselType;

	public VesselTypeEvent(String type) {
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

	@Override
	public void put(int field$, Object value$) {
		switch (field$) {
		case 0:
			vesselType = (VesselTypeDTO) value$;
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
