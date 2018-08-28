package es.redmic.vesselscommands.streams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.Consumed;
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
import es.redmic.commandslib.streaming.common.StreamConfig;
import es.redmic.commandslib.streaming.streams.EventSourcingStreams;
import es.redmic.vesselslib.dto.vessel.VesselDTO;
import es.redmic.vesselslib.dto.vesseltype.VesselTypeDTO;
import es.redmic.vesselslib.events.vessel.VesselEventFactory;
import es.redmic.vesselslib.events.vessel.VesselEventTypes;
import es.redmic.vesselslib.events.vessel.common.VesselEvent;
import es.redmic.vesselslib.events.vessel.partialupdate.vesseltype.AggregationVesselTypeInVesselPostUpdateEvent;
import es.redmic.vesselslib.events.vessel.partialupdate.vesseltype.UpdateVesselTypeInVesselEvent;
import es.redmic.vesselslib.events.vesseltype.VesselTypeEventTypes;
import es.redmic.vesselslib.events.vesseltype.common.VesselTypeEvent;

public class VesselEventStreams extends EventSourcingStreams {

	private String vesselTypeTopic;

	private String vesselsAggByVesselTypeTopic;

	private HashMapSerde<String, AggregationVesselTypeInVesselPostUpdateEvent> hashMapSerde;

	private GlobalKTable<String, HashMap<String, AggregationVesselTypeInVesselPostUpdateEvent>> aggByVesselType;

	private KStream<String, Event> vesselTypeEvents;

	public VesselEventStreams(StreamConfig config, String vesselTypeTopic, String vesselsAggByVesselTypeTopic,
			AlertService alertService) {
		super(config, alertService);
		this.vesselTypeTopic = vesselTypeTopic;
		this.vesselsAggByVesselTypeTopic = vesselsAggByVesselTypeTopic;
		this.hashMapSerde = new HashMapSerde<>(schemaRegistry);

		logger.info("Arrancado servicio de streaming para event sourcing de Vessel con Id: " + this.serviceId);
		init();
	}

	/*
	 * Crea GlobalKTable de vessels agregados por vesseltype
	 * 
	 * @see es.redmic.commandslib.streaming.streams.EventSourcingStreams#
	 * createExtraStreams()
	 */
	@Override
	protected void createExtraStreams() {

		// Crea un store global para procesar los datos de todas las instancias de
		// vessels agregados por vesselType
		aggByVesselType = builder.globalTable(vesselsAggByVesselTypeTopic,
				Consumed.with(Serdes.String(), hashMapSerde));

		vesselTypeEvents = builder.stream(vesselTypeTopic);
	}

	/*
	 * Función que a partir del evento de confirmación de la vista y del evento
	 * create (petición de creación), si todo es correcto, genera evento created
	 */

	@Override
	protected Event getCreatedEvent(Event confirmedEvent, Event requestEvent) {

		assert requestEvent.getType().equals(VesselEventTypes.CREATE);

		assert confirmedEvent.getType().equals(VesselEventTypes.CREATE_CONFIRMED);

		if (!isSameSession(confirmedEvent, requestEvent)) {
			return null;
		}

		VesselDTO vessel = ((VesselEvent) requestEvent).getVessel();

		return VesselEventFactory.getEvent(confirmedEvent, VesselEventTypes.CREATED, vessel);
	}

	/*
	 * Función que a partir del evento de confirmación de la vista y del evento
	 * update (petición de modificación), si todo es correcto, genera evento updated
	 */

	@Override
	protected Event getUpdatedEvent(Event confirmedEvent, Event requestEvent) {

		assert requestEvent.getType().equals(VesselEventTypes.UPDATE);

		assert confirmedEvent.getType().equals(VesselEventTypes.UPDATE_CONFIRMED);

		if (!isSameSession(confirmedEvent, requestEvent)) {
			return null;
		}

		VesselDTO vessel = ((VesselEvent) requestEvent).getVessel();

		return VesselEventFactory.getEvent(confirmedEvent, VesselEventTypes.UPDATED, vessel);
	}

	/*
	 * Comprueba si vessel está referenciado en tracking para cancelar el borrado
	 */
	@Override
	protected void processDeleteStream(KStream<String, Event> events) {
		// TODO: Implementar en relación a tracking
	}

