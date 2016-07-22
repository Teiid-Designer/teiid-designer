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
package org.teiid.runtime.client.admin;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.teiid.adminapi.Model;
import org.teiid.adminapi.impl.DataPolicyMetadata;
import org.teiid.adminapi.impl.DataPolicyMetadata.PermissionMetaData;
import org.teiid.adminapi.impl.ModelMetaData;
import org.teiid.adminapi.impl.SourceMappingMetadata;
import org.teiid.adminapi.impl.VDBImportMetadata;
import org.teiid.adminapi.impl.VDBMetaData;
import org.teiid.adminapi.impl.VDBTranslatorMetaData;
import org.teiid.designer.runtime.spi.ITeiidServer;

/**
 * Visitor that will walk a vdb node tree and convert it to
 * the dynamic vdb xml syntax.
 */
public class VdbManifestGenerator implements StringConstants {

    /**
     * Companion XML tag for permission condition
     */
    public static String DATA_ROLE_PERMISSION_CONDITION_XML = "condition"; //$NON-NLS-1$

    private static final String UNDEFINED = "undefined"; //$NON-NLS-1$
    
    private interface ElementTabValue {
        int VIRTUAL_DATABASE = 0;
        int VDB_PROPERTY = 1;
        int DESCRIPTION = 1;
        int CONNECTION_TYPE = 1;
        int IMPORT_VDB = 1;
        
        int MODEL = 1;
        int MODEL_PROPERTY = 2;
        int MODEL_DESCRIPTION = 2;
        int MODEL_METADATA = 2;
        int MODEL_VALIDATION = 2;
        int MODEL_SOURCE = 2;
        
        int TRANSLATOR = 1;
        int TRANSLATOR_PROPERTY = 2;
        
        int DATA_ROLE = 1;
        int DATA_ROLE_DESCRIPTION = 2;
        int PERMISSION = 2;
        int MAPPED_ROLE_NAME = 2;
        int RESOURCE_NAME = 3;
        int PERMISSION_ALLOW = 3;
        int CONDITION = 3;
        int MASK = 3;
        
        int ENTRY = 1;
        int ENTRY_PROPERTY = 2;
        int ENTRY_DESCRIPTION = 2;
    }

    private final StringWriter strWriter;
    private XMLStreamWriter writer;
    private final ITeiidServer teiidServer;
    private final VDBMetaData vdb;

    /**
     * Create new visitor that writes to the given xml stream writer
     *
     * @param version teiid version
     * @param writer output for the xml
     */
    public VdbManifestGenerator(ITeiidServer server, VDBMetaData vdb) {
        super();
        this.teiidServer = server;
        this.vdb = vdb;
        this.strWriter = new StringWriter();
        this.writer = null;
        try {
			final XMLOutputFactory xof = XMLOutputFactory.newInstance();
			this.writer = xof.createXMLStreamWriter(strWriter);
	        
	        virtualDatabase(vdb);
	        
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}

    }
    
    public String getManifest() {
    	if( strWriter != null ) {
    		return this.strWriter.toString();
    	}
    	
    	return EMPTY_STRING;
    }

    protected String undefined() {
        return UNDEFINED;
    }

    private void writeNewLine(int total) throws XMLStreamException {
        for (int i = 0; i < total; ++i)
            writer.writeCharacters(NEW_LINE);
    }

    private void writeNewLine() throws XMLStreamException {
        writeNewLine(1);
    }
    
    private void writeTab(int total) throws XMLStreamException {
    	for (int i = 0; i < total; ++i)
    		writer.writeCharacters(TAB);
    }

    private void writeStartDocument() throws XMLStreamException {
        writer.writeStartDocument("UTF-8", "1.0"); //$NON-NLS-1$ //$NON-NLS-2$
        writeNewLine();
    }

    private void writeStartElement(String tag) throws XMLStreamException {
        writer.writeStartElement(tag);
    }

    private void writeAttribute(String name, String value) throws XMLStreamException {
        writer.writeAttribute(name, value);
    }

