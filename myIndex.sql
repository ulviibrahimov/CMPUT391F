DROP TABLE viewed;
CREATE TABLE viewed (
   photo_id    int,
   name  varchar(24),
   PRIMARY KEY(photo_id,name),
   FOREIGN KEY(photo_id) REFERENCES images,
   FOREIGN KEY(name) REFERENCES users
);


DROP INDEX myindex1;
DROP INDEX myindex2;
DROP INDEX myindex3;

CREATE INDEX myindex1 ON images(subject) INDEXTYPE IS CTXSYS.CONTEXT;
CREATE INDEX myindex2 ON images(place) INDEXTYPE IS CTXSYS.CONTEXT;
CREATE INDEX myindex3 ON images(description) INDEXTYPE IS CTXSYS.CONTEXT;
