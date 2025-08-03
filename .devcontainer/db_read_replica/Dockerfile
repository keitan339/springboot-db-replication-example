FROM postgres:17.5

COPY ./entrypoint.sh /etc/postgresql/entrypoint.sh
RUN chmod 755 /etc/postgresql/entrypoint.sh

ENTRYPOINT [ "/etc/postgresql/entrypoint.sh" ]
