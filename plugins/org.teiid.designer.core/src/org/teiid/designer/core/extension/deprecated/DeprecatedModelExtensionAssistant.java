/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.extension.deprecated;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.core.extension.EmfModelObjectExtensionAssistant;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.definition.ModelObjectExtensionAssistant;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;
import org.teiid.designer.extension.properties.NamespaceProvider;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.util.ModelObjectClassNameVisitor;
import com.metamatrix.modeler.core.util.ModelVisitorProcessor;

/**
 * 
 */
public class DeprecatedModelExtensionAssistant extends EmfModelObjectExtensionAssistant {

    public static final NamespaceProvider NAMESPACE_PROVIDER;

    private static final String REST_NAMESPACE_PREFIX = "rest"; //$NON-NLS-1$
    private static final String SOURCE_FUNCTION_NAMESPACE_PREFIX = "relational"; //$NON-NLS-1$

    private static final String NEW_PUSH_DOWN;
    private static final String NEW_REST_METHOD;

    private static final String NEW_URI;
    private static final String OLD_PUSH_DOWN;

    private static final String OLD_REST_METHOD;
    private static final String OLD_URI_1;
    private static final String OLD_URI_2;

    static {
        NamespaceProvider relationalNamespaceProvider = new NamespaceProvider() {
            
            @Override
            public String getNamespaceUri() {
                return null;
            }
            
            @Override
            public String getNamespacePrefix() {
                return SOURCE_FUNCTION_NAMESPACE_PREFIX;
            }
        };

        NEW_PUSH_DOWN = ModelExtensionPropertyDefinition.Utils.getPropertyId(relationalNamespaceProvider, "deterministic"); //$NON-NLS-1$

        NamespaceProvider restNamespaceProvider = new NamespaceProvider() {
            
            @Override
            public String getNamespaceUri() {
                return null;
            }
            
            @Override
            public String getNamespacePrefix() {
                return REST_NAMESPACE_PREFIX;
            }
        };

        NEW_REST_METHOD = ModelExtensionPropertyDefinition.Utils.getPropertyId(restNamespaceProvider, "restMethod"); //$NON-NLS-1$
        NEW_URI = ModelExtensionPropertyDefinition.Utils.getPropertyId(restNamespaceProvider, "uri"); //$NON-NLS-1$

        NAMESPACE_PROVIDER = new NamespaceProvider() {
            
            /**
             * {@inheritDoc}
             *
             * @see org.teiid.designer.extension.properties.NamespaceProvider#getNamespaceUri()
             */
            @Override
            public String getNamespaceUri() {
                return null;
            }
            
            /**
             * {@inheritDoc}
             *
             * @see org.teiid.designer.extension.properties.NamespaceProvider#getNamespacePrefix()
             */
            @Override
            public String getNamespacePrefix() {
                return "ext-custom"; //$NON-NLS-1$
            }
        };

        OLD_PUSH_DOWN = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PROVIDER, "deterministic"); //$NON-NLS-1$
        OLD_REST_METHOD = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PROVIDER, "REST-METHOD"); //$NON-NLS-1$
        OLD_URI_1 = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PROVIDER, "URI"); //$NON-NLS-1$
        OLD_URI_2 = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PROVIDER, "uri"); //$NON-NLS-1$
    }

    private ModelObjectExtensionAssistant restAssistant;
    private ModelObjectExtensionAssistant sourceFunctionAssistant;

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
        getRestAssistant().saveModelExtensionDefinition(modelObject);
        getRestAssistant().setPropertyValue(modelObject, NEW_REST_METHOD, restMethodValue);
        getRestAssistant().setPropertyValue(modelObject, NEW_URI, uriValue);
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
        Collection<ModelExtensionPropertyDefinition> propDefns = getModelExtensionDefinition().getPropertyDefinitions(metaclassName);

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

    private ModelObjectExtensionAssistant getRestAssistant() {
        if (this.restAssistant == null) {
            this.restAssistant = (ModelObjectExtensionAssistant)ExtensionPlugin.getInstance()
                                                                               .getRegistry()
                                                                               .getModelExtensionAssistant(REST_NAMESPACE_PREFIX);
        }

        return this.restAssistant;
    }

    private ModelObjectExtensionAssistant getSourceFunctionAssistant() {
        if (this.sourceFunctionAssistant == null) {
            this.sourceFunctionAssistant = (ModelObjectExtensionAssistant)ExtensionPlugin.getInstance()
                                                                                         .getRegistry()
                                                                                         .getModelExtensionAssistant(SOURCE_FUNCTION_NAMESPACE_PREFIX);
        }

        return this.sourceFunctionAssistant;
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

    private void convert( Object modelObject,
                          String oldPropId,
                          String newPropId,
                          ModelObjectExtensionAssistant assistant ) throws Exception {
        String value = getPropertyValue(modelObject, oldPropId);

        if (!CoreStringUtil.isEmpty(value)) {
            removeProperty(modelObject, oldPropId);
            assistant.setPropertyValue(modelObject, newPropId, value);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.core.extension.EmfModelObjectExtensionAssistant#setPropertyValue(java.lang.Object, java.lang.String,
     *      java.lang.String)
     */
    @Override
    public void setPropertyValue( Object modelObject,
                                  String propId,
                                  String newValue ) throws Exception {
        CoreArgCheck.isInstanceOf(EObject.class, modelObject);
        CoreArgCheck.isNotEmpty(propId, "id is empty"); //$NON-NLS-1$

        // convert all procedure model objects that have the same old namespace property
        String className = "com.metamatrix.metamodels.relational.impl.ProcedureImpl"; //$NON-NLS-1$
        ModelObjectClassNameVisitor visitor = new ModelObjectClassNameVisitor(Collections.singletonList(className));
        ModelVisitorProcessor processor = new ModelVisitorProcessor(visitor, ModelVisitorProcessor.MODE_VISIBLE_CONTAINMENTS);
        processor.walk(getModelResource(modelObject), ModelVisitorProcessor.DEPTH_INFINITE);

        Collection<EObject> eObjects = getModelResource(modelObject).getEObjects();
        ModelObjectExtensionAssistant assistant;

        // first save the corresponding MED
        if (OLD_PUSH_DOWN.equals(propId)) {
            assistant = getSourceFunctionAssistant();
            assistant.saveModelExtensionDefinition(modelObject);

            for (EObject eObject : eObjects) {
                convert(eObject, OLD_PUSH_DOWN, NEW_PUSH_DOWN, assistant);

                // save new value
                if (modelObject.equals(eObject)) {
                    assistant.setPropertyValue(modelObject, NEW_PUSH_DOWN, newValue);
                }
            }
        } else if (OLD_URI_1.equals(propId) || OLD_URI_2.equals(propId) || OLD_REST_METHOD.equals(propId)) {
            assistant = getRestAssistant();
            assistant.saveModelExtensionDefinition(modelObject);

            for (EObject eObject : eObjects) {
                convert(eObject, OLD_REST_METHOD, NEW_REST_METHOD, assistant);
                convert(eObject, OLD_URI_1, NEW_URI, assistant);
                convert(eObject, OLD_URI_2, NEW_URI, assistant);

                // save new value
                if (modelObject.equals(eObject)) {
                    if (OLD_REST_METHOD.equals(propId)) {
                        assistant.setPropertyValue(modelObject, NEW_REST_METHOD, newValue);
                    } else {
                        assistant.setPropertyValue(modelObject, NEW_URI, newValue);
                    }
                }
            }
        } else {
            // should not happen
            ModelerCore.Util.log(ModelerCore.Util.getString("DeprecatedModelExtensionAssistant.propertyNotFound", propId)); //$NON-NLS-1$
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.definition.ModelExtensionAssistant#supportsMedOperation(java.lang.String, java.lang.Object)
     */
    @Override
    public boolean supportsMedOperation( String proposedOperationName,
                                         Object context ) {
        return false;
    }

}
