/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.validator.v7;

import org.junit.Ignore;
import org.junit.Test;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.metadata.Table;
import org.teiid.query.sql.AbstractTestFactory;
import org.teiid.query.sql.v7.Test7Factory;
import org.teiid.query.validator.AbstractTestValidator;

/**
 *
 */
@SuppressWarnings( {"javadoc", "nls"} )
public class Test7Validator extends AbstractTestValidator {

    private Test7Factory factory;

    /**
     *
     */
    public Test7Validator() {
        super(Version.TEIID_7_7);
    }

    @Override
    protected AbstractTestFactory getFactory() {
        if (factory == null)
            factory = new Test7Factory(getQueryParser());

        return factory;
    }

    // valid variable declared
    @Test
    public void testCreateUpdateProcedure4() {
        String procedure = "CREATE PROCEDURE  ";
        procedure = procedure + "BEGIN\n";
        procedure = procedure + "DECLARE integer var1;\n";
        procedure = procedure + "ROWS_UPDATED =0;\n";
        procedure = procedure + "END\n";

        String userUpdateStr = "UPDATE vm1.g1 SET e1='x'";

        helpValidateProcedure(procedure, userUpdateStr, Table.TriggerEvent.UPDATE);
    }

    // validating criteria selector(on HAS CRITERIA), elements on it should be virtual group elements
    @Test
    public void testCreateUpdateProcedure5() {
        String procedure = "CREATE PROCEDURE  ";
        procedure = procedure + "BEGIN\n";
        procedure = procedure + "DECLARE integer var1;\n";
        procedure = procedure + "if(HAS CRITERIA ON (vm1.g1.E1, vm1.g1.e1))\n";
        procedure = procedure + "BEGIN\n";
        procedure = procedure + "END\n";
        procedure = procedure + "ROWS_UPDATED =0;\n";
        procedure = procedure + "END\n";

        String userUpdateStr = "UPDATE vm1.g1 SET e1='x'";

        helpValidateProcedure(procedure, userUpdateStr, Table.TriggerEvent.UPDATE);
    }

    // validating Translate CRITERIA, elements on it should be virtual group elements
    @Test
    public void testCreateUpdateProcedure7() {
        String procedure = "CREATE PROCEDURE  ";
        procedure = procedure + "BEGIN\n";
        procedure = procedure + "DECLARE integer var1;\n";
        procedure = procedure + "Select pm1.g1.e1 from pm1.g1 where Translate CRITERIA WITH (vm1.g1.e1 = 1, vm1.g1.e1 = 2);\n";
        procedure = procedure + "ROWS_UPDATED =0;\n";
        procedure = procedure + "END\n";

        String userUpdateStr = "UPDATE vm1.g1 SET e1='x'";

        helpValidateProcedure(procedure, userUpdateStr, Table.TriggerEvent.UPDATE);
    }

    // ROWS_UPDATED not assigned
    @Test
    public void testCreateUpdateProcedure8() {
        String procedure = "CREATE PROCEDURE  ";
        procedure = procedure + "BEGIN\n";
        procedure = procedure + "DECLARE integer var1;\n";
        procedure = procedure + "Select pm1.g1.e1 from pm1.g1 where Translate CRITERIA WITH (vm1.g1.e1 = 1);\n";
        procedure = procedure + "END\n";

        String userUpdateStr = "UPDATE vm1.g1 SET e1='x'";

        helpFailProcedure(procedure, userUpdateStr, Table.TriggerEvent.UPDATE);
    }

    // TranslateCriteria on criteria of the if statement
    @Test
    public void testCreateUpdateProcedure13() {
        String procedure = "CREATE PROCEDURE  ";
        procedure = procedure + "BEGIN\n";
        procedure = procedure + "DECLARE integer var1;\n";
        procedure = procedure + "if(TRANSLATE CRITERIA ON (vm1.g1.e1) WITH (vm1.g1.e1 = 1))\n";
        procedure = procedure + "BEGIN\n";
        procedure = procedure + "Select pm1.g1.e2 from pm1.g1;\n";
        procedure = procedure + "END\n";
        procedure = procedure + "ROWS_UPDATED =0;\n";
        procedure = procedure + "END\n";

        String userUpdateStr = "UPDATE vm1.g1 SET e1='x'";

        helpValidateProcedure(procedure, userUpdateStr, Table.TriggerEvent.UPDATE);
    }

