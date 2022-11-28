package dev.ludwing.mobileappws;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Esta clase puede usarse desde cualquier punto de la aplicación para 
 * acceder a los beans que son generados por Spring Framework y que han sido
 * generados de forma automática, pero desde cualquier punto de la aplicación.
 * 
 * @author ludwingp
 *
 */
public class SpringApplicationContext implements ApplicationContextAware {
	
	private static ApplicationContext CONTEXT;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		CONTEXT = applicationContext;
		
	}
	
	/**
	 * Esta función recibe un nombre de bean y lo retorna. 
	 * Generalmente el nombre de un bean es el nombre de la clase pero iniciando con
	 * minúscula.
	 * 
	 * @param beanName
	 * @return
	 */
	public static Object getBean(String beanName) {
		return CONTEXT.getBean(beanName);
	}

}
