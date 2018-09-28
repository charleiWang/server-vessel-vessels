package es.redmic.vesselsview.mapper.vesseltracking;

import org.springframework.stereotype.Component;

import es.redmic.vesselslib.dto.tracking.VesselTrackingDTO;
import es.redmic.vesselsview.model.vesseltracking.VesselTracking;
import ma.glasnost.orika.CustomMapper;

@Component
public class VesselTrackingESMapper extends CustomMapper<VesselTracking, VesselTrackingDTO> {

}