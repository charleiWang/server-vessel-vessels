package es.redmic.vesselsview.model.vesseltracking;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
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

	private String qFlag;

	private String vFlag;

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

	@JsonProperty(value = "qFlag")
	public String getQFlag() {
		return qFlag;
	}

	@JsonProperty(value = "qFlag")
	public void setQFlag(String qFlag) {
		this.qFlag = qFlag;
	}

	@JsonProperty(value = "vFlag")
	public String getVFlag() {
		return vFlag;
	}

	@JsonProperty(value = "vFlag")
	public void setVFlag(String vFlag) {
		this.vFlag = vFlag;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VesselTrackingProperties other = (VesselTrackingProperties) obj;
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