    private void writeCData(String data) throws XMLStreamException {
        writer.writeCData(data);
    }

    private void writeCharacters(String characters) throws XMLStreamException {
        writer.writeCharacters(characters);
    }

    private void writeEndElement() throws XMLStreamException {
        writer.writeEndElement();
        writeNewLine();
    }

    private void writeElementWithText(String name, String text) throws XMLStreamException {
        writeStartElement(name);
        writeCharacters(text);
        writeEndElement();
    }

    private void writeEndDocument() throws XMLStreamException {
        writer.writeEndDocument();
        writer.close();
    }

//    private boolean isPrimaryNodeType(Node node, NodeTypeName nodeTypeName) throws RepositoryException {
//        NodeType nodeType = node.getPrimaryNodeType();
//        return nodeTypeName.getId().equals(nodeType.getName());
//    }

//    protected Property property(Node node, String propName) {
//        if (node == null || propName == null)
//            return null;
//    
//        if (! node.hasProperty(propName))
//            return null;
//    
//        Property property = node.getProperty(propName);
//        return property;
//    }

    private void properties(int numTabs, Properties exportableProps) throws XMLStreamException {

        for( Object key : exportableProps.keySet() ) {
        	String name = (String)key;
            String value = (String)exportableProps.getProperty(name);

            writeTab(numTabs);
            writeStartElement(VdbLexicon.ManifestIds.PROPERTY);
            writeAttribute(VdbLexicon.ManifestIds.NAME, name);
            writeAttribute(VdbLexicon.ManifestIds.VALUE, value);
            writeEndElement();
        }
    }

    private void mask(PermissionMetaData permission ) throws XMLStreamException {
    	String mask = permission.getMask();
    	if( isNotEmpty(mask) ) {
	        writeTab(ElementTabValue.MASK);
	        // Condition element
	        writeStartElement(VdbLexicon.ManifestIds.MASK);
	
	        if( permission.getOrder()  > 0 ) {
	        	writeAttribute(VdbLexicon.ManifestIds.ORDER, Integer.toString(permission.getOrder()));
	        }
	
	        writeCharacters(mask);
	        writeEndElement();
    	}
    }

    private void condition(PermissionMetaData permission) throws XMLStreamException {
    	String condition = permission.getCondition();
    	if( isNotEmpty(condition) ) {
	        // Condition element
	        writeTab(ElementTabValue.CONDITION);
	        writeStartElement(VdbLexicon.ManifestIds.CONDITION);
	
	        if( ! permission.getConstraint() ) {
	        	writeAttribute(VdbLexicon.ManifestIds.CONSTRAINT, Boolean.toString(false));
	        }
	
	        writeCharacters(condition);
	        writeEndElement();
    	}
    }

