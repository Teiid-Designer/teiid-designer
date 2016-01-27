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
import org.teiid.designer.metamodels.relational.DirectionKind;
import org.teiid.designer.metamodels.relational.ForeignKey;
import org.teiid.designer.metamodels.relational.Procedure;
import org.teiid.designer.metamodels.relational.ProcedureParameter;
import org.teiid.designer.metamodels.relational.RelationalPackage;
import org.teiid.designer.metamodels.relational.Table;
import org.teiid.designer.metamodels.relational.View;

/**
 * @since 8.0
 *
 */
public class RelationalModelExtensionAssistant extends EmfModelObjectExtensionAssistant {

    private static String getPropertyId(final String propName) {
        return ModelExtensionPropertyDefinition.Utils.getPropertyId(RelationalModelExtensionConstants.NAMESPACE_PROVIDER,
                                                                    propName);
    }

    private enum PropertyName {

        AGGREGATE(getPropertyId("aggregate")), //$NON-NLS-1$
        ALLOWS_ORDER_BY(getPropertyId("allows-orderby")), //$NON-NLS-1$
        ALLOWS_DISTINCT(getPropertyId("allows-distinct")), //$NON-NLS-1$
        ANALYTIC(getPropertyId("analytic")), //$NON-NLS-1$
        DECOMPOSABLE(getPropertyId("decomposable")), //$NON-NLS-1$
        DETERMINISTIC(getPropertyId("deterministic")), //$NON-NLS-1$
        NATIVE_QUERY(getPropertyId("native-query")), //$NON-NLS-1$
        NON_PREPARED(getPropertyId("non-prepared")), //$NON-NLS-1$
        USES_DISTINCT_ROWS(getPropertyId("uses-distinct-rows")), //$NON-NLS-1$
        VARARGS(getPropertyId("varargs")), //$NON-NLS-1$
        NULL_ON_NULL(getPropertyId("null-on-null")), //$NON-NLS-1$
        JAVA_CLASS(getPropertyId("java-class")), //$NON-NLS-1$
        JAVA_METHOD(getPropertyId("java-method")), //$NON-NLS-1$
        FUNCTION_CATEGORY(getPropertyId("function-category")), //$NON-NLS-1$
        UDF_JAR_PATH(getPropertyId("udfJarPath")), //$NON-NLS-1$
        ALLOW_JOIN(getPropertyId("allow-join")), //$NON-NLS-1$
        NATIVE_TYPE(getPropertyId("native_type")), //$NON-NLS-1$
        GLOBAL_TEMP_TABLE(getPropertyId("global-temp-table")), //$NON-NLS-1$
        ALLOW_MATVIEW_MANAGEMENT(getPropertyId("ALLOW_MATVIEW_MANAGEMENT")), //$NON-NLS-1$
        MATVIEW_STATUS_TABLE(getPropertyId("MATVIEW_STATUS_TABLE")), //$NON-NLS-1$
        MATVIEW_BEFORE_LOAD_SCRIPT(getPropertyId("MATVIEW_BEFORE_LOAD_SCRIPT")), //$NON-NLS-1$
        MATVIEW_LOAD_SCRIPT(getPropertyId("MATVIEW_LOAD_SCRIPT")), //$NON-NLS-1$
        MATVIEW_AFTER_LOAD_SCRIPT(getPropertyId("MATVIEW_AFTER_LOAD_SCRIPT")), //$NON-NLS-1$
        MATVIEW_SHARE_SCOPE(getPropertyId("MATVIEW_SHARE_SCOPE")), //$NON-NLS-1$
        MATERIALIZED_STAGE_TABLE(getPropertyId("MATERIALIZED_STAGE_TABLE")), //$NON-NLS-1$
        ON_VDB_START_SCRIPT(getPropertyId("ON_VDB_START_SCRIPT")), //$NON-NLS-1$
        ON_VDB_DROP_SCRIPT(getPropertyId("ON_VDB_DROP_SCRIPT")), //$NON-NLS-1$
        MATVIEW_ONERROR_ACTION(getPropertyId("MATVIEW_ONERROR_ACTION")), //$NON-NLS-1$
        MATVIEW_TTL(getPropertyId("MATVIEW_TTL")); //$NON-NLS-1$
        

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
    
    public static RelationalModelExtensionAssistant getRelationalAssistant() {
    	final ModelExtensionRegistry registry = ExtensionPlugin.getInstance().getRegistry();
        final String prefix = RelationalModelExtensionConstants.NAMESPACE_PROVIDER.getNamespacePrefix();
        return (RelationalModelExtensionAssistant)registry.getModelExtensionAssistant(prefix);
    }

