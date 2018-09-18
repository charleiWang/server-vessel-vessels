package es.redmic.vesselsview.model.vesseltracking;

import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import es.redmic.brokerlib.deserializer.CustomDateTimeDeserializer;
import es.redmic.brokerlib.serializer.CustomDateTimeSerializer;
import es.redmic.models.es.geojson.base.Properties;
import es.redmic.vesselsview.model.vessel.Vessel;

public class VesselTrackingProperties extends Properties {

	public VesselTrackingProperties() {
		super();
	}

	private Vessel vessel;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@JsonDeserialize(using = CustomDateTimeDeserializer.class)
	private DateTime date;

	private Double cog;

	private Double sog;

	private Integer heading;

	private Integer navStat;

	private String dest;

	private String eta;

	public Vessel getVessel() {
		return vessel;
	}

	public void setVessel(Vessel vessel) {
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
}