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

package com.metamatrix.modeler.compare.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.emf.mapping.MappingHelper;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.modeler.compare.DifferenceDescriptor;
import com.metamatrix.modeler.compare.DifferenceReport;
import com.metamatrix.modeler.compare.DifferenceType;
import com.metamatrix.modeler.compare.MergeProcessor;
import com.metamatrix.modeler.compare.ModelerComparePlugin;
import com.metamatrix.modeler.compare.PropertyDifference;
import com.metamatrix.modeler.compare.selector.ModelSelector;
import com.metamatrix.modeler.core.ModelEditor;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.util.ModelVisitor;
import com.metamatrix.modeler.core.util.ModelVisitorProcessor;
import com.metamatrix.modeler.core.util.ModelVisitorWithFinish;

/**
 * MergeProcessorImpl
 */
public class MergeProcessorImpl implements MergeProcessor {

    private static final String PLUGINID = ModelerComparePlugin.PLUGIN_ID;

    // -------------------------------------------------------------------------
    // MESSAGE CODE CONSTANTS
    // -------------------------------------------------------------------------
    protected static final int PROCESSOR_ALREADY_CLOSED = 60001;
    protected static final int NO_PROBLEMS = 60002;
    protected static final int HAS_WARNINGS = 60003;
    protected static final int HAS_ERRORS = 60004;
    protected static final int HAS_WARNINGS_AND_ERRORS = 60005;
    protected static final int NO_WARNINGS_AND_ERRORS = 60006;
    protected static final int ERROR_PLANNING_MERGE = 60010;
    protected static final int ERROR_MERGING_ADDS_AND_DELETES = 60011;
    protected static final int ERROR_MERGING_CHANGES = 60012;
    protected static final int ERROR_RESOLVING_REFERENCES = 60013;

    // -------------------------------------------------------------------------
    // PROGRESS MONITOR CONSTANTS
    // -------------------------------------------------------------------------
    protected static final int AMOUNT_OF_WORK_FOR_PLANNING = 200;
    protected static final int AMOUNT_OF_WORK_FOR_MERGING_ADDS_AND_DELETES = 10000;
    protected static final int AMOUNT_OF_WORK_FOR_MERGING_CHANGES = AMOUNT_OF_WORK_FOR_MERGING_ADDS_AND_DELETES;
    protected static final int AMOUNT_OF_WORK_FOR_REFERENCE_RESOLUTION = 300;
    protected static final int AMOUNT_OF_WORK_FOR_CREATING_RESULTS = 100;

    public static final boolean DEFAULT_ADDS_MOVED_FROM_STARTING_INTO_ENDING = false;

    private final DifferenceReport differenceReport;
    private final ModelSelector sourceModelSelector;
    // private final ModelSelector resultsModelSelector;
    private IProgressMonitor monitor;
    private List problems;
    private WorkInfo workInfo;
    private ModelEditor editor;
    private Map resultObjectToSourceObject;
    private final List newRoots;
    private boolean computeTasks;
    private boolean closed;
    private final boolean moveAddedObjectsFromStartingSelector;

    /**
     * Construct a MergeProcessor that merges the supplied changes/differences. The result of executing the merge processor is to
     * convert the original (starting) state of the difference report into the final (ending) state.
     * <p>
     * This is called by the {@link com.metamatrix.modeler.compare.ModelerComparePlugin#createMergeProcessor(DifferenceProcessor)}
     * method when the resource has no unsaved changes (i.e., there are no differences}.
     * </p>
     * 
     * @param resource the resource; may not be null
     * @param moveAddsRatherThanCopy true if objects that are considered "adds" should be <i>moved</i> rather than copied (will be
     *        removed from the source model), or false the source model should be left unchanged and any "adds" be copied into the
     *        output model
     */
    public MergeProcessorImpl( final DifferenceReport differenceReport,
                               final ModelSelector startingModelSelector,
                               final ModelSelector endingModelSelector,
                               final EObject[] externalReferences,
                               final boolean moveAddsRatherThanCopy ) {
        super();
        ArgCheck.isNotNull(differenceReport);
        ArgCheck.isNotNull(startingModelSelector);
        ArgCheck.isNotNull(endingModelSelector);
        this.differenceReport = differenceReport;
        this.sourceModelSelector = startingModelSelector;
        // this.resultsModelSelector = endingModelSelector;
        this.resultObjectToSourceObject = new HashMap();
        this.newRoots = new LinkedList();

        // Initialize the resultObjectToSourceObject adding EObject references
        // that are external to the starting or ending models being merged
        this.initializeResultToSourceMap(externalReferences);
        this.moveAddedObjectsFromStartingSelector = moveAddsRatherThanCopy;
    }

