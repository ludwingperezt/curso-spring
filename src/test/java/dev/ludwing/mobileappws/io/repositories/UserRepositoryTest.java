package dev.ludwing.mobileappws.io.repositories;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import dev.ludwing.mobileappws.io.entity.AddressEntity;
import dev.ludwing.mobileappws.io.entity.UserEntity;

/**
 * Este test es para probar la consulta SQL nativa que se
 * definió en UserRepository.
 * 
 * Este test debe ser un integration test, por esa razón se usan
 * los decorators @@ExtendWith y @SpringBootTest
 * 
 * @author ludwingp
 *
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserRepositoryTest {
	
	@Autowired
	UserRepository userRepository;
	
	// Esta variable sirve para controlar si los registros dummy ya fueron creados en la DB
	// para evitar hacerlo más de una vez.
	static boolean recordsCreated = false;
	
	private void createRecords() {
		// En caso de que se use la base de datos H2 (in memory), se puede crear una entidad para que
		// la consulta se ejecute correctamente.

		UserEntity userEntity = new UserEntity();
	    userEntity.setFirstName("Ludwing");
	    userEntity.setLastName("Perez");
	    userEntity.setUserId("1a2b3c");
	    userEntity.setEncryptedPassword("xxx");
	    userEntity.setEmail("test@test.com");
	    userEntity.setEmailVerificationStatus(true);
	     
	    // Prepare User Addresses
	    AddressEntity addressEntity = new AddressEntity();
	    addressEntity.setType("shipping");
	    addressEntity.setAddressId("ahgyt74hfy");
	    addressEntity.setCity("Vancouver");
	    addressEntity.setCountry("Canada");
	    addressEntity.setPostalCode("ABCCDA");
	    addressEntity.setStreetName("123 Street Address");

	    List<AddressEntity> addresses = new ArrayList<>();
	    addresses.add(addressEntity);
	    
	    userEntity.setAddresses(addresses);
	    
	    // userRepository.save(userEntity);
	    
	    recordsCreated = true;
	}

	@BeforeEach
	void setUp() throws Exception {
		if (!recordsCreated) createRecords();		
	}

	@Test
	void testGetVerifiedUsers() {
		Pageable pageableRequest = PageRequest.of(0, 2);  // Solicitar la página 0 con 2 registros por página.
		Page<UserEntity> page = userRepository.findAllUsersWithConfirmedEmailAddress(pageableRequest);
		
		assertNotNull(page);
		
		List<UserEntity> userEntities = page.getContent();
		assertNotNull(userEntities);
		assertTrue(userEntities.size() >= 1);
	}
	
	/**
	 * Este test comprueba el ejemplo de una consulta SQL nativa con parámetros posicionales.
	 * 
	 * En este momento funciona porque en la base de datos MySQL que usa la aplicación hay 
	 * dos registros con el primer nombre "Homero".
	 */
	@Test
	void testFindUserByFirstName() {
		String firstName = "Homero";
		List<UserEntity> users = userRepository.findUserByFirstName(firstName);
		assertNotNull(users);
		assertTrue(users.size() == 2);
	}

}
