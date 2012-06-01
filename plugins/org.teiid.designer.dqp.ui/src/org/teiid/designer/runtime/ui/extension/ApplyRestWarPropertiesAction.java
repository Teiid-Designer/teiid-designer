/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.extension;

import static com.metamatrix.modeler.dqp.ui.DqpUiConstants.UTIL;
import static org.teiid.designer.runtime.extension.rest.RestModelExtensionConstants.NAMESPACE_PROVIDER;

import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.ExtensionConstants.MedOperations;
import org.teiid.designer.extension.registry.ModelExtensionRegistry;
import org.teiid.designer.runtime.extension.rest.RestModelExtensionAssistant;
import org.teiid.designer.runtime.extension.rest.RestModelExtensionConstants;

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
            if (getNewAssistant().supportsMedOperation(MedOperations.ADD_MED_TO_MODEL, getModelResource().getResource())
                    && !getOldAssistant().hasOldRestProperties(procedure)) {
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
     * @see org.teiid.designer.runtime.ui.extension.RestWarPropertiesAction#runImpl(com.metamatrix.metamodels.relational.Procedure)
     */
    @Override
    protected void runImpl( Procedure procedure ) throws Exception {
        getNewAssistant().saveModelExtensionDefinition(procedure);
    }
    
    public static void applyRestWarProperties(EObject procedure) throws Exception {
    	if( procedure instanceof Procedure) {
			ModelExtensionRegistry registry = ExtensionPlugin.getInstance().getRegistry();
			RestModelExtensionAssistant assistant = (RestModelExtensionAssistant)registry.getModelExtensionAssistant(NAMESPACE_PROVIDER.getNamespacePrefix());
			assistant.saveModelExtensionDefinition(procedure);
    	}
    }
    
    public static void setRestMethod(EObject procedure, String newValue) throws Exception {
    	if( procedure instanceof Procedure) {
			ModelExtensionRegistry registry = ExtensionPlugin.getInstance().getRegistry();
			RestModelExtensionAssistant assistant = (RestModelExtensionAssistant)registry.getModelExtensionAssistant(NAMESPACE_PROVIDER.getNamespacePrefix());
			assistant.setPropertyValue(procedure, RestModelExtensionConstants.PropertyIds.REST_METHOD, newValue);
    	}
    }
    
    public static void setRestUri(EObject procedure, String newValue) throws Exception {
    	if( procedure instanceof Procedure) {
			ModelExtensionRegistry registry = ExtensionPlugin.getInstance().getRegistry();
			RestModelExtensionAssistant assistant = (RestModelExtensionAssistant)registry.getModelExtensionAssistant(NAMESPACE_PROVIDER.getNamespacePrefix());
			assistant.setPropertyValue(procedure, RestModelExtensionConstants.PropertyIds.URI, newValue);
    	}
    }

}
