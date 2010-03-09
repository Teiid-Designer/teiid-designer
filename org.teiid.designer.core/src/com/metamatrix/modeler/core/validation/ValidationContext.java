/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ValidationDescriptor;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.core.types.DatatypeManager;
import com.metamatrix.modeler.internal.core.index.IndexUtil;

/**
 * ValidationContext contains state information stored durring validation process.
 */
public class ValidationContext {

    private final static Resource[] EMPTY_RESOURCE_ARRAY = new Resource[0];

    // control/preference info
    private Preferences preferences;

    // Map of preference key to IStatus value
    private Map preferenceStatusMap;

    // validation results to be passed as part of context as
    private List validationResults;

    // map between container uuid/uri and collection of rule names
    private Map rulesRun;

	// map between a target and transformation to which it is a target
    private Map targetTransformMap;

	// map between a target and validation results for the target
    private Map targetValidationResults;

    // collection of resources being validated
    private Resource[] resourcesToValidate;

    /**
     * Collection of {@link org.eclipse.emf.ecore.resource.Resource}s defining all the valid resources in
     * the validation scope. All resources not is this collection should not validate. When value is
     * empty all resources are to be considered valid. Value is never <code>null</code>.
     */
    private Resource[] resourcesInScope = EMPTY_RESOURCE_ARRAY;

    // Reference to the container in which the resources exist.  This is not required,
    // but specifying it is strongly suggested.  No assumptions are made if no container is specified.
    private Container resourceContainer;

    // flag to indicate if VdbMetadata/ServerRuntimeMetadata should be used for query validation/resolution
    private boolean useServerIndexes = false;

    // flag to indicate if index files should be used for query validation/resolution
    private boolean useIndexesToResolve = true;

    // cache validation results for sqlTransformation mapping roots
    // defalut true for workspace validation
    private boolean cacheMappingRootResults = true;

    // directory containing the index files
    // defaults to the state directory of the modeler
    private String indexDirectory = IndexUtil.INDEX_PATH;

    // collection of uuids in the context of validation
    private Collection uuidsInContext = null;

    // collection of eobjects that should not be validated
    private Collection objectsToIgnore = null;

    // A place to store any other data
    private Map dataMap;

    /**
	 * Construct an instance of ValidationContext.
	 */
	public ValidationContext() {
	    this.preferenceStatusMap = new HashMap();
    }

    /**
     * Construct an instance of ValidationContext.
     * @param preferences Preferences used by various validation rules.
     */
    public ValidationContext(final Preferences preferences) {
        ArgCheck.isNotNull(preferences);
        this.preferences = preferences;
        this.preferenceStatusMap = new HashMap();
    }

	/**
	 * Add the uuid to the context, these uuid strings are used to check uniqueness.
	 */
    public void addUuidToContext(final String uuidString) {
        if(this.uuidsInContext == null) {
            this.uuidsInContext = new HashSet();
        }
        this.uuidsInContext.add(uuidString);
    }

    /**
     * Check if the context already contains this uuid string.
     */
    public boolean containsUuid(final String uuidString) {
        if(this.uuidsInContext != null) {
            return this.uuidsInContext.contains(uuidString);
        }
        return false;
    }

    /**
     * Obtains the collection of resources being validated.
     * <p>
     * If the {@link #getResourceContainer() resource container} is used, all of the
     * {@link #getResourcesToValidate() resources in the context} should exist in the same container.
     * </p>
     * @return the resourcesToValidate (never <code>null</code>)
     * @since 4.2
     */
    public Resource[] getResourcesToValidate() {
        if (this.resourcesToValidate == null) {
            this.resourcesToValidate = EMPTY_RESOURCE_ARRAY;
        }
        return this.resourcesToValidate;
    }

    /**
     * Sets the collection of resources being validated.
     * <p>
     * If the {@link #getResourceContainer() resource container} is used, all of the
     * {@link #getResourcesToValidate() resources in the context} should exist in the same container.
     * </p>
     * @param eResourcesToValidate the resources to validate
     * @since 4.2
     */
    public void setResourcesToValidate(final Resource[] eResourcesToValidate) {
        this.resourcesToValidate = eResourcesToValidate;
    }

    /**
     * Sets the {@link org.eclipse.emf.ecore.resource.Resource}s that define the valid resources considered
     * to be within the validation scope. Resources that are not defined in this scope should be considered invalid
     * and should result in a validation error.
     * @param theResources the resources in scope or <code>null</code> or empty if all resources are in scope
     * @since 4.2
     */
    public void setResourcesInScope(Resource[] theResources) {
        if (theResources == null) {
            this.resourcesInScope = EMPTY_RESOURCE_ARRAY;
        } else {
            this.resourcesInScope = theResources;
        }
    }

