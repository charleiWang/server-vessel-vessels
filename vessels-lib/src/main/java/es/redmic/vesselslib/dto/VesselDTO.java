package es.redmic.vesselslib.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaUrl;

import es.redmic.brokerlib.avro.common.CommonDTO;
import es.redmic.brokerlib.deserializer.CustomRelationDeserializer;
import es.redmic.vesselslib.constraintvalidation.vessel.ValidateVesselId;

@ValidateVesselId(mmsi = "mmsi", imo = "imo")
public class VesselDTO extends CommonDTO {

	// @formatter:off

	@JsonIgnore
	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse(
		"{\"type\":\"record\",\"name\":\"VesselDTO\",\"namespace\":\"es.redmic.vesselslib.dto\",\"fields\":["
			+ "{\"name\":\"mmsi\",\"type\":\"int\"},"
			+ "{\"name\":\"imo\",\"type\":[\"int\", \"null\"]},"
			+ "{\"name\":\"type\",\"type\":[{ \"name\":\"VesselTypeDTO\", \"type\":\"record\","
					+ "\"namespace\":\"es.redmic.vesselslib.dto\",\"fields\":["
				+ "{\"name\":\"code\",\"type\":\"string\"},"
				+ "{\"name\":\"name\",\"type\":\"string\"},"
				+ "{\"name\":\"name_en\",\"type\":\"string\"},"
				+ "{\"name\":\"id\",\"type\":\"string\"}]}, \"null\"]},"
			+ "{\"name\":\"name\",\"type\":[\"string\", \"null\"]},"
			+ "{\"name\":\"callSign\",\"type\":[\"string\", \"null\"]},"
			+ "{\"name\":\"length\",\"type\":[\"double\", \"null\"]},"
			+ "{\"name\":\"beam\",\"type\":[\"double\", \"null\"]},"
			+ "{\"name\":\"id\",\"type\":\"string\"}]}");
	// @formatter:on

	public VesselDTO() {
	}

	@Max(999999999)
	private Integer mmsi;

	@Max(9999999)
	private Integer imo;

	@NotNull
	@JsonSerialize(as = VesselTypeDTO.class)
	@JsonDeserialize(using = CustomRelationDeserializer.class)
	@JsonSchemaUrl(value = "controller.mapping.vesseltype")
	private VesselTypeDTO type;

	@Size(min = 1, max = 500)
	@NotNull
	private String name;

	private String callSign;

	private Double length;

	private Double beam;

	public Integer getMmsi() {
		return mmsi;
	}

	public void setMmsi(Integer mmsi) {
		this.mmsi = mmsi;
	}

	public Integer getImo() {
		return imo;
	}

	public void setImo(Integer imo) {

		if (imo != null && imo == 0)
			this.imo = null;
		else
			this.imo = imo;
	}

	public VesselTypeDTO getType() {
		return type;
	}

	public void setType(VesselTypeDTO type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCallSign() {
		return callSign;
	}

	public void setCallSign(String callSign) {
		this.callSign = callSign;
	}

	public Double getLength() {
		return length;
	}

	public void setLength(Double length) {
		this.length = length;
	}

	public Double getBeam() {
		return beam;
	}

	public void setBeam(Double beam) {
		this.beam = beam;
	}

	@JsonIgnore
	@Override
	public org.apache.avro.Schema getSchema() {
		return SCHEMA$;
	}

	@JsonIgnore
	@Override
	public java.lang.Object get(int field$) {
		switch (field$) {
		case 0:
			return mmsi;
		case 1:
			return imo;
		case 2:
			return type;
		case 3:
			return name;
		case 4:
			return callSign;
		case 5:
			return length;
		case 6:
			return beam;
		case 7:
			return getId();
		default:
			throw new org.apache.avro.AvroRuntimeException("Bad index");
		}
	}

	@JsonIgnore
	@Override
	public void put(int field$, java.lang.Object value$) {
		switch (field$) {
		case 0:
			mmsi = (java.lang.Integer) value$;
			break;
		case 1:
			imo = (java.lang.Integer) value$;
			break;
		case 2:
			type = (VesselTypeDTO) value$;
			break;
		case 3:
			name = value$ != null ? value$.toString() : null;
			break;
		case 4:
			callSign = value$ != null ? value$.toString() : null;
			break;
		case 5:
			length = (java.lang.Double) value$;
			break;
		case 6:
			beam = (java.lang.Double) value$;
			break;
		case 7:
			setId(value$.toString());
			break;
		default:
			throw new org.apache.avro.AvroRuntimeException("Bad index");
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((beam == null) ? 0 : beam.hashCode());
		result = prime * result + ((callSign == null) ? 0 : callSign.hashCode());
		result = prime * result + ((imo == null) ? 0 : imo.hashCode());
		result = prime * result + ((length == null) ? 0 : length.hashCode());
		result = prime * result + ((mmsi == null) ? 0 : mmsi.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		VesselDTO other = (VesselDTO) obj;
		if (beam == null) {
			if (other.beam != null)
				return false;
		} else if (!beam.equals(other.beam))
			return false;
		if (callSign == null) {
			if (other.callSign != null)
				return false;
		} else if (!callSign.equals(other.callSign))
			return false;
		if (imo == null) {
			if (other.imo != null)
				return false;
		} else if (!imo.equals(other.imo))
			return false;
		if (length == null) {
			if (other.length != null)
				return false;
		} else if (!length.equals(other.length))
			return false;
		if (mmsi == null) {
			if (other.mmsi != null)
				return false;
		} else if (!mmsi.equals(other.mmsi))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
}
