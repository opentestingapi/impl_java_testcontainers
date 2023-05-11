#! /bin/bash

sleep 5
while ! cqlsh -u cassandra -p cassandra -e 'describe cluster' ; do    
    echo 'waiting for cassandra...'
    sleep 5 
done

# run CQLs to create the required keyspaces and users
echo 'env2.cql'
cqlsh -u cassandra -p cassandra -f /init/env2.cql
echo 'done'