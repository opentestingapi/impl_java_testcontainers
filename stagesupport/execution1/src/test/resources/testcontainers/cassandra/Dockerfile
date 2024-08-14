#FROM ${REPOSITORY}/library/cassandra:4.0.7
FROM docker.io/library/cassandra

# Cassandra fix enable authenticator
RUN sed -i 's/^authenticator.\+$/authenticator: PasswordAuthenticator/g' /etc/cassandra/cassandra.yaml

# Cassandra fix disable AllowAllAuthorizer
RUN sed -i 's/AllowAllAuthorizer/org.apache.cassandra.auth.CassandraAuthorizer/g' /etc/cassandra/cassandra.yaml