    /**
     * Construct a MergeProcessor that merges the supplied changes/differences. The result of executing the merge processor is to
     * convert the original (starting) state of the difference report into the final (ending) state.
     * <p>
     * This is called by the {@link com.metamatrix.modeler.compare.ModelerComparePlugin#createMergeProcessor(DifferenceProcessor)}
     * method when the resource has no unsaved changes (i.e., there are no differences}.
     * </p>
     * 
     * @param resource the resource; may not be null
     */
    public MergeProcessorImpl( final DifferenceReport differenceReport,
                               final ModelSelector startingModelSelector,
                               final ModelSelector endingModelSelector,
                               final EObject[] externalReferences ) {
        this(differenceReport, startingModelSelector, endingModelSelector, externalReferences,
             DEFAULT_ADDS_MOVED_FROM_STARTING_INTO_ENDING);
    }

    /**
     * Construct a MergeProcessor that merges the supplied changes/differences. The result of executing the merge processor is to
     * convert the original (starting) state of the difference report into the final (ending) state.
     * <p>
     * This is called by the {@link com.metamatrix.modeler.compare.ModelerComparePlugin#createMergeProcessor(DifferenceProcessor)}
     * method when the resource has no unsaved changes (i.e., there are no differences}.
     * </p>
     * 
     * @param resource the resource; may not be null
     */
    public MergeProcessorImpl( final DifferenceReport differenceReport,
                               final ModelSelector startingModelSelector,
                               final ModelSelector endingModelSelector ) {
        this(differenceReport, startingModelSelector, endingModelSelector, null);
    }

    /**
     * Create a merge processor that merges the differences computed by the supplied processor. The result of executing the merge
     * processor is to convert the original (starting) state of the difference report into the final (ending) state.
     * 
     * @param difference the difference report that specifies those differences that should be merged
     * @return the processor that can be used to execute the merge
     */
    public MergeProcessorImpl( final DifferenceProcessorImpl differenceProcessor,
                               final EObject[] externalReferences ) {
        this(differenceProcessor.getDifferenceReport(), differenceProcessor.getBeforeSelector(),
             differenceProcessor.getAfterSelector(), externalReferences);
    }

    /**
     * Create a merge processor that merges the differences computed by the supplied processor. The result of executing the merge
     * processor is to convert the original (starting) state of the difference report into the final (ending) state.
     * 
     * @param difference the difference report that specifies those differences that should be merged
     * @param moveAddsRatherThanCopy true if objects that are considered "adds" should be <i>moved</i> rather than copied (will be
     *        removed from the source model), or false the source model should be left unchanged and any "adds" be copied into the
     *        output model
     * @return the processor that can be used to execute the merge
     */
    public MergeProcessorImpl( final DifferenceProcessorImpl differenceProcessor,
                               final EObject[] externalReferences,
                               final boolean moveAddsRatherThanCopy ) {
        this(differenceProcessor.getDifferenceReport(), differenceProcessor.getBeforeSelector(),
             differenceProcessor.getAfterSelector(), externalReferences, moveAddsRatherThanCopy);
    }

    /**
     * Create a merge processor that merges the differences computed by the supplied processor. The result of executing the merge
     * processor is to convert the original (starting) state of the difference report into the final (ending) state.
     * 
     * @param difference the difference report that specifies those differences that should be merged
     * @return the processor that can be used to execute the merge
     */
    public MergeProcessorImpl( final DifferenceProcessorImpl differenceProcessor ) {
        this(differenceProcessor.getDifferenceReport(), differenceProcessor.getBeforeSelector(),
             differenceProcessor.getAfterSelector());
    }

    /**
     * Pre-populate the resultObjectToSourceObject map with EObject instances that can be referenced from both the starting and
     * ending models but are not contained in either model.
     * 
     * @param externalReferences
     * @since 4.2
     */
    private void initializeResultToSourceMap( final EObject[] externalReferences ) {
        if (externalReferences != null && externalReferences.length > 0) {
            for (int i = 0; i != externalReferences.length; ++i) {
                final EObject obj = externalReferences[i];
                // Since this EObject is external to both the starting and
                // ending models we use it as both the result object
                // reference (key) and source object reference (value)
                this.resultObjectToSourceObject.put(obj, obj);
            }
        }
    }

    public void setEndingToSourceMapping( final Map mapping ) {
        this.resultObjectToSourceObject = mapping != null ? mapping : new HashMap();
    }

    /**
     * Return whether objects that are "adds" in the ending selector are to be moved into the ending selector rather than copied
     * into the ending selector. By default, objects are copied (i.e., this method returns <code>false</code>). If it is
     * acceptable that the starting model is modified (the objects are <i>removed</i> from the starting model and inserted into
     * the ending model), then this can be set to <code>true</code> (which may result in a performance boost).
     * 
     * @return true if the "adds" in the different report are to be moved from the starting model into the ending model, or false
     *         if the starting model is to be left unmodified.
     * @since 4.2
     */
    public boolean isMoveAddedObjectsFromStartingSelector() {
        return this.moveAddedObjectsFromStartingSelector;
    }

