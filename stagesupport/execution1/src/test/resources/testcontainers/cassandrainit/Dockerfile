FROM ${REPOSITORY}/library/cassandra:4.0.7

COPY init /init

USER root
RUN chmod 777 /init/*
RUN chmod +x /init/*

CMD ./init/execInit.sh