	/*
	 * Función que a partir del último evento correcto + el evento de edición
	 * parcial + la confirmación de la vista, envía evento modificado.
	 */
	@Override
	protected void processPartialUpdatedStream(KStream<String, Event> vesselEvents,
			KStream<String, Event> updateConfirmedEvents) {

		// Stream filtrado por eventos de petición de modificar vesseltype
		KStream<String, Event> updateRequestEvents = vesselEvents
				.filter((id, event) -> (VesselEventTypes.UPDATE_VESSELTYPE.equals(event.getType())));

		// Join por id, mandando a kafka el evento de éxito
		KStream<String, UpdateVesselTypeInVesselEvent> partialUpdateEvent = updateConfirmedEvents.join(
				updateRequestEvents,
				(confirmedEvent, requestEvent) -> isSameSession(confirmedEvent, requestEvent)
						? (UpdateVesselTypeInVesselEvent) requestEvent
						: null,
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

	/*
	 * Función que a partir del último evento correcto + el evento de edición
	 * parcial + la confirmación de la vista, si todo es correcto, genera evento
	 * updated
	 */

	private Event getUpdatedEventFromPartialUpdate(UpdateVesselTypeInVesselEvent partialUpdateConfirmEvent,
			Event lastSuccessEvent) {

		assert (lastSuccessEvent.getType().equals(VesselEventTypes.CREATED)
				|| lastSuccessEvent.getType().equals(VesselEventTypes.UPDATED));

		assert partialUpdateConfirmEvent.getType().equals(VesselEventTypes.UPDATE_VESSELTYPE);

		VesselDTO vessel = ((VesselEvent) lastSuccessEvent).getVessel();
		vessel.setType(partialUpdateConfirmEvent.getVesselType());

		return VesselEventFactory.getEvent(partialUpdateConfirmEvent, VesselEventTypes.UPDATED, vessel);
	}

	/*
	 * Función que a partir del evento fallido y el último evento correcto, genera
	 * evento UpdateCancelled
	 */

	@Override
	protected Event getUpdateCancelledEvent(Event failedEvent, Event lastSuccessEvent) {

		assert lastSuccessEvent.getType().equals(VesselEventTypes.CREATED)
				|| lastSuccessEvent.getType().equals(VesselEventTypes.UPDATED);

		assert failedEvent.getType().equals(VesselEventTypes.UPDATE_FAILED);

		VesselDTO vessel = ((VesselEvent) lastSuccessEvent).getVessel();

		EventError eventError = (EventError) failedEvent;

		alertService.errorAlert("UpdateVesselCancelledEvent para: " + failedEvent.getAggregateId(),
				eventError.getExceptionType() + " " + eventError.getArguments());

		return VesselEventFactory.getEvent(failedEvent, VesselEventTypes.UPDATE_CANCELLED, vessel,
				eventError.getExceptionType(), eventError.getArguments());
	}

	/*
	 * Función que a partir del evento fallido y el último evento correcto, genera
	 * evento DeleteFailed
	 */

	@Override
	protected Event getDeleteCancelledEvent(Event failedEvent, Event lastSuccessEvent) {

		assert lastSuccessEvent.getType().equals(VesselEventTypes.CREATED)
				|| lastSuccessEvent.getType().equals(VesselEventTypes.UPDATED);

		assert failedEvent.getType().equals(VesselEventTypes.DELETE_FAILED);

		VesselDTO vessel = ((VesselEvent) lastSuccessEvent).getVessel();

		EventError eventError = (EventError) failedEvent;

		return VesselEventFactory.getEvent(failedEvent, VesselEventTypes.DELETE_CANCELLED, vessel,
				eventError.getExceptionType(), eventError.getArguments());
	}

	/*
	 * Función para procesar modificaciones de referencias
	 */

	@Override
	protected void processPostUpdateStream(KStream<String, Event> vesselEvents) {

		KStream<String, Event> vesselEventsStream = vesselEvents.filter((id, event) -> {
			return (event instanceof VesselEvent);
		}).selectKey((k, v) -> getVesselTypeIdFromVessel(v));

		// Para cada una de las referencias

		// Agregar por vesseltype
		aggregateVesselsByVesselType(vesselEventsStream);

		// processar los vesseltype modificados
		processVesselTypePostUpdate();
	}

	private String getVesselTypeIdFromVessel(Event evt) {

		return ((VesselEvent) evt).getVessel().getType().getId();
	}

	private void aggregateVesselsByVesselType(KStream<String, Event> vesselEventsStream) {

		vesselEventsStream.groupByKey()
				.aggregate(HashMap<String, AggregationVesselTypeInVesselPostUpdateEvent>::new,
						(k, v, map) -> aggregateVesselsByVesselType(k, v, map),
						Materialized.with(Serdes.String(), hashMapSerde))
				.toStream().to(vesselsAggByVesselTypeTopic, Produced.with(Serdes.String(), hashMapSerde));
	}

	private void processVesselTypePostUpdate() {

		// Vesseltypes modificados
		KStream<String, Event> updateReferenceEvents = vesselTypeEvents
				.filter((id, event) -> (VesselTypeEventTypes.UPDATED.equals(event.getType())));

		KStream<String, ArrayList<UpdateVesselTypeInVesselEvent>> join = updateReferenceEvents.join(aggByVesselType,
				(k, v) -> k,
				(updateReferenceEvent, vesselWithReferenceEvents) -> getPostUpdateEvent(updateReferenceEvent,
						vesselWithReferenceEvents));

		// desagregar, cambiar clave por la de vessel y enviar a topic
		join.flatMapValues(value -> value).selectKey((k, v) -> v.getAggregateId()).to(topic);
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

		VesselTypeDTO vesselType = ((VesselTypeEvent) updateReferenceEvent).getVesselType();

		for (Map.Entry<String, AggregationVesselTypeInVesselPostUpdateEvent> entry : vesselWithReferenceEvents
				.entrySet()) {

			AggregationVesselTypeInVesselPostUpdateEvent aggregationEvent = entry.getValue();

			if (VesselEventTypes.isLocked(aggregationEvent.getType())) {

				if (!aggregationEvent.getType().equals(VesselEventTypes.DELETED)) {
					String message = "Item con id " + aggregationEvent.getAggregateId()
							+ " se encuentra en mitad de un ciclo de creación o edición, por lo que no se modificó la referencia "
							+ updateReferenceEvent.getAggregateId();

					logger.info(message);
					alertService.errorAlert(aggregationEvent.getAggregateId(), message);
				}

			} else if (!aggregationEvent.getVesselType().equals(vesselType)) {

				result.add((UpdateVesselTypeInVesselEvent) VesselEventFactory.getEvent(aggregationEvent,
						updateReferenceEvent, VesselEventTypes.UPDATE_VESSELTYPE));

			} else {
				logger.debug("VesselType ya estaba actualizado o los campos indexados no han cambiado ");
			}
		}
		return result;
	}
}
