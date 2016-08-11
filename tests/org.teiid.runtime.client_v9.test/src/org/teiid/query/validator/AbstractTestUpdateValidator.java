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

import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.teiid.core.types.DataTypeManagerService;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.metadata.BaseColumn.NullType;
import org.teiid.metadata.Column;
import org.teiid.metadata.ColumnSet;
import org.teiid.metadata.KeyRecord;
import org.teiid.metadata.KeyRecord.Type;
import org.teiid.metadata.MetadataStore;
import org.teiid.metadata.Procedure;
import org.teiid.metadata.ProcedureParameter;
import org.teiid.metadata.Schema;
import org.teiid.metadata.Table;
import org.teiid.query.mapping.relational.QueryNode;
import org.teiid.query.metadata.TransformationMetadata;
import org.teiid.query.resolver.QueryResolver;
import org.teiid.query.resolver.util.ResolverUtil;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.symbol.Expression;
import org.teiid.query.sql.symbol.GroupSymbol;
import org.teiid.query.sql.symbol.Symbol;
import org.teiid.query.validator.UpdateValidator.UpdateInfo;
import org.teiid.query.validator.UpdateValidator.UpdateType;

@SuppressWarnings({"javadoc", "nls"})
public abstract class AbstractTestUpdateValidator extends AbstractTest {

	/**
     * @param teiidVersion
     */
    public AbstractTestUpdateValidator(Version teiidVersion) {
        super(teiidVersion);
    }

    private UpdateValidator helpTest(String sql, TransformationMetadata md, boolean shouldFail) {
		return helpTest(sql, md, shouldFail, shouldFail, shouldFail);
	}
	
