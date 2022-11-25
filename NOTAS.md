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