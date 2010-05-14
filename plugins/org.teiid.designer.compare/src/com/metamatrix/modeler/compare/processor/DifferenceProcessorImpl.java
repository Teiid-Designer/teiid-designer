/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.compare.processor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.emf.mapping.MappingFactory;
import org.eclipse.emf.mapping.MappingHelper;
import org.eclipse.emf.mapping.impl.MappingFactoryImpl;
import org.w3c.dom.Node;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.modeler.compare.CompareFactory;
import com.metamatrix.modeler.compare.DifferenceDescriptor;
import com.metamatrix.modeler.compare.DifferenceGuidelines;
import com.metamatrix.modeler.compare.DifferenceProcessor;
import com.metamatrix.modeler.compare.DifferenceReport;
import com.metamatrix.modeler.compare.DifferenceType;
import com.metamatrix.modeler.compare.ModelerComparePlugin;
import com.metamatrix.modeler.compare.PropertyDifference;
import com.metamatrix.modeler.compare.impl.CompareFactoryImpl;
import com.metamatrix.modeler.compare.selector.ModelSelector;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.compare.EObjectMatcher;
import com.metamatrix.modeler.core.compare.EObjectMatcherCache;
import com.metamatrix.modeler.core.compare.EObjectMatcherFactory;
import com.metamatrix.modeler.core.compare.MappingProducer;

/**
 * DifferenceProcessorImpl
 */
public class DifferenceProcessorImpl implements DifferenceProcessor {

    private static final String PLUGINID = ModelerComparePlugin.PLUGIN_ID;

    // -------------------------------------------------------------------------
    // MESSAGE CODE CONSTANTS
    // -------------------------------------------------------------------------
    protected static final int PROCESSOR_ALREADY_CLOSED = 50001;
    protected static final int ERROR_LOADING_RESOURCES = 50002;
    protected static final int ERROR_CREATING_MAPPING = 50003;
    protected static final int ERROR_COMPUTING_DIFFERENCES = 50004;
    protected static final int MISSING_MAPPING_ADAPTERS = 50005;
    protected static final int NO_PROBLEMS = 50010;
    protected static final int HAS_WARNINGS = 50011;
    protected static final int HAS_ERRORS = 50012;
    protected static final int HAS_WARNINGS_AND_ERRORS = 50013;
    protected static final int NO_WARNINGS_AND_ERRORS = 50014;

    // -------------------------------------------------------------------------
    // PROGRESS MONITOR CONSTANTS
    // -------------------------------------------------------------------------
    protected static final int AMOUNT_OF_WORK_FOR_LOADING_RESOURCES = 300;
    protected static final int AMOUNT_OF_WORK_FOR_MAPPING_RESOURCES = 1000;
    protected static final int AMOUNT_OF_WORK_FOR_COMUTING_DIFFERENCES = 400;
    protected static final int AMOUNT_OF_WORK_FOR_CREATING_RESULTS = 100;
    protected static final Collection DOM_FEATURE_NAMES = new ArrayList();
    {
        DOM_FEATURE_NAMES.add("description"); //$NON-NLS-1$
        DOM_FEATURE_NAMES.add("applicationInformation"); //$NON-NLS-1$
        DOM_FEATURE_NAMES.add("userInformation"); //$NON-NLS-1$
    }

    private final ModelSelector beforeSelector;
    private final ModelSelector afterSelector;
    private CompareFactory compareFactory;
    private MappingFactory mappingFactory;
    private DifferenceReport report;
    private boolean closed;
    private final List matcherFactories;
    private final HashMap inputsToObjects;
    private int totalAdditions;
    private int totalDeletions;
    private int totalChanges;
    private DifferenceGuidelines guidelines;
    private MappingProducer mappingProducer;
    private boolean isMultiModel;

    /**
     * Construct a DifferenceProcessor for a single model. This form of the constructor is used to signal that there are no
     * differences to be computed, but that a difference report should be generated when asked for. This is called by the
     * {@link com.metamatrix.modeler.compare.ModelerComparePlugin#createDifferenceProcessor(ModelResource)} method when the
     * resource has no unsaved changes (i.e., there are no differences}.
     * 
     * @param resource the resource; may not be null
     */
    public DifferenceProcessorImpl( final ModelSelector selector ) {
        this(selector, selector);
    }

