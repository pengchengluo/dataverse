ALTER TABLE datasetversion
	DROP CONSTRAINT uq_datasetversion;

ALTER TABLE dataversefieldtypeinputlevel
	DROP CONSTRAINT unq_dataversefieldtypeinputlevel_add;

DROP INDEX index_datafile_md5;

DROP INDEX index_explicitgroup_groupalias;

DROP INDEX index_maplayermetadata_datafile_id;

DROP INDEX index_persistedglobalgroup_persistedgroupalias;

ALTER TABLE authenticateduser
	DROP COLUMN name,
	ALTER COLUMN createdtime DROP DEFAULT;

ALTER TABLE datafile
	DROP COLUMN name,
	ALTER COLUMN rootdatafileid DROP DEFAULT,
	ALTER COLUMN rootdatafileid SET NOT NULL;

ALTER TABLE datasetversion
	DROP COLUMN inreview;

ALTER TABLE ingestreport
	ALTER COLUMN report TYPE text /* TYPE change - table: ingestreport original: character varying(255) new: text */;

ALTER TABLE dataset
	ADD CONSTRAINT fk_dataset_harvestingclient_id FOREIGN KEY (harvestingclient_id) REFERENCES harvestingclient(id);

ALTER TABLE template
	ADD CONSTRAINT fk_template_termsofuseandaccess_id FOREIGN KEY (termsofuseandaccess_id) REFERENCES termsofuseandaccess(id);

CREATE INDEX index_builtinuser_username ON builtinuser USING btree (username);

CREATE INDEX index_datafile_checksumvalue ON datafile USING btree (checksumvalue);
