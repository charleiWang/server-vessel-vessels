package es.redmic.vesselsview.model.vesseltracking;

import org.locationtech.jts.geom.Point;

import es.redmic.models.es.geojson.GeoJSONFeatureType;
import es.redmic.models.es.geojson.base.Feature;;

public class VesselTracking extends Feature<VesselTrackingProperties, Point> {

	public VesselTracking() {
		super();
		this.setType(GeoJSONFeatureType.FEATURE);
	}
}
