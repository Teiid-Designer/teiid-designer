/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.builder;

import java.util.Collection;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.validation.ValidationContext;

/**
 * ResourceValidator
 */
public interface ResourceValidator {

    /**
     * Return true if this validator instance can be used to validate the given Object.
     * 
     * @param obj - may be null
     * @return
     */
    boolean isValidatorForObject( Object obj );

    /**
     * Validate the given Object
     * 
     * @param obj is the Object to validate; may be null
     * @param context is the validation content to use; may not be null
     * @throws ModelerCoreException if this validator can not be used to validate the given object or if any of the params are
     *         null
     */
    void validate( IProgressMonitor monitor,
                   Object obj,
                   ValidationContext context ) throws ModelerCoreException;

    /**
     * Create {@link org.eclipse.core.resources.IMarker} instances with the given IResource
     * 
     * @param context
     * @param iResource
     * @throws ModelerCoreException
     * @since 4.2
     */
    void addMarkers( ValidationContext context,
                     IResource iResource ) throws ModelerCoreException;

    /**
     * Return true if this validator instance can be used to validate the given IResource
     * 
     * @param iResource - may not be null
     * @return true if this validator instance can be used to validate the given iResource
     * @deprecated replaced by isValidatorForObject(Object)
     */
    @Deprecated
    boolean isValidatorForResource( IResource iResource );

    /**
     * Validate the given resource for the given IResource
     * 
     * @param resource - may be null
     * @param iResource - may not be null
     * @param context - may not be null
     * @throws ModelerCoreException if this validate can not be used to validate the given resource or if any of the params are
     *         null
     * @deprecated replaced by validate(IProgressMonitor,Object,ValidationContext)
     */
    @Deprecated
    void validate( IProgressMonitor monitor,
                   Resource resource,
                   IResource iResource,
                   ValidationContext context ) throws ModelerCoreException;

    /**
     * Called immediately after validation starts for multiple resources.
     * 
     * @param resources The {@link IResources resources} being validated; never null
     * @param context The {@link ValidationContext context} to use for validation; never null
     */
    void validationStarted( Collection resources,
                            ValidationContext context );

    /**
     * Called immediately after validation ends for multiple resources.
     * 
     * @param context The {@link ValidationContext context} to use for validation; never null
     */
    void validationEnded( ValidationContext context );
}
