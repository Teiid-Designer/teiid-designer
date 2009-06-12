/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.validation;

import java.util.ArrayList;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.ValidationResult;

/**
 * ValidationResultImpl
 */
public class ValidationResultImpl implements ValidationResult {

	private static final ValidationProblem[] EMPTY_ARRAY = new ValidationProblem[0];

	private boolean hasInitialized = false;
    private boolean isFatalObj = false;
    private boolean isFatalResource = false;
    private ArrayList problems;
    private String locationPath;
    private String locationUri;
    private String targetUri;
    private Object target;
    private Object location;

    /**
     * Construct an instance of ValidationResultImpl.
     */
    public ValidationResultImpl(Object target) {
		this(target, null);
    }

    /**
     * Construct an instance of ValidationResultImpl.
     */
    public ValidationResultImpl(Object target, Object location) {
		this.target = target;
		this.location = location;
    }

    //-------------------------------------------------------------------------
    //                  INTERFACE METHODS
    //-------------------------------------------------------------------------
    /**
     * @see com.metamatrix.modeler.core.validation.ValidationResult#getTarget()
     * @since 4.2
     */
    public Object getTarget() {
        return this.target;
    }

    /*
     * @see com.metamatrix.modeler.core.validation.ValidationResult#isFatalObject()
     */
    public boolean isFatalObject(Object eObject) {
        if(eObject != null && eObject.equals(this.target)) {
            return isFatalObj;
        }
        return false;
    }

    /*
     * @see com.metamatrix.modeler.core.validation.ValidationResult#isFatalResource()
     */
    public boolean isFatalResource() {
        return isFatalResource;
    }

    /**
     * @see com.metamatrix.modeler.core.validation.ValidationResult#getProblems()
     */
    public ValidationProblem[] getProblems() {
    	if (problems != null && problems.size() != 0) {
			final ValidationProblem[] result = new ValidationProblem[problems.size()];
			problems.toArray(result);
			return result;
    	}
  		return EMPTY_ARRAY;
    }

    /**
     * @see com.metamatrix.modeler.core.validation.ValidationResult#hasProblems()
     */
    public boolean hasProblems() {
        if (problems != null && problems.size() > 0) {
            return true;
        }
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.validation.ValidationResult#addProblem(com.metamatrix.modeler.core.validation.ValidationProblem)
     */
    public void addProblem(final ValidationProblem problem) {
        if(problem != null) {
	        isFatalObj = (problem.getSeverity() == IStatus.ERROR);
	        if(problems == null) {
	        	problems = new ArrayList(1);
	        }
	        problems.add(problem);
	        // there are problems...so initialize the result
			this.init(this.target, this.location);
        }
    }

    /**
     * @see com.metamatrix.modeler.core.validation.ValidationResult#getLocationPath()
     */
    public String getLocationPath() {
        return this.locationPath;
    }

    /**
     * @see com.metamatrix.modeler.core.validation.ValidationResult#setLocationPath(java.lang.String)
     */
    public void setLocationPath(final String locationPath) {
        ArgCheck.isNotNull(locationPath);
        this.locationPath = locationPath;
    }

    /**
     * @see com.metamatrix.modeler.core.validation.ValidationResult#getLocationUri()
     */
    public String getLocationUri() {
        return this.locationUri;
    }

    /**
     * @see com.metamatrix.modeler.core.validation.ValidationResult#getTargetUri()
     */
    public String getTargetUri() {
        return this.targetUri;
    }

    //-------------------------------------------------------------------------
    //                  INSTANCE METHODS
    //-------------------------------------------------------------------------

    /*
     * @see com.metamatrix.modeler.core.validation.ValidationResult#setFatalResource(boolean)
     */
    public void setFatalResource(boolean fatal) {
        this.isFatalResource = fatal;
    }

    private void init(final Object target, final Object location) {
    	if(!hasInitialized) {
	        this.initTarget(target);
	        if (target != location) {
	            this.initLocation(location);
	        }
			hasInitialized = true;
    	}
    }

    private void initTarget(final Object target) {
        ArgCheck.isNotNull(target);
        this.target = target;
        if (target instanceof EObject) {
            EObject eObj = (EObject) target;
            // Set the target URI ... used in locating the EObject containing the error
            this.targetUri = ModelerCore.getModelEditor().getUri(eObj).toString();

            // Set the location path ...
            if (this.locationPath == null) {
                this.locationPath = ModelerCore.getModelEditor().getModelRelativePath(eObj).toString();
            }

            // Set the location URI ... used in locating the primary EObject containing the error
            if (this.locationUri == null) {
                this.locationUri = this.targetUri;
            }
        } else if (target instanceof Resource) {
            // Set the target URI ... used in locating the EObject containing the error
            this.targetUri = null;
            // Set the location path ...
            this.locationPath = ModelerCore.getModelEditor().getModelName((Resource)target);
            // Set the location URI ... used in locating the primary EObject containing the error
            this.locationUri = null;
        } else if (target instanceof IResource) {
            // Set the target URI ... used in locating the EObject containing the error
            this.targetUri = null;
            // Set the location path ...
            this.locationPath = ((IResource)target).getFullPath().toString();
            // Set the location URI ... used in locating the primary EObject containing the error
            this.locationUri = null;
        }
    }

    private void initLocation(final Object location) {
        if (location instanceof EObject) {
            EObject eObj = (EObject) location;
            // Set the location path ...
            this.locationPath = ModelerCore.getModelEditor().getModelRelativePath(eObj).toString();
            // Set the location URI ... used in locating the primary EObject containing the error
            this.locationUri = ModelerCore.getModelEditor().getUri(eObj).toString();

            // Set the target strings
            if (this.targetUri == null) {
                // Set the target URI ... used in locating the EObject containing the error
                this.targetUri = this.locationUri;
            }
        } else if (location instanceof Resource) {
            // Set the location path ...
            this.locationPath = ModelerCore.getModelEditor().getModelName((Resource)location);
            // Set the location URI ... used in locating the primary EObject containing the error
            this.locationUri = null;
        } else if (target instanceof IResource) {
            // Set the target URI ... used in locating the EObject containing the error
            this.targetUri = null;
            // Set the location path ...
            this.locationPath = ((IResource)target).getFullPath().toString();
            // Set the location URI ... used in locating the primary EObject containing the error
            this.locationUri = null;
        }
    }

    @Override
    public String toString() {
        if (this.hasProblems()) {
            StringBuffer sb = new StringBuffer();
            sb.append(this.getLocationPath());
            sb.append(StringUtil.Constants.NEW_LINE_CHAR);
            ValidationProblem[] problems = this.getProblems();
            for (int i = 0; i !=problems.length; ++i) {
                ValidationProblem problem = problems[i];
                sb.append(problem.toString());
                sb.append(StringUtil.Constants.NEW_LINE_CHAR);
            }
            return sb.toString();
        }
        return StringUtil.Constants.EMPTY_STRING;
    }

//    private void printUriValues(Object target) {
//        System.out.println("ValidationResult for EObject "+target); //$NON-NLS-1$
//        System.out.println("  locationPath = "+locationPath); //$NON-NLS-1$
//        System.out.println("  locationUri  = "+locationUri); //$NON-NLS-1$
//        System.out.println("  targetUri    = "+targetUri); //$NON-NLS-1$
//    }

}
