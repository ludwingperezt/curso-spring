package dev.ludwing.mobileappws.io.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import dev.ludwing.mobileappws.io.entity.RoleEntity;

@Repository
public interface RoleRepository extends CrudRepository<RoleEntity, Long> {
	
	RoleEntity findByName(String name);

}
