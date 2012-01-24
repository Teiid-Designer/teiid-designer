/*
 * Copyright � 2000-2006 MetaMatrix, Inc.
 * All rights reserved.
 */
package org.teiid.designer.advisor.ui.core;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.graphics.Image;

/**
 * @since 5.0
 */
public class InfoPopAction {
    public static final int TYPE_FIX = 0;
    public static final int TYPE_DO = 1;
    public static final int TYPE_OTHER = 2;

    // Fields
    private String description;
    private IAction action;
    private Image image;
    private int type;

    /**
     * Constructor
     * 
     * @since 5.0
     */
    public InfoPopAction( IAction action ) {
        this(action, TYPE_OTHER, null, null);
        this.action = action;
    }

    /**
     * Constructor
     * 
     * @since 5.0
     */
    public InfoPopAction( IAction action,
                          int type ) {
        this(action, type, null, null);
        this.action = action;
        this.type = type;
    }

    /**
     * Constructor
     * 
     * @since 5.0
     */
    public InfoPopAction( IAction action,
                          int type,
                          String description,
                          Image image ) {
        super();
        this.action = action;
        this.type = type;
        this.description = description;
        this.image = image;
    }

    /**
     * @return Returns the action.
     * @since 5.0
     */
    public IAction getAction() {
        return this.action;
    }

    /**
     * @param theAction The action to set.
     * @since 5.0
     */
    public void setAction( IAction theAction ) {
        this.action = theAction;
    }

    /**
     * @return Returns the description.
     * @since 5.0
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * @param theDescription The description to set.
     * @since 5.0
     */
    public void setDescription( String theDescription ) {
        this.description = theDescription;
    }

    /**
     * @return Returns the image.
     * @since 5.0
     */
    public Image getImage() {
        return this.image;
    }

    /**
     * @param theImage The image to set.
     * @since 5.0
     */
    public void setImage( Image theImage ) {
        this.image = theImage;
    }

    /**
     * @return Returns the type.
     * @since 5.0
     */
    public int getType() {
        return this.type;
    }

    /**
     * @param theType The type to set.
     * @since 5.0
     */
    public void setType( int theType ) {
        this.type = theType;
    }

}
