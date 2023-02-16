package dev.ludwing.mobileappws;

import java.util.Arrays;
import java.util.Collection;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import dev.ludwing.mobileappws.io.entity.AuthorityEntity;
import dev.ludwing.mobileappws.io.entity.RoleEntity;
import dev.ludwing.mobileappws.io.entity.UserEntity;
import dev.ludwing.mobileappws.io.repositories.AuthorityRepository;
import dev.ludwing.mobileappws.io.repositories.RoleRepository;
import dev.ludwing.mobileappws.io.repositories.UserRepository;
import dev.ludwing.mobileappws.shared.Utils;

 /**
  * Un @Component es un tipo especial de @Bean que permite que la clase
  * sea cargada automáticamente por Spring durante la carga inicial de la aplicación.
  * 
  * En este caso estamos definiendo un bean que escucha al evento ApplicationReadyEvent
  * que se dispara cuando la aplicación está completamente cargada, es decir, que todos los
  * Beans están creados y listos para funcionar.
  * 
  * En este caso usamos la función onApplicationEvent que tiene la anotación @EventListener
  * para que escuche el evento ApplicationReadyEvent y que cuando eso suceda, genere el código
  * para insertar el role de superadmin y el primer usuario admin.
  * 
  * @author ludwingp
  *
  */
@Component
public class InitialUsersSetup {
	
	@Autowired
	AuthorityRepository authorityRepository;
	
	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	BCryptPasswordEncoder encoder;
	
	@Autowired
	Utils utils;
	
	@Autowired
	UserRepository userRepository;

	@EventListener
	@Transactional
	public void onApplicationEvent(ApplicationReadyEvent event) {
		System.out.println("Disparado desde el evento de carga de la aplicación...");
		
		// Crear las Authorities
		AuthorityEntity readAuthority = createAuthority("READ_AUTHORITY");
		AuthorityEntity writeAuthority = createAuthority("WRITE_AUTHORITY");
		AuthorityEntity deleteAuthority = createAuthority("DELETE_AUTHORITY");
		
		// Crear los roles
		RoleEntity userRole = createRole("ROLE_USER", Arrays.asList(readAuthority, writeAuthority));
		RoleEntity adminRole = createRole("ROLE_ADMIN", Arrays.asList(readAuthority, writeAuthority, deleteAuthority));
		
		// Si el rol admin ya existe, ya no es necesario crear al usuario admin.
		if (adminRole == null) return;
		
		UserEntity adminUser = new UserEntity();
		adminUser.setFirstName("admin");
		adminUser.setLastName("admin");
		adminUser.setEmail("admin@admin.admin");
		adminUser.setUserId(utils.generateUserId(30));
		adminUser.setEncryptedPassword(encoder.encode("123456789"));
		adminUser.setRoles(Arrays.asList(adminRole));
		
		userRepository.save(adminUser);
	}
	
	/**
	 * Función para crear las Authorities al inicio de la aplicación.
	 * 
	 * La anotación @Transactional debe usarse para cada función que ejecuta queryies de modificación
	 * en la base de datos.
	 * 
	 * @param name
	 * @return
	 */
	@Transactional
	private AuthorityEntity createAuthority(String name) {
		
		AuthorityEntity authority = authorityRepository.findByName(name);
		
		if (authority == null) {
			authority = new AuthorityEntity(name);
			authorityRepository.save(authority);
		}
		
		return authority;
	}
	
	/**
	 * Crea un rol con sus authorities relacionadas.
	 * 
	 * La anotación @Transactional debe usarse para cada función que ejecuta queryies de modificación
	 * en la base de datos.
	 * 
	 * @param name
	 * @param authorities
	 * @return
	 */
	@Transactional
	private RoleEntity createRole(String name, Collection<AuthorityEntity> authorities) {
		RoleEntity role = roleRepository.findByName(name);
		
		if (role == null) {
			role = new RoleEntity(name);
			role.setAuthorities(authorities);
			roleRepository.save(role);
		}
		return role;
	}


}
