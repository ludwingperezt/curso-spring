package dev.ludwing.mobileappws.io.repositories;

import org.springframework.data.repository.CrudRepository;

import dev.ludwing.mobileappws.io.entity.PasswordResetTokenEntity;

public interface PasswordResetTokenRepository extends CrudRepository<PasswordResetTokenEntity, Long> {
	PasswordResetTokenEntity findByToken(String token);
}
