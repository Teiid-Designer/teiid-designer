/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import com.metamatrix.core.util.ArgCheck;

/**
 * The NavigationContextInfo represents a serializable and durable unique reference to a navigation context focused on a
 * particular object. This implementation uses the {@link URI} of the object to determine uniqueness, so multiple
 * NavigationContextInfo instances with the same URI will be considered to be equivalent.
 */
public class NavigationContextInfo {

    private final String focusNodeUri;
    private final String focusNodeMetaclassUri;
    public String label;

    /**
     * Construct an instance of NavigationContextInfo using the supplied information.
     * 
     * @param obj the object for which the navigation context is to be focused on; may not be null, and must have a valid
     *        {@link URI}.
     * @throws IllegalArgumentException if the supplied object is null or has no valid URI
     */
    public NavigationContextInfo( final EObject obj,
                                  final String uri ) {
        ArgCheck.isNotNull(obj);
        ArgCheck.isNotNull(uri);
        this.focusNodeUri = uri;
        this.focusNodeMetaclassUri = EcoreUtil.getURI(obj.eClass()).toString();
    }

    public NavigationContextInfo( final String uri ) {
        ArgCheck.isNotNull(uri);
        this.focusNodeUri = uri;
        this.focusNodeMetaclassUri = ""; //$NON-NLS-1$
    }

    /**
     * Returns the label of the FocusNode
     * 
     * @return label of the focus node; may be null.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Returns the label of the FocusNode
     * 
     * @param label of the focus node.
     */
    public void setLabel( String label ) {
        this.label = label;
    }

    public String getFocusNodeUri() {
        return this.focusNodeUri;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return focusNodeUri.hashCode();
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return focusNodeUri;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( final Object obj ) {
        // Check if instances are identical ...
        if (this == obj) {
            return true;
        }

        // Check if object can be compared to this one
        // (this includes checking for null ) ...
        // if ( this.getClass().isInstance(obj) ) {
        if (obj instanceof NavigationContextInfo) {
            final NavigationContextInfo that = (NavigationContextInfo)obj;
            if (this.focusNodeUri.equals(that.focusNodeUri)) {
                return true;
            }
        }

        // Otherwise not comparable ...
        return false;
    }

    /**
     * @return
     */
    public String getFocusNodeMetaclassUri() {
        return focusNodeMetaclassUri;
    }

}
