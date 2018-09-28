package es.redmic.vesselsview.mapper.vessel;

import org.springframework.stereotype.Component;

import es.redmic.vesselslib.dto.vessel.VesselDTO;
import es.redmic.vesselsview.model.vessel.Vessel;
import ma.glasnost.orika.CustomMapper;

@Component
public class VesselESMapper extends CustomMapper<Vessel, VesselDTO> {
}
