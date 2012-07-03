/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.wizards.sqlbuilder;


/**
 * 
 */
public class SQLTemplates {

    // ----------------------------------------
    // Templates valid for Table/View Targets
    // ----------------------------------------

    public static String SELECT_SIMPLE = "SELECT * FROM [TABLEA]"; //$NON-NLS-1$

    public static String SELECT_JOIN = "SELECT [TABLEA.COL1], [TABLEA.COL2], [TABLEB.COL1] FROM [TABLEA], [TABLEB] " //$NON-NLS-1$
                                       + "WHERE [TABLEA.COL1] = [TABLEB.COL1]"; //$NON-NLS-1$

    public static String SELECT_UNION = "SELECT [COL1], [COL2] FROM [TABLEA] UNION SELECT [COL1], [COL2] FROM [TABLEB]"; //$NON-NLS-1$

    public static String SELECT_FLATFILE_SRC = "SELECT A.[Name], A.[Sport], A.[Position], A.[City] FROM " //$NON-NLS-1$
                                               + "(EXEC [EmployeeFileProcedures].getTextFiles('PlayerData.txt')) AS f, " //$NON-NLS-1$
                                               + " TEXTTABLE(f.file COLUMNS Name string, Sport string, Position string, City string HEADER 2 SKIP 3) AS A"; //$NON-NLS-1$

    public static String SELECT_XMLFILE_LOCAL_SRC = "SELECT A.PMID AS PMID, A.Journal AS Journal, A.Title AS Title FROM " //$NON-NLS-1$
                                                    + "(EXEC MP.getTextFiles('medsamp2011.xml')) AS f, " //$NON-NLS-1$
                                                    + "XMLTABLE('/MedlineCitationSet/MedlineCitation' PASSING XMLPARSE(DOCUMENT f.file) " //$NON-NLS-1$
                                                    + "COLUMNS PMID string PATH '/PMID', Journal string PATH '/Article/Journal', Title string PATH '/Article/ArticleTitle') AS A"; //$NON-NLS-1$

    public static String SELECT_XMLFILE_URL_SRC = "SELECT A.COMMON AS COMMON, A.BOTANICAL AS BOTANICAL, A.ZONE AS ZONE, A.PRICE AS PRICE FROM " //$NON-NLS-1$
                                                  + "(EXEC PlantWSProcedures.invokeHttp('GET', null, 'http://www.w3schools.com/xml/plant_catalog.xml')) AS f, " //$NON-NLS-1$
                                                  + "XMLTABLE('/CATALOG/PLANT' PASSING XMLPARSE(DOCUMENT f.result) " //$NON-NLS-1$
                                                  + "COLUMNS COMMON string PATH '/COMMON', BOTANICAL string PATH '/BOTANICAL', ZONE string PATH '/ZONE', " //$NON-NLS-1$
                                                  + "PRICE string PATH '/PRICE') AS A"; //$NON-NLS-1$

    // ----------------------------------------
    // Templates valid for Procedure Targets
    // ----------------------------------------
    public static String PROC_INSERT_DEFAULT = "FOR EACH ROW BEGIN ATOMIC " //$NON-NLS-1$
                                               + "INSERT INTO [TABLEA] ([COL1], [COL2], [COL3]) VALUES (NEW.[COL1], NEW.[COL2], NEW.[COL3]); END"; //$NON-NLS-1$

    public static String PROC_UPDATE_DEFAULT = "FOR EACH ROW BEGIN " //$NON-NLS-1$
                                               + "UPDATE [TABLEA] SET [COL1]=NEW.[COL1], [COL2]=NEW.[COL2], [COL3]=NEW.[COL3] WHERE [PK-KEY-COL]=OLD.[PK-KEY-COL]; END"; //$NON-NLS-1$

    public static String PROC_DELETE_DEFAULT = "FOR EACH ROW BEGIN " //$NON-NLS-1$
                                               + "DELETE FROM [TABLEA] WHERE [PK-KEY COL] = OLD.[PK-KEY-COL]; END"; //$NON-NLS-1$

    public static String PROC_SOAP_WS_CREATE = "CREATE VIRTUAL PROCEDURE BEGIN " //$NON-NLS-1$
                                               + "SELECT XMLELEMENT(NAME CapitalCity, XMLNAMESPACES(DEFAULT 'http://www.oorsprong.org/websamples.countryinfo'), " //$NON-NLS-1$
                                               + "XMLELEMENT(NAME sCountryISOCode, COUNTRYINFOSERVICEXML.CAPITALCITY.CREATE_CAPITALCITY.sCountryISOCode)) AS xml_out; END"; //$NON-NLS-1$

    public static String PROC_SOAP_WS_EXTRACT = "CREATE VIRTUAL PROCEDURE BEGIN " //$NON-NLS-1$
                                                + "SELECT employee.* FROM XMLTABLE(XMLNAMESPACES('http://teiid.org' as teiid), " //$NON-NLS-1$
                                                + "'/teiid:getdepartmentResponse/return/employee' PASSING f.result " //$NON-NLS-1$
                                                + "COLUMNS empID integer PATH '@id', firstname string PATH 'name/first', " //$NON-NLS-1$
                                                + "lastname string PATH 'name/last') AS employee; END"; //$NON-NLS-1$
    
    public static String REST_PROCEDURE = "CREATE VIRTUAL PROCEDURE BEGIN " //$NON-NLS-1$
    											+ "SELECT XMLELEMENT(NAME authors, " //$NON-NLS-1$
    												+ "XMLAGG(XMLELEMENT(NAME author, " //$NON-NLS-1$
    												+ "XMLFOREST(MySqlBooks.AUTHORS.AUTHOR_ID, MySqlBooks.AUTHORS.FIRSTNAME, MySqlBooks.AUTHORS.LASTNAME, MySqlBooks.AUTHORS.MIDDLEINIT)))) " //$NON-NLS-1$
    												+ "AS result " //$NON-NLS-1$
    												+ "FROM MySqlBooks.AUTHORS " //$NON-NLS-1$
    												+ "WHERE Procedures.GetAuthorByID.author_id = MySqlBooks.AUTHORS.AUTHOR_ID; END"; //$NON-NLS-1$
}
