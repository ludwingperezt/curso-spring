package dev.ludwing.mobileappws.service;

import dev.ludwing.mobileappws.shared.dto.UserDto;

/**
 * Esta interfaz define los métodos de interacción con la capa de datos.
 * @author ludwingp
 *
 */
public interface UserService {

	UserDto createUser(UserDto user);
}
