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

package com.metamatrix.modeler.ui.wizards;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.wizard.IWizard;

import com.metamatrix.core.plugin.PluginUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.product.IModelerProductContexts;
import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 * This manager maintains and gives access to a current map/list of any ImportWizard contributions.
 * @author BLaFond
 *
 */

public abstract class ImportWizardExtensionManager {
//    private static Collection wizards;
    private static HashMap<String, IConfigurationElement> wizardExtMap = new HashMap<String, IConfigurationElement>();
    private static boolean wizardsLoaded = false;
    
//    private static HashMap getIWizardExtensions(Object selection) {
//        if( !wizardsLoaded ) {
//            loadExtensions();
//        }
//        return wizardExtMap;
//    }
    
    //============================================================================================================================
    // Overridden Methods
    
    protected IConfigurationElement[] getConfigurationElementsFor() {
        IConfigurationElement[] elements = PluginUtilities.getConfigurationElementsFor(UiConstants.ExtensionPoints.ImportWizards.ID);
        
        // FILTER THESE!!
        List<IConfigurationElement> result = new ArrayList<IConfigurationElement>(elements.length);
        for( int i=0; i< elements.length; i++ ) {
            String contribID = elements[i].getAttribute(UiConstants.ExtensionPoints.ImportWizards.ID_ID);
            if (UiPlugin.getDefault().isProductContextValueSupported(IModelerProductContexts.Contributions.IMPORT, contribID)) {
                result.add(elements[i]);
            }
        }

        return result.toArray(new IConfigurationElement[result.size()]);
    }
    
    private static void loadExtensions() {
        wizardsLoaded = true;

        IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(UiConstants.ExtensionPoints.ImportWizards.ID);
        
        // get the all extensions to the extension point
        IExtension[] extensions = extensionPoint.getExtensions();
        
        // walk through the extensions and find all implementations
        for ( int i=0 ; i<extensions.length ; ++i ) {
            IConfigurationElement[] elements = extensions[i].getConfigurationElements();
            try {
                // Find the contribution class & Id, check if supported, and add to classname map
                for ( int j=0 ; j<elements.length ; ++j ) {
                    String contribID = elements[j].getAttribute(UiConstants.ExtensionPoints.ImportWizards.ID_ID);
                    if (UiPlugin.getDefault().isProductContextValueSupported(IModelerProductContexts.Contributions.IMPORT, contribID)) {
                        wizardExtMap.put(contribID, elements[j]);
                    }
                }
            
            } catch (Exception e) {
                // catch any Exception that occurred obtaining the configuration and log it
                String message = UiConstants.Util.getString("ImportWizardExtensionManager.loadingExtensionsErrorMessage", //$NON-NLS-1$
                            extensions[i].getUniqueIdentifier()); 
                UiConstants.Util.log(IStatus.ERROR, e, message);
            }
        }

    }
    
    public static IWizard getWizard(String importWizardId) {
        if( !wizardsLoaded ) {
            loadExtensions();
        }
        
        IConfigurationElement wizardExtElement = wizardExtMap.get(importWizardId);
        if( wizardExtElement != null ) {
            String wizardClassName = wizardExtElement.getAttribute(UiConstants.ExtensionPoints.ImportWizards.CLASS);
            if( wizardClassName != null ) {
                try {
                    return (IWizard)wizardExtElement.createExecutableExtension(UiConstants.ExtensionPoints.ImportWizards.CLASS);
                } catch (final CoreException err) {
                    UiConstants.Util.log(err);
                    WidgetUtil.showError(err);
                    return null;
                }
            }
        }
        
        return null;
    }
}

