/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.relational.aspects.validation.rules;

import static org.teiid.designer.metamodels.relational.extension.RelationalModelExtensionConstants.NAMESPACE_PROVIDER;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.metamodel.aspect.AspectManager;
import org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureAspect;
import org.teiid.designer.core.util.VdbHelper.VdbFolders;
import org.teiid.designer.core.validation.ObjectValidationRule;
import org.teiid.designer.core.validation.ValidationContext;
import org.teiid.designer.core.validation.ValidationProblem;
import org.teiid.designer.core.validation.ValidationProblemImpl;
import org.teiid.designer.core.validation.ValidationResult;
import org.teiid.designer.core.validation.ValidationResultImpl;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.definition.ModelObjectExtensionAssistant;
import org.teiid.designer.metamodels.relational.DirectionKind;
import org.teiid.designer.metamodels.relational.Procedure;
import org.teiid.designer.metamodels.relational.ProcedureParameter;
import org.teiid.designer.metamodels.relational.RelationalPlugin;
import org.teiid.designer.metamodels.relational.extension.RelationalModelExtensionConstants;


/**
 * ProcedureFunctionRule
 * Validation Rules for Relational Procedures where function=true
 *   -- view procedures - pushdown is determined automatically
 *   -- source procedures - if no "java-method" property "PUSHDOWN_REQUIRED"
 *
 * @since 8.1
 */
public class ProcedureFunctionRule implements ObjectValidationRule {
    
    /*
     * @See org.teiid.designer.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject, org.teiid.designer.core.validation.ValidationContext)
     */
    @Override
	public void validate(EObject eObject, ValidationContext context) {
        CoreArgCheck.isInstanceOf(Procedure.class, eObject);

        Procedure procedure = (Procedure) eObject;
        SqlProcedureAspect procAspect = (SqlProcedureAspect) AspectManager.getSqlAspect(eObject);
        
        // If procedure is not a virtual function, return.
        if(!procedure.isFunction()) return;

        // For virtual procedure functions, all 'Udf' properties must be validated
        // For physical procedure functions, only validate the 'Udf' properties if either 'java-class' or 'java-method' is not empty.
        boolean isVirtual = procAspect.isVirtual(eObject);
        // If not virtual, need to check java-method and java-class properties
        String javaClass = getJavaClass(procedure);
        String javaMethod = getJavaMethod(procedure);
        // If both empty, no need to validate
        if( !isVirtual && (javaClass==null || javaClass.trim().isEmpty()) && (javaMethod==null || javaMethod.trim().isEmpty()) ) {
            return;
        }

        ValidationResult result = new ValidationResultImpl(eObject);

        // validate the return parameter
        validateReturnParameter(procedure, result);

        // validate java-class invocation class
        if(CoreStringUtil.isEmpty(javaClass)) {
            ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.ERROR ,RelationalPlugin.Util.getString("ProcedureFunctionRule.javaClassNotSpecified")); //$NON-NLS-1$
            result.addProblem(problem);
        } else {
            validateJavaIdentifier(javaClass, RelationalPlugin.Util.getString("ProcedureFunctionRule.javaClass",javaClass), true, result); //$NON-NLS-1$
        }

