/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.ui;

import java.util.ResourceBundle;

import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IPageLayout;

import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.PluginUtilImpl;
import com.metamatrix.modeler.ui.actions.IModelerActionConstants;

/**
 * UiConstants
 * @since 4.0
 */
public interface UiConstants {
    //============================================================================================================================
	// Constants
    
    /**
     * The ID of the plug-in containing this constants class.
     * @since 4.0
     */
    String PLUGIN_ID = "com.metamatrix.modeler.ui"; //$NON-NLS-1$ 

    /**
     * Contains private constants used by other constants within this class.
     * @since 4.0
     */
    class PC {
        private static final String BUNDLE_NAME = PLUGIN_ID + ".i18n"; //$NON-NLS-1$
        private static final String WIZARDS     = PLUGIN_ID + ".wizards."; //$NON-NLS-1$
        private static final String VIEWS       = PLUGIN_ID + ".views."; //$NON-NLS-1$
        private static final String EXPLORER    = PLUGIN_ID + ".explorer"; //$NON-NLS-1$
        private static final String SEARCH      = PLUGIN_ID + ".search."; //$NON-NLS-1$
        private static final String FAVORITES   = PLUGIN_ID + ".favorites."; //$NON-NLS-1$
    }
    
    /**
     * Provides access to the plugin's log and to it's resources.
     * @since 4.0
     */
    PluginUtil Util = new PluginUtilImpl(PLUGIN_ID, PC.BUNDLE_NAME, ResourceBundle.getBundle(PC.BUNDLE_NAME));

    //============================================================================================================================
    // Extension constants
    
    /**
     * Constants related to extensions, including all extension ID's.
     * @since 4.0
     */
    interface Extensions {
        // Perspectives
        String PERSPECTIVE     = PLUGIN_ID + ".modelerPerspective"; //$NON-NLS-1$
        String OUTPUT_FOLDER   = PLUGIN_ID + ".outputFolder"; //$NON-NLS-1$
        String PROPERTY_FOLDER = PLUGIN_ID + ".propertyFolder"; //$NON-NLS-1$
        String TREE_FOLDER     = PLUGIN_ID + ".treeFolder"; //$NON-NLS-1$
        String CENTER_LEFT_FOLDER   = PLUGIN_ID + ".centerLeftFolder"; //$NON-NLS-1$

        // Wizards
        String NEW_MODEL_WIZARD   = PC.WIZARDS + "newModel"; //$NON-NLS-1$
        String NEW_PROJECT_WIZARD = PC.WIZARDS + "newModelProject"; //$NON-NLS-1$
        String NEW_VDB_WIZARD     = "com.metamatrix.modeler.internal.vdb.ui.wizards.newVdbWizard"; //$NON-NLS-1$
        String NEW_FOLDER_WIZARD  = "org.eclipse.ui.wizards.new.folder"; //$NON-NLS-1$

        // Views
        String DATATYPE_HIERARCHY_VIEW = PC.VIEWS + "datatypeHierarchyView"; //$NON-NLS-1$
        String DESCRIPTION_VIEW        = PC.VIEWS + "descriptionView"; //$NON-NLS-1$
        String METAMODELS_VIEW         = PC.VIEWS + "metamodelsView"; //$NON-NLS-1$
        String ERROR_LOG_VIEW          = PC.VIEWS + "logView"; //$NON-NLS-1$
        String TAGS_VIEW               = PC.VIEWS + "tagsView"; //$NON-NLS-1$
        String FAVORITES_VIEW_ID       = PC.FAVORITES + "favoritesView"; //$NON-NLS-1$
		//        String PROBLEM_VIEW_ID         = PC.VIEWS + "problemView"; //$NON-NLS-1$
        String OUTLINE_VIEW            = IPageLayout.ID_OUTLINE;
        String PROPERTY_VIEW           = IPageLayout.ID_PROP_SHEET;
        String SEARCH_RESULT_VIEW      = NewSearchUI.SEARCH_VIEW_ID;
        String SYSTEM_CATALOG_VIEW     = PC.VIEWS + "systemModelView"; //$NON-NLS-1$
        String ECLIPSE_CHEAT_SHEET_VIEW = "org.eclipse.ui.cheatsheets.views.CheatSheetView";//$NON-NLS-1$
        
