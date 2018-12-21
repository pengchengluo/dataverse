ALTER TABLE datafile
	DROP COLUMN name;

ALTER TABLE alternativepersistentidentifier
	ADD CONSTRAINT fk_alternativepersistentidentifier_dvobject_id FOREIGN KEY (dvobject_id) REFERENCES dvobject(id);

ALTER TABLE confirmemaildata
	ADD CONSTRAINT fk_confirmemaildata_authenticateduser_id FOREIGN KEY (authenticateduser_id) REFERENCES authenticateduser(id);

ALTER TABLE dataset
	ADD CONSTRAINT fk_dataset_harvestingclient_id FOREIGN KEY (harvestingclient_id) REFERENCES harvestingclient(id);

ALTER TABLE datasetversion
	ADD CONSTRAINT unq_datasetversion_0 UNIQUE (dataset_id, versionnumber, minorversionnumber);


/**
  升级时如下外键出错
*/
ALTER TABLE datasetversion
	ADD CONSTRAINT fk_datasetversion_termsofuseandaccess_id FOREIGN KEY (termsofuseandaccess_id) REFERENCES termsofuseandaccess(id);

ALTER TABLE dataverse_citationdatasetfieldtypes
	ADD CONSTRAINT dataverse_citationdatasetfieldtypes_citationdatasetfieldtype_id FOREIGN KEY (citationdatasetfieldtype_id) REFERENCES datasetfieldtype(id);

ALTER TABLE dataverse_citationdatasetfieldtypes
	ADD CONSTRAINT fk_dataverse_citationdatasetfieldtypes_dataverse_id FOREIGN KEY (dataverse_id) REFERENCES dvobject(id);

ALTER TABLE dataversefieldtypeinputlevel
	ADD CONSTRAINT unq_dataversefieldtypeinputlevel_0 UNIQUE (dataverse_id, datasetfieldtype_id);

ALTER TABLE dvobject
	ADD CONSTRAINT unq_dvobject_0 UNIQUE (authority, protocol, identifier);

ALTER TABLE harvestingclient
	ADD CONSTRAINT fk_harvestingclient_dataverse_id FOREIGN KEY (dataverse_id) REFERENCES dvobject(id);

ALTER TABLE oauth2tokendata
	ADD CONSTRAINT fk_oauth2tokendata_user_id FOREIGN KEY (user_id) REFERENCES authenticateduser(id);

ALTER TABLE pendingworkflowinvocation
	ADD CONSTRAINT fk_pendingworkflowinvocation_dataset_id FOREIGN KEY (dataset_id) REFERENCES dvobject(id);

ALTER TABLE template
	ADD CONSTRAINT fk_template_termsofuseandaccess_id FOREIGN KEY (termsofuseandaccess_id) REFERENCES termsofuseandaccess(id);

ALTER TABLE workflowcomment
	ADD CONSTRAINT fk_workflowcomment_authenticateduser_id FOREIGN KEY (authenticateduser_id) REFERENCES authenticateduser(id);

ALTER TABLE workflowcomment
	ADD CONSTRAINT fk_workflowcomment_datasetversion_id FOREIGN KEY (datasetversion_id) REFERENCES datasetversion(id);

/**
  升级时如下索引出错
*/
CREATE UNIQUE INDEX index_authenticateduser_lower_email ON authenticateduser USING btree (lower((email)::text));

CREATE INDEX index_builtinuser_username ON builtinuser USING btree (username);