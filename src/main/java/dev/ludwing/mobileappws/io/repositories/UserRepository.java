package dev.ludwing.mobileappws.io.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import dev.ludwing.mobileappws.io.entity.UserEntity;

/**
 * Si no se utiliza Spring Data JPA, entonces sería necesario crear
 * una clase Data Access Object manualmente para realizar operaciones CRUD
 * siendo necesario gestionar manualmente las conexiones a base de datos
 * y el SQL a ejecutar.
 * 
 * La annotation @Repository indica a Spring que esta interfaz será un repositorio
 * CRUD.
 * 
 * No es necesario definir los métodos CRUD, pero pueden agregarse métodos personalizados,
 * por ejemplo para búsquedas sobre campos específicos, etc.
 * 
 * PagingAndSortingRepository es necesaria para la implementación de la paginación.
 * 
 * @author ludwingp
 *
 */
@Repository
public interface UserRepository extends PagingAndSortingRepository<UserEntity, Long> {

	// El método findUserByEmail() es un ejemplo de método personalizado para buscar un 
	// usuario con base al email.
	//
	// Spring provee una forma muy simple de realizar querys sobre campos específicos.
	// Para lograrlo se usa la siguiente formula: se debe definir un método de búsqueda
	// y éste siempre debe iniciar con la palabra "find" luego se coloca "By" para indicar
	// que se utilizará algún campo y por último se coloca el nombre del campo a buscar, el
	// cual debe estar definido en UserEntity.
	UserEntity findUserByEmail(String email);
	
	// Este método es para obtener el usuario por User ID
	UserEntity findUserByUserId(String userId);
}
