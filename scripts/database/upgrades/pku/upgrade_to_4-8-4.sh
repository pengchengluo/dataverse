# First step: drop database and create new database, then import the old data 
dropdb -U postgres -e dvndb_v4_0_to_v4_92
createdb -U postgres --encoding=UTF-8 --owner=dvnapp_v4_84 --echo dvndb_v4_0_to_v4_92
psql dvndb_v4_0_to_v4_92 < dvndb_v4_0_20180910015238.sql

# Second step: start the glassfish and reinstall the new version of dataverse
# ...

# Third step: upgrade the dataverse database,
# following uncomment command used for comparing two database schemes
# pg_dump -s -f upgrade_to_484.sql dvndb_v4_0_to_v4_84
# pg_dump -s -f 484.sql dvndb_v4_84
# java -jar apgdiff-2.4.jar --ignore-start-with upgrade_to_484.sql 484.sql > upgrade_pku_to_v4.8.4.sql
psql -d dvndb_v4_0_to_v4_92 -f ../upgrade_v4.0_to_v4.0.1.sql
psql -d dvndb_v4_0_to_v4_92 -f ../upgrade_v4.0.1_to_v4.1.sql
psql -d dvndb_v4_0_to_v4_92 -f ../upgrade_v4.1_to_v4.2.sql
psql -d dvndb_v4_0_to_v4_92 -f ../upgrade_v4.2.1_to_v4.2.2.sql
psql -d dvndb_v4_0_to_v4_92 -f ../upgrade_v4.2.4_to_4.3.sql
psql -d dvndb_v4_0_to_v4_92 -f ../upgrade_v4.4_to_v4.5.sql
psql -d dvndb_v4_0_to_v4_92 -f ../upgrade_v4.5_to_v4.5.1.sql
psql -d dvndb_v4_0_to_v4_92 -f ../upgrade_v4.5.1_to_v4.6.sql
psql -d dvndb_v4_0_to_v4_92 -f ../upgrade_v4.6_to_v4.6.1.sql
psql -d dvndb_v4_0_to_v4_92 -f ../upgrade_v4.6.1_to_v4.6.2.sql
psql -d dvndb_v4_0_to_v4_92 -f ../upgrade_v4.6.2_to_v4.7.sql
psql -d dvndb_v4_0_to_v4_92 -f ../upgrade_v4.7_to_v4.7.1.sql
psql -d dvndb_v4_0_to_v4_92 -f ../upgrade_v4.7.1_to_v4.8.sql
psql -d dvndb_v4_0_to_v4_92 -f ../upgrade_v4.8.3_to_v4.8.4.sql
psql -d dvndb_v4_0_to_v4_92 -f ../upgrade_v4.8.6_to_v4.9.0.sql
psql -d dvndb_v4_0_to_v4_92 -f ../upgrade_v4.9.1_to_v4.9.2.sql
psql -d dvndb_v4_0_to_v4_92 -f ./upgrade_pku_to_v4.8.4.sql