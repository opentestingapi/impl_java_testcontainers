CREATE ROLE IF NOT EXISTS projectstarter
WITH SUPERUSER = false
 AND LOGIN = true
 AND PASSWORD =  'projectstarter';

CREATE KEYSPACE IF NOT EXISTS projectstarter
WITH REPLICATION = {
	'class' : 'SimpleStrategy',
	'replication_factor' : 1
}
AND DURABLE_WRITES = true;

GRANT ALL ON KEYSPACE projectstarter TO projectstarter;
REVOKE ALTER ON KEYSPACE projectstarter FROM projectstarter;
REVOKE DROP ON KEYSPACE projectstarter FROM projectstarter;

CREATE ROLE IF NOT EXISTS projectstarter_ddl
WITH SUPERUSER = false
 AND LOGIN = true
 AND PASSWORD =  'projectstarter_ddl';

GRANT CREATE ON KEYSPACE projectstarter TO projectstarter_ddl;
GRANT ALTER ON KEYSPACE projectstarter TO projectstarter_ddl;
GRANT DROP ON KEYSPACE projectstarter TO projectstarter_ddl;

CREATE TABLE if not exists projectstarter.testtable(
   test text,   
   PRIMARY KEY (test)
);
