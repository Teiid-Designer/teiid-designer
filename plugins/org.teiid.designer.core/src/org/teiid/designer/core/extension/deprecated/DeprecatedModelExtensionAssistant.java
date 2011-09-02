/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.extension.deprecated;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.core.extension.ModelObjectExtensionAssistant;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.definition.ModelExtensionAssistant;
import org.teiid.designer.extension.definition.ModelExtensionDefinition;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;

/**
 * 
 */
public class DeprecatedModelExtensionAssistant extends ModelObjectExtensionAssistant {

    public static final String REST_NAMESPACE_PREFIX = "rest"; //$NON-NLS-1$
    public static final String SOURCE_FUNCTION_NAMESPACE_PREFIX = "sourcefunction"; //$NON-NLS-1$

    public static final String NAMESPACE_PREFIX = "ext-custom"; //$NON-NLS-1$
    private static final String NEW_PUSH_DOWN = ModelExtensionPropertyDefinition.Utils.getPropertyId(SOURCE_FUNCTION_NAMESPACE_PREFIX,
                                                                                                     "deterministic"); //$NON-NLS-1$
    private static final String NEW_REST_METHOD = ModelExtensionPropertyDefinition.Utils.getPropertyId(REST_NAMESPACE_PREFIX,
                                                                                                       "restMethod"); //$NON-NLS-1$

