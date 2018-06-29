package es.redmic.vesselsview.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.models.es.common.query.dto.SimpleQueryDTO;
import es.redmic.vesselslib.dto.VesselTypeDTO;
import es.redmic.vesselsview.model.VesselType;
import es.redmic.vesselsview.repository.VesselTypeESRepository;
import es.redmic.viewlib.data.service.RWDataService;

@Service
public class VesselTypeESService extends RWDataService<VesselType, VesselTypeDTO, SimpleQueryDTO> {

	@Autowired
	public VesselTypeESService(VesselTypeESRepository repository) {
		super(repository);
	}
}