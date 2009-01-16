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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.importer.rose.builder.RoseStrings;
import org.eclipse.emf.importer.rose.builder.RoseWalker;
import org.eclipse.emf.importer.rose.parser.RoseLexer;
import org.eclipse.emf.importer.rose.parser.RoseLoader;
import org.eclipse.emf.importer.rose.parser.RoseNode;
import org.eclipse.emf.importer.rose.parser.RoseParser;
import org.eclipse.emf.importer.rose.parser.Util;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.emf.mapping.MappingHelper;
import com.metamatrix.core.MetaMatrixRuntimeException;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.FileUtils;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.modeler.compare.DifferenceDescriptor;
import com.metamatrix.modeler.compare.DifferenceProcessor;
import com.metamatrix.modeler.compare.DifferenceReport;
import com.metamatrix.modeler.compare.MergeProcessor;
import com.metamatrix.modeler.compare.ModelerComparePlugin;
import com.metamatrix.modeler.compare.processor.DifferenceProcessorImpl;
import com.metamatrix.modeler.compare.processor.MergeProcessorImpl;
import com.metamatrix.modeler.compare.selector.EmfResourceSelector;
import com.metamatrix.modeler.compare.selector.TransientModelSelector;
import com.metamatrix.modeler.compare.util.CompareUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.TransactionRunnable;
import com.metamatrix.modeler.core.transaction.UnitOfWork;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.resource.xmi.MtkXmiResourceImpl;
import com.metamatrix.modeler.internal.core.workspace.ModelResourceImpl;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.rose.internal.impl.AmbiguousReference;
import com.metamatrix.rose.internal.impl.Message;
import com.metamatrix.rose.internal.impl.Unit;

/**
 * One instance of this class should be instantiated for each Rose model that is to be imported to one or more MetaMatrix models.
 * 
 * @since 4.1
 */
public final class RoseImporter implements FileUtils.Constants, IRoseConstants, ModelerCore.ILicense, StringUtil.Constants {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(RoseImporter.class);

    private static final String CLOSED_PROJECT_MESSAGE_ID = "closedProjectMessage"; //$NON-NLS-1$
    private static final String DIFFERENT_METAMODEL_MESSAGE_ID = "differentMetamodelMessage"; //$NON-NLS-1$
    private static final String FINISH_PARSING_MESSAGE = "finishParsingMessage"; //$NON-NLS-1$
    private static final String FOLDER_MESSAGE_ID = "folderMessage"; //$NON-NLS-1$
    private static final String IO_EXCEPTION_MESSAGE_ID = "ioExceptionMessage"; //$NON-NLS-1$
    private static final String LOADING_UNIT_MESSAGE_ID = "loadingUnitMessage"; //$NON-NLS-1$
    private static final String MISSING_FOLDER_MESSAGE_ID = "missingFolderMessage"; //$NON-NLS-1$
    private static final String NOT_MODEL_MESSAGE_ID = "notModelMessage"; //$NON-NLS-1$
    private static final String NO_UNITS_SELECTED_MESSAGE = getString("noUnitsSelectedMessage"); //$NON-NLS-1$
    private static final String PARSING_UNIT_MESSAGE_ID = "parsingUnitMessage"; //$NON-NLS-1$
    private static final String PATH_NOT_FOUND_MESSAGE = getString("pathNotFoundMessage"); //$NON-NLS-1$
    private static final String PATH_UNRESOLVABLE_MESSAGE = getString("pathUnresolvableMessage"); //$NON-NLS-1$
    private static final String READ_ONLY_MESSAGE_ID = "readOnlyMessage"; //$NON-NLS-1$
    private static final String START_PARSING_MESSAGE = "startParsingMessage"; //$NON-NLS-1$

    /**
     * @since 4.1
     */
    private static String getString( final String id ) {
        return UTIL.getString(I18N_PREFIX + id);
    }

    /**
     * @since 4.1
     */
    private static String getString( final String id,
                                     final Object parameter ) {
        return UTIL.getString(I18N_PREFIX + id, parameter);
    }

    /**
     * @since 4.1
     */
    private static String getString( final String id,
                                     final Object parameter1,
                                     final Object parameter2 ) {
        return UTIL.getString(I18N_PREFIX + id, parameter1, parameter2);
    }

    /**
     * @since 4.1
     */
    private static String getString( final String id,
                                     final Object parameter1,
                                     final Object parameter2,
                                     final Object parameter3 ) {
        return UTIL.getString(I18N_PREFIX + id, parameter1, parameter2, parameter3);
    }

    private Map pathMap = new HashMap();
    private Unit unit;
    private List sourceMsgListener = new ArrayList();
    private List selectedUnits = new ArrayList();
    private List targetProblems = new ArrayList();
    private List parseProblems = new ArrayList();
    private int ambiguousRefs;
    IRoseHandler handler;
    private Map diffProcMap = new HashMap();
    private List matcherFactories = ModelerComparePlugin.createEObjectMatcherFactories();
    private URIConverter uriConverter = new ResourceSetImpl().getURIConverter();

    /**
     * Creates an importer that can import a Rose model to a MetaMatrix model using the specified MetaMatrix metamodel-specific
     * IRoseHandler.
     * 
     * @param handler The MetaMatrix metamodel-specific IRoseHandler; never null.
     * @throws LicenseException If a valid license cannot be found for either the Modeler product or the Rose import capability.
     * @since 4.1
     */
    public RoseImporter( final IRoseHandler handler ) {
        ArgCheck.isNotNull(handler);
        // Initialize handler
        handler.initialize(this.matcherFactories);
        this.handler = handler;
        uriConverter.getURIMap().putAll(EcorePlugin.computePlatformURIMap());
    }

