-- Build Script
--     RDBMS           : IBM DB2 7.x UDB
--     Generated With  :  
--     Generated On    : 2011-03-10 12:58:32
--     Generation Options
--         Generate Comments             : true
--         Generate Drop Statements      : true
--

--  ----------------------------------------------------------------------------------------------------------------
--  Generate From
--    Model       : /PartsProject/PartsSourceA.xmi
--    Model Type  : Physical
--    Metamodel   : relational (http://www.metamatrix.com/metamodels/Relational)
--    Model UUID  : mmuuid:8fb3de1d-e4d4-41f6-807f-c607e6527d30
--  ----------------------------------------------------------------------------------------------------------------

DROP TABLE PARTS%


-- (generated from PARTS)

CREATE TABLE PARTS
(
  PART_ID       VARCHAR(50) NOT NULL,
  PART_NAME     VARCHAR(255),
  PART_COLOR    VARCHAR(30),
  PART_WEIGHT   VARCHAR(255)
)%


