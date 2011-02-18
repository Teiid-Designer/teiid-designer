package com.metamatrix.modeler.core.metamodel.core.aspects.validation.rules;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.resource.Resource;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.metamodels.core.extension.XPackage;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.validation.ResourceValidationRule;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.internal.core.validation.ValidationProblemImpl;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;

public class DeletedXmlRelationalExtensionRule implements ResourceValidationRule {

	@Override
	public void validate(Resource resource, ValidationContext context) {
		CoreArgCheck.isNotNull(resource);
		CoreArgCheck.isNotNull(context);
		if (ModelUtil.isModelFile(resource)) {
			List contents = resource.getContents();
			for (final Iterator objIter = contents.iterator(); objIter.hasNext();) {
				Object obj = objIter.next();
				if(obj instanceof XPackage) {
					XPackage pkg = (XPackage)obj;
					if(pkg.getName().equalsIgnoreCase("XMLSOAPExtension") ||
							pkg.getName().equalsIgnoreCase("XMLHTTPExtension") ||
							pkg.getName().equalsIgnoreCase("XMLFileExtension")) {
						final ValidationResult result = new ValidationResultImpl(resource, context);
						final ValidationProblem problem = new ValidationProblemImpl(0,
								IStatus.ERROR, ModelerCore.Util.getString("DeletedXmlRelationalExtensionRule.invalidextension")); //$NON-NLS-1$);
						result.addProblem(problem);
						context.addResult(result);
					}
				}
			}
		}
	}
}
