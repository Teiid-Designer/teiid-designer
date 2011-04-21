-- THIS SCRIPT WILL CREATE THE DATABASE SCHEMA OF
-- GREEN HOUSE MOTELS 

-- It's for Oracle - http://www.experts-exchange.com/Microsoft/Development/MS-SQL-Server/Q_24742525.html
 
--================================================
 
DROP TABLE Motel                 CASCADE CONSTRAINTS;
DROP TABLE Room                  CASCADE CONSTRAINTS;
DROP TABLE BedType               CASCADE CONSTRAINTS;
DROP TABLE RoomType              CASCADE CONSTRAINTS;
DROP TABLE JobType               CASCADE CONSTRAINTS;
DROP TABLE Employee              CASCADE CONSTRAINTS;
DROP TABLE Customer              CASCADE CONSTRAINTS;
DROP TABLE CustomerPrivateInfo   CASCADE CONSTRAINTS;
DROP TABLE ThirdParty            CASCADE CONSTRAINTS;
DROP TABLE CustomerFamilyMember  CASCADE CONSTRAINTS;
DROP TABLE MotelRoom             CASCADE CONSTRAINTS;
DROP TABLE BookedRoom            CASCADE CONSTRAINTS;
DROP TABLE MotelJobType          CASCADE CONSTRAINTS;
DROP TABLE MotelJobTypeEmployee  CASCADE CONSTRAINTS;
DROP TABLE Address               CASCADE CONSTRAINTS;
DROP TABLE Province              CASCADE CONSTRAINTS;
DROP TABLE Country               CASCADE CONSTRAINTS;
DROP TABLE PhoneCustEmpType      CASCADE CONSTRAINTS;
DROP TABLE PhoneMotelSection     CASCADE CONSTRAINTS;
DROP TABLE PhoneCus              CASCADE CONSTRAINTS;
DROP TABLE PhoneEmp              CASCADE CONSTRAINTS;
DROP TABLE PhoneThdPar           CASCADE CONSTRAINTS;
DROP TABLE PhoneMot              CASCADE CONSTRAINTS;
DROP TABLE EmployeeHistory       CASCADE CONSTRAINTS;
DROP TABLE CustomerHistory       CASCADE CONSTRAINTS;
DROP TABLE ThirdPartyHistory     CASCADE CONSTRAINTS;
DROP TABLE CustomerFamilyHistory CASCADE CONSTRAINTS;
 
--=================================================
 
CREATE TABLE BedType
(
	bedtypename varchar2(50) CONSTRAINT bedtypename_pk PRIMARY KEY
);
 
 
--================================================
 
CREATE TABLE RoomType
(
	roomtypename varchar2(50) CONSTRAINT roomtypename_pk PRIMARY KEY
);
 
 
--================================================
 
CREATE TABLE Room
(
	roomid number(7) CONSTRAINT roomid_pk PRIMARY KEY,
	roomtypename varchar2(50) NOT NULL CONSTRAINT roomtypename_fk REFERENCES RoomType(roomtypename),
	roombedtypename varchar2(50) CONSTRAINT roombedtypename_fk REFERENCES BedType(bedtypename) ON DELETE SET NULL,
	extranotes varchar2(500),
	roompricepernight number(6,2) NOT NULL
);
 
 
--================================================
 
CREATE TABLE JobType
(
	jobtypename varchar2(50) CONSTRAINT jobtypename_pk PRIMARY KEY
);
 
 
--================================================
 
CREATE TABLE Country
(
	countryname varchar2(50) CONSTRAINT countryname_pk PRIMARY KEY
);
 
 
--================================================
 
CREATE TABLE Province
(
	provincename varchar2(50) CONSTRAINT provincename_pk PRIMARY KEY
);
 
 
--================================================
 
CREATE TABLE Address
(
	addressID number(7) CONSTRAINT address_id_pk PRIMARY KEY,
        address1 varchar2(100) NOT NULL,
        address2 varchar2(100),
        city varchar2(50) NOT NULL,
        provincename varchar2(50) CONSTRAINT provincename_fk REFERENCES Province(provincename),
        postalcode varchar2(50) NOT NULL CONSTRAINT postalcode_uq UNIQUE,
        countryname varchar2(50) NOT NULL,
        CONSTRAINT countryname_fk FOREIGN KEY(countryname) REFERENCES Country(countryname)
);
 
 
--================================================
 
