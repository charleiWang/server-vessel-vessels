package es.redmic.vesselsview.model.vessel;

import es.redmic.models.es.common.model.BaseUpdatableES;
import es.redmic.vesselsview.model.vesseltype.VesselType;

public class Vessel extends BaseUpdatableES {

	private Integer mmsi;

	private Integer imo;

	private VesselType type;

	private String name;

	private String callSign;

	private Double length;

	private Double beam;

	public Vessel() {

	}

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
		this.imo = imo;
	}

	public VesselType getType() {
		return type;
	}

	public void setType(VesselType type) {
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
}