/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.aspects.validation.rules;

import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.core.validation.ObjectValidationRule;
import org.teiid.designer.core.validation.ValidationContext;
import org.teiid.designer.core.validation.ValidationProblem;
import org.teiid.designer.core.validation.ValidationProblemImpl;
import org.teiid.designer.core.validation.ValidationResult;
import org.teiid.designer.core.validation.ValidationResultImpl;
import org.teiid.designer.mapping.factory.DefaultMappableTree;
import org.teiid.designer.mapping.factory.IMappableTree;
import org.teiid.designer.mapping.factory.TreeMappingAdapter;
import org.teiid.designer.metamodels.transformation.InputBinding;
import org.teiid.designer.metamodels.transformation.InputParameter;
import org.teiid.designer.metamodels.transformation.MappingClass;
import org.teiid.designer.metamodels.transformation.MappingClassSet;
import org.teiid.designer.metamodels.xml.XmlDocument;
import org.teiid.designer.transformation.TransformationPlugin;


/**
 * InputParameterValidationRule
 */
public class InputParameterValidationRule implements ObjectValidationRule {

	/* (non-Javadoc)
	 * @See org.teiid.designer.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject, org.teiid.designer.core.validation.ValidationContext)
	 */
	public void validate(EObject eObject, ValidationContext context) {
		CoreArgCheck.isInstanceOf(InputParameter.class, eObject);
		InputParameter inputParam = (InputParameter) eObject;
		if(inputParam != null) {
			MappingClass mappingClass = inputParam.getInputSet().getMappingClass();
			Iterator bindingIterator = mappingClass.getMappingClassSet().getInputBinding().iterator();
			// try to find the binding for the input parameter
            InputBinding foundBinding = null;
			while(foundBinding == null && bindingIterator.hasNext()) {
				InputBinding binding = (InputBinding) bindingIterator.next();
				InputParameter bindingParam = binding.getInputParameter(); 
				if(bindingParam != null && bindingParam.equals(inputParam)) {
					foundBinding = binding;
				}
			}

			// if the input parameter is unbounded, there should be an error
			if(foundBinding == null) {
				String msg = TransformationPlugin.Util.getString("InputParameterValidationRule.The_inputParameter_{0}_on_the_inputSet_of_the_MappingClass_{1}_is_not_bound_to_any_mappingClass_column._1", inputParam.getName(), mappingClass.getName()); //$NON-NLS-1$
				ValidationProblem failureProblem  = new ValidationProblemImpl(0, IStatus.ERROR, msg);
				ValidationResult validationResult = new ValidationResultImpl(eObject);                
				validationResult.addProblem(failureProblem);
				context.addResult(validationResult);
			} else {
                EObject mcs = mappingClass.eContainer();
                CoreArgCheck.isInstanceOf(MappingClassSet.class, mcs);
                EObject target = ((MappingClassSet)mcs).getTarget();
                CoreArgCheck.isInstanceOf(XmlDocument.class, target);       
                XmlDocument document = (XmlDocument)target;
                TreeMappingAdapter mappingAdapter = new TreeMappingAdapter(document);
                IMappableTree mappableTree = new DefaultMappableTree(document);
                List parentMCs = XmlDocumentValidationRule.getParentMappingClasses(mappingClass,mappingAdapter,mappableTree);
                boolean validColumn = false;
                for (Iterator i = parentMCs.iterator(); i.hasNext() && !validColumn;) {
                    MappingClass parent = (MappingClass)i.next();
                    if (parent.getColumns().contains(foundBinding.getMappingClassColumn())) {
                        validColumn = true;
                    }
                }
                if (!validColumn) {
                    String msg = TransformationPlugin.Util.getString("InputParameterValidationRule.Invalid_binding", inputParam.getName(), mappingClass.getName()); //$NON-NLS-1$
                    ValidationProblem failureProblem  = new ValidationProblemImpl(0, IStatus.ERROR, msg);
                    ValidationResult validationResult = new ValidationResultImpl(eObject);                
                    validationResult.addProblem(failureProblem);
                    context.addResult(validationResult);
                }
            }
		}
	}

}
