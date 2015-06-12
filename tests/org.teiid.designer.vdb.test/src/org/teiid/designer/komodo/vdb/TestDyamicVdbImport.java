/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.komodo.vdb;

import java.io.File;
import org.eclipse.core.resources.IFile;
import org.teiid.core.util.SmartTestDesignerSuite;

@SuppressWarnings( "javadoc" )
public class TestDyamicVdbImport {

    private static final String DYNAMIC_VDBS = "dynamic_vdbs";

    private File PRODUCT_VIEW_DYNAMIC_VDB = SmartTestDesignerSuite.getTestDataFile(getClass(), DYNAMIC_VDBS + File.separator + "product-view-vdb.xml");

	private File portfolioVdbFile = SmartTestDesignerSuite.getTestDataFile(getClass(), DYNAMIC_VDBS + File.separator + "portfolio-vdb.xml");

	private IFile portfolioVdbEclipseFile;

//	@Before
//	public void setup() {
//	    initMocks(this);
//
//	    final IPath name = mock(Path.class);
//        when(name.
//        portfolioVdbEclipseFile = mock(IFile.class);
//        when(portfolioVdbEclipseFile.getLocation()).thenReturn(name);
//        when(portfolioVdbEclipseFile.getLocation().toFile()).thenReturn(tempFile);
//        when(portfolioVdbEclipseFile.getContents()).thenReturn(fileInputStream);
//	}
//
//    @Test
//	public void testLoad_1() throws Exception {
//		Collection<String> modelNames = new ArrayList<String>();
//		modelNames.add("MarketData");
//		modelNames.add("Accounts");
//		modelNames.add("PersonalValuations");
//		modelNames.add("Stocks");
//		modelNames.add("StocksMatModel");
//		
//		DynamicVdb vdb = new DynamicVdb(portfolioVdbFile);
//		
//		assertNotNull(vdb.getName());
//		assertEquals(5, vdb.getDynamicModels().size());
//		
//		for( DynamicModel model : vdb.getDynamicModels()) {
//			assertTrue( modelNames.contains(model.getName()) );
//			if( model.getName().equalsIgnoreCase("Stocks")) {
//				assertNotNull(model.getMetadata());
//				assertEquals(Type.DDL, model.getMetadata().getType());
//				System.out.println("CDATA for model = " + model.getName() + "\n\n" +
//						model.getMetadata().getSchemaText() );
//			}
//		}
//		
//		
//	}
//	
//	@Test
//	public void testExport_1() throws Exception {
//		DynamicVdb vdb = new DynamicVdb(portfolioVdbFile);
//		vdb.export(null);
//		
//	}
//	
//	@Test
//	public void testExport_2() throws Exception {
//        IPath fullPath = new Path(portfolioVdbFile.getAbsolutePath());
//
//        IPath locationPath = fullPath.removeLastSegments(1);
//        IPath tempPath = locationPath.append("DynamicVdbTest-vdb.xml");
//		DynamicVdb vdb = new DynamicVdb("DynamicVdbTest");
//		vdb.setFilePath(tempPath);
//		
//		DynamicModel model = new DynamicModel("TestModel_1");
//		model.setMetadata(new Metadata("CREATE VIEW ABCDEFG (C1 integer, C2 integer) AS SELECT * FROM TABLE_B;", "DDL"));
//		model.setModelType("VIRTUAL");
//		vdb.addDynamicModel(model);
//
//		vdb.export(null);
//	}

}
