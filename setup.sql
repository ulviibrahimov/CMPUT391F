/*
 *  File name:  setup.sql
 *  Function:   to create the intial database schema for the CMPUT 391 project,
 *              Fall, 2014
 *  Author:     Prof. Li-Yan Yuan
 */
DROP TABLE viewed;
DROP TABLE images;
DROP TABLE group_lists;
DROP TABLE groups;
DROP TABLE persons;
DROP TABLE users;

CREATE TABLE users (
   user_name varchar(24),
   password  varchar(24),
   date_registered date,
   primary key(user_name)
);

CREATE TABLE persons (
   user_name  varchar(24),
   first_name varchar(24),
   last_name  varchar(24),
   address    varchar(128),
   email      varchar(128),
   phone      char(10),
   PRIMARY KEY(user_name),
   UNIQUE (email),
   FOREIGN KEY (user_name) REFERENCES users
);

CREATE TABLE groups (
   group_id   int,
   user_name  varchar(24),
   group_name varchar(24),
   date_created date,
   PRIMARY KEY (group_id),
   UNIQUE (user_name, group_name),
   FOREIGN KEY(user_name) REFERENCES users
);

INSERT INTO groups values(1,null,'public', sysdate);
INSERT INTO groups values(2,null,'private',sysdate);

CREATE TABLE group_lists (
   group_id    int,
   friend_id   varchar(24),
   date_added  date,
   notice      varchar(1024),
   PRIMARY KEY(group_id, friend_id),
   FOREIGN KEY(group_id) REFERENCES groups,
   FOREIGN KEY(friend_id) REFERENCES users
);

CREATE TABLE images (
   photo_id    int,
   owner_name  varchar(24),
   permitted   int,
   subject     varchar(128),
   place       varchar(128),
   timing      date,
   description varchar(2048),
   thumbnail   blob,
   photo       blob,
   PRIMARY KEY(photo_id),
   FOREIGN KEY(owner_name) REFERENCES users,
   FOREIGN KEY(permitted) REFERENCES groups
);

-- Our custom SQL setup commands

INSERT INTO users values('admin', 'secret', SYSDATE);
INSERT INTO persons values('admin', null, null, null, null, null);


CREATE TABLE viewed (
   photo_id    int,
   name  varchar(24),
   PRIMARY KEY(photo_id,name),
   FOREIGN KEY(photo_id) REFERENCES images,
   FOREIGN KEY(name) REFERENCES users
);

DROP SEQUENCE pic_id_seq;
CREATE SEQUENCE pic_id_seq;

DROP SEQUENCE group_id_seq;
CREATE SEQUENCE group_id_seq START WITH 3;

DROP INDEX myindex1;
DROP INDEX myindex2;
DROP INDEX myindex3;

CREATE INDEX myindex1 ON images(subject) INDEXTYPE IS CTXSYS.CONTEXT;
CREATE INDEX myindex2 ON images(place) INDEXTYPE IS CTXSYS.CONTEXT;
CREATE INDEX myindex3 ON images(description) INDEXTYPE IS CTXSYS.CONTEXT;

REM
Rem Copyright (c) 1999, 2001, Oracle Corporation. All rights reserved.
Rem
Rem NAME
Rem drjobdml.sql - DR dbms_JOB DML script
Rem
Rem NOTES
Rem This is a script which demonstrates how to submit a DBMS_JOB
Rem which will keep a context index up-to-date.
Rem
Rem Before running this script, please ensure that your database
Rem is set up to run dbms_jobs. job_queue_processes must be set
Rem (and non-zero) in init.ora. See Administrator's Guide for more
Rem information.
Rem
Rem Also, due to PL/SQL security, ctxsys must manually grant EXECUTE
Rem on ctx_ddl directly to the user. CTXAPP role is not sufficient.
Rem
Rem USAGE
Rem as index owner, in SQL*Plus:
Rem
Rem @drjobdml <indexname> <interval>
Rem
Rem A job will be submitted to check for and process dml for the
Rem named index every <interval> minutes
Rem
Rem MODIFIED (MM/DD/YY)
Rem gkaminag 11/19/01 - bug 2052473.
Rem gkaminag 05/14/99 - bugs
Rem gkaminag 04/09/99 - Creation
define idxname = "myindex1"
define interval = "1"
set serveroutput on
declare
job number;
begin
dbms_job.submit(job, 'ctx_ddl.sync_index(''&idxname'');',
interval=>'SYSDATE+&interval/1440');
commit;
dbms_output.put_line('job '||job||' has been submitted.');
end;
/


REM
Rem Copyright (c) 1999, 2001, Oracle Corporation. All rights reserved.
Rem
Rem NAME
Rem drjobdml.sql - DR dbms_JOB DML script
Rem
Rem NOTES
Rem This is a script which demonstrates how to submit a DBMS_JOB
Rem which will keep a context index up-to-date.
Rem
Rem Before running this script, please ensure that your database
Rem is set up to run dbms_jobs. job_queue_processes must be set
Rem (and non-zero) in init.ora. See Administrator's Guide for more
Rem information.
Rem
Rem Also, due to PL/SQL security, ctxsys must manually grant EXECUTE
Rem on ctx_ddl directly to the user. CTXAPP role is not sufficient.
Rem
Rem USAGE
Rem as index owner, in SQL*Plus:
Rem
Rem @drjobdml <indexname> <interval>
Rem
Rem A job will be submitted to check for and process dml for the
Rem named index every <interval> minutes
Rem
Rem MODIFIED (MM/DD/YY)
Rem gkaminag 11/19/01 - bug 2052473.
Rem gkaminag 05/14/99 - bugs
Rem gkaminag 04/09/99 - Creation
define idxname = "myindex2"
define interval = "1"
set serveroutput on
declare
job number;
begin
dbms_job.submit(job, 'ctx_ddl.sync_index(''&idxname'');',
interval=>'SYSDATE+&interval/1440');
commit;
dbms_output.put_line('job '||job||' has been submitted.');
end;
/


REM
Rem Copyright (c) 1999, 2001, Oracle Corporation. All rights reserved.
Rem
Rem NAME
Rem drjobdml.sql - DR dbms_JOB DML script
Rem
Rem NOTES
Rem This is a script which demonstrates how to submit a DBMS_JOB
Rem which will keep a context index up-to-date.
Rem
Rem Before running this script, please ensure that your database
Rem is set up to run dbms_jobs. job_queue_processes must be set
Rem (and non-zero) in init.ora. See Administrator's Guide for more
Rem information.
Rem
Rem Also, due to PL/SQL security, ctxsys must manually grant EXECUTE
Rem on ctx_ddl directly to the user. CTXAPP role is not sufficient.
Rem
Rem USAGE
Rem as index owner, in SQL*Plus:
Rem
Rem @drjobdml <indexname> <interval>
Rem
Rem A job will be submitted to check for and process dml for the
Rem named index every <interval> minutes
Rem
Rem MODIFIED (MM/DD/YY)
Rem gkaminag 11/19/01 - bug 2052473.
Rem gkaminag 05/14/99 - bugs
Rem gkaminag 04/09/99 - Creation
define idxname = "myindex3"
define interval = "1"
set serveroutput on
declare
job number;
begin
dbms_job.submit(job, 'ctx_ddl.sync_index(''&idxname'');',
interval=>'SYSDATE+&interval/1440');
commit;
dbms_output.put_line('job '||job||' has been submitted.');
end;
/