        // Preference Pages
        String MODELER_PREFERENCE_PAGE = PLUGIN_ID + ".preferences.modelerPreferencePage"; //$NON-NLS-1$
        
        // Model Editor Pages
        String TABLE_EDITOR = "tableEditorPage"; //$NON-NLS-1$
        String TEXT_EDITOR  = "textEditorPage"; //$NON-NLS-1$
        
        // Views from other plugins
//        String NAVIGATOR_VIEW = "com.metamatrix.modeler.relationship.ui.navigation.navigationView"; //$NON-NLS-1$
        
        // Search
        String METADATA_SEARCH_PAGE = PC.SEARCH + "metadataSearchPage"; //$NON-NLS-1$
        /**
         * Constants related to extensions that apply to the model explorer.
         * @since 4.0
         */
        interface Explorer {        
            // Explorer components
            String VIEW         = PC.EXPLORER + ".view"; //$NON-NLS-1$
            String DECORATOR    = PC.EXPLORER + ".decorator"; //$NON-NLS-1$
            String CONTEXT_MENU = PC.EXPLORER + IModelerActionConstants.ContextMenu.MENU_ID_SUFFIX;
        }
        
        // DQP UI
        String PREVIEW_DATA_ACTION_ID = "com.metamatrix.modeler.internal.dqp.ui.actions.PreviewTableDataContextAction"; //$NON-NLS-1$
    }

    //============================================================================================================================
    // Extension Point constants

    /**
     * Constants related to extension points, including all extension point ID's and extension point schema component names.
     * @since 4.0
     */
    interface ExtensionPoints {
        
        /** Constants for the DiagramContentProvider extension point */
        interface DiagramContentProvider {
            String DIAGRAM_ID                    = "com.metamatrix.modeler.ui.diagramContentProvider";   //$NON-NLS-1$
            String DIAGRAM_CLASS                 = "class"; //$NON-NLS-1$
            String DIAGRAM_CLASSNAME             = "name"; //$NON-NLS-1$
            String DIAGRAM_TYPE          = "diagramType";  //$NON-NLS-1$
            String DIAGRAM_TYPE_NAME     = "name"; //$NON-NLS-1$
        }
        
        /** Constants for the DiagramLabelProvider extension point */
        interface DiagramLabelProvider {
            String DIAGRAM_ID                    = "com.metamatrix.modeler.ui.diagramLabelProvider";   //$NON-NLS-1$
            String DIAGRAM_CLASS                 = "class"; //$NON-NLS-1$
            String DIAGRAM_CLASSNAME             = "name"; //$NON-NLS-1$
            String DIAGRAM_TYPE          = "diagramType";  //$NON-NLS-1$
            String DIAGRAM_TYPE_NAME     = "name"; //$NON-NLS-1$
        }
        
        /** Constants for the ExtendedModelContentProvider extension point */
        interface ExtendedModelContentProvider {
            String EXTENDED_MODEL_ID                    = "com.metamatrix.modeler.ui.extendedModelContentProvider";   //$NON-NLS-1$
            String EXTENDED_MODEL_CLASS                 = "class"; //$NON-NLS-1$
            String EXTENDED_MODEL_CLASSNAME             = "name"; //$NON-NLS-1$
        }
        
        /** Constants for the ExtendedModelLabelProvider extension point */
        interface ExtendedModelLabelProvider {
            String EXTENDED_MODEL_ID                    = "com.metamatrix.modeler.ui.extendedModelLabelProvider";   //$NON-NLS-1$
            String EXTENDED_MODEL_CLASS                 = "class"; //$NON-NLS-1$
            String EXTENDED_MODEL_CLASSNAME             = "name"; //$NON-NLS-1$
        }

        /** Constants for the exportWizards extension point */
        interface ExportWizards {
//            String ID = "com.metamatrix.modeler.ui.exportWizards"; //$NON-NLS-1$
            String ID = "org.eclipse.ui.exportWizards"; //$NON-NLS-1$
            // Attributes
            String CLASS = "class"; //$NON-NLS-1$
            String ICON  = "icon"; //$NON-NLS-1$
            String NAME  = "name"; //$NON-NLS-1$
            String ID_ID = "id"; //$NON-NLS-1$
        }

