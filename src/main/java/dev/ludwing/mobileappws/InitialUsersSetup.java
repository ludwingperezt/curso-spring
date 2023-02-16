package dev.ludwing.mobileappws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import dev.ludwing.mobileappws.io.entity.AuthorityEntity;
import dev.ludwing.mobileappws.io.repositories.AuthorityRepository;

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

	@EventListener
	public void onApplicationEvent(ApplicationReadyEvent event) {
		System.out.println("Disparado desde el evento de carga de la aplicación...");
		
		AuthorityEntity readAuthority = createAuthority("READ_AUTHORITY");
		AuthorityEntity writeAuthority = createAuthority("WRITE_AUTHORITY");
		AuthorityEntity deleteAuthority = createAuthority("DELETE_AUTHORITY");
	}
	
	/**
	 * Función para crear las Authorities al inicio de la aplicación
	 * @param name
	 * @return
	 */
	private AuthorityEntity createAuthority(String name) {
		
		AuthorityEntity authority = authorityRepository.findByName(name);
		
		if (authority == null) {
			authority = new AuthorityEntity(name);
			authorityRepository.save(authority);
		}
		
		return authority;
	}
}
