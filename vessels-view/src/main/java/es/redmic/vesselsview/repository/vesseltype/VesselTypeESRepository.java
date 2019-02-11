package es.redmic.vesselsview.repository.vesseltype;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.search.MultiSearchRequest;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.MultiSearchResponse.Item;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Repository;

import es.redmic.elasticsearchlib.data.repository.RWDataESRepository;
import es.redmic.exception.common.ExceptionType;
import es.redmic.exception.elasticsearch.ESQueryException;
import es.redmic.models.es.common.dto.EventApplicationResult;
import es.redmic.models.es.common.query.dto.SimpleQueryDTO;
import es.redmic.vesselsview.model.vesseltype.VesselType;
import es.redmic.viewlib.data.repository.IDataRepository;

@Repository
public class VesselTypeESRepository extends RWDataESRepository<VesselType, SimpleQueryDTO>
		implements IDataRepository<VesselType, SimpleQueryDTO> {

	private static String[] INDEX = { "platform-domains" };
	private static String TYPE = "vesseltype";

	// @formatter:off
	 
		private final String ID_PROPERTY = "id",
				CODE_PROPERTY = "code";
	// @formatter:on

	public VesselTypeESRepository() {
		super(INDEX, TYPE);
	}

	@Override
	protected EventApplicationResult checkInsertConstraintsFulfilled(VesselType modelToIndex) {
		// @formatter:off

		QueryBuilder idTerm = QueryBuilders.termQuery(ID_PROPERTY, modelToIndex.getId()),
				codeTerm = QueryBuilders.termQuery(CODE_PROPERTY, modelToIndex.getCode());
		
		MultiSearchRequest request = new MultiSearchRequest();
		
		SearchSourceBuilder requestBuilderId = new SearchSourceBuilder().query(idTerm).size(1),
			requestBuilderCode = new SearchSourceBuilder().query(codeTerm).size(1);

		request
			.add(new SearchRequest().indices(getIndex()).source(requestBuilderId))
			.add(new SearchRequest().indices(getIndex()).source(requestBuilderCode));

		// @formatter:on

		MultiSearchResponse sr;
		try {
			sr = ESProvider.getClient().msearch(request, RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ESQueryException();
		}

		Map<String, String> arguments = new HashMap<>();

		Item[] responses = sr.getResponses();

		if (responses != null && responses[0].getResponse().getHits().getTotalHits() > 0) {
			arguments.put(ID_PROPERTY, modelToIndex.getId());
		}

		if (responses != null && responses[1].getResponse().getHits().getTotalHits() > 0) {
			arguments.put(CODE_PROPERTY, modelToIndex.getCode().toString());
		}

		if (arguments.size() > 0) {
			return new EventApplicationResult(ExceptionType.ES_INSERT_DOCUMENT.toString(), arguments);
		}

		return new EventApplicationResult(true);
	}

	@Override
	protected EventApplicationResult checkUpdateConstraintsFulfilled(VesselType modelToIndex) {
		// @formatter:off

		BoolQueryBuilder codeTerm = QueryBuilders.boolQuery()
				.must(QueryBuilders.termQuery(CODE_PROPERTY, modelToIndex.getCode()))
				.mustNot(QueryBuilders.termQuery(ID_PROPERTY, modelToIndex.getId()));
		
		MultiSearchRequest request = new MultiSearchRequest();
		
		SearchSourceBuilder requestBuilderCode = new SearchSourceBuilder().query(codeTerm).size(1);

		request.add(new SearchRequest().indices(getIndex()).source(requestBuilderCode));

		// @formatter:on

		MultiSearchResponse sr;
		try {
			sr = ESProvider.getClient().msearch(request, RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ESQueryException();
		}

		Map<String, String> arguments = new HashMap<>();

		Item[] responses = sr.getResponses();

		if (responses != null && responses[0].getResponse().getHits().getTotalHits() > 0) {
			arguments.put(CODE_PROPERTY, modelToIndex.getCode().toString());
		}

		if (arguments.size() > 0) {
			return new EventApplicationResult(ExceptionType.ES_UPDATE_DOCUMENT.toString(), arguments);
		}

		return new EventApplicationResult(true);
	}

	@Override
	protected EventApplicationResult checkDeleteConstraintsFulfilled(String modelToIndexId) {
		return new EventApplicationResult(true);
	}
}
