/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.internal.def;


/**
* This class structure mimics the structure of a VDB Definition XML file
* used to define the .vdb, it's models, connector bindings and connector types
* 
* NOTE: This structure is the same used in the configuration file and are meant
*       to match so that the the configuration file as well as the .def file
*       can be used to import connector bindings
*/
public interface VDBDefXMLElementNames {
    
    /**
    * This is used as the delimiter of all XML element names.
    */
    public static final String DELIMITER = "."; //$NON-NLS-1$
    
   
    /**
    * This is the class that represents the ComponentObject Element which contains
    * all of the XML elements that represent a ComponentObject object.
    */
    public static class ComponentObject {
        public static class Attributes {
            public static final String NAME = "Name"; //$NON-NLS-1$
            public static final String COMPONENT_TYPE = "ComponentType"; //$NON-NLS-1$
        }
    }
    
    /**
    * This is the class that represents the Properties Element which contains
    * all of the XML elements that represent a Properties object.
    */
    public static class Properties {
    
        /**
        * This is the name of the Properties Element.
        */
        public static final String ELEMENT = "Properties"; //$NON-NLS-1$
        
        /**
        * This is the class that represents the Property Element which contains
        * all of the XML elements that represent a Property object.
        */
        public static class Property {
        
            /**
            * This is the name of the Property Element.
            */
            public static final String ELEMENT = "Property"; //$NON-NLS-1$
            
            
            /**
            * This class defines the Attributes of the Element class that 
            * contains it. 
            */
            public static class Attributes {
                public static final String NAME = "Name"; //$NON-NLS-1$
            }
            
        }
    }
    
    /**
    * This is the class that represents the ChangeHistory Properties Element which contains
    * all of the XML elements that represent the change information for the object.
    */
    /**
    * This is the class that represents the ID Element which contains
    * all of the XML elements that represent a ID object.
    */
    public static class ID {
        // these are the  shared attributes of all ID Elements
        
        /**
        * This class defines the Attributes of the Element class that 
        * contains it. 
        */
        public static class Attributes {
            public static final String NAME = "Name"; //$NON-NLS-1$
        }
    }
        
             
    
              /**
            * This is the class that represents the ServiceComponentDefns Element which contains
            * all of the XML elements that represent a ServiceComponentDefns object.
            */
            public static class ConnectorComponents {
            
                /**
                * This is the name of the ServiceComponentDefns Element.
                */
                public static final String ELEMENT = "ConnectorBindings"; //$NON-NLS-1$
                
                /**
                * This is the class that represents the ConnectorBinding Element which contains
                * all of the XML elements that represent a ConnectorBinding object.
                */
                public static class ConnectorComponent {
                
                    /**
                    * This is the name of the ConnectorBinding Element.
                    */
                    public static final String ELEMENT = "Connector"; //$NON-NLS-1$
                
                    public static class Attributes extends ComponentObject.Attributes{
                        public static final String QUEUED_SERVICE = "QueuedService"; //$NON-NLS-1$
//                        public static final String IS_ENABLED = "IsEnabled";
                        public static final String ROUTING_UUID = "routingUUID"; //$NON-NLS-1$
                    }
                }
            }
            
 
    
    /**
    * This is the class that represents the ComponentTypeID Element which contains
    * all of the XML elements that represent a ComponentTypeID object.
    */
    public static class ComponentTypeID {
    
        /**
        * This is the name of the ComponentTypeID Element.
        */
        public static final String ELEMENT = "ComponentTypeID"; //$NON-NLS-1$
        
        /**
        * This class defines the Attributes of the Element class that 
        * contains it.  Note that this class just inherits its attributes
        * from its configuration object superclass.
        */
        public static class Attributes extends ID.Attributes {
        }
        
    }
    
    /**
    * This is the class that represents the ComponentTypes Element which contains
    * all of the XML elements that represent a ComponentTypes object.
    */
    public static class ComponentTypes {
    
        /**
        * This is the name of the ComponentTypes Element.
        */
        public static final String ELEMENT = "ComponentTypes"; //$NON-NLS-1$
        
        /**
        * This is the class that represents the ComponentType Element which contains
        * all of the XML elements that represent a ComponentType object.
        */
        public static class ComponentType {
        
            /**
            * This is the name of the ComponentType Element.
            */
            public static final String ELEMENT = "ComponentType"; //$NON-NLS-1$
            
            /**
            * This class defines the Attributes of the Element class that 
            * contains it.
            */
            public static class Attributes {
                public static final String NAME = "Name"; //$NON-NLS-1$
                public static final String PARENT_COMPONENT_TYPE = "ParentComponentType"; //$NON-NLS-1$
                public static final String SUPER_COMPONENT_TYPE = "SuperComponentType"; //$NON-NLS-1$
                public static final String COMPONENT_TYPE_CODE = "ComponentTypeCode"; //$NON-NLS-1$
                public static final String DEPLOYABLE = "Deployable"; //$NON-NLS-1$
                public static final String DEPRECATED = "Deprecated"; //$NON-NLS-1$
                public static final String MONITORABLE = "Monitorable"; //$NON-NLS-1$
            }
            
            
            /**
            * This is the class that represents the ComponentTypeDefn Element which contains
            * all of the XML elements that represent a ComponentTypeDefn object.
            */
            public static class ComponentTypeDefn {
            
