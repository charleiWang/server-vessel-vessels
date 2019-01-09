package es.redmic.vesselslib.utils;

import es.redmic.vesselslib.dto.ais.AISTrackingDTO;
import es.redmic.vesselslib.dto.vesseltype.VesselTypeDTO;

public class VesselTypeUtil {

	private static final String PREFIX = "vesseltype-code-";

	public static String generateId(String code) {
		return PREFIX + code;
	}

	public static VesselTypeDTO convertTrackToVesselType(AISTrackingDTO aisTracking) {

		VesselTypeDTO vesselType = new VesselTypeDTO();
		vesselType.setId(generateId(aisTracking.getType().toString()));
		vesselType.setCode(aisTracking.getType().toString());

		return vesselType;
	}
}