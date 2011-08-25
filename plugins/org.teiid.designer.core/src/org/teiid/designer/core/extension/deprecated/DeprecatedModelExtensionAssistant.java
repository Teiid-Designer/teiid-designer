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
import org.teiid.designer.core.extension.ModelObjectExtensionAssistant;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.definition.ModelExtensionAssistant;
import org.teiid.designer.extension.definition.ModelExtensionDefinition;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;

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
            // get current values
            String oldRestMethodValue = getPropertyValue(modelObject, OLD_REST_METHOD);
            String oldUri1Value = getPropertyValue(modelObject, OLD_URI_1);
            String oldUri2Value = getPropertyValue(modelObject, OLD_URI_2);
            String oldUriValue = (oldUri1Value != null) ? oldUri1Value : oldUri2Value;

            // remove all old properies
            removeProperty(modelObject, OLD_REST_METHOD);
            removeProperty(modelObject, OLD_URI_1);
            removeProperty(modelObject, OLD_URI_2);

            // save new
            String newRestMethodValue = null;
            String newUriValue = null;

            if (OLD_REST_METHOD.equals(propId)) {
                newRestMethodValue = newValue;
                newUriValue = oldUriValue;
            } else if (OLD_URI_1.equals(propId) || OLD_URI_2.equals(propId)) {
                newUriValue = newValue;
                newRestMethodValue = oldRestMethodValue;
            } else {
                assert false : "an unexpected property ID was found: " + propId; //$NON-NLS-1$
            }

            getRestAssistant().saveModelExtensionDefinition(modelObject, getRestMed());
            getRestAssistant().setPropertyValue(modelObject, NEW_REST_METHOD, newRestMethodValue);
            getRestAssistant().setPropertyValue(modelObject, NEW_URI, newUriValue);
        }
    }
}