    private static final String NEW_URI = ModelExtensionPropertyDefinition.Utils.getPropertyId(REST_NAMESPACE_PREFIX, "uri"); //$NON-NLS-1$
    private static final String OLD_PUSH_DOWN = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PREFIX,
                                                                                                     "deterministic"); //$NON-NLS-1$

    private static final String OLD_REST_METHOD = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PREFIX,
                                                                                                       "REST-METHOD"); //$NON-NLS-1$
    private static final String OLD_URI_1 = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PREFIX, "URI"); //$NON-NLS-1$
    private static final String OLD_URI_2 = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PREFIX, "uri"); //$NON-NLS-1$

    private ModelExtensionAssistant restAssistant;
    private ModelExtensionDefinition restMed;

    private ModelExtensionAssistant sourceFunctionAssistant;
    private ModelExtensionDefinition sourceFunctionMed;

    /**
     * Converts old REST properties to new ones and saves the REST model extension definition (MED) in the model resource.
     * 
     * @param modelObject the model object whose properties are being converted (cannot be <code>null</code>)
     * @throws Exception if there is a problem accessing the model resource
     */
    public void convertOldRestProperties( Object modelObject ) throws Exception {
        // get current values
        String restMethodValue = getPropertyValue(modelObject, OLD_REST_METHOD);
        String uri1Value = getPropertyValue(modelObject, OLD_URI_1);
        String uri2Value = getPropertyValue(modelObject, OLD_URI_2);
        String uriValue = ((uri2Value == null) ? uri1Value : uri2Value);

        // remove all old properties
        removeOldRestProperties(modelObject);

        // save new
        getRestAssistant().saveModelExtensionDefinition(modelObject, getRestMed());
        getRestAssistant().setPropertyValue(modelObject, NEW_REST_METHOD, restMethodValue);
        getRestAssistant().setPropertyValue(modelObject, NEW_URI, uriValue);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.definition.ModelExtensionAssistant#getNamespacePrefix()
     */
    @Override
    public String getNamespacePrefix() {
        return NAMESPACE_PREFIX;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.definition.ModelExtensionAssistant#getPropertyDefinition(java.lang.Object,
     *      java.lang.String)
     */
    @Override
    public ModelExtensionPropertyDefinition getPropertyDefinition( Object modelObject,
                                                                   String propId ) {
        if (OLD_PUSH_DOWN.equals(propId)) {
            propId = NEW_PUSH_DOWN;
        } else if (OLD_REST_METHOD.equals(propId)) {
            propId = NEW_REST_METHOD;
        } else if (OLD_URI_1.equals(propId) || OLD_URI_2.equals(propId)) {
            propId = NEW_URI;
        }

        return super.getPropertyDefinition(modelObject, propId);
    }

    /**
     * @param modelObject the model object whose extension property property definitions for this namespace is being requested
     *            (never <code>null</code>)
     * @return the property definitions (never <code>null</code> but can be empty)
     * @throws Exception if there is a problem accessing the model resource
     */
    public Collection<ModelExtensionPropertyDefinition> getPropertyDefinitions( EObject modelObject ) throws Exception {
        CoreArgCheck.isNotNull(modelObject, "modelObject"); //$NON-NLS-1$
        String metaclassName = modelObject.getClass().getName();
        Collection<ModelExtensionPropertyDefinition> propDefns = ExtensionPlugin.getInstance()
                                                                                .getRegistry()
                                                                                .getPropertyDefinitions(NAMESPACE_PREFIX,
                                                                                                        metaclassName);

        if (propDefns.isEmpty()) {
            return Collections.emptyList();
        }

        for (Iterator<ModelExtensionPropertyDefinition> itr = propDefns.iterator(); itr.hasNext();) {
            ModelExtensionPropertyDefinition propDefn = itr.next();
            String value = getOverriddenValue(modelObject, propDefn.getId());

            if (CoreStringUtil.isEmpty(value)) {
                itr.remove();
            }
        }

        return propDefns;
    }

    private ModelExtensionAssistant getRestAssistant() {
        if (this.restAssistant == null) {
            this.restAssistant = ExtensionPlugin.getInstance().getRegistry().getModelExtensionAssistant(REST_NAMESPACE_PREFIX);
        }

        return this.restAssistant;
    }

    private ModelExtensionDefinition getRestMed() {
        if (this.restMed == null) {
            this.restMed = ExtensionPlugin.getInstance().getRegistry().getDefinition(REST_NAMESPACE_PREFIX);
        }

        return this.restMed;
    }

    private ModelExtensionAssistant getSourceFunctionAssistant() {
        if (this.sourceFunctionAssistant == null) {
            this.sourceFunctionAssistant = ExtensionPlugin.getInstance()
                                                          .getRegistry()
                                                          .getModelExtensionAssistant(SOURCE_FUNCTION_NAMESPACE_PREFIX);
        }

        return this.sourceFunctionAssistant;
    }

    private ModelExtensionDefinition getSourceFunctionMed() {
        if (this.sourceFunctionMed == null) {
            this.sourceFunctionMed = ExtensionPlugin.getInstance().getRegistry().getDefinition(SOURCE_FUNCTION_NAMESPACE_PREFIX);
        }

        return this.sourceFunctionMed;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.core.extension.ModelObjectExtensionAssistant#hasExtensionProperties(java.io.File)
     */
    @Override
    public boolean hasExtensionProperties( File file ) throws Exception {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IPath location = Path.fromOSString(file.getAbsolutePath());
        IFile modelFile = workspace.getRoot().getFileForLocation(location);

        if ((modelFile != null) && ModelUtil.isModelFile(modelFile.getFullPath())) {
            // since there is no MED saved in the file, the only way to check is to see if any model object has a 7.4 property
            Collection<EObject> eObjects = getModelResource(modelFile).getEObjects();

            for (EObject modelObject : eObjects) {
                if (SqlAspectHelper.isProcedure(modelObject) && hasExtensionProperties(modelObject)) {
                    return true;
                }
            }
        }

        // none found
        return false;
    }

    /**
     * @param modelObject the model object being checked (cannot be <code>null</code>)
     * @return <code>true</code> if the model object's model resource contains 7.4 pushdown extension properties
     * @throws Exception if there is a problem accessing the model resource
     */
    public boolean hasOldPushdownProperties( EObject modelObject ) throws Exception {
        return !CoreStringUtil.isEmpty(getOverriddenValue(modelObject, OLD_PUSH_DOWN));
    }

    /**
     * @param modelObject the model object being checked (cannot be <code>null</code>)
     * @return <code>true</code> if the model object's model resource contains 7.4 REST extension properties
     * @throws Exception if there is a problem accessing the model resource
     */
    public boolean hasOldRestProperties( EObject modelObject ) throws Exception {
        // need to only check if one of the properties is present
        return !CoreStringUtil.isEmpty(getOverriddenValue(modelObject, OLD_REST_METHOD));
    }

    /**
     * @param modelObject the model object whose old REST extension properties (cannot be <code>null</code>)
     * @throws Exception if there is a problem accessing the model resource
     */
    public void removeOldRestProperties( Object modelObject ) throws Exception {
        removeProperty(modelObject, OLD_REST_METHOD);
        removeProperty(modelObject, OLD_URI_1);
        removeProperty(modelObject, OLD_URI_2);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.core.extension.ModelObjectExtensionAssistant#setPropertyValue(java.lang.Object, java.lang.String,
     *      java.lang.String)
     */
    @Override
    public void setPropertyValue( Object modelObject,
                                  String propId,
                                  String newValue ) throws Exception {
        CoreArgCheck.isInstanceOf(EObject.class, modelObject);
        CoreArgCheck.isNotEmpty(propId, "id is empty"); //$NON-NLS-1$

        // Change to old pushdown property will remove it - and re-save as new property
        if (OLD_PUSH_DOWN.equals(propId)) {
            // remove old
            removeProperty(modelObject, OLD_PUSH_DOWN);

            // save new
            getSourceFunctionAssistant().saveModelExtensionDefinition(modelObject, getSourceFunctionMed());
            getSourceFunctionAssistant().setPropertyValue(modelObject, NEW_PUSH_DOWN, newValue);
        } else if (OLD_URI_1.equals(propId) || OLD_URI_2.equals(propId) || OLD_REST_METHOD.equals(propId)) {
            // convert all properties
            convertOldRestProperties(modelObject);

            // set new value
            if (OLD_REST_METHOD.equals(propId)) {
                propId = NEW_REST_METHOD;
            } else if (OLD_URI_1.equals(propId) || OLD_URI_2.equals(propId)) {
                propId = NEW_URI;
            } else {
                assert false : "an unexpected property ID was found: " + propId; //$NON-NLS-1$
            }

            getRestAssistant().setPropertyValue(modelObject, propId, newValue);
        }
    }
}
