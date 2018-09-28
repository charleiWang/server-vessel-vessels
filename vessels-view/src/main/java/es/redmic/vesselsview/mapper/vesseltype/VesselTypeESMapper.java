package es.redmic.vesselsview.mapper.vesseltype;

import org.springframework.stereotype.Component;

import es.redmic.vesselslib.dto.vesseltype.VesselTypeDTO;
import es.redmic.vesselsview.model.vesseltype.VesselType;
import ma.glasnost.orika.CustomMapper;

@Component
public class VesselTypeESMapper extends CustomMapper<VesselType, VesselTypeDTO> {
}
