/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.validation;

import java.util.ArrayList;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.core.ModelerCore;


/**
 * ValidationResultImpl
 *
 * @since 8.0
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
     * @see org.teiid.designer.core.validation.ValidationResult#getTarget()
     * @since 4.2
     */
    @Override
	public Object getTarget() {
        return this.target;
    }

    /*
     * @See org.teiid.designer.core.validation.ValidationResult#isFatalObject()
     */
    @Override
	public boolean isFatalObject(Object eObject) {
        if(eObject != null && eObject.equals(this.target)) {
            return isFatalObj;
        }
        return false;
    }

    /*
     * @See org.teiid.designer.core.validation.ValidationResult#isFatalResource()
     */
    @Override
	public boolean isFatalResource() {
        return isFatalResource;
    }

    /**
     * @see org.teiid.designer.core.validation.ValidationResult#getProblems()
     */
    @Override
	public ValidationProblem[] getProblems() {
    	if (problems != null && problems.size() != 0) {
			final ValidationProblem[] result = new ValidationProblem[problems.size()];
			problems.toArray(result);
			return result;
    	}
  		return EMPTY_ARRAY;
    }

    /**
     * @see org.teiid.designer.core.validation.ValidationResult#hasProblems()
     */
    @Override
	public boolean hasProblems() {
        if (problems != null && problems.size() > 0) {
            return true;
        }
        return false;
    }

    /**
     * @see org.teiid.designer.core.validation.ValidationResult#addProblem(org.teiid.designer.core.validation.ValidationProblem)
     */
    @Override
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
     * @see org.teiid.designer.core.validation.ValidationResult#getLocationPath()
     */
    @Override
	public String getLocationPath() {
        return this.locationPath;
    }

    /**
     * @see org.teiid.designer.core.validation.ValidationResult#setLocationPath(java.lang.String)
     */
    @Override
	public void setLocationPath(final String locationPath) {
        CoreArgCheck.isNotNull(locationPath);
        this.locationPath = locationPath;
    }

    /**
     * @see org.teiid.designer.core.validation.ValidationResult#getLocationUri()
     */
    @Override
	public String getLocationUri() {
        return this.locationUri;
    }

    /**
     * @see org.teiid.designer.core.validation.ValidationResult#getTargetUri()
     */
    @Override
	public String getTargetUri() {
        return this.targetUri;
    }

    //-------------------------------------------------------------------------
    //                  INSTANCE METHODS
    //-------------------------------------------------------------------------

    /*
     * @See org.teiid.designer.core.validation.ValidationResult#setFatalResource(boolean)
     */
    @Override
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
        CoreArgCheck.isNotNull(target);
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
            sb.append(CoreStringUtil.Constants.NEW_LINE_CHAR);
            ValidationProblem[] problems = this.getProblems();
            for (int i = 0; i !=problems.length; ++i) {
                ValidationProblem problem = problems[i];
                sb.append(problem.toString());
                sb.append(CoreStringUtil.Constants.NEW_LINE_CHAR);
            }
            return sb.toString();
        }
        return CoreStringUtil.Constants.EMPTY_STRING;
    }

//    private void printUriValues(Object target) {
//        System.out.println("ValidationResult for EObject "+target); //$NON-NLS-1$
//        System.out.println("  locationPath = "+locationPath); //$NON-NLS-1$
//        System.out.println("  locationUri  = "+locationUri); //$NON-NLS-1$
//        System.out.println("  targetUri    = "+targetUri); //$NON-NLS-1$
//    }

}
