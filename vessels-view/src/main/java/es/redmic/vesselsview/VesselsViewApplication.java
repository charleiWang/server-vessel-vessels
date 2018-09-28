package es.redmic.vesselsview;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import com.bedatadriven.jackson.datatype.jts.JtsModule;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import es.redmic.models.es.common.view.QueryDTODeserializerModifier;
import es.redmic.restlib.common.service.UserUtilsServiceItfc;
import es.redmic.restlib.config.ResourceBundleMessageSource;
import es.redmic.viewlib.common.querymanagement.QueryDTOMessageConverter;

@SpringBootApplication
@ComponentScan({ "es.redmic.vesselsview", "es.redmic.viewlib.common.mapper.es2dto", "es.redmic.elasticsearchlib",
		"es.redmic.restlib" })
public class VesselsViewApplication {

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	UserUtilsServiceItfc userService;

	public static void main(String[] args) {
		SpringApplication.run(VesselsViewApplication.class, args);
	}

	@Bean
	public MessageSource messageSource() {

		return new ResourceBundleMessageSource();
	}

	@Bean
	public Module jtsModule() {
		return new JtsModule();
	}

	@Bean
	public SimpleModule queryDTOJsonViewModule() {

		return new SimpleModule().setDeserializerModifier(new QueryDTODeserializerModifier());
	}

	@Bean
	public QueryDTOMessageConverter queryDTOMessageConverter() {

		FilterProvider filters = new SimpleFilterProvider().setFailOnUnknownId(false).addFilter("DataQueryDTO",
				SimpleBeanPropertyFilter.serializeAll());
		objectMapper.setFilterProvider(filters);

		return new QueryDTOMessageConverter(objectMapper, userService);
	}
}
