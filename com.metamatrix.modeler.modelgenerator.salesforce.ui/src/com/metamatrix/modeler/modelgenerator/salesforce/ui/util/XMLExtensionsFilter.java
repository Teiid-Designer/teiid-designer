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

package com.metamatrix.modeler.modelgenerator.salesforce.ui.util;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.ui.views.navigator.ResourcePatternFilter;

public class XMLExtensionsFilter extends ViewerFilter {
	
	private ResourcePatternFilter filter;

	private static String[] patterns;

	static {
		patterns = new String[] {
		  new String("XML*Extensions.xmi"), //$NON-NLS-1$
		  new String(".project") //$NON-NLS-1$
		};
	}
	
	public XMLExtensionsFilter() {
		filter = new ResourcePatternFilter();
		filter.setPatterns(patterns);
	}
	
	@Override
    public boolean select(Viewer arg0, Object arg1, Object arg2) {
		return filter.select(arg0, arg1, arg2);
	}
}
