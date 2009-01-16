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

package com.metamatrix.rose.internal;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.emf.importer.rose.builder.RoseVisitor;

import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.rose.internal.impl.Unit;

/**
 * This interface must be implemented by classes responsible for parsing Rose models to one or more MetaMatrix models.
 * Implementations will provide the logic and information necessary to map information from a Rose model to a MetaMatrix model
 * based upon the MetaMatrix metamodel {@link #getPrimaryMetamodelUri() indicated}.
 * 
 * @since 4.1
 */
public interface IRoseHandler extends
                             IRoseConstants.IReferenceTypes,
                             RoseVisitor {

    //============================================================================================================================
    // Property Methods

    /**
     * Performs cleanup.
     * 
     * @since 4.2.2
     */
    void cleanup();

    /**
     * Clears the {@link #getProblems() problems}and {@link #getAmbiguousReferences() ambiguous references}lists.
     * 
     * @since 4.1
     */
    void clear();

    /**
     * @return The list of ambiguous references (as {@link IAmbiguousReference IAmbiguousReferences}) encountered during parsing;
     *         never null, unmodifiable.
     * @since 4.1
     */
    List getAmbiguousReferences();

    /**
     * @return The list of problems (as {@link IMessage IProblems}) encountered during parsing; never null, unmodifiable.
     * @since 4.1
     */
    List getProblems();

    /**
     * @param object
     *            The MetaMatrix model object containing the reference.
     * @param type
     *            The type of the reference that cannot be resolved; either {@link #GENERALIZATION},{@link #OWNER}, or
     *            {@link #TYPE}.
     * @param name
     *            The name of the reference that cannot be resolved.
     * @since 4.1
     * @return
     */
    String getUnresolvableReferenceMessage(Object object,
                                           String type,
                                           String name);

    //============================================================================================================================
    // Controller Methods

    /**
     * @param type
     *            The type of the reference that cannot be resolved; either {@link #GENERALIZATION},{@link #OWNER}, or
     *            {@link #TYPE}.
     * @param referencer
     *            The object containing the reference that cannot be resolved.
     * @param quid
     *            The Rose QUID of the reference that cannot be resolved.
     * @param name
     *            The name of the reference that cannot be resolved.
     * @return An object representing the missing referenced object; may be null.
     * @since 4.1
     */
    Object createMissingObject(String type,
                               Object referencer,
                               String quid,
                               String name);

    /**
     * Called by the {@link RoseImporter}to provide for any difference analysis that needs to be performed before differences are
     * presented to the user.
     * 
     * @param mappings
     *            The list of mappings between objects before and after being modified by the importer; never null, unmodifiable.
     * @since 4.1
     */
    void differenceReportGenerated(final List mappings);

    /**
     * Called by the {@link RoseImporter}to determine the model type to set on newly created MetaMatrix models.
     * 
     * @return The model type of the MetaMatrix model; never null.
     * @since 4.1
     */
    ModelType getModelType();

    /**
     * Called by the {@link RoseImporter}to determine the primary metamodel URL to set on newly created MetaMatrix models.
     * 
     * @return The primary metamodel URI of the MetaMatrix model; never null.
     * @since 4.1
     */
    String getPrimaryMetamodelUri();

    /**
     * @param factories
     *            The list of {@link com.metamatrix.modeler.core.compare.EObjectMatcherFactory EObjectMatcherFactories}that will
     *            be used to match newly imported objects to objects within models being updated within the workspace.
     * @since 4.1
     */
    void initialize(List factories);

    /**
     * Called by the {@link RoseImporter}to provide for any final difference analysis that needs to be performed before
     * MetaMatrix models changed by the importer are saved.
     * 
     * @param mappings
     *            The list of mappings between objects before and after being modified by the importer; never null, unmodifiable.
     * @since 4.1
     */
    void modelsMerged(final List mappings);

    /**
     * Called by the {@link RoseImporter}to provide for any final handling (such as lazy reference resolution) that needs to be
     * performed after parsing all Rose units selected for import.
     * 
     * @param rootsList
     *            The root objects of each of the target MetaMatrix models for the Rose models being parsed (i.e., a list of
     *            lists); must not be null, must be modifiable.
     * @since 4.1
     */
    void parsingFinished(List rootsList);

    /**
     * Called by the {@link RoseImporter}to provide for any initial handling that needs to be performed before parsing Rose units
     * selected for import.
     * 
     * @param monitor
     *            A cancelable progress monitor that will be updated during processing; never null.
     * @since 4.1
     */
    void parsingStarting(IProgressMonitor monitor);

    /**
     * @param referencer
     *            The object containing the reference.
     * @param object
     *            The object referenced.
     * @param type
     *            The type of the reference; either {@link #GENERALIZATION},{@link #OWNER}, or {@link #TYPE}.
     * @param quid
     *            The referencer's Rose QUID.
     * @since 4.1
     */
    void resolveReference(Object referencer,
                          Object object,
                          String type,
                          String quid);

    /**
     * Called by the {@link RoseImporter}to provide for any final handling that needs to be performed after parsing each Rose
     * unit selected for import.
     * 
     * @param monitor
     *            A cancelable progress monitor that will be updated during processing; never null.
     * @since 4.2
     */
    void unitParsingFinished(IProgressMonitor monitor);

    /**
     * Called by the {@link RoseImporter}to provide for any initial handling that needs to be performed before parsing each Rose
     * unit selected for import.
     * 
     * @param unit
     *            The Rose unit currently being parsed; must not be null.
     * @param roots
     *            The root objects of the target MetaMatrix model for the current Rose model being parsed; must not be null, must
     *            be modifiable.
     * @since 4.2
     */
    void unitParsingStarting(Unit unit, List roots);
}
