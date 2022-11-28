package dev.ludwing.mobileappws.service.impl;

import java.util.ArrayList;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import dev.ludwing.mobileappws.io.entity.UserEntity;
import dev.ludwing.mobileappws.io.repositories.UserRepository;
import dev.ludwing.mobileappws.service.UserService;
import dev.ludwing.mobileappws.shared.Utils;
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
	
	@Autowired
	Utils utils;
	
	@Autowired
	BCryptPasswordEncoder encoder;

	/**
	 * Función de servicio que ejecuta la inserción de un registro en la base de datos.
	 */
	@Override
	public UserDto createUser(UserDto user) {
		// En primer lugar verificar que no existe un usuario con el mismo email.
		// Si existe, por el momento lanza una excepción de tiempo de ejecución.
		UserEntity existingUser = userRepository.findUserByEmail(user.getEmail());
		
		if (existingUser != null) throw new RuntimeException("Record already exists");

		UserEntity userEntity = new UserEntity();
		BeanUtils.copyProperties(user, userEntity);
		
		String publicUserId = utils.generateUserId(30);
		
		userEntity.setEncryptedPassword(encoder.encode(user.getPassword()));  // Encriptación de la contraseña
		userEntity.setUserId(publicUserId); // Generar el ID único alfanumérico del usuario.
		
		UserEntity storedUserDetails = userRepository.save(userEntity);
		
		// Regresar el valor recién guardado al controller
		UserDto returnValue = new UserDto();
		BeanUtils.copyProperties(storedUserDetails, returnValue);
				
		return returnValue;
	}

	/**
	 * Este método es utilizado por Spring para cargar los datos del usuario en base a su identificador
	 * (para hacer login) en este caso, el email. 
	 * 
	 * Este método se usa al hacer login.
	 * 
	 */
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		UserEntity userEntity = userRepository.findUserByEmail(email);
		
		if (userEntity == null) throw new UsernameNotFoundException(email);
		
		return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
	}
	
	/***
	 * Obtiene un usuario de la base de datos en base a su Email pero retorna como
	 * respuesta un objeto de tipo UserDto.
	 */
	@Override
	public UserDto getUser(String email) {
		UserEntity userEntity = userRepository.findUserByEmail(email);
		
		if (userEntity == null) throw new UsernameNotFoundException(email);

		UserDto returnValue = new UserDto();
		BeanUtils.copyProperties(userEntity, returnValue);
		return returnValue;
	}

}