CREATE TABLE Motel
(
	motelID number(7) CONSTRAINT motel_id_pk PRIMARY KEY,
        motelbranchname varchar2(50) NOT NULL,
        moteladdressID number(7) NOT NULL CONSTRAINT moteladdressid_uk UNIQUE,
	CONSTRAINT moteladdressid_fk FOREIGN KEY(moteladdressID) REFERENCES Address(addressID)
);
 
 
--================================================
 
 
CREATE TABLE Employee
(
	employeeID number(7) CONSTRAINT employeeid_pk PRIMARY KEY,
	employeefirstname varchar2(50) NOT NULL,
	employeelastname varchar2(50) NOT NULL,
	employeeemail varchar2(50) NOT NULL,
	employeeaddressID number(7) NOT NULL CONSTRAINT employeeaddressid_fk REFERENCES Address(addressID),
	employeesalary number(9,2) NOT NULL,
	employeehiredate date DEFAULT SYSDATE NOT NULL,
	employeeworkstatus varchar2(10),
	datecreated datetime DEFAULT SYSDATE NOT NULL,
	datamodified datetime DEFAULT SYSDATE NOT NULL
);
 
 
--================================================
 
CREATE TABLE ThirdParty
(
	thirdpartyID number(7) CONSTRAINT thirdpartyid_pk PRIMARY KEY,
	thirdpartyfirstname varchar2(50) NOT NULL,
	thirdpartylastname varchar2(50) NOT NULL,
	thirdpartyemail varchar2(50) NOT NULL,
	thirdpartyaddressID number(7) NOT NULL CONSTRAINT thirdpartyaddressID_fk REFERENCES Address(addressID),
	thirdpartygender varchar2(20),
	thirdpartycompany varchar2(50),
	datecreated datetime DEFAULT SYSDATE NOT NULL,
	datamodified datetime DEFAULT SYSDATE NOT NULL
);
 
 
--================================================
 
CREATE TABLE Customer
(
	customerID number(7) CONSTRAINT customerid_pk PRIMARY KEY,
	customerfirstname varchar2(50) NOT NULL,
	customerlastname varchar2(50) NOT NULL,
	customeremail varchar2(50) NOT NULL,
	customeraddressID number(7) NOT NULL CONSTRAINT customeraddressID_fk REFERENCES Address(addressID), 
	customertype varchar2(20) DEFAULT 'Single' NOT NULL,
	numberoffamilymembers number(1) DEFAULT 0 NOT NULL,
	isthirdparty varchar2(20) DEFAULT 'NONE'NOT NULL,
	customerthirdpartyid number(7) CONSTRAINT customerthirdpartyid_fk REFERENCES ThirdParty(thirdpartyID)
);
 
 
--================================================
 
CREATE TABLE CustomerPrivateInfo
(
	customerid number(7) CONSTRAINT customerid_privateinfo_fk REFERENCES Customer(customerid) ON DELETE CASCADE,
	paymentmethod varchar2(20) DEFAULT 'NONE' NOT NULL,
	notesaboutpayment varchar2(500) DEFAULT 'NONE' NOT NULL,
	dateCreated datetime DEFAULT SYSDATE NOT NULL,
	dateModified datetime DEFAULT SYSDATE NOT NULL,
	CONSTRAINT customerid_privateinfo_pk PRIMARY KEY(customerid)
);
 
 
--================================================
 
CREATE TABLE CustomerFamilyMember
(
	customerfamilymemberID number(7) CONSTRAINT customerfamilymemberid_pk PRIMARY KEY,
	customerfamilymemberfirstname varchar2(50) NOT NULL,
	customerfamilymemberlastname varchar2(50) NOT NULL,
	customerfamilymemberage number(3),
	customerfamilymembersibling varchar2(20),
	cusfamilymembcustomerID number(7) NOT NULL CONSTRAINT cusfamilymembcustomerid_fk REFERENCES Customer(customerid) ON DELETE CASCADE	
);
 
 
--================================================
 
