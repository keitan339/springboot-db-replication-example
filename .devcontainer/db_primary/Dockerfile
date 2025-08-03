FROM postgres:17.5

COPY ./init.sh /docker-entrypoint-initdb.d/init.sh
RUN chmod 755 /docker-entrypoint-initdb.d/init.sh

COPY ./pg_hba.conf /etc/postgresql/pg_hba.conf

CMD [ "-c", "hba_file=/etc/postgresql/pg_hba.conf" ]