	private UpdateValidator helpTest(String sql, TransformationMetadata md, boolean failInsert, boolean failUpdate, boolean failDelete) { 	
		try {
			String vGroup = "gx";
			Command command = createView(sql, md, vGroup);
			
			UpdateValidator uv = new UpdateValidator(md, UpdateType.INHERENT, UpdateType.INHERENT, UpdateType.INHERENT);
			GroupSymbol gs = getFactory().newGroupSymbol(vGroup);
			ResolverUtil.resolveGroup(gs, md);
			uv.validate(command, ResolverUtil.resolveElementsInGroup(gs, md));
			UpdateInfo info = uv.getUpdateInfo();
			assertEquals(uv.getReport().getFailureMessage(), failInsert, info.getInsertValidationError() != null);
			assertEquals(uv.getReport().getFailureMessage(), failUpdate, info.getUpdateValidationError() != null);
			assertEquals(uv.getReport().getFailureMessage(), failDelete, info.getDeleteValidationError() != null);
			return uv;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Command createView(String sql, TransformationMetadata md, String vGroup)
			throws Exception {
		QueryNode vm1g1n1 = new QueryNode(sql); 
		Table vm1g1 = getMetadataFactory().createUpdatableVirtualGroup(vGroup, md.getMetadataStore().getSchema("VM1"), vm1g1n1);

		Command command = getQueryParser().parseCommand(sql);
		QueryResolver queryResolver = new QueryResolver(getTeiidVersion());
		queryResolver.resolveCommand(command, md);

		List<Expression> symbols = command.getProjectedSymbols();
		String[] names = new String[symbols.size()];
		String[] types = new String[symbols.size()];
		int i = 0;
		for (Expression singleElementSymbol : symbols) {
			names[i] = Symbol.getShortName(singleElementSymbol);
			types[i++] = getDataTypeManager().getDataTypeName(singleElementSymbol.getType());
		}
		
		getMetadataFactory().createElements(vm1g1, names, types);
		return command;
	}
	
 	public TransformationMetadata example1() { 
 		return example1(true);
 	}

 	public TransformationMetadata example1(boolean allUpdatable) { 
 		MetadataStore metadataStore = new MetadataStore();
		
 		// Create models
		Schema pm1 = getMetadataFactory().createPhysicalModel("pm1", metadataStore); //$NON-NLS-1$
		Schema vm1 = getMetadataFactory().createVirtualModel("vm1", metadataStore);	 //$NON-NLS-1$

		// Create physical groups
		Table pm1g1 = getMetadataFactory().createPhysicalGroup("g1", pm1); //$NON-NLS-1$
		Table pm1g2 = getMetadataFactory().createPhysicalGroup("g2", pm1); //$NON-NLS-1$
		Table pm1g3 = getMetadataFactory().createPhysicalGroup("g3", pm1); //$NON-NLS-1$
				
		// Create physical elements
		List<Column> pm1g1e = getMetadataFactory().createElements(pm1g1, 
			new String[] { "e1", "e2", "e3", "e4" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			new String[] { 
		        DataTypeManagerService.DefaultDataTypes.STRING.getId(),
		        DataTypeManagerService.DefaultDataTypes.INTEGER.getId(),
		        DataTypeManagerService.DefaultDataTypes.BOOLEAN.getId(),
		        DataTypeManagerService.DefaultDataTypes.DOUBLE.getId() });
		if (!allUpdatable) {
			pm1g1e.get(0).setUpdatable(false);
		}
		
		KeyRecord pk = getMetadataFactory().createKey(Type.Primary, "pk", pm1g1, pm1g1e.subList(0, 1));
		
		List<Column> pm1g2e = getMetadataFactory().createElements(pm1g2, 
			new String[] { "e1", "e2", "e3", "e4" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			new String[] {
		        DataTypeManagerService.DefaultDataTypes.STRING.getId(),
		        DataTypeManagerService.DefaultDataTypes.INTEGER.getId(),
		        DataTypeManagerService.DefaultDataTypes.BOOLEAN.getId(),
		        DataTypeManagerService.DefaultDataTypes.DOUBLE.getId() });

		getMetadataFactory().createKey(Type.Primary, "pk", pm1g2, pm1g1e.subList(1, 2));
		getMetadataFactory().createForeignKey("fk", pm1g2, pm1g2e.subList(0, 1), pk);
		
        List<Column> pm1g3e = getMetadataFactory().createElements(pm1g3, 
            new String[] { "e1", "e2", "e3", "e4" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            new String[] { 
                DataTypeManagerService.DefaultDataTypes.STRING.getId(),
                DataTypeManagerService.DefaultDataTypes.INTEGER.getId(),
                DataTypeManagerService.DefaultDataTypes.BOOLEAN.getId(),
                DataTypeManagerService.DefaultDataTypes.DOUBLE.getId() });
        pm1g3e.get(0).setNullType(NullType.No_Nulls);
        pm1g3e.get(0).setDefaultValue(null);
        
        pm1g3e.get(1).setNullType(NullType.No_Nulls);
        pm1g3e.get(1).setAutoIncremented(true);
        pm1g3e.get(1).setDefaultValue(null);
        
        pm1g3e.get(2).setNullType(NullType.No_Nulls);
        pm1g3e.get(2).setDefaultValue("xyz"); //$NON-NLS-1$

		// Create virtual groups
		QueryNode vm1g1n1 = new QueryNode("SELECT e1 as a, e2 FROM pm1.g1 WHERE e3 > 5"); //$NON-NLS-1$ //$NON-NLS-2$
		Table vm1g1 = getMetadataFactory().createUpdatableVirtualGroup("g1", vm1, vm1g1n1); //$NON-NLS-1$
		QueryNode vm1g2n1 = new QueryNode("SELECT e1, e2, e3, e4 FROM pm1.g2 WHERE e3 > 5"); //$NON-NLS-1$ //$NON-NLS-2$
		Table vm1g2 = getMetadataFactory().createUpdatableVirtualGroup("g2", vm1, vm1g2n1); //$NON-NLS-1$
        QueryNode vm1g3n1 = new QueryNode("SELECT e1, e3 FROM pm1.g3"); //$NON-NLS-1$ //$NON-NLS-2$
        Table vm1g3 = getMetadataFactory().createUpdatableVirtualGroup("g3", vm1, vm1g3n1); //$NON-NLS-1$
        QueryNode vm1g4n1 = new QueryNode("SELECT e1, e2 FROM pm1.g3"); //$NON-NLS-1$ //$NON-NLS-2$
        Table vm1g4 = getMetadataFactory().createUpdatableVirtualGroup("g4", vm1, vm1g4n1); //$NON-NLS-1$
        QueryNode vm1g5n1 = new QueryNode("SELECT e2, e3 FROM pm1.g3"); //$NON-NLS-1$ //$NON-NLS-2$
        Table vm1g5 = getMetadataFactory().createVirtualGroup("g5", vm1, vm1g5n1); //$NON-NLS-1$

		// Create virtual elements
		getMetadataFactory().createElements(vm1g1, 
			new String[] { "a", "e2"}, //$NON-NLS-1$ //$NON-NLS-2$
			new String[] { 
		        DataTypeManagerService.DefaultDataTypes.STRING.getId(),
		        DataTypeManagerService.DefaultDataTypes.INTEGER.getId()});
		getMetadataFactory().createElements(vm1g2, 
			new String[] { "e1", "e2","e3", "e4"}, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			new String[] {
		        DataTypeManagerService.DefaultDataTypes.STRING.getId(),
		        DataTypeManagerService.DefaultDataTypes.INTEGER.getId(),
		        DataTypeManagerService.DefaultDataTypes.BOOLEAN.getId(),
		        DataTypeManagerService.DefaultDataTypes.DOUBLE.getId() });
        getMetadataFactory().createElements(vm1g3, 
            new String[] { "e1", "e2"}, //$NON-NLS-1$ //$NON-NLS-2$
            new String[] {
                DataTypeManagerService.DefaultDataTypes.STRING.getId(),
                DataTypeManagerService.DefaultDataTypes.INTEGER.getId()});
        getMetadataFactory().createElements(vm1g4, 
            new String[] { "e1", "e3"}, //$NON-NLS-1$ //$NON-NLS-2$
            new String[] {
                DataTypeManagerService.DefaultDataTypes.STRING.getId(),
                DataTypeManagerService.DefaultDataTypes.BOOLEAN.getId()});
        getMetadataFactory().createElements(vm1g5, 
            new String[] { "e2","e3"}, //$NON-NLS-1$ //$NON-NLS-2$
            new String[] {
                DataTypeManagerService.DefaultDataTypes.INTEGER.getId(),
                DataTypeManagerService.DefaultDataTypes.BOOLEAN.getId()});

        // Stored queries
        ColumnSet<Procedure> rs1 = getMetadataFactory().createResultSet("rs1", new String[] { "e1", "e2" }, 
                                                                            new String[] {
                                                                                DataTypeManagerService.DefaultDataTypes.STRING.getId(),
                                                                                DataTypeManagerService.DefaultDataTypes.INTEGER.getId()});
        QueryNode sq1n1 = new QueryNode("CREATE VIRTUAL PROCEDURE BEGIN SELECT e1, e2 FROM pm1.g1; END"); //$NON-NLS-1$ //$NON-NLS-2$
        Procedure sq1 = getMetadataFactory().createVirtualProcedure("sq1", pm1, Collections.<ProcedureParameter> emptyList(), sq1n1);  //$NON-NLS-1$
        sq1.setResultSet(rs1);
		// Create the facade from the store
		return getMetadataFactory().createTransformationMetadata(metadataStore, "example");
	}	
 	
	//actual tests
	@Test public void testCreateInsertCommand(){
		helpTest("select e1 as a, e2 from pm1.g1 where e4 > 5", 
			example1(), false); //$NON-NLS-1$
	}
	
	@Test public void testCreateInsertCommand2(){ //put a constant in select statement
		helpTest("select e1 as a, 5 from pm1.g1 where e4 > 5", 
			example1(), false); //$NON-NLS-1$
	}
	
	@Test public void testCreateInsertCommand3(){ 
		helpTest("select * from pm1.g2 where e4 > 5", 
			example1(), false); //$NON-NLS-1$
	}
	
	@Test public void testCreateInsertCommand4(){ //test group alias
		helpTest("select * from pm1.g2 as g_alias", 
			example1(), false); //$NON-NLS-1$
	}	

	@Test public void testCreateInsertCommand5(){
		helpTest("select e1 as a, e2 from pm1.g1 as g_alias where e4 > 5", 
			example1(), false); //$NON-NLS-1$
	}
		
	@Test public void testCreateUpdateCommand(){
		helpTest("select e1 as a, e2 from pm1.g1 where e4 > 5", 
			example1(), false); //$NON-NLS-1$
	}
	
	@Test public void testCreateDeleteCommand(){
		helpTest("select e1 as a, e2 from pm1.g1 where e4 > 5", 
			example1(), false); //$NON-NLS-1$
	}

    @Test public void testCreateInsertCommand1(){
        helpTest("SELECT pm1.g1.e1 FROM pm1.g1, pm1.g2",
            example1(), true);
    }
    
    @Test public void testCreateInsertCommand14(){
        helpTest("SELECT pm1.g2.e1 FROM pm1.g1, pm1.g2 where g1.e1 = g2.e1",
            example1(), false);
    }

    @Test public void testCreateInsertCommand2_fail(){
        helpTest("SELECT CONCAT(pm1.g1.e1, convert(pm1.g2.e1, string)) as x FROM pm1.g1, pm1.g2",
            example1(), true);
    }

    @Test public void testCreateInsertCommand3_fail(){
        helpTest("SELECT e1 FROM pm1.g1 UNION SELECT e1 FROM pm1.g2",
            example1(), true);
    }

    @Test public void testCreateInsertCommand4_fail(){
        helpTest("SELECT COUNT(*) FROM pm1.g1",
            example1(), true);
    }

    @Test public void testCreateInsertCommand5_fail(){
        helpTest("SELECT * FROM pm1.g1 GROUP BY e1",
            example1(), true);
    }

    @Test public void testCreateInsertCommand6_fail(){
        helpTest("EXEC pm1.sq1()",
            example1(), true);
    }

    @Test public void testCreateInsertCommand7_fail(){
        helpTest("INSERT INTO pm1.g1 (e1) VALUES ('x')",
            example1(), true);
    }

    @Test public void testCreateInsertCommand8_fail(){
        helpTest("UPDATE pm1.g1 SET e1='x'",
            example1(), true);
    }

    @Test public void testCreateInsertCommand9_fail(){
        helpTest("DELETE FROM pm1.g1",
            example1(), true);
    }

    @Test public void testCreateInsertCommand10_fail(){
        helpTest("SELECT COUNT(*) FROM pm1.g1",
            example1(), true);
    }

    @Test public void testCreateInsertCommand11_fail(){
        helpTest("SELECT COUNT(e1) as x FROM pm1.g1",
            example1(), true);
    }
    
    @Test public void testCreateInsertCommand12_fail(){
        helpTest("SELECT * FROM (EXEC pm1.sq1()) AS a", 
            example1(), true);
    }    

    @Test public void testCreateInsertCommand13_fail(){
        helpTest("SELECT 1", 
            example1(), true);
    }    
    
    @Test public void testRequiredElements1() {
        helpTest("SELECT e1, e2 FROM pm1.g3",
            example1(), false); //$NON-NLS-1$
    }

    @Test public void testRequiredElements2() {
        helpTest("SELECT e1, e3 FROM pm1.g3",
            example1(), false); //$NON-NLS-1$
    }

    @Test public void testRequiredElements3() {
        helpTest("SELECT e2, e3 FROM pm1.g3",
            example1(), true, false, false);
    }

    @Test public void testNonUpdateableElements() {
        helpTest("select e1 as a, e2 from pm1.g1 where e4 > 5", 
                    example1(false), false); //$NON-NLS-1$
	}
	
    @Test public void testNonUpdateableElements2() {
        helpTest("SELECT e1, e2 FROM pm1.g1",
            example1(false), false); //$NON-NLS-1$
    }
    
    @Test public void testSelectDistinct() {
        helpTest("SELECT distinct e1, e2 FROM pm1.g1",
            example1(), true); //$NON-NLS-1$
    }
    
    @Test public void testNonUpdatable() {
        helpTest("SELECT e2 FROM vm1.g5",
            example1(), true); //$NON-NLS-1$
    }
    
    @Test public void testAnsiJoin() {
        helpTest("SELECT g1.e1, x.e2 FROM pm1.g2 x inner join pm1.g1 on (x.e1 = g1.e1)",
            example1(), false); //$NON-NLS-1$
    }
    
    @Test public void testUnionAll() {
        helpTest("SELECT g1.e1, x.e2 FROM pm1.g2 x inner join pm1.g1 on (x.e1 = g1.e1) union all select pm1.g2.e1, pm1.g2.e2 from pm1.g2",
            example1(), true, false, false); //$NON-NLS-1$
    }
    
    @Test public void testParitionedUnionAll() {
        helpTest("SELECT g1.e1, x.e2 FROM pm1.g2 x inner join pm1.g1 on (x.e1 = g1.e1) where x.e2 in (1, 2) union all select pm1.g2.e1, pm1.g2.e2 from pm1.g2 where pm1.g2.e2 in (3, 4)",
            example1(), false, false, false); //$NON-NLS-1$
    }
}