    /**
     * Saves the relational MED to a model if necessary.
     * @param model the model being checked (can be <code>null</code>)
     * @throws Exception if there is an error applying MED
     */
    public void applyMedIfNecessary(final IResource model) throws Exception {
        if (model != null) {
            final ModelResource modelResource = ModelerCore.getModelWorkspace().findModelResource(model);

            if (modelResource != null && !modelResource.isReadOnly()) {
                if ((ModelType.PHYSICAL_LITERAL == modelResource.getModelType() || ModelType.VIRTUAL_LITERAL == modelResource.getModelType())
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
            // View objects in virtual models should not have extension properties
            if (ModelUtil.isVirtual(modelObject) && (modelObject instanceof View)) {
                return null;
            }

            boolean isPhysical = ModelUtil.isPhysical(modelObject);
        	boolean isFunction = false;
        	if( modelObject instanceof Procedure ) {
        		isFunction = ((Procedure)modelObject).isFunction();
        	}
        	
            // must be a table or a procedure in a physical model to have these properties
            if (PropertyName.same(PropertyName.NATIVE_QUERY, propId)) {
                if (((modelObject instanceof Table) || (modelObject instanceof Procedure)) && isPhysical) {
                    return propDefn;
                }

                // EObject should not have the native query property definition
                return null;
            }
            
            // must be a table and physical
            if (PropertyName.same(PropertyName.GLOBAL_TEMP_TABLE, propId)) {
                if (( modelObject instanceof Table )  && !isPhysical) {
                    return propDefn;
                }

                // EObject should not have the native query property definition
                return null;
            }
            
            // must be a table or a procedure in a physical model to have these properties
            if (PropertyName.same(PropertyName.NATIVE_TYPE, propId)) {
                if (modelObject instanceof ProcedureParameter && isPhysical && 
                	((ProcedureParameter)modelObject).getDirection() == DirectionKind.OUT_LITERAL) {
                    return propDefn;
                }

                // EObject should not have the native query property definition
                return null;
            }
            
            // must be a procedure in a physical model to have these properties
            if (PropertyName.same(PropertyName.ALLOW_JOIN, propId)) {
                if ((modelObject instanceof ForeignKey) && isPhysical) {
                    return propDefn;
                }

                // EObject should not have these property definitions
                return null;
            }

            // must be a procedure in a physical model to have these properties
            if (PropertyName.same(PropertyName.NON_PREPARED, propId)) {
                if ((modelObject instanceof Procedure) && isPhysical) {
                    return propDefn;
                }

                // EObject should not have these property definitions
                return null;
            }
            
            // 'UDF' properties are supported in physical or virtual models now, when function=true
            if (  PropertyName.same(PropertyName.JAVA_CLASS, propId) || PropertyName.same(PropertyName.JAVA_METHOD, propId) 
               || PropertyName.same(PropertyName.FUNCTION_CATEGORY, propId) || PropertyName.same(PropertyName.UDF_JAR_PATH, propId)
               || PropertyName.same(PropertyName.VARARGS, propId) || PropertyName.same(PropertyName.NULL_ON_NULL, propId)
               || PropertyName.same(PropertyName.DETERMINISTIC, propId) || PropertyName.same(PropertyName.AGGREGATE, propId)) {
                if ((modelObject instanceof Procedure) && isFunction) {
                    return propDefn;
                }
                
                // make sure model object does not have these extension properties for when function is false
                removeProperty(modelObject, PropertyName.DETERMINISTIC.toString());
                removeProperty(modelObject, PropertyName.JAVA_CLASS.toString());
                removeProperty(modelObject, PropertyName.JAVA_METHOD.toString());
                removeProperty(modelObject, PropertyName.FUNCTION_CATEGORY.toString());
                removeProperty(modelObject, PropertyName.UDF_JAR_PATH.toString());
                removeProperty(modelObject, PropertyName.VARARGS.toString());
                removeProperty(modelObject, PropertyName.NULL_ON_NULL.toString());
                removeProperty(modelObject, PropertyName.AGGREGATE.toString());
                removeProperty(modelObject, PropertyName.ANALYTIC.toString());
                removeProperty(modelObject, PropertyName.ALLOWS_ORDER_BY.toString());
                removeProperty(modelObject, PropertyName.USES_DISTINCT_ROWS.toString());
                removeProperty(modelObject, PropertyName.DECOMPOSABLE.toString());
                removeProperty(modelObject, PropertyName.ALLOWS_DISTINCT.toString());

                // EObject should not have these property definitions
                return null;
            }

            if (PropertyName.same(PropertyName.ANALYTIC, propId) || PropertyName.same(PropertyName.ALLOWS_ORDER_BY, propId)
            || PropertyName.same(PropertyName.USES_DISTINCT_ROWS, propId)
            || PropertyName.same(PropertyName.ALLOWS_DISTINCT, propId)
            || PropertyName.same(PropertyName.DECOMPOSABLE, propId)) {
                if ((modelObject instanceof Procedure) ) {
                    // aggregate must be true to have the above properties
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
                }

                // EObject should not have the requested property definition
                return null;
            }
            
            // MATERIALIZED VIEW PROPERTIES
            if( ( modelObject instanceof Table )  && isPhysical) {
            	// remove if physical table
            	 if (  PropertyName.same(PropertyName.MATERIALIZED_STAGE_TABLE, propId) || 
            			 PropertyName.same(PropertyName.MATVIEW_AFTER_LOAD_SCRIPT, propId) || 
            			 PropertyName.same(PropertyName.MATVIEW_BEFORE_LOAD_SCRIPT, propId) || 
            			 PropertyName.same(PropertyName.MATVIEW_LOAD_SCRIPT, propId) || 
            			 PropertyName.same(PropertyName.MATVIEW_ONERROR_ACTION, propId) || 
            			 PropertyName.same(PropertyName.MATVIEW_SHARE_SCOPE, propId) || 
            			 PropertyName.same(PropertyName.MATVIEW_STATUS_TABLE, propId) || 
            			 PropertyName.same(PropertyName.MATVIEW_TTL, propId) || 
            			 PropertyName.same(PropertyName.ON_VDB_DROP_SCRIPT, propId) || 
            			 PropertyName.same(PropertyName.ON_VDB_START_SCRIPT, propId) ) {
            		 removeProperty(modelObject, PropertyName.MATERIALIZED_STAGE_TABLE.toString());
            		 removeProperty(modelObject, PropertyName.MATVIEW_AFTER_LOAD_SCRIPT.toString());
            		 removeProperty(modelObject, PropertyName.MATVIEW_BEFORE_LOAD_SCRIPT.toString());
            		 removeProperty(modelObject, PropertyName.MATVIEW_LOAD_SCRIPT.toString());
            		 removeProperty(modelObject, PropertyName.MATVIEW_ONERROR_ACTION.toString());
            		 removeProperty(modelObject, PropertyName.MATVIEW_SHARE_SCOPE.toString());
            		 removeProperty(modelObject, PropertyName.MATVIEW_STATUS_TABLE.toString());
            		 removeProperty(modelObject, PropertyName.MATVIEW_TTL.toString());
            		 removeProperty(modelObject, PropertyName.ON_VDB_DROP_SCRIPT.toString());
            		 removeProperty(modelObject, PropertyName.ON_VDB_START_SCRIPT.toString());
            	 }
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
    	boolean isVirtual = ModelUtil.isVirtual(modelObject);
    	
    	if( isVirtual && !PropertyName.same(PropertyName.NON_PREPARED, propId)) {
    		super.setPropertyValue(modelObject, propId, newValue);
    	} else if( !isVirtual && PropertyName.same(PropertyName.NATIVE_QUERY, propId)) {
    		super.setPropertyValue(modelObject, propId, newValue);
    	} else if( !isVirtual && PropertyName.same(PropertyName.NATIVE_TYPE, propId)) {
    		super.setPropertyValue(modelObject, propId, newValue);
    	}

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
     * @param context the context of the operation
     *
     * @see org.teiid.designer.core.extension.EmfModelObjectExtensionAssistant#supportsMedOperation(java.lang.String, java.lang.Object)
     */
    @Override
    public boolean supportsMedOperation(String proposedOperationName,
                                        Object context) {
        CoreArgCheck.isNotEmpty(proposedOperationName, "proposedOperationName is empty"); //$NON-NLS-1$
        return ExtensionConstants.MedOperations.SHOW_IN_REGISTRY.equals(proposedOperationName); // only show in registry
    }
    

    @Override
    public boolean supportsProperty(Object modelObject, String propId)
    	throws Exception {
    	return getPropertyDefinition(modelObject, propId) != null;
    }
}