    /**
     * Construct a DifferenceProcessor that computes the differences between two models.
     * <p>
     * This processor computes the difference between the two models as if recording the actions necessary to change the
     * <i>before</i> model into the <i>after</i> model.
     * </p>
     * 
     * @param beforeSelector the ModelSelector to the model considered the <i>before</i> state; may not be null
     * @param afterSelector the ModelSelector to the model considered the <i>after</i> state; may not be null
     */
    public DifferenceProcessorImpl( final ModelSelector beforeSelector,
                                    final ModelSelector afterSelector ) {
        this(beforeSelector, afterSelector, null);
    }

    /**
     * Construct a DifferenceProcessor that computes the differences between two models.
     * <p>
     * This processor computes the difference between the two models as if recording the actions necessary to change the
     * <i>before</i> model into the <i>after</i> model.
     * </p>
     * 
     * @param beforeSelector the ModelSelector to the model considered the <i>before</i> state; may not be null
     * @param afterSelector the ModelSelector to the model considered the <i>after</i> state; may not be null
     * @param mappings = Any mappings from a previous differencing that may be required for this comparison.
     */
    public DifferenceProcessorImpl( final ModelSelector beforeSelector,
                                    final ModelSelector afterSelector,
                                    final HashMap mappings ) {
        super();
        CoreArgCheck.isNotNull(beforeSelector);
        CoreArgCheck.isNotNull(afterSelector);
        this.beforeSelector = beforeSelector;
        this.afterSelector = afterSelector;
        this.closed = false;
        this.compareFactory = new CompareFactoryImpl();
        this.mappingFactory = new MappingFactoryImpl();
        this.matcherFactories = new ArrayList();
        this.guidelines = NullDifferenceGuidelines.INSTANCE;
        if (mappings == null) {
            this.inputsToObjects = new HashMap();
        } else {
            this.inputsToObjects = mappings;
            this.isMultiModel = true;
        }
    }

    /**
     * @see com.metamatrix.modeler.compare.DifferenceProcessor#getDifferenceGuidelines()
     */
    public DifferenceGuidelines getDifferenceGuidelines() {
        return this.guidelines;
    }

    /**
     * @see com.metamatrix.modeler.compare.DifferenceProcessor#setDifferenceGuidelines(com.metamatrix.modeler.compare.DifferenceGuidelines)
     */
    public void setDifferenceGuidelines( final DifferenceGuidelines guidelines ) {
        this.guidelines = guidelines != null ? guidelines : NullDifferenceGuidelines.INSTANCE;
    }

