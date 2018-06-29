package es.redmic.vesselsview.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import es.redmic.models.es.common.model.BaseAbstractStringES;

public class VesselType extends BaseAbstractStringES {

	private String code;

	@JsonProperty("name")
	private String name;

	@JsonProperty("name_en")
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

}