package dev.ludwing.mobileappws.ui.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import dev.ludwing.mobileappws.service.impl.UserServiceImpl;
import dev.ludwing.mobileappws.shared.dto.AddressDto;
import dev.ludwing.mobileappws.shared.dto.UserDto;
import dev.ludwing.mobileappws.ui.model.response.UserRest;

class UserControllerTest {
	
	@InjectMocks
	UserController userController;
	
	@Mock
	UserServiceImpl userService;
	
	UserDto userDto;
	
	final String USER_ID = "someID";
	
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

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
		
		userDto = new UserDto();
		userDto.setFirstName("Ludwing");
		userDto.setLastName("Perez");
		userDto.setEmail("test@mail.com");
		userDto.setEmailVerificationStatus(false);
		userDto.setEmailVerificationToken(null);
		userDto.setUserId(USER_ID);
		userDto.setAddresses(getAddressesDto());
		userDto.setEncryptedPassword("encryptedPassword");
	}

	@Test
	void testGetUser() {
		when(userService.getUserByUserId(anyString())).thenReturn(userDto);
		
		UserRest resp = userController.getUser(USER_ID);
		
		assertNotNull(resp);
		assertEquals(USER_ID, resp.getUserId());
		assertEquals(userDto.getFirstName(), resp.getFirstName());
		assertEquals(userDto.getLastName(), resp.getLastName());
		assertTrue(userDto.getAddresses().size() == resp.getAddresses().size());
	}

}
