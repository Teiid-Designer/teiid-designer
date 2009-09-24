/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.util;


/** 
 * @since 4.3
 */
public class PerspectiveObject {
    public static final int NO_LOCATION = -1;
    public static final int TOP_LEFT = 0;
    public static final int BOTTOM_LEFT = 1;
    public static final int BOTTOM_RIGHT= 2;
    public static final int TOP_RIGHT = 3;
    public static final int LEFT_CENTER = 4;
    
    public int locationID = NO_LOCATION;
    public boolean primary = false;
    public String viewId;
    public boolean placeholder = false;
    
    /**
     * Constructs a new factory.
     */
    public PerspectiveObject(String viewId) {
        super();
        this.viewId = viewId;
    }
    
    /**
     * Constructs a new factory.
     */
    public PerspectiveObject(String viewId, boolean placeholder) {
        this(viewId);
        this.placeholder = placeholder;
    }
    
    /**
     * Constructs a new factory.
     */
    public PerspectiveObject(String viewId, boolean placeholder, int locationID) {
        this(viewId, placeholder);
        this.locationID = locationID;
    }
    
    /** 
     * @return Returns the folderID.
     * @since 4.3
     */
    public int getLocationID() {
        return this.locationID;
    }

    
    /** 
     * @param folderID The folderID to set.
     * @since 4.3
     */
    public void setLocationID(int folderID) {
        this.locationID = folderID;
    }


    
    /** 
     * @return Returns the primary.
     * @since 4.3
     */
    public boolean isPrimary() {
        return this.primary;
    }


    
    /** 
     * @param primary The primary to set.
     * @since 4.3
     */
    public void setPrimary(boolean primary) {
        this.primary = primary;
    }


    
    /** 
     * @return Returns the viewId.
     * @since 4.3
     */
    public String getViewId() {
        return this.viewId;
    }


    
    /** 
     * @param viewId The viewId to set.
     * @since 4.3
     */
    public void setViewId(String viewId) {
        this.viewId = viewId;
    }

    
    /** 
     * @return Returns the placeholder.
     * @since 4.3
     */
    public boolean isPlaceholder() {
        return this.placeholder;
    }

    
    /** 
     * @param placeholder The placeholder to set.
     * @since 4.3
     */
    public void setPlaceholder(boolean placeholder) {
        this.placeholder = placeholder;
    }
}
