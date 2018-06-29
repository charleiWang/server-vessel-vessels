package es.redmic.vesselsview.mapper;

import org.springframework.stereotype.Component;

import es.redmic.vesselslib.dto.VesselDTO;
import es.redmic.vesselsview.model.Vessel;
import es.redmic.vesselsview.model.VesselType;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class VesselESMapper extends CustomMapper<Vessel, VesselDTO> {

	@Override
	public void mapBtoA(VesselDTO b, Vessel a, MappingContext context) {

		a.setType(mapperFacade.map(b.getType(), VesselType.class));
	}
}
