// testForeignTable
CREATE FOREIGN TABLE T1 (
   e1 integer primary key,
   e2 varchar(10) unique,
   e3 date not null unique,
   e4 decimal(12,3) options (searchable 'unsearchable'),
   e5 integer auto_increment INDEX OPTIONS (UUID 'uuid', NAMEINSOURCE 'nis', SELECTABLE 'NO'),
   e6 varchar index default 'hello'
) OPTIONS (
   CARDINALITY 12,
   UUID 'uuid2',
   UPDATABLE 'true',
   FOO 'BAR',
   ANNOTATION 'Test Table'
);

// testDuplicatePrimarykey
//CREATE FOREIGN TABLE T2 (
//   e1 integer primary key,
//   e2 varchar primary key
//);

// testAutoIncrementPrimarykey
CREATE FOREIGN TABLE T3 (
   e1 integer auto_increment primary key,
   e2 varchar
);

// testUDT
CREATE FOREIGN TABLE T4 (
   e1 integer,
   e2 varchar OPTIONS (UDT 'NMTOKENS(12,13,14)')
);
	
// testFBI
CREATE FOREIGN TABLE T5 (
   e1 integer,
   e2 varchar,
   CONSTRAINT fbi INDEX (UPPER(e2))
);

// testMultiKeyPK
CREATE FOREIGN TABLE T6 (
   e1 integer,
   e2 varchar,
   e3 date,
   PRIMARY KEY (e1, e2)
);

// testOptionsKey
CREATE FOREIGN TABLE T7 (
   e1 integer,
   e2 varchar,
   e3 date,
   UNIQUE (e1) OPTIONS (CUSTOM_PROP 'VALUE')
);

// testConstraints
CREATE FOREIGN TABLE T8 (
   e1 integer,
   e2 varchar,
   e3 date,
   PRIMARY KEY (e1, e2),
   INDEX(e2, e3),
   ACCESSPATTERN(e1),
   UNIQUE(e1),
   ACCESSPATTERN(e2, e3)
);

// testConstraints2
CREATE FOREIGN TABLE T9 (
   e1 integer,
   e2 varchar,
   e3 date,
   ACCESSPATTERN(e1),
   UNIQUE(e1),
   ACCESSPATTERN(e2, e3)
);

// testFK
CREATE FOREIGN TABLE T10 (
   g1e1 integer,
   g1e2 varchar,
   PRIMARY KEY(g1e1, g1e2)
);

CREATE FOREIGN TABLE T11 (
   g2e1 integer,
   g2e2 varchar,
   FOREIGN KEY (g2e1, g2e2) REFERENCES T10 (g1e1, g1e2)
);

// testOptionalFK
CREATE FOREIGN TABLE T12 (
   g1e1 integer,
   g1e2 varchar,
   PRIMARY KEY(g1e1, g1e2)
);

CREATE FOREIGN TABLE T13 (
   g2e1 integer,
   g2e2 varchar,
   PRIMARY KEY(g2e1, g2e2),
   FOREIGN KEY (g2e1, g2e2) REFERENCES T12
);

// testOptionalFKFail
CREATE FOREIGN TABLE T14 (
   g1e1 integer,
   g1e2 varchar
);

CREATE FOREIGN TABLE T15 (
   g2e1 integer,
   g2e2 varchar,
   PRIMARY KEY(g2e1, g2e2),
   FOREIGN KEY (g2e1, g2e2) REFERENCES T14
);

// testFKAccrossSchemas
CREATE FOREIGN TABLE T16 (
   g1e1 integer,
   g1e2 varchar,
   PRIMARY KEY(g1e1, g1e2)
);

CREATE FOREIGN TABLE T17 (
   g2e1 integer,
   g2e2 varchar,
   PRIMARY KEY(g2e1, g2e2),
   FOREIGN KEY (g2e1, g2e2) REFERENCES T16
);

// testViewWithoutColumns
CREATE VIEW V1 AS SELECT * FROM PM1.G1;

// testMultipleCommands
CREATE VIEW V2 AS SELECT * FROM PM1.G1;

CREATE PROCEDURE P1 (
   P1 integer
) RETURNS (
   e1 integer,
   e2 varchar
)  AS SELECT * FROM PM1.G1;

