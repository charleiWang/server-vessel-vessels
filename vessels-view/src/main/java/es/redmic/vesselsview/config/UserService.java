package es.redmic.vesselsview.config;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import es.redmic.restlib.common.service.UserUtilsServiceItfc;
import es.redmic.restlib.config.UserBaseService;

@SessionScope
@Service
public class UserService extends UserBaseService implements UserUtilsServiceItfc {

	/*
	 * Implementa la interfaz para obtener información de usuarios
	 */

	public UserService() {
		super();
	}
}
