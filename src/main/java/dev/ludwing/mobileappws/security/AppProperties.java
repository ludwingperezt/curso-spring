package dev.ludwing.mobileappws.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Esta clase se utiliza para leer los valores del archivo
 * application.properties (para variables de entorno).
 * 
 * Se declara como un @Component para que pueda se utilizada con
 * @Autowired desde cualquier punto de la aplicación que necesite leer
 * variables de entorno.
 * 
 * @author ludwingp
 *
 */
@Component
public class AppProperties {
	
	@Autowired
	private Environment env;
	
	/**
	 * Esta función obtiene y retorna el valor que esté definido para la
	 * clave "tokenSecret" en el archivo application.properties.
	 * @return
	 */
	public String getTokenSecret() {
		return env.getProperty("tokenSecret");
	}
}