        //  validate invocation method
        if(CoreStringUtil.isEmpty(javaMethod)) {
            ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.ERROR ,RelationalPlugin.Util.getString("ProcedureFunctionRule.javaMethodNotSpecified")); //$NON-NLS-1$
            result.addProblem(problem);
        } else {
            validateJavaIdentifier(javaMethod, RelationalPlugin.Util.getString("ProcedureFunctionRule.javaMethod",javaMethod), false, result); //$NON-NLS-1$
        }
        
        // validate jarPath property
        validateUdfJarPath(procedure,result);

        // validate function category
        String category = getFunctionCategory(procedure);
        if (CoreStringUtil.isEmpty(category)) {
            ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.ERROR ,RelationalPlugin.Util.getString("ProcedureFunctionRule.categoryNotSpecified")); //$NON-NLS-1$
            result.addProblem(problem);
        }
                
		// add the result to the context
		context.addResult(result);
    }
    
    /**
     * Validate the udfJarPath property.
     * - the path must be set
     * - the specified jar must be located in the workspace project
     * @param scalarFunc the Scalar Function to validate
     * @param result the ValidationResult
     */
    private final void validateUdfJarPath(Procedure proc, ValidationResult result) {
        String udfJarPath = getUdfJarPath(proc);

        if(udfJarPath!=null) {
            if (CoreStringUtil.isEmpty(udfJarPath.trim())) {
                String message = RelationalPlugin.Util.getString("ProcedureFunctionRule.udfJarPathNotSet"); //$NON-NLS-1$
                ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.ERROR ,message); 
                result.addProblem(problem);
            } else {
                final ModelResource resrc = ModelerCore.getModelWorkspace().findModelResource(proc);
                IProject project = resrc.getModelProject().getProject();
                IFolder libFolder = getUdfJarFolder(project);
                boolean found = isJarInFolder(libFolder,udfJarPath);
                if(!found) {
                    String message = RelationalPlugin.Util.getString("ProcedureFunctionRule.udfJarNotFound",udfJarPath); //$NON-NLS-1$
                    ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.ERROR ,message); 
                    result.addProblem(problem);
                }
            }
        }
    }
    
    /**
     * Get the java-class property from the supplied Procedure
     * @param proc the supplied Procedure
     * @return the java-class property value
     */
    public static String getJavaClass(final Procedure proc) {
        String javaClass = null;
        ModelObjectExtensionAssistant assistant = (ModelObjectExtensionAssistant)ExtensionPlugin.getInstance().getRegistry().getModelExtensionAssistant(NAMESPACE_PROVIDER.getNamespacePrefix());
        if(assistant!=null) {
            try {
                javaClass = assistant.getPropertyValue(proc, RelationalModelExtensionConstants.PropertyIds.JAVA_CLASS);
            } catch (Exception ex) {
                String msg = RelationalPlugin.Util.getString("ProcedureFunctionRule.errorGettingJavaClass", proc.getName());  //$NON-NLS-1$
                RelationalPlugin.Util.log(IStatus.ERROR,ex,msg);
            }
        }
        return javaClass;
    }

    /**
     * Get the java-method property from the supplied Procedure
     * @param proc the supplied Procedure
     * @return the java-method property value
     */
    public static String getJavaMethod(final Procedure proc) {
        String javaMethod = null;
        ModelObjectExtensionAssistant assistant = (ModelObjectExtensionAssistant)ExtensionPlugin.getInstance().getRegistry().getModelExtensionAssistant(NAMESPACE_PROVIDER.getNamespacePrefix());
        if(assistant!=null) {
            try {
                javaMethod = assistant.getPropertyValue(proc, RelationalModelExtensionConstants.PropertyIds.JAVA_METHOD);
            } catch (Exception ex) {
                String msg = RelationalPlugin.Util.getString("ProcedureFunctionRule.errorGettingJavaMethod", proc.getName());  //$NON-NLS-1$
                RelationalPlugin.Util.log(IStatus.ERROR,ex,msg);
            }
        }
        return javaMethod;
    }

    /**
     * Get the function-category property from the supplied Procedure
     * @param proc the supplied Procedure
     * @return the function-category property value
     */
    public static String getFunctionCategory(final Procedure proc) {
        String javaMethod = null;
        ModelObjectExtensionAssistant assistant = (ModelObjectExtensionAssistant)ExtensionPlugin.getInstance().getRegistry().getModelExtensionAssistant(NAMESPACE_PROVIDER.getNamespacePrefix());
        if(assistant!=null) {
            try {
                javaMethod = assistant.getPropertyValue(proc, RelationalModelExtensionConstants.PropertyIds.FUNCTION_CATEGORY);
            } catch (Exception ex) {
                String msg = RelationalPlugin.Util.getString("ProcedureFunctionRule.errorGettingFunctionCategory", proc.getName());  //$NON-NLS-1$
                RelationalPlugin.Util.log(IStatus.ERROR,ex,msg);
            }
        }
        return javaMethod;
    }

    /**
     * Get the Udf jarPath property from the supplied ScalarFunction
     * @param proc the supplied Procedure
     * @return the Udf jarPath property value
     */
    public static String getUdfJarPath(final Procedure proc) {
        String udfJarPath = null;
        ModelObjectExtensionAssistant assistant = (ModelObjectExtensionAssistant)ExtensionPlugin.getInstance().getRegistry().getModelExtensionAssistant(NAMESPACE_PROVIDER.getNamespacePrefix());
        if(assistant!=null) {
            try {
                udfJarPath = assistant.getPropertyValue(proc, RelationalModelExtensionConstants.PropertyIds.UDF_JAR_PATH);
            } catch (Exception ex) {
                String msg = RelationalPlugin.Util.getString("ProcedureFunctionRule.errorGettingJarPath", proc.getName());  //$NON-NLS-1$
                RelationalPlugin.Util.log(IStatus.ERROR,ex,msg);
            }
        }
        return udfJarPath;
    }
    
    /**
     * Check that View Procedure function has a return parameter, and it's valid
     * @param procedure the Procedure
     * @param result the ValidationResult
     */
    private final void validateReturnParameter(Procedure procedure, ValidationResult result) {
        List<EObject> params = procedure.getParameters();
        
        // First check for at least one parameter
        if(params.size()==0) {
            ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.ERROR ,RelationalPlugin.Util.getString("ProcedureFunctionRule.returnParmeterIsRequired")); //$NON-NLS-1$
            result.addProblem(problem);
            return;
        }
        
        // Next verify that there is a return parameter
        ProcedureParameter returnParam = null;
        for (EObject param : params) {
            if (param instanceof ProcedureParameter) {
                DirectionKind direction = ((ProcedureParameter)param).getDirection();
                int directionKind = direction.getValue();
                if (directionKind == DirectionKind.RETURN) {
                    returnParam = (ProcedureParameter)param;
                    break;
                }
            }
        }
        
        // Return Parameter not found
        if(returnParam==null) {
            ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.ERROR ,RelationalPlugin.Util.getString("ProcedureFunctionRule.returnParmeterIsRequired")); //$NON-NLS-1$
            result.addProblem(problem);
            return;
        }
    }

    /**
     * Check that specified string is valid Java identifier.  If not, create problems on the validation result.
     * @param identifier String to check
     * @param strName String to use in exception message
     * @param allowMultiple True if multiple identifiers are allowed, as in a class name
     */
    private final void validateJavaIdentifier(String identifier, String strName, boolean allowMultiple, ValidationResult result) {
        // First check first character
        if(!CoreStringUtil.isEmpty(identifier)) {
            char firstChar = identifier.charAt(0);
            if(! Character.isJavaIdentifierStart(firstChar)) {
                ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.ERROR , strName+RelationalPlugin.Util.getString("ProcedureFunctionRule.hasInvalidFirstChar")+'\''+firstChar+'\''); //$NON-NLS-1$
                result.addProblem(problem);
            }

            // Then check the rest of the characters
            for(int i=1; i<identifier.length(); i++) {
                char ch = identifier.charAt(i);
                if(! Character.isJavaIdentifierPart(ch)) {
                    if(! allowMultiple || ! (ch == '.')) {
                        ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.ERROR , strName+RelationalPlugin.Util.getString("ProcedureFunctionRule.hasInvalidChar")+'\''+ch+'\''); //$NON-NLS-1$ 
                        result.addProblem(problem);
                    }
                }
            }

            if(identifier.charAt(identifier.length()-1) == '.') {
                ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.ERROR ,strName+RelationalPlugin.Util.getString("ProcedureFunctionRule.cannotEndWithDot")); //$NON-NLS-1$
                result.addProblem(problem);
            }
        }
    }
    
    /**
     * Get the project's folder that contains the Udf jars.  If it doesnt exist, returns null.
     * @param project the supplied project
     * @return the Udf jar folder within the project, null if non-existent.
     */
    public static IFolder getUdfJarFolder(IProject project) {
        IFolder libFolder = null;
        if(project!=null) {
            IResource[] resources = null;
            try {
                resources = project.members();
            } catch (CoreException ex) {
                return null;
            }
            // Iterate the child resources, looking for lib folder
            if(resources!=null) {
                for(int i=0; i<resources.length; i++) {
                    IResource theResc = resources[i];
                    if(theResc instanceof IFolder && VdbFolders.UDF.getReadFolder().equalsIgnoreCase(((IFolder)theResc).getName())) { 
                        libFolder = (IFolder)theResc;
                        break;
                    }
                }
            }
        }
        return libFolder;
    }
    
    /**
     * Determine if a jar with the specified name is in the specified folder
     * @param folder the supplied folder
     * @param jarFileName the name of the jar to find
     * @return 'true' if found, 'false' if not.
     */
    public static boolean isJarInFolder(IFolder folder,String jarFileName) {
        boolean found = false;
        // Iterate the child resources, looking for lib folder
        if(folder!=null) {
            try {
                IResource[] folderEntries = folder.members();
                for(int j=0; j<folderEntries.length; j++) {
                    IResource folderEntry = folderEntries[j];
                    if( folderEntry instanceof IFile && ((IFile)folderEntry).getProjectRelativePath().toString().equalsIgnoreCase(jarFileName) ) { 
                        found = true;
                        break;
                    }
                }
            } catch (CoreException ex) {
                ModelerCore.Util.log(IStatus.ERROR,ex,RelationalPlugin.Util.getString("ProcedureFunctionRule.errorWithJarLookupInFolder", folder.getName())); //$NON-NLS-1$
            }
        }
        return found;
    }
    
}
