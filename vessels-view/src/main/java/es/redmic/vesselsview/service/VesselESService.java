package es.redmic.vesselsview.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.models.es.common.dto.EventApplicationResult;
import es.redmic.models.es.common.query.dto.MetadataQueryDTO;
import es.redmic.vesselslib.dto.VesselDTO;
import es.redmic.vesselsview.model.Vessel;
import es.redmic.vesselsview.model.VesselType;
import es.redmic.vesselsview.repository.VesselESRepository;
import es.redmic.viewlib.data.service.RWDataService;

@Service
public class VesselESService extends RWDataService<Vessel, VesselDTO, MetadataQueryDTO> {

	VesselESRepository repository;

	@Autowired
	public VesselESService(VesselESRepository repository) {
		super(repository);
		this.repository = repository;
	}

	public EventApplicationResult updateVesselTypeInVessel(String vesselId, VesselType model) {
		return repository.updateVesselTypeInVessel(vesselId, model);
	}
}
