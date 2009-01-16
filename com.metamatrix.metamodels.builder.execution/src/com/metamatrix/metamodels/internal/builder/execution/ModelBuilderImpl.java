package com.metamatrix.metamodels.internal.builder.execution;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.metamodels.builder.DebugConstants;
import com.metamatrix.metamodels.builder.MetamodelBuilderPlugin;
import com.metamatrix.metamodels.builder.ModelRecord;
import com.metamatrix.metamodels.builder.ModelBuilder;
import com.metamatrix.metamodels.builder.execution.MetamodelBuilderConstants;
import com.metamatrix.metamodels.builder.execution.MetamodelBuilderExecutionPlugin;
import com.metamatrix.metamodels.core.CorePackage;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.core.extension.ExtensionPackage;
import com.metamatrix.metamodels.core.extension.XPackage;
import com.metamatrix.metamodels.internal.builder.execution.util.MetamodelBuilderUtil;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.metamodels.relationship.RelationshipPackage;

/** 
 * Implementation of the ModelBuilder - creates a Resource given a ModelRecord or
 * List of records.
 */
public class ModelBuilderImpl implements ModelBuilder, MetamodelBuilderConstants {
	private final ResourceSet eResourceSet;
    private final MultiStatus status;
    private boolean builderDebugEnabled = false;

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(ModelBuilderImpl.class);
	
    private static String getString(final String id, final Object param1, final Object param2) {
        return UTIL.getString(I18N_PREFIX  + id, param1, param2);
    }
    
	// ==================================================================================
    //                        C O N S T R U C T O R S
    // ==================================================================================
    /**
     * Constructor
     * @param - theContainer - ResourceSet containing all resources (Internal Resoruces
     *                          should already be added)
     * @param - status - The MultiStatus to use to accumulate all warnings and errors                          
     */
    public ModelBuilderImpl(final MultiStatus status, final ResourceSet resourceSet) {
        ArgCheck.isNotNull(resourceSet);
        ArgCheck.isNotNull(status);
        this.eResourceSet = resourceSet;
        this.status = status;
    }
    
    /**
     * Interface method - Create a Model using the given ModelRecord informations 
     * @see com.metamatrix.metamodels.builder.ModelBuilder#create(com.metamatrix.metamodels.builder.ModelRecord)
     * @param - ModelRecord - the record to use to drive creation - May not be null
     * @since 4.3
     */
	public Resource create(ModelRecord record) {
        ArgCheck.isNotNull(record);
		this.builderDebugEnabled = MetamodelBuilderPlugin.Util.isDebugEnabled(DebugConstants.METAMODEL_BUILDER);
        
        // Construct absolute model path from record
        String absoluteModelPath = getModelAbsolutePath(record);
        
        // Check if a resource with this URI already exists in the resource set.
        // If the resource already exists, return it - dont flag as error.
        URI uri = URI.createFileURI(absoluteModelPath);
        if (this.eResourceSet.getResource(uri, false) != null) {
// Case 4857 - Allows usage of existing model
//          // If resource already exists, add error status and return.
//            final String msg = getString("modelExistsErr", uri.lastSegment() ); //$NON-NLS-1$
//            MetamodelBuilderUtil.addStatus(status, IStatus.ERROR, msg);
//            if(this.builderDebugEnabled) {
//              MetamodelBuilderExecutionPlugin.Util.log(IStatus.ERROR, msg);
//            }
//            return null;
            return this.eResourceSet.getResource(uri,false);
        }
        
        // Create the empty resource
        Resource eResource = this.eResourceSet.createResource(uri);

        // Set resource using record details
        initModelResource(eResource,record);
        
        // Add newly created resource to the resource set
        this.eResourceSet.getResources().add(eResource);
                
        return eResource;
	}
	
