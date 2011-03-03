/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.rose.internal.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.importer.rose.parser.RoseNode;
import com.metamatrix.rose.internal.IRoseConstants;
import com.metamatrix.rose.internal.IUnit;

/**
 * @since 4.1
 */
public final class Unit implements IRoseConstants, IUnit {
    // ============================================================================================================================
    // Variables

    private IContainer modelFolder;

    private String modelName;

    private String name;

    private String path, resolvedPath;

    private List units = new ArrayList();

    private Unit parent;

    private IResource resrc;

    private boolean selected;

    private boolean exists;

    private RoseNode node;

    private int loadStatus = IStatus.OK;

    private int parseStatus = IStatus.OK;

    private String loadMsg, parseMsg;

    // ============================================================================================================================
    // Constructors

    /**
     * @param name
     * @param path
     * @since 4.1
     */
    public Unit( final String name,
                 final String path ) {
        this.name = this.modelName = name;
        this.path = path;
    }

    // ============================================================================================================================
    // Implemented Methods

    /**
     * @see com.metamatrix.rose.internal.IUnit#exists()
     * @since 4.1
     */
    public boolean exists() {
        return this.exists;
    }

    /**
     * @see com.metamatrix.rose.internal.IUnit#getContainedUnits()
     * @since 4.1
     */
    public List getContainedUnits() {
        return Collections.unmodifiableList(this.units);
    }

    /**
     * @see com.metamatrix.rose.internal.IUnit#getContainingUnit()
     * @since 4.1
     */
    public IUnit getContainingUnit() {
        return this.parent;
    }

    /**
     * @see com.metamatrix.rose.internal.IUnit#getSourceMessage()
     * @since 4.1
     */
    public String getSourceMessage() {
        return this.loadMsg;
    }

    /**
     * @see com.metamatrix.rose.internal.IUnit#getSourceStatus()
     * @since 4.1
     */
    public int getSourceStatus() {
        return this.loadStatus;
    }

    /**
     * @see com.metamatrix.rose.internal.IUnit#getModelFolder()
     * @since 4.1
     */
    public IContainer getModelFolder() {
        return this.modelFolder;
    }

    /**
     * @see com.metamatrix.rose.internal.IUnit#getModelName()
     * @since 4.1
     */
    public String getModelName() {
        return this.modelName;
    }

    /**
     * @see com.metamatrix.rose.internal.IUnit#getName()
     * @since 4.1
     */
    public String getName() {
        return this.name;
    }

    /**
     * @see com.metamatrix.rose.internal.IUnit#getTargetMessage()
     * @since 4.1
     */
    public String getTargetMessage() {
        return this.parseMsg;
    }

    /**
     * @see com.metamatrix.rose.internal.IUnit#getTargetStatus()
     * @since 4.1
     */
    public int getTargetStatus() {
        return this.parseStatus;
    }

    /**
     * @see com.metamatrix.rose.internal.IUnit#getQualifiedName()
     * @since 4.1
     */
    public String getQualifiedName() {
        return (this.parent == null ? this.name : this.parent.getQualifiedName() + '/' + this.name);
    }

    /**
     * @see com.metamatrix.rose.internal.IUnit#getResolvedPath()
     * @since 4.1
     */
    public String getResolvedPath() {
        return this.resolvedPath;
    }

    /**
     * @see com.metamatrix.rose.internal.IUnit#getUnresolvedPath()
     * @since 4.1
     */
    public String getUnresolvedPath() {
        return this.path;
    }

    /**
     * @see com.metamatrix.rose.internal.IUnit#getWorkspaceResource()
     * @since 4.1
     */
    public IResource getWorkspaceResource() {
        return this.resrc;
    }

    /**
     * @see com.metamatrix.rose.internal.IUnit#isLoaded()
     * @since 4.1
     */
    public boolean isLoaded() {
        return (this.node != null);
    }

    /**
     * @see com.metamatrix.rose.internal.IUnit#isSelected()
     * @since 4.1
     */
    public boolean isSelected() {
        return this.selected;
    }

    // ============================================================================================================================
    // Overridden Methods

    /**
     * @see java.lang.Object#toString()
     * @since 4.1
     */
    @Override
    public String toString() {
        return getName();
    }

    // ============================================================================================================================
    // Property Methods

    /**
     * @param unit
     * @since 4.1
     */
    public void addUnit( final Unit unit ) {
        this.units.add(unit);
        unit.setContainingUnit(this);
    }

    /**
     * @return The root RoseNode for this Rose unit.
     * @since 4.1
     */
    public RoseNode getRootRoseNode() {
        return this.node;
    }

    /**
     * @since 4.1
     */
    private void setContainingUnit( final Unit parent ) {
        this.parent = parent;
    }

    /**
     * @param message
     * @since 4.1
     */
    public void setLoadMessage( final String message ) {
        this.loadMsg = message;
    }

    /**
     * @param status
     * @since 4.1
     */
    public void setLoadStatus( final int status ) {
        this.loadStatus = status;
    }

    /**
     * @param folder
     * @since 4.1
     */
    public void setModelFolder( final IContainer folder ) {
        this.modelFolder = folder;
        setWorkspaceResource();
    }

    /**
     * @param name
     * @since 4.1
     */
    public void setModelName( final String name ) {
        this.modelName = name;
        setWorkspaceResource();
    }

    /**
     * @param message
     * @since 4.1
     */
    public void setParseMessage( final String message ) {
        this.parseMsg = message;
    }

    /**
     * @param status
     * @since 4.1
     */
    public void setParseStatus( final int status ) {
        this.parseStatus = status;
    }

    /**
     * @param path
     * @since 4.1
     */
    public void setResolvedPath( final String path ) {
        this.resolvedPath = path;
        this.exists = new File(path).exists();
        if (!this.exists) {
            this.node = null;
            this.units.clear();
        }
    }

    /**
     * @param node
     * @since 4.1
     */
    public void setRootRoseNode( final RoseNode node ) {
        this.node = node;
    }

    /**
     * @param selected
     * @since 4.1
     */
    public void setSelected( final boolean selected ) {
        this.selected = selected;
    }

    // ============================================================================================================================
    // Utility Methods

    /**
     * @since 4.1
     */
    private void setWorkspaceResource() {
        this.resrc = (this.modelFolder == null ? null : this.modelFolder.findMember(this.modelName));
    }
}
