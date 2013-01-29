/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.extension.rest;

import org.eclipse.core.resources.IResource;
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.extension.EmfModelObjectExtensionAssistant;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.extension.ExtensionConstants;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.metamodels.relational.Procedure;
import org.teiid.designer.metamodels.relational.RelationalPackage;
import org.teiid.designer.runtime.extension.rest.RestModelExtensionConstants.PropertyIds;

/**
 * 
 *
 * @since 8.0
 */
public class RestModelExtensionAssistant extends EmfModelObjectExtensionAssistant {

    /**
     * Saves the relational MED to a model if necessary.
     * @param model the model being checked (can be <code>null</code>)
     * @throws Exception if there is an error applying MED
     */
    public void applyMedIfNecessary(final IResource model) throws Exception {
        if (model != null) {
            final ModelResource modelResource = ModelerCore.getModelWorkspace().findModelResource(model);

            if (modelResource != null && !modelResource.isReadOnly()) {
                if ((ModelType.VIRTUAL_LITERAL == modelResource.getModelType())
                    && RelationalPackage.eNS_URI.equals(modelResource.getPrimaryMetamodelUri()) && !supportsMyNamespace(model)) {
                    saveModelExtensionDefinition(model);
                }
            }
        }
    }
	
    /**
     * {@inheritDoc}
     *
     * @see org.teiid.designer.core.extension.EmfModelObjectExtensionAssistant#getPropertyDefinition(java.lang.Object, java.lang.String)
     */
    @Override
    protected ModelExtensionPropertyDefinition getPropertyDefinition(final Object modelObject,
                                                                     final String propId) throws Exception {
        CoreArgCheck.isInstanceOf(EObject.class, modelObject);

        // make sure there is a property definition first
        final ModelExtensionPropertyDefinition propDefn = super.getPropertyDefinition(modelObject, propId);

        if (propDefn != null) {
            // must be procedure in a virtual model
            if ((modelObject instanceof Procedure) && ModelUtil.isVirtual(modelObject)) {
                if (PropertyIds.REST_METHOD.equals(propId) || PropertyIds.URI.equals(propId)) {
                    return propDefn;
                }
            }
        }

        return null;
    }

    /**
         * {@inheritDoc}
         *
         * @see org.teiid.designer.core.extension.EmfModelObjectExtensionAssistant#supportsMedOperation(java.lang.String, java.lang.Object)
         */
        @Override
        public boolean supportsMedOperation(String proposedOperationName,
                                            Object context) {
            return ExtensionConstants.MedOperations.SHOW_IN_REGISTRY.equals(proposedOperationName); // only show in registry
        }
}