        /** Constants for the importWizards extension point */
        interface ImportWizards {
//            String ID = "com.metamatrix.modeler.ui.importWizards"; //$NON-NLS-1$
            String ID = "org.eclipse.ui.importWizards"; //$NON-NLS-1$
            // Attributes
            String CLASS = "class"; //$NON-NLS-1$
            String ICON  = "icon"; //$NON-NLS-1$
            String NAME  = "name"; //$NON-NLS-1$
            String ID_ID = "id"; //$NON-NLS-1$
            interface ContributionIds {
                String JDBC = "com.metamatrix.modeler.internal.jdbc.ui.wizards.jdbcImportWizard"; //$NON-NLS-1$
                String TEXT = "com.metamatrix.modeler.tools.textimport.ui.wizards.ImportTextWizard"; //$NON-NLS-1$
                String XSD = "com.metamatrix.modeler.internal.xsd.ui.wizards.xsdFileSystemImportWizard"; //$NON-NLS-1$
                String WSDL = "com.metamatrix.modeler.internal.webservice.ui.wizard.wsdlFileSystemImportWizard"; //$NON-NLS-1$
            }
        }

        /** Constants for the ModelEditorPage extension point */
        interface ModelEditorPage {
            String ID                    = "com.metamatrix.modeler.ui.modelEditorPage"; //$NON-NLS-1$
            String CLASS_ELEMENT         = "class";  //$NON-NLS-1$
            String CLASSNAME             = "name"; //$NON-NLS-1$
            String CONTRIBUTOR_CLASSNAME = "contributorName"; //$NON-NLS-1$
            String TABNAME               = "tabName"; //$NON-NLS-1$
            String ORDER                 = "order"; //$NON-NLS-1$
        }

        /** Constants for the ModelObjectActionContributor extension point */
        interface ModelObjectActionContributor {
            String ID                    = "com.metamatrix.modeler.ui.modelObjectActionContributor"; //$NON-NLS-1$
            String CLASS_ELEMENT         = "class";  //$NON-NLS-1$
            String CLASSNAME             = "name"; //$NON-NLS-1$
        }

        /** Constants for the ModelObjectEditorPage extension point */
        interface ModelObjectEditorPage {
            String ID                    = "com.metamatrix.modeler.ui.modelObjectEditorPage"; //$NON-NLS-1$
            String CLASS_ELEMENT         = "class";  //$NON-NLS-1$
            String CLASSNAME             = "name"; //$NON-NLS-1$
        }

        /** Constants for the NewAssociationWizard extension point */
        // NewModelObjectWizardManager depends on all constands besides ID being same as NewModelObjectWizard
        interface NewAssociationWizard {
            String ID                    = "com.metamatrix.modeler.ui.newAssociationWizard"; //$NON-NLS-1$
            String CLASS_ELEMENT         = NewModelObjectWizard.CLASS_ELEMENT; 
            String CLASSNAME             = NewModelObjectWizard.CLASSNAME;
            String DESCRIPTOR_ELEMENT    = NewModelObjectWizard.DESCRIPTOR_ELEMENT;
            String DESCRIPTOR_ID         = NewModelObjectWizard.DESCRIPTOR_ID;
        }

        /** Constants for the NewModelObjectWizard extension point */
        interface NewModelObjectWizard {
            String ID                    = "com.metamatrix.modeler.ui.newModelObjectWizard"; //$NON-NLS-1$
            String CLASS_ELEMENT         = "class";  //$NON-NLS-1$
            String CLASSNAME             = "name"; //$NON-NLS-1$
            String DESCRIPTOR_ELEMENT    = "descriptorType";  //$NON-NLS-1$
            String DESCRIPTOR_ID         = "name"; //$NON-NLS-1$
        }
        
