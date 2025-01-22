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
#PGPASSWORD=5quCTZRJEPaLJlegSBBuaaPRkkdPjf6w psql -h dpg-cu8hvkq3esus73b02u4g-a.oregon-postgres.render.com -U reuser redb_jji4
spring.datasource.username=reuser
spring.datasource.password=5quCTZRJEPaLJlegSBBuaaPRkkdPjf6w
spring.datasource.url=jdbc:postgresql://dpg-cu8hvkq3esus73b02u4g-a.oregon-postgres.render.com:5432/redb_jji4
```
