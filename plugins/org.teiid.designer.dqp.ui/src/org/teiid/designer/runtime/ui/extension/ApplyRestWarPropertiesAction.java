/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.extension;

import static com.metamatrix.modeler.dqp.ui.DqpUiConstants.UTIL;
import org.teiid.designer.extension.definition.ModelExtensionDefinition;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.metamodels.relational.Procedure;

/**
 * Action to apply REST WAR generation extension properties to virtual procedures.
 */
public class ApplyRestWarPropertiesAction extends RestWarPropertiesAction {

    private static final String PREFIX = I18nUtil.getPropertyPrefix(ApplyRestWarPropertiesAction.class);

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.ui.extension.RestWarPropertiesAction#getErrorMessage()
     */
    @Override
    protected String getErrorMessage() {
        return UTIL.getString(PREFIX + "errorApplyingRestExtensionProperties"); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.ui.extension.RestWarPropertiesAction#getSuccessfulMessage()
     */
    @Override
    protected String getSuccessfulMessage() {
        return UTIL.getString(PREFIX + "restExtensionPropertiesSaved"); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.ui.extension.RestWarPropertiesAction#getTransactionName()
     */
    @Override
    protected String getTransactionName() {
        return UTIL.getString(PREFIX + "makeRestfulTransactionName"); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.ui.extension.RestWarPropertiesAction#isValidSelection(com.metamatrix.metamodels.relational.Procedure)
     */
    @Override
    protected boolean isValidSelection( Procedure procedure ) {
        try {
            // check for existence of either new extension framework properties or 7.4 properties
            if (!getNewAssistant().supportsMyNamespace(procedure) && !getOldAssistant().hasOldRestProperties(procedure)) {
                return true;
            }
        } catch (Exception e) {
            UTIL.log(e);
        }

        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.ui.extension.RestWarPropertiesAction#runImpl(com.metamatrix.metamodels.relational.Procedure,
     *      org.teiid.designer.extension.definition.ModelExtensionDefinition)
     */
    @Override
    protected void runImpl( Procedure procedure,
                            ModelExtensionDefinition definition ) throws Exception {
        getNewAssistant().saveModelExtensionDefinition(procedure, definition);
    }

}
