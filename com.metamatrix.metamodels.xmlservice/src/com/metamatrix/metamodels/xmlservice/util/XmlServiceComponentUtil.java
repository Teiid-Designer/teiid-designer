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

package com.metamatrix.metamodels.xmlservice.util;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.util.ModelVisitor;
import com.metamatrix.modeler.core.util.ModelVisitorProcessor;

/**
 * RelationshipUtil.java
 */
public class XmlServiceComponentUtil {

	/**
	 * Prevent allocation
	 */
	private XmlServiceComponentUtil() {
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
	 * Add any {@link XmlOperation} instances found under the supplied container
	 * @param container the Resource under which the xmlOperations are to be found; may not be null
	 * @return the xmlOperations that were found; may not be null
	 */
	public static List findXmlOperations( final Object container ) {
		return findXmlOperations(container, ModelVisitorProcessor.DEPTH_INFINITE);
	}

	/**
	 * Add any {@link XmlOperation} instances found under the supplied container
	 * @param container the Resource under which the xmlOperations are to be found; may not be null
	 * @param depth how deep to search beneath the container, see {@link ModelVisitorProcessor}
	 * @return the xmlOperations that were found; may not be null
	 */
	public static List findXmlOperations( final Object container, final int depth ) {
		final XmlOperationFinder finder = new XmlOperationFinder();
		executeVisitor(container,finder,depth);
		return finder.getObjects();
	}
	
	/**
	 * Add any {@link XmlInput} instances found under the supplied container
	 * @param container the Resource under which the xmlInputs are to be found; may not be null
	 * @return the xmlInputs that were found; may not be null
	 */
	public static List findXmlInputs( final Object container ) {
		return findXmlInputs(container, ModelVisitorProcessor.DEPTH_INFINITE);
	}

	/**
	 * Add any {@link XmlInput} instances found under the supplied container
	 * @param container the Resource under which the xmlInputs are to be found; may not be null
	 * @param depth how deep to search beneath the container, see {@link ModelVisitorProcessor}
	 * @return the xmlInputs that were found; may not be null
	 */
	public static List findXmlInputs( final Object container, final int depth ) {
		final XmlInputFinder finder = new XmlInputFinder();
		executeVisitor(container,finder,depth);
		return finder.getObjects();
	}

}