    private void permission(PermissionMetaData permission) throws XMLStreamException {

        // Permission element
//        writeNewLine();
        writeTab(ElementTabValue.PERMISSION);
        writeStartElement(VdbLexicon.ManifestIds.PERMISSION);

        // Resource name element
        writeNewLine();
        writeTab(ElementTabValue.RESOURCE_NAME);
        writeElementWithText(VdbLexicon.ManifestIds.RESOURCE_NAME, permission.getResourceName());

        writeTab(ElementTabValue.PERMISSION_ALLOW);
        try {
			writeElementWithText(VdbLexicon.ManifestIds.ALLOW_ALTER, Boolean.toString(permission.getAllowAlter()));
		} catch (NullPointerException e) {
			// NPE can be ignored
			writeElementWithText(VdbLexicon.ManifestIds.ALLOW_ALTER, Boolean.toString(false));
		}
        writeTab(ElementTabValue.PERMISSION_ALLOW);
        try {
			writeElementWithText(VdbLexicon.ManifestIds.ALLOW_CREATE, Boolean.toString(permission.getAllowCreate()));
		} catch (NullPointerException e) {
			// NPE can be ignored
			writeElementWithText(VdbLexicon.ManifestIds.ALLOW_CREATE, Boolean.toString(false));
		}
        writeTab(ElementTabValue.PERMISSION_ALLOW);
        try {
			writeElementWithText(VdbLexicon.ManifestIds.ALLOW_DELETE, Boolean.toString(permission.getAllowDelete()));
		} catch (NullPointerException e) {
			// NPE can be ignored
			writeElementWithText(VdbLexicon.ManifestIds.ALLOW_DELETE, Boolean.toString(false));
		}
        writeTab(ElementTabValue.PERMISSION_ALLOW);
        try {
			writeElementWithText(VdbLexicon.ManifestIds.ALLOW_EXECUTE, Boolean.toString(permission.getAllowExecute()));
		} catch (NullPointerException e) {
			// NPE can be ignored
			writeElementWithText(VdbLexicon.ManifestIds.ALLOW_EXECUTE, Boolean.toString(false));
		}
        writeTab(ElementTabValue.PERMISSION_ALLOW);
        try {
			writeElementWithText(VdbLexicon.ManifestIds.ALLOW_READ, Boolean.toString(permission.getAllowRead()));
		} catch (NullPointerException e) {
			// NPE can be ignored
			writeElementWithText(VdbLexicon.ManifestIds.ALLOW_READ, Boolean.toString(false));
		}
        writeTab(ElementTabValue.PERMISSION_ALLOW);
        try {
			writeElementWithText(VdbLexicon.ManifestIds.ALLOW_UPDATE, Boolean.toString(permission.getAllowUpdate()));
		} catch (NullPointerException e) {
			// NPE can be ignored
			writeElementWithText(VdbLexicon.ManifestIds.ALLOW_UPDATE, Boolean.toString(false));
		}
        writeTab(ElementTabValue.PERMISSION_ALLOW);
        try {
			writeElementWithText(VdbLexicon.ManifestIds.ALLOW_LANGUAGE, Boolean.toString(permission.getAllowLanguage()));
		} catch (NullPointerException e) {
			// NPE can be ignored
			writeElementWithText(VdbLexicon.ManifestIds.ALLOW_LANGUAGE, Boolean.toString(false));
		}

        // Conditions
        condition(permission);

        // Masks
        mask(permission);

        // End Permission
        writeTab(ElementTabValue.PERMISSION);
        writeEndElement();
    }

    private void dataRole(List<DataPolicyMetadata> dataPolicies) throws XMLStreamException {
    	for( DataPolicyMetadata policy : dataPolicies ) {
	        // Data Role element
	        writeTab(ElementTabValue.DATA_ROLE);
	        writeStartElement(VdbLexicon.ManifestIds.DATA_ROLE);
	
	        // Process data role attributes
	        writeAttribute(VdbLexicon.ManifestIds.NAME, policy.getName());

	        writeAttribute(VdbLexicon.ManifestIds.ANY_AUTHENTICATED, Boolean.toString(policy.isAnyAuthenticated()));
	        writeAttribute(VdbLexicon.ManifestIds.ALLOW_CREATE_TEMP_TABLES, Boolean.toString(policy.isAllowCreateTemporaryTables()));
	        writeAttribute(VdbLexicon.ManifestIds.GRANT_ALL, Boolean.toString(policy.isGrantAll()));
	
	        writeNewLine();
	        
	        description(policy.getDescription(), ElementTabValue.DATA_ROLE_DESCRIPTION);
	
	        // Permissions
	        for( PermissionMetaData permission : policy.getPermissionMap().values()) {
	        	permission(permission);
	        }
	
	        // Mapped Role Names
	        for ( String mrName : policy.getMappedRoleNames()) {
	            writeTab(ElementTabValue.MAPPED_ROLE_NAME);
	            writeElementWithText(VdbLexicon.ManifestIds.MAPPED_ROLE_NAME, mrName);
	        }
	        writeTab(ElementTabValue.DATA_ROLE);
	        writeEndElement();
    	}
    }

