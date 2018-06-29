package es.redmic.vesselscommands.config;

import org.springframework.stereotype.Service;

import es.redmic.restlib.common.service.UserUtilsServiceItfc;
import es.redmic.restlib.config.UserBaseService;

@Service
public class UserService extends UserBaseService implements UserUtilsServiceItfc {

	/*
	 * Implementa la interfaz para obtener información de usuarios
	 */

	public UserService() {
		super();
	}
}
