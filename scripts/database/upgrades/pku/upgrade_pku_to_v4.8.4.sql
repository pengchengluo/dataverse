
ALTER TABLE datasetversion
	DROP CONSTRAINT uq_datasetversion;

ALTER TABLE dataversefieldtypeinputlevel
	DROP CONSTRAINT unq_dataversefieldtypeinputlevel_add;

DROP INDEX index_datafile_md5;

DROP INDEX index_explicitgroup_groupalias;

DROP INDEX index_maplayermetadata_datafile_id;

DROP INDEX index_persistedglobalgroup_persistedgroupalias;

/*DROP TABLE competitiongroup;

DROP TABLE competitiongroup_authenticateduser;

DROP TABLE highqualitydataverse;

DROP TABLE joingrouprequests;

DROP TABLE pkuiaaauser;

DROP SEQUENCE competitiongroup_id_seq;

DROP SEQUENCE highqualitydataverse_id_seq;

DROP SEQUENCE pkuiaaauser_id_seq;*/

ALTER TABLE authenticateduser
	DROP COLUMN name,
/*	DROP COLUMN usertype,*/
	ALTER COLUMN createdtime DROP DEFAULT;

/*ALTER TABLE builtinuser
	DROP COLUMN address,
	DROP COLUMN cellphone,
	DROP COLUMN certificatenumber,
	DROP COLUMN certificatetype,
	DROP COLUMN city,
	DROP COLUMN country,
	DROP COLUMN department,
	DROP COLUMN education,
	DROP COLUMN gender,
	DROP COLUMN officephone,
	DROP COLUMN otheremail,
	DROP COLUMN professionaltitle,
	DROP COLUMN province,
	DROP COLUMN researchinterest,
	DROP COLUMN speciality,
	DROP COLUMN supervisor,
	DROP COLUMN usertype,
	DROP COLUMN zipcode;*/

ALTER TABLE datafile
	ALTER COLUMN rootdatafileid DROP DEFAULT,
	ALTER COLUMN rootdatafileid SET NOT NULL;

/*ALTER TABLE dataset
	DROP COLUMN pubcolhandle;*/

ALTER TABLE datasetversion
	DROP COLUMN inreview;

/*ALTER TABLE dataverse
	DROP COLUMN affiliation_zh,
	DROP COLUMN description_zh,
	DROP COLUMN name_zh,
	DROP COLUMN dr_user_type;*/

/*ALTER TABLE explicitgroup
	DROP COLUMN requestjoinusertype;*/

ALTER TABLE ingestreport
	ALTER COLUMN report TYPE text /* TYPE change - table: ingestreport original: character varying(255) new: text */;

/*ALTER TABLE usernotification
	DROP COLUMN message;*/

ALTER TABLE dataset
	ADD CONSTRAINT fk_dataset_harvestingclient_id FOREIGN KEY (harvestingclient_id) REFERENCES harvestingclient(id);

ALTER TABLE template
	ADD CONSTRAINT fk_template_termsofuseandaccess_id FOREIGN KEY (termsofuseandaccess_id) REFERENCES termsofuseandaccess(id);

CREATE INDEX index_datafile_checksumvalue ON datafile USING btree (checksumvalue);

ALTER TABLE datasetlock
	ADD COLUMN reason character varying(255) NOT NULL;

ALTER TABLE maplayermetadata
	ALTER COLUMN isjoinlayer DROP DEFAULT,
	ALTER COLUMN lastverifiedstatus TYPE integer /* TYPE change - table: maplayermetadata original: bigint new: integer */;

ALTER TABLE datasetversion
	ADD CONSTRAINT fk_datasetversion_termsofuseandaccess_id FOREIGN KEY (termsofuseandaccess_id) REFERENCES termsofuseandaccess(id);