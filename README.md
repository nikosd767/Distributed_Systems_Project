# Project : Real Estates Management System

The following commands are used to compile and run the Real Estate project. \
Main in RealEstatesApplication.class provides a simple code in collaboration with \
Spring Boot in order to run the program and check if the combination of the \
classes(entities, service, repositories, controllers) manages to create a Real \
Estate app with all the functions referred to the pronunciation of the project. 


## Usage

Clone Project using 

```
git clone https://github.com/nikosd767/Distributed_Systems_Project.git 
```

Compile using 

```
mvn compile
```

Create a jar using 

```
mvn package
```

Run using 

```
mvn spring-boot:run
```

Connect to database using

```
#PGPASSWORD=r3Jw5SnpaLABKvAPutqsn8Ilo2zGaLeQ psql -h dpg-ctuh0prqf0us73f46hkg-a.oregon-postgres.render.com -U reuser redb_6346
spring.datasource.username=reuser
spring.datasource.password=r3Jw5SnpaLABKvAPutqsn8Ilo2zGaLeQ
spring.datasource.url=jdbc:postgresql://dpg-ctuh0prqf0us73f46hkg-a.oregon-postgres.render.com:5432/redb_6346
```
