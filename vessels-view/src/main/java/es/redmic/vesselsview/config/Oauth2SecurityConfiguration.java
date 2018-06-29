package es.redmic.vesselsview.config;

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
			
			http.authorizeRequests().antMatchers(HttpMethod.GET, "/actuator/**").permitAll();
			
			http.authorizeRequests().antMatchers(HttpMethod.GET, "/**").permitAll();
			
			http.authorizeRequests().antMatchers(HttpMethod.POST, "/**/_search").permitAll();
			
			http.authorizeRequests().antMatchers(HttpMethod.POST, "/**/_mget").permitAll();
			
			http.authorizeRequests().antMatchers(HttpMethod.POST, "/**/_suggest").permitAll();
			
			http.authorizeRequests().antMatchers("/**/_selection/**").permitAll();
			
			http.authorizeRequests().antMatchers(HttpMethod.GET, "/**/_search/_schema").permitAll();
			
			http.authorizeRequests().antMatchers("/**").access(
					"#oauth2.hasScope('read') and hasAnyRole('ROLE_ADMINISTRATOR', 'ROLE_OAG', 'ROLE_COLLABORATOR')");
		}
	}
}