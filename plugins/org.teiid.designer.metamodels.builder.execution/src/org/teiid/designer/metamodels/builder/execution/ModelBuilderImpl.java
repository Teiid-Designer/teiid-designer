/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.builder.execution;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.metamodels.builder.ModelBuilder;
import org.teiid.designer.metamodels.builder.ModelRecord;
import org.teiid.designer.metamodels.builder.execution.util.MetamodelBuilderUtil;
import org.teiid.designer.metamodels.core.CorePackage;
import org.teiid.designer.metamodels.core.ModelAnnotation;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.metamodels.core.extension.ExtensionPackage;
import org.teiid.designer.metamodels.core.extension.XPackage;
import org.teiid.designer.metamodels.relational.RelationalPackage;


/**
 * Implementation of the ModelBuilder - creates a Resource given a ModelRecord or List of records.
 *
 * @since 8.0
 */
public class ModelBuilderImpl implements ModelBuilder, MetamodelBuilderConstants {
    private final ResourceSet eResourceSet;
    private final MultiStatus status;
    private boolean builderDebugEnabled = false;

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(ModelBuilderImpl.class);

    private static String getString( final String id,
                                     final Object param1,
                                     final Object param2 ) {
        return UTIL.getString(I18N_PREFIX + id, param1, param2);
    }

    // ==================================================================================
    // C O N S T R U C T O R S
    // ==================================================================================
    /**
     * Constructor
     * 
     * @param - theContainer - ResourceSet containing all resources (Internal Resoruces should already be added)
     * @param - status - The MultiStatus to use to accumulate all warnings and errors
     */
    public ModelBuilderImpl( final MultiStatus status,
                             final ResourceSet resourceSet ) {
        CoreArgCheck.isNotNull(resourceSet);
        CoreArgCheck.isNotNull(status);
        this.eResourceSet = resourceSet;
        this.status = status;
    }

    /**
     * Interface method - Create a Model using the given ModelRecord informations
     * 
     * @see org.teiid.designer.metamodels.builder.ModelBuilder#create(org.teiid.designer.metamodels.builder.ModelRecord)
     * @param - ModelRecord - the record to use to drive creation - May not be null
     * @since 4.3
     */
    @Override
	public Resource create( ModelRecord record ) {
        CoreArgCheck.isNotNull(record);

        // Construct absolute model path from record
        String absoluteModelPath = getModelAbsolutePath(record);

        // Check if a resource with this URI already exists in the resource set.
        // If the resource already exists, return it - dont flag as error.
        URI uri = URI.createFileURI(absoluteModelPath);
        if (this.eResourceSet.getResource(uri, false) != null) {
            return this.eResourceSet.getResource(uri, false);
        }

        // Create the empty resource
        Resource eResource = this.eResourceSet.createResource(uri);

        // Set resource using record details
        initModelResource(eResource, record);

        // Add newly created resource to the resource set
        this.eResourceSet.getResources().add(eResource);

        return eResource;
    }

    /**
     * Interface method - Create a Model using the given ModelRecord informations
     * 
     * @see org.teiid.designer.metamodels.builder.ModelBuilder#create(org.teiid.designer.metamodels.builder.ModelRecord)
     * @param - ModelRecord - the record to use to drive creation - May not be null
     * @since 4.3
     */
    @Override
	public List create( List records ) {
        CoreArgCheck.isNotNull(records);

        List resources = new ArrayList(records.size());
        Iterator iter = records.iterator();
        while (iter.hasNext()) {
            Resource resource = create((ModelRecord)iter.next());
            if (resource != null) {
                resources.add(resource);
            }
        }

        return resources;
    }

    // ==================================================================================
    // H E L P E R M E T H O D S
    // ==================================================================================

    // Helper to use to get an absolute path from a record
    private String getModelAbsolutePath( ModelRecord record ) {
        String locationPath = record.getLocationPath();
        String modelName = record.getModelName();
        String absolutePath = locationPath + PATH_SEPARATOR + modelName + MODEL_EXT;
        return absolutePath;
    }

    // Helper to initialize a resource
    private void initModelResource( Resource modelResource,
                                    ModelRecord record ) {
        // Get the details of the model to be created.
        String modelType = record.getModelType();
        String modelSubType = record.getModelSubType();
        String modelNameInSource = record.getModelNameInSource();
        String modelDescription = record.getModelDescription();
        String extensionPackage = record.getExtensionPackage();

        // Create ModelAnnotation
        EFactory factory = CorePackage.eINSTANCE.getEFactoryInstance();
        ModelAnnotation annot = (ModelAnnotation)factory.create(CorePackage.eINSTANCE.getModelAnnotation());

        // --------------------------------
        // Relational Model
        // --------------------------------
        if (RELATIONAL_STR.equalsIgnoreCase(modelType)) {
            // Set primary metamodel type
            annot.setPrimaryMetamodelUri(RelationalPackage.eNS_URI);
            // Set subType
            if (PHYSICAL_STR.equalsIgnoreCase(modelSubType)) {
                annot.setModelType(ModelType.PHYSICAL_LITERAL);
            } else if (VIRTUAL_STR.equalsIgnoreCase(modelSubType)) {
                annot.setModelType(ModelType.VIRTUAL_LITERAL);
            }
            // --------------------------------
            // Relationship Model
            // --------------------------------
        } else if (EXTENSION_STR.equalsIgnoreCase(modelType)) {
            // Set primary metamodel type
            annot.setPrimaryMetamodelUri(ExtensionPackage.eNS_URI);
            // Set subType
            annot.setModelType(ModelType.EXTENSION_LITERAL);
        }

        // Set Model NameInSource
        annot.setNameInSource(modelNameInSource);
        // Set Model Description
        annot.setDescription(modelDescription);

        // If the record has an extension package entry... look up the
        // XPackage and set that info on the ModelAnnotation
        if (extensionPackage != null) {
            final Object xPkg = MetamodelBuilderUtil.findEObjectByPath(eResourceSet, extensionPackage, null, status);
            if (xPkg != null && xPkg instanceof XPackage) {
                annot.setExtensionPackage((XPackage)xPkg);
            } else {
                // Found wrong entity... log error
                final String msg = getString("noXPkg", extensionPackage, modelResource.getURI().lastSegment()); //$NON-NLS-1$
                MetamodelBuilderUtil.addStatus(status, IStatus.ERROR, msg);
                if (this.builderDebugEnabled) {
                    MetamodelBuilderExecutionPlugin.Util.log(IStatus.ERROR, msg);
                }
            }
        }

        // Add ModelAnnotation
        modelResource.getContents().add(annot);
    }

}