    /**
     * @see com.metamatrix.modeler.compare.MergeProcessor#execute(org.eclipse.core.runtime.IProgressMonitor)
     */
    public IStatus execute( final IProgressMonitor progressMonitor ) {
        if (closed) {
            final int code = PROCESSOR_ALREADY_CLOSED;
            final String msg = ModelerComparePlugin.Util.getString("MergeProcessorImpl.The_processor_has_already_been_closed"); //$NON-NLS-1$
            final IStatus status = new Status(IStatus.ERROR, PLUGINID, code, msg, null);
            return status;
        }

        // -------------------------------------------------------------------------
        // Initialize up the progress monitor ...
        // -------------------------------------------------------------------------
        if (progressMonitor != null) {
            computeTasks = true;
            this.monitor = progressMonitor;
        } else {
            computeTasks = false;
            this.monitor = new NullProgressMonitor();
        }
        final String taskName = doGetTaskName();
        final int totalWork = doComputeTotalWork();
        this.monitor.beginTask(taskName, totalWork);

        // -------------------------------------------------------------------------
        // Do the real execution ...
        // -------------------------------------------------------------------------
        final Object[] paramsExec = new Object[] {this.sourceModelSelector.getLabel()};
        final String execSubTask = ModelerComparePlugin.Util.getString("MergeProcessorImpl.Performing_merge", paramsExec); //$NON-NLS-1$
        this.monitor.subTask(execSubTask);

        this.newRoots.clear();
        this.editor = ModelerCore.getModelEditor();
        this.problems = new LinkedList();
        doExecute();

        // -------------------------------------------------------------------------
        // Analyze any problems ...
        // -------------------------------------------------------------------------
        IStatus resultStatus = null;
        try {
            // Create a subtask ...
            final String analysisSubTask = ModelerComparePlugin.Util.getString("MergeProcessorImpl.Analyzing_problems"); //$NON-NLS-1$
            this.monitor.subTask(analysisSubTask);

            // Put all of the problems into a single IStatus ...
            if (this.problems.isEmpty()) {
                final String msg = ModelerComparePlugin.Util.getString("MergeProcessorImpl.Execution_completed"); //$NON-NLS-1$
                final IStatus status = new Status(IStatus.OK, PLUGINID, NO_PROBLEMS, msg, null);
                resultStatus = status;
            } else if (this.problems.size() == 1) {
                resultStatus = (IStatus)problems.get(0);
            } else {
                // There were problems, so determine whether there were warnings and errors ...
                int numErrors = 0;
                int numWarnings = 0;
                final Iterator iter = problems.iterator();
                while (iter.hasNext()) {
                    final IStatus aStatus = (IStatus)iter.next();
                    if (aStatus.getSeverity() == IStatus.WARNING) {
                        ++numWarnings;
                    } else if (aStatus.getSeverity() == IStatus.ERROR) {
                        ++numErrors;
                    }
                }

                // Create the final status ...
                final IStatus[] statusArray = (IStatus[])this.problems.toArray(new IStatus[this.problems.size()]);
                if (numWarnings != 0 && numErrors == 0) {
                    final Object[] params = new Object[] {new Integer(numWarnings)};
                    final String msg = ModelerComparePlugin.Util.getString("MergeProcessorImpl.Execution_resulted_in_warnings", params); //$NON-NLS-1$
                    resultStatus = new MultiStatus(PLUGINID, HAS_WARNINGS, statusArray, msg, null);
                } else if (numWarnings == 0 && numErrors != 0) {
                    final Object[] params = new Object[] {new Integer(numErrors)};
                    final String msg = ModelerComparePlugin.Util.getString("MergeProcessorImpl.Execution_resulted_in_errors", params); //$NON-NLS-1$
                    resultStatus = new MultiStatus(PLUGINID, HAS_ERRORS, statusArray, msg, null);
                } else if (numWarnings != 0 && numErrors != 0) {
                    final Object[] params = new Object[] {new Integer(numWarnings), new Integer(numErrors)};
                    final String msg = ModelerComparePlugin.Util.getString("MergeProcessorImpl.Execution_resulted_in_warnings_and_errors", params); //$NON-NLS-1$
                    resultStatus = new MultiStatus(PLUGINID, HAS_WARNINGS_AND_ERRORS, statusArray, msg, null);
                } else {
                    final String msg = ModelerComparePlugin.Util.getString("MergeProcessorImpl.Execution_completed_with_no_warnings_or_errors"); //$NON-NLS-1$
                    resultStatus = new MultiStatus(PLUGINID, NO_WARNINGS_AND_ERRORS, statusArray, msg, null);
                }
            }

        } finally {
            this.monitor.worked(AMOUNT_OF_WORK_FOR_CREATING_RESULTS);
        }
        return resultStatus;
    }

    /**
     * @see com.metamatrix.modeler.compare.MergeProcessor#reresolve(org.eclipse.core.runtime.IProgressMonitor)
     * @since 4.2
     */
    public void reresolve( IProgressMonitor monitor ) {
        doReResolveAndRebuildImports();
    }

    /**
     * @see com.metamatrix.modeler.compare.MergeProcessor#close()
     */
    public void close() {
        if (this.closed) {
            return;
        }
        doClose();
        this.closed = true;
    }

    // =========================================================================
    // Helper methods
    // =========================================================================

    protected IStatus newWarning( final int code,
                                  final String msg,
                                  final Throwable t ) {
        return new Status(IStatus.WARNING, PLUGINID, code, msg, t);
    }

