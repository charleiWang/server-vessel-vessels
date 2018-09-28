package es.redmic.vesselsview.mapper.vesseltracking;

import org.springframework.stereotype.Component;

import es.redmic.vesselslib.dto.tracking.VesselTrackingPropertiesDTO;
import es.redmic.vesselsview.model.vesseltracking.VesselTrackingProperties;
import ma.glasnost.orika.CustomMapper;

@Component
public class VesselTrackingPropertiesESMapper
		extends CustomMapper<VesselTrackingProperties, VesselTrackingPropertiesDTO> {
}
