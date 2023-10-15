# Project Introduction

This project utilizes the ZeroMQ library to simulate a distributed systems environment and employs MySQL replication for data synchronization.

## Environment Configuration

To ensure the program functions correctly, you need to configure the environment variables as follows:

## Configure JDK

1. Copy the path to the bin folder of your JDK (works with JDK version 8 onwards).

2. Edit the system environment variables.

3. In the system variables section, edit the 'PATH' variable.

4. Add the path you copied from the bin folder of the JDK.

5. Click OK until you exit.

## Configure Maven

1. Copy the path to the bin folder of your Maven (download the .zip of Maven and extract it into the Program Files).

2. Edit the system environment variables.

3. In the SYSTEM VARIABLES section, click Edit next to the 'PATH' variable.

4. Add the path you copied from the Maven bin folder.

5. Click OK until you exit.

## Visual Studio Code

1. Install the Visual Studio Code IDE from the official website to execute.

2. Download the "Extension Pack for Java" in the extensions.

To run some tests in JMeter

1. Copy the path of the text file.

2. Configure by instantiating classes according to the tests.

# MySQL Replica Configuration

A replica was implemented based on the MASTER-SLAVE model (primary replica) offered by MySQL for configuring replication; updates to this replica are done asynchronously.

## MASTER Configuration

1. Go to the MYSQL configuration file on the MASTER server, in this case, it may be called my.cnf (for Linux) or my.ini (for Windows). Below the [mysqld] section, edit:

```
server-id = 1
log-bin=mysql-bin
```

2. Open the MYSQL console and execute the following commands to create the user and grant replication to it.

```sql
CREATE USER 'replication_user'@'10.43.100.141' IDENTIFIED BY 'password';
GRANT REPLICATION SLAVE ON *.* TO 'replication_user'@'10.43.100.141';
FLUSH PRIVILEGES;
```

- CREATE USER: Enter the username you want to create followed by the IP address (you can enter a name instead of the IP address if desired; this is just a domain).
- IDENTIFIED BY: Enter the password you want to assign to this user.

3. Execute the command to view the log file and its position to save these values:

```sql
SHOW MASTER STATUS \G
```

## SLAVE Configuration

4. Go to the MYSQL configuration file on the SLAVE server, in this case, it may be called my.cnf (for Linux) or my.ini (for Windows). Below the [mysqld] section, edit:

```
server-id = 2
log-bin=mysql-bin
```

It's important that the server-id is different from the MASTER.

5. Configure the SLAVE server to replicate with the MASTER by executing the following command:

```sql
CHANGE REPLICATION SOURCE TO SOURCE_HOST='10.43.100.136', SOURCE_USER='replication_user', SOURCE_PASSWORD='password', SOURCE_LOG_FILE='mysql-bin.000001', SOURCE_LOG_POS=905, SOURCE_SSL=1;
```

- SOURCE TO SOURCE_HOST: Enter the IP address of the MASTER server.
- SOURCE_USER: The user created on the MASTER server.
- SOURCE_PASSWORD: The password assigned to the user on the MASTER server.
- SOURCE_LOG_FILE: The log file of the MASTER server, as seen in step 3.
- SOURCE_LOG_POS: The position of the log file of the MASTER server, as seen in step 3.
- SOURCE_SSL: Connection security, in this case, the value should be 1.

6. Start the SLAVE server by executing the following command:

```sql
START REPLICA USER='replication_user' PASSWORD='password';  
```

Here, enter the user and password created on the MASTER server.

7. Check the replica configuration status by executing the following command:

```sql
SHOW REPLICA STATUS \G   
```

If everything went correctly, it should display the following information: "waiting for the source to send event."

You can test by creating a database on the MASTER server; it should replicate to the SLAVE server, as well as performing CRUD operations in this database.

Note: If you had databases saved on the MASTER server before, they will not be created in the replica. Replication of information from MASTER to SLAVE only starts after this configuration.

# Execution

To run the project, you need to modify the "requirements.txt" file, which should have the following parameters for each line:

```
<requirement type>,<code>,<branch number>
```

- Requirement type: renew, return, request
- Code: If the requirement type is request, enter the book code; otherwise, enter the loan code.
- Branch number: The branch number to which the requirements are sent (1 or 2).

Afterward, execute the actors for branch 1 (classes: ExecuteActorReturn, ExecuteActorRenew, ExecuteActorRequest), the managers for both branches (classes: ExecuteGCTest, ExecuteGCTestTwo), and the request processes on the other computer (class: PSTest).

Please note that the above text has been translated into English as requested.
