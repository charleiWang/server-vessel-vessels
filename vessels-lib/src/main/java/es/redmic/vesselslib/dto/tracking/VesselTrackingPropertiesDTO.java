package es.redmic.vesselslib.dto.tracking;

import javax.validation.constraints.NotNull;

import org.apache.avro.Schema;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

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
	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse(
			"{\"type\":\"record\",\"name\":\"VesselTrackingPropertiesDTO\",\"namespace\":\"es.redmic.vesselslib.dto.tracking\",\"fields\":["
					+ "{\"name\":\"vessel\",\"type\":[{\"type\":\"record\",\"name\":\"VesselDTO\",\"namespace\":\"es.redmic.vesselslib.dto.vessel\",\"fields\":["
						+ "{\"name\":\"mmsi\",\"type\":\"int\"},"
						+ "{\"name\":\"imo\",\"type\":[\"int\", \"null\"]},"
						+ "{\"name\":\"type\",\"type\":[{ \"name\":\"VesselTypeDTO\", \"type\":\"record\","
								+ "\"namespace\":\"es.redmic.vesselslib.dto.vesseltype\",\"fields\":["
							+ "{\"name\":\"code\",\"type\":\"string\"},"
							+ "{\"name\":\"name\",\"type\":[\"string\", \"null\"]},"
							+ "{\"name\":\"name_en\",\"type\":[\"string\", \"null\"]},"
							+ "{\"name\":\"id\",\"type\":\"string\"}]}, \"null\"]},"
						+ "{\"name\":\"name\",\"type\":[\"string\", \"null\"]},"
						+ "{\"name\":\"callSign\",\"type\":[\"string\", \"null\"]},"
						+ "{\"name\":\"length\",\"type\":[\"double\", \"null\"]},"
						+ "{\"name\":\"beam\",\"type\":[\"double\", \"null\"]},"
						+ "{\"name\":\"inserted\",\"type\":[\"null\",{\"type\":\"long\",\"logicalType\":\"timestamp-millis\"}],"
							+ "\"default\": null},"
						+ "{\"name\":\"updated\",\"type\":[\"null\",{\"type\":\"long\",\"logicalType\":\"timestamp-millis\"}],"
							+ "\"default\": null},"
						+ "{\"name\":\"id\",\"type\":\"string\"}]}]},"
						
					+ "{\"name\":\"date\",\"type\":{ \"type\":\"long\",\"logicalType\":\"timestamp-millis\"}},"
					+ "{\"name\":\"cog\",\"type\":[\"double\", \"null\"]},"
					+ "{\"name\":\"sog\",\"type\":[\"double\", \"null\"]},"
					+ "{\"name\":\"heading\",\"type\":[\"int\", \"null\"]},"
					+ "{\"name\":\"navStat\",\"type\":[\"int\", \"null\"]},"
					+ "{\"name\":\"dest\",\"type\":[\"string\", \"null\"]},"
					+ "{\"name\":\"eta\",\"type\":[\"string\", \"null\"]},"
					+ "{\"name\":\"activityId\",\"type\":\"string\"},"
					+ "{\"name\":\"inserted\",\"type\":[\"null\",{\"type\":\"long\",\"logicalType\":\"timestamp-millis\"}],"
						+ "\"default\": null},"
					+ "{\"name\":\"updated\",\"type\":[\"null\",{\"type\":\"long\",\"logicalType\":\"timestamp-millis\"}],"
						+ "\"default\": null}]}");
	
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
			return vessel;
		case 1:
			return date.getMillis();
		case 2:
			return cog;
		case 3:
			return sog;
		case 4:
			return heading;
		case 5:
			return navStat;
		case 6:
			return dest;
		case 7:
			return eta;
		case 8:
			return getActivityId();
		case 9:
			return getInserted() != null ? getInserted().getMillis() : null;
		case 10:
			return getUpdated() != null ? getUpdated().getMillis() : null;
		default:
			throw new org.apache.avro.AvroRuntimeException("Bad index");
		}
	}

	@JsonIgnore
	@Override
	public void put(int field, Object value) {
		switch (field) {
		case 0:
			vessel = (VesselDTO) value;
			break;
		case 1:
			date = new DateTime(value, DateTimeZone.UTC);
			break;
		case 2:
			cog = (java.lang.Double) value;
			break;
		case 3:
			sog = (java.lang.Double) value;
			break;
		case 4:
			heading = (java.lang.Integer) value;
			break;
		case 5:
			navStat = (java.lang.Integer) value;
			break;
		case 6:
			dest = value != null ? value.toString() : null;
			break;
		case 7:
			eta = value != null ? value.toString() : null;
			break;
		case 8:
			setActivityId(value.toString());
			break;
		case 9:
			setInserted(value != null ? new DateTime(value, DateTimeZone.UTC).toDateTime() : null);
			break;
		case 10:
			setUpdated(value != null ? new DateTime(value, DateTimeZone.UTC).toDateTime() : null);
			break;
		default:
			throw new org.apache.avro.AvroRuntimeException("Bad index");
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((cog == null) ? 0 : cog.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((dest == null) ? 0 : dest.hashCode());
		result = prime * result + ((eta == null) ? 0 : eta.hashCode());
		result = prime * result + ((heading == null) ? 0 : heading.hashCode());
		result = prime * result + ((navStat == null) ? 0 : navStat.hashCode());
		result = prime * result + ((sog == null) ? 0 : sog.hashCode());
		result = prime * result + ((vessel == null) ? 0 : vessel.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		VesselTrackingPropertiesDTO other = (VesselTrackingPropertiesDTO) obj;
		if (cog == null) {
			if (other.cog != null)
				return false;
		} else if (!cog.equals(other.cog))
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (dest == null) {
			if (other.dest != null)
				return false;
		} else if (!dest.equals(other.dest))
			return false;
		if (eta == null) {
			if (other.eta != null)
				return false;
		} else if (!eta.equals(other.eta))
			return false;
		if (heading == null) {
			if (other.heading != null)
				return false;
		} else if (!heading.equals(other.heading))
			return false;
		if (navStat == null) {
			if (other.navStat != null)
				return false;
		} else if (!navStat.equals(other.navStat))
			return false;
		if (sog == null) {
			if (other.sog != null)
				return false;
		} else if (!sog.equals(other.sog))
			return false;
		if (vessel == null) {
			if (other.vessel != null)
				return false;
		} else if (!vessel.equals(other.vessel))
			return false;
		return true;
	}
}