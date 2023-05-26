# Configuración del ambiente

Para que el programa funcione correctamente, primero se tiene que configurar las variables de entorno de la siguiente manera:

## Configurar el jdk

1.Copiar la ruta de acceso a la carpeta bin de tu jdk (solo funciona con jdk version 8 en adelante)

2.Editar las variables de entorno del sistema

3.En las variables del sistema editar la variable 'PATH'

4.Añadir la ruta de acceso que copiamos del la carpeta bin del jdk anteriormente

5.Le damos OK hasta salir

## Configurar maven

1.Copiar la ruta de acceso a la carpeta bin de tu maven (descargar el .zip del maven y descomprimirlo en los archivos de programa)

2.Editar las variables de entorno del sistema

3.En la seccion VARIABLES DEL SISTEMA dar click en editar encima de la variable 'PATH'

4.Añadir la ruta de acceso que copiamos del la carpeta bin del maven anteriormente

5.Le damos OK hasta salir

## Visual studio code

1.Instalar desde la pagina oficial el IDE de visual studio code para poder ejecutar 

2.Descargar el "Extension Pack for Java" en las extensiones

Para ejecutar algunas pruebas en JMeter

1.Copiar el path del archivo de texto

2.Configurar instanciando las clases de acuerdo de las pruebas

# Configuracion de la replica MySQL

Se implemento una replica en base al modelo MASTER-SLAVE (replica primaria) que ofrece MYSQL para configurar la replicación, las actualizaciones de esta replica se hacen de manera asincrona.

## Configuracion del MASTER

1. Ir al archivo de configuración de MYSQL en el servidor MASTER, en este caso se puede llamar my.cnf (para linux) o my.ini (para Windows). Debajo de la seccion de [mysqld] editar:

```
server-id = 1
log-bin=mysql-bin
```

2. Abrir la consola de MYSQL y ejecutar los siguentes comandos para crear el usuario y otogar la replicacion a este.

```sql
CREATE USER 'replication_user'@'10.43.100.141' IDENTIFIED BY 'password';
GRANT REPLICATION SLAVE ON *.* TO 'replication_user'@'10.43.100.141';
FLUSH PRIVILEGES;
```

- CREATE USER: Se coloca el nombre del usuario que queremos crear seguido de la direccion IP (se puedo colocar un nombre en lugar de la dirección IP si se quiere, este solo es un domnio).
- IDENTIFIED BY: Se coloca la contraseña que se le quiere otorgar a este usuario que creamos.

3. Ejecutar el comando para ver el archivo log y la posicion en que se encuentra para guardar estos valores:

```sql
SHOW MASTER STATUS \G
```

## Configuracion del SLAVE

4. Ir al archivo de configuración de MYSQL en el servidor SLAVE, en este caso se puede llamar my.cnf (para linux) o my.ini (para Windows). Debajo de la seccion de [mysqld] editar:

```
server-id = 2
log-bin=mysql-bin
```

Es importante que el servir-id sea diferente al del MASTER

5. Configurar el servidor SLAVE para poder hacer la replicacion con el master, ejecutando el siguiente comando:

```sql
CHANGE REPLICATION SOURCE TO SOURCE_HOST='10.43.100.136', SOURCE_USER='replication_user', SOURCE_PASSWORD='password', SOURCE_LOG_FILE='mysql-bin.000001', SOURCE_LOG_POS=905, SOURCE_SSL=1;
```

- SOURCE TO SOURCE_HOST: se coloca la dirección IP del servidor MASTER.
- SOURCE_USER: usuario que se creo en el servidor MASTER.
- SOURCE_PASSWORD: contraseña que se le otorgo al usuario en el servidor MASTER.
- SOURCE_LOG_FILE: archivo log del servidor MASTER que se puede ver en el paso 3.
- SOURCE_LOG_POS: posición del archivo log del servidor Master que se puede ver en el paso 3.
- SOURCE_SSL: seguridad de la conexion, en este caso el valor debe ser 1.

6. Inicar el sevidor SLAVE ejecutando el siguiente comando:

```sql
START REPLICA USER='replication_user' PASSWORD='password';  
```

Aqui se coloca el usuario y el password que se crearon en el servidor MASTER.

7. Ver el estado de la configuración de la replica ejecuntado el siguiente comando:

```sql
SHOW REPLICA STATUS \G   
```

Si todo salio correctamente, deberia salir la siguiente informacion: waiting for source to send event

Puede probar crando una base de datos en el servidor MASTER, este se debe replicar al servidor SLAVE, como tambien el hacer operaciones CRUD en esta base de datos.

Nota: si se tenian bases de datos guardadas anteriormente en el servidor MASTER, estas no se van a crear en la replica. Unicamente se empieza a replicar la información de MASTER al SLAVE despues de haber hecho esta configuracion. 


# Ejecucion

Para poder ejecutar el proyecto tiene que modificar el archivo de "requermientos.txt", el cual tiene debe tener los siguientes parametros para cada linea:

```
<tipo de requerimiento>,<codigo>,<numero de sede>
```

-Tipo de requerimiento: renovar, devolver, solicitar
-codigo: si el tipo de requerimiento es solicitar se coloca el codigo del libro, de lo contrario el codigo el prestamo
-numero de sede: numero de la sede al cual se le envian los requerimientos (1 o 2)

Posteriormente se debe ejecutar los actores de la sede 1 (clases: ActorDevolver, ActorRenovar, ActorSolicitar), los gestores de ambas sedes (clases: GCTest, GCTestDos) y los proceso solicitantes en el otro computador(clase: PSTest). 