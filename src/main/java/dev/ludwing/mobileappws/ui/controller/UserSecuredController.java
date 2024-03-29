package dev.ludwing.mobileappws.ui.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.ludwing.mobileappws.service.UserService;
import dev.ludwing.mobileappws.shared.dto.UserDto;
import dev.ludwing.mobileappws.ui.model.response.OperationStatusModel;
import dev.ludwing.mobileappws.ui.model.response.RequestOperationName;
import dev.ludwing.mobileappws.ui.model.response.RequestOperationStatus;
import dev.ludwing.mobileappws.ui.model.response.UserRest;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * Este controller es solo para ejemplificar el uso de anotaciones de seguridad.
 * @author ludwingp
 *
 */
@RestController
@RequestMapping("/user-security")
public class UserSecuredController {
	
	@Autowired
	UserService userService;

	/**
	 * Ejemplo de uso de la anotación @Secured con el uso de un role
	 * @param userid
	 * @return
	 */
	@Secured("ROLE_ADMIN")
	@ApiOperation(value="", notes="")
	@ApiImplicitParams({
		@ApiImplicitParam(name="authorization", value="", paramType="header")
	})
	@DeleteMapping(path="/secured/hasrole/{userid}", produces={MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public OperationStatusModel securedRoleExample(@PathVariable String userid) {
		
		OperationStatusModel returnValue = new OperationStatusModel();
		
		returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		
		return returnValue;
	}
	
	/**
	 * Ejemplo de uso de la anotación @Secured con el uso de un authority
	 * @param userid
	 * @return
	 */
	@Secured("DELETE_AUTHORITY")
	@ApiOperation(value="", notes="")
	@ApiImplicitParams({
		@ApiImplicitParam(name="authorization", value="", paramType="header")
	})
	@DeleteMapping(path="/secured/hasauthority/{userid}", produces={MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public OperationStatusModel securedAuthorityExample(@PathVariable String userid) {
		
		OperationStatusModel returnValue = new OperationStatusModel();
		
		returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		
		return returnValue;
	}
	
	/**
	 * Ejemplo de uso de la anotación @PreAuthorize con hasRole
	 * 
	 * Con @PreAuthorize la security expression se evalúa antes de ejecutar el método y
	 * si no se cumplen las condiciones el método no se ejecuta.
	 * Para que esto funcione la anotación @EnableGlobalMethodSecurity debe tener el
	 * parámetro prePostEnabled=true
	 */
	@PreAuthorize("hasRole('ADMIN')") // Esto equivale a @Secured("ROLE_ADMIN")
	//@PreAuthorize("hasAuthority('DELETE_AUTHORITY')") // Esto equivale a @Secured("DELETE_AUTHORITY")
	@ApiOperation(value="", notes="")
	@ApiImplicitParams({
		@ApiImplicitParam(name="authorization", value="", paramType="header")
	})
	@DeleteMapping(path="/preauthorize/hasrole/{userid}", produces={MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public OperationStatusModel preAuthorizeHasRoleExample(@PathVariable String userid) {
		
		OperationStatusModel returnValue = new OperationStatusModel();
		
		returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		
		return returnValue;
	}
	
	
	/**
	 * Ejemplo de uso de la anotación @PreAuthorize con hasAuthority
	 */
	@PreAuthorize("hasAuthority('DELETE_AUTHORITY')") // Esto equivale a @Secured("DELETE_AUTHORITY")
	@ApiOperation(value="", notes="")
	@ApiImplicitParams({
		@ApiImplicitParam(name="authorization", value="", paramType="header")
	})
	@DeleteMapping(path="/preauthorize/hasauthority/{userid}", produces={MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public OperationStatusModel preAuthorizeHasAuthorityExample(@PathVariable String userid) {
		
		OperationStatusModel returnValue = new OperationStatusModel();
		
		returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		
		return returnValue;
	}
	
	
	/**
	 * Este es un ejemplo de acceso al objeto "principal" que está disponible en
	 * las security expressions.  En este caso estamos validando que el método solo
	 * sea accedido por usuarios con rol admin o por el usuario mismo.
	 * 
	 * Para que esto funcione la anotación @EnableGlobalMethodSecurity debe tener el
	 * parámetro prePostEnabled=true y el objeto que es retornado por la función
	 * getAuthentication() del AuthorizationFilter debe tener el mismo campo al que
	 * se está accediendo en la validación, en este caso es userId.
	 * 
	 * Para acceder al parametro de URL se usa el caracter # seguido del nombre del parámetro.
	 * En este caso por ejemplo se compara el ID público del usuario recibido en la URL
	 * con el ID del usuario que se autenticó con el token JWT
	 * 
	 * @param userid
	 * @return
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or #userid == principal.userId")
	@ApiOperation(value="", notes="")
	@ApiImplicitParams({
		@ApiImplicitParam(name="authorization", value="", paramType="header")
	})
	@DeleteMapping(path="/preauthorize/adminorowner/{userid}", produces={MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public OperationStatusModel preAuthorizeAdminOrOwnerExample(@PathVariable String userid) {
		
		OperationStatusModel returnValue = new OperationStatusModel();
		
		returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		
		return returnValue;
	}
	
	/**
	 * Ejemplo de uso de @PostAuthorize
	 * 
	 * Con @PostAuthorize la security expression se evalúa DESPUÉS de ejecutar el método.
	 * Para que esto funcione la anotación @EnableGlobalMethodSecurity debe tener el
	 * parámetro prePostEnabled=true
	 * 
	 * En este ejemplo lo que queremos es que la información retornada pertenezca al usuario
	 * que la solicita o si el solicitante es un usuario admin.  
	 * 
	 * Este ejemplo es una copia del método GET de la data de un usuario en
	 * el UserController.
	 * 
	 * El objeto que es retornado al cliente es accesible bajo el nombre de returnObject (en este
	 * caso es una instancia de UserRest).  También está disponible el usuario que ha hecho la
	 * petición, como en @PreAuthorize, bajo el nombre principal.
	 */
	@PostAuthorize("hasRole('ADMIN') or returnObject.userId == principal.userId")
	@ApiOperation(value="", notes="")
	@ApiImplicitParams({
		@ApiImplicitParam(name="authorization", value="", paramType="header")
	})
	@GetMapping(path="/postauthorize/{userid}", produces={MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public UserRest postAuthorizeExample(@PathVariable String userid) {
		
		UserDto userDto = userService.getUserByUserId(userid);
		
		ModelMapper modelMapper = new ModelMapper();
		// BeanUtils.copyProperties(userDto, returnValue);
		UserRest returnValue = modelMapper.map(userDto, UserRest.class);
		
		return returnValue;
	}
	
	
	
}