        /** Constants for the NewModelWizardContributor extension point */
        interface NewModelWizardContributor {
            String ID                    = "com.metamatrix.modeler.ui.newModelWizardContributor"; //$NON-NLS-1$
            String CLASS                 = "class"; //$NON-NLS-1$
            String ICON                  = "icon"; //$NON-NLS-1$
            String NAME                  = "name"; //$NON-NLS-1$
            String METAMODEL             = "metamodelDescriptorType"; //$NON-NLS-1$
            String IS_VIRTUAL            = "isVirtual"; //$NON-NLS-1$
            String IS_PHYSICAL           = "isPhysical"; //$NON-NLS-1$
            String ANY                   = "all"; //$NON-NLS-1$
        }

        /** Constants for the NewChildAction extension point */
        interface NewChildExtension {
            String ID                    = "com.metamatrix.modeler.ui.newChildAction"; //$NON-NLS-1$
            String CLASS                 = "class"; //$NON-NLS-1$
            String CLASSNAME             = "name"; //$NON-NLS-1$
        }
        
        /** Constants for the NewSiblingAction extension point */
        interface NewSiblingExtension {
            String ID                    = "com.metamatrix.modeler.ui.newSiblingAction"; //$NON-NLS-1$
            String CLASS                 = "class"; //$NON-NLS-1$
            String CLASSNAME             = "name"; //$NON-NLS-1$
        }
        
		/** Constants for the NewChildAction extension point */
		interface DiagramHelperExtension {
			String ID                    = "com.metamatrix.modeler.ui.diagramHelper"; //$NON-NLS-1$
			String CLASS                 = "class"; //$NON-NLS-1$
			String CLASSNAME             = "name"; //$NON-NLS-1$
			String DIAGRAM             	 = "diagram"; //$NON-NLS-1$
			String DIAGRAM_TYPE          = "diagramType"; //$NON-NLS-1$
		}
		
		/** Constants for the NewChildAction extension point */
		interface ModelObjectEditHelperExtension {
			String ID                    = "com.metamatrix.modeler.ui.modelObjectEditHelper"; //$NON-NLS-1$
			String CLASS                 = "class"; //$NON-NLS-1$
			String CLASSNAME             = "name"; //$NON-NLS-1$
		}

        interface MetadataPasteSpecialExtension {
            String ID                    = "com.metamatrix.modeler.ui.metadataPasteSpecial"; //$NON-NLS-1$
            String CLASS                 = "class"; //$NON-NLS-1$
            String CLASSNAME             = "classname"; //$NON-NLS-1$
            String LABEL                 = "label"; //$NON-NLS-1$
            String DESCRIPTION           = "description"; //$NON-NLS-1$
        }

        interface MetadataRefreshExtension {
            String ID                    = "com.metamatrix.modeler.ui.metadataRefresh"; //$NON-NLS-1$
            String CLASS                 = "class"; //$NON-NLS-1$
            String CLASSNAME             = "classname"; //$NON-NLS-1$
            String LABEL                 = "label"; //$NON-NLS-1$
            String DESCRIPTION           = "description"; //$NON-NLS-1$
        }
        
        interface PropertyEditorFactoryExtension {
            String ID                    = "com.metamatrix.modeler.ui.propertyEditorFactory"; //$NON-NLS-1$
            String CLASS                 = "class"; //$NON-NLS-1$
            String CLASSNAME             = "name"; //$NON-NLS-1$
        }
        
        interface ModelerPerspectiveContributorExtension {
            String ID                    = "com.metamatrix.modeler.ui.modelerPerspectiveContributor"; //$NON-NLS-1$
            String CLASS                 = "class"; //$NON-NLS-1$
            String CLASSNAME             = "name"; //$NON-NLS-1$
        }
        
        interface ModelResourceActionExtension {
            String ID                    = "com.metamatrix.modeler.ui.modelResourceAction"; //$NON-NLS-1$
            String RESOURCE_ACTION       = "resourceAction"; //$NON-NLS-1$
            String CLASSNAME             = "name"; //$NON-NLS-1$
            String LABEL				 = "label"; //$NON-NLS-1$
        }
        
