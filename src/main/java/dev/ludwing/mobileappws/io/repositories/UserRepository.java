package dev.ludwing.mobileappws.io.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
	
	// Busca al usuario que tenga el mismo token de verificación de email
	UserEntity findUserByEmailVerificationToken(String token);
	
	// Es MUY IMPORTANTE especificar los valores de value y nativeQuery=true, de lo contrario
	// se tomarían como una consulta Java Persistence Query
	// value debe ser una consulta SQL normal
	// nativeQuery siempre debe ser true
	// Debido a que se está usando paginación, es necesario especificar también el parámetro
	// countQuery, el cual es la misma consulta que value pero en este caso se retorna el conteo
	// de registros totales que devolvería la consulta (con count(*)).  Esto es para que se
	// pueda hacer correctamente la paginación.  Esto no se hace si no se usa Page o Pageable
	@Query(value="SELECT * FROM users u where u.email_verification_status = true",
			countQuery="SELECT COUNT(*) FROM users u where u.email_verification_status = true",
			nativeQuery=true)
	Page<UserEntity> findAllUsersWithConfirmedEmailAddress(Pageable pageableRequest);
	
	// Esta consulta es un ejemplo de cómo usar parámetros posicionales en una consulta SQL
	// nativa.  Para usar el primer parámetro se usa ?1, para el segundo ?2, etc.
	// La posición de los parámetros está determinada por el orden en que están definidos en la 
	// firma del método, es decir, el primer parámetro corresponde a ?1, el segundo a ?2, etc.
	@Query(value="SELECT * FROM users u where u.first_name = ?1",
			nativeQuery=true)
	List<UserEntity> findUserByFirstName(String firstName);
	
	
	// En este ejemplo se muestra como usar parámetros por nombre en una consulta SQL nativa.
	// En este caso solo hace falta especificar en la consulta el nombre del parámetro antecedido
	// de dos puntos (:).  Ese nombre debe coincidir con el nombre del parámetro especificado en
	// la anotación @Param.  El nombre del parámetro como tal en la firma del método puede ser 
	// cualquier otro, como en este ejemplo (aunque también puede ser el mismo).
	@Query(value="SELECT * FROM users u where u.last_name = :lastNameParam",
			nativeQuery=true)
	List<UserEntity> findUsersByLastName(@Param("lastNameParam") String lastName);
	
	// Query de ejemplo de uso de LIKE para buscar partes de una palabra.
	@Query(value="SELECT * FROM users u WHERE u.first_name LIKE %:keyword% OR u.last_name LIKE %:keyword%",
			nativeQuery=true)
	List<UserEntity> findUsersByKeyword(@Param("keyword") String keyword);
	
	// Este es un ejemplo de una query que solo retorna un subconjunto de los campos de la consulta.
	// Esta función en lugar de retornar una lista de UserEntity, lo que retorna es un array de Objects
	// dependiendo de la cantidad de campos seleccionados.
	// En el array de Object que representa cada fila de la consulta, el primer elemento corresponde al 
	// primer campo especificado en la consulta, el segundo elemento del array al segundo elemento de la
	// consulta y así sucesivamente.
	@Query(value="SELECT u.first_name, u.last_name FROM users u WHERE u.first_name LIKE %:keyword% OR u.last_name LIKE %:keyword%",
			nativeQuery=true)
	List<Object[]> findUsersFisrtAndLastNameByKeyword(@Param("keyword") String keyword);
	
	/**
	 * Este es un ejemplo de una query que modifica un registro en la base de datos.
	 * Debido a este mismo motivo, es necesario usar las anotaciones @Transactional para que se haga un
	 * rollback al momento de fallar la actualización; también es necesario usar @Modifying para indicar
	 * que la query está modificando un registro en la base de datos.  También se debe hacer esto en caso
	 * de queries que eliminen registros.  En el caso de la anotación @Transactional, ésta usualmente se
	 * coloca en las clases de servicio o en los controllers cuando se requiere un control transaccional,
	 * pero en este caso se colocó aquí porque de momento este método no es utilizado en ningún servicio
	 * o controller.
	 * 
	 * @param emailVerificationStatus
	 * @param userId
	 */
	@Transactional
	@Modifying
	@Query(value="UPDATE users SET email_verification_status = :emailVerificationStatus WHERE user_id = :userId",
			nativeQuery=true)
	void updateUserEmailVerificationStatus(@Param("emailVerificationStatus") boolean emailVerificationStatus, @Param("userId") String userId);
	
	/**
	 * Ejemplo de una consulta select con JPQL.
	 * 
	 * Cuando se hacen consultas con JPQL, en lugar de usar nombres de tablas y campos como están en la base de datos
	 * se utilizan los nombres de las clases y atributos definidos en las entidades de persistencia.
	 * 
	 * @param userId
	 * @return
	 */
	@Query("SELECT user FROM UserEntity user WHERE user.userId = :userId")
	UserEntity findUserEntityByUserId(@Param("userId") String userId);
	
	/**
	 * Ejemplo de una consulta que solo retorna un subconjunto de los datos de la entidad pero usando JPQL.
	 * 
	 * Igual que en las consultas con SQL nativo que solo retornan un subconjunto de campos de la tabla, en este
	 * caso también se retorna una lista de arrays de object, donde cada array de la lista representa una
	 * fila retornada por la consulta y dentro de cada array la primera posición es el primer elemento especificado
	 * en la query y así sucesivamente.
	 * 
	 * @param userId
	 * @return
	 */
	@Query("SELECT u.firstName, u.lastName FROM UserEntity u WHERE u.userId = :userId")
	List<Object[]> findUserEntityFullNameById(@Param("userId") String userId);
	
	/**
	 * Este es un ejemplo de una query que modifica un registro en la base de datos usando JPQL.
	 * Debido a este mismo motivo, es necesario usar las anotaciones @Transactional para que se haga un
	 * rollback al momento de fallar la actualización; también es necesario usar @Modifying para indicar
	 * que la query está modificando un registro en la base de datos.  También se debe hacer esto en caso
	 * de queries que eliminen registros.  En el caso de la anotación @Transactional, ésta usualmente se
	 * coloca en las clases de servicio y en los controllers cuando se requiere un control transaccional,
	 * pero en este caso se colocó aquí porque de momento este método no es utilizado en ningún servicio
	 * o controller.
	 * 
	 * @param emailStatus
	 * @param userId
	 */
	@Modifying
	@Transactional
	@Query("UPDATE UserEntity u SET u.emailVerificationStatus = :emailStatus WHERE u.userId = :userId")
	void updateUserEntityEmailVerificationStatus(@Param("emailStatus") boolean emailStatus, @Param("userId") String userId);
}
