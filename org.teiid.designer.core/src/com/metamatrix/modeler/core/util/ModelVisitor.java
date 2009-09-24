/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.util;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import com.metamatrix.modeler.core.ModelerCoreException;

/** 
 * This interface is used to walk/visit {@link EObject model objects}.
 * <p> 
 * Define a new visitor by implementing this interface:
 * <pre>
 * class MyVisitor implements ModelVisitor {
 *    public boolean visit( EObject object) throws ModelerCoreException {
 *       // your code here
 *       return true;
 *    }
 *    public boolean visit( Resource resource) throws ModelerCoreException {
 *       // your code here
 *       return true;
 *    }
 * }
 * </pre>
 * and use the visitor by creating and using a {@link ModelVisitorProcessor}:
 * <pre>
 * final ModelVisitor visitor = new MyVisitor();
 * final ModelVisitorProcessor processor = new ModelVisitorProcessor(visitor);
 * try {
 *    processor.walk(startingPoint,ModelVisitorProcessor.DEPTH_INFINITE);
 * } catch (ModelerCoreException e) {
 *    // handle the exception
 * }
 * </pre>
 * where <code>startingPoint</code> can be one of the following:
 * <ul>
 *   <li>An {@link EObject}</li>
 *   <li>An {@link Resource EMF Resource}</li>
 *   <li>A {@link com.metamatrix.modeler.core.workspace.ModelResource ModelResource}</li>
 *   <li>A {@link com.metamatrix.modeler.core.workspace.ModelFolder ModelFolder}</li>
 *   <li>A {@link com.metamatrix.modeler.core.workspace.ModelProject ModelProject}</li>
 *   <li>A {@link com.metamatrix.modeler.core.workspace.ModelWorkspace ModelWorkspace}</li>
 *   <li>A {@link List list} of any of the above objects</li>
 * </ul>
 * and where <code>depth</code> is one of the following:
 * <ul>
 *   <li>{@link com.metamatrix.modeler.core.util.ModelVisitorProcessor#DEPTH_INFINITE ModelVisitorProcessor.DEPTH_INFINITE}</li>
 *   <li>{@link com.metamatrix.modeler.core.util.ModelVisitorProcessor#DEPTH_ONE ModelVisitorProcessor.DEPTH_ONE}</li>
 *   <li>{@link com.metamatrix.modeler.core.util.ModelVisitorProcessor#DEPTH_ZERO ModelVisitorProcessor.DEPTH_ZERO}</li>
 * </ul>
 * </p><p>
 * Note that regardless of the <code>startingPoint</code>, the ModelVisitor implementation's methods
 * are only called for {@link Resource EMF Resource} and {@link EObject} instances.
 * </p>
 * @see ModelVisitorProcessor
 */
public interface ModelVisitor {

    /**
     * Visit the supplied {@link ModelWorkspaceItem item}. 
     * <p>
     * The default implementation of this method simply returns true.
     * </p>
     * @param item the item to visit; never null
     * @return true if the children of <code>item</code> should be visited, or false if they should not.
     * @throws ModelerCoreException if the visit fails for some reason
     */
    public boolean visit( EObject object) throws ModelerCoreException;

    /**
     * Visit the supplied {@link ModelWorkspaceItem item}. 
     * <p>
     * The default implementation of this method simply returns true.
     * </p>
     * @param item the item to visit; never null
     * @return true if the children of <code>item</code> should be visited, or false if they should not.
     * @throws ModelerCoreException if the visit fails for some reason
     */
    public boolean visit( Resource resource) throws ModelerCoreException;
}
