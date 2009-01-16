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

package com.metamatrix.modeler.modelgenerator.xml.wizards;

import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.I18nUtil;

/** 
 * The <code>RequestSelectionPage</code> allows the user to select XSD files from the file system which will be
 * used to generate the new model.
 * @since 4.2
 */
public final class RequestSelectionPage extends XsdSelectionPage
{
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /** Used as a prefix to properties file keys. */
    private static final String PREFIX = I18nUtil.getPropertyPrefix(RequestSelectionPage.class);
    
    /**
     * Constructs a <code>RequestSelectionPage</code> using the specified builder.
     * @param theBuilder the model builder
     * @since 4.2
     */
    public RequestSelectionPage(XsdAsRelationalImportWizard importWizard,
                                PluginUtil util) {
        super(importWizard, PREFIX, util); 
    }
    
}