    private void translator(List<VDBTranslatorMetaData> translators) throws XMLStreamException {
    	for( VDBTranslatorMetaData translator : translators ) {

	        // Translator element
	        writeTab(ElementTabValue.TRANSLATOR);
	        writeStartElement(VdbLexicon.ManifestIds.TRANSLATOR);
	
	        // Process translator attributes
	        writeAttribute(VdbLexicon.ManifestIds.NAME, translator.getName());
	        writeAttribute(VdbLexicon.ManifestIds.TYPE, translator.getType());
	        if( translator.getDescription() != null ) {
	        	writeAttribute(VdbLexicon.ManifestIds.DESCRIPTION, translator.getDescription());
	        }
	
	        writeNewLine();
	
	        // Process property attributes
	        // TODO: ADD TRANSLATOR PROPERTIES
//	        Properties exportableProps = filterExportableProperties(node.getProperties(), VdbLexicon.Translator.TYPE, VdbLexicon.Translator.DESCRIPTION);
//	        properties(node, ElementTabValue.TRANSLATOR_PROPERTY, exportableProps);
	        
	        writeTab(ElementTabValue.TRANSLATOR);
	        writeEndElement();
    	}
    }

    private void source( SourceMappingMetadata source) throws XMLStreamException {
        // Translator element
        writeTab(ElementTabValue.MODEL_SOURCE);
        writeStartElement(VdbLexicon.ManifestIds.SOURCE);

        // Process source attributes
        writeAttribute(VdbLexicon.ManifestIds.NAME, source.getName());
        writeAttribute(VdbLexicon.ManifestIds.TRANSLATOR_NAME, source.getTranslatorName());
        writeAttribute(VdbLexicon.ManifestIds.JNDI_NAME, source.getConnectionJndiName());

        writeEndElement();
    }