    /**
     * @see com.metamatrix.modeler.compare.DifferenceProcessor#execute(org.eclipse.core.runtime.IProgressMonitor)
     */
    public IStatus execute( final IProgressMonitor progressMonitor ) {
        if (closed) {
            final int code = PROCESSOR_ALREADY_CLOSED;
            final String msg = ModelerComparePlugin.Util.getString("DifferenceProcessorImpl.The_processor_has_already_been_closed_and_cannot_be_reused"); //$NON-NLS-1$
            final IStatus status = new Status(IStatus.ERROR, PLUGINID, code, msg, null);
            return status;
        }

        // -------------------------------------------------------------------------
        // Phase 0: Set up the progress monitor ...
        // -------------------------------------------------------------------------
        final IProgressMonitor monitor = progressMonitor != null ? progressMonitor : new NullProgressMonitor();
        final Object[] taskParams = new Object[] {this.beforeSelector.getLabel(), this.afterSelector.getLabel()};
        final String taskName = ModelerComparePlugin.Util.getString("DifferenceProcessorImpl.execute_task_name", taskParams); //$NON-NLS-1$
        final int totalWork = AMOUNT_OF_WORK_FOR_LOADING_RESOURCES + AMOUNT_OF_WORK_FOR_MAPPING_RESOURCES
                              + AMOUNT_OF_WORK_FOR_COMUTING_DIFFERENCES + AMOUNT_OF_WORK_FOR_CREATING_RESULTS;
        monitor.beginTask(taskName, totalWork);

        // Create the problems list ...
        final List problems = new LinkedList();

        // -------------------------------------------------------------------------
        // Phase 1: Create the report ...
        // -------------------------------------------------------------------------
        try {
            this.report = createDifferenceReport();
        } finally {
            monitor.worked(AMOUNT_OF_WORK_FOR_CREATING_RESULTS);
        }

        // -------------------------------------------------------------------------
        // Phase 2: Load the resources and get the root objects in each resource ...
        // -------------------------------------------------------------------------
        List beforeRoots = null;
        List afterRoots = null;
        ModelSelector selector = this.beforeSelector;
        try {
            // Create a subtask ...
            final String loadingSubTask = ModelerComparePlugin.Util.getString("DifferenceProcessorImpl.Loading_resources_subtask_name"); //$NON-NLS-1$
            monitor.subTask(loadingSubTask);

            // Open the model selectors and get their contents ...
            selector.open();
            beforeRoots = selector.getRootObjects();
            selector = this.afterSelector;
            selector.open();
            afterRoots = selector.getRootObjects();
        } catch (ModelerCoreException e) {
            final Object[] params = new Object[] {selector.getLabel(), e.getMessage()};
            final String msg = ModelerComparePlugin.Util.getString("DifferenceProcessorImpl.Error_loading_resource", params); //$NON-NLS-1$
            final IStatus status = newError(ERROR_LOADING_RESOURCES, msg, e);
            problems.add(status);
        } finally {
            monitor.worked(AMOUNT_OF_WORK_FOR_LOADING_RESOURCES);
        }

        // -------------------------------------------------------------------------
        // Phase 3: Map the input model and the output model ...
        // -------------------------------------------------------------------------
        try {
            // Create a subtask ...
            final String analysisSubTask = ModelerComparePlugin.Util.getString("DifferenceProcessorImpl.Performing_mapping"); //$NON-NLS-1$
            monitor.subTask(analysisSubTask);

            if (beforeRoots != null && afterRoots != null) {
                final Mapping mapping = createMapping(beforeRoots, afterRoots, problems, monitor, analysisSubTask);
                this.report.setMapping(mapping);
            }
        } catch (Throwable e) {
            final Object[] params = new Object[] {e.getMessage()};
            final String msg = ModelerComparePlugin.Util.getString("DifferenceProcessorImpl.Error_performing_mapping", params); //$NON-NLS-1$
            final IStatus status = newError(ERROR_CREATING_MAPPING, msg, e);
            problems.add(status);
        } finally {
            monitor.worked(AMOUNT_OF_WORK_FOR_MAPPING_RESOURCES);
        }

        // -------------------------------------------------------------------------
        // Phase 4: Add the difference information to the mapping ...
        // -------------------------------------------------------------------------
        try {
            if (this.report.getMapping() != null) {
                this.totalAdditions = 0;
                this.totalChanges = 0;
                this.totalDeletions = 0;

                // Create a subtask ...
                final String analysisSubTask = ModelerComparePlugin.Util.getString("DifferenceProcessorImpl.Computing_differences"); //$NON-NLS-1$
                monitor.subTask(analysisSubTask);
                final Mapping rootMapping = this.report.getMapping();
                if (rootMapping != null) {
                    addDifferencesInTransaction(rootMapping, monitor, analysisSubTask);
                }

                this.report.setTotalAdditions(this.totalAdditions);
                this.report.setTotalChanges(this.totalChanges);
                this.report.setTotalDeletions(this.totalDeletions);
            }
        } catch (Throwable e) {
            final Object[] params = new Object[] {e.getMessage()};
            final String msg = ModelerComparePlugin.Util.getString("DifferenceProcessorImpl.Error_computing_differences", params); //$NON-NLS-1$
            final IStatus status = newError(ERROR_COMPUTING_DIFFERENCES, msg, e);
            problems.add(status);
        } finally {
            if (!this.isMultiModel) {
                this.inputsToObjects.clear();
            }
            this.totalAdditions = 0;
            this.totalChanges = 0;
            this.totalDeletions = 0;
            monitor.worked(AMOUNT_OF_WORK_FOR_COMUTING_DIFFERENCES);
        }

        IStatus resultStatus = null;
        // -------------------------------------------------------------------------
        // Phase 5: Analyze any problems ...
        // -------------------------------------------------------------------------
        try {
            // Create a subtask ...
            final String analysisSubTask = ModelerComparePlugin.Util.getString("DifferenceProcessorImpl.Analyzing_problems"); //$NON-NLS-1$
            monitor.subTask(analysisSubTask);

            // Put all of the problems into a single IStatus ...
            if (problems.isEmpty()) {
                final String msg = ModelerComparePlugin.Util.getString("DifferenceProcessorImpl.Execution_completed"); //$NON-NLS-1$
                final IStatus status = new Status(IStatus.OK, PLUGINID, NO_PROBLEMS, msg, null);
                resultStatus = status;
            } else if (problems.size() == 1) {
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
                final IStatus[] statusArray = (IStatus[])problems.toArray(new IStatus[problems.size()]);
                if (numWarnings != 0 && numErrors == 0) {
                    final Object[] params = new Object[] {new Integer(numWarnings)};
                    final String msg = ModelerComparePlugin.Util.getString("DifferenceProcessorImpl.Execution_resulted_in_warnings", params); //$NON-NLS-1$
                    resultStatus = new MultiStatus(PLUGINID, HAS_WARNINGS, statusArray, msg, null);
                } else if (numWarnings == 0 && numErrors != 0) {
                    final Object[] params = new Object[] {new Integer(numErrors)};
                    final String msg = ModelerComparePlugin.Util.getString("DifferenceProcessorImpl.Execution_resulted_in_errors", params); //$NON-NLS-1$
                    resultStatus = new MultiStatus(PLUGINID, HAS_ERRORS, statusArray, msg, null);
                } else if (numWarnings != 0 && numErrors != 0) {
                    final Object[] params = new Object[] {new Integer(numWarnings), new Integer(numErrors)};
                    final String msg = ModelerComparePlugin.Util.getString("DifferenceProcessorImpl.Execution_resulted_in_warnings_and_errors", params); //$NON-NLS-1$
                    resultStatus = new MultiStatus(PLUGINID, HAS_WARNINGS_AND_ERRORS, statusArray, msg, null);
                } else {
                    final String msg = ModelerComparePlugin.Util.getString("DifferenceProcessorImpl.Execution_completed_with_no_warnings_or_errors"); //$NON-NLS-1$
                    resultStatus = new MultiStatus(PLUGINID, NO_WARNINGS_AND_ERRORS, statusArray, msg, null);
                }
            }

        } finally {
            monitor.worked(AMOUNT_OF_WORK_FOR_CREATING_RESULTS);
        }
        return resultStatus;
    }

