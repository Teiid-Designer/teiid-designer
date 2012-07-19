package org.teiid.designer.core.metamodel.aspect.core.aspects.validation.rules;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.resource.Resource;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.validation.ResourceValidationRule;
import org.teiid.designer.core.validation.ValidationContext;
import org.teiid.designer.core.validation.ValidationProblem;
import org.teiid.designer.core.validation.ValidationProblemImpl;
import org.teiid.designer.core.validation.ValidationResult;
import org.teiid.designer.core.validation.ValidationResultImpl;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.metamodels.core.extension.XPackage;


/**
 * @since 8.0
 */
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
					if(pkg.getName().equalsIgnoreCase("XMLSOAPExtension") || //$NON-NLS-1$
							pkg.getName().equalsIgnoreCase("XMLHTTPExtension") || //$NON-NLS-1$
							pkg.getName().equalsIgnoreCase("XMLFileExtension")) { //$NON-NLS-1$
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
