package dev.ludwing.mobileappws.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import dev.ludwing.mobileappws.shared.dto.UserDto;

/**
 * Esta interfaz define los métodos de lógica de negocio y/o
 * de interacción con la capa de datos.
 * 
 * Esta clase hereda de UserDetailsService para tener disponibles las
 * características de seguridad que provee Spring security. 
 * 
 * @author ludwingp
 *
 */
public interface UserService  extends UserDetailsService{

	UserDto createUser(UserDto user);
	UserDto getUser(String email);
	UserDto getUserByUserId(String userId);
	UserDto updateUser(String userid, UserDto user);
	void deleteUser(String userid);
	List<UserDto> getUsers(int page, int limit);
}
