package es.redmic.vesselslib.dto.tracking;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.avro.Schema;
import org.locationtech.jts.geom.Point;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;

import es.redmic.brokerlib.avro.geodata.common.FeatureDTO;
import es.redmic.brokerlib.avro.geodata.utils.GeoJSONFeatureType;

public class VesselTrackingDTO extends FeatureDTO<VesselTrackingPropertiesDTO, Point> {

	// @formatter:off

	@JsonIgnore
	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse(
			"{\"type\":\"record\",\"name\":\"VesselTrackingDTO\",\"namespace\":\"es.redmic.vesselslib.dto.tracking\",\"fields\":["
				+ "{\"name\":\"uuid\",\"type\": \"string\"},"
				+ "{\"name\":\"type\", \"type\": {"
					+ "\"type\":\"enum\",\"name\":\"GeoJSONFeatureType\"," + 
						"\"symbols\":[\"FEATURE\"]}},"
				+ "{\"name\":\"properties\",\"type\":" + VesselTrackingPropertiesDTO.SCHEMA$.toString() + "},"
				+ "{\"name\":\"geometry\",\"type\":[\"bytes\", \"null\"]},"
				+ "{\"name\":\"id\",\"type\":\"string\"}"
			+ "]}");
	
	// @formatter:on

	public VesselTrackingDTO() {
		super();
		this.setType(GeoJSONFeatureType.FEATURE);
	}

	@JsonIgnore
	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}

	@JsonIgnore
	@Override
	public Object get(int field) {
		switch (field) {
		case 0:
			return getUuid();
		case 1:
			return getType();
		case 2:
			return getProperties();
		case 3:
			try {
				return ByteBuffer.wrap(mapper.writeValueAsBytes(getGeometry()));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				return null;
			}
		case 4:
			return getId();
		default:
			throw new org.apache.avro.AvroRuntimeException("Bad index");
		}
	}

	@JsonIgnore
	@Override
	public void put(int field, Object value) {
		switch (field) {
		case 0:
			setUuid(value != null ? value.toString() : null);
			break;
		case 1:
			if (value != null) {
				setType(GeoJSONFeatureType.fromString(value.toString()));
			}
			break;
		case 2:
			setProperties((VesselTrackingPropertiesDTO) value);
			break;
		case 3:
			try {
				setGeometry(mapper.readValue(((ByteBuffer) value).array(), Point.class));
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case 4:
			setId(value != null ? value.toString() : null);
			break;
		default:
			throw new org.apache.avro.AvroRuntimeException("Bad index");
		}
	}

}
