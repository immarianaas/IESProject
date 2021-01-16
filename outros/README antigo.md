# IESProject

- Alexandre Antunes Rodrigues: 92951

- Ana Rita Martins Ferrolho: 88822

- João Pedro Lacerda Vasconcelos:  89022

- Luís Miguel Páscoa Teixeira: 76511

- Mariana Sousa Pinho Santos:  93257



### link para o relatório:

https://docs.google.com/document/d/1Af_r418LNgigEB2g1XdCrRuQbWzGK7ZDH9mrDSDqgFw/edit?usp=sharing



---

---

## Instruções para correr o projeto:

**1º** é necessário executar o servidor da base de dados MySql no _docker_, utilizando o _script_ `run_mysql.sh`, se for pela primeira vez. Das vezes seguintes basta correr o comando `docker start /mysql5`.

**2º** é necessário executar o servidor RabbitMQ, utilizando o _script_ `run_rabbitmq.sh`.

------------------------------------------------------------------------------ o projeto já deve funcionar a este ponto! -----------------------------------------------------------------------------

Para gerar dados, fazer os seguintes passos:

**3º** instalar o módulo python `pika` caso já não esteja (este comando deve chegar `sudo python3 -m pip install pika --upgrade`).

**4º** correr o script python `gen_data.py`. Não é suposto aparecer nenhum output! Este programa enviará de x em x segundos dados para o servidor RabbitMQ. Se abrirem o projeto Spring MVC dá para ver ele a receber dados e a guardar na BD, para confirmar que está tudo a funcionar.

