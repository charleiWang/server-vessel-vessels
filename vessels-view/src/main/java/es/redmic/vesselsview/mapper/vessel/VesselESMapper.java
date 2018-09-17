package es.redmic.vesselsview.mapper.vessel;

import org.springframework.stereotype.Component;

import es.redmic.vesselslib.dto.vessel.VesselDTO;
import es.redmic.vesselsview.model.vessel.Vessel;
import es.redmic.vesselsview.model.vesseltype.VesselType;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class VesselESMapper extends CustomMapper<Vessel, VesselDTO> {

	@Override
	public void mapBtoA(VesselDTO b, Vessel a, MappingContext context) {

		a.setType(mapperFacade.map(b.getType(), VesselType.class));
	}
}
