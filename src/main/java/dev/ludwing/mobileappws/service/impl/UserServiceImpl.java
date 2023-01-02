package dev.ludwing.mobileappws.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.ludwing.mobileappws.exceptions.UserServiceException;
import dev.ludwing.mobileappws.io.entity.PasswordResetTokenEntity;
import dev.ludwing.mobileappws.io.entity.UserEntity;
import dev.ludwing.mobileappws.io.repositories.PasswordResetTokenRepository;
import dev.ludwing.mobileappws.io.repositories.UserRepository;
import dev.ludwing.mobileappws.service.UserService;
import dev.ludwing.mobileappws.shared.AmazonEmailService;
import dev.ludwing.mobileappws.shared.Utils;
import dev.ludwing.mobileappws.shared.dto.AddressDto;
import dev.ludwing.mobileappws.shared.dto.UserDto;
import dev.ludwing.mobileappws.ui.model.response.ErrorMessages;

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
	
	@Autowired
	PasswordResetTokenRepository passwordResetTokenRepository;

	/**
	 * Función de servicio que ejecuta la inserción de un registro en la base de datos.
	 */
	@Override
	public UserDto createUser(UserDto user) {
		// En primer lugar verificar que no existe un usuario con el mismo email.
		// Si existe, por el momento lanza una excepción de tiempo de ejecución.
		UserEntity existingUser = userRepository.findUserByEmail(user.getEmail());
		
		if (existingUser != null) throw new RuntimeException("Record already exists");
		
		for (AddressDto tmpAddress: user.getAddresses()) {
			tmpAddress.setUserDetails(user);
			tmpAddress.setAddressId(utils.generateAddressId(30));
		}
		
		// Si el ciclo for anterior no funciona, utilizar este
//		int i = 0;
//		int addressLength = user.getAddresses().size();
//		for (i=0; i < addressLength; i++) {
//			AddressDto address = user.getAddresses().get(i);
//			address.setUserDetails(user);
//			address.setAddressId(utils.generateAddressId(30));
//			user.getAddresses().set(i, address);
//		}

		ModelMapper mapper = new ModelMapper();
		// BeanUtils.copyProperties(user, userEntity);
		UserEntity userEntity = mapper.map(user, UserEntity.class);
		
		String publicUserId = utils.generateUserId(30);
		
		userEntity.setEncryptedPassword(encoder.encode(user.getPassword()));  // Encriptación de la contraseña
		userEntity.setUserId(publicUserId); // Generar el ID único alfanumérico del usuario.
		userEntity.setEmailVerificationToken(Utils.generateEmailVerificationToken(publicUserId));
		userEntity.setEmailVerificationStatus(false);
		
		// Iterar sobre la lista de direcciones del usuario y generar el ID para cada una de ellas.
		
		UserEntity storedUserDetails = userRepository.save(userEntity);

		// Regresar el valor recién guardado al controller
		UserDto returnValue = mapper.map(storedUserDetails, UserDto.class);
		
		// Enviar el email de verificación
		new AmazonEmailService().verifyEmail(returnValue);
		
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
		
		//return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
		
		// En este constructor de User se especifica si el usuario está validado o no.
		return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), 
						userEntity.getEmailVerificationStatus(), true, true, true, new ArrayList<>());
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

	/**
	 * Obtiene el usuario según su UserId
	 */
	@Override
	public UserDto getUserByUserId(String userId) {

		UserEntity userEntity = userRepository.findUserByUserId(userId);
		
		if (userEntity == null) throw new UserServiceException("User with ID: " + userId + " not found.");
		
		ModelMapper mapper = new ModelMapper();
		UserDto userDto = mapper.map(userEntity, UserDto.class);
		
		return userDto;
	}

	/**
	 * Función que actualiza los datos de un usuario.
	 */
	@Override
	public UserDto updateUser(String userId, UserDto user) {
		UserDto userDtoResponse = new UserDto();
		
		UserEntity userEntity = userRepository.findUserByUserId(userId);
		
		if (userEntity == null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
		
		userEntity.setFirstName(user.getFirstName());
		userEntity.setLastName(user.getLastName());
		
		UserEntity updatedUser = userRepository.save(userEntity);
		
		BeanUtils.copyProperties(updatedUser, userDtoResponse);
		
		return userDtoResponse;
	}
	
	/**
	 * Función que implementa la funcionalidad de eliminación de un registro.
	 */
	@Transactional
	@Override
	public void deleteUser(String userid) {

		UserEntity userEntity = userRepository.findUserByUserId(userid);
		
		if (userEntity == null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
		
		userRepository.delete(userEntity);
		
	}

	/**
	 * Servicio que obtiene la lista de usuarios en la db pero con paginación.
	 */
	@Override
	public List<UserDto> getUsers(int page, int limit) {
		
		ModelMapper mapper = new ModelMapper();
		
		List<UserDto> returnValue = new ArrayList<>();
		
		// Esta es la configuración de la paginación al solicitar los registros.
		Pageable pageable = PageRequest.of(page, limit);
		
		// Esta es la página de registros que retorna la consulta.
		Page<UserEntity> userPages = userRepository.findAll(pageable);
		
		// De la página de registros se obtiene la lista de registros como tal.
		List<UserEntity> users = userPages.getContent();
		
		// La lista de registros obtenida se convierte a una lista de objetos tipo UserDto
		// que es devuelta a la función que invocó la consulta.
		for (UserEntity userEntity: users) {

			UserDto userDto = mapper.map(userEntity, UserDto.class);
			
			returnValue.add(userDto);
		}
		
		return returnValue;
	}

	@Override
	public boolean verifyEmailToken(String token) {
		boolean rValue = false;
		
		UserEntity userEntity = userRepository.findUserByEmailVerificationToken(token);
		
		if (userEntity != null) {
			boolean hasTokenExpired = Utils.hasTokenExpired(token);
			
			if (!hasTokenExpired) {
				userEntity.setEmailVerificationStatus(true);
				userEntity.setEmailVerificationToken(null);
				userRepository.save(userEntity);
				rValue = true;
			}
		}
		
		return rValue;
	}

	@Override
	public boolean requestPasswordReset(String email) {
		boolean returnValue = false;
		
		UserEntity userEntity = userRepository.findUserByEmail(email);
		
		if (userEntity == null) {
			return returnValue;
		}
		
		String token = Utils.generatePasswordResetToken(userEntity.getUserId());
		
		PasswordResetTokenEntity passwordResetTokenEntity = new PasswordResetTokenEntity();
		passwordResetTokenEntity.setToken(token);
		passwordResetTokenEntity.setUserDetails(userEntity);
		
		passwordResetTokenRepository.save(passwordResetTokenEntity);
		
		returnValue = new AmazonEmailService().sendPasswordResetRequest(userEntity.getFirstName(), userEntity.getEmail(), token);
		
		return returnValue;
	}

	@Override
	public boolean resetPassword(String token, String password) {
		boolean returnVal = false;
		
		if (Utils.hasTokenExpired(token)) {
			return returnVal;
		}
		
		PasswordResetTokenEntity passResetTokenEntity = passwordResetTokenRepository.findByToken(token);
		
		if (passResetTokenEntity == null) {
			return returnVal;
		}
		
		// Preparar la nueva contraseña
		String encodedPass = encoder.encode(password);
		
		UserEntity userEntity = passResetTokenEntity.getUserDetails();
		userEntity.setEncryptedPassword(encodedPass);
		
		UserEntity savedUserEntity = userRepository.save(userEntity);
		
		if (savedUserEntity != null && savedUserEntity.getEncryptedPassword().equalsIgnoreCase(encodedPass)) returnVal = true;
		
		// Eliminar el token recién utilizado.
		passwordResetTokenRepository.delete(passResetTokenEntity);
		
		return returnVal;
	}

}