    // INPUT ised in command
    @Test
    public void testCreateUpdateProcedure16() {
        String procedure = "CREATE PROCEDURE  ";
        procedure = procedure + "BEGIN\n";
        procedure = procedure + "DECLARE integer var1;\n";
        procedure = procedure + "INSERT into pm1.g1 (pm1.g1.e1) values (INPUT.e1);\n";
        procedure = procedure + "ROWS_UPDATED =0;\n";
        procedure = procedure + "END\n";

        String userUpdateStr = "UPDATE vm1.g1 SET e1='x'";

        helpValidateProcedure(procedure, userUpdateStr, Table.TriggerEvent.UPDATE);
    }

    // virtual group elements used in procedure in if statement(TRANSLATE CRITERIA)
    // elements on with should be on ON
    @Test
    public void testCreateUpdateProcedure17() {
        String procedure = "CREATE PROCEDURE  ";
        procedure = procedure + "BEGIN\n";
        procedure = procedure + "DECLARE integer var1;\n";
        procedure = procedure
                    + "Select pm1.g1.e2 from pm1.g1, pm1.g2 where TRANSLATE = CRITERIA ON (e1) WITH (e1 = 20, e2 = 30);\n";
        procedure = procedure + "ROWS_UPDATED =0;\n";
        procedure = procedure + "END\n";

        String userQuery = "UPDATE vm1.g1 SET e1='x'";

        helpFailProcedure(procedure, userQuery, Table.TriggerEvent.UPDATE);
    }

    // virtual group elements used in procedure in if statement(TRANSLATE CRITERIA)
    // failure, aggregate function in query transform
    @Ignore
    @Test
    public void testCreateUpdateProcedure18() {
        String procedure = "CREATE PROCEDURE  ";
        procedure = procedure + "BEGIN\n";
        procedure = procedure + "DECLARE integer var1;\n";
        procedure = procedure + "Select pm1.g1.e2 from pm1.g1 where TRANSLATE = CRITERIA ON (e3);\n";
        procedure = procedure + "ROWS_UPDATED =0;\n";
        procedure = procedure + "END\n";

        String userQuery = "UPDATE vm1.g3 SET x='x' where e3= 1";

        helpFailProcedure(procedure, userQuery, Table.TriggerEvent.UPDATE);
    }

    // virtual group elements used in procedure in if statement(TRANSLATE CRITERIA)
    // failure, aggregate function in query transform
    @Ignore
    @Test
    public void testCreateUpdateProcedure18a() {
        String procedure = "CREATE PROCEDURE  ";
        procedure = procedure + "BEGIN\n";
        procedure = procedure + "DECLARE integer var1;\n";
        procedure = procedure + "Select pm1.g1.e2 from pm1.g1 where TRANSLATE = CRITERIA ON (e3);\n";
        procedure = procedure + "ROWS_UPDATED =0;\n";
        procedure = procedure + "END\n";

        String userQuery = "UPDATE vm1.g3 SET x='x' where y like '%a' and e3= 1";

        helpFailProcedure(procedure, userQuery, Table.TriggerEvent.UPDATE);
    }

    // virtual group elements used in procedure in if statement(TRANSLATE CRITERIA)
    // failure, translated criteria elements not present on groups of command
    @Test
    public void testCreateUpdateProcedure19() {
        String procedure = "CREATE PROCEDURE  ";
        procedure = procedure + "BEGIN\n";
        procedure = procedure + "DECLARE integer var1;\n";
        procedure = procedure + "Select pm1.g2.e2 from pm1.g2 where TRANSLATE = CRITERIA ON (x, y);\n";
        procedure = procedure + "ROWS_UPDATED =0;\n";
        procedure = procedure + "END\n";

        String userQuery = "UPDATE vm1.g3 SET x='x' where y= 1";

        helpFailProcedure(procedure, userQuery, Table.TriggerEvent.UPDATE);
    }