    protected IStatus newInfo( final int code,
                               final String msg,
                               final Throwable t ) {
        return new Status(IStatus.INFO, PLUGINID, code, msg, t);
    }

    protected IStatus newError( final int code,
                                final String msg,
                                final Throwable t ) {
        return new Status(IStatus.ERROR, PLUGINID, code, msg, t);
    }

    protected IStatus newOk( final int code,
                             final String msg,
                             final Throwable t ) {
        return new Status(IStatus.OK, PLUGINID, code, msg, t);
    }

    // =========================================================================
    // Methods that can be overridden to specialize behavior
    // =========================================================================

    /**
     * Do the work of closing the processor. This method should not do anything with the {@link #closed} attribute, since that is
     * handled by the {@link #close()} method.
     */
    protected void doClose() {
        this.problems = null;
        this.monitor = null;
        this.newRoots.clear();
        this.resultObjectToSourceObject.clear();
    }

    protected int doComputeTotalWork() {
        return AMOUNT_OF_WORK_FOR_PLANNING + AMOUNT_OF_WORK_FOR_MERGING_ADDS_AND_DELETES + AMOUNT_OF_WORK_FOR_MERGING_CHANGES
               + AMOUNT_OF_WORK_FOR_REFERENCE_RESOLUTION + AMOUNT_OF_WORK_FOR_CREATING_RESULTS;
    }

    protected String doGetTaskName() {
        return ModelerComparePlugin.Util.getString("MergeProcessorImpl.taskName"); //$NON-NLS-1$
    }

    protected void doExecute() {
        // -------------------------------------------------------------------------
        // Phase 1: Plan how much work is required ...
        // -------------------------------------------------------------------------
        final PlanningVisitor planningVisitor = new PlanningVisitor();
        try {
            // First, iterate through the report and count up the number of Mapping instances ...
            final ModelVisitorProcessor processor = new ModelVisitorProcessor(planningVisitor);
            processor.walk(this.differenceReport, ModelVisitorProcessor.DEPTH_INFINITE);
        } catch (Throwable e) {
            final String msg = ModelerComparePlugin.Util.getString("MergeProcessorImpl.Error_planning_the_merge"); //$NON-NLS-1$
            final IStatus status = newError(ERROR_PLANNING_MERGE, msg, e);
            problems.add(status);
        } finally {
            this.monitor.worked(AMOUNT_OF_WORK_FOR_PLANNING);
        }
        // Compute the amount of work per mapping ...
        final int numMappings = planningVisitor.getMappingCount();
        this.workInfo = new WorkInfo(numMappings, AMOUNT_OF_WORK_FOR_MERGING_ADDS_AND_DELETES);

        // -------------------------------------------------------------------------
        // Phase 2: Perform the adds and deletes ...
        // -------------------------------------------------------------------------
        final AddAndDeleteVisitor addAndDeleteVisitor = new AddAndDeleteVisitor();
        try {
            // First, iterate through the report and count up the number of Mapping instances ...
            final ModelVisitorProcessor processor = new ModelVisitorProcessor(addAndDeleteVisitor);
            processor.walk(this.differenceReport, ModelVisitorProcessor.DEPTH_INFINITE);
        } catch (Throwable e) {
            final String msg = ModelerComparePlugin.Util.getString("MergeProcessorImpl.Error_while_merging_adds_and_deletes"); //$NON-NLS-1$
            final IStatus status = newError(ERROR_MERGING_ADDS_AND_DELETES, msg, e);
            problems.add(status);
        } finally {
            this.monitor.worked(this.workInfo.workRemaining);
        }

        // -------------------------------------------------------------------------
        // Phase 3: Perform the property changes ...
        // -------------------------------------------------------------------------
        final ChangeVisitor changeVisitor = new ChangeVisitor();
        try {
            // First, iterate through the report and count up the number of Mapping instances ...
            final ModelVisitorProcessor processor = new ModelVisitorProcessor(changeVisitor);
            processor.walk(this.differenceReport, ModelVisitorProcessor.DEPTH_INFINITE);
        } catch (Throwable e) {
            final String msg = ModelerComparePlugin.Util.getString("MergeProcessorImpl.Error_while_merging_changes"); //$NON-NLS-1$
            final IStatus status = newError(ERROR_MERGING_CHANGES, msg, e);
            problems.add(status);
        } finally {
            this.monitor.worked(this.workInfo.workRemaining);
        }

        // -------------------------------------------------------------------------
        // Phase 4: Put the new objects into the resource ...
        // -------------------------------------------------------------------------
        if (this.newRoots != null && !this.newRoots.isEmpty()) {
            try {
                // Figure out which is the last root that has been mapped...
                int indexOfLastMappedRoot = -1;
                int index = -1;
                final Iterator iter = this.sourceModelSelector.getRootObjects().iterator();
                while (iter.hasNext()) {
                    final Object root = iter.next();
                    ++index;
                    if (this.resultObjectToSourceObject.containsValue(root)) {
                        indexOfLastMappedRoot = index;
                    }
                }

                // Add the new roots right after the last mapped root object ...
                this.sourceModelSelector.addRootObjects(this.newRoots, indexOfLastMappedRoot + 1);
            } catch (Throwable e) {
                final String msg = ModelerComparePlugin.Util.getString("MergeProcessorImpl.Error_while_adding_new_root_objects"); //$NON-NLS-1$
                final IStatus status = newError(ERROR_RESOLVING_REFERENCES, msg, e);
                problems.add(status);
            } finally {
                this.monitor.worked(AMOUNT_OF_WORK_FOR_REFERENCE_RESOLUTION);
            }
        }

        // -------------------------------------------------------------------------
        // Phase 5: Replace references in the (now modified) source graph
        // pointing to objects in the results graph. This is a side effect
        // of the 'add' steps (since the things added to the results were
        // copied and put into the source) ...
        // -------------------------------------------------------------------------
        final ResolutionVisitor resolveVisitor = new ResolutionVisitor();
        try {
            final ModelVisitorProcessor processor = new ModelVisitorProcessor(resolveVisitor);
            final List sourceRoots = doGetSourceRoots();
            processor.walk(sourceRoots, ModelVisitorProcessor.DEPTH_INFINITE);
        } catch (Throwable e) {
            final String msg = ModelerComparePlugin.Util.getString("MergeProcessorImpl.Error_while_resolving_references"); //$NON-NLS-1$
            final IStatus status = newError(ERROR_RESOLVING_REFERENCES, msg, e);
            problems.add(status);
        } finally {
            this.monitor.worked(AMOUNT_OF_WORK_FOR_REFERENCE_RESOLUTION);
        }

        // -------------------------------------------------------------------------
        // Phase 6: Rebuild model imports ...
        // -------------------------------------------------------------------------
        try {
            this.sourceModelSelector.rebuildModelImports();
        } catch (Throwable e) {
            final String msg = ModelerComparePlugin.Util.getString("MergeProcessorImpl.Error_while_rebuilding_imports"); //$NON-NLS-1$
            final IStatus status = newError(ERROR_RESOLVING_REFERENCES, msg, e);
            problems.add(status);
        } finally {
            this.monitor.worked(AMOUNT_OF_WORK_FOR_REFERENCE_RESOLUTION);
        }

    }