// testMultipleCommands2
CREATE VIRTUAL PROCEDURE getTweets(
   query varchar
) RETURNS (
   created_on varchar(25),
   from_user varchar(25), to_user varchar(25),
   profile_image_url varchar(25),
   source varchar(25),
   text varchar(140)
) AS 
select tweet.* from 
(call twitter.invokeHTTP(action => 'GET', endpoint =>querystring('',query as \"q\"))) w, 
XMLTABLE('results' passing JSONTOXML('myxml', w.result) columns 
created_on string PATH 'created_at', 
from_user string PATH 'from_user',
to_user string PATH 'to_user', 
profile_image_url string PATH 'profile_image_url', 
source string PATH 'source', 
text string PATH 'text') tweet;

CREATE VIEW Tweet AS select * FROM twitterview.getTweets;

// testView
CREATE View V3(
   e1 integer,
   e2 varchar
) OPTIONS (
   CARDINALITY 12
) AS select e1, e2 from foo.bar;

// testPushdownFunctionNoArgs
CREATE FOREIGN FUNCTION F1() RETURNS integer OPTIONS (UUID 'hello world');

// testDuplicateFunctions2
CREATE FUNCTION F2() RETURNS string;

CREATE FUNCTION F3(param string) RETURNS string

// testUDF
CREATE VIRTUAL FUNCTION F4(
   flag boolean,
   msg varchar
) RETURNS varchar 
OPTIONS (
   CATEGORY 'misc', 
   DETERMINISM 'DETERMINISTIC', 
   "NULL-ON-NULL" 'true', 
   JAVA_CLASS 'foo', 
   JAVA_METHOD 'bar', RANDOM 'any', UUID 'x'
);

// testUDAggregate
CREATE VIRTUAL FUNCTION F5(
   flag boolean,
   msg varchar
) RETURNS varchar 
OPTIONS(
   CATEGORY 'misc',
   AGGREGATE 'true',
   "allows-distinct" 'true',
   UUID 'y'
);

// testVarArgs
CREATE FUNCTION F6(
   flag boolean
) RETURNS varchar 
options (
   varargs 'true',
   UUID 'z'
);

// testMixedCaseTypes
CREATE FUNCTION F7(
   flag Boolean
) RETURNS varchaR 
options (
   UUID 'z'
);

// testVirtualProcedure
CREATE VIRTUAL PROCEDURE myProc(OUT p1 boolean, p2 varchar, INOUT p3 decimal) RETURNS (r1 varchar, r2 decimal) 
OPTIONS(RANDOM 'any', UUID 'uuid', NAMEINSOURCE 'nis', ANNOTATION 'desc', UPDATECOUNT '2') 
AS /*+ cache */ BEGIN select * from foo; END

// testInsteadOfTrigger
CREATE VIEW G1( e1 integer, e2 varchar) AS select * from foo;
CREATE TRIGGER ON G1 INSTEAD OF INSERT AS 
FOR EACH ROW 
BEGIN ATOMIC 
insert into g1 (e1, e2) values (1, 'trig');
END;
CREATE View G2( e1 integer, e2 varchar) AS select * from foo;

// testSourceProcedure
CREATE FOREIGN PROCEDURE myProc(OUT p1 boolean, p2 varchar, INOUT p3 decimal) RETURNS (r1 varchar, r2 decimal)
OPTIONS(RANDOM 'any', UUID 'uuid', NAMEINSOURCE 'nis', ANNOTATION 'desc', UPDATECOUNT '2');

// testNamespace
//set namespace 'http://teiid.org' AS teiid;

// testAlterTableAddOptions
CREATE FOREIGN TABLE G1( e1 integer, e2 varchar, e3 date); 
ALTER FOREIGN TABLE G1 OPTIONS(ADD CARDINALITY 12);
ALTER FOREIGN TABLE G1 OPTIONS(ADD FOO 'BAR');

// testAlterTableModifyOptions
CREATE FOREIGN TABLE G1( e1 integer, e2 varchar, e3 date) OPTIONS(CARDINALITY 12, FOO 'BAR');
ALTER FOREIGN TABLE G1 OPTIONS(SET CARDINALITY 24);
ALTER FOREIGN TABLE G1 OPTIONS(SET FOO 'BARBAR');

// testAlterTableDropOptions
CREATE FOREIGN TABLE G1( e1 integer, e2 varchar, e3 date) OPTIONS(CARDINALITY 12, FOO 'BAR');
ALTER FOREIGN TABLE G1 OPTIONS(DROP CARDINALITY);
ALTER FOREIGN TABLE G1 OPTIONS(DROP FOO);

// testAlterTableAddColumnOptions
CREATE FOREIGN TABLE G1( e1 integer, e2 varchar, e3 date);
ALTER FOREIGN TABLE G1 OPTIONS(ADD CARDINALITY 12);
ALTER FOREIGN TABLE G1 ALTER COLUMN e1 OPTIONS(ADD NULL_VALUE_COUNT 12);
ALTER FOREIGN TABLE G1 ALTER COLUMN e1 OPTIONS(ADD FOO 'BAR');

// testAlterTableRemoveColumnOptions
CREATE FOREIGN TABLE G1( e1 integer OPTIONS (NULL_VALUE_COUNT 12, FOO 'BAR'), e2 varchar, e3 date);
ALTER FOREIGN TABLE G1 ALTER COLUMN e1 OPTIONS(DROP NULL_VALUE_COUNT);
ALTER FOREIGN TABLE G1 ALTER COLUMN e1 OPTIONS(DROP FOO);
ALTER FOREIGN TABLE G1 ALTER COLUMN e1 OPTIONS( ADD x 'y');

// testAlterProcedureOptions
CREATE FOREIGN PROCEDURE myProc(OUT p1 boolean, p2 varchar, INOUT p3 decimal) 
RETURNS (r1 varchar, r2 decimal)
OPTIONS(RANDOM 'any', UUID 'uuid', NAMEINSOURCE 'nis', ANNOTATION 'desc', UPDATECOUNT '2');
ALTER FOREIGN PROCEDURE myProc OPTIONS(SET NAMEINSOURCE 'x')
ALTER FOREIGN PROCEDURE myProc ALTER PARAMETER p2 OPTIONS (ADD x 'y');
ALTER FOREIGN PROCEDURE myProc OPTIONS(DROP UPDATECOUNT);
