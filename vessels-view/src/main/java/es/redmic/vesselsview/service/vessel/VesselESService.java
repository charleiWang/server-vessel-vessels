package es.redmic.vesselsview.service.vessel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.models.es.common.dto.EventApplicationResult;
import es.redmic.models.es.common.query.dto.MetadataQueryDTO;
import es.redmic.vesselslib.dto.vessel.VesselDTO;
import es.redmic.vesselsview.model.vessel.Vessel;
import es.redmic.vesselsview.model.vesseltype.VesselType;
import es.redmic.vesselsview.repository.vessel.VesselESRepository;
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
