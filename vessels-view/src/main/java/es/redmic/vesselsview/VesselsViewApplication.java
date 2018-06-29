package es.redmic.vesselsview;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import es.redmic.restlib.config.ResourceBundleMessageSource;

@SpringBootApplication
@EnableWebMvc
@ComponentScan({ "es.redmic.vesselsview", "es.redmic.viewlib.common.mapper.es2dto", "es.redmic.elasticsearchlib",
		"es.redmic.restlib" })
public class VesselsViewApplication {

	public static void main(String[] args) {
		SpringApplication.run(VesselsViewApplication.class, args);
	}

	@Bean
	public MessageSource messageSource() {

		return new ResourceBundleMessageSource();
	}
}
