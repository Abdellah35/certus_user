-- create db
CREATE DATABASE `certus-core-db` /*!40100 DEFAULT CHARACTER SET ascii */;

-- User table create sql
DROP TABLE IF EXISTS user_tbl;
CREATE TABLE `certus-core-db`.user_tbl (
	id BIGINT auto_increment NOT NULL,
	email_id varchar(100) NOT NULL,
	name varchar(100) NOT NULL,
	password varchar(100) NULL,
	dob varchar(100) NULL,
	gender varchar(100) NULL,
	nationality varchar(100) NULL,
	photo varchar(100) NULL,
	created_date TIMESTAMP NOT NULL,
	created_by varchar(100) NOT NULL,
	modified_date TIMESTAMP NULL,
	modified_by varchar(100) NULL,
	CONSTRAINT user_tbl_pk PRIMARY KEY (id),
	CONSTRAINT user_tbl_un UNIQUE KEY (email_id)
)
ENGINE=InnoDB
DEFAULT CHARSET=ascii
COLLATE=ascii_general_ci;
CREATE INDEX user_tbl_email_id_IDX USING BTREE ON `certus-core-db`.user_tbl (email_id);


-- user_company_mapping
DROP TABLE IF EXISTS user_company_mapping;
CREATE TABLE `certus-core-db`.user_company_mapping (
	user_id BIGINT NOT NULL,
	company_id BIGINT NOT NULL,
	created_date TIMESTAMP NOT NULL,
	created_by varchar(100) NOT NULL,
	modified_date TIMESTAMP NULL,
	modified_by varchar(100) NULL,
	CONSTRAINT user_company_mapping_FK FOREIGN KEY (user_id) REFERENCES `certus-core-db`.user_tbl(id) ON DELETE CASCADE
)
ENGINE=InnoDB
DEFAULT CHARSET=ascii
COLLATE=ascii_general_ci;
CREATE INDEX user_company_mapping_company_id_IDX USING BTREE ON `certus-core-db`.user_company_mapping (company_id);
CREATE INDEX user_company_mapping_user_id_company_id_IDX USING BTREE ON `certus-core-db`.user_company_mapping (user_id,company_id);
CREATE INDEX user_company_mapping_user_id_IDX USING BTREE ON `certus-core-db`.user_company_mapping (user_id);


-- category_mtb
DROP TABLE IF EXISTS category_mtb;
CREATE TABLE `certus-core-db`.category_mtb (
	id BIGINT auto_increment NOT NULL,
	category_name varchar(100) NOT NULL,
	category_description varchar(100) NULL,
	CONSTRAINT category_mtb_pk PRIMARY KEY (id)
)
ENGINE=InnoDB
DEFAULT CHARSET=ascii
COLLATE=ascii_general_ci;
CREATE INDEX category_mtb_category_name_IDX USING BTREE ON `certus-core-db`.category_mtb (category_name);


