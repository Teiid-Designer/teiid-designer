/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.webservice.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.util.ModelVisitor;
import com.metamatrix.modeler.core.util.ModelVisitorProcessor;


/** 
 * WebServiceUtil
 */
public class WebServiceUtil {

    /** 
     * 
     * @since 4.2
     */
    public WebServiceUtil() {
        super();
    }

    protected static void executeVisitor( final Object container, final ModelVisitor visitor, final int depth ) {
        final ModelVisitorProcessor processor = new ModelVisitorProcessor(visitor);
        try {
            if ( container instanceof Resource ) {
                processor.walk((Resource)container,depth);
            } else if ( container instanceof EObject ) {
                processor.walk((EObject)container,depth);
            }
        } catch (ModelerCoreException e) {
            ModelerCore.Util.log(e);
        }
    }

    /**
     * Add any {@link Interface} instances found under the supplied container
     * @param container the EObject or Resource under which the keys are to be found; may not be null
     * @return the keys that were found; may not be null
     */
    public static List findInterfaces( final Object container ) {
        return findInterfaces(container, ModelVisitorProcessor.DEPTH_INFINITE);
    }

    /**
     * Add any {@link Interface} instances found under the supplied container
     * @param container the EObject or Resource under which the keys are to be found; may not be null
     * @param depth how deep to search beneath the container, see {@link ModelVisitorProcessor}
     * @return the keys that were found; may not be null
     */
    public static List findInterfaces( final Object container, final int depth ) {
        final InterfaceFinder finder = new InterfaceFinder();
		return findObjects(finder, container, depth);
    }

    /**
     * Add any {@link Interface} instances found in the collection of resources
     * @param container the EObject or Resource under which the keys are to be found; may not be null
     * @return the keys that were found; may not be null
     */
    public static List findInterfaces( final Collection resources ) {
        return findInterfaces(resources,ModelVisitorProcessor.DEPTH_INFINITE);
    }
    
    /**
     * Add any {@link Interface} instances found in the collection of resources
     * @param container the EObject or Resource under which the keys are to be found; may not be null
     * @return the keys that were found; may not be null
     */
    public static List findInterfaces( final Collection resources, final int depth ) {
        List interfaces = new ArrayList();
        for(Iterator iter = resources.iterator(); iter.hasNext();) {
            interfaces.addAll(findInterfaces(iter.next(),depth));
        }
        return interfaces;
    }

    /**
     * Add any {@link Operation} instances found in the collection of resources
     * @param container the EObject or Resource under which the keys are to be found; may not be null
     * @return the keys that were found; may not be null
     */
    public static List findOperations( final Collection resources ) {
        List operations = new ArrayList();
        for(Iterator iter = resources.iterator(); iter.hasNext();) {
            operations.addAll(findOperations(iter.next(), ModelVisitorProcessor.DEPTH_INFINITE));
        }
        return operations;
    }    

    /**
     * Add any {@link Operation} instances found under the supplied container
     * @param container the EObject or Resource under which the keys are to be found; may not be null
     * @return the keys that were found; may not be null
     */
    public static List findOperations( final Object container ) {
        return findOperations(container, ModelVisitorProcessor.DEPTH_INFINITE);
    }

    /**
     * Add any {@link Operation} instances found under the supplied container
     * @param container the EObject or Resource under which the keys are to be found; may not be null
     * @param depth how deep to search beneath the container, see {@link ModelVisitorProcessor}
     * @return the keys that were found; may not be null
     */
    public static List findOperations( final Object container, final int depth ) {
        final OperationFinder finder = new OperationFinder();
		return findObjects(finder, container, depth);
    }

    /**
     * Add any {@link Input} instances found under the supplied container
     * @param container the EObject or Resource under which the keys are to be found; may not be null
     * @return the keys that were found; may not be null
     */
    public static List findInputs( final Object container ) {
        return findInputs(container, ModelVisitorProcessor.DEPTH_INFINITE);
    }

    /**
     * Add any {@link Input} instances found under the supplied container
     * @param container the EObject or Resource under which the keys are to be found; may not be null
     * @param depth how deep to search beneath the container, see {@link ModelVisitorProcessor}
     * @return the keys that were found; may not be null
     */
    public static List findInputs( final Object container, final int depth ) {
        final InputFinder finder = new InputFinder();
		return findObjects(finder, container, depth);
    }

    /**
     * Add any {@link Output} instances found under the supplied container
     * @param container the EObject or Resource under which the keys are to be found; may not be null
     * @return the keys that were found; may not be null
     */
    public static List findOutputs( final Object container ) {
        return findOutputs(container, ModelVisitorProcessor.DEPTH_INFINITE);
    }

    /**
     * Add any {@link Output} instances found under the supplied container
     * @param container the EObject or Resource under which the keys are to be found; may not be null
     * @param depth how deep to search beneath the container, see {@link ModelVisitorProcessor}
     * @return the keys that were found; may not be null
     */
    public static List findOutputs( final Object container, final int depth ) {
        final OutputFinder finder = new OutputFinder();
		return findObjects(finder, container, depth);
    }

    /**
     * Add any {@link Message} instances found under the supplied container
     * @param container the EObject or Resource under which the keys are to be found; may not be null
     * @return the keys that were found; may not be null
     */
    public static List findMessages( final Object container ) {
        return findMessages(container, ModelVisitorProcessor.DEPTH_INFINITE);
    }

    /**
     * Add any {@link Message} instances found under the supplied container
     * @param container the EObject or Resource under which the keys are to be found; may not be null
     * @param depth how deep to search beneath the container, see {@link ModelVisitorProcessor}
     * @return the keys that were found; may not be null
     */
    public static List findMessages( final Object container, final int depth ) {
        final MessageFinder finder = new MessageFinder();
		return findObjects(finder, container, depth);
    }    

	/**
	 * Add any objects found under the supplied container
	 * @param container the EObject or Resource under which the objects are to be found; may not be null
	 * @return the objects that were found; may not be null
	 */
	private static List findObjects(final WebServiceComponentFinder finder, final Object container, final int depth  ) {
		executeVisitor(container,finder,depth);
		// the container is collected along with childre
		// remove the container from results
		finder.removeContainer(container);
		return finder.getObjects();		
	}
}
