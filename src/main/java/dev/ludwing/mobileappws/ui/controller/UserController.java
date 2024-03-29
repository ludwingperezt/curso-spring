package dev.ludwing.mobileappws.ui.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
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
import dev.ludwing.mobileappws.shared.Roles;
import dev.ludwing.mobileappws.shared.dto.AddressDto;
import dev.ludwing.mobileappws.shared.dto.UserDto;
import dev.ludwing.mobileappws.ui.model.request.PasswordResetModel;
import dev.ludwing.mobileappws.ui.model.request.PasswordResetRequestModel;
import dev.ludwing.mobileappws.ui.model.request.UserDetailRequestModel;
import dev.ludwing.mobileappws.ui.model.response.AddressRest;
import dev.ludwing.mobileappws.ui.model.response.ErrorMessages;
import dev.ludwing.mobileappws.ui.model.response.OperationStatusModel;
import dev.ludwing.mobileappws.ui.model.response.RequestOperationName;
import dev.ludwing.mobileappws.ui.model.response.RequestOperationStatus;
import dev.ludwing.mobileappws.ui.model.response.UserRest;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

// La anotación @RestController identifica la clase como un controlador REST para que pueda
// recibir peticiones HTTP.
// La anotación @RequestMapping sirve para enrutar acciones con paths

