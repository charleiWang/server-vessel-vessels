package es.redmic.vesselsview.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.models.es.common.query.dto.MetadataQueryDTO;
import es.redmic.vesselslib.dto.VesselDTO;
import es.redmic.vesselsview.model.Vessel;
import es.redmic.vesselsview.repository.VesselESRepository;
import es.redmic.viewlib.data.service.RWDataService;

@Service
public class VesselESService extends RWDataService<Vessel, VesselDTO, MetadataQueryDTO> {

	@Autowired
	public VesselESService(VesselESRepository repository) {
		super(repository);
	}
}
