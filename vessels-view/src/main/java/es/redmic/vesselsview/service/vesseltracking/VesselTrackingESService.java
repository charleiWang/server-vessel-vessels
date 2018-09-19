package es.redmic.vesselsview.service.vesseltracking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.models.es.common.dto.EventApplicationResult;
import es.redmic.models.es.common.query.dto.DataQueryDTO;
import es.redmic.vesselslib.dto.tracking.VesselTrackingDTO;
import es.redmic.vesselsview.model.vessel.Vessel;
import es.redmic.vesselsview.model.vesseltracking.VesselTracking;
import es.redmic.vesselsview.repository.vesseltracking.VesselTrackingESRepository;
import es.redmic.viewlib.geodata.service.RWGeoDataService;

@Service
public class VesselTrackingESService extends RWGeoDataService<VesselTracking, VesselTrackingDTO, DataQueryDTO> {

	VesselTrackingESRepository repository;

	@Autowired
	public VesselTrackingESService(VesselTrackingESRepository repository) {
		super(repository);
		this.repository = repository;
	}

	public EventApplicationResult updateVesselInVesselTracking(String vesselTrackingId, Vessel model) {
		return repository.updateVesselTypeInVessel(vesselTrackingId, model);
	}

	@Override
	protected String[] getDefaultSearchFields() {
		// @formatter:off
		return new String[] {
			"properties.dest",
			"properties.vessel.name",
			"properties.vessel.mmsi",
			"properties.vessel.imo",
			"properties.vessel.type.name",
			"properties.vessel.type.name_en",
			
			"properties.dest.suggest",
			"properties.vessel.name.suggest",
			"properties.vessel.mmsi.suggest",
			"properties.vessel.imo.suggest",
			"properties.vessel.type.name.suggest",
			"properties.vessel.type.name_en.suggest"
		};
		// @formatter:on
	}

	@Override
	protected String[] getDefaultHighlightFields() {
		// @formatter:off
		return new String[] {
			"properties.dest.suggest",
			"properties.vessel.name.suggest",
			"properties.vessel.mmsi.suggest",
			"properties.vessel.imo.suggest",
			"properties.vessel.type.name.suggest",
			"properties.vessel.type.name_en.suggest"
		};
		// @formatter:on
	}

	@Override
	protected String[] getDefaultSuggestFields() {
		// @formatter:off
		return new String[] {
			"properties.dest",
			"properties.vessel.name",
			"properties.vessel.mmsi",
			"properties.vessel.imo",
			"properties.vessel.type.name",
			"properties.vessel.type.name_en"
		};
		// @formatter:on
	}
}
