package es.redmic.vesselslib.utils;

public class VesselTypeUtil {

	private static final String PREFIX = "vesseltype-code-";

	public static String generateId(String code) {
		return PREFIX + code;
	}
}