@RestController
@RequestMapping("/users") // GET|POST|PUT|DELETE http://localhost/users
// @CrossOrigin(origins="*")  // Habilita CORS para todos los dominios (menos seguro)
// @CrossOrigin(origins="http://localhost:8090") // Habilita CORS para un solo dominio
// @CrossOrigin(origins={"http://localhost:8090", "http://localhost:8091"}) // Habilita CORS para una lista determinada de dominios.
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
	@ApiOperation(value="The GET user details web service endpoint", 
			notes="${userController.GetUser.ApiOperation.Notes}")
	@ApiImplicitParams({
		@ApiImplicitParam(name="authorization", value="${userController.authorizationHeader.description}", paramType="header")  // En este ejemplo la descripción del header que se ve en swagger-ui se define a través de una property en el archivo application.properties
	})
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
	@ApiOperation(value="The POST user web service endpoint (sign up)", 
			notes="${userController.PostUser.ApiOperation.Notes}")
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
		
		// Asignar el rol de usuario al usuario que se va a crear
		// Se usa un HashSet para que no hayan valores repetidos
		userDto.setRoles(new HashSet<>(Arrays.asList(Roles.ROLE_USER.name()))); 
		
		UserDto createdUser = userService.createUser(userDto);

		// Se copian los datos del usuario recién creado al objeto de respuesta.
		// BeanUtils.copyProperties(createdUser, returnValue);
		UserRest returnValue = modelMapper.map(createdUser, UserRest.class);
		
		return returnValue;
	}
	
	@ApiOperation(value="The update user details web service endpoint", 
			notes="${userController.PutUser.ApiOperation.Notes}")
	@ApiImplicitParams({
		@ApiImplicitParam(name="authorization", value="${userController.authorizationHeader.description}", paramType="header")  // En este ejemplo la descripción del header que se ve en swagger-ui se define a través de una property en el archivo application.properties
	})
	@PutMapping(path="/{userid}", 
				consumes= {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}, 
				produces={MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public UserRest updateUser(@PathVariable String userid, @RequestBody UserDetailRequestModel userDetail) {
		
		if (userDetail.getFirstName().isEmpty()) throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
		
		UserDto userDto = new UserDto();
		
		// BeanUtils.copyProperties() se usa para llenar los datos del objeto DTO con 
		// los datos del objeto que se recibió como body del Request.
		BeanUtils.copyProperties(userDetail, userDto);
		
		UserDto updatedUser = userService.updateUser(userid, userDto);

		// Se copian los datos del usuario recién creado al objeto de respuesta.
		//BeanUtils.copyProperties(updatedUser, returnValue);
		ModelMapper modelMapper = new ModelMapper();
		UserRest returnValue = modelMapper.map(updatedUser, UserRest.class);
		
		return returnValue;
	}
	
	/**
	 * Método de endpoint para eliminar un registro de usuario.
	 * @param userid
	 * 
	 * En la anotación @Secured se puede especificar qué rol o authority es necesaria para
	 * poder ejecutar la acción.  Usar siempre el nombre completo del rol o authority
	 * (por lo regular los roles comienzan con el prefix "ROLE_")
	 * 
	 * @return
	 */
	@Secured("ROLE_ADMIN")
	@ApiOperation(value="The delete user web service endpoint", 
			notes="${userController.DeleteUser.ApiOperation.Notes}")
	@ApiImplicitParams({
		@ApiImplicitParam(name="authorization", value="${userController.authorizationHeader.description}", paramType="header")  // En este ejemplo la descripción del header que se ve en swagger-ui se define a través de una property en el archivo application.properties
	})
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
	@ApiOperation(value="The list users web service endpoint", 
			notes="${userController.ListUsers.ApiOperation.Notes}")
	@ApiImplicitParams({
		@ApiImplicitParam(name="authorization", value="${userController.authorizationHeader.description}", paramType="header")  // En este ejemplo la descripción del header que se ve en swagger-ui se define a través de una property en el archivo application.properties
	})
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
	 * Se retorna una instancia de CollectionModel<AddressRest> para que se genere
	 * un objeto JSON que contenga la lista de los elementos solicitados y también los
	 * enlaces de navegación. 
	 * 
	 * que cada uno
	 * de los elementos de la lista retornada tenga los enlaces de navegación que
	 * tendría cada uno en el endpoint de detalle.
	 * 
	 * @param userid
	 * @return
	 */
	@ApiOperation(value="The list user's addresses web service endpoint", 
			notes="${userController.ListAddresses.ApiOperation.Notes}")
	@ApiImplicitParams({
		@ApiImplicitParam(name="authorization", value="${userController.authorizationHeader.description}", paramType="header")  // En este ejemplo la descripción del header que se ve en swagger-ui se define a través de una property en el archivo application.properties
	})
	@GetMapping(path="/{userid}/addresses", produces={MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}) 
	public CollectionModel<AddressRest> getUserAddresses(@PathVariable String userid) {
		
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
			
			// Este recorrido de la lista de Direcciones se hace para agregar los enlaces de navegación embebidos.
			// En este caso solo se agrega el self link para acceder directamente a ese recurso.
			for (AddressRest addressRest : returnValue) {
				Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class)
						.getAddress(userid, addressRest.getAddressId()))
						.withSelfRel();
				addressRest.add(selfLink);
			}
		}
		
		// Generar los Links de navegación:
		Link userLink = WebMvcLinkBuilder.linkTo(UserController.class).slash(userid).withRel("user");
		Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class)
				.getUserAddresses(userid))
				.withSelfRel();
		
		return CollectionModel.of(returnValue, userLink);
	}

	/**
	 * Este es el endpoint para obtener una dirección en específico.
	 * 
	 * @param userid
	 * @param addressId
	 * @return
	 */
	@ApiOperation(value="The Get user's address web service endpoint", 
			notes="${userController.GetAddress.ApiOperation.Notes}")
	@ApiImplicitParams({
		@ApiImplicitParam(name="authorization", value="${userController.authorizationHeader.description}", paramType="header")  // En este ejemplo la descripción del header que se ve en swagger-ui se define a través de una property en el archivo application.properties
	})
	@GetMapping(path="/{userid}/addresses/{addressId}", produces={MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}) 
	public EntityModel<AddressRest> getAddress(@PathVariable String userid, @PathVariable String addressId) {
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
		// Si se usa methodOn() entonces no es necesario construir manualmente la URL con .slash(), basta con
		// llamar a la función mapeada al endpoint que se desea linkear y enviarle los parámetros que requiera
		// la función.
		Link userAddressesLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class)
				.getUserAddresses(userid))
				//.slash(userid)
				//.slash("addresses")
				.withRel("addresses");
		
		// 3. Generar el enlace self
		// http://localhost:8080/users/<userId>/addresses/<addressId>
		// Se usa .withSelfRel() para indicar que es el enlace al recurso mismo.
		Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class)
				.getAddress(userid, addressId))
				//.slash(userid)
				//.slash("addresses")
				//.slash(addressId)
				.withSelfRel();
		