    /**
     * Interface method - Create a Model using the given ModelRecord informations 
     * @see com.metamatrix.metamodels.builder.ModelBuilder#create(com.metamatrix.metamodels.builder.ModelRecord)
     * @param - ModelRecord - the record to use to drive creation - May not be null
     * @since 4.3
     */
	public List create(List records) {
        ArgCheck.isNotNull(records);
        
        List resources = new ArrayList(records.size());
        Iterator iter = records.iterator();
        while(iter.hasNext()) {
        	Resource resource = create( (ModelRecord)iter.next() );
        	if(resource!=null) {
        		resources.add( resource );
        	}
        }
            
        return resources;
	}
    
    
    // ==================================================================================
    //                        H E L P E R   M E T H O D S
    // ==================================================================================   
	
    //Helper to use to get an absolute path from a record
	private String getModelAbsolutePath(ModelRecord record) {
		String locationPath = record.getLocationPath();
		String modelName = record.getModelName();
		String absolutePath = locationPath + PATH_SEPARATOR + modelName + MODEL_EXT;
		return absolutePath;
	}
	
    //Helper to initialize a resource
	private void initModelResource(Resource modelResource, ModelRecord record) {
        // Get the details of the model to be created.
        String modelType = record.getModelType();
        String modelSubType = record.getModelSubType();
        String modelNameInSource = record.getModelNameInSource();
        String modelDescription = record.getModelDescription();
        String extensionPackage = record.getExtensionPackage();
		
        // Create ModelAnnotation 
        EFactory factory = CorePackage.eINSTANCE.getEFactoryInstance();
        ModelAnnotation annot = (ModelAnnotation)factory.create(CorePackage.eINSTANCE.getModelAnnotation());
        
        //--------------------------------
        // Relational Model
        //--------------------------------
        if(RELATIONAL_STR.equalsIgnoreCase(modelType)) {
        	// Set primary metamodel type
            annot.setPrimaryMetamodelUri(RelationalPackage.eNS_URI);
            // Set subType
            if(PHYSICAL_STR.equalsIgnoreCase(modelSubType)) {
                annot.setModelType(ModelType.PHYSICAL_LITERAL);
            } else if (VIRTUAL_STR.equalsIgnoreCase(modelSubType)) {
            	annot.setModelType(ModelType.VIRTUAL_LITERAL);
            }
        //--------------------------------
        // Relationship Model
        //--------------------------------
        } else if (RELATIONSHIP_STR.equalsIgnoreCase(modelType)) {
        	// Set primary metamodel type
            annot.setPrimaryMetamodelUri(RelationshipPackage.eNS_URI);
            // Set subType
            annot.setModelType(ModelType.PHYSICAL_LITERAL);
        //--------------------------------
        // Extension Model
        //--------------------------------
        }else if(EXTENSION_STR.equalsIgnoreCase(modelType)) {
            // Set primary metamodel type
            annot.setPrimaryMetamodelUri(ExtensionPackage.eNS_URI);
            // Set subType
            annot.setModelType(ModelType.EXTENSION_LITERAL);
        }
        
        // Set Model NameInSource
        annot.setNameInSource(modelNameInSource);
        // Set Model Description
        annot.setDescription(modelDescription);
        
        //If the record has an extension package entry... look up the 
        //XPackage and set that info on the ModelAnnotation
        if(extensionPackage != null) {
            final Object xPkg = MetamodelBuilderUtil.findEObjectByPath(eResourceSet, extensionPackage, null, status);
            if(xPkg != null && xPkg instanceof XPackage) {
                annot.setExtensionPackage( (XPackage)xPkg);
            }else {
                //Found wrong entity... log error
                final String msg = getString("noXPkg", extensionPackage, modelResource.getURI().lastSegment() ); //$NON-NLS-1$
                MetamodelBuilderUtil.addStatus(status, IStatus.ERROR, msg);
                if(this.builderDebugEnabled) {
        	        MetamodelBuilderExecutionPlugin.Util.log(IStatus.ERROR, msg);
                }
            }
        }
        
        // Add ModelAnnotation
        modelResource.getContents().add(annot);
	}
	        
}
