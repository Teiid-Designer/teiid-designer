/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relational.aspects.validation.rules;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.metamodels.relational.RelationalPlugin;
import com.metamatrix.metamodels.relational.Table;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.types.DatatypeConstants;
import com.metamatrix.modeler.core.types.DatatypeManager;
import com.metamatrix.modeler.core.validation.StructuralFeatureValidationRule;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.internal.core.resource.EmfResource;
import com.metamatrix.modeler.internal.core.validation.ValidationProblemImpl;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;

/**
 * @since 4.2
 */
public class TableMaterializedRule implements StructuralFeatureValidationRule {

    // id of the feature being validated
    private final int featureID;

    private static Collection COLUMN_TYPES_THAT_CANNOT_BE_MATERIALIZED;

    /**
     * @since 4.2
     */
    public TableMaterializedRule( int featureID ) {
        super();
        this.featureID = featureID;
    }

    /**
     * @see com.metamatrix.modeler.core.validation.StructuralFeatureValidationRule#validate(org.eclipse.emf.ecore.EStructuralFeature,
     *      org.eclipse.emf.ecore.EObject, java.lang.Object, com.metamatrix.modeler.core.validation.ValidationContext)
     * @since 4.2
     */
    public void validate( final EStructuralFeature eStructuralFeature,
                          final EObject eObject,
                          final Object value,
                          final ValidationContext context ) {
        // check if the feature matches the given feature
        if (eStructuralFeature.getFeatureID() != this.featureID) {
            return;
        }

        // Check that the EObject is an instanceof Table; otherwise we cannot apply this rule
        if (eObject == null || !(eObject instanceof Table)) {
            return;
        }

        ValidationResult result = null;

        // Ensure the value is valid ...
        if (value instanceof Boolean) {
            final boolean materialized = ((Boolean)value).booleanValue();
            if (materialized) {
                boolean virtual = false;

                // Get the resource and the ModelAnnotation ...
                final Resource resource = eObject.eResource();
                if (resource instanceof EmfResource) {
                    final EmfResource emfResource = (EmfResource)resource;
                    final ModelAnnotation modelAnn = emfResource.getModelAnnotation();
                    if (modelAnn != null) {
                        if (ModelType.VIRTUAL_LITERAL.equals(modelAnn.getModelType())) {
                            virtual = true;
                        }
                    }
                }

                if (virtual) {
                    // We only care to validate VIRTUAL tables, and don't care at all what
                    // the materialized property is set to on PHYSICAL tables.
                    final Table table = (Table)eObject;
                    final Collection invalidTypes = this.getColumnTypesThatCannotBeMaterialized(context);

                    // Make sure there are no columns with the following datatypes: CLOB, BLOB or Object
                    final List columns = table.getColumns();
                    final Iterator iter = columns.iterator();
                    while (iter.hasNext()) {
                        final Column column = (Column)iter.next();
                        final EObject datatype = column.getType();
                        if (datatype != null) {
                            // See if it's one of the invalid datatypes ...
                            final boolean invalid = invalidTypes.contains(datatype);
                            if (invalid) {
                                result = new ValidationResultImpl(eObject);

                                // create validation problem and add it to the result
                                final String msg = RelationalPlugin.Util.getString("TableMaterializedRule.MaterializedTableFromInvalidColumnTypes"); //$NON-NLS-1$
                                final ValidationProblem problem = new ValidationProblemImpl(0, IStatus.ERROR, msg);
                                result.addProblem(problem);
                                context.addResult(result);
                                return; // return immediately, since done
                            }
                        }
                    }
                    
                    // Make sure that the materializedTable reference is set correctly
                    // 1) Table reference is NOT NULL
                    // 2) Table is Physical and NOT a View
                    
                    final Table materializedTable = table.getMaterializedTable();
                    
                    if( materializedTable == null ) {
                    	result = new ValidationResultImpl(eObject);
                    	
                    	// create validation problem and add it to the result
                        final String msg = RelationalPlugin.Util.getString("TableMaterializedRule.MaterializedTableReferenceUndefined"); //$NON-NLS-1$
                        final ValidationProblem problem = new ValidationProblemImpl(0, IStatus.WARNING, msg);
                        result.addProblem(problem);
                        context.addResult(result);
                    }
                }
            }

        }

        // 

        // The datatype reference cannot be null
        if (value == null) {
            // this is already validated by the multiplicity rule
            return;
        }

    }

    protected Collection getColumnTypesThatCannotBeMaterialized( final ValidationContext context ) {
        if (COLUMN_TYPES_THAT_CANNOT_BE_MATERIALIZED == null) {
            final Collection types = new HashSet();

            // Look up in the DatatypeManager the following datatypes: CLOB, BLOB and Object
            final DatatypeManager dtMgr = context.getDatatypeManager();
            if (dtMgr != null) {
                try {
                    EObject datatype = null;

                    // Add Object ...
                    datatype = dtMgr.findDatatype(DatatypeConstants.BuiltInNames.OBJECT);
                    if (datatype != null) {
                        types.add(datatype);
                    }

                } catch (ModelerCoreException err) {
                    RelationalPlugin.Util.log(err);
                }
            }

            COLUMN_TYPES_THAT_CANNOT_BE_MATERIALIZED = types;
        }
        return COLUMN_TYPES_THAT_CANNOT_BE_MATERIALIZED;
    }

}