    private void addDifferencesInTransaction( Mapping rootMapping,
                                              IProgressMonitor monitor,
                                              String analysisSubTask ) {
        // Let's wrap in transaction and make non-undoable....
        boolean started = ModelerCore.startTxn(false, false, "Add Differences", this); //$NON-NLS-1$
        boolean succeeded = false;

        try {
            addDifferences(rootMapping, monitor, analysisSubTask);
            succeeded = true;
        } catch (Exception ex) {
            ModelerCore.Util.log(IStatus.ERROR, ex, ex.getMessage());
        } finally {
            if (started) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    // We don't want to roll this back. Not really changing anything in any model
                    // ModelerCore.rollbackTxn();
                }
            }
        }
    }

    /**
     * @see com.metamatrix.modeler.compare.DifferenceProcessor#getDifferenceReport()
     */
    public DifferenceReport getDifferenceReport() {
        return this.report;
    }

    /**
     * @see com.metamatrix.modeler.compare.DifferenceProcessor#close()
     */
    public void close() {
        if (this.closed) {
            return;
        }
        this.report = null;
        // Close the selectors ...
        if (this.beforeSelector != null) {
            this.beforeSelector.close();
        }
        if (this.afterSelector != null) {
            this.afterSelector.close();
        }
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
     * Create the mapping between the supplied model objects.
     * 
     * @param beforeSiblings the siblings in the <i>before</i> state; never null
     * @param afterSiblings the siblings in the <i>afterSiblings</i> state; never null
     * @param monitor the progress monitor; never null
     * @param subtaskPrefix the string for subtasks that can be used to prefix the names of the objects being compared
     */
    protected Mapping createMapping( final List beforeSiblings,
                                     final List afterSiblings,
                                     final List problems,
                                     final IProgressMonitor monitor,
                                     final String subtaskPrefix ) {

        // Create a composite mapping adapter ...
        this.mappingProducer = new MappingProducer(this.inputsToObjects);
        final EObjectMatcherCache cache = mappingProducer.getEObjectMatcherCache();

        // Add all of the mapping adapters referenced by this object ...
        cache.addEObjectMatcherFactories(this.matcherFactories);

        // Let the subclasses add any they need ...
        doAddMappingAdapters(cache);

        // Ensure there is at least one adapter ...
        if (cache.getEObjectMatcherFactories().isEmpty()) {
            final String msg = ModelerComparePlugin.Util.getString("DifferenceProcessorImpl.No_mapping_adapters"); //$NON-NLS-1$
            final IStatus status = newError(MISSING_MAPPING_ADAPTERS, msg, null);
            problems.add(status);
            return null;
        }

        // Perform the mapping ...
        final MappingFactory factory = this.getMappingFactory();
        final boolean recursive = true;
        final Mapping mapping = mappingProducer.createMappings(beforeSiblings, afterSiblings, recursive, factory, monitor);

        // Create the DifferenceDescriptor for the root mapping
        // (this really doesn't mean anything, but is there primarily so that every Mapping has a
        // DifferenceDescriptor helper object)
        final DifferenceDescriptor rootDesc = this.getCompareFactory().createDifferenceDescriptor();
        mapping.setHelper(rootDesc);
        rootDesc.setType(DifferenceType.CHANGE_BELOW_LITERAL);

        // Get the mapping of inputs to outputs ...
        return mapping;
    }

    protected void addDifferences( final Mapping mapping,
                                   final IProgressMonitor monitor,
                                   final String subtaskPrefix ) {
        final List mappingsToRemove = new LinkedList();
        final Iterator iter = mapping.getNested().iterator();
        while (iter.hasNext()) {
            final Mapping nestedMapping = (Mapping)iter.next();
            if (this.includeInReport(nestedMapping)) {
                // Get the inputs and outputs
                final List inputs = nestedMapping.getInputs();
                final List outputs = nestedMapping.getOutputs();
                if (inputs.isEmpty()) {
                    // There are no inputs ...
                    if (!outputs.isEmpty()) {
                        // But there are outputs, so this is an addition
                        setDescriptor(nestedMapping, DifferenceType.ADDITION_LITERAL);
                        ++this.totalAdditions;
                    }
                    // Otherwise there are no outputs either; do nothing
                } else {
                    // There are inputs ...
                    if (outputs.isEmpty()) {
                        // There are no outputs, so this is a deletion ...
                        setDescriptor(nestedMapping, DifferenceType.DELETION_LITERAL);
                        ++this.totalDeletions;
                    } else {
                        // There are outputs, so compute the differences ...
                        final DifferenceDescriptor desc = setDescriptor(nestedMapping, DifferenceType.NO_CHANGE_LITERAL);
                        final boolean changed = computeFeatureDifferences(desc, inputs, outputs);
                        if (changed) {
                            desc.setType(DifferenceType.CHANGE_LITERAL);
                            ++this.totalChanges;
                        }
                    }
                }

                addDifferences(nestedMapping, monitor, subtaskPrefix);
                DifferenceDescriptor descriptor = (DifferenceDescriptor)mapping.getHelper();
                if (((DifferenceDescriptor)nestedMapping.getHelper()).getType() != DifferenceType.NO_CHANGE_LITERAL
                    && descriptor.getType() == DifferenceType.NO_CHANGE_LITERAL) {
                    descriptor.setType(DifferenceType.CHANGE_BELOW_LITERAL);
                }
            } else {
                // Prune it from the result
                mappingsToRemove.add(nestedMapping);
            }
        }

        // Remove any mappings that should not have been there ...
        final Iterator iterator = mappingsToRemove.iterator();
        while (iterator.hasNext()) {
            final Mapping nestedMapping = (Mapping)iterator.next();
            nestedMapping.setNestedIn(null); // remove from parent
        }
    }

    /**
     * @param nestedMapping
     * @return
     */
    protected boolean includeInReport( final Mapping nestedMapping ) {
        // Get the inputs and outputs
        final List inputs = nestedMapping.getInputs();
        final List outputs = nestedMapping.getOutputs();
        List objects = null;
        if (outputs != null && outputs.size() != 0) {
            objects = outputs;
        }
        if (inputs != null && inputs.size() != 0) {
            objects = inputs;
        }
        if (objects != null) {
            final Iterator iter = objects.iterator();
            while (iter.hasNext()) {
                final EObject obj = (EObject)iter.next();
                if (!includeInReport(obj)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    protected boolean includeInReport( final EObject eobject ) {
        final EClass eclass = eobject.eClass();
        final EPackage epkg = eclass.getEPackage();
        if (!this.guidelines.includeMetamodel(epkg.getNsURI())) {
            return false;
        }
        if (!this.guidelines.includeMetaclass(eclass)) {
            return false;
        }
        return true;
    }

    protected boolean includeInReport( final EStructuralFeature feature ) {
        // Skip the feature if not changeable ...
        if (!feature.isChangeable()) {
            // There is no point in recording differences on a feature we can't change (must be changeable some other way)
            return false;
        }
        if (!this.guidelines.includeFeature(feature)) {
            return false;
        }
        return true;
    }

    protected DifferenceDescriptor setDescriptor( final Mapping mapping,
                                                  final DifferenceType type ) {
        // Find a helper that is a DifferenceDescriptor ...
        final MappingHelper currentHelper = mapping.getHelper();
        DifferenceDescriptor desc = null;
        if (currentHelper == null) {
            // This should be the case almost all of the time!
            desc = this.getCompareFactory().createDifferenceDescriptor();
            desc.setType(type);
            mapping.setHelper(desc);
        } else {
            // There is already a helper ...
            if (currentHelper instanceof DifferenceDescriptor) {
                desc = (DifferenceDescriptor)currentHelper;
                if (desc.getType().getValue() == DifferenceType.CHANGE) {
                    // Remove any existing property differences ...
                    desc.getPropertyDifferences().clear();
                }
                desc.setType(type);
            } else {
                // Find a child helper ...
                final Iterator iter = currentHelper.getNested().iterator();
                while (iter.hasNext()) {
                    final MappingHelper childHelper = (MappingHelper)iter.next();
                    if (childHelper instanceof DifferenceDescriptor) {
                        desc = (DifferenceDescriptor)currentHelper;
                    }
                }
                if (desc == null) {
                    // We didn't find an existing child helper, so create one ...
                    desc = this.getCompareFactory().createDifferenceDescriptor();
                    desc.setType(type);
                    currentHelper.getNested().add(desc);
                }
            }
        }
        // At this point, we should always have a non-null reference
        return desc;
    }

    protected boolean computeFeatureDifferences( final DifferenceDescriptor descriptor,
                                                 final List inputs,
                                                 final List outputs ) {
        boolean changed = false;
        if (inputs.size() == 1 && outputs.size() == 1) {
            final EObject input = (EObject)inputs.get(0);
            final EObject output = (EObject)outputs.get(0);
            changed = computeFeatureDifferences(descriptor, input, output);
        }
        return changed;
    }

    protected boolean computeFeatureDifferences( final DifferenceDescriptor descriptor,
                                                 final EObject input,
                                                 final EObject output ) {
        final EClass eclass = input.eClass();
        boolean changed = false;
        // Assume that if we are computing feature differences, that we're guaranteed the
        // input's EClass and output's EClass are identical
        final List features = eclass.getEAllStructuralFeatures();
        final Iterator iter = features.iterator();
        while (iter.hasNext()) {
            final EStructuralFeature feature = (EStructuralFeature)iter.next();
            if (this.includeInReport(feature)) {
                // Look at only attributes and non-containment references ...
                if (feature instanceof EAttribute
                    || (!((EReference)feature).isContainment() && !((EReference)feature).isContainer())) {
                    final Object inputValue = input.eGet(feature);
                    final Object outputValue = output.eGet(feature);
                    final boolean sameValue = feature.isMany() ? isEqual(feature, (EList)inputValue, (EList)outputValue) : isEqual(feature,
                                                                                                                                   inputValue,
                                                                                                                                   outputValue);
                    if (!sameValue) {
                        final PropertyDifference diff = this.getCompareFactory().createPropertyDifference();
                        diff.setAffectedFeature(feature);
                        diff.setDescriptor(descriptor);
                        diff.setNewValue(outputValue);
                        diff.setOldValue(inputValue);
                        changed = true;
                    }
                }
            }
        }
        return changed;
    }

    /**
     * @param object
     * @param object2
     */
    protected boolean isEqual( final EStructuralFeature feature,
                               final Object object1,
                               final Object object2 ) {
        if (object1 == null) {
            if (object2 == null) {
                // Both are null
                return true;
            }
            // object1 is null, object2 is NOT null
            return false;
        }
        // object1 is NOT null
        if (object2 == null) {
            // object1 is NOT null, object2 is null
            return false;
        }
        // both are NOT null

        if (feature instanceof EReference) {
            final EObject expectedOutput = (EObject)this.inputsToObjects.get(object1);
            if (!object2.equals(expectedOutput)) {
                // The instances don't match to the inputToObjects map, so
                // before saying they are different, check their URI's. If the URIs are the
                // same, then the references both point to logically the same object that is
                // external to the domain of objects being compared
                final URI uri1 = EcoreUtil.getURI((EObject)object1);
                final URI uri2 = EcoreUtil.getURI((EObject)object2);
                if (uri1 != null && uri2 != null) {
                    if (uri1.equals(uri2)) {
                        return true;
                    }
                }
                // The URIs are not the same, so use the matchers ...
                if (this.mappingProducer != null) {
                    final EReference reference = (EReference)feature;
                    final List object1List = new ArrayList(1);
                    final List object2List = new ArrayList(1);
                    object1List.add(object1);
                    object2List.add(object2);
                    final List matchers = this.mappingProducer.getEObjectMatcherCache().getEObjectMatchers(reference);
                    final Mapping temp = this.mappingFactory.createMapping();
                    final Iterator iter = matchers.iterator();
                    while (iter.hasNext()) {
                        final EObjectMatcher matcher = (EObjectMatcher)iter.next();
                        matcher.addMappings(reference, object1List, object2List, temp, this.mappingFactory);
                        if (object1List.isEmpty()) {
                            return true; // matcher thinks they are equivalent ...
                        }
                    }
                }

                // The matchers didn't find matches, so consider them different objects
                return false;

            }
            return true;
        } else if (object1 instanceof Node && object2 instanceof Node) {
            if (DOM_FEATURE_NAMES.contains(feature.getName())) {
                final String txt1 = getChildText((Node)object1);
                final String txt2 = getChildText((Node)object2);

                final boolean txtMatch = txt1 == null ? txt2 == null : txt1.equals(txt2);

                return (txtMatch);
            }
            return true;
        }
        return object1.equals(object2);
    }

    /**
     * @param list
     * @param list2
     */
    protected boolean isEqual( final EStructuralFeature feature,
                               final EList list1,
                               final EList list2 ) {
        final int list1Size = list1.size();
        final int list2Size = list2.size();

        // Compare the sizes ...
        if (list1Size == 0 && list2Size == 0) {
            // Both are empty, so they are equal
            return true;
        }
        // At least one list has something in it ...
        if (list1Size != list2Size) {
            // They are different sizes, so they are different ...
            return false;
        }
        // The sizes are the same, so check the contents ...
        final Iterator iter1 = list1.iterator();
        final Iterator iter2 = list2.iterator();
        while (iter2.hasNext()) {
            final Object obj1 = iter1.next();
            final Object obj2 = iter2.next();
            final boolean valuesAreEqual = isEqual(feature, obj1, obj2);
            if (!valuesAreEqual) {
                return false;
            }
        }
        return true;
    }

    private String getChildText( final Node node ) {

        // is there anything to do?
        if (node == null) {
            return null;
        }

        // concatenate children text
        StringBuffer str = new StringBuffer();
        Node child = node.getFirstChild();
        while (child != null) {
            short type = child.getNodeType();
            if (type == Node.TEXT_NODE) {
                str.append(child.getNodeValue());
            } else if (type == Node.CDATA_SECTION_NODE) {
                str.append(getChildText(child));
            }
            child = child.getNextSibling();
        }

        // return text value
        return str.toString();

    }

    /**
     * @return
     */
    protected DifferenceReport createDifferenceReport() {
        final DifferenceReport reportObj = this.getCompareFactory().createDifferenceReport();
        final long now = System.currentTimeMillis();
        final String sourceUri = this.beforeSelector.getUri().toString();
        final String resultUri = this.afterSelector.getUri().toString();
        reportObj.setAnalysisTime(now);
        reportObj.setSourceUri(sourceUri);
        reportObj.setResultUri(resultUri);
        // By default, set the title to the label of the input ...
        final String label = this.beforeSelector.getLabel();
        if (label != null && label.trim().length() != 0) {
            reportObj.setTitle(label);
        }
        return reportObj;
    }

    /**
     * @return
     */
    public CompareFactory getCompareFactory() {
        return compareFactory;
    }

    /**
     * @return
     */
    public MappingFactory getMappingFactory() {
        return mappingFactory;
    }

    /**
     * @param factory
     */
    public void setCompareFactory( CompareFactory factory ) {
        compareFactory = factory;
    }

    /**
     * @param factory
     */
    public void setMappingFactory( MappingFactory factory ) {
        mappingFactory = factory;
    }

    /**
     * Return the list of {@link EObjectMappingAdapter} instances that will be used by this processor during
     * {@link #execute(IProgressMonitor) execution}.
     * 
     * @return the mapping adapters; never null
     * @see #addMappingAdapters(List)
     */
    public List getEObjectMatcherFactories() {
        return this.matcherFactories;
    }

    /**
     * Helper method to add to the list of {@link EObjectMappingAdapter} instances that will be used by this processor during
     * {@link #execute(IProgressMonitor) execution}.
     * 
     * @param adapters the new mapping adapters; may not be null
     * @see #getMappingAdapters()
     */
    public synchronized void addEObjectMatcherFactories( final List factories ) {
        final Iterator iter = factories.iterator();
        while (iter.hasNext()) {
            final Object obj = iter.next();
            if (obj instanceof EObjectMatcherFactory) {
                final EObjectMatcherFactory factory = (EObjectMatcherFactory)obj;
                if (!this.matcherFactories.contains(factory)) {
                    this.matcherFactories.add(factory);
                }
            }
        }
    }

    /**
     * Method called to create any {@link EObjectMappingAdapter} instances and add them to those already in the list.
     */
    protected void doAddMappingAdapters( final EObjectMatcherCache cache ) {
    }

    /**
     * @return
     */
    public ModelSelector getAfterSelector() {
        return afterSelector;
    }

    /**
     * @return
     */
    public ModelSelector getBeforeSelector() {
        return beforeSelector;
    }

    /**
     * @return Returns the isMultiModel.
     */
    public boolean isMultiModel() {
        return this.isMultiModel;
    }

}