CREATE TABLE MotelRoom
(
	motelroomID number(7) CONSTRAINT motelroomid_pk PRIMARY KEY,
	motelID number(7) NOT NULL,
	roomID number(7) NOT NULL,
	numofroomsavailable number(10) NOT NULL,
	CONSTRAINT motelid_fk FOREIGN KEY(motelID) REFERENCES Motel(motelID),
	CONSTRAINT roomid_fk FOREIGN KEY(roomID) REFERENCES Room(roomID),
	CONSTRAINT motelid_roomid_uq UNIQUE(motelID, roomID)
);
 
 
--================================================
 
CREATE TABLE BookedRoom
(
	bookedroomID number(7) CONSTRAINT bookedroomid_pk PRIMARY KEY,
	customerID number(7) NOT NULL CONSTRAINT customerid_fk REFERENCES Customer(customerID) ON DELETE CASCADE,
	motelroomID number(7) NOT NULL CONSTRAINT motelroomid_fk REFERENCES MotelRoom(motelroomID),
	checkin datetime DEFAULT SYSDATE NOT NULL,
	checkout datetime DEFAULT SYSDATE NOT NULL,
	datecreated datetime DEFAULT SYSDATE NOT NULL,
	datamodified datetime DEFAULT SYSDATE NOT NULL
);
 
 
--================================================
 
CREATE TABLE MotelJobType
(
	moteljobtypeID number(7) CONSTRAINT moteljobtypeid_pk PRIMARY KEY,
	motelID number(7) NOT NULL CONSTRAINT motelid_moteljobtype_fk REFERENCES Motel(motelID),
	moteljobtypename varchar2(50) NOT NULL CONSTRAINT moteljobtypename_fk REFERENCES JobType(jobtypename),
	numofpositionsavailable number(15) NOT NULL  
);
 
 
--================================================
 
CREATE TABLE MotelJobTypeEmployee
(
	moteljobtypeemployeeID number(7) CONSTRAINT moteljobtypeemployeeid_pk PRIMARY KEY,
	employeeID number(7) NOT NULL CONSTRAINT employeeid_fk REFERENCES Employee(employeeID) ON DELETE CASCADE,
	moteljobtypeID number(7) CONSTRAINT moteljobtypeid_fk REFERENCES MotelJobType(moteljobtypeID),
	CONSTRAINT employeeid_uq UNIQUE(employeeID)
);
 
 
--================================================
 
 
CREATE TABLE PhoneCustEmpType
(
	phonecustemptypename varchar2(50) CONSTRAINT phonecustemptypename_pk PRIMARY KEY
);
 
 
--================================================
 
CREATE TABLE PhoneMotelSection
(
	phonemotelsectionname varchar2(50) CONSTRAINT phonemotelsectionname_pk PRIMARY KEY
);
 
 
--================================================
 
CREATE TABLE PhoneCus
(
	phonecusID number(7) CONSTRAINT phonecusid_pk PRIMARY KEY,
	phonecustomerID number(7) NOT NULL CONSTRAINT phonecustomerid_fk REFERENCES Customer(customerid) ON DELETE CASCADE,
        phonecustemptypename varchar2(50) CONSTRAINT phonecustemp_phonecus_fk REFERENCES PhoneCustEmpType(phonecustemptypename),
	phonenumber varchar2(100) NOT NULL
);
 
 
--================================================
 
CREATE TABLE PhoneEmp
(
	phoneempID number(7) CONSTRAINT phoneempid_pk PRIMARY KEY,
	phoneemployeeID number(7) NOT NULL CONSTRAINT phoneemployeeid_fk REFERENCES Employee(employeeID) ON DELETE CASCADE,
        phonecustemptypename varchar2(50) CONSTRAINT phonecustemp_phoneemp_fk REFERENCES PhoneCustEmpType(phonecustemptypename),
	phonenumber varchar2(100) NOT NULL
);
 
 
--================================================
 
CREATE TABLE PhoneThdPar
(
	phonethdparID number(7) CONSTRAINT phonethdparid_pk PRIMARY KEY,
	phonethirdpartyID number(7) NOT NULL CONSTRAINT phonethirdpartyid_fk REFERENCES ThirdParty(thirdpartyID) ON DELETE CASCADE,
        phonecustemptypename varchar2(50) CONSTRAINT phonecustemptypename_fk REFERENCES PhoneCustEmpType(phonecustemptypename),
	phonenumber varchar2(100) NOT NULL
);
 
 
--================================================
 
