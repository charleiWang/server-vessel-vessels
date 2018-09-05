package es.redmic.vesselslib.dto.tracking;

import javax.validation.constraints.NotNull;

import org.apache.avro.Schema;
import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaNotNull;
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaUrl;

import es.redmic.brokerlib.avro.geodata.common.PropertiesDTO;
import es.redmic.brokerlib.deserializer.CustomDateTimeDeserializer;
import es.redmic.brokerlib.deserializer.CustomRelationDeserializer;
import es.redmic.brokerlib.serializer.CustomDateTimeSerializer;
import es.redmic.vesselslib.dto.vessel.VesselDTO;

@JsonSchemaNotNull
public class VesselTrackingPropertiesDTO extends PropertiesDTO {

	// @formatter:off

	@JsonIgnore
	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("");
	
	// @formatter:on

	@NotNull
	@JsonSerialize(as = VesselDTO.class)
	@JsonDeserialize(using = CustomRelationDeserializer.class)
	@JsonSchemaUrl(value = "controller.mapping.vessel")
	private VesselDTO vessel;

	@NotNull
	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@JsonDeserialize(using = CustomDateTimeDeserializer.class)
	private DateTime date;

	private Double cog;

	private Double sog;

	private Integer heading;

	private Integer navStat;

	private String dest;

	private String eta;

	public VesselDTO getVessel() {
		return vessel;
	}

	public void setVessel(VesselDTO vessel) {
		this.vessel = vessel;
	}

	public DateTime getDate() {
		return date;
	}

	public void setDate(DateTime date) {
		this.date = date;
	}

	public Double getCog() {
		return cog;
	}

	public void setCog(Double cog) {
		this.cog = cog;
	}

	public Double getSog() {
		return sog;
	}

	public void setSog(Double sog) {
		this.sog = sog;
	}

	public Integer getHeading() {
		return heading;
	}

	public void setHeading(Integer heading) {
		this.heading = heading;
	}

	public Integer getNavStat() {
		return navStat;
	}

	public void setNavStat(Integer navStat) {
		this.navStat = navStat;
	}

	public String getDest() {
		return dest;
	}

	public void setDest(String dest) {
		this.dest = dest;
	}

	public String getEta() {
		return eta;
	}

	public void setEta(String eta) {
		this.eta = eta;
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