                /**
                * This is the name of the ComponentTypeDefn Element.
                */
                public static final String ELEMENT = "ComponentTypeDefn"; //$NON-NLS-1$
                
                /**
                * This class defines the Attributes of the Element class that 
                * contains it.
                */
                public static class Attributes {
                    public static final String DEPRECATED = "Deprecated"; //$NON-NLS-1$
                }
                
                /**
                * This is the class that represents the PropertyDefinition Element which contains
                * all of the XML elements that represent a PropertyDefinition object.
                */
                public static class PropertyDefinition {
                
                    /**
                    * This is the name of the PropertyDefinition Element.
                    */
                    public static final String ELEMENT = "PropertyDefinition"; //$NON-NLS-1$
                    
                    /**
                    * This class defines the Attributes of the Element class that 
                    * contains it.
                    */
                    public static class Attributes {
                        public static final String NAME = "Name"; //$NON-NLS-1$
                        public static final String DISPLAY_NAME = "DisplayName"; //$NON-NLS-1$
                        public static final String SHORT_DESCRIPTION ="ShortDescription"; //$NON-NLS-1$
                        public static final String DEFAULT_VALUE = "DefaultValue"; //$NON-NLS-1$
                        public static final String MULTIPLICITY = "Multiplicity"; //$NON-NLS-1$
                        public static final String PROPERTY_TYPE = "PropertyType"; //$NON-NLS-1$
                        public static final String VALUE_DELIMITER = "ValueDelimiter"; //$NON-NLS-1$
                        public static final String IS_CONSTRAINED_TO_ALLOWED_VALUES = "IsConstrainedToAllowedValues"; //$NON-NLS-1$
                        public static final String IS_EXPERT = "IsExpert"; //$NON-NLS-1$
                        public static final String IS_HIDDEN = "IsHidden"; //$NON-NLS-1$
                        public static final String IS_MASKED = "IsMasked"; //$NON-NLS-1$
                        public static final String IS_MODIFIABLE = "IsModifiable"; //$NON-NLS-1$
                        public static final String IS_PREFERRED = "IsPreferred"; //$NON-NLS-1$
                    }
                    
                    /**
                    * This is the class that represents the AllowedValue Element which contains
                    * all of the XML elements that represent a AllowedValue object.
                    */
                    public static class AllowedValue {
                    
                        /**
                        * This is the name of the AllowedValue Element.
                        */
                        public static final String ELEMENT = "AllowedValue"; //$NON-NLS-1$
                    }
                    
                }
                
            }
        }
    }
        
    
    /**
    * This is the class that represents the Header Element which contains
    * all of the XML elements that represent a Header object.
    */
    public static class Header {
    
        /**
        * This is the name of the Header Element.
        */
        public static final String ELEMENT = "Header"; //$NON-NLS-1$
        
        /**
        * This is the class that represents the UserName Element which contains
        * all of the XML elements that represent a UserName object.
        */
        public static class UserCreatedBy {
        
            /**
            * This is the name of the UserName Element.
            */
            public static final String ELEMENT = VDBDefPropertyNames.USER_CREATED_BY; 
        }
        
        /**
        * This is the class that represents the ApplicationCreatedDate Element which contains
        * all of the XML elements that represent a ApplicationCreatedDate object.
        */
        public static class ApplicationCreatedBy {
        
            /**
            * This is the name of the ApplicationCreatedDate Element.
            */
            public static final String ELEMENT = VDBDefPropertyNames.APPLICATION_CREATED_BY; 
        }
        
        /**
        * This is the class that represents the ApplicationVersionCreatedBy Element which contains
        * all of the XML elements that represent a ApplicationVersionCreatedBy object.
        */
        public static class ApplicationVersionCreatedBy {
        
            /**
            * This is the name of the ApplicationVersionCreatedBy Element.
            */
            public static final String ELEMENT = VDBDefPropertyNames.APPLICATION_VERSION_CREATED_BY;            
        }
        
        /**
        * This is the class that represents the Time Element which contains
        * all of the XML elements that represent a Time object.
        */
        public static class Time {
        
            /**
            * This is the name of the Time Element.
            */
            public static final String ELEMENT = VDBDefPropertyNames.TIME; 
        }
        
        /**
        * This is the class that represents the DocumentTypeVersion Element which contains
        * all of the XML elements that represent a DocumentTypeVersion object.
        */
        public static class VDBVersion {
        
            /**
            * This is the name of the DocumentTypeVersion Element.
            */
            public static final String ELEMENT = VDBDefPropertyNames.VDB_EXPORTER_VERSION; 
        }
        
        /**
        * This is the class that represents the MetaMatrixServerVersion Element which contains
        * all of the XML elements that represent a ProductServiceConfigs object.
        */
        public static class MetaMatrixSystemVersion {
        
            /**
            * This is the name of the MetaMatrixServerVersion Element.
            */
            public static final String ELEMENT = VDBDefPropertyNames.METAMATRIX_SYSTEM_VERSION; 
        }
    }    
    
  
    
    
}   
