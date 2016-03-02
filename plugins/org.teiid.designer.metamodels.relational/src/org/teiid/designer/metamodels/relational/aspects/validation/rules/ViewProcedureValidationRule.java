package org.teiid.designer.metamodels.relational.aspects.validation.rules;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.validation.ObjectValidationRule;
import org.teiid.designer.core.validation.ValidationContext;
import org.teiid.designer.core.validation.ValidationProblem;
import org.teiid.designer.core.validation.ValidationProblemImpl;
import org.teiid.designer.core.validation.ValidationResult;
import org.teiid.designer.core.validation.ValidationResultImpl;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.metamodels.relational.Procedure;
import org.teiid.designer.metamodels.relational.RelationalPlugin;
import org.teiid.designer.metamodels.relational.extension.RestModelExtensionAssistant;
import org.teiid.designer.metamodels.relational.extension.RestModelExtensionConstants;
import org.teiid.designer.metamodels.relational.util.PushdownFunctionUtil;

public class ViewProcedureValidationRule  implements ObjectValidationRule {
    private final String ruleName;

    /**
     * Construct an instance of ScalarFunctionUniquenessRule.
     * 
     */
    public ViewProcedureValidationRule() {
        super();
        this.ruleName = this.getClass().getName();
    }

    /**
     * @see org.teiid.designer.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject,
     *      org.teiid.designer.core.validation.ValidationContext)
     */
    public void validate( final EObject eObject,
                          final ValidationContext context ) {

        if (context.hasRunRule(eObject, this.ruleName)) {
            return;
        }

        // if the model type is NOT virtual then return
        
        ModelResource mr = null;
        ModelType modelType = null;
        
        try {
			mr = ModelUtil.getModel(eObject);
			modelType = mr.getModelAnnotation().getModelType();
		} catch (ModelWorkspaceException e) {
			RelationalPlugin.Util.log(IStatus.ERROR, e, e.getMessage());
			return;
		}
        
        if(!modelType.equals(ModelType.VIRTUAL_LITERAL)) {
        	return;
        }

        // Check Rest Properties
        
        // Get REST METHOD and REST URI values
        String theMethod = RestModelExtensionAssistant.getRestProperty(eObject, RestModelExtensionConstants.PropertyIds.REST_METHOD);
        String theUri = RestModelExtensionAssistant.getRestProperty(eObject, RestModelExtensionConstants.PropertyIds.URI);
        
        if(StringUtilities.isEmpty(theMethod) && StringUtilities.isEmpty(theUri)  ) {
        	return;
        }
        
        if(!StringUtilities.isEmpty(theMethod) && !StringUtilities.isEmpty(theUri)  ) {
        	return;
        }
        
        String name = ModelerCore.getModelEditor().getName(eObject);
        
        ValidationResult result = new ValidationResultImpl(eObject);
        final String msg = getWarningMessage(StringUtilities.isEmpty(theMethod), StringUtilities.isEmpty(theUri), name);
        ValidationProblem problem = new ValidationProblemImpl(0, IStatus.WARNING, msg);
        result.addProblem(problem);
        // add the result to the context
        context.addResult(result);

        // set the rule has been run
        context.recordRuleRun(eObject, this.ruleName);
    }

    protected String computeSignature(EObject eObject) {
        return PushdownFunctionUtil.getSignature((Procedure)eObject);
    }

    protected String getWarningMessage( final boolean noMethod, final boolean noUri, String procedureName) {
    	String msg = RelationalPlugin.Util.getString("ViewProcedureValidationRule.Missing_rest_method_property", procedureName); //$NON-NLS-1$
        if( noUri ) {
        	 msg = RelationalPlugin.Util.getString("ViewProcedureValidationRule.Missing_rest_uri_property", procedureName); //$NON-NLS-1$
        }
        return msg;
    }
    
}
