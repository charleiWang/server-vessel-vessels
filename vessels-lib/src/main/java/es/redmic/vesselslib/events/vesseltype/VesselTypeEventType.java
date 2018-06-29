package es.redmic.vesselslib.events.vesseltype;

// @formatter:off
public enum VesselTypeEventType {
	//CREATE
	CREATE_VESSELTYPE,
	CREATE_VESSELTYPE_CONFIRMED,
	VESSELTYPE_CREATED,
	CREATE_VESSELTYPE_FAILED,
	CREATE_VESSELTYPE_CANCELLED,
	//UPDATE
	UPDATE_VESSELTYPE,
	UPDATE_VESSELTYPE_CONFIRMED,
	VESSELTYPE_UPDATED,
	UPDATE_VESSELTYPE_FAILED,
	UPDATE_VESSELTYPE_CANCELLED,
	//DELETE
	DELETE_VESSELTYPE,
	DELETE_VESSELTYPE_CONFIRMED,
	VESSELTYPE_DELETED,
	DELETE_VESSELTYPE_FAILED,
	DELETE_VESSELTYPE_CANCELLED
}
//@formatter:on