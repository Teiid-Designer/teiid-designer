/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.relationship;

import org.eclipse.emf.common.util.URI;
import com.metamatrix.modeler.relationship.NavigationLink;

/**
 * NavigationLinkImpl is an implentation of {@link NavigationLink}
 */
public class NavigationLinkImpl extends NavigationObjectImpl implements NavigationLink {

    private final int hc;
    private final String focusRole;
    private final String nonFocusRole;
    private final String type;
    private final String toolTip;   

    /**
     * Construct an instance of NavigationLinkImpl.
     * 
     */
    public NavigationLinkImpl(final URI modelObjectUri, final String label,
                              final String type ) {
        this(modelObjectUri,label,type,null,null);               
    }

    /**
     * Construct an instance of NavigationLinkImpl.
     * 
     */
    public NavigationLinkImpl(final URI modelObjectUri, final String label,
                              final String type,
                              final String focusRole, final String nonFocusRole ) {
        super(modelObjectUri,label);
        this.type = type;
        this.focusRole = focusRole;
        this.nonFocusRole = nonFocusRole;    
        this.toolTip = ""; //$NON-NLS-1$  
        this.hc = super.getModelObjectUri() != null ?
                  super.getModelObjectUri().hashCode() :
                  super.hashCode();  
    }
    
    /**
     * Construct an instance of NavigationLinkImpl.
     * 
     */
    public NavigationLinkImpl(final URI modelObjectUri, final String label,
                              final String type, final String focusRole, 
                              final String nonFocusRole, final String toolTip ) {
        super(modelObjectUri,label);
        this.type = type;
        this.focusRole = focusRole;
        this.nonFocusRole = nonFocusRole;
        this.toolTip = toolTip;
        this.hc = modelObjectUri.hashCode();
    }
    
    public String getNonFocusRole() {
        return this.nonFocusRole;
    }
    
    /**
     * Return the role for the focus node of this link. 
     * @return the focus node link.
     */
    public String getFocusRole() {
        return focusRole;
    }

    /**
     * Return the relationship type for this link. 
     * @return the relationship type.
     */
    public String getType() {
        return type;
    }
    
    /**
     * Return the tooltip for this link.
     * @return tooltip for this link.
     */
    public String getToolTip() {
         return toolTip;
    }
    
    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
         return this.hc;
    }
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        // Check if instances are identical ...
        if (this == obj) {
            return true;
        }

        // Check if object can be compared to this one
        // (this includes checking for null ) ...
        //if ( this.getClass().isInstance(obj) ) {
        if (obj instanceof NavigationLinkImpl) {
            final NavigationLinkImpl that = (NavigationLinkImpl)obj;
            if ( that.hashCode() != this.hashCode() ) {
                return false;
            }
            final URI thatUri = that.getModelObjectUri();
            final URI thisUri = this.getModelObjectUri();
            return thisUri.equals(thatUri);
        }
         // Otherwise not comparable ...
        return false;
     }     
}
