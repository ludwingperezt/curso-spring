package dev.ludwing.mobileappws.service.impl;

import org.springframework.stereotype.Service;

import dev.ludwing.mobileappws.service.UserService;
import dev.ludwing.mobileappws.shared.dto.UserDto;

/**
 * Clase que implementa la interfaz UserService para acceso a datos. 
 * @author ludwingp
 *
 */
// La anotación @Service sirve para marcar esta implementación como un 
// servicio y que pueda ser usada en el controller a través de la propiedad
// anotada @Autowired
@Service
public class UserServiceImpl implements UserService {

	@Override
	public UserDto createUser(UserDto user) {
		// TODO Auto-generated method stub
		return null;
	}

}
