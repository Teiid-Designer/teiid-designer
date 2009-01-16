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

package com.metamatrix.modeler.tools.textimport.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;

import com.metamatrix.modeler.tools.textimport.ui.wizards.ITextImportMainPage;

public abstract class TextImportContributionManager {
    private static final String ID              = "com.metamatrix.modeler.tools.textimport.ui.textImportContributor"; //$NON-NLS-1$
    private static final String CLASS           = "class"; //$NON-NLS-1$
    private static final String CLASSNAME       = "name"; //$NON-NLS-1$
    private static final String NO_DESCRIPTION  = "No Description"; //$NON-NLS-1$
    private static final String NO_SAMPLE_DATA  = "No Sample Data"; //$NON-NLS-1$
    private final static String TABS_1_KEY = "|1|"; //$NON-NLS-1$
    private final static String TABS_2_KEY = "|2|"; //$NON-NLS-1$
    private final static String TABS_3_KEY = "|3|"; //$NON-NLS-1$
    private final static String TABS_4_KEY = "|4|"; //$NON-NLS-1$
    private final static String TABS_1_STR =   "	 "; //$NON-NLS-1$
    private final static String TABS_2_STR =   "          "; //$NON-NLS-1$
    private final static String TABS_3_STR =   "               "; //$NON-NLS-1$
    private final static String TABS_4_STR =   "                    "; //$NON-NLS-1$
    
    private static IExtension[] contributions;
    private static String[] types;
    private static String[] descriptions;
    private static String[] sampleData;
    private static boolean importersLoaded = false;
    
    private static void loadHelperExtensions() {
        importersLoaded = true;
        
        // get the NewChildAction extension point from the plugin class
        IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(ID);
        
        // get the all extensions to the NewChildAction extension point
        contributions = extensionPoint.getExtensions();
    }
    
    private static ITextImportMainPage[] createImporters() {
    	boolean firstTime = false;
    	if( !importersLoaded ) {
    		firstTime = true;
    		loadHelperExtensions();
    	}
    	List importers = new ArrayList(contributions.length);
    	
        // walk through the extensions and find all INewChildAction implementations
        for ( int i=0 ; i<contributions.length ; ++i ) {
            IConfigurationElement[] elements = contributions[i].getConfigurationElements();
            try {

                // first, find the content provider instance and add it to the instance list
                for ( int j=0 ; j<elements.length ; ++j ) {
                    if ( elements[j].getName().equals(CLASS)) {
                        Object newPage = elements[j].createExecutableExtension(CLASSNAME);
                        if ( newPage instanceof ITextImportMainPage ) {
                        	importers.add(newPage);
                        }
                    }
                }
            
            } catch (Exception e) {
                // catch any Exception that occurred obtaining the configuration and log it
                String message = UiConstants.Util.getString("TextImportContributionManager.loadHelperExtensionsErrorMessage", //$NON-NLS-1$
                            contributions[i].getUniqueIdentifier()); 
                UiConstants.Util.log(IStatus.ERROR, e, message);
            }
        }
        ITextImportMainPage[] pages = (ITextImportMainPage[])importers.toArray(new ITextImportMainPage[importers.size()]);
        if( firstTime ) {
        	// Let's fill the type, description and sample data arrays
        	types = new String[importers.size()];
        	descriptions = new String[importers.size()];
        	sampleData = new String[importers.size()];
        	for( int k=0; k<pages.length; k++ ) {
        		types[k] = pages[k].getType();
        		descriptions[k] = parseDescription(pages[k].getDescriptionText());
        		sampleData[k] = pages[k].getSampleDataText();
        	}
        }

        return pages;
    }

    public static ITextImportMainPage[] getTextImporters() {
    	return createImporters();
    }
    
    public static String getDescription(String type) {
    	for( int k=0; k<types.length; k++ ) {
    		if( types[k].equals(type) ) {
    			return descriptions[k];
    		}
    	}
    	return NO_DESCRIPTION;
    }
    
    
    public static String getSampleData(String type) {
    	for( int k=0; k<types.length; k++ ) {
    		if( types[k].equals(type) ) {
    			return sampleData[k];
    		}
    	}
    	return NO_SAMPLE_DATA;
    }
    
    public static String[] getTypes() {
    	return types;
    }
    
    private static String parseDescription(String descString) {
    	StringBuffer buff = new StringBuffer(descString);
    	// Note:  Looking for TAB identifiers to better format the string
    	// |x| is the key where x = number of tabs.  Look for |1|, |2|, |3|, etc... (max = 4)
    	int iStart = 0;
    	int iEnd = 0;
    	while( buff.indexOf(TABS_1_KEY) > -1) {
    		iStart = buff.indexOf(TABS_1_KEY);
    		iEnd = iStart+3;
    		buff.replace(iStart, iEnd, TABS_1_STR);
    	}
    	while( buff.indexOf(TABS_2_KEY) > -1) {
    		iStart = buff.indexOf(TABS_2_KEY);
    		iEnd = iStart+3;
    		buff.replace(iStart, iEnd, TABS_2_STR);
    	}
    	while( buff.indexOf(TABS_3_KEY) > -1) {
    		iStart = buff.indexOf(TABS_3_KEY);
    		iEnd = iStart+3;
    		buff.replace(iStart, iEnd, TABS_3_STR);
    	}
    	while( buff.indexOf(TABS_4_KEY) > -1) {
    		iStart = buff.indexOf(TABS_4_KEY);
    		iEnd = iStart+3;
    		buff.replace(iStart, iEnd, TABS_4_STR);
    	}
    	return buff.toString();
    }
}
