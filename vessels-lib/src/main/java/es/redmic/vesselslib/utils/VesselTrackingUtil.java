package es.redmic.vesselslib.utils;

public class VesselTrackingUtil {

	private static final String PREFIX = "vesseltracking-mmsi-tstamp-";

	public static String generateId(Integer mmsi, Long timeStamp) {
		return PREFIX + mmsi + "-" + timeStamp;
	}
}
