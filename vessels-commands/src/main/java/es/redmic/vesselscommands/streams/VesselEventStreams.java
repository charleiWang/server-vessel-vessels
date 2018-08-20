package es.redmic.vesselscommands.streams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;

import es.redmic.brokerlib.alert.AlertService;
import es.redmic.brokerlib.avro.common.Event;
import es.redmic.brokerlib.avro.common.EventError;
import es.redmic.brokerlib.avro.serde.hashmap.HashMapSerde;
import es.redmic.commandslib.statestore.StreamConfig;
import es.redmic.commandslib.streams.EventStreams;
import es.redmic.vesselslib.dto.VesselDTO;
import es.redmic.vesselslib.dto.VesselTypeDTO;
import es.redmic.vesselslib.events.vessel.VesselEventTypes;
import es.redmic.vesselslib.events.vessel.common.VesselEvent;
import es.redmic.vesselslib.events.vessel.create.VesselCreatedEvent;
import es.redmic.vesselslib.events.vessel.delete.DeleteVesselCancelledEvent;
import es.redmic.vesselslib.events.vessel.partialupdate.vesseltype.AggregationVesselTypeInVesselPostUpdateEvent;
import es.redmic.vesselslib.events.vessel.partialupdate.vesseltype.UpdateVesselTypeInVesselEvent;
import es.redmic.vesselslib.events.vessel.update.UpdateVesselCancelledEvent;
import es.redmic.vesselslib.events.vessel.update.VesselUpdatedEvent;
import es.redmic.vesselslib.events.vesseltype.VesselTypeEventTypes;
import es.redmic.vesselslib.events.vesseltype.common.VesselTypeEvent;

public class VesselEventStreams extends EventStreams {

	private String VESSELS_AGG_BY_VESSELTYPE_TOPIC = "vesselsAggByVesselType";

	private String vesselTypeTopic;

	public VesselEventStreams(StreamConfig config, String vesselTypeTopic, AlertService alertService) {
		super(config, alertService);
		this.vesselTypeTopic = vesselTypeTopic;
		logger.info("Arrancado servicio de compensación de errores de edición y PostUpdate de Vessel con Id: "
				+ this.serviceId);
		init();
	}

	@Override
	protected void processCreatedStream(KStream<String, Event> vesselEvents) {

		// Stream filtrado por eventos de confirmación al crear
		KStream<String, Event> createConfirmedEvents = vesselEvents
				.filter((id, event) -> (VesselEventTypes.CREATE_CONFIRMED.equals(event.getType())));

		// Stream filtrado por eventos de petición de crear
		KStream<String, Event> createRequestEvents = vesselEvents
				.filter((id, event) -> (VesselEventTypes.CREATE.equals(event.getType())));

		// Join por id, mandando a kafka el evento de éxito
		createConfirmedEvents.join(createRequestEvents,
				(confirmedEvent, requestEvent) -> getCreatedEvent(confirmedEvent, requestEvent),
				JoinWindows.of(windowsTime)).to(topic);
	}

	private Event getCreatedEvent(Event confirmedEvent, Event requestEvent) {

		assert requestEvent.getType().equals(VesselEventTypes.CREATE);

		assert confirmedEvent.getType().equals(VesselEventTypes.CREATE_CONFIRMED);

		if (!isSameSession(confirmedEvent, requestEvent)) {
			return null;
		}

		VesselDTO vessel = ((VesselEvent) requestEvent).getVessel();

		logger.info("Creando evento VesselCreatedEvent para: " + confirmedEvent.getAggregateId());

		VesselCreatedEvent successfulEvent = new VesselCreatedEvent().buildFrom(confirmedEvent);
		successfulEvent.setVessel(vessel);
		return successfulEvent;
	}

	@Override
	protected void processUpdatedStream(KStream<String, Event> vesselEvents) {

		// Stream filtrado por eventos de confirmación al modificar
		KStream<String, Event> updateConfirmedEvents = vesselEvents
				.filter((id, event) -> (VesselEventTypes.UPDATE_CONFIRMED.equals(event.getType())));

		// Stream filtrado por eventos de petición de modificar
		KStream<String, Event> updateRequestEvents = vesselEvents
				.filter((id, event) -> (VesselEventTypes.UPDATE.equals(event.getType())));

		// Join por id, mandando a kafka el evento de éxito
		updateConfirmedEvents.join(updateRequestEvents,
				(confirmedEvent, requestEvent) -> getUpdatedEvent(confirmedEvent, requestEvent),
				JoinWindows.of(windowsTime)).to(topic);

		processPartialUpdatedStream(vesselEvents, updateConfirmedEvents);
	}

