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
import org.teiid.designer.metamodels.relational.Column;
import org.teiid.designer.metamodels.relational.Procedure;
import org.teiid.designer.metamodels.relational.RelationalPackage;
import org.teiid.designer.metamodels.relational.RelationalPlugin;
import org.teiid.designer.metamodels.relational.Table;

public class ExcelModelExtensionAssistant  extends EmfModelObjectExtensionAssistant implements ExcelModelExtensionConstants {

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
            if (ModelUtil.isPhysical(modelObject)) {
            	if( PropertyIds.CELL_NUMBER.equals(propId) && 
            			modelObject instanceof Column ) {
            		return propDefn;
            	} else if( (PropertyIds.FILE.equals(propId) || 
                		PropertyIds.FIRST_DATA_ROW_NUMBER.equals(propId)) &&
            			modelObject instanceof Table) {
            		return propDefn;
            	}
            }
        }

        return null;
    }
    
    public static ExcelModelExtensionAssistant getExcelAssistant() {
    	final ModelExtensionRegistry registry = ExtensionPlugin.getInstance().getRegistry();
        final String prefix = NAMESPACE_PROVIDER.getNamespacePrefix();
        return (ExcelModelExtensionAssistant)registry.getModelExtensionAssistant(prefix);
    }
    
    
    public static boolean setExcelProperty(EObject eObj, String id, String value) {
        final ExcelModelExtensionAssistant assistant = getExcelAssistant();
        if( assistant != null ) {
			try {
				assistant.setPropertyValue(eObj, id, value);
			} catch (Exception e) {
				RelationalPlugin.Util.log(e);
				return false;
			}
        }
        
        return true;
    }
    
    public static String getExcelProperty(EObject eObj, String key) {
    	String result = null;
    	final ExcelModelExtensionAssistant assistant = getExcelAssistant();
        if( assistant != null ) {
			try {
				result = assistant.getPropertyValue(eObj, key);
			} catch (Exception e) {
				RelationalPlugin.Util.log(e);
			}
        }
        
        return result;
    }
}
