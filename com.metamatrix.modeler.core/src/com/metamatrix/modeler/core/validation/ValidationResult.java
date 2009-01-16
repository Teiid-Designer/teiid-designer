/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.core.validation;


/**
 * ValidationResult
 */
public interface ValidationResult {

    /**
     * Get the target EObject for which this result has be created
     * @return an EObject for the validationResult
     */    
    Object getTarget();

    /**
     * Return the array of ValidationProblems-never null
     * @return the array of ValidationProblems-never null
     */
    ValidationProblem[] getProblems();

    /**
     * Checks if the result has any validation problems
     * @return a boolean indicating any validation problems
     */
    boolean hasProblems();

    /**
     * Add the given problem to the array of ValidationProblems
     * @param problem
     */
    void addProblem(final ValidationProblem problem);

    /**
     * Check if the current result has contains fatal problems,
     * if the result is fatal furthur validation of the EObject should
     * stop.
     * @param eObject The eObject that is fatal.
     * @return true if the result has problems of ERROR sevierity
     */
    boolean isFatalObject(Object eObject);

    /**
     * Check if the result is fatal and furthure validation of the Resource
     * should stop.
     * @return true if any of the rules determine that the current
     * resource should not be validated any furthur.
     */
    boolean isFatalResource();

    /**
     * Set the flag indicating that the resource has a fatal problem and
     * furthure validation should stop.
     * @param true if any of the rules determine that the current
     * resource should not be validated any furthur.
     */
    void setFatalResource(boolean fatal);

    /**
     * Return a string representing the path within the model to the
     * entity containing the error. The returned value is a logical path 
     * and should not be used for EObject resolution.
     * @return location string.  May be null.
     */
    String getLocationPath();

    /**
     * Set the string representing the path within the model to the
     * entity containing the error.
     * @param locationPath path within the model; may be null.
     */
    void setLocationPath(String locationPath);

    /**
     * Return the URI fragment used to locate the EObject that logically
     * contains the error.  The logical location represents the visual 
     * entity within the primary model and may not be the same as the target 
     * location. The EObject can be found by calling 
     * <p>
     * <code>ModelerCore.getModelContainer().getEObject(uriFragment, false);</code>
     * </p>
     * @return location uri fragment.  May be null.
     */
    String getLocationUri();

    /**
     * Return the URI fragment used to locate the EObject that is the target of 
     * the error. The EObject can be found by calling 
     * <p>
     * <code>ModelerCore.getModelContainer().getEObject(uriFragment, false);</code>
     * </p>
     * @return target uri fragment.  May be null.
     */
    String getTargetUri();

}
