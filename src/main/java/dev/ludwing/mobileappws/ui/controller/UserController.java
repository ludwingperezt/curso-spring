package dev.ludwing.mobileappws.ui.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.ludwing.mobileappws.exceptions.UserServiceException;
import dev.ludwing.mobileappws.service.AddressService;
import dev.ludwing.mobileappws.service.UserService;
import dev.ludwing.mobileappws.shared.dto.AddressDto;
import dev.ludwing.mobileappws.shared.dto.UserDto;
import dev.ludwing.mobileappws.ui.model.request.UserDetailRequestModel;
import dev.ludwing.mobileappws.ui.model.response.AddressRest;
import dev.ludwing.mobileappws.ui.model.response.ErrorMessages;
import dev.ludwing.mobileappws.ui.model.response.OperationStatusModel;
import dev.ludwing.mobileappws.ui.model.response.RequestOperationName;
import dev.ludwing.mobileappws.ui.model.response.RequestOperationStatus;
import dev.ludwing.mobileappws.ui.model.response.UserRest;

// La anotación @RestController identifica la clase como un controlador REST para que pueda
// recibir peticiones HTTP.
// La anotación @RequestMapping sirve para enrutar acciones con paths

@RestController
@RequestMapping("users") // GET|POST|PUT|DELETE http://localhost/users
public class UserController {
	
	// La anotación @Autowired es para indicar la inyección de dependencias, en este caso
	// el del servicio de acceso a datos de Usuarios.
	@Autowired
	UserService userService;
	
	@Autowired
	AddressService addressService;

	/**
	 * El parámetro "produces" indica qué tipo de respuesta se enviará al cliente.  Si solo se coloca un valor
	 * (sin brackets) entonces el endpoint solo devolverá ese tipo de representación y si el cliente solicita otro
	 * entonces lanzará una excepción.  Si hay dos o más representaciones entonces se devolverá la que
	 * solicite el cliente, pero si el cliente no envía ninguno (en la cabecera Accept) entonces por
	 * defecto se retorna en el formato que esté primero, en este caso sería XML.
	 * 
	 * @param userid
	 * @return
	 */
	@GetMapping(path="/{userid}", produces={MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})  // Mapea la función al método GET 
	public UserRest getUser(@PathVariable String userid) {
		
		UserDto userDto = userService.getUserByUserId(userid);
		
		ModelMapper modelMapper = new ModelMapper();
		// BeanUtils.copyProperties(userDto, returnValue);
		UserRest returnValue = modelMapper.map(userDto, UserRest.class);
		
		return returnValue;
	}
	