    private void model(ModelMetaData model) throws XMLStreamException {

        writeTab(ElementTabValue.MODEL);
        writeStartElement(VdbLexicon.ManifestIds.MODEL);

        writeAttribute(VdbLexicon.ManifestIds.NAME, model.getName());

        boolean isVirtual = model.getModelType().equals(Model.Type.VIRTUAL);
        
        writeAttribute(VdbLexicon.ManifestIds.TYPE, model.getModelType().toString());

        // If PATH is not null, then it's an EMF-based .vdb file and we throw it away
//        if (isNotEmpty(model.getPath()) ) {
//            writeAttribute(VdbLexicon.ManifestIds.PATH, model.getPath());
//        }

        if (! model.isVisible() ) {
        	// True is the default value so no need to include if true
            writeAttribute(VdbLexicon.ManifestIds.VISIBLE, Boolean.toString(false));
        }

        writeNewLine();
        description(model.getDescription(), ElementTabValue.MODEL_DESCRIPTION);

        // TODO:  HANDLE MODEL PROPERTIES
//        Properties exportableProps = filterExportableProperties(node.getProperties(), CoreLexicon.JcrId.MODEL_TYPE);
//        
//        properties(node, ElementTabValue.MODEL_PROPERTY, exportableProps);
        // Properties elements
        Properties exportableProps = new Properties();
        for(Object key : model.getProperties().keySet() ) {
        	String keyStr = (String)key;
        	if( ! keyStr.equalsIgnoreCase(VdbLexicon.ManifestIds.TYPE) &&
        		! keyStr.equalsIgnoreCase(VdbLexicon.ManifestIds.MODEL_UUID) &&
        		! keyStr.equalsIgnoreCase(VdbLexicon.ManifestIds.MODEL_CLASS) &&
        		! keyStr.equalsIgnoreCase(VdbLexicon.ManifestIds.INDEX_NAME) &&
        		! keyStr.equalsIgnoreCase(VdbLexicon.ManifestIds.BUILT_IN) &&
        		! keyStr.equalsIgnoreCase(VdbLexicon.ManifestIds.IMPORTS)  &&
        		! keyStr.equalsIgnoreCase(VdbLexicon.ManifestIds.CHECKSUM)) {
        		exportableProps.put(keyStr, model.getProperties().get(key));
        	}
        }
        
        properties(ElementTabValue.VDB_PROPERTY, exportableProps);

        // Sources
        for( SourceMappingMetadata source : model.getSourceMappings()) {
        	source(source);
        }

        // Metadata TAB ONLY

        if( model.getSchemaText() != null && model.getSchemaText().length() > 0) {
        	writeTab(ElementTabValue.MODEL_METADATA);
        	writeStartElement(VdbLexicon.ManifestIds.METADATA);
        	writeAttribute(VdbLexicon.ManifestIds.TYPE, "DDL");
          
        	writeNewLine();
        	writeTab(ElementTabValue.MODEL_METADATA + 1);
        	writeCData(model.getSchemaText());

          // end metadata tag
        	writeNewLine();
        	writeTab(ElementTabValue.MODEL_METADATA + 1);
        	writeNewLine();
        	writeTab(ElementTabValue.MODEL_METADATA);
        	writeEndElement();
        } else {
//	        if( isVirtual ) {
				try {
					String ddl = teiidServer.getSchema(vdb.getName(), Integer.toString(vdb.getVersion()), model.getName());
					if( ddl != null ) {
						writeTab(ElementTabValue.MODEL_METADATA);
					    writeStartElement(VdbLexicon.ManifestIds.METADATA);
						writeAttribute(VdbLexicon.ManifestIds.TYPE, "DDL");
						writeNewLine();
						writeTab(ElementTabValue.MODEL_METADATA + 1);
						String filteredDdl = TeiidOptionsUtil.filterUuidsFromOptions(ddl);
						writeCData(filteredDdl);

					  // end metadata tag
						writeNewLine();
						writeTab(ElementTabValue.MODEL_METADATA + 1);
						writeNewLine();
						writeTab(ElementTabValue.MODEL_METADATA);
						writeEndElement();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
//			}
        }

        // End model tag
        writeTab(ElementTabValue.MODEL);
        writeEndElement();
    }

    private void importVdbs(List<VDBImportMetadata> vdbImports) throws XMLStreamException {
    	for( VDBImportMetadata vdbImport : vdbImports ) {
	    	
	        // Import-vdb element
	        writeTab(ElementTabValue.IMPORT_VDB);
	        writeStartElement(VdbLexicon.ManifestIds.IMPORT_VDB);

	        // Process import-vdb attributes
	        writeAttribute(VdbLexicon.ManifestIds.NAME, vdbImport.getName());
	        writeAttribute(VdbLexicon.ManifestIds.VERSION, Integer.toString(vdbImport.getVersion()));
	        writeAttribute(VdbLexicon.ManifestIds.IMPORT_DATA_POLICIES, Boolean.toString(vdbImport.isImportDataPolicies()));
	
	        writeTab(ElementTabValue.IMPORT_VDB);
	        writeEndElement();
    	}
    }

    private void description(String description, int numTabs) throws XMLStreamException {
        if (isNotEmpty(description)) {
	        writeTab(numTabs);
	        writeElementWithText(VdbLexicon.ManifestIds.DESCRIPTION, description);
        }
    }
    
//    private Properties filterExportableProperties(PropertyIterator propIter, String... propertiesToIgnore) {
//        Properties exportableProps = new Properties();
//        List<String> propsToIgnore = Arrays.asList(propertiesToIgnore);
//        
//        while(propIter.hasNext()) {
//            Property property = propIter.nextProperty();
//            String name = property.getName();
//            if (name == null)
//                continue;
//
//            if (propsToIgnore.contains(name))
//                continue;
//
//            // Ignore jcr properties since these are internal to modeshape
//            if (name.startsWith(JcrLexicon.Namespace.PREFIX))
//                continue;
//            
//
//            String value = toString(property);
//
//            //
//            // Ignore modeshape vdb properties as <property> type properties will
//            // not have a vdb prefix but simply be the property name on its own, eg.
//            // UseConnectedMetadata or vdb-property1.
//            //
//            if (name.startsWith(VdbLexicon.Namespace.PREFIX + COLON)) {
//                //
//                // Preview is actually converted into a vdb property so need to special-case
//                // turn it back into a simple property name but we only care if the property
//                // is actually true.
//                //
//                if (name.equals(VdbLexicon.Vdb.PREVIEW) && Boolean.parseBoolean(value)) {
//                    name = VdbLexicon.ManifestIds.PREVIEW;
//                } else {
//                	continue;
//                }
//            }
//            
//
//            
//            exportableProps.put(name, value);
//        }
//            
//        return exportableProps;
//    }

    private void virtualDatabase(VDBMetaData vdb) throws XMLStreamException {
        // Start new document
        writeStartDocument();

        // Vdb element
        writeTab(ElementTabValue.VIRTUAL_DATABASE);
        writeStartElement(VdbLexicon.ManifestIds.VDB);

        // Name attribute
        writeAttribute(VdbLexicon.ManifestIds.NAME, vdb.getName());

        // Version attribute
        writeAttribute(VdbLexicon.ManifestIds.VERSION, Integer.toString(vdb.getVersion()));

        writeNewLine(2);

        // Description element
        description(vdb.getDescription(), ElementTabValue.DESCRIPTION);

        // Connection Type element
        if (vdb.getConnectionType() != null) {
        	writeTab(ElementTabValue.CONNECTION_TYPE);
            writeElementWithText(VdbLexicon.ManifestIds.CONNECTION_TYPE, vdb.getConnectionType().toString());
        }

        // Properties elements
        Properties exportableProps = new Properties();
        for(Object key : vdb.getProperties().keySet() ) {
        	String keyStr = (String)key;
        	if( ! keyStr.equalsIgnoreCase(VdbLexicon.ManifestIds.DESCRIPTION) &&
        		! keyStr.equalsIgnoreCase(VdbLexicon.ManifestIds.CONNECTION_TYPE) &&
        		! keyStr.equalsIgnoreCase(VdbLexicon.ManifestIds.VERSION) && 
        		! keyStr.equalsIgnoreCase(VdbLexicon.ManifestIds.DEPLOYMENT_NAME) ) {
        		if( keyStr.equalsIgnoreCase(VdbLexicon.ManifestIds.PREVIEW)) {
        			String value = vdb.getPropertyValue(keyStr);
        			if( Boolean.FALSE.toString().toUpperCase().equalsIgnoreCase(value)) {
        				continue;
        			}
        		}
        		exportableProps.put(keyStr, vdb.getProperties().get(key));
        	}
        }
        
        properties(ElementTabValue.VDB_PROPERTY, exportableProps);

        writeNewLine();

        //
        // Visit vdb children by name since the xsd demands them in a specific order
        //

        // Import Vdbs
        importVdbs(vdb.getVDBImports());
        
        // Models
    	for( ModelMetaData model : vdb.getModelMetaDatas().values()) {
    		model(model);
    	}

        // Translators
    	List<VDBTranslatorMetaData> overrides = new ArrayList<VDBTranslatorMetaData>();
    	for( VDBTranslatorMetaData override : vdb.getOverrideTranslatorsMap().values() ) {
    		overrides.add(override);
    	}
        translator(overrides);

        // Data Roles
    	List<DataPolicyMetadata> dataroles = new ArrayList<DataPolicyMetadata>();
    	for( DataPolicyMetadata datarole : vdb.getDataPolicyMap().values() ) {
    		dataroles.add(datarole);
    	}
        dataRole(dataroles);
        writeNewLine();

        // Close out the xml document
        writeTab(ElementTabValue.VIRTUAL_DATABASE);
        writeEndElement();
        writeEndDocument();
    }
    
    private boolean isNotEmpty(String str) {
    	return str != null && str.trim().length() > 0;
    }
}
