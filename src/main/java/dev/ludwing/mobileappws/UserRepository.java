package dev.ludwing.mobileappws;

import org.springframework.data.repository.CrudRepository;
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
 * @author ludwingp
 *
 */
@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long> {

	// El método findUserByEmail() es un ejemplo de método personalizado para buscar un 
	// usuario con base al email.
	// UserEntity findUserByEmail(String email);
}