	/**
	 * Método para la inserción de usuarios con método HTTP POST.
	 * 
	 * Con el parámetro "consumes" se indica qué representaciones se podrán recibir del cliente.
	 * En este caso se aceptan XML y JSON.
	 * 
	 * @param userDetails
	 * @return
	 */
	@PostMapping(consumes= {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}, 
					produces={MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public UserRest createUser(@RequestBody UserDetailRequestModel userDetails) throws Exception {

		if (userDetails.getFirstName().isEmpty()) throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());

		// BeanUtils.copyProperties() se usa para llenar los datos del objeto DTO con 
		// los datos del objeto que se recibió como body del Request.
		// BeanUtils.copyProperties(userDetails, userDto);
		// BeanUtils no es tan bueno para mapear objetos más complejos, por ello a partir
		// de ahora se utiliza ModelMapper
		ModelMapper modelMapper = new ModelMapper();
		UserDto userDto = modelMapper.map(userDetails, UserDto.class);
		
		UserDto createdUser = userService.createUser(userDto);

		// Se copian los datos del usuario recién creado al objeto de respuesta.
		// BeanUtils.copyProperties(createdUser, returnValue);
		UserRest returnValue = modelMapper.map(createdUser, UserRest.class);
		
		return returnValue;
	}
	
	@PutMapping(path="/{userid}", 
				consumes= {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}, 
				produces={MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public UserRest updateUser(@PathVariable String userid, @RequestBody UserDetailRequestModel userDetail) {
		UserRest returnValue = new UserRest();
		
		if (userDetail.getFirstName().isEmpty()) throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
		
		UserDto userDto = new UserDto();
		
		// BeanUtils.copyProperties() se usa para llenar los datos del objeto DTO con 
		// los datos del objeto que se recibió como body del Request.
		BeanUtils.copyProperties(userDetail, userDto);
		
		UserDto updatedUser = userService.updateUser(userid, userDto);

		// Se copian los datos del usuario recién creado al objeto de respuesta.
		BeanUtils.copyProperties(updatedUser, returnValue);
		
		return returnValue;
	}
	
	/**
	 * Método de endpoint para eliminar un registro de usuario.
	 * @param userid
	 * @return
	 */
	@DeleteMapping(path="/{userid}", produces={MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public OperationStatusModel deleteUser(@PathVariable String userid) {
		
		OperationStatusModel returnValue = new OperationStatusModel();
		
		returnValue.setOperationName(RequestOperationName.DELETE.name());
		
		userService.deleteUser(userid);
		
		returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		
		return returnValue;
	}
	
	/**
	 * Endpoint para listar a los usuarios en la base de datos.
	 * 
	 * @param page: Es un query param que indica qué número de página se está leyendo y su valor por defecto es 1 (la primera página)
	 * @param limit: Es un query param que indica qué cantidad de registros por página se solicita. Por defecto son 25.
	 * @return
	 */
	@GetMapping(produces={MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public List<UserRest> getUsers(@RequestParam(value="page", defaultValue="0") int page,
									@RequestParam(value="limit", defaultValue="25") int limit) {
		List<UserRest> listUsers = new ArrayList<>();
		
		List<UserDto> usersDtos = userService.getUsers(page, limit);
		
		ModelMapper mapper = new ModelMapper();
		
		// Recorrer la lista de usuarios que devuelve la consulta y convertirlos
		// al tipo de objeto que se retorna al cliente.
		for (UserDto userDto: usersDtos) {
			UserRest userModel = mapper.map(userDto, UserRest.class);
			listUsers.add(userModel);
		}
		
		return listUsers;
	}
	
	/**
	 * Este es el endpoint para obtener la lista de direcciones de un usuario.
	 * 
	 * @param userid
	 * @return
	 */
	@GetMapping(path="/{userid}/addresses", produces={MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}) 
	public List<AddressRest> getUserAddresses(@PathVariable String userid) {
		
		List<AddressRest> returnValue = new ArrayList<>();
		
		List<AddressDto> addressesDto = addressService.getAddresses(userid);
		
		if (addressesDto != null && !addressesDto.isEmpty())
		{
			ModelMapper modelMapper = new ModelMapper();

			// Este snippet es para convertir la lista de objetos AddressDto devuelta por la consulta a la lista
			// de AddressRest que debe retornar el endpoint al cliente.  Esta conversión se hace a través del
			// objeto ModelMapper.
			Type listType = new TypeToken<List<AddressRest>>() {}.getType();
			returnValue = modelMapper.map(addressesDto, listType);
		}
		
		return returnValue;
	}

	/**
	 * Este es el endpoint para obtener una dirección en específico.
	 * 
	 * @param userid
	 * @param addressId
	 * @return
	 */
	@GetMapping(path="/{userid}/addresses/{addressId}", produces={MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}) 
	public AddressRest getAddress(@PathVariable String userid, @PathVariable String addressId) {
		AddressDto addressDto = addressService.getAddress(addressId);
		ModelMapper modelMapper = new ModelMapper();
		
		AddressRest returnValue = modelMapper.map(addressDto, AddressRest.class);
		
		// Aquí se generan los links de navegación HATEOAS

		// Agregar los links a la respuesta.
		// 1. Generar el enlace al detalle del usuario:
		// http://localhost:8080/users/<userId>
		// Se usa .withRel("user") para darle un nombre al enlace, pero en realidad puede ser cualquier texto.
		Link userLink = WebMvcLinkBuilder.linkTo(UserController.class).slash(userid).withRel("user");
		
		// 2. Generar el enlace a la lista de direcciones del usuario:
		// http://localhost:8080/users/<userId>/addresses
		// Se usa .withRel("addresses") para darle un nombre al enlace, pero en realidad puede ser cualquier texto.
		Link userAddressesLink = WebMvcLinkBuilder.linkTo(UserController.class)
				.slash(userid)
				.slash("addresses")
				.withRel("addresses");
		
		// 3. Generar el enlace self
		// http://localhost:8080/users/<userId>/addresses/<addressId>
		// Se usa .withSelfRel() para indicar que es el enlace al recurso mismo.
		Link selfLink = WebMvcLinkBuilder.linkTo(UserController.class)
				.slash(userid)
				.slash("addresses")
				.slash(addressId)
				.withSelfRel();
		
		returnValue.add(userLink);
		returnValue.add(userAddressesLink);
		returnValue.add(selfLink);
		
		return returnValue;
	}
}