    protected void doReResolveAndRebuildImports() {
        final ResolutionVisitor resolveVisitor = new ResolutionVisitor();
        try {
            final ModelVisitorProcessor processor = new ModelVisitorProcessor(resolveVisitor);
            final List sourceRoots = doGetSourceRoots();
            processor.walk(sourceRoots, ModelVisitorProcessor.DEPTH_INFINITE);
        } catch (Throwable e) {
            final String msg = ModelerComparePlugin.Util.getString("MergeProcessorImpl.Error_while_resolving_references"); //$NON-NLS-1$
            final IStatus status = newError(ERROR_RESOLVING_REFERENCES, msg, e);
            problems.add(status);
        } finally {
            this.monitor.worked(AMOUNT_OF_WORK_FOR_REFERENCE_RESOLUTION);
        }

        try {
            this.sourceModelSelector.rebuildModelImports();
        } catch (Throwable e) {
            final String msg = ModelerComparePlugin.Util.getString("MergeProcessorImpl.Error_while_rebuilding_imports"); //$NON-NLS-1$
            final IStatus status = newError(ERROR_RESOLVING_REFERENCES, msg, e);
            problems.add(status);
        } finally {
            this.monitor.worked(AMOUNT_OF_WORK_FOR_REFERENCE_RESOLUTION);
        }
    }

    protected List doGetSourceRoots() throws ModelerCoreException {
        final List roots = new LinkedList(this.sourceModelSelector.getRootObjects());
        // Add the new roots that haven't been added yet ...
        final Iterator iter = this.newRoots.iterator();
        while (iter.hasNext()) {
            final Object newRoot = iter.next();
            if (!roots.contains(newRoot)) {
                roots.add(newRoot);
            }
        }
        return roots;
    }

    protected void doProcess( final Mapping mapping,
                              final boolean adds,
                              final boolean changes,
                              final boolean deletes ) throws ModelerCoreException {
        try {
            final MappingHelper helper = mapping.getHelper();
            if (helper != null && helper instanceof DifferenceDescriptor) {
                final DifferenceDescriptor diffDesc = (DifferenceDescriptor)helper;

                // Do this only if not skipping ...
                final boolean skip = diffDesc.isSkip();
                if (!skip) {
                    final DifferenceType type = diffDesc.getType();
                    final int typeValue = type.getValue();
                    switch (typeValue) {
                        case DifferenceType.ADDITION:
                            if (adds) {
                                doAdd(mapping, diffDesc);
                            }
                            break;
                        case DifferenceType.CHANGE:
                            if (changes) {
                                doChange(mapping, diffDesc);
                            }
                            break;
                        case DifferenceType.DELETION:
                            if (deletes) {
                                doDelete(mapping, diffDesc);
                            }
                            break;
                        case DifferenceType.NO_CHANGE:
                        case DifferenceType.CHANGE_BELOW:
                        default:
                            // do nothing
                            break;
                    }
                }

            }
        } finally {
            this.monitor.worked(this.workInfo.workPerMapping);
        }
    }

