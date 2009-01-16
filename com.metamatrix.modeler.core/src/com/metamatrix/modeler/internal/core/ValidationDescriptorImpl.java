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

package com.metamatrix.modeler.internal.core;

import com.metamatrix.modeler.core.ValidationDescriptor;

/**
 * ValidationDescriptorImpl
 */
public class ValidationDescriptorImpl implements ValidationDescriptor {
    
    private String extensionID = null;
    private String name = null;
    private String label = null;
    private String toolTip = null;
    private String category = null;
    private String defaultOption = null;

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.ValidationDescriptor#getExtensionID()
     */
    public String getExtensionID() {
        return extensionID;
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.ValidationDescriptor#getExtensionID()
     */
    public void setExtensionID(String extensionID) {
        this.extensionID = extensionID;
    }    

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.ValidationDescriptor#getName()
     */
    public String getPreferenceName() {
        return this.name;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.ValidationDescriptor#getName()
     */
    public void setPrefernceName(String name) {
        this.name = name;
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.ValidationDescriptor#getName()
     */
    public String getPreferenceLabel() {
        return this.label;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.ValidationDescriptor#getName()
     */
    public void setPrefernceLabel(String label) {
        this.label = label;
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.ValidationDescriptor#getName()
     */
    public String getPreferenceToolTip() {
        return this.toolTip;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.ValidationDescriptor#getName()
     */
    public void setPrefernceToolTip(String toolTip) {
        this.toolTip = toolTip;
    } 

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.ValidationDescriptor#getName()
     */
    public String getPreferenceCategory() {
        return this.category;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.ValidationDescriptor#getName()
     */
    public void setPrefernceCategory(String category) {
        this.category = category;
    }

    /*
     * @see com.metamatrix.modeler.core.ValidationDescriptor#getDefaultOption()
     */
    public String getDefaultOption() {
        if(this.defaultOption == null) {
            return ValidationDescriptor.IGNORE;
        }
        return this.defaultOption;
    }

    /**
     * @param string
     */
    public void setDefaultOption(String defaultOption) {
        this.defaultOption = defaultOption;
    }

}
