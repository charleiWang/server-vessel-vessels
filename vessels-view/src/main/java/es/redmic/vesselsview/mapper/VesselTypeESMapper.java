package es.redmic.vesselsview.mapper;

import org.springframework.stereotype.Component;

import es.redmic.vesselslib.dto.vesseltype.VesselTypeDTO;
import es.redmic.vesselsview.model.VesselType;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class VesselTypeESMapper extends CustomMapper<VesselType, VesselTypeDTO> {

	@Override
	public void mapBtoA(VesselTypeDTO b, VesselType a, MappingContext context) {

		super.mapBtoA(b, a, context);
	}

	@Override
	public void mapAtoB(VesselType a, VesselTypeDTO b, MappingContext context) {

		super.mapAtoB(a, b, context);
	}
}
