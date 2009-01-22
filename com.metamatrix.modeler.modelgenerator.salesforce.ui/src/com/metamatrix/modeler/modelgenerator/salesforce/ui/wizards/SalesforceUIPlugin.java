/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.salesforce.ui.wizards;

import org.eclipse.ui.plugin.AbstractUIPlugin;

public class SalesforceUIPlugin extends AbstractUIPlugin {

    private static SalesforceUIPlugin plugin;

    public SalesforceUIPlugin() {
        plugin = this;
    }

    public static AbstractUIPlugin getDefault() {
        return plugin;
    }

}
