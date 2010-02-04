/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relational.aspects.validation.rules;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.metamodels.relational.RelationalPlugin;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.types.DatatypeManager;
import com.metamatrix.modeler.core.validation.StructuralFeatureValidationRule;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.internal.core.validation.ValidationProblemImpl;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;

/**
 * StringNameRule, rule that validates the string name
 */
public class ColumnDatatypeRule implements StructuralFeatureValidationRule {
    private static final String EMPTY_STR = ""; //$NON-NLS-1$
    private static final String NAME_SF_NAME = "name"; //$NON-NLS-1$
    
    // id of the feature being validated
    private int featureID;    
    
    /**
    * Construct an instance of ColumnDatatypeRule.
    * @param featureID ID of the feature to validate 
    */
   public ColumnDatatypeRule(int featureID)  {
       this.featureID = featureID;
   }    

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.validation.ValidationRule#validate(java.lang.Object, com.metamatrix.modeler.core.validation.ValidationContext)
     */
    public void validate(EStructuralFeature eStructuralFeature, EObject eObject, Object value, ValidationContext context) {
        // check if the feature matches the given feature
        if (eStructuralFeature.getFeatureID() != this.featureID) {
            return;
        }

        // Check that the EObject is an instanceof Column
        // otherwise we cannot apply this rule
        if (eObject == null || !(eObject instanceof Column)) {
            return;
        }
        ValidationResult result = new ValidationResultImpl(eObject);
        
        // The datatype reference cannot be null
        if (value == null) {
            // this is already validated by the multiplicity rule
            return;
        }

        // Check that the value is an instance of EObject
        // otherwise we cannot apply this rule
        if (!(value instanceof EObject)) {
            return;
        }
        EObject dt = (EObject)value;
        
        // The referenced datatype must be resolvable if it is a proxy
        ResourceSet resourceSet = eObject.eResource().getResourceSet();
        if (dt.eIsProxy()) {
            EObject resolvedDatatype = EcoreUtil.resolve(dt, resourceSet);
            if (resolvedDatatype == null) {
                // create validation problem and add it to the result
                Object[] params = new Object[]{((Column)eObject).getName(),((InternalEObject)dt).eProxyURI()};
                String msg = RelationalPlugin.Util.getString("ColumnDatatypeRule.Column_0_references_a_datatype_1_that_cannot_be_resolved_in_the_workspace_2",params); //$NON-NLS-1$
                ValidationProblem problem = new ValidationProblemImpl(0, IStatus.WARNING, msg);
                result.addProblem(problem);
                context.addResult(result);
                return;
            }
        }
        
        // The referenced datatype must be an instanceof XSDSimpleTypeDefinition
        final DatatypeManager dtMgr = context.getDatatypeManager();
        if(!dtMgr.isSimpleDatatype(dt) ){
            final Object[] params = new Object[]{ ((Column)eObject).getName() };
            final String msg = RelationalPlugin.Util.getString("ColumnDatatypeRule.Column_{0}__s_type_attribute_is_not_an_instance_of_XSDSimpleTypeDefinition_1", params); //$NON-NLS-1$
            final ValidationProblem problem = new ValidationProblemImpl(0, IStatus.ERROR, msg);
            result.addProblem(problem);
            context.addResult(result);
            return;
        }
        
        // The referenced datatype must be an "Enterprise" datatype
        if(!dtMgr.isEnterpriseDatatype(dt) ){
            final Object[] params = new Object[]{ ((Column)eObject).getName() };
            final String msg = RelationalPlugin.Util.getString("ColumnDatatypeRule.type_must_be_enterprise_type",params); //$NON-NLS-1$
            final ValidationProblem problem = new ValidationProblemImpl(0, IStatus.WARNING, msg);
            result.addProblem(problem);
            context.addResult(result);
        }
        
        // The referenced type must be resolvable in the workspace ...
        if (dtMgr.isBuiltInDatatype(dt)) {
            try {
                // Verify the UUID can be resolved for this built-in datatype
                String uuid = dtMgr.getUuidString(dt);
                if (uuid != null && dtMgr.findDatatype(uuid) == null) {
                    // create validation problem and add it to the result
                    final Object[] params = new Object[]{ ((Column)eObject).getName(), getDisplayValue(dt) };
                    final String msg = RelationalPlugin.Util.getString("ColumnDatatypeRule.Column_0_references_a_datatype_1_that_cannot_be_resolved_in_the_workspace_3",params); //$NON-NLS-1$
                    final ValidationProblem problem = new ValidationProblemImpl(0, IStatus.ERROR, msg);
                    result.addProblem(problem);
                    context.addResult(result);
                    return;
                }
            } catch (Exception e) {
                RelationalPlugin.Util.log(IStatus.ERROR,e,e.getMessage());
            }
        } else {
            final Resource actualResource = dt.eResource();
            if (actualResource == null) {
                // create validation problem and add it to the result
                final Object[] params = new Object[]{ ((Column)eObject).getName(), getDisplayValue(dt) };
                final String msg = RelationalPlugin.Util.getString("ColumnDatatypeRule.Column_0_references_a_datatype_1_that_cannot_be_resolved_in_the_workspace_3",params); //$NON-NLS-1$
                final ValidationProblem problem = new ValidationProblemImpl(0, IStatus.ERROR, msg);
                result.addProblem(problem);
                context.addResult(result);
                return;
            }
            if (ModelerCore.DEBUG_VALIDATION) {
                final URI typeUri = EcoreUtil.getURI(dt);
                final Resource resource = resourceSet.getResource(typeUri.trimFragment(), false);
                if (resource == null || resource != actualResource || resource.getEObject(typeUri.fragment()) == null) {
                    // create validation problem and add it to the result
                    final Object[] params = new Object[]{ ((Column)eObject).getName(), getDisplayValue(dt) };
                    final String msg = RelationalPlugin.Util.getString("ColumnDatatypeRule.Column_0_references_a_datatype_1_that_cannot_be_resolved_in_the_workspace_3",params); //$NON-NLS-1$
                    final ValidationProblem problem = new ValidationProblemImpl(0, IStatus.ERROR, msg);
                    result.addProblem(problem);
                    context.addResult(result);
                    return;
                }
            }
        }
        
    }
    
    private String getDisplayValue(final EObject eObject){
        if(eObject == null){
            return EMPTY_STR;
        }
            
        final EStructuralFeature name = eObject.eClass().getEStructuralFeature(NAME_SF_NAME);
        if(name != null){
            final Object val = eObject.eGet(name);
            if(val == null){
                return EMPTY_STR;
            }
                
            return val.toString();
        }
            
        return eObject.eClass().getName();
    }


}
