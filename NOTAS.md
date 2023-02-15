# Notas #

1.  Cuando se actualiza una tabla (se agrega o quita un campo) se debe detener 
    el servidor tomcat, borrar la tabla y luego encender de nuevo tomcat para 
    que la tabla sea creada de nuevo con los atributos correctos.
    
2.  Al agregar Spring Boot Security al proyecto, automáticamente TODOS los endpoints
	quedan protegidos.  
	
	Al iniciar el servicio, en la consola se muestra una contraseña
	temporal generada por spring security que puede usarse para usar los endpoints
	a través del navegador (al intentar entrar a un endpoint se pedirá usuario
	y contraseña.  El usuario es "user" y la contraseña aparece en los logs
	al iniciar la aplicación).

3.  Para el despliegue sin spring boot tools:
	(Esto puede ser de utilidad cuando se editan los archivos de un proyecto spring en un
	IDE o editor que no sea STS, por ejemplo VS Code).

	- Moverse a la carpeta root del proyecto
	- Verificar que todas las dependencias están instaladas y luego compilar el 
	  proyecto (verificar que todos los valores del archivo applications.settings sean los
	  adecuados para el despliegue):
	  ´´´
	  mvn install
	  ´´´
	  * Otras opciones son: "mvn clean install" o "mvn package"
	- Correr la aplicación con apache tomcat
	  ´´´
	  mvn spring-boot:run
	  ´´´
4.  Se recomienda crear un Context Path para el proyecto, el cual es de utilidad al hacer
    deploy a cualquier servidor tomcat, el cual ayuda a tomcat a diferenciar a qué aplicación
    redirigir las solicitudes entrantes en base a la primera parte del path.

    La definición del context path se puede hacer de varias formas, pero en este ejemplo se hace 
    en application.properties bajo la variable
    
      ´´´ 
      server.servlet.context-path=/mobile-app-ws
      ´´´
    
    Cuando se usa un context path, todas las URIs deben comenzar con /mobile-app-ws
    
5.  Se puede correr la aplicación como una java application.  Para ello simplemente se busca
    el archivo .jar generado en la carpeta target/ del proyecto y para correr como java
    application se usa el siguiente comando (el comando mvn install genera ese archivo .jar):
	  
	  ´´´
	  java -jar <nombre del archivo>.jar
	  ´´´

	* En este caso, el servidor apache tomcat está incluído en el bundle del archivo .jar

6.  Si lo que se desea es generar un archivo que pueda ser desplegado en un servidor tomcat
	existente, entonces lo que se hace es lo siguiente:
	
	- Hacer que la clase principal del proyecto extienda de SpringBootServletInitializer
	- Luego se sobreescribe el método configure()
	- Cambiar el empaquetado de jar a war en el archivo pom.xml (etiqueta packaging)
		(War significa Web Application aRchive).
	- Agregar la dependencia spring-boot-starter-tomcat con scope provided para indicar
	  que tomcat se agrega como dependencia pero solo en runtime y no en compile time.
	- Compilar el proyecto.
	  
7.  CORS: Hay varias formas de habilitar CORS. Se puede habilitar para un endpoint específico, para
	un controller o para toda la aplicación.
	
	Si se desea habilitar CORS para un endpoint o un controlador específico se debe utilizar
	la anotación @CrossOrigin(origins=...) sobre el método correspondiente al endpoint
	que se va habilitar (en caso de un endpoint) o sobre la clase completa del controlador
	(para habilitar CORS para todos los endpoints que le pertenecen).
	
	Para indicar qué dominios se habilitan para CORS, en el parámetro origins de la anotación
	se puede indicar según se necesite.  A continuación algunos ejemeplos:
	- @CrossOrigin(origins="*") -- Esto habilita CORS para todos los dominios. Es una configuración insegura.
	- @CrossOrigin(origins="http://midominio.com") -- Esto habilita CORS para un dominio específico
	- @CrossOrigin(origins={"http://localhost:8090", "http://localhost:8091"}) -- Esto habilita
		CORS para varios dominios diferentes.
		
	También es posible configurar CORS para toda la aplicación usando una clase de configuración
	que implemente la interfaz WebMvcConfigurer y que sobreescriba el método 
	´´´public void addCorsMappings(CorsRegistry registry)´´´ En ese método se debe configurar
	las rutas, los métodos y los origenes permitidos para CORS. (Ver la clase WebConfig en el
	paquete dev.ludwing.mobileappws para referencia).
	
	Para las rutas que están protegidas por autenticación también es necesario configurar CORS
	en la configuración de WebSecurity (ver clase WebSecurity del paquete dev.ludwing.mobileappws.security)
	Esta configuración permite que se procesen en primer lugar las peticiones pre-flight de CORS
	y que no se den por inválidas, ya que éstas no tienen cookies o headers de autenticación.
	
	Ver: https://docs.spring.io/spring-security/site/docs/4.2.x/reference/html/cors.html 
	
8.  Documentación con swagger
	Para que la documentación con swagger funcionara, fue necesario agregar el @Bean webEndpointServletHandlerMapping
	en la clase MobileAppWsApplication
	Las direcciones para acceder a la documentación de la API con swagger son:
	
	http://localhost:8080/mobile-app-ws/v2/api-docs
	http://localhost:8080/mobile-app-ws/swagger-ui/
	