    // virtual group elements used in procedure in if statement(TRANSLATE CRITERIA)
    @Test
    public void testCreateUpdateProcedure20() {
        String procedure = "CREATE PROCEDURE  ";
        procedure = procedure + "BEGIN\n";
        procedure = procedure + "DECLARE integer var1;\n";
        procedure = procedure + "Select pm1.g1.e2 from pm1.g1 where TRANSLATE = CRITERIA WITH (y = e2+1);\n";
        procedure = procedure + "ROWS_UPDATED =0;\n";
        procedure = procedure + "END\n";

        String userQuery = "UPDATE vm1.g3 SET x='x' where y= 1";

        helpValidateProcedure(procedure, userQuery, Table.TriggerEvent.UPDATE);
    }

    // virtual group elements used in procedure in if statement(TRANSLATE CRITERIA)
    @Test
    public void testCreateUpdateProcedure25() {
        String procedure = "CREATE PROCEDURE  ";
        procedure = procedure + "BEGIN\n";
        procedure = procedure + "DECLARE integer var1;\n";
        procedure = procedure + "Select pm1.g2.e2 from pm1.g2 where TRANSLATE > CRITERIA ON (y);\n";
        procedure = procedure + "ROWS_UPDATED =0;\n";
        procedure = procedure + "END\n";

        String userQuery = "UPDATE vm1.g3 SET x='x' where y > 1";

        helpFailProcedure(procedure, userQuery, Table.TriggerEvent.UPDATE);
    }

    // virtual group elements used in procedure in if statement(TRANSLATE CRITERIA)
    @Test
    public void testCreateUpdateProcedure26() {
        String procedure = "CREATE PROCEDURE  ";
        procedure = procedure + "BEGIN\n";
        procedure = procedure + "DECLARE integer var1;\n";
        procedure = procedure + "Select pm1.g1.e2 from pm1.g1 where TRANSLATE = CRITERIA WITH (e3 = e2+1);\n";
        procedure = procedure + "ROWS_UPDATED =0;\n";
        procedure = procedure + "END\n";

        String userQuery = "UPDATE vm1.g3 SET x='x' where e3 > 1";

        helpValidateProcedure(procedure, userQuery, Table.TriggerEvent.UPDATE);
    }

    // virtual group elements used in procedure in if statement(TRANSLATE CRITERIA)
    @Test
    public void testCreateUpdateProcedure27() {
        String procedure = "CREATE PROCEDURE  ";
        procedure = procedure + "BEGIN\n";
        procedure = procedure + "DECLARE integer var1;\n";
        procedure = procedure + "Select pm1.g2.e2 from pm1.g2 where TRANSLATE LIKE CRITERIA WITH (y = e2+1);\n";
        procedure = procedure + "ROWS_UPDATED =0;\n";
        procedure = procedure + "END\n";

        String userQuery = "UPDATE vm1.g3 SET x='x' where y = 1";

        helpValidateProcedure(procedure, userQuery, Table.TriggerEvent.UPDATE);
    }

    // using aggregate function within a procedure - defect #8394
    @Test
    public void testCreateUpdateProcedure31() {
        String procedure = "CREATE PROCEDURE  ";
        procedure = procedure + "BEGIN\n";
        procedure = procedure + "DECLARE string MaxTran;\n";
        procedure = procedure + "MaxTran = SELECT MAX(e1) FROM pm1.g1;\n";
        procedure = procedure + "ROWS_UPDATED =0;\n";
        procedure = procedure + "END\n";

        String userQuery = "UPDATE vm1.g3 SET x='x' where y = 1";

        helpValidateProcedure(procedure, userQuery, Table.TriggerEvent.UPDATE);
    }

    // assigning null values to known datatype variable
    @Test
    public void testCreateUpdateProcedure32() {
        String procedure = "CREATE PROCEDURE  ";
        procedure = procedure + "BEGIN\n";
        procedure = procedure + "DECLARE string var;\n";
        procedure = procedure + "var = null;\n";
        procedure = procedure + "ROWS_UPDATED =0;\n";
        procedure = procedure + "END\n";

        String userQuery = "UPDATE vm1.g3 SET x='x' where y = 1";

        helpValidateProcedure(procedure, userQuery, Table.TriggerEvent.UPDATE);
    }

