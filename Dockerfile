FROM debian:wheezy

# Get noninteractive frontend for Debian to avoid some problems:
#    debconf: unable to initialize frontend: Dialog
ENV DEBIAN_FRONTEND noninteractive

RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys B97B0AFCAA1A47F044F244A07FCC7D46ACCC4CF8
RUN echo "deb http://apt.postgresql.org/pub/repos/apt/ wheezy-pgdg main" > /etc/apt/sources.list.d/pgdg.list
RUN echo "deb http://http.debian.net/debian wheezy-backports main" > /etc/apt/sources.list.d/backports.list
RUN apt-get update

# Install program to configure locales
RUN apt-get install -y locales
RUN dpkg-reconfigure locales
RUN echo 'en_US.UTF-8 UTF-8' >> /etc/locale.gen && locale-gen
# Set default locale for the environment
ENV LC_ALL en_US.UTF-8
ENV LANG en_US.UTF-8
ENV LANGUAGE en_US.UTF-8

# install postgres
RUN apt-get -y -q install python-software-properties software-properties-common
RUN apt-get install -y postgresql-9.2 postgresql-client-9.2 postgresql-contrib-9.2
RUN locale -a
RUN pg_dropcluster --stop 9.2 main && pg_createcluster --locale=en_EN.UTF8 --encoding=UTF8 --start 9.2 main

USER postgres
RUN echo "host all  all    0.0.0.0/0  md5" >> /etc/postgresql/9.2/main/pg_hba.conf
RUN echo "listen_addresses='*'"            >> /etc/postgresql/9.2/main/postgresql.conf
RUN echo "fsync = off"                     >> /etc/postgresql/9.2/main/postgresql.conf
RUN echo "work_mem=128MB"                  >> /etc/postgresql/9.2/main/postgresql.conf
RUN echo "timezone='Europe/Berlin'"          >> /etc/postgresql/9.2/main/postgresql.conf

WORKDIR /tmp

ADD db/create_test_database.sql /tmp/create_user_and_databases.sql
RUN service postgresql start && psql < /tmp/create_user_and_databases.sql

CMD ["/usr/lib/postgresql/9.2/bin/postgres", "-D", "/var/lib/postgresql/9.2/main", "-c", "config_file=/etc/postgresql/9.2/main/postgresql.conf"]
EXPOSE 5432
