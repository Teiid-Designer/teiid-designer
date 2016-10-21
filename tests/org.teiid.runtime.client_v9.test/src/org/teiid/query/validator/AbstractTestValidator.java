/*
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.teiid.query.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Ignore;
import org.junit.Test;
import org.teiid.api.exception.query.QueryResolverException;
import org.teiid.core.types.DataTypeManagerService;
import org.teiid.designer.query.metadata.IQueryMetadataInterface;
import org.teiid.designer.query.sql.lang.ICommand;
import org.teiid.designer.query.sql.lang.ISPParameter;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.metadata.BaseColumn.NullType;
import org.teiid.metadata.Column;
import org.teiid.metadata.Column.SearchType;
import org.teiid.metadata.ColumnSet;
import org.teiid.metadata.MetadataStore;
import org.teiid.metadata.Procedure;
import org.teiid.metadata.ProcedureParameter;
import org.teiid.metadata.Schema;
import org.teiid.metadata.Table;
import org.teiid.query.mapping.relational.QueryNode;
import org.teiid.query.mapping.xml.MappingDocument;
import org.teiid.query.mapping.xml.MappingElement;
import org.teiid.query.metadata.TransformationMetadata;
import org.teiid.query.resolver.QueryResolver;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.lang.LanguageObject;
import org.teiid.query.sql.lang.ProcedureContainer;
import org.teiid.query.sql.symbol.GroupSymbol;
import org.teiid.query.sql.visitor.SQLStringVisitor;

@SuppressWarnings( {"javadoc", "nls"} )
public abstract class AbstractTestValidator extends AbstractTest {

    /**
     * @param teiidVersion
     */
    public AbstractTestValidator(Version teiidVersion) {
        super(teiidVersion);
    }

    public TransformationMetadata exampleMetadata() {
        MetadataStore metadataStore = new MetadataStore();
        // Create metadata objects        
        Schema modelObj = getMetadataFactory().createPhysicalModel("test", metadataStore); //$NON-NLS-1$
        Schema vModelObj2 = getMetadataFactory().createVirtualModel("vTest", metadataStore); //$NON-NLS-1$
        Table groupObj = getMetadataFactory().createPhysicalGroup("group", modelObj); //$NON-NLS-1$
        Column elemObj0 = getMetadataFactory().createElement("e0", groupObj, DataTypeManagerService.DefaultDataTypes.INTEGER.getId()); //$NON-NLS-1$
        elemObj0.setNullType(NullType.No_Nulls);
        Column elemObj1 = getMetadataFactory().createElement("e1", groupObj, DataTypeManagerService.DefaultDataTypes.STRING.getId()); //$NON-NLS-1$
        elemObj1.setSelectable(false);
        Column elemObj2 = getMetadataFactory().createElement("e2", groupObj, DataTypeManagerService.DefaultDataTypes.STRING.getId()); //$NON-NLS-1$
        elemObj2.setSearchType(SearchType.Like_Only);
        Column elemObj3 = getMetadataFactory().createElement("e3", groupObj, DataTypeManagerService.DefaultDataTypes.STRING.getId()); //$NON-NLS-1$
        elemObj3.setSearchType(SearchType.All_Except_Like);

        Table group2Obj = getMetadataFactory().createPhysicalGroup("group2", modelObj); //$NON-NLS-1$
        Column elemObj2_0 = getMetadataFactory().createElement("e0", group2Obj, DataTypeManagerService.DefaultDataTypes.INTEGER.getId()); //$NON-NLS-1$
        elemObj2_0.setUpdatable(false);
        getMetadataFactory().createElement("e1", group2Obj, DataTypeManagerService.DefaultDataTypes.STRING.getId()); //$NON-NLS-1$
        Column elemObj2_2 = getMetadataFactory().createElement("e2", group2Obj, DataTypeManagerService.DefaultDataTypes.STRING.getId()); //$NON-NLS-1$
        elemObj2_2.setUpdatable(false);

        Table group3Obj = getMetadataFactory().createPhysicalGroup("group3", modelObj); //$NON-NLS-1$
        group3Obj.setSupportsUpdate(false);
        getMetadataFactory().createElement("e0", group3Obj, DataTypeManagerService.DefaultDataTypes.INTEGER.getId()); //$NON-NLS-1$
        getMetadataFactory().createElement("e1", group3Obj, DataTypeManagerService.DefaultDataTypes.STRING.getId()); //$NON-NLS-1$
        getMetadataFactory().createElement("e2", group3Obj, DataTypeManagerService.DefaultDataTypes.STRING.getId()); //$NON-NLS-1$

        // Create virtual group & elements.
        QueryNode vNode = new QueryNode("SELECT * FROM test.group WHERE e2 = 'x'"); //$NON-NLS-1$ //$NON-NLS-2$
        Table vGroup = getMetadataFactory().createVirtualGroup("vGroup", vModelObj2, vNode); //$NON-NLS-1$
        getMetadataFactory().createElements(vGroup, new String[] {"e0", "e1", "e2", "e3"}, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                                           new String[] {DataTypeManagerService.DefaultDataTypes.INTEGER.getId(),
                                               DataTypeManagerService.DefaultDataTypes.STRING.getId(), DataTypeManagerService.DefaultDataTypes.STRING.getId(),
                                               DataTypeManagerService.DefaultDataTypes.STRING.getId()});

        QueryNode vNode2 = new QueryNode("SELECT * FROM test.group"); //$NON-NLS-1$ //$NON-NLS-2$
        Table vGroup2 = getMetadataFactory().createVirtualGroup("vMap", vModelObj2, vNode2); //$NON-NLS-1$
        List<Column> vGroupE2 = getMetadataFactory().createElements(vGroup2, new String[] {"e0", "e1", "e2", "e3"}, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                                                                   new String[] {DataTypeManagerService.DefaultDataTypes.INTEGER.getId(),
                                                                       DataTypeManagerService.DefaultDataTypes.STRING.getId(),
                                                                       DataTypeManagerService.DefaultDataTypes.STRING.getId(),
                                                                       DataTypeManagerService.DefaultDataTypes.STRING.getId()});
        vGroupE2.get(0).setNullType(NullType.No_Nulls);
        vGroupE2.get(1).setSelectable(false);
        vGroupE2.get(2).setSearchType(SearchType.Like_Only);
        vGroupE2.get(3).setSearchType(SearchType.All_Except_Like);

        // Create virtual documents
        MappingDocument doc = new MappingDocument(getTeiidVersion(), false);
        MappingElement complexRoot = new MappingElement(getTeiidVersion(), "a0"); //$NON-NLS-1$
        doc.addChildElement(complexRoot);

        MappingElement sourceNode = new MappingElement(getTeiidVersion(), "a1"); //$NON-NLS-1$
        complexRoot.addChildElement(sourceNode);
        sourceNode.setSource("test.group"); //$NON-NLS-1$
        sourceNode.addChildElement(new MappingElement(getTeiidVersion(), "a2", "test.group.e1")); //$NON-NLS-1$ //$NON-NLS-2$
        sourceNode.addChildElement(new MappingElement(getTeiidVersion(), "b2", "test.group.e2")); //$NON-NLS-1$ //$NON-NLS-2$
        sourceNode.addChildElement(new MappingElement(getTeiidVersion(), "c2", "test.group.e3")); //$NON-NLS-1$ //$NON-NLS-2$

        Schema docModel = getMetadataFactory().createVirtualModel("vm1", metadataStore); //$NON-NLS-1$
        Table doc1 = getMetadataFactory().createXmlDocument("doc1", docModel, doc); //$NON-NLS-1$

        return getMetadataFactory().createTransformationMetadata(metadataStore, "example");
    }

    public TransformationMetadata exampleMetadata1() {
        MetadataStore metadataStore = new MetadataStore();
        // Create metadata objects        
        Schema modelObj = getMetadataFactory().createPhysicalModel("test", metadataStore); //$NON-NLS-1$
        Table groupObj = getMetadataFactory().createPhysicalGroup("group", modelObj); //$NON-NLS-1$

        Column elemObj0 = getMetadataFactory().createElement("e0", groupObj, DataTypeManagerService.DefaultDataTypes.INTEGER.getId()); //$NON-NLS-1$
        Column elemObj1 = getMetadataFactory().createElement("e1", groupObj, DataTypeManagerService.DefaultDataTypes.STRING.getId()); //$NON-NLS-1$
        Column elemObj2 = getMetadataFactory().createElement("e2", groupObj, DataTypeManagerService.DefaultDataTypes.STRING.getId()); //$NON-NLS-1$
        getMetadataFactory().createElement("e3", groupObj, DataTypeManagerService.DefaultDataTypes.STRING.getId()); //$NON-NLS-1$

        elemObj0.setNullType(NullType.No_Nulls);

        elemObj1.setNullType(NullType.Nullable);
        elemObj1.setDefaultValue(Boolean.TRUE.toString());

        elemObj2.setNullType(NullType.Nullable);
        elemObj2.setDefaultValue(Boolean.FALSE.toString());

        return getMetadataFactory().createTransformationMetadata(metadataStore, "example1");
    }

    /**
     * Group has element with type object
     * @return IQueryMetadataInterface
     */
    public TransformationMetadata exampleMetadata2() {
        MetadataStore metadataStore = new MetadataStore();
        // Create metadata objects
        Schema modelObj = getMetadataFactory().createPhysicalModel("test", metadataStore); //$NON-NLS-1$
        Table groupObj = getMetadataFactory().createPhysicalGroup("group", modelObj); //$NON-NLS-1$

        getMetadataFactory().createElements(groupObj, new String[] {"e0", "e1", "e2", "e3", "e4", "e5" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
        }, new String[] {DataTypeManagerService.DefaultDataTypes.INTEGER.getId(), DataTypeManagerService.DefaultDataTypes.STRING.getId(),
            DataTypeManagerService.DefaultDataTypes.OBJECT.getId(), DataTypeManagerService.DefaultDataTypes.BLOB.getId(),
            DataTypeManagerService.DefaultDataTypes.CLOB.getId(), DataTypeManagerService.DefaultDataTypes.XML.getId(),});

        return getMetadataFactory().createTransformationMetadata(metadataStore, "example2");
    }

    public TransformationMetadata exampleMetadata3() {
        MetadataStore metadataStore = new MetadataStore();
        // Create metadata objects        
        Schema modelObj = getMetadataFactory().createPhysicalModel("test", metadataStore); //$NON-NLS-1$
        Table groupObj = getMetadataFactory().createPhysicalGroup("group", modelObj); //$NON-NLS-1$

        getMetadataFactory().createElement("e0", groupObj, DataTypeManagerService.DefaultDataTypes.INTEGER.getId()); //$NON-NLS-1$
        Column elemObj1 = getMetadataFactory().createElement("e1", groupObj, DataTypeManagerService.DefaultDataTypes.STRING.getId()); //$NON-NLS-1$

        elemObj1.setNullType(NullType.No_Nulls);
        elemObj1.setDefaultValue(Boolean.FALSE.toString());
        elemObj1.setAutoIncremented(true);
        elemObj1.setNameInSource("e1:SEQUENCE=MYSEQUENCE.nextVal"); //$NON-NLS-1$

        return getMetadataFactory().createTransformationMetadata(metadataStore, "example3");
    }

    public TransformationMetadata exampleMetadata4() {
        MetadataStore metadataStore = new MetadataStore();
        // Create metadata objects 
        Schema modelObj = getMetadataFactory().createPhysicalModel("test", metadataStore); //$NON-NLS-1$
        Table groupObj = getMetadataFactory().createPhysicalGroup("group", modelObj); //$NON-NLS-1$
        getMetadataFactory().createElement("e0", groupObj, DataTypeManagerService.DefaultDataTypes.INTEGER.getId()); //$NON-NLS-1$
        getMetadataFactory().createElement("e1", groupObj, DataTypeManagerService.DefaultDataTypes.STRING.getId()); //$NON-NLS-1$
        getMetadataFactory().createElement("e2", groupObj, DataTypeManagerService.DefaultDataTypes.STRING.getId()); //$NON-NLS-1$
        Schema vModelObj = getMetadataFactory().createVirtualModel("vTest", metadataStore); //$NON-NLS-1$
        QueryNode vNode = new QueryNode("SELECT * FROM test.group"); //$NON-NLS-1$ //$NON-NLS-2$ 
        Table vGroupObj = getMetadataFactory().createVirtualGroup("vGroup", vModelObj, vNode); //$NON-NLS-1$
        Column vElemObj0 = getMetadataFactory().createElement("e0", vGroupObj, DataTypeManagerService.DefaultDataTypes.INTEGER.getId()); //$NON-NLS-1$
        Column vElemObj1 = getMetadataFactory().createElement("e1", vGroupObj, DataTypeManagerService.DefaultDataTypes.STRING.getId()); //$NON-NLS-1$
        getMetadataFactory().createElement("e2", vGroupObj, DataTypeManagerService.DefaultDataTypes.STRING.getId()); //$NON-NLS-1$
        List<Column> elements = new ArrayList<Column>(2);
        elements.add(vElemObj0);
        elements.add(vElemObj1);
        getMetadataFactory().createAccessPattern("ap1", vGroupObj, elements); //e1 //$NON-NLS-1$

        QueryNode vNode2 = new QueryNode("SELECT * FROM vTest.vGroup"); //$NON-NLS-1$ //$NON-NLS-2$ 
        Table vGroupObj2 = getMetadataFactory().createVirtualGroup("vGroup2", vModelObj, vNode2); //$NON-NLS-1$
        Column vElemObj20 = getMetadataFactory().createElement("e0", vGroupObj2, DataTypeManagerService.DefaultDataTypes.INTEGER.getId()); //$NON-NLS-1$
        Column vElemObj21 = getMetadataFactory().createElement("e1", vGroupObj2, DataTypeManagerService.DefaultDataTypes.STRING.getId()); //$NON-NLS-1$
        getMetadataFactory().createElement("e2", vGroupObj2, DataTypeManagerService.DefaultDataTypes.STRING.getId()); //$NON-NLS-1$
        elements = new ArrayList<Column>(2);
        elements.add(vElemObj20);
        elements.add(vElemObj21);
        getMetadataFactory().createAccessPattern("vTest.vGroup2.ap1", vGroupObj2, elements); //e1 //$NON-NLS-1$

        return getMetadataFactory().createTransformationMetadata(metadataStore, "example4");
    }

    // ################################## TEST HELPERS ################################

    private Command helpResolve(String sql, IQueryMetadataInterface metadata) {
        Command command = null;

        try {
            command = getQueryParser().parseCommand(sql);
            QueryResolver queryResolver = new QueryResolver(getTeiidVersion());
            queryResolver.resolveCommand(command, metadata);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return command;
    }

    private Command helpResolve(String sql, GroupSymbol container, int type, IQueryMetadataInterface metadata)
        throws Exception {
        Command command = getQueryParser().parseCommand(sql);
        QueryResolver queryResolver = new QueryResolver(getTeiidVersion());
        queryResolver.resolveCommand(command, container, type, metadata, false);
        return command;
    }

    public ValidatorReport helpValidate(String sql, String[] expectedStringArray, IQueryMetadataInterface metadata) {
        Command command = helpResolve(sql, metadata);

        return helpRunValidator(command, expectedStringArray, metadata);
    }

    public ValidatorReport helpRunValidator(Command command, String[] expectedStringArray, IQueryMetadataInterface metadata) {
        try {
            ValidatorReport report = new Validator().validate(command, metadata);

            examineReport(command, expectedStringArray, report);
            return report;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void examineReport(Object command, String[] expectedStringArray, ValidatorReport report) {
        // Get invalid objects from report
        Collection<LanguageObject> actualObjs = new ArrayList<LanguageObject>();
        report.collectInvalidObjects(actualObjs);

        // Compare expected and actual objects
        Set<String> expectedStrings = new HashSet<String>(Arrays.asList(expectedStringArray));
        Set<String> actualStrings = new HashSet<String>();
        for (LanguageObject obj : actualObjs) {
            actualStrings.add(SQLStringVisitor.getSQLString(obj));
        }

        if (expectedStrings.size() == 0 && actualStrings.size() > 0) {
            fail("Expected no failures but got some: " + report.getFailureMessage()); //$NON-NLS-1$ 
        } else if (actualStrings.size() == 0 && expectedStrings.size() > 0) {
            fail("Expected some failures but got none for sql = " + command); //$NON-NLS-1$
        } else {
            assertEquals("Expected and actual sets of strings are not the same: ", expectedStrings, actualStrings); //$NON-NLS-1$
        }
    }

    protected void helpValidateProcedure(String procedure, String userUpdateStr, Table.TriggerEvent procedureType) {

        IQueryMetadataInterface metadata = getMetadataFactory().exampleUpdateProc(procedureType, procedure);

        try {
            validateProcedure(userUpdateStr, metadata);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void validateProcedure(String userUpdateStr, IQueryMetadataInterface metadata)
        throws Exception {
        ProcedureContainer command = (ProcedureContainer)helpResolve(userUpdateStr, metadata);

        QueryResolver queryResolver = new QueryResolver(getTeiidVersion());
        Command proc = queryResolver.expandCommand(command, metadata);

        Validator validator = new Validator();
        ValidatorReport report = validator.validate(proc, metadata);
        if (report.hasItems()) {
            throw new Exception(report.getFailureMessage());
        }

        report = validator.validate(command, metadata);
        if (report.hasItems()) {
            throw new Exception(report.getFailureMessage());
        }
    }

    protected void helpFailProcedure(String procedure, String userUpdateStr, Table.TriggerEvent procedureType) {

        IQueryMetadataInterface metadata = getMetadataFactory().exampleUpdateProc(procedureType, procedure);

        try {
            validateProcedure(userUpdateStr, metadata);
            fail("Expected failures for " + procedure);
        } catch (Exception e) {
            // Expected exception required
        }
    }

    // ################################## ACTUAL TESTS ################################

    @Test
    public void testSelectStarWhereNoElementsAreNotSelectable() {
        helpValidate("SELECT * FROM pm1.g5", new String[] {"SELECT * FROM pm1.g5"}, getMetadataFactory().example1Cached()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testValidateSelect1() {
        helpValidate("SELECT e1, e2 FROM test.group", new String[] {"e1"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testValidateSelect2() {
        helpValidate("SELECT e2 FROM test.group", new String[] {}, exampleMetadata()); //$NON-NLS-1$
    }

    @Test
    public void testValidateCompare1() {
        helpValidate("SELECT e2 FROM vTest.vMap WHERE e2 = 'a'", new String[] {}, exampleMetadata()); //$NON-NLS-1$ 
    }

    @Test
    public void testValidateCompare4() {
        helpValidate("SELECT e3 FROM vTest.vMap WHERE e3 LIKE 'a'", new String[] {}, exampleMetadata()); //$NON-NLS-1$ 
    }

    @Test
    public void testValidateCompare6() {
        helpValidate("SELECT e0 FROM vTest.vMap WHERE e0 BETWEEN 1000 AND 2000", new String[] {}, exampleMetadata()); //$NON-NLS-1$
    }

    @Test
    public void testValidateCompareInHaving2() {
        helpValidate("SELECT e2 FROM vTest.vMap GROUP BY e2 HAVING e2 IS NULL", new String[] {}, exampleMetadata()); //$NON-NLS-1$ 
    }

    @Test
    public void testValidateCompareInHaving3() {
        helpValidate("SELECT e2 FROM vTest.vMap GROUP BY e2 HAVING e2 IN ('a')", new String[] {}, exampleMetadata()); //$NON-NLS-1$ 
    }

    @Test
    public void testValidateCompareInHaving4() {
        helpValidate("SELECT e3 FROM vTest.vMap GROUP BY e3 HAVING e3 LIKE 'a'", new String[] {}, exampleMetadata()); //$NON-NLS-1$ 
    }

    @Test
    public void testValidateCompareInHaving5() {
        helpValidate("SELECT e2 FROM vTest.vMap GROUP BY e2 HAVING e2 BETWEEN 1000 AND 2000", new String[] {}, exampleMetadata()); //$NON-NLS-1$ 
    }

    @Test
    public void testInvalidAggregate1() {
        helpValidate("SELECT SUM(e3) FROM test.group GROUP BY e2", new String[] {"SUM(e3)"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testInvalidAggregate2() {
        helpValidate("SELECT e3 FROM test.group GROUP BY e2", new String[] {"e3"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testInvalidAggregate3() {
        helpValidate("SELECT SUM(e2) FROM test.group GROUP BY e2", new String[] {"SUM(e2)"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testInvalidAggregate4() {
        helpValidate("SELECT AVG(e2) FROM test.group GROUP BY e2", new String[] {"AVG(e2)"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testInvalidAggregate5() {
        helpValidate("SELECT e1 || 'x' frOM pm1.g1 GROUP BY e2 + 1", new String[] {"e1"}, getMetadataFactory().example1Cached()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testInvalidAggregate6() {
        helpValidate("SELECT e2 + 1 frOM pm1.g1 GROUP BY e2 + 1 HAVING e1 || 'x' > 0", new String[] {"e1"}, getMetadataFactory().example1Cached()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testInvalidAggregate7() {
        helpValidate("SELECT StringKey, SUM(length(StringKey || 'x')) + 1 AS x FROM BQT1.SmallA GROUP BY StringKey || 'x' HAVING space(MAX(length((StringKey || 'x') || 'y'))) = '   '", //$NON-NLS-1$
                     new String[] {"StringKey"}, getMetadataFactory().exampleBQTCached()); //$NON-NLS-1$
    }

    @Test
    public void testInvalidAggregate8() {
        helpValidate("SELECT max(ObjectValue) FROM BQT1.SmallA GROUP BY StringKey", //$NON-NLS-1$
                     new String[] {"MAX(ObjectValue)"}, getMetadataFactory().exampleBQTCached()); //$NON-NLS-1$
    }

    @Test
    public void testInvalidAggregate9() {
        helpValidate("SELECT count(distinct ObjectValue) FROM BQT1.SmallA GROUP BY StringKey", //$NON-NLS-1$
                     new String[] {"COUNT(DISTINCT ObjectValue)"}, getMetadataFactory().exampleBQTCached()); //$NON-NLS-1$
    }

    /**
     * previously failed on stringkey, which is not entirely correct
     */
    @Test
    public void testInvalidAggregate10() {
        helpValidate("SELECT xmlparse(document stringkey) FROM BQT1.SmallA GROUP BY xmlparse(document stringkey)", //$NON-NLS-1$
                     new String[] {"XMLPARSE(DOCUMENT stringkey)"}, getMetadataFactory().exampleBQTCached()); //$NON-NLS-1$
    }

    @Test
    public void testInvalidAggregateIssue190644() {
        helpValidate("SELECT e3 + 1 from pm1.g1 GROUP BY e2 + 1 HAVING e2 + 1 = 5", new String[] {"e3"}, getMetadataFactory().example1Cached()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testValidAggregate1() {
        helpValidate("SELECT (e2 + 1) * 2 frOM pm1.g1 GROUP BY e2 + 1", new String[] {}, getMetadataFactory().example1Cached()); //$NON-NLS-1$ 
    }

    @Test
    public void testValidAggregate2() {
        helpValidate("SELECT e2 + 1 frOM pm1.g1 GROUP BY e2 + 1", new String[] {}, getMetadataFactory().example1Cached()); //$NON-NLS-1$ 
    }

    @Test
    public void testValidAggregate3() {
        helpValidate("SELECT sum (IntKey), case when IntKey>=5000 then '5000 +' else '0-999' end " + //$NON-NLS-1$
                     "FROM BQT1.SmallA GROUP BY case when IntKey>=5000 then '5000 +' else '0-999' end", //$NON-NLS-1$
                     new String[] {},
                     getMetadataFactory().exampleBQTCached());
    }

    @Test
    public void testValidAggregate4() {
        helpValidate("SELECT max(e1), e2 is null from pm1.g1 GROUP BY e2 is null", new String[] {}, getMetadataFactory().example1Cached()); //$NON-NLS-1$ 
    }

    @Test
    public void testInvalidHaving1() {
        helpValidate("SELECT e3 FROM test.group HAVING e3 > 0", new String[] {"e3"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testInvalidHaving2() {
        helpValidate("SELECT e3 FROM test.group HAVING concat(e3,'a') > 0", new String[] {"e3"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testNestedAggregateInHaving() {
        helpValidate("SELECT e0 FROM test.group GROUP BY e0 HAVING SUM(COUNT(e0)) > 0", new String[] {"COUNT(e0)"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testNestedAggregateInSelect() {
        helpValidate("SELECT SUM(COUNT(e0)) FROM test.group GROUP BY e0", new String[] {"COUNT(e0)"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testValidateCaseInGroupBy() {
        helpValidate("SELECT SUM(e2) FROM pm1.g1 GROUP BY CASE e2 WHEN 0 THEN 1 ELSE 2 END", new String[] {}, getMetadataFactory().example1Cached()); //$NON-NLS-1$ 
    }

    @Test
    public void testValidateFunctionInGroupBy() {
        helpValidate("SELECT SUM(e2) FROM pm1.g1 GROUP BY (e2 + 1)", new String[] {}, getMetadataFactory().example1Cached()); //$NON-NLS-1$ 
    }

    @Test
    public void testInvalidScalarSubqueryInGroupBy() {
        helpValidate("SELECT COUNT(*) FROM pm1.g1 GROUP BY (SELECT 1)", new String[] {"(SELECT 1)"}, getMetadataFactory().example1Cached()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testInvalidConstantInGroupBy() {
        helpValidate("SELECT COUNT(*) FROM pm1.g1 GROUP BY 1", new String[] {"1"}, getMetadataFactory().example1Cached()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testInvalidReferenceInGroupBy() {
        helpValidate("SELECT COUNT(*) FROM pm1.g1 GROUP BY ?", new String[] {"?"}, getMetadataFactory().example1Cached()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testValidateObjectType1() {
        helpValidate("SELECT DISTINCT * FROM test.group", new String[] {"test.\"group\".e2", "test.\"group\".e3", "test.\"group\".e4", "test.\"group\".e5"}, exampleMetadata2()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
    }

    @Test
    public void testValidateObjectType2() {
        helpValidate("SELECT * FROM test.group ORDER BY e1, e2", new String[] {"e2"}, exampleMetadata2()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testValidateObjectType3() {
        helpValidate("SELECT e2 AS x FROM test.group ORDER BY x", new String[] {"x"}, exampleMetadata2()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testValidateNonComparableType() {
        helpValidate("SELECT e3 FROM test.group ORDER BY e3", new String[] {"e3"}, exampleMetadata2()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testValidateNonComparableType1() {
        helpValidate("SELECT e3 FROM test.group union SELECT e3 FROM test.group", new String[] {"e3"}, exampleMetadata2()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testValidateNonComparableType2() {
        helpValidate("SELECT e3 FROM test.group GROUP BY e3", new String[] {"e3"}, exampleMetadata2()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testValidateNonComparableType3() {
        helpValidate("SELECT e3 FROM test.group intersect SELECT e3 FROM test.group", new String[] {"e3"}, exampleMetadata2()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testValidateNonComparableType4() {
        helpValidate("SELECT e3 FROM test.group except SELECT e3 FROM test.group", new String[] {"e3"}, exampleMetadata2()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testValidateIntersectAll() {
        helpValidate("SELECT e3 FROM pm1.g1 intersect all SELECT e3 FROM pm1.g1", new String[] {"SELECT e3 FROM pm1.g1 INTERSECT ALL SELECT e3 FROM pm1.g1"}, getMetadataFactory().example1Cached()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testValidateSetSelectInto() {
        helpValidate("SELECT e3 into #temp FROM pm1.g1 intersect all SELECT e3 FROM pm1.g1", new String[] {"SELECT e3 INTO #temp FROM pm1.g1 INTERSECT ALL SELECT e3 FROM pm1.g1"}, getMetadataFactory().example1Cached()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testInsert1() {
        helpValidate("INSERT INTO test.group (e0) VALUES (null)", new String[] {"e0"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    // non-null, no-default elements not left
    @Test
    public void testInsert4() throws Exception {
        IQueryMetadataInterface metadata = exampleMetadata1();

        Command command = getQueryParser().parseCommand("INSERT INTO test.group (e0) VALUES (2)"); //$NON-NLS-1$

        QueryResolver queryResolver = new QueryResolver(getTeiidVersion());
        queryResolver.resolveCommand(command, metadata);

        helpRunValidator(command, new String[] {}, metadata);
    }

    // non-null, no-default elements left
    @Test
    public void testInsert5() throws Exception {
        IQueryMetadataInterface metadata = exampleMetadata1();

        Command command = getQueryParser().parseCommand("INSERT INTO test.group (e1, e2) VALUES ('x', 'y')"); //$NON-NLS-1$
        QueryResolver queryResolver = new QueryResolver(getTeiidVersion());
        queryResolver.resolveCommand(command, metadata);

        helpRunValidator(command, new String[] {"test.\"group\".e0"}, metadata); //$NON-NLS-1$
    }

    @Test
    public void testValidateInsertElements1() throws Exception {
        IQueryMetadataInterface metadata = exampleMetadata();

        Command command = getQueryParser().parseCommand("INSERT INTO test.group2 (e0, e1, e2) VALUES (5, 'x', 'y')"); //$NON-NLS-1$
        QueryResolver queryResolver = new QueryResolver(getTeiidVersion());
        queryResolver.resolveCommand(command, metadata);

        helpRunValidator(command, new String[] {"e2", "e0"}, metadata); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testValidateInsertElements2() throws Exception {
        IQueryMetadataInterface metadata = exampleMetadata();

        Command command = getQueryParser().parseCommand("INSERT INTO test.group2 (e1) VALUES ('y')"); //$NON-NLS-1$

        QueryResolver queryResolver = new QueryResolver(getTeiidVersion());
        queryResolver.resolveCommand(command, metadata);

        helpRunValidator(command, new String[] {}, metadata);
    }

    @Test
    public void testValidateInsertElements3_autoIncNotRequired() throws Exception {
        helpValidate("INSERT INTO test.group (e0) VALUES (1)", new String[] {}, exampleMetadata3()); //$NON-NLS-1$
    }

    @Test
    public void testUpdate1() {
        helpValidate("UPDATE test.group SET e0=null", new String[] {"e0"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testUpdate2() {
        helpValidate("UPDATE test.group SET e0=1, e0=2", new String[] {"e0"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testValidateUpdateElements1() throws Exception {
        IQueryMetadataInterface metadata = exampleMetadata();

        Command command = getQueryParser().parseCommand("UPDATE test.group2 SET e0 = 5, e1 = 'x', e2 = 'y'"); //$NON-NLS-1$
        QueryResolver queryResolver = new QueryResolver(getTeiidVersion());
        queryResolver.resolveCommand(command, metadata);

        helpRunValidator(command, new String[] {"e2", "e0"}, metadata); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testValidateUpdateElements2() throws Exception {
        IQueryMetadataInterface metadata = exampleMetadata();

        Command command = getQueryParser().parseCommand("UPDATE test.group2 SET e1 = 'x'"); //$NON-NLS-1$
        QueryResolver queryResolver = new QueryResolver(getTeiidVersion());
        queryResolver.resolveCommand(command, metadata);

        helpRunValidator(command, new String[] {}, metadata);
    }

    @Test
    public void testXMLQuery1() {
        helpValidate("SELECT * FROM vm1.doc1", new String[] {}, exampleMetadata()); //$NON-NLS-1$
    }

    @Test
    public void testXMLQuery2() {
        helpValidate("SELECT * FROM vm1.doc1 where a2='x'", new String[] {}, exampleMetadata()); //$NON-NLS-1$
    }

    @Test
    public void testXMLQuery3() {
        helpValidate("SELECT * FROM vm1.doc1 order by a2", new String[] {}, exampleMetadata()); //$NON-NLS-1$
    }

    @Test
    public void testXMLQuery6() {
        helpValidate("SELECT * FROM vm1.doc1 UNION SELECT * FROM vm1.doc1", new String[] {"\"xml\"", "SELECT * FROM vm1.doc1 UNION SELECT * FROM vm1.doc1"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    @Test
    public void testXMLQueryWithLimit() {
        helpValidate("SELECT * FROM vm1.doc1 limit 1", new String[] {"SELECT * FROM vm1.doc1 LIMIT 1"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /** test rowlimit function is valid */
    @Test
    public void testXMLQueryRowLimit() {
        helpValidate("SELECT * FROM vm1.doc1 where 2 = RowLimit(a2)", new String[] {}, exampleMetadata()); //$NON-NLS-1$ 
    }

    /** rowlimit function operand must be nonnegative integer */
    @Test
    public void testXMLQueryRowLimit1() {
        helpValidate("SELECT * FROM vm1.doc1 where RowLimit(a2)=-1", new String[] {"RowLimit(a2) = -1"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /** rowlimit function operand must be nonnegative integer */
    @Test
    public void testXMLQueryRowLimit2() {
        helpValidate("SELECT * FROM vm1.doc1 where RowLimit(a2)='x'", new String[] {"RowLimit(a2) = 'x'"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /** rowlimit function cannot be nested within another function (this test inserts an implicit type conversion) */
    @Test
    public void testXMLQueryRowLimitNested() {
        helpValidate("SELECT * FROM vm1.doc1 where RowLimit(a2)=a2", new String[] {"RowLimit(a2) = a2"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /** rowlimit function cannot be nested within another function */
    @Test
    public void testXMLQueryRowLimitNested2() {
        helpValidate("SELECT * FROM vm1.doc1 where convert(RowLimit(a2), string)=a2", new String[] {"convert(RowLimit(a2), string) = a2"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /** rowlimit function operand must be nonnegative integer */
    @Test
    public void testXMLQueryRowLimit3a() {
        helpValidate("SELECT * FROM vm1.doc1 where RowLimit(a2) = convert(a2, integer)", new String[] {"RowLimit(a2) = convert(a2, integer)"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /** rowlimit function operand must be nonnegative integer */
    @Test
    public void testXMLQueryRowLimit3b() {
        helpValidate("SELECT * FROM vm1.doc1 where convert(a2, integer) = RowLimit(a2)", new String[] {"convert(a2, integer) = RowLimit(a2)"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /** rowlimit function arg must be an element symbol */
    @Test
    public void testXMLQueryRowLimit4() {
        helpValidate("SELECT * FROM vm1.doc1 where rowlimit('x') = 3", new String[] {"rowlimit('x')"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /** rowlimit function arg must be an element symbol */
    @Test
    public void testXMLQueryRowLimit5() {
        helpValidate("SELECT * FROM vm1.doc1 where rowlimit(concat(a2, 'x')) = 3", new String[] {"rowlimit(concat(a2, 'x'))"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /** rowlimit function arg must be a single conjunct */
    @Test
    public void testXMLQueryRowLimitConjunct() {
        helpValidate("SELECT * FROM vm1.doc1 where rowlimit(a2) = 3 OR a2 = 'x'", new String[] {"(rowlimit(a2) = 3) OR (a2 = 'x')"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /** rowlimit function arg must be a single conjunct */
    @Test
    public void testXMLQueryRowLimitCompound() {
        helpValidate("SELECT * FROM vm1.doc1 where rowlimit(a2) = 3 AND a2 = 'x'", new String[] {}, exampleMetadata()); //$NON-NLS-1$ 
    }

    /** rowlimit function arg must be a single conjunct */
    @Test
    public void testXMLQueryRowLimitCompound2() {
        helpValidate("SELECT * FROM vm1.doc1 where rowlimit(a2) = 3 AND concat(a2, 'y') = 'xy'", new String[] {}, exampleMetadata()); //$NON-NLS-1$ 
    }

    /** rowlimit function arg must be a single conjunct */
    @Test
    public void testXMLQueryRowLimitCompound3() {
        helpValidate("SELECT * FROM vm1.doc1 where rowlimit(a2) = 3 AND (concat(a2, 'y') = 'xy' OR concat(a2, 'y') = 'zy')", new String[] {}, exampleMetadata()); //$NON-NLS-1$ 
    }

    /** each rowlimit function arg must be a single conjunct */
    @Test
    public void testXMLQueryRowLimitCompound4() {
        helpValidate("SELECT * FROM vm1.doc1 where rowlimit(a2) = 3 AND rowlimit(c2) = 4", new String[] {}, exampleMetadata()); //$NON-NLS-1$ 
    }

    /** 
     * It doesn't make sense to use rowlimit twice on same element, but can't be
     * invalidated here (could be two different elements but in the same 
     * mapping class - needs to be caught in XMLPlanner)
     */
    @Test
    public void testXMLQueryRowLimitCompound5() {
        helpValidate("SELECT * FROM vm1.doc1 where rowlimit(a2) = 3 AND rowlimit(a2) = 4", new String[] {}, exampleMetadata()); //$NON-NLS-1$ 
    }

    @Test
    public void testXMLQueryRowLimitInvalidCriteria() {
        helpValidate("SELECT * FROM vm1.doc1 where not(rowlimit(a2) = 3)", new String[] {"NOT (rowlimit(a2) = 3)"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testXMLQueryRowLimitInvalidCriteria2() {
        helpValidate("SELECT * FROM vm1.doc1 where rowlimit(a2) IN (3)", new String[] {"rowlimit(a2) IN (3)"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testXMLQueryRowLimitInvalidCriteria3() {
        helpValidate("SELECT * FROM vm1.doc1 where rowlimit(a2) LIKE 'x'", new String[] {"rowlimit(a2) LIKE 'x'"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testXMLQueryRowLimitInvalidCriteria4() {
        helpValidate("SELECT * FROM vm1.doc1 where rowlimit(a2) IS NULL", new String[] {"rowlimit(a2) IS NULL"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testXMLQueryRowLimitInvalidCriteria5() {
        helpValidate("SELECT * FROM vm1.doc1 where rowlimit(a2) IN (SELECT e0 FROM vTest.vMap)", new String[] {"rowlimit(a2) IN (SELECT e0 FROM vTest.vMap)"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testXMLQueryRowLimitInvalidCriteria6() {
        helpValidate("SELECT * FROM vm1.doc1 where 2 = CASE WHEN rowlimit(a2) = 2 THEN 2 END", new String[] {"2 = CASE WHEN rowlimit(a2) = 2 THEN 2 END"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testXMLQueryRowLimitInvalidCriteria6a() {
        helpValidate("SELECT * FROM vm1.doc1 where 2 = CASE rowlimit(a2) WHEN 2 THEN 2 END", new String[] {"2 = CASE rowlimit(a2) WHEN 2 THEN 2 END"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testXMLQueryRowLimitInvalidCriteria7() {
        helpValidate("SELECT * FROM vm1.doc1 where rowlimit(a2) BETWEEN 2 AND 3", new String[] {"rowlimit(a2) BETWEEN 2 AND 3"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testXMLQueryRowLimitInvalidCriteria8() {
        helpValidate("SELECT * FROM vm1.doc1 where rowlimit(a2) = ANY (SELECT e0 FROM vTest.vMap)", new String[] {"rowlimit(a2) = ANY (SELECT e0 FROM vTest.vMap)"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /** using rowlimit pseudo-function in non-XML query is invalid */
    @Test
    public void testNonXMLQueryRowLimit() {
        helpValidate("SELECT e2 FROM vTest.vMap WHERE rowlimit(e1) = 2", new String[] {"rowlimit(e1)"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /** test rowlimitexception function is valid */
    @Test
    public void testXMLQueryRowLimitException() {
        helpValidate("SELECT * FROM vm1.doc1 where 2 = RowLimitException(a2)", new String[] {}, exampleMetadata()); //$NON-NLS-1$ 
    }

    /** rowlimitexception function operand must be nonnegative integer */
    @Test
    public void testXMLQueryRowLimitException1() {
        helpValidate("SELECT * FROM vm1.doc1 where RowLimitException(a2)=-1", new String[] {"RowLimitException(a2) = -1"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /** rowlimitexception function operand must be nonnegative integer */
    @Test
    public void testXMLQueryRowLimitException2() {
        helpValidate("SELECT * FROM vm1.doc1 where RowLimitException(a2)='x'", new String[] {"RowLimitException(a2) = 'x'"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /** rowlimitexception function cannot be nested within another function (this test inserts an implicit type conversion) */
    @Test
    public void testXMLQueryRowLimitExceptionNested() {
        helpValidate("SELECT * FROM vm1.doc1 where RowLimitException(a2)=a2", new String[] {"RowLimitException(a2) = a2"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /** rowlimitexception function cannot be nested within another function */
    @Test
    public void testXMLQueryRowLimitExceptionNested2() {
        helpValidate("SELECT * FROM vm1.doc1 where convert(RowLimitException(a2), string)=a2", new String[] {"convert(RowLimitException(a2), string) = a2"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /** rowlimitexception function operand must be nonnegative integer */
    @Test
    public void testXMLQueryRowLimitException3a() {
        helpValidate("SELECT * FROM vm1.doc1 where RowLimitException(a2) = convert(a2, integer)", new String[] {"RowLimitException(a2) = convert(a2, integer)"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /** rowlimitexception function operand must be nonnegative integer */
    @Test
    public void testXMLQueryRowLimitException3b() {
        helpValidate("SELECT * FROM vm1.doc1 where convert(a2, integer) = RowLimitException(a2)", new String[] {"convert(a2, integer) = RowLimitException(a2)"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /** rowlimitexception function arg must be an element symbol */
    @Test
    public void testXMLQueryRowLimitException4() {
        helpValidate("SELECT * FROM vm1.doc1 where rowlimitexception('x') = 3", new String[] {"rowlimitexception('x')"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /** rowlimitexception function arg must be an element symbol */
    @Test
    public void testXMLQueryRowLimitException5() {
        helpValidate("SELECT * FROM vm1.doc1 where rowlimitexception(concat(a2, 'x')) = 3", new String[] {"rowlimitexception(concat(a2, 'x'))"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /** rowlimitexception function arg must be a single conjunct */
    @Test
    public void testXMLQueryRowLimitExceptionConjunct() {
        helpValidate("SELECT * FROM vm1.doc1 where rowlimitexception(a2) = 3 OR a2 = 'x'", new String[] {"(rowlimitexception(a2) = 3) OR (a2 = 'x')"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /** rowlimitexception function arg must be a single conjunct */
    @Test
    public void testXMLQueryRowLimitExceptionCompound() {
        helpValidate("SELECT * FROM vm1.doc1 where rowlimitexception(a2) = 3 AND a2 = 'x'", new String[] {}, exampleMetadata()); //$NON-NLS-1$ 
    }

    /** rowlimitexception function arg must be a single conjunct */
    @Test
    public void testXMLQueryRowLimitExceptionCompound2() {
        helpValidate("SELECT * FROM vm1.doc1 where rowlimitexception(a2) = 3 AND concat(a2, 'y') = 'xy'", new String[] {}, exampleMetadata()); //$NON-NLS-1$ 
    }

    /** rowlimitexception function arg must be a single conjunct */
    @Test
    public void testXMLQueryRowLimitExceptionCompound3() {
        helpValidate("SELECT * FROM vm1.doc1 where rowlimitexception(a2) = 3 AND (concat(a2, 'y') = 'xy' OR concat(a2, 'y') = 'zy')", new String[] {}, exampleMetadata()); //$NON-NLS-1$ 
    }

    /** each rowlimitexception function arg must be a single conjunct */
    @Test
    public void testXMLQueryRowLimitExceptionCompound4() {
        helpValidate("SELECT * FROM vm1.doc1 where rowlimitexception(a2) = 3 AND rowlimitexception(c2) = 4", new String[] {}, exampleMetadata()); //$NON-NLS-1$ 
    }

    /** 
     * It doesn't make sense to use rowlimitexception twice on same element, but can't be
     * invalidated here (could be two different elements but in the same 
     * mapping class - needs to be caught in XMLPlanner)
     */
    @Test
    public void testXMLQueryRowLimitExceptionCompound5() {
        helpValidate("SELECT * FROM vm1.doc1 where rowlimitexception(a2) = 3 AND rowlimitexception(a2) = 4", new String[] {}, exampleMetadata()); //$NON-NLS-1$ 
    }

    @Test
    public void testXMLQueryRowLimitExceptionInvalidCriteria() {
        helpValidate("SELECT * FROM vm1.doc1 where not(rowlimitexception(a2) = 3)", new String[] {"NOT (rowlimitexception(a2) = 3)"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testXMLQueryRowLimitExceptionInvalidCriteria2() {
        helpValidate("SELECT * FROM vm1.doc1 where rowlimitexception(a2) IN (3)", new String[] {"rowlimitexception(a2) IN (3)"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testXMLQueryRowLimitExceptionInvalidCriteria3() {
        helpValidate("SELECT * FROM vm1.doc1 where rowlimitexception(a2) LIKE 'x'", new String[] {"rowlimitexception(a2) LIKE 'x'"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testXMLQueryRowLimitExceptionInvalidCriteria4() {
        helpValidate("SELECT * FROM vm1.doc1 where rowlimitexception(a2) IS NULL", new String[] {"rowlimitexception(a2) IS NULL"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testXMLQueryRowLimitExceptionInvalidCriteria5() {
        helpValidate("SELECT * FROM vm1.doc1 where rowlimitexception(a2) IN (SELECT e0 FROM vTest.vMap)", new String[] {"rowlimitexception(a2) IN (SELECT e0 FROM vTest.vMap)"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testXMLQueryRowLimitExceptionInvalidCriteria6() {
        helpValidate("SELECT * FROM vm1.doc1 where 2 = CASE WHEN rowlimitexception(a2) = 2 THEN 2 END", new String[] {"2 = CASE WHEN rowlimitexception(a2) = 2 THEN 2 END"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testXMLQueryRowLimitExceptionInvalidCriteria6a() {
        helpValidate("SELECT * FROM vm1.doc1 where 2 = CASE rowlimitexception(a2) WHEN 2 THEN 2 END", new String[] {"2 = CASE rowlimitexception(a2) WHEN 2 THEN 2 END"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testXMLQueryRowLimitExceptionInvalidCriteria7() {
        helpValidate("SELECT * FROM vm1.doc1 where rowlimitexception(a2) BETWEEN 2 AND 3", new String[] {"rowlimitexception(a2) BETWEEN 2 AND 3"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testXMLQueryRowLimitExceptionInvalidCriteria8() {
        helpValidate("SELECT * FROM vm1.doc1 where rowlimitexception(a2) = ANY (SELECT e0 FROM vTest.vMap)", new String[] {"rowlimitexception(a2) = ANY (SELECT e0 FROM vTest.vMap)"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /** using rowlimit pseudo-function in non-XML query is invalid */
    @Test
    public void testNonXMLQueryRowLimitException() {
        helpValidate("SELECT e2 FROM vTest.vMap WHERE rowlimitexception(e1) = 2", new String[] {"rowlimitexception(e1)"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /** using context pseudo-function in non-XML query is invalid */
    @Test
    public void testNonXMLQueryContextOperator() {
        helpValidate("SELECT e2 FROM vTest.vMap WHERE context(e1, e1) = 2", new String[] {"context(e1, e1)"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testValidateSubquery1() {
        helpValidate("SELECT e2 FROM (SELECT e2 FROM vTest.vMap WHERE e2 = 'a') AS x", new String[] {}, exampleMetadata()); //$NON-NLS-1$ 
    }

    @Test
    public void testValidateSubquery2() {
        helpValidate("SELECT e2 FROM (SELECT e3 FROM vTest.vMap) AS x, vTest.vMap WHERE e2 = 'a'", new String[] {}, exampleMetadata()); //$NON-NLS-1$ 
    }

    @Test
    public void testValidateSubquery3() {
        helpValidate("SELECT * FROM pm1.g1, (EXEC pm1.sq1( )) AS alias", new String[] {}, getMetadataFactory().example1Cached()); //$NON-NLS-1$
    }

    @Test
    public void testValidateUnionWithSubquery() {
        helpValidate("SELECT e2 FROM test.group2 union all SELECT e3 FROM test.group union all select * from (SELECT e1 FROM test.group) as subquery1", new String[] {"e1"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testValidateExistsSubquery() {
        helpValidate("SELECT e2 FROM test.group2 WHERE EXISTS (SELECT e2 FROM vTest.vMap WHERE e2 = 'a')", new String[] {}, exampleMetadata()); //$NON-NLS-1$ 
    }

    @Test
    public void testValidateScalarSubquery() {
        helpValidate("SELECT e2, (SELECT e1 FROM vTest.vMap WHERE e2 = '3') FROM test.group2", new String[] {"e1"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$ 
    }

    @Test
    public void testValidateAnyCompareSubquery() {
        helpValidate("SELECT e2 FROM test.group2 WHERE e1 < ANY (SELECT e1 FROM test.group)", new String[] {"e1"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testValidateAllCompareSubquery() {
        helpValidate("SELECT e2 FROM test.group2 WHERE e1 = ALL (SELECT e1 FROM test.group)", new String[] {"e1"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testValidateSomeCompareSubquery() {
        helpValidate("SELECT e2 FROM test.group2 WHERE e1 <= SOME (SELECT e1 FROM test.group)", new String[] {"e1"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testValidateCompareSubquery() {
        helpValidate("SELECT e2 FROM test.group2 WHERE e1 >= (SELECT e1 FROM test.group WHERE e1 = 1)", new String[] {"e1"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testValidateInClauseSubquery() {
        helpValidate("SELECT e2 FROM test.group2 WHERE e1 IN (SELECT e1 FROM test.group)", new String[] {"e1"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testValidateExec1() {
        helpValidate("EXEC pm1.sq1()", new String[] {}, getMetadataFactory().example1Cached()); //$NON-NLS-1$
    }

    @Test
    public void testValidHaving() {
        helpValidate("SELECT intnum " + //$NON-NLS-1$
                     "FROM bqt1.smalla " + //$NON-NLS-1$
                     "GROUP BY intnum " + //$NON-NLS-1$
                     "HAVING SUM(floatnum) > 1", //$NON-NLS-1$
                     new String[] {},
                     getMetadataFactory().exampleBQTCached());
    }

    @Test
    public void testValidHaving2() {
        String sql = "SELECT intkey FROM bqt1.smalla WHERE intkey = 1 " + //$NON-NLS-1$
                     "GROUP BY intkey HAVING intkey = 1"; //$NON-NLS-1$
        helpValidate(sql, new String[] {}, getMetadataFactory().exampleBQTCached());
    }

    @Test
    public void testVirtualProcedure() {
        helpValidate("EXEC pm1.vsp1()", new String[] {}, getMetadataFactory().example1Cached()); //$NON-NLS-1$
    }

    @Test
    public void testSelectWithNoFrom() {
        helpValidate("SELECT 5", new String[] {}, exampleMetadata()); //$NON-NLS-1$
    }

    @Test
    public void testSelectIntoPhysicalGroupNotUpdateable_Defect16857() {
        helpValidate("SELECT e0, e1, e2 INTO test.group3 FROM test.group2", new String[] {"test.group3"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testSelectIntoElementsNotUpdateable() {
        helpValidate("SELECT e0, e1, e2 INTO test.group2 FROM test.group3", new String[] {"test.group2"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testSelectIntoWithStar() {
        helpResolve("SELECT * INTO pm1.g1 FROM pm1.g2", getMetadataFactory().example1Cached()); //$NON-NLS-1$
    }

    @Test
    public void testVirtualProcedure2() {
        helpValidate("EXEC pm1.vsp13()", new String[] {}, getMetadataFactory().example1Cached()); //$NON-NLS-1$
    }

    //procedure that has another procedure in the transformation
    @Test
    public void testVirtualProcedure3() {
        helpValidate("EXEC pm1.vsp27()", new String[] {}, getMetadataFactory().example1Cached()); //$NON-NLS-1$
    }

    @Test
    public void testNonEmbeddedSubcommand_defect11000() {
        helpValidate("SELECT e0 FROM vTest.vGroup", new String[0], exampleMetadata()); //$NON-NLS-1$ 
    }

    @Test
    public void testValidateObjectInComparison() throws Exception {
        String sql = "SELECT IntKey FROM BQT1.SmallA WHERE ObjectValue = 5"; //$NON-NLS-1$
        ValidatorReport report = helpValidate(sql, new String[] {"ObjectValue = 5"}, getMetadataFactory().exampleBQTCached()); //$NON-NLS-1$
        assertEquals("Expressions of type OBJECT, CLOB, BLOB, or XML cannot be used in comparison: ObjectValue = 5.", report.toString()); //$NON-NLS-1$
    }

    @Test
    public void testValidateAssignmentWithFunctionOnParameter_InServer() throws Exception {
        String sql = "EXEC pm1.vsp36(5)"; //$NON-NLS-1$
        IQueryMetadataInterface metadata = getMetadataFactory().example1Cached();

        Command command = getQueryParser().parseCommand(sql);
        QueryResolver queryResolver = new QueryResolver(getTeiidVersion());
        queryResolver.resolveCommand(command, metadata);

        // Validate
        ValidatorReport report = new Validator().validate(command, metadata);
        assertEquals(0, report.getItems().size());
    }

    @Test
    public void testDefect9917() throws Exception {
        IQueryMetadataInterface metadata = getMetadataFactory().example1Cached();
        String sql = "SELECT lookup('pm1.g1', 'e1a', 'e2', e2) AS x, lookup('pm1.g1', 'e4', 'e3', e3) AS y FROM pm1.g1"; //$NON-NLS-1$
        Command command = getQueryParser().parseCommand(sql);
        try {
            QueryResolver queryResolver = new QueryResolver(getTeiidVersion());
        queryResolver.resolveCommand(command, metadata);
            fail("Did not get exception"); //$NON-NLS-1$
        } catch (QueryResolverException e) {
            //expected
        }

        sql = "SELECT lookup('pm1.g1a', 'e1', 'e2', e2) AS x, lookup('pm1.g1', 'e4', 'e3', e3) AS y FROM pm1.g1"; //$NON-NLS-1$
        command = getQueryParser().parseCommand(sql);
        try {
            QueryResolver queryResolver = new QueryResolver(getTeiidVersion());
        queryResolver.resolveCommand(command, metadata);
            fail("Did not get exception"); //$NON-NLS-1$
        } catch (QueryResolverException e) {
            //expected
        }
    }

    @Test
    public void testLookupKeyElementComparable() throws Exception {
        IQueryMetadataInterface metadata = exampleMetadata2();
        String sql = "SELECT lookup('test.group', 'e2', 'e3', convert(e2, blob)) AS x FROM test.group"; //$NON-NLS-1$
        Command command = getQueryParser().parseCommand(sql);
        QueryResolver queryResolver = new QueryResolver(getTeiidVersion());
        queryResolver.resolveCommand(command, metadata);

        ValidatorReport report = new Validator().validate(command, metadata);
        assertEquals("Expressions of type OBJECT, CLOB, BLOB, or XML cannot be used as LOOKUP key columns: test.\"group\".e3.", report.toString()); //$NON-NLS-1$
    }

    @Test
    public void testDefect12107() throws Exception {
        IQueryMetadataInterface metadata = getMetadataFactory().example1Cached();
        String sql = "SELECT SUM(DISTINCT lookup('pm1.g1', 'e2', 'e2', e2)) FROM pm1.g1"; //$NON-NLS-1$
        Command command = helpResolve(sql, metadata);
        sql = "SELECT SUM(DISTINCT lookup('pm1.g1', 'e3', 'e2', e2)) FROM pm1.g1"; //$NON-NLS-1$
        command = helpResolve(sql, metadata);
        ValidatorReport report = new Validator().validate(command, metadata);
        assertEquals("The aggregate function SUM cannot be used with non-numeric expressions: SUM(DISTINCT lookup('pm1.g1', 'e3', 'e2', e2))", report.toString()); //$NON-NLS-1$
    }

    private ValidatorReport helpValidateInModeler(String procName, String procSql, IQueryMetadataInterface metadata)
        throws Exception {
        Command command = getQueryParser().parseCommand(procSql);

        GroupSymbol group = getFactory().newGroupSymbol(procName);
        QueryResolver queryResolver = new QueryResolver(getTeiidVersion());
        queryResolver.resolveCommand(command, group, ICommand.TYPE_STORED_PROCEDURE, metadata, true);

        // Validate
        return new Validator().validate(command, metadata);
    }

    @Test
    public void testValidateDynamicCommandWithNonTempGroup_InModeler() throws Exception {
        // SQL is same as pm1.vsp36() in example1 
        String sql = "CREATE VIRTUAL PROCEDURE BEGIN execute string 'select ' || '1' as X integer into pm1.g3; END"; //$NON-NLS-1$        
        IQueryMetadataInterface metadata = getMetadataFactory().example1Cached();

        // Validate
        ValidatorReport report = helpValidateInModeler("pm1.vsp36", sql, metadata); //$NON-NLS-1$
        assertEquals(1, report.getItems().size());
        assertEquals("Wrong number of elements being SELECTed INTO the target table. Expected 4 elements, but was 1.", report.toString()); //$NON-NLS-1$
    }

    @Test
    public void testDynamicDupUsing() throws Exception {
        String sql = "CREATE VIRTUAL PROCEDURE BEGIN execute string 'select ' || '1' as X integer into #temp using id=1, id=2; END"; //$NON-NLS-1$        
        IQueryMetadataInterface metadata = getMetadataFactory().example1Cached();

        // Validate
        ValidatorReport report = helpValidateInModeler("pm1.vsp36", sql, metadata); //$NON-NLS-1$
        assertEquals(1, report.getItems().size());
        assertEquals("Elements cannot appear more than once in a SET or USING clause.  The following elements are duplicated: [DVARS.id]", report.toString()); //$NON-NLS-1$
    }

    @Test
    public void testValidateAssignmentWithFunctionOnParameter_InModeler() throws Exception {
        // SQL is same as pm1.vsp36() in example1 
        String sql = "CREATE VIRTUAL PROCEDURE BEGIN DECLARE integer x; x = pm1.vsp36.param1 * 2; SELECT x; END"; //$NON-NLS-1$        
        IQueryMetadataInterface metadata = getMetadataFactory().example1Cached();

        // Validate
        ValidatorReport report = helpValidateInModeler("pm1.vsp36", sql, metadata); //$NON-NLS-1$
        assertEquals(0, report.getItems().size());
    }

    @Test
    public void testDefect12533() {
        String sql = "SELECT BQT1.SmallA.DateValue, BQT2.SmallB.ObjectValue FROM BQT1.SmallA, BQT2.SmallB " + //$NON-NLS-1$
                     "WHERE BQT1.SmallA.DateValue = BQT2.SmallB.DateValue AND BQT1.SmallA.ObjectValue = BQT2.SmallB.ObjectValue "
                     + //$NON-NLS-1$
                     "AND BQT1.SmallA.IntKey < 30 AND BQT2.SmallB.IntKey < 30 ORDER BY BQT1.SmallA.DateValue"; //$NON-NLS-1$
        IQueryMetadataInterface metadata = getMetadataFactory().exampleBQTCached();

        // Validate
        helpValidate(sql, new String[] {"BQT1.SmallA.ObjectValue = BQT2.SmallB.ObjectValue"}, metadata); //$NON-NLS-1$ 
    }

    @Test
    public void testDefect16772() throws Exception {
        String sql = "CREATE VIRTUAL PROCEDURE BEGIN IF (pm1.vsp42.param1 > 0) SELECT 1 AS x; ELSE SELECT 0 AS x; END"; //$NON-NLS-1$

        IQueryMetadataInterface metadata = getMetadataFactory().example1Cached();

        // Validate
        ValidatorReport report = helpValidateInModeler("pm1.vsp42", sql, metadata); //$NON-NLS-1$ 
        assertEquals("Expected report to have no validation failures", false, report.hasItems()); //$NON-NLS-1$
    }

    @Test
    public void testDupLabel() throws Exception {
        String sql = "CREATE VIRTUAL PROCEDURE BEGIN IF (pm1.vsp42.param1 > 0) x : begin SELECT 1 AS x; x: begin atomic select 2 as x; end end END"; //$NON-NLS-1$

        IQueryMetadataInterface metadata = getMetadataFactory().example1Cached();

        // Validate
        ValidatorReport report = helpValidateInModeler("pm1.vsp42", sql, metadata); //$NON-NLS-1$ 
        examineReport(sql, new String[] {"x : BEGIN ATOMIC\nSELECT 2 AS x;\nEND"}, report);
    }

    @Test
    public void testInvalidContinue() throws Exception {
        String sql = "CREATE VIRTUAL PROCEDURE BEGIN continue; END"; //$NON-NLS-1$

        IQueryMetadataInterface metadata = getMetadataFactory().example1Cached();

        // Validate
        ValidatorReport report = helpValidateInModeler("pm1.vsp42", sql, metadata); //$NON-NLS-1$ 
        examineReport(sql, new String[] {"CONTINUE;"}, report);
    }

    @Test
    public void testInvalidLabel() throws Exception {
        String sql = "CREATE VIRTUAL PROCEDURE BEGIN IF (pm1.vsp42.param1 > 0) x : begin continue y; end END"; //$NON-NLS-1$

        IQueryMetadataInterface metadata = getMetadataFactory().example1Cached();

        // Validate
        ValidatorReport report = helpValidateInModeler("pm1.vsp42", sql, metadata); //$NON-NLS-1$ 
        examineReport(sql, new String[] {"CONTINUE y;"}, report);
    }

    @Test
    public void testNonQueryAgg() throws Exception {
        String sql = "CREATE VIRTUAL PROCEDURE BEGIN IF (max(pm1.vsp42.param1) > 0) SELECT 1 AS x; ELSE SELECT 0 AS x; END"; //$NON-NLS-1$

        IQueryMetadataInterface metadata = getMetadataFactory().example1Cached();

        // Validate
        ValidatorReport report = helpValidateInModeler("pm1.vsp42", sql, metadata); //$NON-NLS-1$ 
        examineReport(sql, new String[] {"MAX(pm1.vsp42.param1)"}, report);
    }

    @Test
    public void testDefect14886() throws Exception {
        String sql = "CREATE VIRTUAL PROCEDURE BEGIN END"; //$NON-NLS-1$        
        IQueryMetadataInterface metadata = getMetadataFactory().example1Cached();

        Command command = getQueryParser().parseCommand(sql);
        QueryResolver queryResolver = new QueryResolver(getTeiidVersion());
        queryResolver.resolveCommand(command, metadata);

        // Validate
        ValidatorReport report = new Validator().validate(command, metadata);
        // Validate
        assertEquals(0, report.getItems().size());
    }

    @Test
    public void testDefect21389() throws Exception {
        String sql = "CREATE VIRTUAL PROCEDURE BEGIN SELECT * INTO #temptable FROM pm1.g1; INSERT INTO #temptable (e1) VALUES ('a'); END"; //$NON-NLS-1$      
        TransformationMetadata metadata = getMetadataFactory().example1();
        Column c = metadata.getElementID("pm1.g1.e1"); //$NON-NLS-1$
        c.setUpdatable(false);

        Command command = getQueryParser().parseCommand(sql);
        QueryResolver queryResolver = new QueryResolver(getTeiidVersion());
        queryResolver.resolveCommand(command, metadata);

        // Validate
        ValidatorReport report = new Validator().validate(command, metadata);
        // Validate
        assertEquals(0, report.getItems().size());
    }

    @Test
    public void testMakeNotDep() {
        helpValidate("select group2.e1, group3.e2 from group2, group3 WHERE group2.e0 = group3.e0 OPTION MAKENOTDEP group2, group3", new String[0], exampleMetadata()); //$NON-NLS-1$
    }

    @Test
    public void testInvalidMakeNotDep() {
        helpValidate("select group2.e1, group3.e2 from group2, group3 WHERE group2.e0 = group3.e0 OPTION MAKEDEP group2 MAKENOTDEP group2, group3", new String[] {"OPTION MAKEDEP group2 MAKENOTDEP group2, group3"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Test case 4237.  This test simulates the way the modeler transformation 
     * panel uses the query resolver and validator to validate a transformation for
     * a virtual procedure.  The modeler has to supply external metadata for the 
     * virtual procedure group and parameter names (simulated in this test).
     * 
     * This virtual procedure calls a physical stored procedure directly.  
     */
    @Test
    public void testCase4237() throws Exception {

        IQueryMetadataInterface metadata = helpCreateCase4237VirtualProcedureMetadata();

        String sql = "CREATE VIRTUAL PROCEDURE BEGIN EXEC pm1.sp(vm1.sp.in1); END"; //$NON-NLS-1$ 
        Command command = helpResolve(sql, getFactory().newGroupSymbol("vm1.sp"), ICommand.TYPE_STORED_PROCEDURE, metadata);
        helpRunValidator(command, new String[0], metadata);
    }

    /**
     * This test was already working before the case was logged, due for some reason
     * to the exec() statement being inside an inline view.  This is a control test. 
     */
    @Test
    public void testCase4237InlineView() throws Exception {

        IQueryMetadataInterface metadata = helpCreateCase4237VirtualProcedureMetadata();

        String sql = "CREATE VIRTUAL PROCEDURE BEGIN SELECT * FROM (EXEC pm1.sp(vm1.sp.in1)) AS FOO; END"; //$NON-NLS-1$ 
        Command command = helpResolve(sql, getFactory().newGroupSymbol("vm1.sp"), ICommand.TYPE_STORED_PROCEDURE, metadata);
        helpRunValidator(command, new String[0], metadata);
    }

    /**
     * Create fake metadata for this case.  Need a physical stored procedure and
     * a virtual stored procedure which calls the physical one. 
     * @return
     */
    private TransformationMetadata helpCreateCase4237VirtualProcedureMetadata() {
        MetadataStore metadataStore = new MetadataStore();
        Schema physicalModel = getMetadataFactory().createPhysicalModel("pm1", metadataStore); //$NON-NLS-1$
        ColumnSet<Procedure> resultSet = getMetadataFactory().createResultSet("pm1.rs", new String[] {"e1", "e2"}, new String[] {DataTypeManagerService.DefaultDataTypes.STRING.getId(), DataTypeManagerService.DefaultDataTypes.INTEGER.getId()}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        ProcedureParameter inParam = getMetadataFactory().createParameter("in", ISPParameter.ParameterInfo.IN, DataTypeManagerService.DefaultDataTypes.STRING.getId()); //$NON-NLS-1$
        Procedure storedProcedure = getMetadataFactory().createStoredProcedure("sp", physicalModel, Arrays.asList(inParam)); //$NON-NLS-1$ //$NON-NLS-2$
        storedProcedure.setResultSet(resultSet);

        Schema virtualModel = getMetadataFactory().createVirtualModel("vm1", metadataStore); //$NON-NLS-1$
        ColumnSet<Procedure> virtualResultSet = getMetadataFactory().createResultSet("vm1.rs", new String[] {"e1", "e2"}, new String[] {DataTypeManagerService.DefaultDataTypes.STRING.getId(), DataTypeManagerService.DefaultDataTypes.INTEGER.getId()}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        ProcedureParameter virtualInParam = getMetadataFactory().createParameter("in1", ISPParameter.ParameterInfo.IN, DataTypeManagerService.DefaultDataTypes.STRING.getId()); //$NON-NLS-1$
        QueryNode queryNode = new QueryNode("CREATE VIRTUAL PROCEDURE BEGIN EXEC pm1.sp(vm1.sp.in1); END"); //$NON-NLS-1$ //$NON-NLS-2$
        Procedure virtualStoredProcedure = getMetadataFactory().createVirtualProcedure("sp", virtualModel, Arrays.asList(virtualInParam), queryNode); //$NON-NLS-1$
        virtualStoredProcedure.setResultSet(virtualResultSet);

        return getMetadataFactory().createTransformationMetadata(metadataStore, "case4237");
    }

    @Test
    public void testSelectIntoWithNull() {
        helpValidate("SELECT null, null, null, null INTO pm1.g1 FROM pm1.g2", new String[] {}, getMetadataFactory().example1Cached()); //$NON-NLS-1$
    }

    @Test
    public void testCreateWithNonSortablePrimaryKey() {
        IQueryMetadataInterface metadata = getMetadataFactory().example1Cached();
        Command command = helpResolve("create local temporary table x (column1 string, column2 clob, primary key (column2))", metadata); //$NON-NLS-1$
        helpRunValidator(command, new String[] {"column2"}, getMetadataFactory().example1Cached());
    }

    @Test
    public void testDropNonTemporary() {
        IQueryMetadataInterface metadata = getMetadataFactory().example1Cached();
        Command command = helpResolve("drop table pm1.g1", metadata); //$NON-NLS-1$
        helpRunValidator(command, new String[] {command.toString()}, getMetadataFactory().example1Cached());
    }

    @Test
    public void testNestedContexts() {
        helpValidate("SELECT * FROM vm1.doc1 where context(a0, context(a0, a2))='x'", new String[] {"context(a0, context(a0, a2))"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testValidContextElement() {
        helpValidate("SELECT * FROM vm1.doc1 where context(1, a2)='x'", new String[] {"context(1, a2)"}, exampleMetadata()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testInsertIntoVirtualWithQuery() throws Exception {
        IQueryMetadataInterface metadata = getMetadataFactory().example1Cached();
        Command command = helpResolve("insert into vm1.g1 select 1, 2, true, 3", metadata); //$NON-NLS-1$
        ValidatorReport report = new Validator().validate(command, metadata);
        assertTrue(report.getItems().isEmpty());
    }

    @Test
    public void testDynamicIntoDeclaredTemp() throws Exception {
        StringBuffer procedure = new StringBuffer("CREATE VIRTUAL PROCEDURE  ") //$NON-NLS-1$
        .append("BEGIN\n") //$NON-NLS-1$
        .append("CREATE LOCAL TEMPORARY TABLE x (column1 string);") //$NON-NLS-1$
        .append("execute string 'SELECT e1 FROM pm1.g2' as e1 string INTO x;\n") //$NON-NLS-1$
        .append("select cast(column1 as integer) from x;\n") //$NON-NLS-1$
        .append("END\n"); //$NON-NLS-1$

        IQueryMetadataInterface metadata = getMetadataFactory().example1Cached();

        // Validate
        ValidatorReport report = helpValidateInModeler("pm1.vsp36", procedure.toString(), metadata); //$NON-NLS-1$
        assertEquals(report.toString(), 0, report.getItems().size());
    }

    @Test
    public void testVariablesGroupSelect() {
        String procedure = "CREATE VIRTUAL PROCEDURE "; //$NON-NLS-1$
        procedure += "BEGIN\n"; //$NON-NLS-1$
        procedure += "DECLARE integer VARIABLES.var1 = 1;\n"; //$NON-NLS-1$
        procedure += "select * from variables;\n"; //$NON-NLS-1$
        procedure += "END\n"; //$NON-NLS-1$

        IQueryMetadataInterface metadata = getMetadataFactory().example1Cached();

        Command command = helpResolve(procedure, metadata);
        helpRunValidator(command, new String[] {"variables"}, metadata); //$NON-NLS-1$
    }

    @Test
    public void testClobEquals() {
        helpValidate("SELECT * FROM test.group where e4 = '1'", new String[] {"e4 = '1'"}, exampleMetadata2()); //$NON-NLS-1$ //$NON-NLS-2$ 
    }

    /**
     *  Should not fail since the update changing set is not really criteria
     */
    @Test
    public void testUpdateWithClob() {
        helpValidate("update test.group set e4 = ?", new String[] {}, exampleMetadata2()); //$NON-NLS-1$ 
    }

    @Test
    public void testBlobLessThan() {
        helpValidate("SELECT * FROM test.group where e3 < ?", new String[] {"e3 < ?"}, exampleMetadata2()); //$NON-NLS-1$ //$NON-NLS-2$ 
    }

    @Test
    public void testValidateCompare2() {
        helpValidate("SELECT e2 FROM test.group WHERE e4 IS NULL", new String[] {}, exampleMetadata2()); //$NON-NLS-1$ 
    }

    @Test
    public void testValidateCompare3() {
        helpValidate("SELECT e2 FROM test.group WHERE e4 IN ('a')", new String[] {"e4 IN ('a')"}, exampleMetadata2()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testValidateCompare5() {
        helpValidate("SELECT e2 FROM test.group WHERE e4 BETWEEN '1' AND '2'", new String[] {"e4 BETWEEN '1' AND '2'"}, exampleMetadata2()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testValidateCompareInHaving1() {
        helpValidate("SELECT e1 FROM test.group GROUP BY e1 HAVING convert(e1, clob) = 'a'", new String[] {"convert(e1, clob) = 'a'"}, exampleMetadata2()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testValidateNoExpressionName() {
        helpValidate("SELECT xmlelement(name a, xmlattributes('1'))", new String[] {"XMLATTRIBUTES('1')"}, exampleMetadata2()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testValidateNoExpressionName1() {
        helpValidate("SELECT xmlforest('1')", new String[] {"XMLFOREST('1')"}, exampleMetadata2()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testXpathValueValid_defect15088() {
        String userSql = "SELECT xpathValue('<?xml version=\"1.0\" encoding=\"utf-8\" ?><a><b><c>test</c></b></a>', 'a/b/c')"; //$NON-NLS-1$
        helpValidate(userSql, new String[] {}, getMetadataFactory().exampleBQTCached());
    }

    @Test
    public void testXpathValueInvalid_defect15088() throws Exception {
        String userSql = "SELECT xpathValue('<?xml version=\"1.0\" encoding=\"utf-8\" ?><a><b><c>test</c></b></a>', '//*[local-name()=''bookName\"]')"; //$NON-NLS-1$
        helpValidate(userSql,
                     new String[] {"xpathValue('<?xml version=\"1.0\" encoding=\"utf-8\" ?><a><b><c>test</c></b></a>', '//*[local-name()=''bookName\"]')"},
                     getMetadataFactory().exampleBQTCached());
    }

    @Test
    public void testTextTableNoWidth() {
        helpValidate("SELECT * from texttable(null columns x string width 1, y integer) as x",
                     new String[] {"TEXTTABLE(null COLUMNS x string WIDTH 1, y integer) AS x"},
                     getMetadataFactory().exampleBQTCached());
    }

    @Test
    public void testTextTableInvalidDelimiter() {
        helpValidate("SELECT * from texttable(null columns x string width 1 DELIMITER 'z') as x",
                     new String[] {"TEXTTABLE(null COLUMNS x string WIDTH 1 DELIMITER 'z') AS x"},
                     getMetadataFactory().exampleBQTCached());
    }

    @Test
    public void testTextTableNoRowDelimiter() {
        helpValidate("SELECT * from texttable(null columns x string NO ROW DELIMITER) as x",
                     new String[] {"TEXTTABLE(null COLUMNS x string NO ROW DELIMITER) AS x"},
                     getMetadataFactory().exampleBQTCached());
    }

    @Test
    public void testXMLNamespaces() {
        helpValidate("select xmlforest(xmlnamespaces(no default, default 'http://foo'), e1 as \"table\") from pm1.g1",
                     new String[] {"XMLNAMESPACES(NO DEFAULT, DEFAULT 'http://foo')"},
                     getMetadataFactory().example1Cached());
    }

    @Test
    public void testXMLNamespacesInvalid() {
        helpValidate("select xmlforest(xmlnamespaces('http://foo' as \"1\"), e1 as \"table\") from pm1.g1",
                     new String[] {"XMLNAMESPACES('http://foo' AS \"1\")"},
                     getMetadataFactory().example1Cached());
    }

    @Test
    public void testXMLNamespacesReserved() {
        helpValidate("select xmlforest(xmlnamespaces('http://foo' as xmlns), e1 as \"table\") from pm1.g1",
                     new String[] {"XMLNAMESPACES('http://foo' AS xmlns)"},
                     getMetadataFactory().example1Cached());
    }

    @Test
    public void testXMLTablePassingMultipleContext() {
        helpValidate("select * from pm1.g1, xmltable('/' passing xmlparse(DOCUMENT '<a/>'), xmlparse(DOCUMENT '<b/>')) as x",
                     new String[] {"XMLTABLE('/' PASSING XMLPARSE(DOCUMENT '<a/>'), XMLPARSE(DOCUMENT '<b/>')) AS x"},
                     getMetadataFactory().example1Cached());
    }

    @Test
    public void testInvalidDefault() {
        helpValidate("select * from pm1.g1, xmltable('/' passing xmlparse(DOCUMENT '<a/>') columns y string default 'a', x string default (select e1 from pm1.g1)) as x",
                     new String[] {"XMLTABLE('/' PASSING XMLPARSE(DOCUMENT '<a/>') COLUMNS y string DEFAULT 'a', x string DEFAULT (SELECT e1 FROM pm1.g1)) AS x"},
                     getMetadataFactory().example1Cached());
    }

    @Ignore( "this is actually handled by saxon and will show up during resolving" )
    @Test
    public void testXMLTablePassingSameName() {
        helpValidate("select * from pm1.g1, xmltable('/' passing {x '<a/>'} as a, {x '<b/>'} as a) as x",
                     new String[] {"xmltable('/' passing e1, e1 || 'x') as x"},
                     getMetadataFactory().example1Cached());
    }

    @Test
    public void testXMLTablePassingContextType() {
        helpValidate("select * from pm1.g1, xmltable('/' passing 2) as x",
                     new String[] {"XMLTABLE('/' PASSING 2) AS x"},
                     getMetadataFactory().example1Cached());
    }

    @Test
    public void testXMLTableMultipleOrdinals() {
        helpValidate("select * from pm1.g1, xmltable('/' passing XMLPARSE(DOCUMENT '<a/>') columns x for ordinality, y for ordinality) as x",
                     new String[] {"XMLTABLE('/' PASSING XMLPARSE(DOCUMENT '<a/>') COLUMNS x FOR ORDINALITY, y FOR ORDINALITY) AS x"},
                     getMetadataFactory().example1Cached());
    }

    @Test
    public void testXMLTableContextRequired() {
        helpValidate("select * from xmltable('/a/b' passing convert('<a/>', xml) as a columns x for ordinality, c integer path '.') as x",
                     new String[] {"XMLTABLE('/a/b' PASSING convert('<a/>', xml) AS a COLUMNS x FOR ORDINALITY, c integer PATH '.') AS x"},
                     getMetadataFactory().example1Cached());
    }

    @Test
    public void testXMLQueryPassingContextType() {
        helpValidate("select xmlquery('/' passing 2)",
                     new String[] {"XMLQUERY('/' PASSING 2)"},
                     getMetadataFactory().example1Cached());
    }

    @Test
    public void testQueryString() {
        helpValidate("select querystring('/', '1')", new String[] {"QUERYSTRING('/', '1')"}, getMetadataFactory().example1Cached());
    }

    @Test
    public void testXmlNameValidation() throws Exception {
        helpValidate("select xmlelement(\":\")", new String[] {"XMLELEMENT(NAME \":\")"}, getMetadataFactory().example1Cached());
    }

    @Test
    public void testXmlParse() throws Exception {
        helpValidate("select xmlparse(content e2) from pm1.g1",
                     new String[] {"XMLPARSE(CONTENT e2)"},
                     getMetadataFactory().example1Cached());
    }

    @Test
    public void testDecode() throws Exception {
        helpValidate("select to_bytes(e1, '?') from pm1.g1",
                     new String[] {"to_bytes(e1, '?')"},
                     getMetadataFactory().example1Cached());
    }

    @Test
    public void testValidateXMLAGG() {
        helpValidate("SELECT XMLAGG(e1) from pm1.g1", new String[] {"XMLAGG(e1)"}, getMetadataFactory().example1Cached()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testValidateBooleanAgg() {
        helpValidate("SELECT EVERY(e1) from pm1.g1", new String[] {"EVERY(e1)"}, getMetadataFactory().example1Cached()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testValidateStatAgg() {
        helpValidate("SELECT stddev_pop(distinct e2) from pm1.g1", new String[] {"STDDEV_POP(DISTINCT e2)"}, getMetadataFactory().example1Cached()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testValidateScalarSubqueryTooManyColumns() {
        helpValidate("SELECT e2, (SELECT e1, e2 FROM pm1.g1 WHERE e2 = '3') FROM pm1.g2", new String[] {"SELECT e1, e2 FROM pm1.g1 WHERE e2 = '3'"}, getMetadataFactory().example1Cached()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testInvalidIntoSubquery() {
        helpValidate("SELECT e2, (SELECT e1, e2 INTO #x FROM pm1.g1 WHERE e2 = '3') FROM pm1.g2", new String[] {"SELECT e1, e2 INTO #x FROM pm1.g1 WHERE e2 = '3'"}, getMetadataFactory().example1Cached()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testInvalidIntoSubquery1() {
        helpValidate("SELECT e2 FROM pm1.g2 WHERE EXISTS (SELECT e1, e2 INTO #x FROM pm1.g1 WHERE e2 = '3')", new String[] {"SELECT e1, e2 INTO #x FROM pm1.g1 WHERE e2 = '3'"}, getMetadataFactory().example1Cached()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testInvalidIntoSubquery2() {
        helpValidate("SELECT * FROM (SELECT e1, e2 INTO #x FROM pm1.g1 WHERE e2 = '3') x", new String[] {"SELECT e1, e2 INTO #x FROM pm1.g1 WHERE e2 = '3'"}, getMetadataFactory().example1Cached()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testInvalidIntoSubquery3() {
        helpValidate("SELECT e2 FROM pm1.g2 WHERE e2 in (SELECT e1, e2 INTO #x FROM pm1.g1 WHERE e2 = '3')", new String[] {"SELECT e1, e2 INTO #x FROM pm1.g1 WHERE e2 = '3'"}, getMetadataFactory().example1Cached()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testInvalidIntoSubquery4() throws Exception {
        StringBuffer procedure = new StringBuffer("CREATE VIRTUAL PROCEDURE\n") //$NON-NLS-1$
        .append("BEGIN\n") //$NON-NLS-1$
        .append("loop on (SELECT e1, e2 INTO #x FROM pm1.g1 WHERE e2 = '3') as x\n") //$NON-NLS-1$
        .append("BEGIN\nSELECT 1;\nEND\nSELECT 1\n;END\n"); //$NON-NLS-1$

        IQueryMetadataInterface metadata = getMetadataFactory().example1Cached();

        // Validate
        ValidatorReport report = helpValidateInModeler("pm1.vsp36", procedure.toString(), metadata); //$NON-NLS-1$
        examineReport(procedure, new String[] {"SELECT e1, e2 INTO #x FROM pm1.g1 WHERE e2 = '3'"}, report);
    }

    @Test
    public void testTextAggEncoding() throws Exception {
        helpValidate("select textagg(for e1 encoding abc) from pm1.g1", new String[] {"TEXTAGG(FOR e1 ENCODING abc)"}, getMetadataFactory().example1Cached()); //$NON-NLS-1$
    }

    @Test
    public void testTextAggHeader() throws Exception {
        helpValidate("select textagg(for e1 || 1 HEADER) from pm1.g1", new String[] {"TEXTAGG(FOR (e1 || 1) HEADER)"}, getMetadataFactory().example1Cached()); //$NON-NLS-1$
    }

    @Test
    public void testFailAggregateInGroupBy() {
        helpValidate("SELECT max(e1) FROM pm1.g1 GROUP BY count(e2)",
                     new String[] {"COUNT(e2)"},
                     getMetadataFactory().example1Cached());
    }

    @Test
    public void testFailAggregateInWhere() {
        helpValidate("SELECT e1 FROM pm1.g1 where count(e2) = 1",
                     new String[] {"COUNT(e2)"},
                     getMetadataFactory().example1Cached());
    }

    @Test
    public void testFailAggregateInFrom() {
        helpValidate("SELECT g2.e1 FROM pm1.g1 inner join pm1.g2 on (avg(g1.e2) = g2.e2)",
                     new String[] {"AVG(g1.e2)"},
                     getMetadataFactory().example1Cached());
    }

    @Test
    public void testFailAggregateFilterSubquery() {
        helpValidate("SELECT min(g1.e1) filter (where (select 1) = 1) from pm1.g1",
                     new String[] {"(SELECT 1) = 1"},
                     getMetadataFactory().example1Cached());
    }

    @Test
    public void testNestedAgg() {
        helpValidate("SELECT min(g1.e1) filter (where max(e2) = 1) from pm1.g1",
                     new String[] {"MAX(e2)"},
                     getMetadataFactory().example1Cached());
    }

    @Test
    public void testWindowFunction() {
        helpValidate("SELECT e1 from pm1.g1 where row_number() over (order by e2) = 1",
                     new String[] {"ROW_NUMBER() OVER (ORDER BY e2)"},
                     getMetadataFactory().example1Cached());
    }

    @Test
    public void testWindowFunction1() {
        helpValidate("SELECT 1 from pm1.g1 having row_number() over (order by e2) = 1", new String[] {"e2",
            "ROW_NUMBER() OVER (ORDER BY e2)"}, getMetadataFactory().example1Cached());
    }

    @Test
    public void testWindowFunctionWithoutOrdering() {
        helpValidate("SELECT row_number() over () from pm1.g1",
                     new String[] {"ROW_NUMBER() OVER ()"},
                     getMetadataFactory().example1Cached());
    }

    @Test
    public void testWindowFunctionWithNestedOrdering() {
        helpValidate("SELECT xmlagg(xmlelement(name x, e1) order by e2) over () from pm1.g1",
                     new String[] {"XMLAGG(XMLELEMENT(NAME x, e1) ORDER BY e2) OVER ()"},
                     getMetadataFactory().example1Cached());
    }

    @Test
    public void testWindowFunctionWithNestedaggAllowed() {
        helpValidate("SELECT max(e1) over (order by max(e2)) from pm1.g1 group by e1",
                     new String[] {},
                     getMetadataFactory().example1Cached());
    }

    @Test
    public void testWindowFunctionWithNestedaggAllowed1() {
        helpValidate("SELECT max(min(e1)) over (order by max(e2)) from pm1.g1 group by e1",
                     new String[] {},
                     getMetadataFactory().example1Cached());	
    }

    @Test
    public void testWindowFunctionWithoutFrom() {
        helpValidate("select count(*) over () as y", new String[] {"COUNT(*) OVER ()"}, getMetadataFactory().example1Cached());
    }

    @Test
    public void testWindowFunctionOrderedDistinct() {
        helpValidate("select count(distinct e1) over (order by e2) as y from pm1.g1",
                     new String[] {"COUNT(DISTINCT e1) OVER (ORDER BY e2)"},
                     getMetadataFactory().example1Cached());
    }

    @Test
    public void testInvalidCorrelation() {
        helpValidate("SELECT XMLELEMENT(NAME metadata, XMLFOREST(e1 AS objectName), (SELECT XMLAGG(XMLELEMENT(NAME subTypes, XMLFOREST(e1))) FROM pm1.g2 AS b WHERE b.e2 = a.e2)) FROM pm1.g1 AS a GROUP BY e1",
                     new String[] {"a.e2"},
                     getMetadataFactory().example1Cached());
    }
    
    @Test public void testUpdateError() {
        String userUpdateStr = "UPDATE vm1.g2 SET e1='x'"; //$NON-NLS-1$
        
        helpValidate(userUpdateStr, new String[]{"vm1.g2", "UPDATE vm1.g2 SET e1 = 'x'"}, getMetadataFactory().example1Cached()); //$NON-NLS-1$
    }
    
    @Test public void testInsertError() {
        String userUpdateStr = "INSERT into vm1.g2 (e1) values ('x')"; //$NON-NLS-1$
        
        helpValidate(userUpdateStr, new String[]{"vm1.g2", "INSERT INTO vm1.g2 (e1) VALUES ('x')"}, getMetadataFactory().example1Cached()); //$NON-NLS-1$
    }
    
    @Test public void testMergeNoKey() {
        String userUpdateStr = "MERGE into pm1.g2 (e1) values ('x')"; //$NON-NLS-1$
        
        helpValidate(userUpdateStr, new String[]{"MERGE INTO pm1.g2 (e1) VALUES ('x')"}, getMetadataFactory().example1Cached()); //$NON-NLS-1$
    }
    
    @Test public void testDeleteError() {
        String userUpdateStr = "DELETE from vm1.g2 where e1='x'"; //$NON-NLS-1$
        
        helpValidate(userUpdateStr, new String[]{"vm1.g2"}, getMetadataFactory().example1Cached()); //$NON-NLS-1$
    }
    
    @Test public void testJsonArrayBlob() {
        String sql = "select jsonArray(to_bytes('hello', 'us-ascii'))"; //$NON-NLS-1$
        
        helpValidate(sql, new String[]{"jsonArray(to_bytes('hello', 'us-ascii'))"}, getMetadataFactory().example1Cached()); //$NON-NLS-1$
    }
    
    @Test public void testJsonArrayClob() {
        String sql = "select jsonArray(cast('hello' as clob))"; //$NON-NLS-1$
        
        helpValidate(sql, new String[]{}, getMetadataFactory().example1Cached()); //$NON-NLS-1$
    }
    
    @Test public void testJsonObject() {
        String sql = "select jsonObject(to_bytes('hello', 'us-ascii'))"; //$NON-NLS-1$
        
        helpValidate(sql, new String[]{"JSONOBJECT(to_bytes('hello', 'us-ascii'))"}, getMetadataFactory().example1Cached()); //$NON-NLS-1$
    }
    
    @Test public void testWithValidation() {
        String sql = "with a as (select jsonObject(to_bytes('hello', 'us-ascii')) as x) select a.x from a"; //$NON-NLS-1$
        
        helpValidate(sql, new String[]{"JSONOBJECT(to_bytes('hello', 'us-ascii'))"}, getMetadataFactory().example1Cached()); //$NON-NLS-1$
    }
    
    @Test public void testInsertIntoVirtualWithQueryExpression() { 

//        String sql = "select * from vm1.g1 as x"; //$NON-NLS-1$
//        
//        TestProcessor.helpGetPlan(sql, getMetadataFactory().example1Cached());
//        
//        sql = "insert into vm1.g1 (e1, e2, e3, e4) select * from pm1.g1"; //$NON-NLS-1$
//        
//        helpValidate(sql, new String[] {}, getMetadataFactory().example1Cached());

    }
    
    @Test public void testInsertDuplicateVariable() { 
        String sql = "insert into pm1.g1 (e1, e1) values ('a', 'b')"; //$NON-NLS-1$
        helpValidate(sql, new String[] {"e1"}, getMetadataFactory().example1Cached());
    }
}
