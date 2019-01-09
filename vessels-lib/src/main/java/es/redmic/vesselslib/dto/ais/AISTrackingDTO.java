package es.redmic.vesselslib.dto.ais;

import java.util.Map;

import javax.validation.constraints.NotNull;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class AISTrackingDTO extends org.apache.avro.specific.SpecificRecordBase
		implements org.apache.avro.specific.SpecificRecord {

// @formatter:off

public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse(
	"{\"type\":\"record\",\"name\":\"AISTrackingDTO\",\"namespace\":\"es.redmic.vesselslib.dto.ais\",\"fields\":[{"
		+ "\"name\":\"mmsi\",\"type\":\"int\"},"
		+ "{\"name\":\"tstamp\",\"type\":{ \"type\":\"long\",\"logicalType\":\"timestamp-millis\"}},"
		+ "{\"name\":\"latitude\",\"type\":\"double\"},"
		+ "{\"name\":\"longitude\",\"type\":\"double\"},"
		+ "{\"name\":\"cog\",\"type\":[\"double\", \"null\"]},"
		+ "{\"name\":\"sog\",\"type\":[\"double\", \"null\"]},"
		+ "{\"name\":\"draught\",\"type\":[\"double\", \"null\"]},"
		+ "{\"name\":\"type\",\"type\":[\"int\", \"null\"]},"
		+ "{\"name\":\"a\",\"type\":[\"double\", \"null\"]},"
		+ "{\"name\":\"b\",\"type\":[\"double\", \"null\"]},"
		+ "{\"name\":\"c\",\"type\":[\"double\", \"null\"]},"
		+ "{\"name\":\"d\",\"type\":[\"double\", \"null\"]},"
		+ "{\"name\":\"imo\",\"type\":[\"int\", \"null\"]},"
		+ "{\"name\":\"heading\",\"type\":[\"int\", \"null\"]},"
		+ "{\"name\":\"navStat\",\"type\":[\"int\", \"null\"]},"
		+ "{\"name\":\"name\",\"type\":[\"string\", \"null\"]},"
		+ "{\"name\":\"dest\",\"type\":[\"string\", \"null\"]},"
		+ "{\"name\":\"callSign\",\"type\":[\"string\", \"null\"]},"
		+ "{\"name\":\"eta\",\"type\":[\"string\", \"null\"]}]}");
// @formatter:on

	@NotNull
	private Integer mmsi;

	@NotNull
	private DateTime tstamp;

	@NotNull
	private Double latitude;

	@NotNull
	private Double longitude;

	private Double cog;

	private Double sog;

	private Double draught;

	private Integer type = 0;

	private Double a;

	private Double b;

	private Double c;

	private Double d;

	private Integer imo;

	private Integer heading;

	private Integer navStat;

	private String name;

	private String dest;

	private String callSign;

	private String eta;

	public Integer getMmsi() {
		return mmsi;
	}

	public void setMmsi(Integer mmsi) {
		this.mmsi = mmsi;
	}

	public DateTime getTstamp() {
		return tstamp;
	}

	public void setTstamp(DateTime tstamp) {
		this.tstamp = tstamp;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
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

	public Double getDraught() {
		return draught;
	}

	public void setDraught(Double draught) {
		this.draught = draught;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {

		if (type == null)
			this.type = 0;
		else
			this.type = type;
	}

	public Double getA() {
		return a;
	}

	public void setA(Double a) {
		this.a = a;
	}

	public Double getB() {
		return b;
	}

	public void setB(Double b) {
		this.b = b;
	}

	public Double getC() {
		return c;
	}

	public void setC(Double c) {
		this.c = c;
	}

	public Double getD() {
		return d;
	}

	public void setD(Double d) {
		this.d = d;
	}

	public Integer getImo() {
		return imo;
	}

	public void setImo(Integer imo) {
		this.imo = imo;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDest() {
		return dest;
	}

	public void setDest(String dest) {
		this.dest = dest;
	}

	public String getCallSign() {
		return callSign;
	}

	public void setCallSign(String callSign) {
		this.callSign = callSign;
	}

	public String getEta() {
		return eta;
	}

	public void setEta(String eta) {
		this.eta = eta;
	}

	@JsonIgnore
	public void buildFromMap(Map<String, String> row) {

		this.setMmsi(parseInteger(row.get("MMSI")));
		this.setTstamp(DateTime.parse(row.get("TSTAMP"), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss zzz")));
		this.setLatitude(parseDouble(row.get("LATITUDE")));
		this.setLongitude(parseDouble(row.get("LONGITUDE")));
		this.setCog(parseDouble(row.get("COG")));
		this.setSog(parseDouble(row.get("SOG")));
		this.setDraught(parseDouble(row.get("DRAUGHT")));
		this.setType(parseInteger(row.get("TYPE")));
		this.setImo(parseInteger(row.get("IMO")));
		this.setHeading(parseInteger(row.get("HEADING")));
		this.setNavStat(parseInteger(row.get("NAVSTAT")));
		this.setName(row.get("NAME"));
		this.setDest(row.get("DEST"));
		this.setCallSign(row.get("CALLSIGN"));
		this.setEta(row.get("ETA"));
		this.setA(parseDouble(row.get("A")));
		this.setB(parseDouble(row.get("B")));
		this.setC(parseDouble(row.get("C")));
		this.setD(parseDouble(row.get("D")));
	}

	@JsonIgnore
	private Double parseDouble(String value) {

		if (value != null) {
			return Double.parseDouble(value);
		}
		return null;
	}

	@JsonIgnore
	private Integer parseInteger(String value) {

		if (value != null) {
			return Integer.parseInt(value);
		}
		return null;
	}

	@Override
	public org.apache.avro.Schema getSchema() {
		return SCHEMA$;
	}

	// Used by DatumWriter. Applications should not call.
	@Override
	public java.lang.Object get(int field$) {
		switch (field$) {
		case 0:
			return mmsi;
		case 1:
			return tstamp.getMillis();
		case 2:
			return latitude;
		case 3:
			return longitude;
		case 4:
			return cog;
		case 5:
			return sog;
		case 6:
			return draught;
		case 7:
			return type;
		case 8:
			return a;
		case 9:
			return b;
		case 10:
			return c;
		case 11:
			return d;
		case 12:
			return imo;
		case 13:
			return heading;
		case 14:
			return navStat;
		case 15:
			return name;
		case 16:
			return dest;
		case 17:
			return callSign;
		case 18:
			return eta;
		default:
			throw new org.apache.avro.AvroRuntimeException("Bad index");
		}
	}

	// Used by DatumReader. Applications should not call.
	@Override
	public void put(int field$, java.lang.Object value$) {
		switch (field$) {
		case 0:
			mmsi = (java.lang.Integer) value$;
			break;
		case 1:
			tstamp = new DateTime(value$, DateTimeZone.UTC);
			break;
		case 2:
			latitude = (java.lang.Double) value$;
			break;
		case 3:
			longitude = (java.lang.Double) value$;
			break;
		case 4:
			cog = (java.lang.Double) value$;
			break;
		case 5:
			sog = (java.lang.Double) value$;
			break;
		case 6:
			draught = (java.lang.Double) value$;
			break;
		case 7:
			type = (java.lang.Integer) value$;
			break;
		case 8:
			a = (java.lang.Double) value$;
			break;
		case 9:
			b = (java.lang.Double) value$;
			break;
		case 10:
			c = (java.lang.Double) value$;
			break;
		case 11:
			d = (java.lang.Double) value$;
			break;
		case 12:
			imo = (java.lang.Integer) value$;
			break;
		case 13:
			heading = (java.lang.Integer) value$;
			break;
		case 14:
			navStat = (java.lang.Integer) value$;
			break;
		case 15:
			name = value$ != null ? value$.toString() : null;
			break;
		case 16:
			dest = value$ != null ? value$.toString() : null;
			break;
		case 17:
			callSign = value$ != null ? value$.toString() : null;
			break;
		case 18:
			eta = value$ != null ? value$.toString() : null;
			break;
		default:
			throw new org.apache.avro.AvroRuntimeException("Bad index");
		}
	}
}