    /**
     * Adds the specified listener to the list of listeners that receive messages whenever a problem related to loading a unit is
     * encountered or resolved; must not be null.
     * 
     * @param listener The listener to receive messages.
     * @since 4.1
     */
    public void addUnitSourceMessageListener( final IMessageListener listener ) {
        ArgCheck.isNotNull(listener);
        this.sourceMsgListener.add(listener);
    }

    /**
     * @return True if all ambiguous references, if any, have been resolved.
     * @since 4.1
     */
    public boolean ambiguousReferencesResolved() {
        return (this.ambiguousRefs == 0);
    }

    /**
     * @return The list of ambiguous references (as {@link IAmbiguousReference IAmbiguousReferences}) encountered during parsing;
     *         never null, unmodifiable.
     * @see IRoseHandler#getAmbiguousReferences()
     * @since 4.1
     */
    public List getAmbiguousReferences() {
        return this.handler.getAmbiguousReferences();
    }

    /**
     * Returns the list of problems encountered during parsing. A problem will exist if:
     * <ul>
     * <li>an parsed type could not be resolved,
     * <li>a I/O exception occurred while reading a Rose unit, or
     * <li>an unexpected error occurred during parsing.
     * </ul>
     * 
     * @return The list of problems encountered during parsing; never null, unmodifiable.
     * @since 4.1
     */
    public List getParseProblems() {
        return Collections.unmodifiableList(this.parseProblems);
    }

    /**
     * Returns the path variables (which appear in Rose units prefixed by dollar signs ($) within references to other units)
     * mapped to the paths that will be substituted for them.
     * 
     * @return The map of path variables to paths; never null, unmodifiable.
     * @since 4.1
     */
    public Map getPathMap() {
        return Collections.unmodifiableMap(this.pathMap);
    }

    /**
     * @return The list of Rose units selected for import; never null, unmodifiable.
     * @since 4.1
     */
    public List getSelectedUnits() {
        return Collections.unmodifiableList(this.selectedUnits);
    }

    /**
     * @return The unit with the most severe problem preventing it from being parsed.
     * @since 4.1
     */
    public IUnit getUnitWithMostSevereTargetProblem() {
        return (this.targetProblems.isEmpty() ? null : (IUnit)this.targetProblems.get(0));
    }

    /**
     * Removes the specified listener from the list of listeners that receive messages whenever a problem related to loading a
     * unit is encountered or resolved; must not be null.
     * 
     * @param listener The listener to be removed.
     * @since 4.1
     */
    public void removeUnitSourceMessageListener( final IMessageListener listener ) {
        ArgCheck.isNotNull(listener);
        this.sourceMsgListener.remove(listener);
    }

    private void garbageCollect() {
        System.gc();
        Thread.yield();
    }

