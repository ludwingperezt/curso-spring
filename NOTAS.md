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
	  proyecto:
	  ´´´
	  mvn install
	  ´´´
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
	  
