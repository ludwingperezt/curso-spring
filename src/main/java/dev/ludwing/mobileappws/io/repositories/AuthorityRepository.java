package dev.ludwing.mobileappws.io.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import dev.ludwing.mobileappws.io.entity.AuthorityEntity;

@Repository
public interface AuthorityRepository extends CrudRepository<AuthorityEntity, Long> {
	AuthorityEntity findByName(String name);
}