    /**
     * @param monitor A cancelable progress monitor that will be updated during processing; may be null.
     * @return The list of {@link DifferenceReport difference reports}describing the changes that will be made to the workspace
     *         once the new or updated MetaMatrix models are saved; never null, unmodifiable.
     * @since 4.1
     */
    public List generateDifferenceReports( IProgressMonitor monitor ) {
        if (this.ambiguousRefs > 0) {
            return Collections.EMPTY_LIST;
        }
        // Ensure monitor is not null
        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }
        // Perform difference analysis against workspace model
        final List rpts = new ArrayList(this.diffProcMap.size());
        for (final Iterator iter = this.diffProcMap.entrySet().iterator(); iter.hasNext();) {
            final Entry entry = (Entry)iter.next();
            final DifferenceProcessorImpl diffProc = (DifferenceProcessorImpl)entry.getValue();
            diffProc.addEObjectMatcherFactories(this.matcherFactories);
            final IStatus status = diffProc.execute(monitor);
            if (status.isOK()) {
                final DifferenceReport rpt = diffProc.getDifferenceReport();
                rpt.setTitle(rpt.getResultUri());
                CompareUtil.skipDeletesOfStandardContainers(rpt);
                this.handler.differenceReportGenerated(rpt.getMapping().getNested());
                rpts.add(rpt);
            } else {
                // Add any problems encountered to problem list.
                this.parseProblems.add(new Message(status.getSeverity(), status.getMessage(), status.getException(), diffProc));
                UTIL.log(status);
            }
        }
        garbageCollect();
        // Return difference anaylsys reports for all models created or updated.
        return Collections.unmodifiableList(rpts);
    }

    protected List getRoots( final DifferenceProcessorImpl processor ) throws ModelerCoreException {
        return processor.getAfterSelector().getRootObjects();
    }

    /**
     * Imports the parsed model elements into the workspace according to the {@link DifferenceReport difference report}returned
     * after {@link #parseSelectedUnits(IProgressMonitor) parsing}the Rose units.
     * 
     * @param monitor A cancelable progress monitor that will be updated during processing; may be null.
     * @return The list of problems (as {@link IMessage IMessages}) encountered during import; never null.
     * @since 4.1
     */
    public List importModels( IProgressMonitor monitor ) {
        // Clear memory used by handler
        this.handler.cleanup();
        garbageCollect();
        // Ensure monitor is not null
        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }
        final IProgressMonitor finalMon = monitor;
        final List problems = new ArrayList();
        // Import each model
        final List resrcs = new ArrayList(this.diffProcMap.size());
        for (final Iterator iter = this.diffProcMap.entrySet().iterator(); iter.hasNext();) {
            final Entry entry = (Entry)iter.next();
            final DifferenceProcessorImpl diffProc = (DifferenceProcessorImpl)entry.getValue();

            if (!shouldMergeModels(diffProc)) {
                continue;
            }

            // Merge imported data with workspace data
            final MergeProcessor mergeProc = new MergeProcessorImpl(diffProc, null, true);
            final EmfResourceSelector targetSelector = (EmfResourceSelector)diffProc.getBeforeSelector();
            final TransactionRunnable op = new TransactionRunnable() {

                /**
                 * @see com.metamatrix.modeler.core.TransactionRunnable#run(com.metamatrix.modeler.core.transaction.UnitOfWork)
                 * @since 4.1
                 */
                public Object run( final UnitOfWork work ) {
                    final IStatus status = mergeProc.execute(finalMon);
                    RoseImporter.this.handler.modelsMerged(diffProc.getDifferenceReport().getMapping().getNested());
                    return status;
                }
            };
            try {
                final Resource resrc = targetSelector.getResource();
                final IStatus status = (IStatus)ModelerCore.getModelEditor().executeAsTransaction(op, null, true, this);
                if (status.isOK()) {
                    resrcs.add(resrc);
                } else {
                    problems.add(new Message(status.getSeverity(), status.getMessage(), status.getException(), resrc));
                    UTIL.log(status);
                }
            } catch (final Exception err) {
                problems.add(new Message(err));
                UTIL.log(err);
            } finally {
                mergeProc.close();
            }
        }
        // Save models & refresh containing folders
        final List models = new ArrayList(this.diffProcMap.size());
        this.diffProcMap.clear();
        final String uri = this.handler.getPrimaryMetamodelUri();
        final ModelType type = this.handler.getModelType();
        this.handler = null;
        garbageCollect();
        for (final Iterator iter = resrcs.iterator(); iter.hasNext();) {
            final Resource resrc = (Resource)iter.next();
            final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(resrc.getURI().toFileString()));
            try {
                // Create model resource
                final ModelResource model = ModelerCore.create(file);
                // Copy contents of newly created EMF resource to model resource's EMF resource
                models.add(model);
                final List contents = model.getEmfResource().getContents();
                for (final Iterator objIter = new ArrayList(resrc.getContents()).iterator(); objIter.hasNext();) {
                    final Object obj = objIter.next();
                    if (!(obj instanceof ModelAnnotation)) {
                        contents.add(obj);
                    }
                }
                // Set model's primary metamodel URI and type
                final ModelAnnotation annotation = model.getModelAnnotation();
                annotation.setPrimaryMetamodelUri(uri);
                annotation.setModelType(type);
                if (resrc instanceof ModelResourceImpl) {
                    ((ModelResourceImpl)resrc).setModelType(type);
                }
                // Save model & refresh containing folder
                garbageCollect();
                model.save(monitor, true);
            } catch (final CoreException err) {
                problems.add(new Message(err));
                UTIL.log(err);
            }
        }

        for (final Iterator iter = models.iterator(); iter.hasNext();) {
            final ModelResource model = (ModelResource)iter.next();
            try {
                // Save model & refresh containing folder
                garbageCollect();
                model.save(monitor, true);
                model.getResource().getParent().refreshLocal(IResource.DEPTH_INFINITE, monitor);
            } catch (final CoreException err) {
                problems.add(new Message(err));
                UTIL.log(err);
            }
        }
        garbageCollect();
        return problems;
    }

    /**
     * Loads the Rose unit with the specified file path. The returned IUnit contains information about the specified Rose unit,
     * including Rose units contained within it (which must be {@link #loadUnit(IUnit, IProgressMonitor) loaded}separately).
     * 
     * @param path The Rose unit's file path; must not be null.
     * @param monitor A non-cancelable progress monitor that will be updated during processing; may be null.
     * @return The IUnit for the The Rose unit with the specified file path, or null if the unit could not be loaded.
     * @since 4.1
     */
    public IUnit loadUnit( final String path,
                           IProgressMonitor monitor ) {
        ArgCheck.isNotEmpty(path);
        // Ensure monitor is not null
        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }
        // Update monitor
        monitor.beginTask(getString(LOADING_UNIT_MESSAGE_ID, path), IProgressMonitor.UNKNOWN);
        // Clear any previously selected units and target problems
        this.selectedUnits.clear();
        this.targetProblems.clear();
        // Parse unit
        RoseLoader loader = null;
        try {
            loader = new RoseLoader(path, this.uriConverter);
            if (!loader.isValid()) {
                return null;
            }
            final RoseParser parser = new RoseParser(new RoseLexer(loader), true, true);
            parser.parse();
            final RoseNode node = parser.getModelTree();
            if (node == null) {
                final String msg = UTIL.getString("RoseImporter.no_nodes"); //$NON-NLS-1$
                throw new MetaMatrixRuntimeException(msg);
            }

            String qualifier;
            if (EMPTY_STRING.equals(node.getKey()) && (Util.getType(node.getValue())).equals(RoseStrings.CLASS_CATEGORY)) {
                // .cat file
                this.unit = new Unit(Util.getName(node.getValue()), path);
                qualifier = Util.getName(node.getValue());
            } else {
                // .mdl file
                int startNdx = path.lastIndexOf(File.separatorChar);
                int endNdx = path.lastIndexOf(FILE_EXTENSION_SEPARATOR_CHAR);
                final String name = path.substring(startNdx + 1, endNdx >= 0 ? endNdx : path.length());
                this.unit = new Unit(name, path);
                qualifier = null;
            }
            // Set resolved path to path
            this.unit.setResolvedPath(path);
            // Associate root Rose node with Unit
            this.unit.setRootRoseNode(node);
            // Build unit tree
            new UnitBuilder().traverse(qualifier, node, this.unit);
            // Fire no units selected message to listener
            updateUnitSourceStatus(IStatus.ERROR, NO_UNITS_SELECTED_MESSAGE, null);
            return this.unit;
        } catch (final Exception notPossible) {
            fireUnitSourceMessage(notPossible);
            UTIL.log(notPossible);
            return null;
        } finally {
            // Close loader if open
            if (loader != null) {
                try {
                    loader.close();
                } catch (final IOException err) {
                    UTIL.log(err);
                }
            }
            // Notify monitor task is complete
            monitor.done();
            garbageCollect();
        }
    }

    /**
     * {@link #loadUnit(String, IProgressMonitor) Loads}the Rose unit corresponding to the specified {@link IUnit}if it has not
     * already been loaded.
     * 
     * @param unit The IUnit corresponding to the Rose unit to be loaded; must not be null.
     * @param monitor A non-cancelable progress monitor that will be updated during processing; may be null.
     * @since 4.1
     */
    public void loadUnit( final IUnit unit,
                          IProgressMonitor monitor ) {
        ArgCheck.isNotNull(unit);
        // Ensure monitor is not null
        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }
        // Update monitor
        monitor.beginTask(getString(LOADING_UNIT_MESSAGE_ID, unit.getQualifiedName()), IProgressMonitor.UNKNOWN);

        final Unit unitImpl = (Unit)unit;
        RoseLoader loader = null;
        try {
            // Skip units that have already been loaded
            if (unit.isLoaded()) {
                return;
            }
            // Send message and return if unit path is unresolved or doesn't exist
            if (!unit.exists()) {
                updateUnitSourceStatus(unit.getSourceStatus(), unit.getSourceMessage(), unitImpl);
                return;
            }
            // Parse unit
            loader = new RoseLoader(unit.getResolvedPath(), this.uriConverter);
            if (!loader.isValid()) {
                updateUnitSourceStatus(IStatus.ERROR, getString(IO_EXCEPTION_MESSAGE_ID, unit.getQualifiedName()), unitImpl);
                return;
            }
            final RoseParser parser = new RoseParser(new RoseLexer(loader), true, true);
            parser.parse();
            // Associate root Rose node with Unit
            final RoseNode node = parser.getModelTree();
            unitImpl.setRootRoseNode(node);
            // Update unit's children
            new UnitBuilder().traverse(Util.getName(node.getValue()), node, unitImpl);
        } catch (final Exception notPossible) {
            fireUnitSourceMessage(notPossible);
            UTIL.log(notPossible);
        } finally {
            // Close loader if open
            if (loader != null) {
                try {
                    loader.close();
                } catch (final IOException err) {
                    UTIL.log(err);
                }
            }
            // Notify monitor task is complete
            monitor.done();
            garbageCollect();
        }
    }

    /**
     * Maps the specified variable that already exists in the {@link #getPathMap() path map}to the specified path.
     * 
     * @param variable The existing variable in the path map to map; must not be null.
     * @param path The path segment that will be substituted for the specified variable when resolving Rose unit paths; may be
     *        null.
     * @since 4.1
     */
    public void mapPath( final String variable,
                         final String path ) {
        ArgCheck.isNotNull(variable);
        this.pathMap.put(variable, path);
        if (this.unit != null) {
            // Re-resolve paths for each unit that contains variables in its path
            resolvePath(this.unit);
        }
    }

    /**
     * Parses all {@link #loadUnit(String, IProgressMonitor) loaded}and {@link #setUnitSelected(IUnit, boolean) selected}Rose
     * units that have valid model {@link #setUnitModelFolder(IUnit, IContainer) folders}and
     * {@link #setUnitModelName(IUnit, String) names}.
     * 
     * @param monitor A cancelable progress monitor that will be updated during processing; may be null.
     * @return The list of problems encountered during parsing; never null, unmodifiable.
     * @see #getParseProblems()
     * @since 4.1
     */
    public List parseSelectedUnits( IProgressMonitor monitor ) {
        // Check if anything to parse
        if (this.selectedUnits.isEmpty()) {
            this.parseProblems.add(new Message(IStatus.ERROR, NO_UNITS_SELECTED_MESSAGE));
            return Collections.EMPTY_LIST;
        }
        // Ensure monitor is not null
        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }
        // Update monitor
        monitor.beginTask(START_PARSING_MESSAGE, IProgressMonitor.UNKNOWN);
        try {
            // Clear any info leftover from previous imports
            this.parseProblems.clear();
            this.handler.clear();
            this.diffProcMap.clear();
            // Notify handler that parsing is starting
            this.handler.parsingStarting(monitor);
            // Return if monitor canceled.
            if (monitor.isCanceled()) {
                return Collections.EMPTY_LIST;
            }
            // Parse selected units
            final List units = getSelectedUnits();
            parseUnits(units, new ArrayList(units), monitor);
            // Notify handler that parsing has finished.
            monitor.setTaskName(FINISH_PARSING_MESSAGE);
            final List roots = new ArrayList(this.diffProcMap.size());
            for (final Iterator iter = this.diffProcMap.entrySet().iterator(); iter.hasNext();) {
                final Entry entry = (Entry)iter.next();
                final DifferenceProcessorImpl proc = (DifferenceProcessorImpl)entry.getValue();
                try {
                    roots.add(getRoots(proc));
                } catch (final ModelerCoreException err) {
                    this.parseProblems.add(new Message(err));
                    UTIL.log(err);
                }
            }
            this.handler.parsingFinished(roots);
            // Add handler problems to parse problems list
            this.parseProblems.addAll(this.handler.getProblems());
            // Initialize ambiguous reference count
            this.ambiguousRefs = this.handler.getAmbiguousReferences().size();
            return getParseProblems();
        } finally {
            // Notify monitor task is complete
            monitor.done();
            garbageCollect();
        }
    }

    /**
     * @param reference The ambiguous reference to resolve; must not be null.
     * @param element The element to use to resolve the specified ambiguous reference. if null, an unresolvable reference warning
     *        will be added to the {@link #getParseProblems() list of parse problems}.
     * @return True if all ambiguous references have been resolved.
     * @since 4.1
     */
    public boolean resolveAmbiguousReference( final IAmbiguousReference reference,
                                              Object element ) {
        ArgCheck.isNotNull(reference);
        final Object referencer = reference.getReferencer();
        final String type = reference.getType();
        final String name = reference.getName();
        final String text = this.handler.getUnresolvableReferenceMessage(referencer, type, name);
        final AmbiguousReference ref = (AmbiguousReference)reference;
        if (element == null) {
            // Add unresolvable reference warning to parse problems list if not already present
            boolean found = false;
            for (final Iterator iter = this.parseProblems.iterator(); iter.hasNext();) {
                final IMessage msg = (IMessage)iter.next();
                if (msg.getType() == IStatus.WARNING && referencer == msg.getObject() && text.equals(msg.getText())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                // Set type
                this.handler.createMissingObject(type, referencer, ref.getQuid(), name);
                this.parseProblems.add(new Message(IStatus.WARNING, text, referencer));
            }
        } else {
            // Remove any existing unresolvable reference warning from parse problems list
            for (final Iterator iter = this.parseProblems.iterator(); iter.hasNext();) {
                final IMessage msg = (IMessage)iter.next();
                if (msg.getType() == IStatus.WARNING && referencer == msg.getObject() && text.equals(msg.getText())) {
                    iter.remove();
                    break;
                }
            }
        }
        // Set type or owner on referencer
        ref.setReferencedObject(element);
        this.handler.resolveReference(referencer, element, type, ref.getReferencerQuid());
        // Decrement number of remaining ambiguous references
        if (this.ambiguousRefs > 0) {
            --this.ambiguousRefs;
        }
        return (this.ambiguousRefs == 0);
    }

    /**
     * Sets the folder in which to create the UML model into which the specified Rose unit will be imported.
     * 
     * @param unit The Rose unit.
     * @param folder The folder in which to create the model into which the specified Rose unit will be imported.
     * @since 4.1
     */
    public void setUnitModelFolder( final IUnit unit,
                                    final IContainer folder ) {
        final Unit unitImpl = (Unit)unit;
        unitImpl.setModelFolder(folder);
        // Update parse problem units list.
        updateUnitTargetStatus(unitImpl);
    }

    /**
     * Sets the name of the model into which the specified Rose unit will be imported.
     * 
     * @param unit The Rose unit.
     * @param name The name of the model into which the specified Rose unit will be imported.
     * @since 4.1
     */
    public void setUnitModelName( final IUnit unit,
                                  final String name ) {
        final Unit unitImpl = (Unit)unit;
        unitImpl.setModelName(name);
        // Update parse problem units list.
        updateUnitTargetStatus(unitImpl);
    }

    /**
     * Sets whether the specified Rose unit will be imported.
     * 
     * @param unit The Rose unit.
     * @param selected True if the specified Rose unit is to be imported.
     * @since 4.1
     */
    public void setUnitSelected( final IUnit unit,
                                 final boolean selected ) {
        // Return if unit already selected.
        if (unit.isSelected() == selected) {
            return;
        }
        // Select unit.
        final Unit unitImpl = (Unit)unit;
        unitImpl.setSelected(selected);
        final IUnit unitAncestor = unit.getContainingUnit();
        if (selected) {
            // Add unit to selected units list, sorted within containment heirarchy.
            int ndx = -1;
            IUnit ancestor = unit.getContainingUnit();
            if (ancestor != null) {
                final List siblings = ancestor.getContainedUnits();
                int unitNdx = siblings.indexOf(unit);
                while (ndx < 0 && unitNdx > 0) {
                    ndx = this.selectedUnits.indexOf(siblings.get(--unitNdx));
                }
                if (ndx < 0) {
                    do {
                        ndx = this.selectedUnits.indexOf(ancestor);
                        ancestor = ancestor.getContainingUnit();
                    } while (ndx < 0 && ancestor != null);
                }
            }
            // Add unit to selected units list at appropriate index
            if (ndx < 0) {
                // Ensure that the top-level unit (unitAncestor=null) gets added at index 0
                if (unitAncestor != null) {
                    this.selectedUnits.add(unit);
                } else {
                    this.selectedUnits.add(0, unit);
                }
            } else {
                this.selectedUnits.add(ndx + 1, unit);
            }
            // Update parse problem units list.
            updateUnitTargetStatus(unitImpl);
        } else {
            // Remove unit from list of selected units.
            this.selectedUnits.remove(unit);
            // Remove this unit from list of parse problem units list.
            this.targetProblems.remove(unit);
        }
    }

    /**
     * @since 4.1
     */
    private void fireUnitSourceMessage( final int status,
                                        final String message,
                                        final Unit unit ) {
        fireUnitSourceMessage(new Message(status, message, unit));
    }

    /**
     * @since 4.1
     */
    private void fireUnitSourceMessage( final Throwable error ) {
        fireUnitSourceMessage(new Message(error));
    }

    /**
     * @since 4.1
     */
    private void fireUnitSourceMessage( final IMessage message ) {
        for (final Iterator iter = this.sourceMsgListener.iterator(); iter.hasNext();) {
            ((IMessageListener)iter.next()).messageSent(message);
        }
    }

    /**
     * Recursive.
     * 
     * @since 4.1
     */
    private void parseUnits( final List units,
                             final List unparsedUnits,
                             IProgressMonitor monitor ) {
        for (final Iterator iter = units.iterator(); iter.hasNext();) {
            // Return if monitor canceled.
            if (monitor.isCanceled()) {
                return;
            }
            final Unit unit = (Unit)iter.next();
            // Skip if not selected for import or parent is selected (since parsing of parent will include selected children).
            if (!unparsedUnits.contains(unit) || unparsedUnits.contains(unit.getContainingUnit())) {
                continue;
            }
            // Load unit if necessary
            if (!unit.isLoaded()) {
                loadUnit(unit, null);
                if (!unit.isLoaded()) {
                    this.parseProblems.add(new Message(IStatus.ERROR, unit.getSourceMessage(), unit));
                    continue;
                }
            }
            // Update monitor
            monitor.setTaskName(getString(PARSING_UNIT_MESSAGE_ID, unit.getQualifiedName()));
            // Skip parse problem units.
            if (unit.getTargetStatus() == IStatus.ERROR) {
                this.parseProblems.add(new Message(IStatus.ERROR, unit.getTargetMessage(), unit));
                continue;
            }
            RoseLoader loader = null;
            try {
                // Open input stream for unit
                loader = new RoseLoader(unit.getResolvedPath(), this.uriConverter);
                if (!loader.isValid()) {
                    final Message msg = new Message(IStatus.ERROR, getString(IO_EXCEPTION_MESSAGE_ID, unit.getQualifiedName()),
                                                    unit);
                    this.parseProblems.add(msg);
                    continue;
                }

                // Determine full name of target model
                String name = unit.getModelName();
                if (!name.endsWith(ModelerCore.MODEL_FILE_EXTENSION)) {
                    name += ModelerCore.MODEL_FILE_EXTENSION;
                }

                // make sure all folders exist on the file system first
                IPath path = null;
                IContainer container = unit.getModelFolder();

                if (container instanceof IProject) {
                    // don't need to check if adding the model right at the project level. project does exist.
                    path = container.getFullPath().append(name);
                } else {
                    IFolder folder = ResourcesPlugin.getWorkspace().getRoot().getFolder(unit.getModelFolder().getFullPath());
                    path = folder.getFullPath().append(name);
                }

                // Pass temporary container, specific to model folder and name, to RoseHandler
                DifferenceProcessorImpl diffProc = (DifferenceProcessorImpl)this.diffProcMap.get(path);
                if (diffProc == null) {
                    final URI uri = URI.createURI(path.toString());
                    final TransientModelSelector srcSelector = new TransientModelSelector(uri);
                    srcSelector.open();
                    // Create model and selector for new or existing file
                    final MtkXmiResourceImpl resrc = new MtkXmiResourceImpl(uri);
                    ModelerCore.getModelContainer().getResources().add(resrc);
                    final EmfResourceSelector targetSelector = new EmfResourceSelector(resrc);
                    targetSelector.open();
                    // Create difference analysis report
                    diffProc = new DifferenceProcessorImpl(targetSelector, srcSelector);
                    this.diffProcMap.put(path, diffProc);
                }
                final List roots = getRoots(diffProc);
                // Notify handler that parsing for this unit is about to start
                this.handler.unitParsingStarting(unit, roots);
                // Parse unit
                final RoseParser parser = new RoseParser(new RoseLexer(loader), true, true);
                parser.parse();
                // Visit all parsed nodes using handler
                new RoseWalker(parser.getModelTree()).traverse(this.handler);
                // Notify handler that parsing for this unit has finished
                this.handler.unitParsingFinished(monitor);
            } catch (final Exception err) {
                // Add any problems encountered to problem list.
                this.parseProblems.add(new Message(err, unit));
                UTIL.log(err);
            } finally {
                // Close loader if open.
                if (loader != null) {
                    try {
                        loader.close();
                    } catch (final IOException err) {
                        UTIL.log(err);
                    }
                }
            }
            // Remove unit from unparsed units list.
            unparsedUnits.remove(unit);
            // Import the children of the Unit recursively.
            parseUnits(unit.getContainedUnits(), unparsedUnits, monitor);
        }
    }

    /**
     * @since 4.1
     */
    void resolvePath( final Unit unit ) {
        String remainingPath = unit.getUnresolvedPath();
        String path = EMPTY_STRING;
        int ndx;
        while ((ndx = remainingPath.indexOf(File.separator)) >= 0) {
            String folder = remainingPath.substring(0, ndx);
            if (folder.startsWith(VARIABLE_PREFIX)) {
                final String var = folder.substring(1);
                final String val = (String)this.pathMap.get(var);
                if (val == null) {
                    folder = null;
                    this.pathMap.put(var, MODEL_PATH_SYMBOL);
                } else {
                    folder = val;
                }
            }
            if (folder != null && !CURRENT_FOLDER_SYMBOL.equals(folder) && !MODEL_PATH_SYMBOL.equals(folder)) {
                path += folder + File.separator;
            }
            remainingPath = remainingPath.substring(ndx + 1);
        }
        path += remainingPath;
        // Prefix base path if path is relative
        if (path.indexOf(DRIVE_SEPARATOR_CHAR) == -1 && !path.startsWith(File.separator)) {
            // Save base path for relative .cat file references
            String basePath = unit.getContainingUnit().getResolvedPath();
            basePath = basePath.substring(0, basePath.lastIndexOf(File.separator) + 1);
            path = basePath + path;
        }
        final boolean didExist = unit.exists();
        unit.setResolvedPath(path);
        if (!unit.exists()) {
            if (unit.getUnresolvedPath().indexOf(VARIABLE_PREFIX) >= 0) {
                // Notify listener that unit doesn't exist, but pathmap may fix
                updateUnitSourceStatus(IStatus.WARNING, PATH_UNRESOLVABLE_MESSAGE, unit);
            } else {
                // Notify listener that unit doesn't exist
                updateUnitSourceStatus(IStatus.ERROR, PATH_NOT_FOUND_MESSAGE, unit);
            }
        } else {
            // Resolve paths of children
            for (final Iterator iter = unit.getContainedUnits().iterator(); iter.hasNext();) {
                final Unit child = (Unit)iter.next();
                // Skip units with paths that don't contain variables
                if (child.getUnresolvedPath().indexOf(VARIABLE_PREFIX) < 0) {
                    continue;
                }
                // Save exists status in case didn't exist before and ends up existing after re-resolution
                resolvePath(child);
            }
            if (!didExist) {
                // Notify listener that path is now resolvable and unit exists
                updateUnitSourceStatus(IStatus.OK, null, unit);
            }
        }
    }

    /**
     * Indicates if the importer should merge the models owned by the specified processor. The <code>skip</code> flag is used to
     * make this determination.
     * 
     * @param theDiffProcessor the processor being checked
     * @return <code>true</code> if the models should be merged; <code>false</code> otherwise.
     * @since 4.2
     */
    private boolean shouldMergeModels( DifferenceProcessor theDiffProcessor ) {
        boolean result = true;
        final Mapping mapping = theDiffProcessor.getDifferenceReport().getMapping();
        final MappingHelper helper = mapping.getHelper();

        if ((helper != null) && (helper instanceof DifferenceDescriptor) && ((DifferenceDescriptor)helper).isSkip()) {
            result = false;
        }

        return result;
    }

    /**
     * Sets model folders of sibling and descendant units based on their source unit path relative to the specified unit's model
     * folder. Only units that exist will be modified. If the specified input unit does not exist, is not selected, or does not
     * have a model folder, nothing happens.
     * 
     * @param theUnit the unit whose model folder is being used
     * @since 4.2
     */
    public void synchronizePathsRelativeTo( IUnit theUnit ) {
        if (theUnit.exists()) {
            IContainer folder = theUnit.getModelFolder();

            if ((folder != null) && this.selectedUnits.contains(theUnit)) {
                // walk through selected units and modify model folder relative to input unit
                String folderTxt = new File(theUnit.getResolvedPath()).getParent();
                int nextPartIndex = folderTxt.length();
                Iterator itr = this.selectedUnits.iterator();

                while (itr.hasNext()) {
                    IUnit selectedUnit = (IUnit)itr.next();

                    if ((selectedUnit != theUnit) && selectedUnit.exists()) {
                        String tmpFolderTxt = new File(selectedUnit.getResolvedPath()).getParent();
                        int index = tmpFolderTxt.indexOf(folderTxt);

                        // if index != 0 then unit is not a sibling or a descendant of the input unit
                        // so don't set it's model folder. if index == 0 then set it.
                        if (index == 0) {
                            // set to same folder by default
                            IContainer modelFolder = folder;

                            if (!folderTxt.equals(tmpFolderTxt)) {
                                // append rest of path
                                IPath path = new Path(tmpFolderTxt.substring(nextPartIndex));
                                modelFolder = folder.getFolder(path);
                            }

                            setUnitModelFolder(selectedUnit, modelFolder);
                        }
                    }
                }
            }
        }
    }

    /**
     * @since 4.1
     */
    String toNativePath( String path ) {
        path = Util.trimQuotes(path);
        path = Util.updateFileName(path, "\\\\"); //$NON-NLS-1$
        path = Util.updateFileName(path, "\\"); //$NON-NLS-1$
        path = Util.updateFileName(path, "/"); //$NON-NLS-1$
        return path;
    }

    /**
     * @since 4.1
     */
    private void updateUnitSourceStatus( final int status,
                                         final String message,
                                         final Unit unit ) {
        // If unit supplied, save status and message in it
        if (unit != null) {
            unit.setLoadStatus(status);
            unit.setLoadMessage(message);
        }
        // Notify load listeners
        fireUnitSourceMessage(status, message, unit);
    }

    /**
     * @since 4.1
     */
    private void updateUnitTargetStatus( final Unit unit ) {
        // Initialize this unit as being OK.
        updateUnitTargetStatus(IStatus.OK, null, unit);
        // Check if unit has a folder.
        final IContainer folder = unit.getModelFolder();
        if (folder == null) {
            updateUnitTargetStatus(IStatus.ERROR, getString(MISSING_FOLDER_MESSAGE_ID, unit.getName()), unit);
        } else if (folder instanceof IProject && !((IProject)folder).isOpen()) {
            // Folder is a closed project
            updateUnitTargetStatus(IStatus.ERROR, getString(CLOSED_PROJECT_MESSAGE_ID,
                                                            unit.getName(),
                                                            folder.getFullPath().makeRelative()), unit);
        } else {
            // Check if unit destination is a folder.
            final IResource resrc = unit.getWorkspaceResource();
            if (resrc != null) {
                if (ModelUtil.isIResourceReadOnly(resrc)) {
                    // Unit destination is a read-only
                    updateUnitTargetStatus(IStatus.ERROR,
                                           getString(READ_ONLY_MESSAGE_ID, unit.getName(), resrc.getFullPath()),
                                           unit);
                } else if (resrc instanceof IContainer) {
                    // Unit destination is a folder
                    updateUnitTargetStatus(IStatus.ERROR, getString(FOLDER_MESSAGE_ID, unit.getName(), resrc.getFullPath()), unit);
                } else if (!ModelUtil.isModelFile(resrc)) {
                    // Unit destination is a non-model.
                    updateUnitTargetStatus(IStatus.WARNING,
                                           getString(NOT_MODEL_MESSAGE_ID, unit.getName(), resrc.getFullPath()),
                                           unit);
                } else {
                    String uri = null;
                    try {
                        uri = ModelUtil.getModel(resrc).getPrimaryMetamodelDescriptor().getNamespaceURI();
                    } catch (final ModelWorkspaceException err) {
                        UTIL.log(err);
                    }
                    // Check if unit destination is a model from different metamodel.
                    if (uri != null && !uri.equals(this.handler.getPrimaryMetamodelUri())) {
                        updateUnitTargetStatus(IStatus.WARNING, getString(DIFFERENT_METAMODEL_MESSAGE_ID,
                                                                          unit.getName(),
                                                                          resrc.getFullPath(),
                                                                          uri), unit);
                    }
                }
            }
        }
    }

    /**
     * @since 4.1
     */
    private void updateUnitTargetStatus( final int status,
                                         final String message,
                                         final Unit unit ) {
        // Save status and message in unit
        unit.setParseStatus(status);
        unit.setParseMessage(message);
        // Remove unit from parse problem units list
        this.targetProblems.remove(unit);
        // Return if parse status OK.
        if (status == IStatus.OK) {
            return;
        }
        // Add message to appropriate location in parse problem units list.
        int ndx = 0;
        final Iterator iter = this.targetProblems.iterator();
        // Move past higher severity problems
        if (iter.hasNext()) {
            Unit problemUnit = (Unit)iter.next();
            while (problemUnit.getTargetStatus() > status) {
                ++ndx;
                if (iter.hasNext()) {
                    problemUnit = (Unit)iter.next();
                } else {
                    break;
                }
            }
            if (problemUnit.getTargetStatus() == status) {
                // Move past problems for units earlier in the parse problem units list.
                final int selectionNdx = this.selectedUnits.indexOf(unit);
                while (problemUnit.getTargetStatus() == status && this.selectedUnits.indexOf(problemUnit) < selectionNdx) {
                    ++ndx;
                    if (iter.hasNext()) {
                        problemUnit = (Unit)iter.next();
                    } else {
                        break;
                    }
                }
            }
        }
        // Add problem to parse problem units list
        this.targetProblems.add(ndx, unit);
    }

    /**
     * Provided for test cases only.
     * 
     * @return The collection of difference processors used to create models in the workspace.
     * @since 4.1
     */
    public Collection testGetDifferenceProcessors() {
        return this.diffProcMap.values();
    }

    /**
     * @since 4.1
     */
    class UnitBuilder {

        private static final String QUALIFIER_SEPARATOR = "."; //$NON-NLS-1$

        /**
         * @since 4.1
         */
        void traverse( final String qualifier,
                       final RoseNode node,
                       final Unit unit ) {
            List nodes = node.getNodes();
            for (int ndx = 0; ndx < nodes.size(); ndx++) {
                final RoseNode child = (RoseNode)nodes.get(ndx);
                if (child.getRoseNodeType() == RoseNode.OBJECT) {
                    traverseObject(qualifier, child, unit);
                } else if (child.getRoseNodeType() == RoseNode.LIST) {
                    traverseList(qualifier, child, unit);
                }
            }
        }

        /**
         * @since 4.1
         */
        private void traverseList( final String qualifier,
                                   final RoseNode node,
                                   final Unit unit ) {
            traverse(qualifier, node, unit);
        }

        /**
         * @since 4.1
         */
        private void traverseObject( final String qualifier,
                                     final RoseNode node,
                                     final Unit unit ) {
            final String key = node.getKey();
            final String type = Util.getType(node.getValue());
            final String name = Util.getName(node.getValue());
            if (RoseStrings.ROOT_CATEGORY.equals(key) && RoseStrings.CLASS_CATEGORY.equals(type)) {
                traverse(qualifier, node, unit);
            } else if (EMPTY_STRING.equals(key) && RoseStrings.CLASS_CATEGORY.equals(type)) {
                if (node.findNodeWithKey(RoseStrings.IS_LOADED) != null) {
                    // The package is in a .cat file.
                    final String path = toNativePath(node.findNodeWithKey(RoseStrings.FILE_NAME).getValue());
                    final Unit child = new Unit(name, path);
                    unit.addUnit(child);
                    resolvePath(child);
                } else {
                    // The package not in a .cat file. (Maybe handles component files?)
                    String qualifiedName = name;
                    if (qualifier != null) {
                        qualifiedName = qualifier + QUALIFIER_SEPARATOR + name;
                    }
                    traverse(qualifiedName, node, unit);
                }
            } else if (RoseStrings.CLASS.equals(type)) {
                final String qualifiedName = (qualifier == null ? name : qualifier + QUALIFIER_SEPARATOR + name);
                traverse(qualifiedName, node, unit);
            } else if (RoseStrings.OPERATION.equals(type) || RoseStrings.CLASSATTRIBUTE.equals(type)
                       || RoseStrings.INHERITANCE_RELATIONSHIP.equals(type) || RoseStrings.ASSOCIATION.equals(type)
                       || RoseStrings.ROLE.equals(type) || RoseStrings.VISIBILITY_RELATIONSHIP.equals(type)
                       || RoseStrings.USES_RELATIONSHIP.equals(type)) {
                traverse(qualifier, node, unit);
            }
        }
    }
}
