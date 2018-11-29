package es.redmic.vesselslib.utils;

import org.geotools.geometry.jts.JTSFactoryFinder;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import es.redmic.exception.databinding.FieldNotValidException;
import es.redmic.vesselslib.dto.ais.AISTrackingDTO;
import es.redmic.vesselslib.dto.tracking.VesselTrackingDTO;
import es.redmic.vesselslib.dto.tracking.VesselTrackingPropertiesDTO;

public class VesselTrackingUtil {

	private static final String PREFIX = "vesseltracking-mmsi-tstamp-";
	public static final String UUID_DEFAULT = "not-processed";

	public static String generateId(Integer mmsi, Long timeStamp) {
		return PREFIX + mmsi + "-" + timeStamp;
	}

	public static VesselTrackingDTO convertTrackToVesselTracking(AISTrackingDTO aisTracking, String qFlagDefault,
			String vFlagDefault, String activityId) {

		if (aisTracking.getMmsi() == null)
			throw new FieldNotValidException("mmsi", "null");

		if (aisTracking.getTstamp() == null)
			throw new FieldNotValidException("date", "null");

		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();

		VesselTrackingDTO vesselTracking = new VesselTrackingDTO();

		Point geometry = geometryFactory
				.createPoint(new Coordinate(aisTracking.getLongitude(), aisTracking.getLatitude()));

		vesselTracking.setGeometry(geometry);

		VesselTrackingPropertiesDTO properties = new VesselTrackingPropertiesDTO();

		properties.setVessel(VesselUtil.convertTrackToVessel(aisTracking));

		properties.setDate(aisTracking.getTstamp());

		properties.setCog(aisTracking.getCog());
		properties.setSog(aisTracking.getSog());
		properties.setHeading(aisTracking.getHeading());
		properties.setNavStat(aisTracking.getNavStat());
		properties.setDest(aisTracking.getDest());
		properties.setEta(aisTracking.getEta());
		properties.setQFlag(qFlagDefault);
		properties.setVFlag(vFlagDefault);
		properties.setActivity(activityId);

		vesselTracking.setProperties(properties);
		vesselTracking.setUuid(UUID_DEFAULT);
		vesselTracking.setId(generateId(aisTracking.getMmsi(), aisTracking.getTstamp().getMillis()));

		return vesselTracking;
	}
}