    /**
     * Obtain all the valid {@link org.eclipse.emf.ecore.resource.Resource}s that define the validation
     * scope. If an empty collection is returned then all resources are considered to be within the validation scope.
     * @return the valid resources (never <code>null</code>)
     * @since 4.2
     */
    public Resource[] getResourcesInScope() {
        return this.resourcesInScope;
    }

    /**
     * Check if the given object is ignored during validation.
     * @return boolen true if object should not be validated
     * @since 4.2
     */
    public boolean shouldIgnore(final Object object) {
        if(this.objectsToIgnore != null) {
            return this.objectsToIgnore.contains(object);
        }
        return false;
    }

    /**
     * Add an object that should be ignored during validation.
     * @param object
     * @param recursive if true add children recursively
     * @since 4.2
     */
    public void addObjectToIgnore(final EObject eObject, final boolean recursive) {
        if(eObject != null) {
	        if(this.objectsToIgnore == null) {
	            this.objectsToIgnore = new HashSet();
	        }
	        this.objectsToIgnore.add(eObject);
	        if(recursive) {
	            for(final Iterator iter = eObject.eContents().iterator(); iter.hasNext();) {
	                addObjectToIgnore((EObject)iter.next(), recursive);
	            }
	        }
        }
    }

    /**
     * Get the container in which the {@link #getResourcesToValidate() resources} exist
     * @return the container, or null if no container is available
     * <p>
     * If the {@link #getResourceContainer() resource container} is used, all of the
     * {@link #getResourcesToValidate() resources in the context} should exist in the same container.
     * </p>
     * @since 4.2
     */
    public Container getResourceContainer() {
        return this.resourceContainer;
    }

    /**
     * Set the container in which the {@link #getResourcesToValidate() resources} exist
     * @param container the container, or null if there is no container
     * <p>
     * If the {@link #getResourceContainer() resource container} is used, all of the
     * {@link #getResourcesToValidate() resources in the context} should exist in the same container.
     * </p>
     * @since 4.2
     */
    public void setResourceContainer( final Container container ) {
        this.resourceContainer = container;
    }

    public DatatypeManager getDatatypeManager() {
        DatatypeManager dtMgr = null;
        if ( this.getResourceContainer() != null ) {
            dtMgr = this.getResourceContainer().getDatatypeManager();
        }
        Assertion.isNotNull(dtMgr);
        return dtMgr;
    }

    /**
     * Should cache validation results for SqlTransformationMappingRoots
     * @return Returns the cacheMappingRootResults.
     * @since 4.2
     */
    public boolean cacheMappingRootResults() {
        return this.cacheMappingRootResults;
    }

    /**
     * Set flag to determine if validation results for SqlTransformationMappingRoots
     * should be cached.
     * @return Returns the cacheMappingRootResults.
     * @since 4.2
     */
    public void setCacheMappingRootResults(boolean cacheMappingRootResults) {
        this.cacheMappingRootResults = cacheMappingRootResults;
    }

    /**
     * Set the absolute path to the directory containing the index files.
     * @param location The absolute path to the directory containing index files
     * @since 4.2
     */
    public void setIndexLocation(final String location) {
        this.indexDirectory = location;
    }

    /**
     * Get the directory location for the index files
     * @return The directory location for index files
     * @since 4.2
     */
    public String getIndexLocation() {
        return this.indexDirectory;
    }

    /**
     * @return Returns the useIndexesToResolve.
     * @since 4.2
     */
    public boolean useIndexesToResolve() {
        return this.useIndexesToResolve;
    }

    /**
     * @param useIndexesToResolve The useIndexesToResolve to set.
     * @since 4.2
     */
    public void setUseIndexesToResolve(boolean useIndexesToResolve) {
        this.useIndexesToResolve = useIndexesToResolve;
    }

    /**
     * @return Returns the useServerIndexes.
     * @since 4.2
     */
    public boolean useServerIndexes() {
        return this.useServerIndexes;
    }

    /**
     * @param useServerIndexes The useServerIndexes to set.
     * @since 4.2
     */
    public void setUseServerIndexes(boolean useServerIndexes) {
        this.useServerIndexes = useServerIndexes;
    }

    /**
     * Check if the context has any preferences on it.
     * @return true if has preferences else false.
     * @since 4.2
     */
    public boolean hasPreferences() {
        return preferences != null;
    }

    /**
     * Get the preferences for this context
     * @return Preferences object with validation preference info
     */
    public Preferences getPreferences() {
        return preferences;
    }

