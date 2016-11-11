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
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;
import org.teiid.designer.extension.registry.ModelExtensionRegistry;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.metamodels.relational.BaseTable;
import org.teiid.designer.metamodels.relational.RelationalPackage;
import org.teiid.designer.metamodels.relational.extension.InfinispanCacheModelExtensionConstants.PropertyIds;

public class InfinispanCacheModelExtensionAssistant  extends EmfModelObjectExtensionAssistant {

	/**
	 * Saves the relational MED to a model if necessary.
	 * @param model the model being checked (can be <code>null</code>)
	 * @throws Exception if there is an error applying MED
	 */
	public void applyMedIfNecessary(final IResource model) throws Exception {
		if (model != null) {
			final ModelResource modelResource = ModelerCore.getModelWorkspace().findModelResource(model);

			if (modelResource != null && !modelResource.isReadOnly()) {
				if ((ModelType.PHYSICAL_LITERAL == modelResource.getModelType())
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

		// must be table in a physical model
		if (propDefn != null && ModelUtil.isPhysical(modelObject)) {
			if (modelObject instanceof BaseTable) {
				if (PropertyIds.PRIMARY_TABLE.equals(propId)) {
					return propDefn;
				}
			}
		}

		return null;
	}

	@Override
	public boolean supportsMedOperation(String proposedOperationName, Object context) {
		// TODO Auto-generated method stub
		return super.supportsMedOperation(proposedOperationName, context);
	}
    
    public static InfinispanCacheModelExtensionAssistant getRestAssistant() {
    	final ModelExtensionRegistry registry = ExtensionPlugin.getInstance().getRegistry();
        final String prefix = InfinispanCacheModelExtensionConstants.NAMESPACE_PROVIDER.getNamespacePrefix();
        return (InfinispanCacheModelExtensionAssistant)registry.getModelExtensionAssistant(prefix);
    }
    
}
