/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.help.examples;

import org.eclipse.osgi.util.NLS;

/**
 *
 */
public class HelpExamplesMessages extends NLS {

    public static String WizardPageMessage;

    public static String WizardPageSourcePrompt;

    public static String WizardPageTitle;

    // ===========================================================================================================================
    // Class Initializer
    // ===========================================================================================================================

    static {
        // load message values from bundle file
        NLS.initializeMessages("com.metamatrix.modeler.help.examples.helpExamplesMessages", HelpExamplesMessages.class); //$NON-NLS-1$
    }
}