    @Test
    public void testDefect13643() {
        String procedure = "CREATE PROCEDURE  ";
        procedure = procedure + "BEGIN\n";
        procedure = procedure + "DECLARE integer var1;\n";
        procedure = procedure + "LOOP ON (SELECT * FROM pm1.g1) AS myCursor\n";
        procedure = procedure + "BEGIN\n";
        procedure = procedure + "var1 = SELECT COUNT(*) FROM myCursor;\n";
        procedure = procedure + "END\n";
        procedure = procedure + "ROWS_UPDATED = 0;\n";
        procedure = procedure + "END\n";

        String userQuery = "UPDATE vm1.g3 SET x='x' where y = 1";

        helpFailProcedure(procedure, userQuery, Table.TriggerEvent.UPDATE);
    }

    @Test
    public void testSelectIntoTempGroup() {
        String procedure = "CREATE PROCEDURE  ";
        procedure = procedure + "BEGIN\n";
        procedure = procedure + "SELECT e1, e2, e3, e4 INTO #myTempTable FROM pm1.g2;\n";
        procedure = procedure + "ROWS_UPDATED = SELECT COUNT(*) FROM #myTempTable;\n";
        procedure = procedure + "END\n";

        String userQuery = "UPDATE vm1.g3 SET x='x' where y = 1";

        helpValidateProcedure(procedure, userQuery, Table.TriggerEvent.UPDATE);
    }

    /**
     * Defect 24346
     */
    @Test
    public void testInvalidSelectIntoTempGroup() {
        String procedure = "CREATE PROCEDURE  ";
        procedure = procedure + "BEGIN\n";
        procedure = procedure + "SELECT e1, e2, e3, e4 INTO #myTempTable FROM pm1.g2;\n";
        procedure = procedure + "SELECT e1, e2, e3 INTO #myTempTable FROM pm1.g2;\n";
        procedure = procedure + "ROWS_UPDATED = SELECT COUNT(*) FROM #myTempTable;\n";
        procedure = procedure + "END\n";

        String userQuery = "UPDATE vm1.g3 SET x='x' where y = 1";

        helpFailProcedure(procedure, userQuery, Table.TriggerEvent.UPDATE);
    }

    /**
     * Defect 24346 with type mismatch
     */
    @Test
    public void testInvalidSelectIntoTempGroup1() {
        String procedure = "CREATE PROCEDURE  ";
        procedure = procedure + "BEGIN\n";
        procedure = procedure + "create local temporary table #myTempTable (e1 integer);\n";
        procedure = procedure + "SELECT e1 INTO #myTempTable FROM pm1.g2;\n";
        procedure = procedure + "ROWS_UPDATED = SELECT COUNT(*) FROM #myTempTable;\n";
        procedure = procedure + "END\n";

        String userQuery = "UPDATE vm1.g3 SET x='x' where y = 1";

        helpFailProcedure(procedure, userQuery, Table.TriggerEvent.UPDATE);
    }

    @Test
    public void testSelectIntoPhysicalGroup() {
        helpValidate("SELECT e1, e2, e3, e4 INTO pm1.g1 FROM pm1.g2", new String[] {}, getMetadataFactory().example1Cached());

        String procedure = "CREATE PROCEDURE  ";
        procedure = procedure + "BEGIN\n";
        procedure = procedure + "SELECT e1, e2, e3, e4 INTO pm1.g1 FROM pm1.g2;\n";
        procedure = procedure + "ROWS_UPDATED = 0;\n";
        procedure = procedure + "END\n";

        String userQuery = "UPDATE vm1.g3 SET x='x' where y = 1";

        helpValidateProcedure(procedure, userQuery, Table.TriggerEvent.UPDATE);
    }

    @Test
    public void testInvalidSelectIntoTooManyElements() {
        helpValidate("SELECT e1, e2, e3, e4, 'val' INTO pm1.g1 FROM pm1.g2", new String[] {"SELECT e1, e2, e3, e4, 'val' INTO pm1.g1 FROM pm1.g2"}, getMetadataFactory().example1Cached()); //$NON-NLS-2$

        String procedure = "CREATE PROCEDURE  ";
        procedure = procedure + "BEGIN\n";
        procedure = procedure + "SELECT e1, e2, e3, e4, 'val' INTO pm1.g1 FROM pm1.g2;\n";
        procedure = procedure + "ROWS_UPDATED = 0;\n";
        procedure = procedure + "END\n";

        String userQuery = "UPDATE vm1.g3 SET x='x' where y = 1";

        helpFailProcedure(procedure, userQuery, Table.TriggerEvent.UPDATE);
    }

