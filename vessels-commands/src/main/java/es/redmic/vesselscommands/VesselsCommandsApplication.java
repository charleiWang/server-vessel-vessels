package es.redmic.vesselscommands;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import es.redmic.commandslib.config.GenerateJsonSchemaScanBean;
import es.redmic.restlib.config.ResourceBundleMessageSource;

@SpringBootApplication
@EnableWebMvc
@ComponentScan({ "es.redmic.vesselscommands", "es.redmic.restlib", "es.redmic.commandslib",
		"es.redmic.brokerlib.alert" })
public class VesselsCommandsApplication {

	public static void main(String[] args) {
		SpringApplication.run(VesselsCommandsApplication.class, args);
	}

	@Bean
	public MessageSource messageSource() {

		return new ResourceBundleMessageSource();
	}

	@Bean
	public GenerateJsonSchemaScanBean generateSchemaScanBean() {
		return new GenerateJsonSchemaScanBean();
	}
}