CREATE TABLE PhoneMot
(
	phonemotID number(7) CONSTRAINT phonemotid_pk PRIMARY KEY,
	phonemotelID number(7) NOT NULL CONSTRAINT phonemotelid_fk REFERENCES Motel(motelID) ON DELETE CASCADE,
	phonemotelsectionname varchar2(50) NOT NULL CONSTRAINT phonemotelsectionname_fk
	REFERENCES PhoneMotelSection(phonemotelsectionname) ON DELETE CASCADE,
	phonenumber varchar2(100) NOT NULL 
);
 
 
--================================================
 
CREATE TABLE EmployeeHistory
(
	employeehistoryid number(10) CONSTRAINT employeehistoryid_pk PRIMARY KEY,
	employeeid number(7) NOT NULL,
	firstname varchar2(50) NOT NULL,
	lastname varchar2(50) NOT NULL,
	email varchar2(50) NOT NULL,
	workstatus varchar2(10),
	salary number(9,2) NOT NULL,
	motelname varchar2(50) NOT NULL,
	jobtype varchar2(50) NOT NULL,
	hiredate date NOT NULL,
	leavingdate date DEFAULT SYSDATE NOT NULL,
	address1 varchar2(100) NOT NULL,
	address2 varchar2(100) NOT NULL,
	city varchar2(50) NOT NULL,
	province varchar2(50) NOT NULL,
	postalcode varchar2(50) NOT NULL,
	country varchar2(50) NOT NULL,
	phonetype varchar2(50),
	phonenumber varchar2(100) NOT NULL,
	CONSTRAINT leavingdate_ck CHECK (leavingdate >= hiredate)
);
 
 
--================================================
 
CREATE TABLE CustomerHistory
(
	customerhistoryid number(10) CONSTRAINT customerhistoryid_pk PRIMARY KEY,
	customerid number(7) NOT NULL,
	firstname varchar2(50) NOT NULL,
	lastname varchar2(50) NOT NULL,
	email varchar2(50) NOT NULL,
	address1 varchar2(100) NOT NULL,
	address2 varchar2(100) NOT NULL,
	city varchar2(50) NOT NULL,
	postalcode varchar2(50) NOT NULL,
	province varchar2(50) NOT NULL,
	country varchar2(50) NOT NULL,
	phonetype varchar2(50),
	phonenumber varchar2(100) NOT NULL,
	motelname varchar2(50) NOT NULL,
	moteladdress1 varchar2(50) NOT NULL,
	moteladdress2 varchar2(50) NOT NULL,
	motelprovince varchar2(50) NOT NULL,
	motelcity varchar2(50) NOT NULL,
	motelpostalcode varchar2(50) NOT NULL,
	motelcountry varchar2(50) NOT NULL,
	roomid number(7) NOT NULL,
	roomtype varchar2(50) NOT NULL,
	checkin datetime NOT NULL,
	checkout datetime NOT NULL,
	paymentmethod varchar2(20) NOT NULL,
	paymentdate datetime NOT NULL,
	isFamily varchar2(20) NOT NULL,
	familymembers number(1) NOT NULL,
	isThirdParty varchar2(20) NOT NULL,
	thirdpartyid number(7) NOT NULL
);
 
 
--================================================
 
CREATE TABLE ThirdPartyHistory
(
	thirdpartyhistoryid number(10) CONSTRAINT thirdpartyhistoryid_pk PRIMARY KEY,
	thirdpartyid number(7) NOT NULL,
	firstname varchar2(50) NOT NULL,
	lastname varchar2(50) NOT NULL,
	email varchar2(50) NOT NULL,
	address1 varchar2(100) NOT NULL,
	address2 varchar2(100) NOT NULL,
	province varchar2(50) NOT NULL,
	city varchar2(50) NOT NULL,
	postalcode varchar2(50) NOT NULL,
	country varchar2(50) NOT NULL,
	phonetype varchar2(50),
	phonenumber varchar2(100) NOT NULL,
	gender varchar2(20),
	companyname varchar2(50),
	datecreated datetime NOT NULL
);
 
 
--================================================
 
 
CREATE TABLE CustomerFamilyHistory
(
	customerfamilyhistoryid number(10) CONSTRAINT customerfamilyhistoryid_pk PRIMARY KEY,
	firstname varchar2(50) NOT NULL,
	lastname varchar2(50) NOT NULL,
	age number(3),
	sibling varchar2(20),
	customerid number(7) NOT NULL
);
 
--================================================