    protected void doPostProcess( final Mapping mapping ) {
        try {
            final MappingHelper helper = mapping.getHelper();
            if (helper != null && helper instanceof DifferenceDescriptor) {
                final DifferenceDescriptor diffDesc = (DifferenceDescriptor)helper;
                // Skip if this node is to be skipped OR if this is the root mapping ...
                final boolean skip = diffDesc.isSkip() || mapping.getNestedIn() == null;
                if (!skip) {
                    final DifferenceType type = diffDesc.getType();
                    final int typeValue = type.getValue();
                    switch (typeValue) {
                        case DifferenceType.NO_CHANGE:
                        case DifferenceType.CHANGE_BELOW:
                        case DifferenceType.CHANGE:
                            doOrderContained(mapping, diffDesc);
                            break;
                        case DifferenceType.DELETION:
                        case DifferenceType.ADDITION:
                        default:
                            // do nothing
                            break;
                    }
                }
            }
        } finally {
            this.monitor.worked(this.workInfo.workPerMapping);
        }
    }

    protected String computeSubtaskPath( final EObject object ) {
        // Return the path; we don't want the model in the path
        return this.editor.getModelRelativePath(object).toString();
    }

    /**
     * Method the perform the 'add' operation defined by the supplied descriptor and mapping.
     * 
     * @param mapping
     * @param diffDesc
     */
    protected void doAdd( final Mapping mapping,
                          final DifferenceDescriptor diffDesc ) throws ModelerCoreException {
        // There should be no input ...
        final List outputs = mapping.getOutputs();
        final EObject newObject = (EObject)outputs.get(0);
        if (computeTasks) {
            final String path = computeSubtaskPath(newObject);
            final Object[] params = new Object[] {path};
            final String loadingSubTask = ModelerComparePlugin.Util.getString("MergeProcessorImpl.AddingSubTask", params); //$NON-NLS-1$
            this.monitor.subTask(loadingSubTask);
        }
        // Make a copy of the new object and it's contents. Here, the 'orinals to copies' map is
        // the 'resultObjectToSourceObject' map, since the 'original' of the copy operation is
        // the object in the 'result' model and since the 'copy' will get put into the 'source' model ...
        final EObject copy = this.moveAddedObjectsFromStartingSelector ? newObject : // if move, then just reference the copy
        this.editor.copy(newObject, this.resultObjectToSourceObject);

        // Add the copy under the correct feature ...
        final EStructuralFeature feature = newObject.eContainmentFeature();
        if (feature == null) {
            // There is no container (i.e., this must be a root-level object)
            if (this.moveAddedObjectsFromStartingSelector) {
                // First remove from the original model ...
                newObject.eResource().getContents().remove(copy);
            }
            this.newRoots.add(copy);
        } else {
            // There is a container and this is the feature under which the copy should be placed
            // Get the parent, which is the input on the parent mapping ...
            final EObject parent = (EObject)this.resultObjectToSourceObject.get(newObject.eContainer());
            if (feature.isMany()) {
                final List values = (List)parent.eGet(feature);
                values.add(copy);
            } else {
                parent.eSet(feature, copy);
            }
        }
    }

    /**
     * Method the perform the 'change' operation defined by the supplied descriptor and mapping.
     * 
     * @param mapping
     * @param diffDesc
     */
    protected void doChange( final Mapping mapping,
                             final DifferenceDescriptor diffDesc ) {
        // There should both one input and one output ...
        final List inputs = mapping.getInputs();
        final EObject oldObject = (EObject)inputs.get(0);
        // final List outputs = mapping.getOutputs();
        // final EObject newObject = (EObject)outputs.get(0);
        if (computeTasks) {
            final String path = computeSubtaskPath(oldObject);
            final Object[] params = new Object[] {path};
            final String loadingSubTask = ModelerComparePlugin.Util.getString("MergeProcessorImpl.ChangingSubTask", params); //$NON-NLS-1$
            this.monitor.subTask(loadingSubTask);
        }
        // Go through all of the property differences ...
        final List propDiffs = diffDesc.getPropertyDifferences();
        final Iterator iter = propDiffs.iterator();
        while (iter.hasNext()) {
            final PropertyDifference propDiff = (PropertyDifference)iter.next();
            if (!propDiff.isSkip()) {
                final EStructuralFeature feature = propDiff.getAffectedFeature();
                final Object newValue = propDiff.getNewValue();
                final Object oldValue = propDiff.getOldValue();
                // Set the new (result) values onto the old (source) object
                if (feature.isMany()) {
                    final EList newValues = (EList)newValue;
                    final EList oldValues = (EList)oldValue;
                    final List newValuesInSource = convertFromResultsToSource(newValues);
                    ECollections.setEList(oldValues, newValuesInSource);
                } else {
                    if (newValue instanceof EObject) {
                        final EObject newValueInSource = (EObject)this.resultObjectToSourceObject.get(newValue);
                        // The new value may be null (e.g., if the new value is an object outside of the model
                        // being merged/updated); in such cases, use the original value ...
                        if (newValueInSource == null) {
                            oldObject.eSet(feature, newValue); // newValue is never null!
                        } else {
                            oldObject.eSet(feature, newValueInSource);
                        }
                    } else {
                        oldObject.eSet(feature, newValue);
                    }
                }
            }
        }
    }

