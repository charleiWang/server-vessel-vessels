package es.redmic.vesselsview.service.vesseltype;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.models.es.common.query.dto.SimpleQueryDTO;
import es.redmic.vesselslib.dto.vesseltype.VesselTypeDTO;
import es.redmic.vesselsview.model.vesseltype.VesselType;
import es.redmic.vesselsview.repository.vesseltype.VesselTypeESRepository;
import es.redmic.viewlib.data.service.RWDataService;

@Service
public class VesselTypeESService extends RWDataService<VesselType, VesselTypeDTO, SimpleQueryDTO> {

	@Autowired
	public VesselTypeESService(VesselTypeESRepository repository) {
		super(repository);
	}
}