        interface ModelerSpecialActionExtension {
            String ID                    = "com.metamatrix.modeler.ui.modelObjectSpecialAction"; //$NON-NLS-1$
            String SPECIAL_ACTION        = "specialAction"; //$NON-NLS-1$
            String CLASSNAME             = "name"; //$NON-NLS-1$
            String LABEL				 = "label"; //$NON-NLS-1$
        }

        interface GeneralPreferenceContributor {
            String ID = "generalPreferenceContributor"; //$NON-NLS-1$
            String CLASS_ELEMENT = "class";  //$NON-NLS-1$
            String CLASS_NAME = "name"; //$NON-NLS-1$
            String CATEGORY_ELEMENT = "category"; //$NON-NLS-1$
            String CATEGORY_ID_ATTRIBUTE = "id"; //$NON-NLS-1$
            String CATEGORY_NAME_ATTRIBUTE = "name"; //$NON-NLS-1$
            String PREFERENCE_ELEMENT = "preference"; //$NON-NLS-1$
            String PREFERENCE_ID_ATTRIBUTE = "id"; //$NON-NLS-1$
            String PREFERENCE_CATEGORY_ID_ATTRIBUTE = "categoryId"; //$NON-NLS-1$
            String PREFERENCE_CONTRIBUTOR_CLASS_ATTRIBUTE = "class"; //$NON-NLS-1$
        }
        
        interface VetoableShutdownListener {
            String ID = "vetoableShutdownListener"; //$NON-NLS-1$
            String CLASS_ELEMENT = "class";  //$NON-NLS-1$
            String CLASS_ATTRIBUTE = "name"; //$NON-NLS-1$
        }
    }
	
    //============================================================================================================================
	// Debug constants

    /**
     * Contains debug contexts defined by this plug-in.
	 * @since 4.0
	 */
    interface Debug {    
    }
    
    //============================================================================================================================
    // Product Capabilities constants

    interface ProductInfo {
        String PRODUCT = "Modeler"; //$NON-NLS-1$
        String VERSION = "5.5"; //$NON-NLS-1$
        String DELIMETER = "/"; //$NON-NLS-1$

        
        interface Capabilities {
            String VIRTUAL_MODELING = "Views"; //$NON-NLS-1$
            String RELATIONAL_VIRTUAL_MODELING = VIRTUAL_MODELING + DELIMETER + "Relational"; //$NON-NLS-1$
            String XML_VIRTUAL_MODELING = VIRTUAL_MODELING + DELIMETER + "XML"; //$NON-NLS-1$
            
            String IMPORT = "Importer"; //$NON-NLS-1$
            String JDBC_IMPORT = IMPORT + DELIMETER + "JDBC"; //$NON-NLS-1$
            String RATIONAL_ROSE_IMPORT = IMPORT + DELIMETER + "Rational Rose"; //$NON-NLS-1$
            String ERWIN_IMPORT = IMPORT + DELIMETER + "ERwin"; //$NON-NLS-1$
            
            String EXPORT = "Exporter"; //$NON-NLS-1$
            String RDBMS_EXPORT = EXPORT + DELIMETER + "RDBMS"; //$NON-NLS-1$
            
            String MODELGEN = "Models"; //$NON-NLS-1$
            String RELATIONAL_MODELGEN = MODELGEN + DELIMETER + "Relational"; //$NON-NLS-1$
            String RELATIONAL_FROM_UML_MODELGEN = MODELGEN + DELIMETER + "UML"; //$NON-NLS-1$
            
            String REPOSITORY = "Repository"; //$NON-NLS-1$
            
            String PRODUCT_DQP = "Query Engine"; //$NON-NLS-1$
            String CONNECTOR = "Connector"; //$NON-NLS-1$
            String CONNECTOR_JDBC = CONNECTOR + DELIMETER + "JDBC"; //$NON-NLS-1$
            String CONNECTOR_TEXT = CONNECTOR + DELIMETER + "TEXT"; //$NON-NLS-1$
            String CONNECTOR_LIBRADOS = CONNECTOR + DELIMETER + "LIBRADOS"; //$NON-NLS-1$
        }
    }
    