    /**
     * Get the value given a preference key, look up preferences on the context.
     * @param prefKey The pref key
     * @param context The validation context to look up preferences
     * @return String value of preference
     * @since 4.2
     */
    public String getPreferenceValue(final String prefKey) {
    	if (this.preferences != null) {
    	    String preferenceValue = this.preferences.getString(prefKey);
            if (StringUtil.isEmpty(preferenceValue)) {
    	        preferenceValue = this.preferences.getDefaultString(prefKey);
    	    }
    	    return preferenceValue;
    	}
        return null;
    }

    /**
     * Get an IStatus value for the given preference string
     * @param preferences The preferences to look up
     * @param value The key used to look up preference value
     * @return The IStatus value
     * @since 4.2
     */
	public int getPreferenceStatus(final String prefKey, final int defaultStatus) {
        if (this.preferenceStatusMap.containsKey(prefKey)) {
//System.out.println(prefKey + " = "+getValidationDescriptorValue(((Integer)this.preferenceStatusMap.get(prefKey)).intValue()));
            return ((Integer)this.preferenceStatusMap.get(prefKey)).intValue();
        }

        String value = getPreferenceValue(prefKey);
        if (value != null) {
			if (value.equals(ValidationDescriptor.IGNORE)) {
                this.preferenceStatusMap.put(prefKey, new Integer(IStatus.OK));
				return IStatus.OK;

            } else if (value.equals(ValidationDescriptor.INFO)) {
                this.preferenceStatusMap.put(prefKey, new Integer(IStatus.INFO));
                return IStatus.INFO;

            }  else if (value.equals(ValidationDescriptor.WARNING)) {
                this.preferenceStatusMap.put(prefKey, new Integer(IStatus.WARNING));
                return IStatus.WARNING;

			} else if (value.equals(ValidationDescriptor.ERROR)) {
                this.preferenceStatusMap.put(prefKey, new Integer(IStatus.ERROR));
				return IStatus.ERROR;
			}
        }

        this.preferenceStatusMap.put(prefKey, new Integer(defaultStatus));
		return defaultStatus;
	}

    protected String getValidationDescriptorValue(final int statusValue) {
        switch (statusValue) {
            case IStatus.OK: {
                return ValidationDescriptor.IGNORE;
            }
            case IStatus.INFO: {
                return ValidationDescriptor.INFO;
            }
            case IStatus.WARNING: {
                return ValidationDescriptor.WARNING;
            }
            case IStatus.ERROR: {
                return ValidationDescriptor.ERROR;
            }
            default: {
                break;
            }
        }
        return StringUtil.Constants.EMPTY_STRING;
    }

    /**
     * Clear all the results on this context
     */
    public void clearResults() {
        if (validationResults != null) {
            validationResults.clear();
        }
        if (targetValidationResults != null) {
            targetValidationResults.clear();
        }
    }

	/**
	 * Clear all the results on this context
	 */
	public void clearState() {
		clearResults();
		if(this.rulesRun != null) {
			this.rulesRun.clear();
		}
		if(this.targetTransformMap != null) {
			this.targetTransformMap.clear();
		}

		this.resourcesToValidate = null;
		this.preferences = null;
        this.preferenceStatusMap.clear();
		this.resourceContainer = null;
		this.useServerIndexes = false;
		this.useIndexesToResolve = true;
		this.indexDirectory = IndexUtil.INDEX_PATH;
		this.uuidsInContext = null;
		this.dataMap = null;
	}

	public boolean hasResults() {
		if(validationResults != null && !validationResults.isEmpty()) {
			return true;
		}
		return false;
	}

    /**
     * Collect the validation results that may be used by validation rules that use
     * this context
     * @param result ValidationResult
     */
    public void addResult(final ValidationResult result) {
        ArgCheck.isNotNull(result);
        // add the result only if it has problems
        if(result.hasProblems()) {
			if(validationResults == null) {
				validationResults = new ArrayList();
			}
	        validationResults.add(result);
	        // update the map of target to result
	        updateTargetResults(result.getTarget(), result);
        }
    }

    public ValidationResult getLastResult() {
        if(validationResults == null || validationResults.isEmpty()) {
            return null;
        }
        return (ValidationResult) validationResults.get(validationResults.size() -1);
    }

    /**
     * @return
     */
    public List getValidationResults() {
        return validationResults;
    }

    /**
     * Check if the given rule has been run given the information about the container
     * of eobject on which the rule is run.
     * @param eObject The EObject for whose container we are checking if the rule is run
     * @param ruleName The name of the rule
     * @return true if this rule has already been run, else false.
     * @since 4.2
     */
    public boolean hasRunRule(final EObject eObject, final String ruleName) {
        ArgCheck.isNotNull(eObject);
        if(rulesRun != null) {
            String containerInfo = getContainerInfo(eObject);
            return hasRunRule(containerInfo, ruleName);
        }
        return false;
    }

