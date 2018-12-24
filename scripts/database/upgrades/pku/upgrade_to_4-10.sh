# First step: drop database and create new database, then import the old data 
dropdb -U postgres -e dvndb_v4_0_to_v4_94
createdb -U postgres --encoding=UTF-8 --owner=dvnapp_v4_94 --echo dvndb_v4_0_to_v4_94
psql dvndb_v4_0_to_v4_94 < ../../dvndb_v4_0_20181220022045.sql

# Second step: start the glassfish and reinstall the new version of dataverse
# ...

# Third step: upgrade the dataverse database,
# following uncomment command used for comparing two database schemes
# pg_dump -s -f upgrade_to_494.sql dvndb_v4_0_to_v4_94
# pg_dump -s -f 494.sql dvndb_v4_9_4
# java -jar apgdiff-2.4.jar --ignore-start-with upgrade_to_494.sql 494.sql > upgrade_pku_to_v4.9.4.sql
psql -d dvndb_v4_0_to_v4_94 -f ../upgrade_v4.0_to_v4.0.1.sql
psql -d dvndb_v4_0_to_v4_94 -f ../upgrade_v4.0.1_to_v4.1.sql
psql -d dvndb_v4_0_to_v4_94 -f ../upgrade_v4.1_to_v4.2.sql
psql -d dvndb_v4_0_to_v4_94 -f ../upgrade_v4.2.1_to_v4.2.2.sql
psql -d dvndb_v4_0_to_v4_94 -f ../upgrade_v4.2.4_to_4.3.sql
psql -d dvndb_v4_0_to_v4_94 -f ../upgrade_v4.4_to_v4.5.sql
psql -d dvndb_v4_0_to_v4_94 -f ../upgrade_v4.5_to_v4.5.1.sql
psql -d dvndb_v4_0_to_v4_94 -f ../upgrade_v4.5.1_to_v4.6.sql
psql -d dvndb_v4_0_to_v4_94 -f ../upgrade_v4.6_to_v4.6.1.sql
psql -d dvndb_v4_0_to_v4_94 -f ../upgrade_v4.6.1_to_v4.6.2.sql
psql -d dvndb_v4_0_to_v4_94 -f ../upgrade_v4.6.2_to_v4.7.sql
psql -d dvndb_v4_0_to_v4_94 -f ../upgrade_v4.7_to_v4.7.1.sql
psql -d dvndb_v4_0_to_v4_94 -f ../upgrade_v4.7.1_to_v4.8.sql
psql -d dvndb_v4_0_to_v4_94 -f ../upgrade_v4.8.3_to_v4.8.4.sql
psql -d dvndb_v4_0_to_v4_94 -f ./upgrade_pku_to_v4.9.0.sql
psql -d dvndb_v4_0_to_v4_94 -f ../upgrade_v4.8.6_to_v4.9.0.sql
psql -d dvndb_v4_0_to_v4_94 -f ../upgrade_v4.9.1_to_v4.9.2.sql
psql -d dvndb_v4_0_to_v4_94 -f ../upgrade_v4.9.2_to_v4.9.3.sql
psql -d dvndb_v4_0_to_v4_94 -f ./upgrade_pku_to_v4.9.4.sql
psql -d dvndb_v4_0_to_v4_94 -f ../upgrade_v4.9.4_to_v4.10.sql