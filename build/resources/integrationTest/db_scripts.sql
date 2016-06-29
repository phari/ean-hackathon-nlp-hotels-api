drop TABLE  IF EXISTS insights_resource_group;
drop TABLE  IF EXISTS insights_resource;
drop TABLE  IF EXISTS insights_group_users;
drop TABLE  IF EXISTS insights_capability;
drop TABLE  IF EXISTS insights_group;

CREATE TABLE insights_resource
(
  resource_id BIGINT ,
  name character varying(255),
  display_name character varying(255),
  enabled boolean,
  updated_date timestamp not null,
  updated_user varchar(255) not null,
  cid_required boolean,
  CONSTRAINT ir_temp_pk PRIMARY KEY (resource_id),
  CONSTRAINT ir_temp_unique UNIQUE (name)
);


CREATE TABLE insights_group
(
  group_id    BIGINT,
  group_name  CHARACTER VARYING(255),

  CONSTRAINT irg_pk PRIMARY KEY (group_id),
  CONSTRAINT irg_grp_nme UNIQUE (group_name)
);

CREATE TABLE insights_capability
(
  capability CHARACTER VARYING(60),
  capability_desc CHARACTER VARYING(255),

  CONSTRAINT icap_unique PRIMARY KEY (capability)
);

CREATE TABLE insights_resource_group
(
  resource_id BIGINT REFERENCES insights_resource (resource_id),
  group_id BIGINT REFERENCES insights_group (group_id),
  capability VARCHAR(60) REFERENCES insights_capability (capability),
  updated_date timestamp not null,
  updated_user varchar(255) not null,

  CONSTRAINT res_grp_uniq UNIQUE (resource_id, group_id, capability)
);

CREATE TABLE insights_group_users
(
  group_id BIGINT REFERENCES insights_group (group_id),
  user_id CHARACTER VARYING(255)
);


INSERT INTO insights_resource (resource_id,name,display_name, enabled, cid_required, updated_date, updated_user ) VALUES (1,'Actual_Marketing_Payments', 'Marketing Fee Payments', true, true, now(), 'initial');
INSERT INTO insights_resource (resource_id,name, display_name, enabled, cid_required, updated_date, updated_user) VALUES (2,'Deals_Files', 'Deals File', true, true, now(), 'initial');
INSERT INTO insights_resource (resource_id,name, display_name, enabled, cid_required, updated_date, updated_user) VALUES (3,'Content_Files', 'Database Files', true, false, now(), 'initial');
INSERT INTO insights_resource (resource_id,name, display_name, enabled, cid_required, updated_date, updated_user) VALUES (4,'Admin_UI', 'Admin', true, false, now(), 'initial');

INSERT INTO insights_group (group_id, group_name) VALUES (1,'internal');
INSERT INTO insights_group (group_id, group_name) VALUES (2,'external');
INSERT INTO insights_group (group_id, group_name) VALUES (3,'Admin_Group');

INSERT INTO insights_capability (capability, capability_desc) VALUES ('READ', 'Capability to read a resource');
INSERT INTO insights_capability (capability, capability_desc) VALUES ('WRITE', 'Capability to update a resource');
INSERT INTO insights_capability (capability, capability_desc) VALUES ('DENY', 'Denies the usage of a resource');

/* Entries to enable user groups to access the resources */
INSERT INTO insights_resource_group (resource_id, group_id, capability, updated_date, updated_user) VALUES (1,1, 'READ', now(), 'initial'); --ActualMarketPayments to Internal user group
INSERT INTO insights_resource_group (resource_id, group_id, capability, updated_date, updated_user) VALUES (2,2, 'READ', now(), 'initial'); --Deals files to External user group
INSERT INTO insights_resource_group (resource_id, group_id, capability, updated_date, updated_user) VALUES (2,1, 'READ', now(), 'initial'); --Deals files to Internal user group
INSERT INTO insights_resource_group (resource_id, group_id, capability, updated_date, updated_user) VALUES (3,1, 'READ', now(), 'initial'); --Content_Files to Internal user group**
INSERT INTO insights_resource_group (resource_id, group_id, capability, updated_date, updated_user) VALUES (4,3, 'READ', now(), 'initial'); --Admin_UI to Admin user group**

/* Specific privileges... Include list scenario */
INSERT INTO insights_group (group_id, group_name) VALUES (4,'ActualMarketPayments_Beta_Partners');
insert into insights_group_users (group_id, user_id) VALUES (4, '366702'); --Make 366702 CID to access ActualMarketPayments
insert into insights_group_users (group_id, user_id) VALUES (4, '318792'); --Make 318792 CID to access ActualMarketPayments
INSERT INTO insights_resource_group (resource_id, group_id, capability, updated_date, updated_user) VALUES (1, 4, 'READ', now(), 'initial'); --ActualMarketPayments to ActualMarketPayments_Beta_Partners user group

/* Specific privileges... Exclude list scenario */
INSERT INTO insights_group (group_id, group_name) VALUES (5,'ActualMarketPayments_Disabled_Users');
insert into insights_group_users (group_id, user_id) VALUES (5, 'testdisableduser'); --Exclude testdiableduser from accessing Actual Marketing files
INSERT INTO insights_resource_group (resource_id, group_id, capability, updated_date, updated_user) VALUES (1, 5, 'DENY', now(), 'initial');

/* Admin UI privileges */
insert into insights_group_users (group_id, user_id) VALUES (3, 'an_admin_user'); --Make an_admin_user a part of admin user group
