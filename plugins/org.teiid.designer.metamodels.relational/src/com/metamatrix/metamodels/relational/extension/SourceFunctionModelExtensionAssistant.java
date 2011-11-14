/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relational.extension;

import org.teiid.designer.core.extension.EmfModelObjectExtensionAssistant;

import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.relational.RelationalPlugin;
import com.metamatrix.modeler.core.workspace.ModelResource;

public class SourceFunctionModelExtensionAssistant extends EmfModelObjectExtensionAssistant {

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
                assert (modelResource != null) : "superclass is not checking for null model resource"; //$NON-NLS-1$
                return (ModelType.VIRTUAL == modelResource.getModelType().getValue()); // must be a virtual model
            }
        } catch (Exception e) {
            RelationalPlugin.Util.log(e);
        }

        return super.supportsMedOperation(proposedOperationName, context);
    }

}
