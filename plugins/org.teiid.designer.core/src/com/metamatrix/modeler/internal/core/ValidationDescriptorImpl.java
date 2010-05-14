/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
