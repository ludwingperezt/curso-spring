package dev.ludwing.mobileappws.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.ludwing.mobileappws.UserRepository;
import dev.ludwing.mobileappws.io.entity.UserEntity;
import dev.ludwing.mobileappws.service.UserService;
import dev.ludwing.mobileappws.shared.dto.UserDto;

/**
 * Clase que implementa la interfaz UserService, la cual define las operaciones
 * de lógica de negocio sobre las entidades de usuario.
 *  
 * @author ludwingp
 *
 */
// La anotación @Service sirve para marcar esta implementación como un 
// servicio y que pueda ser usada en el controller a través de la propiedad
// anotada @Autowired
@Service
public class UserServiceImpl implements UserService {
	
	// Inyección de la clase de repositorio de usuarios.
	@Autowired
	UserRepository userRepository;

	/**
	 * Función de servicio que ejecuta la inserción de un registro en la base de datos.
	 */
	@Override
	public UserDto createUser(UserDto user) {
		UserEntity userEntity = new UserEntity();
		BeanUtils.copyProperties(user, userEntity);
		
		// POr el momento, para algunos datos obligatorios se establecen
		// hardcoded values para que no falle la operación
		userEntity.setEncryptedPassword("una_password");
		userEntity.setUserId("test_user_ID");
		
		UserEntity storedUserDetails = userRepository.save(userEntity);
		
		// Regresar el valor recién guardado al controller
		UserDto returnValue = new UserDto();
		BeanUtils.copyProperties(storedUserDetails, returnValue);
				
		return returnValue;
	}

}
