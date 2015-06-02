/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.komodo.vdb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.junit.Test;
import org.teiid.core.util.SmartTestDesignerSuite;
import org.teiid.designer.komodo.vdb.Metadata.Type;
import org.teiid.designer.komodo.vdb.Model;
import org.teiid.designer.komodo.vdb.dynamic.DynamicVdb;

public class TestDyamicVdb {
	private File PRODUCT_VIEW_DYNAMIC_VDB = SmartTestDesignerSuite.getTestDataFile(getClass(), "dynamic_vdbs" + File.separator + "product-view-vdb.xml");
	private File PORTFOLIO_DYNAMIC_VDB = SmartTestDesignerSuite.getTestDataFile(getClass(), "dynamic_vdbs" + File.separator + "portfolio-vdb.xml");

	
	@Test
	public void testLoad_1() {
		Collection<String> modelNames = new ArrayList<String>();
		modelNames.add("MarketData");
		modelNames.add("Accounts");
		modelNames.add("PersonalValuations");
		modelNames.add("Stocks");
		modelNames.add("StocksMatModel");
		
		DynamicVdb vdb = new DynamicVdb(PORTFOLIO_DYNAMIC_VDB);

		try {
			vdb.load();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertNotNull(vdb.getName());

		assertEquals(5, vdb.getModels().length);
		
		for( Model model : vdb.getModels()) {
			assertTrue( modelNames.contains(model.getName()) );
			if( model.getName().equalsIgnoreCase("Stocks")) {
				assertTrue(model.getMetadata() != null );
				assertTrue(model.getMetadata().getType() == Type.DDL );
				System.out.println("CDATA for model = " + model.getName() + "\n\n" +
						model.getMetadata().getSchemaText() );
			}
		}
		
		
	}
	
	@Test
	public void testExport_1() {
		DynamicVdb vdb = new DynamicVdb(PORTFOLIO_DYNAMIC_VDB);

		try {
			vdb.load();
			vdb.export();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void testExport_2() {
        IPath fullPath = new Path(PORTFOLIO_DYNAMIC_VDB.getAbsolutePath());

        IPath locationPath = fullPath.removeLastSegments(1);
        IPath tempPath = locationPath.append("DynamicVdbTest-vdb.xml");
		DynamicVdb vdb = new DynamicVdb("DynamicVdbTest");
		vdb.setOriginalFilePath(tempPath.toString());
		
		Model model = new Model("TestModel_1");
		model.setMetadata(new Metadata("CREATE VIEW ABCDEFG (C1 integer, C2 integer) AS SELECT * FROM TABLE_B;", "DDL"));
		model.setModelType("VIRTUAL");
		vdb.addModel(model);

		try {
			vdb.export();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
