/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.util;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.modeler.core.ModelerCoreException;

/**
 * The ConcurrentModelVisitorProcessor is used to walk a {@link ModelVisitor} implementation through one or more
 * models.  This class extends ModelVisitorProcessor overloading implementations of the walk method in order
 * to add logic to prevent a ConcurrentModificationException while processing a list of children.  The logic
 * to prevent a ConcurrentModificationException is to create an array copy of the child list before iterating
 * over the children.  As a result of the array copy operations ConcurrentModelVisitorProcessor is less
 * efficient that using ModelVisitorProcessor.
 * <p>
 * To use, simply create a {@link ModelVisitor visitor} and a ModelProcessor, and use the processor
 * to walk one or more objects by supply the starting point(s):
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
 *   <li>{@link com.metamatrix.modeler.core.util.ConcurrentModelVisitorProcessor#DEPTH_INFINITE ModelVisitorProcessor.DEPTH_INFINITE}</li>
 *   <li>{@link com.metamatrix.modeler.core.util.ConcurrentModelVisitorProcessor#DEPTH_ONE ModelVisitorProcessor.DEPTH_ONE}</li>
 *   <li>{@link com.metamatrix.modeler.core.util.ConcurrentModelVisitorProcessor#DEPTH_ZERO ModelVisitorProcessor.DEPTH_ZERO}</li>
 * </ul>
 * </p>
 * @see ModelVisitor.
 */
public class ConcurrentModelVisitorProcessor extends ModelVisitorProcessor {

    /**
     * Construct an instance of ModelWalker.
     * 
     */
    public ConcurrentModelVisitorProcessor( final ModelVisitor visitor ) {
        this(visitor,MODE_DEFAULT);
    }
    
    /**
     * Construct an instance of ModelWalker.
     * 
     */
    public ConcurrentModelVisitorProcessor( final ModelVisitor visitor, final int mode ) {
        super(visitor,mode);
    }

    /**
     * Walk the supplied {@link Resource} to the supplied depth.  
     * The {@link #getModelVisitor() model visitor} is only called when an {@link EObject} or
     * {@link Resource EMF Resource} is found.  Of course, because the {@link #getModelVisitor() model visitor}
     * can decide whether or not children are to be visited, the supplied depth is really considered
     * to be the maximum depth allowed.
     * @param startingResource the EMF resource to be walked; may not be null
     * @param depth the depth to walk; must be one of {@link #DEPTH_INFINITE DEPTH_INFINITE},
     * {@link #DEPTH_ONE DEPTH_ONE} or {@link #DEPTH_ZERO DEPTH_ZERO}
     * @throws ModelerCoreException if there is an exception walking the resource
     */
    @Override
    public void walk( final Resource startingResource, final int depth ) throws ModelerCoreException {
        ArgCheck.isNotNull(startingResource);
        assertValidDepth(depth);
        
        // visit this resource      
        if (!getModelVisitor().visit(startingResource) || depth == DEPTH_ZERO)
            return;
            
        // visit the children - use an array to avoid ConcurrentModificationException on contents list
        final int nextDepth = (depth == DEPTH_INFINITE ? DEPTH_INFINITE : DEPTH_ZERO );
        final Object[] children = startingResource.getContents().toArray();
        for (int i = 0; i != children.length; ++i) {
            final Object obj = children[i];
            if ( obj instanceof EObject ) {
                walk( (EObject)obj, nextDepth );
            }
        }
    }

    /**
     * Walk to the supplied depth the tree of model objects below the supplied {@link EObject}.  
     * The {@link #getModelVisitor() model visitor} is only called when an {@link EObject} or
     * {@link Resource EMF Resource} is found.  Of course, because the {@link #getModelVisitor() model visitor}
     * can decide whether or not children are to be visited, the supplied depth is really considered
     * to be the maximum depth allowed.
     * @param startingResource the root of the tree of model objects to be walked; may not be null
     * @param depth the depth to walk; must be one of {@link #DEPTH_INFINITE DEPTH_INFINITE},
     * {@link #DEPTH_ONE DEPTH_ONE} or {@link #DEPTH_ZERO DEPTH_ZERO}
     * @throws ModelerCoreException if there is an exception walking the model objects
     */
    @Override
    public void walk( final EObject startingObject, final int depth ) throws ModelerCoreException {
        ArgCheck.isNotNull(startingObject);
        assertValidDepth(depth);

        // visit this resource      
        if (!this.visitor.visit(startingObject) || depth == DEPTH_ZERO)
            return;

//        final URI uri = EcoreUtil.getURI(startingObject);
//        System.out.println("Visiting " + uri.fragment() + " (" + startingObject.eClass().getName() + ")");   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
            
        // visit the children
        final int nextDepth = (depth == DEPTH_INFINITE ? DEPTH_INFINITE : DEPTH_ZERO );

        final Object children = this.navigator.getChildren(startingObject);
        if ( children instanceof List ) {
            final Object[] tmp = ((List)children).toArray();
            for (int i = 0; i != tmp.length; ++i) {
                final Object obj = tmp[i];
                if ( obj instanceof EObject ) {
                    walk( (EObject)obj, nextDepth );
                }
            }
        } else if ( children instanceof EObject ) {
            walk((EObject)children,nextDepth);
        }

        if ( this.visitor instanceof ModelVisitorWithFinish ) {
            ((ModelVisitorWithFinish)this.visitor).postVisit(startingObject);
        }
    }
    
}
