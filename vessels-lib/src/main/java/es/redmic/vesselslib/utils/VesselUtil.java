package es.redmic.vesselslib.utils;

import es.redmic.vesselslib.dto.ais.AISTrackingDTO;
import es.redmic.vesselslib.dto.vessel.VesselDTO;

public class VesselUtil {

	private static final String PREFIX = "vessel-mmsi-";

	public static String generateId(Integer mmsi) {
		return PREFIX + mmsi;
	}

	public static VesselDTO convertTrackToVessel(AISTrackingDTO aisTracking) {

		VesselDTO vessel = new VesselDTO();
		vessel.setId(generateId(aisTracking.getMmsi()));
		vessel.setMmsi(aisTracking.getMmsi());
		vessel.setName(aisTracking.getName());
		vessel.setCallSign(aisTracking.getCallSign());
		vessel.setImo(aisTracking.getImo());

		if (aisTracking.getA() != null && aisTracking.getB() != null)
			vessel.setLength(aisTracking.getA() + aisTracking.getB());

		if (aisTracking.getC() != null && aisTracking.getD() != null)
			vessel.setBeam(aisTracking.getC() + aisTracking.getD());

		vessel.setType(VesselTypeUtil.convertTrackToVesselType(aisTracking));

		return vessel;
	}
}