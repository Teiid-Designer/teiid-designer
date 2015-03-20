/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.relational.extension;

import org.eclipse.core.resources.IResource;
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.extension.EmfModelObjectExtensionAssistant;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.extension.ExtensionConstants;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;
import org.teiid.designer.extension.registry.ModelExtensionRegistry;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.metamodels.relational.Procedure;
import org.teiid.designer.metamodels.relational.RelationalPackage;
import org.teiid.designer.metamodels.relational.RelationalPlugin;
import org.teiid.designer.metamodels.relational.extension.RestModelExtensionConstants.PropertyIds;

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
                if (PropertyIds.REST_METHOD.equals(propId) || PropertyIds.URI.equals(propId) || PropertyIds.CHARSET.equals(propId) || PropertyIds.HEADERS.equals(propId) || PropertyIds.DESCRIPTION.equals(propId)) {
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
    
    public static RestModelExtensionAssistant getRestAssistant() {
    	final ModelExtensionRegistry registry = ExtensionPlugin.getInstance().getRegistry();
        final String prefix = RestModelExtensionConstants.NAMESPACE_PROVIDER.getNamespacePrefix();
        return (RestModelExtensionAssistant)registry.getModelExtensionAssistant(prefix);
    }
    
    public static boolean setRestProperties(EObject procedure, String restMethod, String restUri, String restCharSet, String restHeaders, String restDescription) {
        final RestModelExtensionAssistant assistant = getRestAssistant();
        if( assistant != null ) {
			try {
				assistant.setPropertyValue(procedure, RestModelExtensionConstants.PropertyIds.URI, restUri);
				assistant.setPropertyValue(procedure, RestModelExtensionConstants.PropertyIds.REST_METHOD, restMethod);
				assistant.setPropertyValue(procedure, RestModelExtensionConstants.PropertyIds.CHARSET, restCharSet);
				assistant.setPropertyValue(procedure, RestModelExtensionConstants.PropertyIds.HEADERS, restHeaders);
				assistant.setPropertyValue(procedure, RestModelExtensionConstants.PropertyIds.DESCRIPTION, restDescription);
			} catch (Exception e) {
				RelationalPlugin.Util.log(e);
				return false;
			}
        }
        
        return true;
    }
    
    public static boolean setRestProperty(EObject procedure, String id, String value) {
        final RestModelExtensionAssistant assistant = getRestAssistant();
        if( assistant != null ) {
			try {
				if( RestModelExtensionConstants.PropertyIds.URI.equals(id)) {
					assistant.setPropertyValue(procedure, RestModelExtensionConstants.PropertyIds.URI, value);
				} else if( RestModelExtensionConstants.PropertyIds.REST_METHOD.equals(id)) {
					assistant.setPropertyValue(procedure, RestModelExtensionConstants.PropertyIds.REST_METHOD, value);
				} else if( RestModelExtensionConstants.PropertyIds.CHARSET.equals(id)) {
					assistant.setPropertyValue(procedure, RestModelExtensionConstants.PropertyIds.CHARSET, value);
				} else if( RestModelExtensionConstants.PropertyIds.HEADERS.equals(id)) {
					assistant.setPropertyValue(procedure, RestModelExtensionConstants.PropertyIds.HEADERS, value);
				} else if( RestModelExtensionConstants.PropertyIds.DESCRIPTION.equals(id)) {
					assistant.setPropertyValue(procedure, RestModelExtensionConstants.PropertyIds.DESCRIPTION, value);
				}
			} catch (Exception e) {
				RelationalPlugin.Util.log(e);
				return false;
			}
        }
        
        return true;
    }
    
    public static String getRestProperty(EObject procedure, String key) {
    	String result = null;
    	final RestModelExtensionAssistant assistant = getRestAssistant();
        if( assistant != null ) {
			try {
				result = assistant.getPropertyValue(procedure, key);
			} catch (Exception e) {
				RelationalPlugin.Util.log(e);
			}
        }
        
        return result;
    }
}