//		returnValue.add(userLink);
//		returnValue.add(userAddressesLink);
//		returnValue.add(selfLink);
		
		// Si se utiliza esta forma de retornar los enlaces en la respuesta (a través de retornar
		// un EntityModel<Clase> entonces ya no es necesario derivar AddressRest de RepresentationModel
		
		return EntityModel.of(returnValue, Arrays.asList(userLink, userAddressesLink, selfLink));
	}
	
	/**
	 * Endpoint para verificar la validez de un token para validación de correo electrónico.
	 * 
	 * Este endpoint no debe estar protegido por autenticación.
	 * 
	 * @param token
	 * @return
	 */
	@ApiOperation(value="The verify email token web service endpoint", 
			notes="${userController.GetVerifyEmailToken.ApiOperation.Notes}")
	@GetMapping(path="/email-verification", produces={MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}) 
	// @CrossOrigin(origins="*")  // Habilita CORS para todos los dominios (menos seguro)
	// @CrossOrigin(origins="http://localhost:8090") // Habilita CORS para un solo dominio
	// @CrossOrigin(origins={"http://localhost:8090", "http://localhost:8091"}) // Habilita CORS para una lista determinada de dominios.
	public OperationStatusModel verifyEmailToken(@RequestParam(value="token") String token) {
		
		OperationStatusModel rValue = new OperationStatusModel();
		rValue.setOperationName(RequestOperationName.VERIFY_EMAIL.name());
		
		boolean isVerified = userService.verifyEmailToken(token);
		
		if (isVerified) {
			rValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		}
		else {
			rValue.setOperationResult(RequestOperationStatus.ERROR.name());
		}
		
		return rValue;
	}
	
	/**
	 * Controlador para solicitar reset de contraseña.
	 * 
	 * @param passwordReset
	 * @return
	 */
	@ApiOperation(value="The reset password web service endpoint", 
			notes="${userController.PostRequestResetPassword.ApiOperation.Notes}")
	@PostMapping(path="/reset-password-request", //
			consumes= {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}, 
			produces={MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public OperationStatusModel requestReset(@RequestBody PasswordResetRequestModel passwordReset) {
		
		OperationStatusModel returnValue = new OperationStatusModel();
		
		boolean operationResult = userService.requestPasswordReset(passwordReset.getEmail());
		
		returnValue.setOperationName(RequestOperationName.REQUEST_PASSWORD_RESET.name());
		returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
		
		if (operationResult) {
			returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		}
		
		return returnValue;
	}
	
	/**
	 * Endpoint que ejecuta el usuario para reestrablecer su contraseña.  
	 * 
	 * Este endpoint se llama desde el front a través del link que se envía por correo al usuario.
	 * 
	 * @param passwordReset
	 * @return
	 */
	@ApiOperation(value="The reset password confirm web service endpoint", 
			notes="${userController.PostConfirmResetPassword.ApiOperation.Notes}")
	@PostMapping(path="/password-reset",
			consumes= {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}, 
			produces={MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public OperationStatusModel resetPassword(@RequestBody PasswordResetModel passwordReset) {
		
		OperationStatusModel returnValue = new OperationStatusModel();
		
		boolean operationResult = userService.resetPassword(passwordReset.getToken(), passwordReset.getPassword());
		
		returnValue.setOperationName(RequestOperationName.PASSWORD_RESET.name());
		returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
		
		if (operationResult) {
			returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		}
		
		return returnValue;
	}
}
