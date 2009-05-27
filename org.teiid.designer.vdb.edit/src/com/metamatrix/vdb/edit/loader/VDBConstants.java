/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.edit.loader;

import com.metamatrix.common.config.xml.XMLConfig_ElementNames;
import com.metamatrix.core.vdb.VdbConstants;

public interface VDBConstants {

    /**
    * Property defines the vdb file to load
    */
    public static final String VDB_DEF_FILE_NAME = "vdb.file.name"; //$NON-NLS-1$

    // this is an optional parameter that provides an
    // alternate location to find the models, other than
    // the current directory
    //public static final String VDB_MODEL_DIR = "vdb.model.dir"; //$NON-NLS-1$


  public static final String VDB_DEF_FILE_EXTENSION = VdbConstants.VDB_DEF_FILE_EXTENSION; 
      
  public static final String VDB_ARCHIVE_FILE_EXTENSION = VdbConstants.VDB_ARCHIVE_EXTENSION;

  public static class VDBElementNames {
      public static final String ELEMENT = "VDB"; //$NON-NLS-1$

      /**
      * The VDBInfo contains the properties related
      * to the setup of the VDB
      */
      public static class VDBInfo {
          public static final String ELEMENT = "VDBInfo"; //$NON-NLS-1$
          public static class Properties {
              public static final String NAME = "Name"; //$NON-NLS-1$
              public static final String ARCHIVE_NAME = "VDBArchiveName"; //$NON-NLS-1$

              
              public static final String VERSION = "Version"; //$NON-NLS-1$
              
              // Optional - defaults to VDB Name
              public static final String DESCRIPTION = "Description"; //$NON-NLS-1$
              // Optional - defaults to VDB Name
              public static final String GUID = "GUID"; //$NON-NLS-1$
              // Optional - defaults to false
              public static final String ISACTIVE = "IsActive"; //$NON-NLS-1$
              // Optional - defaults to true
              public static final String IS_USER_DEFINED = "IsUserDefined"; //$NON-NLS-1$


          }
      }

      /**
       * The VDBInfo contains the properties related
       * to the execution of the VDB
       */
      public static class ExecutionProperties{
          public static final String ELEMENT = "ExecutionProperties"; //$NON-NLS-1$
          public static class Properties {
              public static final String TXN_AUTO_WRAP = "txnAutoWrap";    //$NON-NLS-1$
          }
      }
      
      /**
      * The Model contains the properties related
      * to the setup a model with the VDB.  One or more models
      * can be defined
      */
      public static class Model {
          public static final String ELEMENT = "Model"; //$NON-NLS-1$
          public static class Properties {
              public static final String NAME = "Name"; //$NON-NLS-1$
              public static final String VERSION = "Version";    //$NON-NLS-1$
           	  public static final String VERSION_DATE = "VersionDate";               //$NON-NLS-1$
              public static final String MODEL_TYPE = "ModelType"; //$NON-NLS-1$
              public static final String URI = "ModelURI"; //$NON-NLS-1$
           
              public static final String FILENAME = "FileName"; //$NON-NLS-1$
//              public static final String ISPHYSICAL = "IsPhysical"; //$NON-NLS-1$
              // Optional - no binding set
              public static final String CONNECTOR_BINDING_NAME = "ConnectorBindingName"; //$NON-NLS-1$
              // Optional - Default - physical=false, virtual=true
              public static final String VISIBILITY = "Visibility"; //$NON-NLS-1$
              public static final String MULTI_SOURCE_ENABLED = "MultiSourceEnabled"; //$NON-NLS-1$
             
          }
          
          public static class ConnectorBindings {
              public static final String ELEMENT = XMLConfig_ElementNames.Configuration.ConnectorComponents.ELEMENT;
          }
          
          public static class ComponentTypes {
                public static final String ELEMENT = XMLConfig_ElementNames.ComponentTypes.ELEMENT; 
            }

          /**
           * This is an element reference for processing.  The configuration helper
           * will manage the contents of the element.
           */
          public static class ComponentType {
          	public static final String ELEMENT = XMLConfig_ElementNames.ComponentTypes.ComponentType.ELEMENT; 
          }
          
          /**
           * This is an element reference for processing.  The configuration helper
           * will manage the contents of the element.
           */          
          public static class ConnectorBinding {
          	public static final String ELEMENT = XMLConfig_ElementNames.Configuration.ConnectorComponents.ConnectorComponent.ELEMENT;           	
          	
          }   	
      }
      
      
    
      
      
      public static class Property {
          public static final String ELEMENT = "Property"; //$NON-NLS-1$
      	  public static class Attributes {
           	public static final String NAME = "Name"; //$NON-NLS-1$
           	public static final String VALUE = "Value"; //$NON-NLS-1$
    	
      	  }
      }
  }

  public static class Visibility {
      public static final String PUBLIC = "Public"; //$NON-NLS-1$
      public static final String PRIVATE = "Private"; //$NON-NLS-1$
  }

} 