-- company_profile
DROP TABLE IF EXISTS company_profile;
CREATE TABLE `certus-core-db`.`company_profile` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `logo_url` varchar(100) DEFAULT NULL,
  `company_name` varchar(100) NOT NULL,
  `website` varchar(100) DEFAULT NULL,
  `country` varchar(100) DEFAULT NULL,
  `state` varchar(100) DEFAULT NULL,
  `city` varchar(100) DEFAULT NULL,
  `created_date` timestamp NOT NULL,
  `created_by` varchar(100) NOT NULL,
  `modified_date` timestamp NULL DEFAULT NULL,
  `modified_by` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `company_profile_company_name_IDX` (`company_name`) USING BTREE,
  KEY `company_profile_website_IDX` (`website`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=ascii;


-- country_mtb
DROP TABLE IF EXISTS country_mtb;
CREATE TABLE `certus-core-db`.country_mtb (
	id BIGINT auto_increment NOT NULL,
	country_name varchar(100) NOT NULL,
	country_code varchar(50) NOT NULL,
	country_logo varchar(100) NULL,
	CONSTRAINT country_mtb_pk PRIMARY KEY (id)
)
ENGINE=InnoDB
DEFAULT CHARSET=ascii
COLLATE=ascii_general_ci;
CREATE INDEX country_mtb_country_name_IDX USING BTREE ON `certus-core-db`.country_mtb (country_name);
CREATE INDEX country_mtb_country_code_IDX USING BTREE ON `certus-core-db`.country_mtb (country_code);


-- user_tbl
ALTER TABLE `certus-core-db`.user_tbl ADD category_id BIGINT NOT NULL;
ALTER TABLE `certus-core-db`.user_tbl CHANGE category_id category_id BIGINT NOT NULL AFTER password;
ALTER TABLE `certus-core-db`.user_tbl ADD CONSTRAINT user_tbl_FK FOREIGN KEY (category_id) REFERENCES `certus-core-db`.category_mtb(id);
ALTER TABLE `certus-core-db`.user_tbl MODIFY COLUMN nationality BIGINT NULL;
ALTER TABLE `certus-core-db`.user_tbl ADD CONSTRAINT user_tbl_FK_1 FOREIGN KEY (nationality) REFERENCES `certus-core-db`.country_mtb(id);


-- state_mtb
DROP TABLE IF EXISTS state_mtb;
CREATE TABLE `certus-core-db`.state_mtb (
	id BIGINT auto_increment NOT NULL,
	state_name varchar(100) NOT NULL,
	country_id BIGINT NOT NULL,
	CONSTRAINT state_mtb_pk PRIMARY KEY (id),
	CONSTRAINT state_mtb_FK FOREIGN KEY (country_id) REFERENCES `certus-core-db`.country_mtb(id) ON DELETE CASCADE ON UPDATE CASCADE
)
ENGINE=InnoDB
DEFAULT CHARSET=ascii
COLLATE=ascii_general_ci;
CREATE INDEX state_mtb_state_name_IDX USING BTREE ON `certus-core-db`.state_mtb (state_name);



--digital_ipv
DROP TABLE IF EXISTS digital_ipv;
CREATE TABLE `certus-core-db`.digital_ipv (
	id BIGINT auto_increment NOT NULL,
	user_id BIGINT NOT NULL,
	user_ipv_image varchar(100) NOT NULL,
	ipv_code BIGINT NOT NULL,
	verification_status varchar(100) NOT NULL,
	user_request_raised_time TIMESTAMP NOT NULL,
	user_request_modified_time TIMESTAMP NULL,
	audit_verified_time TIMESTAMP NULL,
	CONSTRAINT digital_ipv_pk PRIMARY KEY (id),
	CONSTRAINT digital_ipv_un UNIQUE KEY (user_id),
	CONSTRAINT digital_ipv_FK FOREIGN KEY (user_id) REFERENCES `certus-core-db`.user_tbl(id) ON DELETE CASCADE ON UPDATE CASCADE
)
ENGINE=InnoDB
DEFAULT CHARSET=ascii
COLLATE=ascii_general_ci;
CREATE INDEX digital_ipv_verification_status_IDX USING BTREE ON `certus-core-db`.digital_ipv (verification_status);


--location_tbl
DROP TABLE IF EXISTS location_tbl;
CREATE TABLE `certus-core-db`.location_tbl (
	id BIGINT auto_increment NOT NULL,
	country_id BIGINT NULL,
	state_id BIGINT NULL,
	city varchar(100) NULL,
	pincode varchar(100) NULL,
	address TEXT NULL,
	user_id BIGINT NOT NULL,
	created_date TIMESTAMP NOT NULL,
	created_by varchar(100) NOT NULL,
	modified_date TIMESTAMP NULL,
	modified_by varchar(100) NULL,
	CONSTRAINT location_tbl_pk PRIMARY KEY (id),
	CONSTRAINT location_tbl_FK FOREIGN KEY (user_id) REFERENCES `certus-core-db`.user_tbl(id) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT location_tbl_FK_1 FOREIGN KEY (country_id) REFERENCES `certus-core-db`.country_mtb(id) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT location_tbl_FK_2 FOREIGN KEY (state_id) REFERENCES `certus-core-db`.state_mtb(id) ON DELETE CASCADE ON UPDATE CASCADE
)
ENGINE=InnoDB
DEFAULT CHARSET=ascii
COLLATE=ascii_general_ci;


-- Digital IPV
ALTER TABLE certus_core_db.digital_ipv MODIFY COLUMN user_ipv_image varchar(100) CHARACTER SET ascii COLLATE ascii_general_ci NULL;
ALTER TABLE certus_core_db.digital_ipv ADD file_type varchar(100) NULL;
ALTER TABLE certus_core_db.digital_ipv ADD file_size BIGINT NULL;
ALTER TABLE certus_core_db.digital_ipv ADD file_name varchar(100) NULL;
ALTER TABLE certus_core_db.digital_ipv MODIFY COLUMN user_ipv_image varchar(2000) NULL;
ALTER TABLE certus_core_db.digital_ipv MODIFY COLUMN user_ipv_image varchar(2000) CHARACTER SET ascii COLLATE ascii_general_ci NULL;






-- user_tbl
ALTER TABLE certus_core_db.user_tbl DROP FOREIGN KEY user_tbl_FK_1;
ALTER TABLE certus_core_db.user_tbl MODIFY COLUMN nationality VARCHAR(100) NULL;
ALTER TABLE certus_core_db.user_tbl MODIFY COLUMN phone VARCHAR(200) NULL;
ALTER TABLE certus_core_db.user_tbl MODIFY COLUMN dob TIMESTAMP NULL;
ALTER TABLE certus_core_db.user_tbl MODIFY COLUMN dob DATETIME NULL;
ALTER TABLE certus_core_db.user_tbl MODIFY COLUMN photo varchar(2000) CHARACTER SET ascii COLLATE ascii_general_ci NULL;
ALTER TABLE certus_core_db.user_tbl DROP FOREIGN KEY user_tbl_FK;
ALTER TABLE certus_core_db.user_tbl CHANGE category_id category VARCHAR(200) NULL;
ALTER TABLE certus_core_db.user_tbl MODIFY COLUMN category VARCHAR(200) NULL;
ALTER TABLE certus_core_db.user_tbl DROP FOREIGN KEY FKqsacs7rfqq17su1jxg1ulomb8;
ALTER TABLE certus_core_db.user_tbl DROP COLUMN category_id;




-- location_tbl
ALTER TABLE certus_core_db.location_tbl MODIFY COLUMN created_by varchar(100) CHARACTER SET ascii COLLATE ascii_general_ci NULL;
ALTER TABLE certus_core_db.location_tbl MODIFY COLUMN created_date timestamp NULL;

-- country_mtb
ALTER TABLE certus_core_db.country_mtb ADD country_mobile_code varchar(100) NULL;
ALTER TABLE certus_core_db.country_mtb CHANGE country_mobile_code country_phone_code varchar(100) CHARACTER SET ascii COLLATE ascii_general_ci NULL;
ALTER TABLE certus_core_db.country_mtb MODIFY COLUMN id bigint(255) auto_increment NOT NULL;
ALTER TABLE certus_core_db.country_mtb DROP INDEX country_mtb_country_code_IDX;
ALTER TABLE certus_core_db.country_mtb DROP INDEX country_mtb_country_name_IDX;
ALTER TABLE certus_core_db.country_mtb MODIFY COLUMN country_name TEXT CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL;
ALTER TABLE certus_core_db.country_mtb MODIFY COLUMN country_code TEXT CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL;
ALTER TABLE certus_core_db.country_mtb MODIFY COLUMN country_logo TEXT CHARACTER SET ascii COLLATE ascii_general_ci NULL;
ALTER TABLE certus_core_db.country_mtb MODIFY COLUMN country_phone_code TEXT CHARACTER SET ascii COLLATE ascii_general_ci NULL;
ALTER TABLE certus_core_db.country_mtb
DEFAULT CHARSET=utf8;

ALTER TABLE certus_core_db.country_mtb MODIFY COLUMN country_name VARCHAR(500) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL;
ALTER TABLE certus_core_db.country_mtb MODIFY COLUMN country_code VARCHAR(50) CHARACTER SET ascii COLLATE ascii_general_ci NOT NULL;
ALTER TABLE certus_core_db.country_mtb MODIFY COLUMN country_phone_code VARCHAR(100) CHARACTER SET ascii COLLATE ascii_general_ci NULL;





-- KYC-DB

CREATE DATABASE `kyc_db` /*!40100 DEFAULT CHARACTER SET utf8 */;


CREATE TABLE `kyc_db`.documents_meta (
	id BIGINT auto_increment NOT NULL,
	document_logo varchar(2000) NULL,
	document_name varchar(200) NOT NULL,
	document_desc varchar(500) NULL,
	document_type varchar(100) NULL,
	document_country_id BIGINT NOT NULL,
	CONSTRAINT documents_meta_pk PRIMARY KEY (id),
	CONSTRAINT documents_meta_fk FOREIGN KEY (document_country_id) REFERENCES certus_core_db.country_mtb(id) ON DELETE CASCADE ON UPDATE CASCADE
)
ENGINE=InnoDB
DEFAULT CHARSET=ascii
COLLATE=ascii_general_ci;


-- Kyc
ALTER TABLE kyc_db.documents_meta ADD CONSTRAINT documents_meta_un UNIQUE KEY (document_name);
ALTER TABLE kyc_db.documents_meta CHANGE document_country_id country_id bigint(20) NOT NULL;

-- Countries
CREATE TABLE certus_core_db.countries_tbl (
	id BIGINT auto_increment NOT NULL,
	country_name varchar(100) NOT NULL,
	country_code varchar(100) NOT NULL,
	country_logo varchar(2000) NULL,
	country_phone_code varchar(100) CHARACTER SET ascii COLLATE ascii_general_ci NULL,
	CONSTRAINT countries_tbl_pk PRIMARY KEY (id)
)
ENGINE=InnoDB
DEFAULT CHARSET=latin1
COLLATE=latin1_swedish_ci;

ALTER TABLE certus_core_db.countries_tbl ADD CONSTRAINT countries_tbl_un UNIQUE KEY (country_code);


-- Company tbl
ALTER TABLE certus_core_db.company_profile MODIFY COLUMN country BIGINT NULL;
ALTER TABLE certus_core_db.company_profile MODIFY COLUMN state BIGINT NULL;
ALTER TABLE certus_core_db.company_profile ADD CONSTRAINT company_profile_FK FOREIGN KEY (country) REFERENCES certus_core_db.country_mtb(id) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE certus_core_db.company_profile ADD CONSTRAINT company_profile_FK_1 FOREIGN KEY (state) REFERENCES certus_core_db.state_mtb(id) ON DELETE CASCADE;
ALTER TABLE certus_core_db.company_profile MODIFY COLUMN logo_url varchar(2000) CHARACTER SET ascii COLLATE ascii_general_ci NULL;


-- user_company_mapping
ALTER TABLE certus_core_db.user_company_mapping ADD CONSTRAINT user_company_mapping_FK_1 FOREIGN KEY (company_id) REFERENCES certus_core_db.company_profile(id) ON DELETE CASCADE ON UPDATE CASCADE;

-- notification_tbl
CREATE TABLE certus_core_db.notification_tbl (
	id BIGINT auto_increment NOT NULL,
	module varchar(200) NOT NULL,
	message varchar(1000) NOT NULL,
	`date` TIMESTAMP NOT NULL,
	requestee_user_id BIGINT NULL,
	requestor_user_id BIGINT NULL,
	company_id BIGINT NULL,
	CONSTRAINT notification_tbl_pk PRIMARY KEY (id),
	CONSTRAINT notification_tbl_FK FOREIGN KEY (company_id) REFERENCES certus_core_db.company_profile(id) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT notification_tbl_FK_1 FOREIGN KEY (requestee_user_id) REFERENCES certus_core_db.user_tbl(id) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT notification_tbl_FK_2 FOREIGN KEY (requestor_user_id) REFERENCES certus_core_db.user_tbl(id) ON DELETE CASCADE ON UPDATE CASCADE
)
ENGINE=InnoDB
DEFAULT CHARSET=latin1
COLLATE=latin1_swedish_ci;

ALTER TABLE certus_core_db.notification_tbl CHANGE `date` created_at timestamp NOT NULL;
ALTER TABLE certus_core_db.notification_tbl ADD message_read TINYINT DEFAULT 0 NULL;


-- Comments message table
CREATE TABLE certus_core_db.comment_msg_tbl (
	id BIGINT auto_increment NOT NULL,
	requestee_userid BIGINT NULL,
	requestor_userid BIGINT NULL,
	kyc_id BIGINT NOT NULL,
	message varchar(3000) NOT NULL,
	last_updated_timestamp TIMESTAMP NOT NULL,
	mark_as_read TINYINT DEFAULT 0 NOT NULL,
	created_by varchar(1000) NOT NULL,
	CONSTRAINT comment_msg_tbl_pk PRIMARY KEY (id),
	CONSTRAINT comment_msg_tbl_FK FOREIGN KEY (kyc_id) REFERENCES kyc_db.kyc_documents(id) ON DELETE CASCADE ON UPDATE CASCADE
)
ENGINE=InnoDB
DEFAULT CHARSET=latin1
COLLATE=latin1_swedish_ci;
CREATE INDEX comment_msg_tbl_last_updated_timestamp_IDX USING BTREE ON certus_core_db.comment_msg_tbl (last_updated_timestamp);
CREATE INDEX comment_msg_tbl_kyc_id_IDX USING BTREE ON certus_core_db.comment_msg_tbl (kyc_id);



-- kyc_documents
use kyc_db;

CREATE TABLE kyc_db.kyc_documents (
	id BIGINT auto_increment NOT NULL,
	company_id BIGINT NOT NULL,
	requestor_userid BIGINT NOT NULL,
	requestee_userid BIGINT NOT NULL,
	document_id BIGINT NOT NULL,
	created_date TIMESTAMP NOT NULL,
	created_by varchar(200) NOT NULL,
	modified_date TIMESTAMP NULL,
	modified_by varchar(200) NULL,
	CONSTRAINT kyc_documents_pk PRIMARY KEY (id)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_general_ci;

ALTER TABLE kyc_db.kyc_documents ADD CONSTRAINT kyc_documents_FK FOREIGN KEY (requestor_userid) REFERENCES certus_core_db.user_tbl(id) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE kyc_db.kyc_documents ADD CONSTRAINT kyc_documents_FK_1 FOREIGN KEY (requestee_userid) REFERENCES certus_core_db.user_tbl(id) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE kyc_db.kyc_documents ADD CONSTRAINT kyc_documents_FK_2 FOREIGN KEY (company_id) REFERENCES certus_core_db.company_profile(id) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE kyc_db.kyc_documents ADD CONSTRAINT kyc_documents_FK_3 FOREIGN KEY (document_id) REFERENCES kyc_db.documents_meta(id) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE kyc_db.kyc_documents ADD process_status varchar(100) NOT NULL;
ALTER TABLE kyc_db.kyc_documents ADD CONSTRAINT kyc_documents_un UNIQUE KEY (company_id,requestee_userid);




