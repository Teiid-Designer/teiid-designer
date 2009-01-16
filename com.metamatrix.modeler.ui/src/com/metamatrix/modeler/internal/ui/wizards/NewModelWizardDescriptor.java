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

package com.metamatrix.modeler.internal.ui.wizards;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;
import com.metamatrix.modeler.internal.core.ExtensionDescriptorImpl;
import com.metamatrix.modeler.ui.UiConstants;

/**
 * NewModelWizardDescriptor is a Java object representation of a NewModelWizardContributor
 * Extension.  The class encapsulates the parsing of the Extension from the plugin manifest
 * to expose needed properties as method calls.
 */
public class NewModelWizardDescriptor extends ExtensionDescriptorImpl
    implements UiConstants.ExtensionPoints.NewModelWizardContributor {

    private final static String TRUE = "true";  //$NON-NLS-1$

    private Bundle bundle;
    private String title;
    private ImageDescriptor imageDescriptor;
    private boolean supportsAnyPhysicalMetamodel = false;
    private boolean supportsAnyVirtualMetamodel = false;
    private Collection supportedPhysicalMetamodelList;
    private Collection supportedVirtualMetamodelList;

    /**
     * Construct an instance of NewModelWizardDescriptor.
     * @param id
     * @param className
     * @param classLoader
     */
    public NewModelWizardDescriptor( Object id,
	                                 String className,
	                                 Bundle bundle,
	                                 String title,
	                                 IConfigurationElement[] elements ) {
        super(id, className, bundle);
        this.title = title;
        this.bundle = bundle;
        loadProperties(elements);
    }

    public boolean canBuild(MetamodelDescriptor metamodel, boolean isVirtual) {
        if ( metamodel == null ) {
            return false;
        }

        boolean result = false;
        if ( isVirtual ) {
            if ( supportsAnyVirtualMetamodel ) {
                result = true;
            } else {
                result = supportedVirtualMetamodelList.contains(metamodel.getNamespaceURI());
            }
        } else {
            if ( supportsAnyPhysicalMetamodel ) {
                result = true;
            } else {
                result = supportedPhysicalMetamodelList.contains(metamodel.getNamespaceURI());
            }
        }
        return result;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return title;
    }

    public Image getIcon() {
        if ( this.imageDescriptor != null ) {
            return this.imageDescriptor.createImage();
        }
        return null;
    }

    private void loadProperties(IConfigurationElement[] elements) {
        supportedPhysicalMetamodelList = new ArrayList();
        supportedVirtualMetamodelList = new ArrayList();
        for ( int j=0 ; j<elements.length ; ++j ) {
            if ( elements[j].getName().equals(METAMODEL) ) {
                String metamodelName = elements[j].getAttribute(NAME);
                if ( metamodelName.equals(ANY) ) {
                    String support = elements[j].getAttribute(IS_PHYSICAL);
                    supportsAnyPhysicalMetamodel = TRUE.equalsIgnoreCase(support);
                    support = elements[j].getAttribute(IS_VIRTUAL);
                    supportsAnyVirtualMetamodel = TRUE.equalsIgnoreCase(support);
                } else {
                    String support = elements[j].getAttribute(IS_PHYSICAL);
                    if ( TRUE.equalsIgnoreCase(support) ) {
                        supportedPhysicalMetamodelList.add(metamodelName);
                    }
                    support = elements[j].getAttribute(IS_VIRTUAL);
                    if ( TRUE.equalsIgnoreCase(support) ) {
                        supportedVirtualMetamodelList.add(metamodelName);
                    }
                }
            } else if ( elements[j].getName().equals(CLASS) ) {
                String iconPath = elements[j].getAttribute(ICON);
                if ( iconPath != null ) {
                    try {
                        URL url;

                        // if the path has a colon we know the path is not relative to this bundle
                        if (iconPath.indexOf(':') < 0) {
                            url = FileLocator.find(bundle, new Path(iconPath), null);
                        } else {
                            url = FileLocator.resolve(new URL(iconPath));
                        }
                        
                        this.imageDescriptor = ImageDescriptor.createFromURL(url);
                    } catch (Exception e) {
                        UiConstants.Util.log(e);
                    }
                }
            }
        }
    }

}
