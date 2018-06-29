package es.redmic.vesselscommands.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

import es.redmic.restlib.config.ResourceServerConfigurationBase;

@Configuration
public class Oauth2SecurityConfiguration {

	@Configuration
	@EnableResourceServer
	protected static class ResourceServerConfiguration extends ResourceServerConfigurationBase {

		@Override
		public void configure(HttpSecurity http) throws Exception {
			// @formatter:off
			
			http.anonymous().and().authorizeRequests()
				.antMatchers(HttpMethod.GET, "/actuator/**").permitAll();
			
			http.authorizeRequests().antMatchers(HttpMethod.POST, "/**").access(
					"#oauth2.hasScope('write') and "
					+ "hasAnyRole('ROLE_ADMINISTRATOR', 'ROLE_OAG', 'ROLE_COLLABORATOR')");
			
			http.authorizeRequests().antMatchers(HttpMethod.PUT, "/**").access(
					"#oauth2.hasScope('write') and hasAnyRole('ROLE_ADMINISTRATOR', 'ROLE_OAG', 'ROLE_COLLABORATOR')");
			
			http.authorizeRequests().antMatchers(HttpMethod.DELETE, "/**").access(
					"#oauth2.hasScope('write') and "
					+ "hasAnyRole('ROLE_ADMINISTRATOR', 'ROLE_OAG', 'ROLE_COLLABORATOR')");
		}
	}
}