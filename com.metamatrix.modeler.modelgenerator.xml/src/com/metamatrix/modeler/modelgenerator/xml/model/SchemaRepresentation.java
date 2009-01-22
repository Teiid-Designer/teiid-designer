/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.xml.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.xsd.XSDSchema;

import com.metamatrix.modeler.modelgenerator.xml.wizards.XsdAsRelationalImportWizard;
import com.metamatrix.modeler.schema.tools.model.schema.SchemaObject;
import com.metamatrix.modeler.schema.tools.processing.SchemaUtil;

public class SchemaRepresentation
{
    private List schemaURIs;
    private XSDSchema[] xsdSchemas;
    private Map m_namespaces;
    private List m_defaultNamespaces;
    private List elements; // key: XSDElementDeclaration, value: ElementImpl
    private int m_catalogType;
    private String m_customCatalogName;
    private Object syncObject;
    

    public SchemaRepresentation (Object syncObject)
    {
        this.syncObject = syncObject;
        setSchemaURIs(new ArrayList());
        m_catalogType = XsdAsRelationalImportWizard.NO_CATALOG_VAL;
        m_customCatalogName = null;
        m_namespaces = new HashMap();
        m_defaultNamespaces = new ArrayList();
    }

    //////////////////////////////////////////////
    // Methods to modify the schema resources
    //////////////////////////////////////////////
  
    public void setSchemaURIs(List schemaURIs)
    {
        synchronized(syncObject) {
            // This means that we completely reprocess all the existing schemas
            // when a new one is added, and even reprocess all the existing schemas
            // when one is deleted! Some reprocessing must be done because of potential
            // cross references, but certainly not parsing. However eclipse may be
            // making it easier here. In any case, keep it simple until it is known that
            // this is a performance problem.
        	
        	// 3/20/2006 - It is now known that this is a performace problem
        	// TODO - JD 5/9/2006 Do a comparison of the URI's and only refresh the new
        	// perhaps this involves partitioning the elements into Lists under the URIs.
            this.schemaURIs = schemaURIs;
    //        initialized = false;
            initialize();
        }
    }
   
    
    //////////////////////////////////////////////
    // Methods to access the schema resources
    //////////////////////////////////////////////

   
    public Collection getSchemaURIs()
    {
        synchronized(syncObject) {
        	return schemaURIs;
        }
    }
  
    ////////////////////////////////////////////////////////////
    // Methods to access the table structure
    /////////////////////////////////////////////////////////////


    
    public List getCatalogs()
    {
        synchronized(syncObject) {
            List retVal = null;
            switch(m_catalogType) {
            	case XsdAsRelationalImportWizard.NAMESPACE_CATALOG_VAL:
            	case XsdAsRelationalImportWizard.NO_CATALOG_VAL:	
            		retVal = getDefaultNamespaces();
            		break;
            	case XsdAsRelationalImportWizard.FILENAME_CATALOG_VAL:
            		retVal = getFileNames();
            		break;
            	case XsdAsRelationalImportWizard.CUSTOM_CATALOG_VAL:
            		retVal = new ArrayList(1);
            		retVal.add(m_customCatalogName);
            		break;            	
            	default:
            		retVal = new ArrayList();
            		break;            	            		            	
            }
            return retVal;
        }
    }
    
    private List getDefaultNamespaces() {
    	return m_defaultNamespaces;
    }
    

    
    public SchemaObject[] getRootTables()
    {
        synchronized(syncObject) {
            List roots = new ArrayList();
            for (Iterator iter = elements.iterator(); iter.hasNext(); ) {
                Object otable = iter.next();
                SchemaObject table = (SchemaObject)otable;
                if (table.isCanBeRoot()) {
                    roots.add(table);
                }
            }
            SchemaObject[] retval = new SchemaObject[roots.size()];
            roots.toArray(retval);
            return retval;
        }
    }

    ///////////////////////////////////////////////////////////////////
    // Methods to create the table structure from the schema resources
    ///////////////////////////////////////////////////////////////////
    
    private void initialize()
    {
//        processor.processSchemas(xsdSchemas);
//        m_namespaces = processor.getNamespaces();
//        m_defaultNamespaces = processor.getDefaultNamespaces();
//        elements = processor.getElements();
    }
    


    public Map getNamespaces() {
        synchronized(syncObject) {
            return m_namespaces;
        }
    }
    
    private List getFileNames() {
    		ArrayList retVal = new ArrayList(xsdSchemas.length);
    		for(int i = 0; i < xsdSchemas.length; i++) {
    			retVal.add(SchemaUtil.shortenFileName(xsdSchemas[i].getSchemaLocation()));
    		}
    		return retVal;
	}
    


	public void setCustomCatalogName(String name) {
		m_customCatalogName = name;
		
	}    

	public boolean addElement(SchemaObject element) {
		return elements.add(element);
	}
}
