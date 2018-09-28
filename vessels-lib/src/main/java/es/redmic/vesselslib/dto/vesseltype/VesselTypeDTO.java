package es.redmic.vesselslib.dto.vesseltype;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import es.redmic.brokerlib.avro.common.CommonDTO;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VesselTypeDTO extends CommonDTO {

	// @formatter:off

	@JsonIgnore
	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse(
		"{\"type\":\"record\",\"name\":\"VesselTypeDTO\",\"namespace\":\"es.redmic.vesselslib.dto.vesseltype\",\"fields\":["
			+ "{\"name\":\"code\",\"type\":\"string\"},"
			+ "{\"name\":\"name\",\"type\":[\"string\", \"null\"]},"
			+ "{\"name\":\"name_en\",\"type\":[\"string\", \"null\"]},"
			+ "{\"name\":\"id\",\"type\":\"string\"}]}");
	// @formatter:on

	public VesselTypeDTO() {
	}

	@NotNull
	@Size(min = 1, max = 10)
	private String code;

	@NotNull
	@Size(min = 1, max = 500)
	private String name;

	@NotNull
	@Size(min = 1, max = 500)
	private String name_en;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName_en() {
		return name_en;
	}

	public void setName_en(String name_en) {
		this.name_en = name_en;
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
			return code;
		case 1:
			return name;
		case 2:
			return name_en;
		case 3:
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
			code = value$ != null ? value$.toString() : null;
			break;
		case 1:
			name = value$ != null ? value$.toString() : null;
			break;
		case 2:
			name_en = value$ != null ? value$.toString() : null;
			break;
		case 3:
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
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((name_en == null) ? 0 : name_en.hashCode());
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
		VesselTypeDTO other = (VesselTypeDTO) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (name_en == null) {
			if (other.name_en != null)
				return false;
		} else if (!name_en.equals(other.name_en))
			return false;
		return true;
	}

}
