/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.xml.wizards;

import org.teiid.core.designer.PluginUtil;
import org.teiid.core.designer.util.I18nUtil;


/** 
 * The <code>RequestSelectionPage</code> allows the user to select XSD files from the file system which will be
 * used to generate the new model.
 * @since 8.0
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
