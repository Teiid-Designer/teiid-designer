/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.validation;

import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.util.ModelObjectCollector;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.core.validation.ValidationRuleSet;
import com.metamatrix.modeler.internal.core.resource.EmfResource;

/**
 * Validator looksup the <code>ValidationManager</code> to access the set of
 * validation rules defines and executes each of them.  
 */
public class Validator {
    private static final String VALIDATING = ModelerCore.Util.getString("Validator.validating"); //$NON-NLS-1$
    private static final String OF = ModelerCore.Util.getString("Validator.of"); //$NON-NLS-1$
    private static final String MODEL_OBJECTS = ModelerCore.Util.getString("Validator.modelObjects"); //$NON-NLS-1$
    private static final String SPACE = " "; //$NON-NLS-1$

    /**
     * Looks up <code>ValidationManager</code> and gets the set of validation rules
     * defined for the given EObject, iterates through all the rules and executes each
     * of them with in the given validation context. 
     * @param eObject The <code>EObject</code> for which validation rules are to be executed
     * @param context The validation context for the EObject.
     */
    public static void validate(IProgressMonitor monitor, final EmfResource emfResource, final ValidationContext context) {
        ArgCheck.isNotNull(emfResource);
        ArgCheck.isNotNull(context);        
		// create a monitor if needed
		monitor = monitor != null ? monitor : new NullProgressMonitor();
        // clear any existing results on the context
        context.clearResults();

        // Collect all the EObject instances in the EMF resource using the
        // ModelObjectCollector class to avoid a ConcurrentModificationException
        // that may occur when using the TreeIterator (emfResource.getAllContents())
        final ModelObjectCollector moc = new ModelObjectCollector(emfResource);
        final List eObjects = moc.getEObjects();
        int nObjects = eObjects.size();
        int objIncrement = getIncrement(nObjects); //nObjects/100;
        
        // Iterate througth the contents of the EmfResource validating each EObject
        Iterator iter = eObjects.iterator();
        SubProgressMonitor subMonitor = new SubProgressMonitor(monitor, 100);
        int iObject = 0;
        int incCount = 0;
        String taskMsg = null;
        while(iter.hasNext() && !monitor.isCanceled()) {
            // get the last result
            ValidationResult lastResult = context.getLastResult();
            // do not validate the resource any furthur
            if(lastResult != null && lastResult.isFatalResource()) {
                return;    
            }
            if( incCount == objIncrement ) {
                incCount = 1;
                taskMsg = VALIDATING + SPACE + iObject + SPACE + OF + SPACE + nObjects + SPACE + MODEL_OBJECTS;
                subMonitor.subTask(taskMsg);
            } else {
                incCount++;
            }
			validateObject(monitor, iter.next(), context);
			iObject++;
        }
        // validate the resource itself after validating the contents
		validateObject(monitor, emfResource, context);
    }
    
    private static int getIncrement(final int nObjects) {
        if( nObjects <= 200 )
            return 10;
        if( nObjects <= 1000 )
            return 50;
        if( nObjects <= 5000 )
            return 100;
        if( nObjects <= 10000 )
            return 200;
        
        return 500;
    }

    /**
     * Looks up <code>ValidationManager</code> and gets the set of validation rules
     * defined for the given EObject, iterates through all the rules and executes each
     * of them with in the given validation context. 
     * @param eObject The <code>EObject</code> for which validation rules are to be executed
     * @param context The validation context for the EObject.
     */
    public static void validateObject(IProgressMonitor monitor, final Object object, final ValidationContext context) {
        ArgCheck.isNotNull(object);
        ArgCheck.isNotNull(context);
		// create a monitor if needed
		monitor = monitor != null ? monitor : new NullProgressMonitor();        
        // path to the object in the workspcase

//		if(object instanceof EObject) {
//			String objectPath = ModelerCore.getModelEditor().getModelRelativatePath((EObject)object).toString();
//			// need to truncate objectPath as the monitor uses it to update the status bar and anything extremely
//			// large was taking an enormous amount of time
//			if (objectPath.length() > ModelBuildUtil.MONITOR_TASK_NAME_MAX_LENGTH) {
//			    objectPath = objectPath.substring(0, ModelBuildUtil.MONITOR_TASK_NAME_MAX_LENGTH) + ModelBuildUtil.TASK_NAME_TRUNCTATION_SUFFIX; //$NON-NLS-1$
//			}
//			
//			monitor.subTask(ModelBuildUtil.MONITOR_OBJECT_VALIDATION_MSG + objectPath);
//		}

        ValidationRuleManager ruleManager = ModelerCore.getValidationRuleManager();
        ValidationRuleSet ruleSet = ruleManager.getRuleSet(object, context);
        if(ruleSet != null && ruleSet.hasRules()) {
            ruleSet.validate(monitor, object,context);
        }
    }    
}