	private Event getUpdatedEvent(Event confirmedEvent, Event requestEvent) {

		assert requestEvent.getType().equals(VesselEventTypes.UPDATE);

		assert confirmedEvent.getType().equals(VesselEventTypes.UPDATE_CONFIRMED);

		if (!isSameSession(confirmedEvent, requestEvent)) {
			return null;
		}

		VesselDTO vessel = ((VesselEvent) requestEvent).getVessel();

		logger.info("Creando evento VesselUpdatedEvent para: " + confirmedEvent.getAggregateId());

		VesselUpdatedEvent successfulEvent = new VesselUpdatedEvent().buildFrom(confirmedEvent);
		successfulEvent.setVessel(vessel);
		return successfulEvent;
	}

	private void processPartialUpdatedStream(KStream<String, Event> vesselEvents,
			KStream<String, Event> updateConfirmedEvents) {

		// Stream filtrado por eventos de petición de modificar vesseltype
		KStream<String, Event> updateRequestEvents = vesselEvents
				.filter((id, event) -> (VesselEventTypes.UPDATE_VESSELTYPE.equals(event.getType())));

		// Join por id, mandando a kafka el evento de éxito
		KStream<String, UpdateVesselTypeInVesselEvent> partialUpdateEvent = updateConfirmedEvents.join(
				updateRequestEvents,
				(confirmedEvent, requestEvent) -> checkConfirmPartialUpdate(confirmedEvent, requestEvent),
				JoinWindows.of(windowsTime));

		// Stream filtrado por eventos de creaciones y modificaciones correctos (solo el
		// último que se produzca por id)
		KStream<String, Event> successEvents = vesselEvents
				.filter((id, event) -> (VesselEventTypes.CREATED.equals(event.getType())
						|| VesselEventTypes.UPDATED.equals(event.getType())));

		KTable<String, Event> successEventsTable = successEvents.groupByKey().reduce((aggValue, newValue) -> newValue);

		// Join por id, mandando a kafka el evento de compensación
		partialUpdateEvent.join(successEventsTable, (partialUpdateConfirmEvent,
				lastSuccessEvent) -> getUpdatedEventFromPartialUpdate(partialUpdateConfirmEvent, lastSuccessEvent))
				.to(topic);
	}

	// Comprueba si la confirmación corresponde con el evento enviado.
	private UpdateVesselTypeInVesselEvent checkConfirmPartialUpdate(Event confirmedEvent, Event requestEvent) {

		if (!isSameSession(confirmedEvent, requestEvent)) {
			return null;
		}
		return (UpdateVesselTypeInVesselEvent) requestEvent;
	}

	private Event getUpdatedEventFromPartialUpdate(UpdateVesselTypeInVesselEvent partialUpdateConfirmEvent,
			Event lastSuccessEvent) {

		assert (lastSuccessEvent.getType().equals(VesselEventTypes.CREATED)
				|| lastSuccessEvent.getType().equals(VesselEventTypes.UPDATED));

		assert partialUpdateConfirmEvent.getType().equals(VesselEventTypes.UPDATE_VESSELTYPE);

		VesselDTO vessel = ((VesselEvent) lastSuccessEvent).getVessel();
		vessel.setType(partialUpdateConfirmEvent.getVesselType());

		logger.info("Creando evento VesselUpdatedEvent por una edición parcial de vesselType para: "
				+ partialUpdateConfirmEvent.getAggregateId());

		VesselUpdatedEvent successfulEvent = new VesselUpdatedEvent().buildFrom(partialUpdateConfirmEvent);
		successfulEvent.setVessel(vessel);
		return successfulEvent;
	}

	@Override
	protected void processFailedChangeStream(KStream<String, Event> vesselEvents) {

		// Stream filtrado por eventos de creaciones y modificaciones correctos (solo el
		// último que se produzca por id)
		KStream<String, Event> successEvents = vesselEvents
				.filter((id, event) -> (VesselEventTypes.CREATED.equals(event.getType())
						|| VesselEventTypes.UPDATED.equals(event.getType())));

		processUpdateFailedStream(vesselEvents, successEvents);

		processDeleteFailedStream(vesselEvents, successEvents);
	}

