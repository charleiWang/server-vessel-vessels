package es.redmic.vesselslib.utils;

public class VesselUtil {

	private static final String PREFIX = "vessel-mmsi-";

	public static String generateId(Integer mmsi) {
		return PREFIX + mmsi;
	}
}
