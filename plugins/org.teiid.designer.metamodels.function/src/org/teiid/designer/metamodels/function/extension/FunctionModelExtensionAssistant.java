/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.function.extension;

import org.eclipse.emf.ecore.EObject;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.core.extension.EmfModelObjectExtensionAssistant;
import org.teiid.designer.extension.ExtensionConstants;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;
import org.teiid.designer.metamodels.function.ScalarFunction;

/**
 * @since 8.0
 *
 */
public class FunctionModelExtensionAssistant extends EmfModelObjectExtensionAssistant {

    private static String getPropertyId(final String propName) {
        return ModelExtensionPropertyDefinition.Utils.getPropertyId(FunctionModelExtensionConstants.NAMESPACE_PROVIDER, propName);
    }

    private enum PropertyName {

        AGGREGATE(getPropertyId("aggregate")), //$NON-NLS-1$
        ALLOWS_ORDER_BY(getPropertyId("allows-orderby")), //$NON-NLS-1$
        ALLOWS_DISTINCT(getPropertyId("allows-distinct")), //$NON-NLS-1$
        ANALYTIC(getPropertyId("analytic")), //$NON-NLS-1$
        DECOMPOSABLE(getPropertyId("decomposable")), //$NON-NLS-1$
        USES_DISTINCT_ROWS(getPropertyId("uses-distinct-rows")); //$NON-NLS-1$

        public static boolean same(final PropertyName propName,
                                   final String value) {
            return propName.toString().equals(value);
        }

        private final String propName;

        private PropertyName(final String propName) {
            this.propName = propName;
        }

        /**
         * {@inheritDoc}
         *
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return this.propName;
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

        if ((propDefn != null) && (modelObject instanceof ScalarFunction)) {
            if (PropertyName.same(PropertyName.ANALYTIC, propId) || PropertyName.same(PropertyName.ALLOWS_ORDER_BY, propId)
                || PropertyName.same(PropertyName.USES_DISTINCT_ROWS, propId)
                || PropertyName.same(PropertyName.ALLOWS_DISTINCT, propId)
                || PropertyName.same(PropertyName.DECOMPOSABLE, propId)) {
                // aggregate must be true to have rest of the above properties
                final String isAggregate = getPropertyValue(modelObject, PropertyName.AGGREGATE.toString());

                if (Boolean.parseBoolean(isAggregate)) {
                    return propDefn;
                }

                // make sure model object does not have these extension properties for when aggregate is false
                removeProperty(modelObject, PropertyName.ANALYTIC.toString());
                removeProperty(modelObject, PropertyName.ALLOWS_ORDER_BY.toString());
                removeProperty(modelObject, PropertyName.USES_DISTINCT_ROWS.toString());
                removeProperty(modelObject, PropertyName.DECOMPOSABLE.toString());
                removeProperty(modelObject, PropertyName.ALLOWS_DISTINCT.toString());

                // EObject should not have the requested property definition
                return null;
            }

            return propDefn;
        }

        // property definition not found
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.teiid.designer.core.extension.EmfModelObjectExtensionAssistant#setPropertyValue(java.lang.Object, java.lang.String, java.lang.String)
     */
    @Override
    public void setPropertyValue(final Object modelObject,
                                 final String propId,
                                 final String newValue) throws Exception {
        super.setPropertyValue(modelObject, propId, newValue);

        // if setting aggregate to false remove these properties
        if (PropertyName.same(PropertyName.AGGREGATE, propId) && !Boolean.parseBoolean(newValue)) {
            removeProperty(modelObject, PropertyName.ANALYTIC.toString());
            removeProperty(modelObject, PropertyName.ALLOWS_ORDER_BY.toString());
            removeProperty(modelObject, PropertyName.USES_DISTINCT_ROWS.toString());
            removeProperty(modelObject, PropertyName.ALLOWS_DISTINCT.toString());
            removeProperty(modelObject, PropertyName.DECOMPOSABLE.toString());
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.teiid.designer.core.extension.EmfModelObjectExtensionAssistant#supportsMedOperation(java.lang.String, java.lang.Object)
     */
    @Override
    public boolean supportsMedOperation(String proposedOperationName,
                                        Object context) {
        CoreArgCheck.isNotEmpty(proposedOperationName, "proposedOperationName is empty"); //$NON-NLS-1$
        return ExtensionConstants.MedOperations.SHOW_IN_REGISTRY.equals(proposedOperationName); // only show in registry
    }
}
