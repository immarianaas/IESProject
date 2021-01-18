### Comandos (e outras informações)



* _rabbitmq_:

```bash
docker run -it --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```



* _mysql_:

```bash
docker run --name mysql_iesproject -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=project -e MYSQL_USER=project -e MYSQL_PASSWORD=password -p 3306:3306 -d mysql/mysql-server:5.7
```



* _PhpMyAdmin_:

```bash
docker run --name mk-phpmyadmin -v phpmyadmin-volume:/etc/phpmyadmin/config.user.inc.php --link /mysql5:db -p 82:80 -d phpmyadmin/phpmyadmin
```



* correr o projeto:

```bash
sudo docker run -p 8090:8090 ies/project 
```



* Dockerfile:

```dockerfile
FROM adoptopenjdk/openjdk11:latest
ARG JAR_FILE=project-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```



* a geração de dados é feita através do _script_ `gen_data_v2.py`, que utiliza o `fake_data_generators_nd.py`