	protected void processUpdateFailedStream(KStream<String, Event> vesselEvents,
			KStream<String, Event> successEvents) {

		// Stream filtrado por eventos de fallo al modificar
		KStream<String, Event> failedEvents = vesselEvents
				.filter((id, event) -> (VesselEventTypes.UPDATE_FAILED.equals(event.getType())));

		KTable<String, Event> successEventsTable = successEvents.groupByKey().reduce((aggValue, newValue) -> newValue);

		// Join por id, mandando a kafka el evento de compensación
		failedEvents
				.join(successEventsTable,
						(failedEvent, lastSuccessEvent) -> getUpdateCancelledEvent(failedEvent, lastSuccessEvent))
				.to(topic);
	}

	protected void processDeleteFailedStream(KStream<String, Event> vesselEvents,
			KStream<String, Event> successEvents) {

		// Stream filtrado por eventos de fallo al borrar
		KStream<String, Event> failedEvents = vesselEvents
				.filter((id, event) -> (VesselEventTypes.DELETE_FAILED.equals(event.getType())));

		KTable<String, Event> successEventsTable = successEvents.groupByKey().reduce((aggValue, newValue) -> newValue);

		// Join por id, mandando a kafka el evento de compensación
		failedEvents
				.join(successEventsTable,
						(failedEvent, lastSuccessEvent) -> getDeleteCancelledEvent(failedEvent, lastSuccessEvent))
				.to(topic);
	}

	private Event getUpdateCancelledEvent(Event failedEvent, Event lastSuccessEvent) {

		assert lastSuccessEvent.getType().equals(VesselEventTypes.CREATED)
				|| lastSuccessEvent.getType().equals(VesselEventTypes.UPDATED);

		assert failedEvent.getType().equals(VesselEventTypes.UPDATE_FAILED);

		VesselDTO vessel = ((VesselEvent) lastSuccessEvent).getVessel();

		EventError eventError = (EventError) failedEvent;

		logger.info("Enviando evento UpdateVesselCancelledEvent para: " + failedEvent.getAggregateId());

		alertService.errorAlert("UpdateVesselCancelledEvent para: " + failedEvent.getAggregateId(),
				eventError.getExceptionType() + " " + eventError.getArguments());

		UpdateVesselCancelledEvent cancelledEvent = new UpdateVesselCancelledEvent().buildFrom(failedEvent);
		cancelledEvent.setVessel(vessel);
		cancelledEvent.setExceptionType(eventError.getExceptionType());
		cancelledEvent.setArguments(eventError.getArguments());
		return cancelledEvent;
	}

	private Event getDeleteCancelledEvent(Event failedEvent, Event lastSuccessEvent) {

		assert lastSuccessEvent.getType().equals(VesselEventTypes.CREATED)
				|| lastSuccessEvent.getType().equals(VesselEventTypes.UPDATED);

		assert failedEvent.getType().equals(VesselEventTypes.DELETE_FAILED);

		VesselDTO vessel = ((VesselEvent) lastSuccessEvent).getVessel();

		EventError eventError = (EventError) failedEvent;

		logger.info("Enviando evento DeleteVesselCancelledEvent para: " + failedEvent.getAggregateId());

		DeleteVesselCancelledEvent cancelledEvent = new DeleteVesselCancelledEvent().buildFrom(failedEvent);
		cancelledEvent.setVessel(vessel);
		cancelledEvent.setExceptionType(eventError.getExceptionType());
		cancelledEvent.setArguments(eventError.getArguments());
		return cancelledEvent;
	}

