/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.extension.rest;

import static com.metamatrix.modeler.dqp.DqpPlugin.Util;

import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.core.extension.EmfModelObjectExtensionAssistant;

import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.modeler.core.workspace.ModelResource;

/**
 * 
 */
public class RestModelExtensionAssistant extends EmfModelObjectExtensionAssistant {

    /**
     * @param modelObject the model object whose REST extension properties are being removed from (cannot be <code>null</code>)
     * @throws Exception if there is a problem accessing the model object's model resource
     */
    public void removeRestProperties( EObject modelObject ) throws Exception {
        removeProperty(modelObject, RestModelExtensionConstants.PropertyIds.REST_METHOD);
        removeProperty(modelObject, RestModelExtensionConstants.PropertyIds.URI);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.definition.ModelExtensionAssistant#supportsMedOperation(java.lang.String, java.lang.Object)
     */
    @Override
    public boolean supportsMedOperation( String proposedOperationName,
                                         Object context ) {
        try {
            if (MedOperations.ADD_MED_TO_MODEL.equals(proposedOperationName)
                    && super.supportsMedOperation(proposedOperationName, context)) {
                ModelResource modelResource = getModelResource(context);
                assert (modelResource != null) : "model resource is null"; //$NON-NLS-1$
                return (ModelType.VIRTUAL == modelResource.getModelType().getValue()); // must be a virtual model
            }
        } catch (Exception e) {
            Util.log(e);
        }

        return super.supportsMedOperation(proposedOperationName, context);
    }

}
