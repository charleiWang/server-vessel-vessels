package es.redmic.vesselslib.events.vessel;

// @formatter:off
public enum VesselEventType {
	//CREATE
	CREATE_VESSEL,
	CREATE_VESSEL_CONFIRMED,
	VESSEL_CREATED,
	CREATE_VESSEL_FAILED,
	CREATE_VESSEL_CANCELLED,
	//UPDDATE
	UPDATE_VESSEL,
	UPDATE_VESSEL_CONFIRMED,
	VESSEL_UPDATED,
	UPDATE_VESSEL_FAILED,
	UPDATE_VESSEL_CANCELLED,
	//DELETE
	DELETE_VESSEL,
	DELETE_VESSEL_CONFIRMED,
	VESSEL_DELETED,
	DELETE_VESSEL_FAILED,
	DELETE_VESSEL_CANCELLED
}
//@formatter:on