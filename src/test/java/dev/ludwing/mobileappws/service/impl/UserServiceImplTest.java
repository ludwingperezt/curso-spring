package dev.ludwing.mobileappws.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import dev.ludwing.mobileappws.exceptions.UserServiceException;
import dev.ludwing.mobileappws.io.entity.AddressEntity;
import dev.ludwing.mobileappws.io.entity.UserEntity;
import dev.ludwing.mobileappws.io.repositories.PasswordResetTokenRepository;
import dev.ludwing.mobileappws.io.repositories.UserRepository;
import dev.ludwing.mobileappws.shared.AmazonEmailService;
import dev.ludwing.mobileappws.shared.Utils;
import dev.ludwing.mobileappws.shared.dto.AddressDto;
import dev.ludwing.mobileappws.shared.dto.UserDto;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import java.lang.reflect.Type;

class UserServiceImplTest {
	
	// La clase UserServiceImpl NO se mockea porque es la clase que se está
	// probando.
	// La anotación @InjectMocks sirve para indicar que se deben inyectar los
	// Mocks indicados al momento de instanciar el objeto.
	@InjectMocks
	UserServiceImpl userService;
	
	// Aquí estamos indicando que se hará mocking del objeto UserRepository
	@Mock
	UserRepository userRepository;
	
	@Mock
	Utils utils;
	
	@Mock
	BCryptPasswordEncoder encoder;
	
	UserEntity userEntity;
	
	@Mock
	AmazonEmailService amazonEmailService;
	
	/**
	 * Genera una lista de direcciones dummy de tipo AddressDto
	 * @return
	 */
	private List<AddressDto> getAddressesDto() {
		
		AddressDto addresDto = new AddressDto();
		addresDto.setType("shipping");
		addresDto.setCity("Some city");
		addresDto.setCountry("USA");
		addresDto.setPostalCode("0101");
		addresDto.setStreetName("Some Street");
		
		AddressDto billingAddressDto = new AddressDto();
		billingAddressDto.setType("shipping");
		billingAddressDto.setCity("Some city");
		billingAddressDto.setCountry("USA");
		billingAddressDto.setPostalCode("0101");
		billingAddressDto.setStreetName("Some Street");
		
		List<AddressDto> addresses = new ArrayList<>();
		addresses.add(addresDto);
		addresses.add(billingAddressDto);
		
		return addresses;
	}
	
	/**
	 * Genera una lista de direcciones dummy de tipo AddressEntity
	 * @return
	 */
	private List<AddressEntity> getAddressesEntity() {
		List<AddressDto> addressesDto = getAddressesDto();
		
		Type listType = new TypeToken<List<AddressEntity>>() {}.getType();
		
		return new ModelMapper().map(addressesDto, listType);
	}

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);

		// Aquí estamos creando un objeto dummy para que sea retornado por el Mock
		// cuando se llame al método findUserByEmail (en los casos que sean requeridos)
		// Los datos del objeto por el momento no importan.
		userEntity = new UserEntity();
		userEntity.setId(1L);
		userEntity.setFirstName("Ludwing");
		userEntity.setUserId("xxxxx");
		userEntity.setEncryptedPassword("asdf1234");
		userEntity.setEmail("user@test.com");
		userEntity.setEmailVerificationToken("someverificationtoken");
		
		userEntity.setAddresses(getAddressesEntity());
	}

	@Test
	void testGetUser() {
				
		// Aquí se indica que cuando se utilice el método "findUserByEmail()" el cual
		// puede recibir cualquier cadena de texto, entonces debe retornar una instancia
		// de UserEntity, para la cual se usa el objeto dummy recién creado.
		when(userRepository.findUserByEmail(anyString())).thenReturn(userEntity);
		
		// ///////////////////////////////////////////////////////////////////////////////
		// Esta es la función a testear
		UserDto userDto = userService.getUser("user@test.com");
		// ///////////////////////////////////////////////////////////////////////////////
		
		// Sección de assertions donde se verifica que la función ha hecho lo que se supone que
		// debe hacer:
		assertNotNull(userDto);
		assertEquals("Ludwing", userDto.getFirstName());
	}
	
	@Test
	final void testGetUser_UsernameNotFoundException() {
		
		// En esta parte se configura el mock de UserRepository para que al invocar la función 
		// "findUserByEmail()" se retorne null, como en el caso de uso cuando no se puede encontrar
		// un usuario.
		when(userRepository.findUserByEmail(anyString())).thenReturn(null);
		
		// assertThrows verifica que al llamar a la función que se está testeando se lance la excepción
		// que corresponde, en este caso es UsernameNotFoundException.  El código a testear se coloca
		// dentro de un ejecutable, el cual es una expresión lambda que contiene la llamada a la función
		// que se intenta testear.
		assertThrows(UsernameNotFoundException.class,
				()-> {
					// ///////////////////////////////////////////////////////////////////////////////
					// Esta es la función a testear
					UserDto userDto = userService.getUser("user@test.com");
					// ///////////////////////////////////////////////////////////////////////////////
				});
	}

	@Test
	final void testCreateUser() {
		
		// Preparar los mocks con sus respectivos valores de retorno.
		when(userRepository.findUserByEmail(anyString())).thenReturn(null);
		when(utils.generateAddressId(anyInt())).thenReturn("asdf1234");
		when(utils.generateUserId(anyInt())).thenReturn("zxcv1234");
		when(encoder.encode(anyString())).thenReturn("encriptedPassword");
		when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
		
		// Dar la instrucción al mock de amazon SES de que no haga nada
		// (se hace esto porque este es un unit test, no un integration test)
		Mockito.doNothing().when(amazonEmailService).verifyEmail(any(UserDto.class));
		
		UserDto userDto = new UserDto();
		userDto.setAddresses(getAddressesDto());
		userDto.setFirstName("Ludwing");
		userDto.setLastName("Perez");
		userDto.setPassword("12345");
		userDto.setEmail("test@mail.com");

		// /////////////////////////////////////////////////////////////////////////////////////////
		UserDto storedUser = userService.createUser(userDto);
		// /////////////////////////////////////////////////////////////////////////////////////////
		
		assertNotNull(storedUser);
		assertEquals(userEntity.getFirstName(), storedUser.getFirstName());
		assertEquals(userEntity.getLastName(), storedUser.getLastName());
		assertNotNull(storedUser.getUserId());
		assertEquals(storedUser.getAddresses().size(), userEntity.getAddresses().size());
		
		// Verificar que las funciones generateAddressId() de la clase utils sea llamada dos veces con el parámetro 30
		// y que la función encode() del objeto encoder sea llamada una sola vez con la contraseña configurada.
		verify(utils, times(storedUser.getAddresses().size())).generateAddressId(30);
		verify(encoder, times(1)).encode("12345");
		// Verificar que se llama el método save() del repository de usuarios una sola vez.
		verify(userRepository, times(1)).save(any(UserEntity.class));
		
	}
	
	@Test
	final void testCreateUser_UserServiceException() {
		when(userRepository.findUserByEmail(anyString())).thenReturn(userEntity);
		
		UserDto userDto = new UserDto();
		userDto.setAddresses(getAddressesDto());
		userDto.setFirstName("Ludwing");
		userDto.setLastName("Perez");
		userDto.setPassword("12345");
		userDto.setEmail("test@mail.com");
		
		assertThrows(UserServiceException.class,
				()-> {
					// ///////////////////////////////////////////////////////////////////////////////
					// Esta es la función a testear
					userService.createUser(userDto);
					// ///////////////////////////////////////////////////////////////////////////////
				});
	}
	
}