    @Test
    public void testInvalidSelectIntoTooFewElements() {
        helpValidate("SELECT e1, e2, e3 INTO pm1.g1 FROM pm1.g2", new String[] {"SELECT e1, e2, e3 INTO pm1.g1 FROM pm1.g2"}, getMetadataFactory().example1Cached()); //$NON-NLS-2$

        String procedure = "CREATE PROCEDURE  ";
        procedure = procedure + "BEGIN\n";
        procedure = procedure + "SELECT e1, e2, e3 INTO pm1.g1 FROM pm1.g2;\n";
        procedure = procedure + "ROWS_UPDATED = 0;\n";
        procedure = procedure + "END\n";

        String userQuery = "UPDATE vm1.g3 SET x='x' where y = 1";

        helpFailProcedure(procedure, userQuery, Table.TriggerEvent.UPDATE);
    }

    @Test
    public void testInvalidSelectIntoIncorrectTypes() {
        helpValidate("SELECT e1, convert(e2, string), e3, e4 INTO pm1.g1 FROM pm1.g2", new String[] {"SELECT e1, convert(e2, string), e3, e4 INTO pm1.g1 FROM pm1.g2"}, getMetadataFactory().example1Cached()); //$NON-NLS-2$

        String procedure = "CREATE PROCEDURE  ";
        procedure = procedure + "BEGIN\n";
        procedure = procedure + "SELECT e1, convert(e2, string), e3, e4 INTO pm1.g1 FROM pm1.g2;\n";
        procedure = procedure + "ROWS_UPDATED = 0;\n";
        procedure = procedure + "END\n";

        String userQuery = "UPDATE vm1.g3 SET x='x' where y = 1";

        helpFailProcedure(procedure, userQuery, Table.TriggerEvent.UPDATE);
    }

    @Test
    public void testInvalidSelectIntoWithStar() {
        helpValidate("SELECT * INTO pm1.g1 FROM pm1.g2, pm1.g1", new String[] {"SELECT * INTO pm1.g1 FROM pm1.g2, pm1.g1"}, getMetadataFactory().example1Cached()); //$NON-NLS-2$

        String procedure = "CREATE PROCEDURE  ";
        procedure = procedure + "BEGIN\n";
        procedure = procedure + "SELECT * INTO pm1.g1 FROM pm1.g2, pm1.g1;\n";
        procedure = procedure + "ROWS_UPDATED = 0;\n";
        procedure = procedure + "END\n";

        String userQuery = "UPDATE vm1.g3 SET x='x' where y = 1";

        helpFailProcedure(procedure, userQuery, Table.TriggerEvent.UPDATE);
    }

    @Test
    public void testSelectIntoVirtualGroup() {
        helpValidate("SELECT e1, e2, e3, e4 INTO vm1.g1 FROM pm1.g2", new String[] {}, getMetadataFactory().example1Cached());

        String procedure = "CREATE PROCEDURE  ";
        procedure = procedure + "BEGIN\n";
        procedure = procedure + "SELECT e1, e2, e3, e4 INTO vm1.g1 FROM pm1.g2;\n";
        procedure = procedure + "ROWS_UPDATED = 0;\n";
        procedure = procedure + "END\n";

        String userQuery = "UPDATE vm1.g3 SET x='x' where y = 1";

        helpValidateProcedure(procedure, userQuery, Table.TriggerEvent.UPDATE);
    }

    @Test
    public void testInvalidLimit_Offset() {
        helpValidate("SELECT * FROM pm1.g1 LIMIT -1, 100", new String[] {"LIMIT -1, 100"}, getMetadataFactory().example1Cached()); //$NON-NLS-2$
    }

    @Test
    public void testTextTableNegativeWidth() {
        helpValidate("SELECT * from texttable(null columns x string width -1) as x",
                     new String[] {"TEXTTABLE(null COLUMNS x string WIDTH -1) AS x"},
                     getMetadataFactory().exampleBQTCached());
    }
}
