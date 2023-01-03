package dev.ludwing.mobileappws.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import dev.ludwing.mobileappws.io.entity.UserEntity;
import dev.ludwing.mobileappws.io.repositories.UserRepository;
import dev.ludwing.mobileappws.shared.dto.UserDto;

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

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testGetUser() {
		
		// Aquí estamos creando un objeto dummy para que sea retornado por el Mock
		// cuando se llame al método findUserByEmail
		// Los datos del objeto por el momento no importan.
		UserEntity userEntity = new UserEntity();
		userEntity.setId(1l);
		userEntity.setFirstName("Ludwing");
		userEntity.setUserId("xxxxx");
		userEntity.setEncryptedPassword("asdf1234");
		
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

}
