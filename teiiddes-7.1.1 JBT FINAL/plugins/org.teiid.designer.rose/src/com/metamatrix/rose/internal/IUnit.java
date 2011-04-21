/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.rose.internal;

import java.util.List;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;

/**
 * An instance of this class represents a Rose unit. The word "unit" will be used within the method descriptions below to
 * represent both an instance of this class and the Rose unit represented by that instance.
 * 
 * @since 4.1
 */
public interface IUnit {

    //============================================================================================================================
    // Property Methods
    /**
     * @return True if this unit can be found on the file system.
     * @since 4.1
     */
    boolean exists();

    /**
     * @return The list of units contained within this units (i.e., this unit's children); never null, unmodifiable.
     * @since 4.1
     */
    List getContainedUnits();

    /**
     * @return The unit within which this unit is contained (i.e., this units parent); may be null.
     * @since 4.1
     */
    IUnit getContainingUnit();

    /**
     * @return The folder in which this unit will be imported; may be null.
     * @since 4.1
     */
    IContainer getModelFolder();

    /**
     * @return The name of the model in which this unit will be imported; never null.
     * @since 4.1
     */
    String getModelName();

    /**
     * @return The unit's unqualified name.
     * @since 4.1
     */
    String getName();

    /**
     * @return The unit's qualified name, meaning its {@link #getName() unqualified name}prefixed by each of its containing
     *         unit's unqualified names in hierarchical order, when read from left to right, from the unit with no containing unit
     *         to this unit (i.e., from the oldest ancestor to this unit).
     * @since 4.1
     */
    String getQualifiedName();

    /**
     * @return This unit's resolved path, after path map values have been substituted for path variables appearing within the
     *         path.
     * @since 4.1
     */
    String getResolvedPath();

    /**
     * @return A message describing why this class cannot be
     *         {@link RoseImporter#loadUnit(IUnit, org.eclipse.core.runtime.IProgressMonitor) loaded}; may be null.
     * @since 4.1
     */
    String getSourceMessage();

    /**
     * @return An {@link org.eclipse.core.runtime.IStatus}severity related to
     *         {@link RoseImporter#loadUnit(IUnit, org.eclipse.core.runtime.IProgressMonitor) loading}this unit. An
     *         {@link org.eclipse.core.runtime.IStatus#ERROR error}indicates either this unit cannot be found on the file system
     *         given its {@link #getResolvedPath() resolved path}or an I/O error occurred while
     *         {@link RoseImporter#loadUnit(IUnit, org.eclipse.core.runtime.IProgressMonitor) loading}this unit. A
     *         {@link org.eclipse.core.runtime.IStatus@author#WARNING warning}means this unit cannot be found on the file system,
     *         but also that its {@link #getUnresolvedPath() unresolved path}contains path variables that may be set to other
     *         values which allow this unit to be found.
     * @since 4.1
     */
    int getSourceStatus();

    /**
     * @return A message describing why this unit cannot be
     *         {@link RoseImporter#parseSelectedUnits(org.eclipse.core.runtime.IProgressMonitor) parsed}; may be null.
     * @since 4.1
     */
    String getTargetMessage();

    /**
     * @return An {@link org.eclipse.core.runtime.IStatus}severity indicating whether this unit can be
     *         {@link RoseImporter#parseSelectedUnits(org.eclipse.core.runtime.IProgressMonitor) parsed}. An
     *         {@link org.eclipse.core.runtime.IStatus#ERROR error}indicates either this unit does not yet have a
     *         {@link #getModelFolder() model folder}set or its {@link #getModelName() model name}represents a folder in the
     *         workspace. A {@link org.eclipse.core.runtime.IStatus@author#WARNING warning}indicates its
     *         {@link #getModelName() model name}represents either a non-model file in the workspace or a model created from a
     *         different metamodel than the one that pertains to the currently running {@link RoseImporter importer}.
     * @since 4.1
     */
    int getTargetStatus();

    /**
     * @return This unit's unresolved path, before path map values have been substituted for path variables appearing within the
     *         path.
     * @since 4.1
     */
    String getUnresolvedPath();

    /**
     * @return The workspace resource that will be replaced or updated when this unit is imported.
     * @since 4.1
     */
    IResource getWorkspaceResource();

    /**
     * @return True if this unit has been successfully
     *         {@link RoseImporter#loadUnit(IUnit, org.eclipse.core.runtime.IProgressMonitor) loaded}.
     * @since 4.1
     */
    boolean isLoaded();

    /**
     * @return True if this unit has been selected for import.
     * @since 4.1
     */
    boolean isSelected();
}
