package dev.ludwing.mobileappws.io.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import dev.ludwing.mobileappws.io.entity.AddressEntity;
import dev.ludwing.mobileappws.io.entity.UserEntity;

// Aqui CrudRepository<AddressEntity, Long> se usa Long porque es el tipo de dato
// del ID de la clase AddressEntity.

@Repository
public interface AddressRepository extends CrudRepository<AddressEntity, Long> {
	
	List<AddressEntity> findAllByUserDetails(UserEntity user);

}
