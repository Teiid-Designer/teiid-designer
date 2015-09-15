/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.vdb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.io.File;
import java.util.Collection;
import org.junit.Test;
import org.teiid.core.util.SmartTestDesignerSuite;
import org.teiid.designer.vdb.VdbConstants;
import org.teiid.designer.vdb.VdbUtil;
import org.teiid.designer.vdb.manifest.MetadataElement;
import org.teiid.designer.vdb.manifest.ModelElement;
import org.teiid.designer.vdb.manifest.VdbElement;

/**
 * 
 */
@SuppressWarnings( "javadoc" )
public class VdbUtilTest implements VdbConstants {
	private File PRODUCT_VIEW_DYNAMIC_VDB = SmartTestDesignerSuite.getTestDataFile(getClass(), "dynamic_vdbs" + File.separator + "product-view-vdb.xml");
	private File PORTVOLIO_DYNAMIC_VDB = SmartTestDesignerSuite.getTestDataFile(getClass(), "dynamic_vdbs" + File.separator + "portfolio-vdb.xml");
	
    @Test
    public void testGetVdbManifestWithXmlString() {
    	StringBuilder sb = new StringBuilder();
    	sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
    	sb.append("<vdb name=\"DynamicProducts\" version=\"1\">");
    	sb.append("<description>Product Dynamic VDB</description>");
    	sb.append("<property name=\"UseConnectorMetadata\" value=\"true\" />");
    	sb.append("<model name=\"ProductsMySQL_Dynamic\">");
    	sb.append("<source name=\"jdbc\" translator-name=\"mysql\" connection-jndi-name=\"java:/ProductsMySQL\"/>");
    	sb.append("</model>");
    	sb.append("<model name=\"ProductViews\" type=\"VIRTUAL\">");
    	sb.append("<metadata type=\"DDL\"><![CDATA[");
    	sb.append("CREATE VIEW PRODUCT_INFO (");
    	sb.append("ID string,");
    	sb.append("name string,");
    	sb.append("type string");
    	sb.append(") AS SELECT  INSTR_ID AS ID, NAME, TYPE");
    	sb.append("FROM ProductsMySQL_Dynamic.PRODUCTS.PRODUCTDATA;");
    	sb.append("]]> </metadata>");
    	sb.append("</model>");
    	sb.append("<model name=\"ProductSummary\" type=\"VIRTUAL\">");
    	sb.append("<metadata type=\"DDL\"><![CDATA[");
    	sb.append("CREATE VIEW PRODUCT_INFO (");
    	sb.append("ID string,");
    	sb.append("name string,");
    	sb.append("type string");
    	sb.append(") AS SELECT  INSTR_ID AS ID, NAME, TYPE");
    	sb.append("FROM ProductsMySQL_Dynamic.PRODUCTS.PRODUCTDATA;");
    	sb.append("]]> </metadata>");
    	sb.append("</model>");
    	sb.append("</vdb>");
    	
    	try {
			VdbElement element = VdbUtil.getVdbManifest(sb.toString());
			assertEquals(3, element.getModels().size());
			
			MetadataElement viewModelMetadata = element.getModels().get(2).getMetadata().get(0);
			assertNotNull(viewModelMetadata);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
	
    @Test
    public void testGetVdbManifestWithDynamicVdbFile_1() {
    	try {
			VdbElement element = VdbUtil.getVdbManifest(PRODUCT_VIEW_DYNAMIC_VDB);
			assertEquals(3, element.getModels().size());
			
			MetadataElement viewModelMetadata = element.getModels().get(2).getMetadata().get(0);
			assertNotNull(viewModelMetadata);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @Test
    public void testGetVdbManifestWithDynamicVdbFile_2() {
    	try {
			VdbElement element = VdbUtil.getVdbManifest(PORTVOLIO_DYNAMIC_VDB);
			assertEquals(5, element.getModels().size());
			
			MetadataElement viewModelMetadata = element.getModels().get(2).getMetadata().get(0);
			assertNotNull(viewModelMetadata);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
