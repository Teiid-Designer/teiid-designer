/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.extension;

import static com.metamatrix.modeler.dqp.ui.DqpUiConstants.UTIL;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.extension.ExtensionConstants.MedOperations;

import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.metamodels.relational.Procedure;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper;

/**
 * 
 */
public class RemoveRestWarPropertiesAction extends RestWarPropertiesAction {

    private static final String PREFIX = I18nUtil.getPropertyPrefix(RemoveRestWarPropertiesAction.class);

    private boolean hasDeprecatedProperties;

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.ui.extension.RestWarPropertiesAction#getErrorMessage()
     */
    @Override
    protected String getErrorMessage() {
        return UTIL.getString(PREFIX + "errorRemovingRestExtensionProperties"); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.ui.extension.RestWarPropertiesAction#getSuccessfulMessage()
     */
    @Override
    protected String getSuccessfulMessage() {
        return UTIL.getString(PREFIX + "restExtensionPropertiesRemoved"); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.ui.extension.RestWarPropertiesAction#getTransactionName()
     */
    @Override
    protected String getTransactionName() {
        return UTIL.getString(PREFIX + "removeRestfulTransactionName"); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.ui.extension.RestWarPropertiesAction#isValidSelection(com.metamatrix.metamodels.relational.Procedure)
     */
    @Override
    protected boolean isValidSelection( Procedure procedure ) {
        try {
            // check for existence of new extension framework properties
            if (getNewAssistant().supportsMedOperation(MedOperations.DELETE_MED_FROM_MODEL, getModelResource().getResource())) {
                return true;
            }

            // check for old 7.4 extension properties
            if (getOldAssistant().hasOldRestProperties(procedure)) {
                this.hasDeprecatedProperties = true;
                return true;
            }
        } catch (Exception e) {
            UTIL.log(e);
        }

        this.hasDeprecatedProperties = false;
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.ui.extension.RestWarPropertiesAction#runImpl(com.metamatrix.metamodels.relational.Procedure)
     */
    @Override
    protected void runImpl( Procedure procedure ) throws Exception {
        // delete MED if necessary
        if (!this.hasDeprecatedProperties) {
            getNewAssistant().removeModelExtensionDefinition(procedure);
        }

        // delete REST extension properties from all procedures
        Collection<EObject> eObjects = getModelResource().getEObjects();

        for (EObject eObject : eObjects) {
            if (SqlAspectHelper.isProcedure(eObject)) {
                if (this.hasDeprecatedProperties) {
                    getOldAssistant().removeOldRestProperties(eObject);
                } else {
                    getNewAssistant().removeRestProperties(eObject);
                }
            }
        }
    }

}