    /**
     * Check if the given rule has been run given the information about the container
     * of eobject on which the rule is run.
     * @param containerInfo Can be eObject uuid, resource uri or any other name that
     * uniquely identifies a container.
     * @param ruleName The name of the rule
     * @return true if this rule has already been run, else false.
     * @since 4.2
     */
    public boolean hasRunRule(final String containerInfo, final String ruleName) {
        ArgCheck.isNotNull(containerInfo);
        if(rulesRun != null) {
            Collection ruleNames = (Collection) rulesRun.get(containerInfo);
            if(ruleNames != null && ruleNames.contains(ruleName)) {
                return true;
            }
        }
        return false;
    }

    private String getContainerInfo(EObject eObject) {
        ArgCheck.isNotNull(eObject);
        // container of the eObject
        EObject container = eObject.eContainer();
        if(container != null) {
            return ModelerCore.getObjectIdString(container);
        }
        return eObject.eResource().getURI().toString();
    }

   /**
    * Record rule name for a partucular container of a eObject, indicating this rule need not be
    * run in future for this eObject.
    * @param eObject The eObject for whose container this rule needs to be recorded
    * @param ruleName The name of the rule
    * @return true if this rule has already been run, else false.
    */
    public void recordRuleRun(final EObject eObject, final String ruleName) {
        ArgCheck.isNotNull(eObject);
        String containerInfo = getContainerInfo(eObject);
        recordRuleRun(containerInfo, ruleName);
    }

    /**
     * Record rule name for a partucular container of a eObject, indicating this rule need not be
     * run in future for this eObject.
     * @param containerInfo Can be eObject uuid, resource uri or any other name that
     * uniquely identifies a container.
     * @param ruleName The name of the rule
     * @return true if this rule has already been run, else false.
     */
     public void recordRuleRun(final String containerInfo, final String ruleName) {
         ArgCheck.isNotNull(containerInfo);
         if(rulesRun == null) {
             rulesRun = new HashMap();
         }
         Collection ruleNames = (Collection) rulesRun.get(containerInfo);
         if(ruleNames == null) {
         	// not may rules run per container (initialize to 1)
             ruleNames = new HashSet(1);
         }
         ruleNames.add(ruleName);
         rulesRun.put(containerInfo, ruleNames);
     }

	public Map getTargetTransformMap() {
		return targetTransformMap;
	}

	public void addTargetTransform(final EObject target, final EObject transform) {
		if(targetTransformMap == null) {
			targetTransformMap = new HashMap();
		}
		if(target != null) {
		    targetTransformMap.put(target, transform);
		}
	}

	/**
     * @return Returns the targetValidationResults.
     * @since 4.2
     */
    public Collection getTargetResults(final EObject target) {
        if(this.targetValidationResults != null) {
            return (Collection)this.targetValidationResults.get(target);
        }
        return Collections.EMPTY_LIST;
    }

    public IStatus getTargetStatus(final EObject target) {
	    // construct a multi status to which we add all the validation results
	    final MultiStatus status = new MultiStatus(ModelerCore.PLUGIN_ID, 0, "", null); //$NON-NLS-1$
	    final Collection results = getTargetResults(target);
	    if(results != null && !results.isEmpty()) {
		    for(Iterator iter = results.iterator(); iter.hasNext();) {
		        final ValidationResult result = (ValidationResult) iter.next();
		        final ValidationProblem[] problems = result.getProblems();
		        for(int i=0; i < problems.length; i++) {
		            status.add(problems[i].getStatus());
		        }
		    }
	    }

	    return status;
    }

	/**
     * @return Returns the targetValidationResults.
     * @since 4.2
     */
    protected void updateTargetResults(final Object target, final ValidationResult validationResult) {
        if(this.targetValidationResults == null) {
            this.targetValidationResults = new HashMap();

        }
        // get the current results
        Collection results = (Collection) this.targetValidationResults.get(target);
        if(results == null) {
            // usually one result/obj
            results = new HashSet(1);
        }
        results.add(validationResult);
        // update the map
        this.targetValidationResults.put(target, results);
    }

    /**
     * Obtains the data associated with the specified key.
     * @param theKey the key whose data is being requested
     * @return the data or <code>null</code> if none found
     * @since 4.2
     */
    public Object getData(String theKey) {
        return (this.dataMap == null) ? null : this.dataMap.get(theKey);
    }

    /**
     * Sets the data associated with the specified key. If the key already exists it's data is overwritten.
     * @param theKey the key
     * @param theValue the data
     * @since 4.2
     */
    public void setData(String theKey,
                        Object theValue) {
        ArgCheck.isNotNull(theKey);

        if (this.dataMap == null) {
            this.dataMap = new HashMap();
        }

        this.dataMap.put(theKey, theValue);
    }

}