    /**
     * Converts references to result objects into the reference to the associated object in the source
     * 
     * @param newValues
     * @return
     */
    protected List convertFromResultsToSource( final EList newValues ) {
        final List result = new ArrayList(newValues.size());
        final Iterator iter = newValues.iterator();
        while (iter.hasNext()) {
            final EObject newValue = (EObject)iter.next();
            final EObject newValueInSource = (EObject)this.resultObjectToSourceObject.get(newValue);
            if (newValueInSource != null) {
                result.add(newValueInSource);
            } else {
                result.add(newValue);
            }
        }
        return result;
    }

    /**
     * Method the perform the 'delete' operation defined by the supplied descriptor and mapping.
     * 
     * @param mapping
     * @param diffDesc
     */
    protected void doDelete( final Mapping mapping,
                             final DifferenceDescriptor diffDesc ) throws ModelerCoreException {
        // There should be no output ...
        final List inputs = mapping.getInputs();
        final EObject deletedObject = (EObject)inputs.get(0);
        if (computeTasks) {
            final String path = computeSubtaskPath(deletedObject);
            final Object[] params = new Object[] {path};
            final String loadingSubTask = ModelerComparePlugin.Util.getString("MergeProcessorImpl.RemovingSubTask", params); //$NON-NLS-1$
            this.monitor.subTask(loadingSubTask);
        }
        // Delete the supplied object ...
        this.editor.delete(deletedObject);
    }

    /**
     * Resolves in source objects references to objects in the results, and converts them.
     * 
     * @param mapping
     * @param diffDesc
     */
    protected void doResolve( final EObject sourceObject ) {
        // There should be no output ...
        final EClass metaclass = sourceObject.eClass();
        final List features = metaclass.getEAllReferences();
        final Iterator iter = features.iterator();
        while (iter.hasNext()) {
            final EReference ref = (EReference)iter.next();
            // final EReference oppositeRef = ref.getEOpposite();
            // Only want to process the non-containment references ...
            // if ( !ref.isContainment() || (oppositeRef != null && !oppositeRef.isContainment()) ) {
            if (!ref.isVolatile()) {
                if (ref.isMany()) {
                    final List values = (List)sourceObject.eGet(ref);
                    if (values.size() != 0) {
                        final List newValues = new ArrayList(values.size());
                        boolean foundNewValue = false;
                        for (int i = 0; i != values.size(); ++i) {
                            final Object value = values.get(i);
                            final Object sourceValue = this.resultObjectToSourceObject.get(value);
                            if (sourceValue != null) {
                                if (!newValues.contains(sourceValue)) {
                                    newValues.add(sourceValue);
                                    foundNewValue = true;
                                }
                            } else {
                                if (!newValues.contains(value)) {
                                    newValues.add(value);
                                }
                            }
                        }
                        if (foundNewValue) {
                            values.clear();
                            values.addAll(newValues);
                        }
                    }
                } else {
                    final Object value = sourceObject.eGet(ref);
                    final Object sourceValue = this.resultObjectToSourceObject.get(value);
                    if (sourceValue != null) {
                        sourceObject.eSet(ref, sourceValue);
                    }
                }
            }
        } // while
    }

    protected void recordMapping( final Object source,
                                  final Object result ) {
        this.resultObjectToSourceObject.put(result, source);
    }

    /**
     * Method the perform the 'delete' operation defined by the supplied descriptor and mapping.
     * 
     * @param mapping
     * @param diffDesc
     */
    protected void doOrderContained( final Mapping mapping,
                                     final DifferenceDescriptor diffDesc ) {
        // There should both one input and one output ...
        final List inputs = mapping.getInputs();
        final List outputs = mapping.getOutputs();
        if (inputs.isEmpty() || outputs.isEmpty()) {
            return;
        }
        final EObject sourceObject = (EObject)inputs.get(0);
        final EObject resultObject = (EObject)outputs.get(0);
        final EClass sourceMetaclass = sourceObject.eClass();

        // Go through all of the containment features of the input object,
        // and order the contained objects like in the output
        final Iterator iter = sourceMetaclass.getEAllContainments().iterator();
        while (iter.hasNext()) {
            final EStructuralFeature feature = (EStructuralFeature)iter.next();
            if (feature.isMany()) {
                // Only something to reorder if the feature is many-valued ...
                final List resultsValues = (List)resultObject.eGet(feature);
                final List sourceValuesFromResults = new ArrayList();
                final Iterator resultValuesIter = resultsValues.iterator();
                while (resultValuesIter.hasNext()) {
                    final Object resultValue = resultValuesIter.next();
                    final Object sourceValue = this.resultObjectToSourceObject.get(resultValue);
                    if (sourceValue != null) {
                        if (!sourceValuesFromResults.contains(sourceValue)) {
                            sourceValuesFromResults.add(sourceValue);
                        }
                    }
                }
                // Case 3364 - if a delete was unchecked in the differenceReport (meaning dont delete it),
                // it was getting lost here - essentially getting deleted because the 'undeleted' item was
                // not in the sourceValuesFromResults prototype list. Therefore simply adding it here.
                // This method is strictly for ordering - nothing should be lost...
                EList sourceValues = (EList)sourceObject.eGet(feature);
                Iterator sourceValueIter = sourceValues.iterator();
                while (sourceValueIter.hasNext()) {
                    Object obj = sourceValueIter.next();
                    if (!sourceValuesFromResults.contains(obj)) {
                        sourceValuesFromResults.add(obj);
                    }
                }
                ECollections.setEList(sourceValues, sourceValuesFromResults);
            }
        }
    }

