package dev.ludwing.mobileappws.shared;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Para que se pueda usar el context de spring para correr los tests solo
 * hace falta agregar la anotación @SpringBootTest
 * 
 * @ExtendWith(SpringExtension.class) no es necesario.
 * 
 * @author ludwingp
 *
 */
//@ExtendWith(SpringExtension.class)
@SpringBootTest
class UtilsTest {
	
	@Autowired
	Utils utils;

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testGenerateUserId() {
		String userid = utils.generateUserId(30);
		String userid2 = utils.generateUserId(30);
		
		// Comprobar que el ID generado no es null y que tiene una longitud de 30 caracteres
		assertNotNull(userid);
		assertTrue(userid.length() == 30);
		
		// Comprobar que se generó un segundo ID pero que debe ser distinto al primero.
		assertNotNull(userid2);
		assertTrue(!userid.equalsIgnoreCase(userid2));
	}

	@Test
	// @Disabled // Con la anotación @Disabled se desactiva un test para que no sea ejecutado.
	void testHasTokenNotExpired() {
		
		// Caso 1: Comprobar cuando un token no ha expirado.
		String token = utils.generateEmailVerificationToken("someuserid");
		
		assertNotNull(token);
		
		boolean expired = Utils.hasTokenExpired(token);
		
		assertFalse(expired);
	}
	
	@Test
	@Disabled
	void testHasTokenExpired() {
		
		// Caso 2: Comprobar cuando un token ya expiró
		// Este es un token expirado generado manualmente con la clave secreta definida en application.properties
		String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzb21ldXNlcmlkIiwiZXhwIjoxfQ.ICC57WG34F3kAM-Y6WGDITVrMu04LulZaO1yx8-JFW1UItUin6UX8Wy6KxyuxtUATrfodtDJo48QfxQfIRGjlw";
				
		boolean expired = Utils.hasTokenExpired(token);
		
		assertTrue(expired);
	}

}