    interface Navigation {
        String TAB_INDEX            = "tabIndex"; //$NON-NLS-1$ 
        String SELECTION            = "selection"; //$NON-NLS-1$ 
        String CURRENT_INPUT        = "currentInput";  //$NON-NLS-1$
        String CURRENT_SELECTION    = "currentSelection";  //$NON-NLS-1$
        String MARKER_TYPE          = "markerType";  //$NON-NLS-1$
        String NAVIGATION           = "navigation";  //$NON-NLS-1$
        String UNKNOWN              = "unknown";  //$NON-NLS-1$
        String PAGE_EDITOR          = "pageEditor";  //$NON-NLS-1$
        String DELEGATE             = "delegate";  //$NON-NLS-1$
        String DELEGATES_MARKER     = "delegatesMarker";  //$NON-NLS-1$
    
    }
    
    interface ObjectEditor {
        int IGNORE_OPEN_EDITOR          = 0;
        int FORCE_CLOSE_EDITOR          = 1;
        int FORCE_OPEN_EDITOR           = 2;
        int REFRESH_EDITOR_IF_OPEN      = 3;
    }
    
    /**
     * Constants used in the various Modeler Search Pages search result markers. 
     * @since 4.2
     */
    interface SearchAttributes {
        String ACTUAL_OBJECT = PC.SEARCH + "actualObject"; //$NON-NLS-1$
        String DATATYPE_ID = PC.SEARCH + "datatypeId"; //$NON-NLS-1$
        String DATATYPE_NAME = PC.SEARCH + "datatypeName"; //$NON-NLS-1$
        String DESCRIPTION = PC.SEARCH + "description"; //$NON-NLS-1$
        String ENTITY_FULL_NAME = PC.SEARCH + "entityFullName"; //$NON-NLS-1$
        String ENTITY_NAME = PC.SEARCH + "entityName"; //$NON-NLS-1$
        String ENTITY_URI = PC.SEARCH + "entityUri"; //$NON-NLS-1$
        String ENTITY_UUID = PC.SEARCH + "entityUuid"; //$NON-NLS-1$
        String GROUP_BY_KEY = PC.SEARCH + "groupByKey"; //$NON-NLS-1$
        String METACLASS_URI = PC.SEARCH + "metaclassUri"; //$NON-NLS-1$
        String PROPERTIES = PC.SEARCH + "properties"; //$NON-NLS-1$
        String RESOURCE_PATH = PC.SEARCH + "resourcePath"; //$NON-NLS-1$
        String RUNTIME_TYPE = PC.SEARCH + "runtimeType"; //$NON-NLS-1$
    }

    interface TableEditorAttributes {
        String COLUMN_ORDER = "columnOrder"; //$NON-NLS-1$
    }
    
    public static final String DESCRIPTION_KEY = Util.getString("ModelObjectTableModel.descriptionColumnName"); //$NON-NLS-1$
    public static final String LOCATION_KEY = Util.getString("ModelObjectTableModel.locationColumnName"); //$NON-NLS-1$        

    interface PartEventID {
        int OPENED_ID          = 0;
        int CLOSED_ID          = 1;
        int CHANGED_ID         = 2;
        int ACTIVATED_ID       = 3;
        int DEACTIVATED_ID     = 4;
        int SAVED_AS_ID        = 5;
        int BROUGHT_TO_TOP_ID  = 6;
        int HIDDEN_ID          = 7;
        int VISIBLE_ID         = 8;
        int INPUT_CHANGED_ID   = 9;
        
        String[] Names = { 
            "opened",       //$NON-NLS-1$ 
            "closed",       //$NON-NLS-1$ 
            "changed",      //$NON-NLS-1$ 
            "activated",    //$NON-NLS-1$ 
            "deactived",    //$NON-NLS-1$ 
            "saved as",     //$NON-NLS-1$ 
            "brought to top",//$NON-NLS-1$ 
            "hidden",       //$NON-NLS-1$ 
            "visible",      //$NON-NLS-1$ 
            "input changed" //$NON-NLS-1$ 
        };
    }
    
    interface NamingAttributes {
    	// List of invalid characters for project naming
    	char[] INVALID_PROJECT_CHARS = {'[',']','{','}','%','#','&','$','+',',',';','=','@','!','~','^'};
    }
}
