drop database if exists caas_test;
drop user if exists caas_test;

create user caas_test SUPERUSER LOGIN PASSWORD 'l3tm31n';
create database caas_test owner caas_test ENCODING='UTF8' LC_COLLATE='en_US.UTF-8' LC_CTYPE='en_US.UTF-8';