	@Override
	protected void processPostUpdateStream(KStream<String, Event> vesselEvents) {

		// Filtra eventos que contengan Vessel dentro que son los de comienzo y final
		// del ciclo, los agrega por vesseltype en un hashmap y los envia a un topic

		HashMapSerde<String, AggregationVesselTypeInVesselPostUpdateEvent> hashMapSerde = new HashMapSerde<>(
				schemaRegistry);

		KStream<String, Event> vesselEventsStream = vesselEvents.filter((id, event) -> {
			return (event instanceof VesselEvent);
		}).selectKey((k, v) -> getVesselTypeIdFromVessel(v));

		vesselEventsStream.groupByKey()

				.aggregate(HashMap<String, AggregationVesselTypeInVesselPostUpdateEvent>::new,
						(k, v, map) -> aggregateVesselsByVesselType(k, v, map),
						Materialized.with(Serdes.String(), hashMapSerde))
				.toStream().to(VESSELS_AGG_BY_VESSELTYPE_TOPIC, Produced.valueSerde(hashMapSerde));

		// Crea un store global para procesar los datos de todas las instancias

		GlobalKTable<String, HashMap<String, AggregationVesselTypeInVesselPostUpdateEvent>> aggByVesselType = builder
				.globalTable(VESSELS_AGG_BY_VESSELTYPE_TOPIC);

		// Vesseltypes modificados
		KStream<String, Event> vesselTypeEvents = builder.stream(vesselTypeTopic);

		KStream<String, Event> updateReferenceEvents = vesselTypeEvents
				.filter((id, event) -> (VesselTypeEventTypes.UPDATED.equals(event.getType())));

		KStream<String, ArrayList<UpdateVesselTypeInVesselEvent>> join = updateReferenceEvents.join(aggByVesselType,
				(k, v) -> k,
				(updateReferenceEvent, vesselWithReferenceEvents) -> getPostUpdateEvent(updateReferenceEvent,
						vesselWithReferenceEvents));

		// desagregar, cambiar clave por la de vessel y enviar a topic
		join.flatMapValues(value -> value).selectKey((k, v) -> v.getAggregateId()).to(topic);
	}

	private String getVesselTypeIdFromVessel(Event evt) {

		return ((VesselEvent) evt).getVessel().getType().getId();
	}

	private HashMap<String, AggregationVesselTypeInVesselPostUpdateEvent> aggregateVesselsByVesselType(String key,
			Event value, HashMap<String, AggregationVesselTypeInVesselPostUpdateEvent> hashMap) {

		hashMap.put(value.getAggregateId(), new AggregationVesselTypeInVesselPostUpdateEvent(value.getType(),
				((VesselEvent) value).getVessel().getType()).buildFrom(value));
		return hashMap;
	}

	private ArrayList<UpdateVesselTypeInVesselEvent> getPostUpdateEvent(Event updateReferenceEvent,
			HashMap<String, AggregationVesselTypeInVesselPostUpdateEvent> vesselWithReferenceEvents) {

		ArrayList<UpdateVesselTypeInVesselEvent> result = new ArrayList<>();

		for (Map.Entry<String, AggregationVesselTypeInVesselPostUpdateEvent> entry : vesselWithReferenceEvents
				.entrySet()) {

			AggregationVesselTypeInVesselPostUpdateEvent aggregationEvent = entry.getValue();
			VesselTypeDTO vesselType = ((VesselTypeEvent) updateReferenceEvent).getVesselType();

			if (VesselEventTypes.isLocked(aggregationEvent.getType())) {

				if (!aggregationEvent.getType().equals(VesselEventTypes.DELETED)) {
					String message = "Item con id " + aggregationEvent.getAggregateId()
							+ " se encuentra en mitad de un ciclo de creación o edición, por lo que no se modificó la referencia "
							+ updateReferenceEvent.getAggregateId();

					logger.info(message);
					alertService.errorAlert(aggregationEvent.getAggregateId(), message);
				}

			} else {

				logger.debug("Creando evento de update para Vessel " + aggregationEvent.getAggregateId()
						+ " por cambio en vesselType");

				if (!aggregationEvent.getVesselType().equals(vesselType)) {

					UpdateVesselTypeInVesselEvent updateVesselType = new UpdateVesselTypeInVesselEvent();
					updateVesselType.setAggregateId(aggregationEvent.getAggregateId());
					updateVesselType.setUserId(updateReferenceEvent.getUserId());
					updateVesselType.setVersion(aggregationEvent.getVersion() + 1);
					updateVesselType.setVesselType(((VesselTypeEvent) updateReferenceEvent).getVesselType());
					result.add(updateVesselType);

				} else {
					logger.debug("VesselType ya estaba actualizado o los campos indexados no han cambiado ");
				}
			}
		}
		return result;
	}
}