    // =========================================================================
    // Nested utility classes
    // =========================================================================

    protected class PlanningVisitor implements ModelVisitor {
        int numMappings = 0;

        /**
         * @see com.metamatrix.modeler.core.util.ModelVisitor#visit(org.eclipse.emf.ecore.EObject)
         */
        public boolean visit( EObject object ) {
            if (object instanceof Mapping) {
                ++numMappings;
                final Mapping mapping = (Mapping)object;
                // Do this only for the non-root mappings
                if (mapping.getNestedIn() != null) {
                    // Find the source and result objects ...
                    final MappingHelper helper = mapping.getHelper();
                    if (helper != null && helper instanceof DifferenceDescriptor) {
                        final List inputs = mapping.getInputs();
                        final List outputs = mapping.getOutputs();
                        if (inputs.size() == 1 && outputs.size() == 1) {
                            final Object source = inputs.get(0);
                            final Object result = outputs.get(0);
                            MergeProcessorImpl.this.recordMapping(source, result);
                        }
                    }
                }
            }
            return true;
        }

        /**
         * @see com.metamatrix.modeler.core.util.ModelVisitor#visit(org.eclipse.emf.ecore.resource.Resource)
         */
        public boolean visit( Resource resource ) {
            return true;
        }

        public int getMappingCount() {
            return this.numMappings;
        }
    }

    protected class AddAndDeleteVisitor implements ModelVisitorWithFinish {
        /**
         * @see com.metamatrix.modeler.core.util.ModelVisitor#visit(org.eclipse.emf.ecore.EObject)
         */
        public boolean visit( EObject object ) throws ModelerCoreException {
            if (object instanceof Mapping) {
                MergeProcessorImpl.this.doProcess((Mapping)object, true, false, true);
                return true;
            }
            return true;
        }

        /**
         * @see com.metamatrix.modeler.core.util.ModelVisitor#visit(org.eclipse.emf.ecore.resource.Resource)
         */
        public boolean visit( Resource resource ) {
            return true;
        }

        /**
         * @see com.metamatrix.modeler.core.util.ModelVisitorWithFinish#postVisit(org.eclipse.emf.ecore.EObject)
         */
        public void postVisit( EObject object ) {
            if (object instanceof Mapping) {
                MergeProcessorImpl.this.doPostProcess((Mapping)object);
            }
        }
    }

    protected class ChangeVisitor implements ModelVisitor {
        /**
         * @see com.metamatrix.modeler.core.util.ModelVisitor#visit(org.eclipse.emf.ecore.EObject)
         */
        public boolean visit( EObject object ) throws ModelerCoreException {
            if (object instanceof Mapping) {
                MergeProcessorImpl.this.doProcess((Mapping)object, false, true, false);
                return true;
            }
            return true;
        }

        /**
         * @see com.metamatrix.modeler.core.util.ModelVisitor#visit(org.eclipse.emf.ecore.resource.Resource)
         */
        public boolean visit( Resource resource ) {
            return true;
        }
    }

    protected class ResolutionVisitor implements ModelVisitor {
        /**
         * @see com.metamatrix.modeler.core.util.ModelVisitor#visit(org.eclipse.emf.ecore.EObject)
         */
        public boolean visit( EObject object ) {
            MergeProcessorImpl.this.doResolve(object);
            return true;
        }

        /**
         * @see com.metamatrix.modeler.core.util.ModelVisitor#visit(org.eclipse.emf.ecore.resource.Resource)
         */
        public boolean visit( Resource resource ) {
            return true;
        }
    }

    protected class WorkInfo {
        protected final int numMappings;
        protected final int workPerMapping;
        protected final int workRemaining;

        protected WorkInfo( final int numMappings,
                            final int workForMerging ) {
            this.numMappings = numMappings;
            if (numMappings > 0) {
                this.workPerMapping = numMappings <= workForMerging ? workForMerging / numMappings : 0;
                this.workRemaining = workForMerging - (workPerMapping * numMappings);
            } else {
                this.workPerMapping = 0;
                this.workRemaining = workForMerging;
            }
        }
    }

}
