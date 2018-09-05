package es.redmic.vesselslib.dto.tracking;

import org.apache.avro.Schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vividsolutions.jts.geom.Point;

import es.redmic.brokerlib.avro.geodata.common.FeatureDTO;
import es.redmic.brokerlib.avro.geodata.common.GeoJSONFeatureType;

public class VesselTrackingDTO extends FeatureDTO<VesselTrackingPropertiesDTO, Point> {

	// @formatter:off

	@JsonIgnore
	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("");
	
	// @formatter:on

	public VesselTrackingDTO() {
		super();
		this.setType(GeoJSONFeatureType.FEATURE);
	}

	@Override
	public Schema getSchema() {
		return SCHEMA$;
	}

	@Override
	public Object get(int field) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void put(int field, Object value) {
		// TODO Auto-generated method stub

	}

}
