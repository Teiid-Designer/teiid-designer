/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.compare.generator;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.core.designer.ModelerCoreRuntimeException;
import org.teiid.core.designer.UserCancelledException;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.compare.DifferenceProcessor;
import org.teiid.designer.compare.DifferenceReport;
import org.teiid.designer.compare.ModelProducer;
import org.teiid.designer.compare.ModelerComparePlugin;
import org.teiid.designer.compare.processor.DifferenceProcessorImpl;
import org.teiid.designer.compare.processor.MergeProcessorImpl;
import org.teiid.designer.compare.selector.ModelResourceSelector;
import org.teiid.designer.compare.selector.ModelSelector;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.TransactionRunnable;
import org.teiid.designer.core.compare.EObjectMatcherFactory;
import org.teiid.designer.core.transaction.UnitOfWork;
import org.teiid.designer.core.workspace.ModelWorkspaceException;

/**
 * @since 8.0
 */
public class BasicModelGenerator extends AbstractModelGenerator {

    private static final int UNITS_GENERATION = 10000;
    private static final int UNITS_DIFFERENCE = 100;
    private static final int UNITS_DIFFERENCE_POST_PROCESS = 100;
    private static final int UNITS_MERGE = 1000;

    public static final boolean DEFAULT_USE_TRANSACTION_DURING_MERGE = true;

    private final ModelSelector original;
    final ModelProducer producer;
    private MergeProcessorImpl mergeProcessor;
    private boolean useTransactionDuringMerge = DEFAULT_USE_TRANSACTION_DURING_MERGE;

    /**
     * Construct an instance of ModelGenerator.
     * 
     * @param output the ModelSelector where the <code>outputProducer</code> will put the generated objects; may not be null
     * @param original the ModelSelector defining the original model into which the <code>output</code> objects are to be merged;
     *        may not be null
     * @param outputProducer the component that produces the <code>output</code> contents; may not be null
     * @param mappingAdapters the list of {@link EObjectMatcherFactory mapping adapter} instances
     *        used during the difference processing; may not be null or empty
     * @throws IllegalArgumentException if any of the above requirements are not satisfied, or if the <code>output</code> and
     *         <code>original</code> objects are the same object
     */
    public BasicModelGenerator( final ModelSelector original,
                                final ModelProducer outputProducer,
                                final List mappingAdapters ) {
        super(mappingAdapters);
        CoreArgCheck.isNotNull(original);
        CoreArgCheck.isNotNull(outputProducer);
        this.original = original;
        this.producer = outputProducer;
    }

    /**
     * Close any resources that were opened by this generator. This method should be called when finished with this object.
     */
    @Override
    public void close() {
        super.close();
        this.producer.getOutputSelector().close();
    }

    public ModelSelector getOriginalModelSelector() {
        return this.original;
    }

    /**
     * Execute the generator. This method invokes the producer to populate the output model, performs a difference analysis
     * between the output and original models, and merges all changes so that the original is a mirror of the output.
     * 
     * @param monitor the progress monitor; may be null
     * @param problems the list into which {@link IStatus} problems should be placed
     */
    @Override
    protected void doGenerateOutput( final IProgressMonitor monitor,
                                     final LinkedList problems ) throws UserCancelledException {
        CoreArgCheck.isNotNull(monitor);

        // Set up the progress monitor
        final Object[] taskParams = new Object[] {this.getDescription()};
        final String taskName = ModelerComparePlugin.Util.getString("ModelGenerator.Performing_{0}", taskParams); //$NON-NLS-1$
        final int numUnits = UNITS_GENERATION;
        monitor.beginTask(null, numUnits);
        monitor.setTaskName(taskName);

        // First check if cancelled
        if (monitor.isCanceled()) {
            throw new UserCancelledException();
        }

        // -------------------------------------------------------------
        // Populate the output model ...
        // -------------------------------------------------------------
        try {
            // Open the model selector ...
            this.producer.getOutputSelector().open();

            final TransactionRunnable op = new TransactionRunnable() {
                @Override
				public Object run( UnitOfWork uow ) throws ModelerCoreException {
                    try {
                        producer.execute(monitor, problems);
                    } catch (Exception e) {
                        throw new WrappedModelerCoreException(e);
                    }
                    return null;
                }
            };

            // Perform the runnable ...
            if (this.useTransactionDuringMerge) {
                final boolean significant = true;
                final String desc = this.getDescription();
                ModelerCore.getModelEditor().executeAsTransaction(op, desc, significant, this);
            } else {
                op.run(null);
            }
            // Record work performed ...
            monitor.worked(UNITS_GENERATION);
        } catch (WrappedModelerCoreException we) {
            final Throwable e = we.getException();
            if (e instanceof ModelerCoreRuntimeException
                && ((ModelerCoreRuntimeException)e).getChild() instanceof UserCancelledException) {
                final UserCancelledException cancellation = (UserCancelledException)((ModelerCoreRuntimeException)e).getChild();
                throw cancellation;
                // do nothing - the user cancelled
            }
            final Object[] params = new Object[] {this.getDescription(), e.getLocalizedMessage()};
            final String msg = ModelerComparePlugin.Util.getString("ModelGenerator.Error_during_generation", params); //$NON-NLS-1$
            final String pluginId = ModelerComparePlugin.PLUGIN_ID;
            final int code = ERROR_PRODUCING_OUTPUT_MODEL;
            final IStatus status = new Status(IStatus.ERROR, pluginId, code, msg, e);
            problems.addFirst(status);
        } catch (ModelerCoreRuntimeException e) {
            if (e.getChild() instanceof UserCancelledException) {
                // do nothing - the user cancelled
            } else {
                final Object[] params = new Object[] {this.getDescription(), e.getLocalizedMessage()};
                final String msg = ModelerComparePlugin.Util.getString("ModelGenerator.Error_during_generation", params); //$NON-NLS-1$
                final String pluginId = ModelerComparePlugin.PLUGIN_ID;
                final int code = ERROR_PRODUCING_OUTPUT_MODEL;
                final IStatus status = new Status(IStatus.ERROR, pluginId, code, msg, e);
                problems.addFirst(status);
            }
        } catch (Exception e) {
            final Object[] params = new Object[] {this.getDescription(), e.getLocalizedMessage()};
            final String msg = ModelerComparePlugin.Util.getString("ModelGenerator.Error_during_generation", params); //$NON-NLS-1$
            final String pluginId = ModelerComparePlugin.PLUGIN_ID;
            final int code = ERROR_PRODUCING_OUTPUT_MODEL;
            final IStatus status = new Status(IStatus.ERROR, pluginId, code, msg, e);
            problems.addFirst(status);
        } catch (Throwable e) {
            final Object[] params = new Object[] {this.getDescription(), e.getLocalizedMessage()};
            final String msg = ModelerComparePlugin.Util.getString("ModelGenerator.Unexpected_runtime_error_during_generation", params); //$NON-NLS-1$
            final String pluginId = ModelerComparePlugin.PLUGIN_ID;
            final int code = RUNTIME_ERROR_PRODUCING_OUTPUT_MODEL;
            final IStatus status = new Status(IStatus.ERROR, pluginId, code, msg, e);
            problems.addFirst(status);
        }

        // See if cancelled ...
        if (monitor.isCanceled()) {
            throw new UserCancelledException();
        }
    }

    /**
     * Execute the generator. This method invokes the producer to populate the output model, performs a difference analysis
     * between the output and original models, and merges all changes so that the original is a mirror of the output.
     * 
     * @param monitor the progress monitor; may be null
     * @param problems the list into which {@link IStatus} problems should be placed
     */
    @Override
    protected void doComputeDifferenceReport( final IProgressMonitor monitor,
                                              final LinkedList problems ) throws UserCancelledException {
        CoreArgCheck.isNotNull(monitor);

        // Set up the progress monitor
        final Object[] taskParams = new Object[] {this.getDescription()};
        final String taskName = ModelerComparePlugin.Util.getString("ModelGenerator.Performing_{0}", taskParams); //$NON-NLS-1$
        final int numUnits = UNITS_DIFFERENCE + UNITS_DIFFERENCE_POST_PROCESS;
        monitor.beginTask(null, numUnits);
        monitor.setTaskName(taskName);

        // First check if cancelled
        if (monitor.isCanceled()) {
            throw new UserCancelledException();
        }
        // --------------------------------------------------------------
        // Compute the difference between the output and the original ...
        // --------------------------------------------------------------
        final List matcherFactories = getEObjectMatcherFactories();
        final ModelSelector output = this.producer.getOutputSelector();
        final DifferenceProcessor diffProcessor = new DifferenceProcessorImpl(this.original, output);
        if (matcherFactories.size() != 0) {
            diffProcessor.addEObjectMatcherFactories(matcherFactories);
        }
        final IStatus diffStatus = diffProcessor.execute(monitor);
        // Record work performed ...
        monitor.worked(UNITS_DIFFERENCE);

        // Let the subclass do something with the difference report ...
        final DifferenceReport differenceReport = diffProcessor.getDifferenceReport();
        addDifferenceReport(differenceReport);
        if (diffStatus.getSeverity() == IStatus.ERROR) {
            problems.addFirst(diffStatus);
        } else {
            problems.add(diffStatus);
        }
    }

    protected class WrappedModelerCoreException extends ModelerCoreException {
        public WrappedModelerCoreException( Exception exception ) {
            super(exception);
        }
    }

    /**
     * @see org.teiid.designer.compare.generator.AbstractModelGenerator#doPostProcessDifferenceReports()
     */
    @Override
    protected void doPostProcessDifferenceReports() {
        // do nothing by default
    }

    /**
     * Execute the generator. This method invokes the producer to populate the output model, performs a difference analysis
     * between the output and original models, and merges all changes so that the original is a mirror of the output.
     * 
     * @param monitor the progress monitor; may be null
     * @param problems the list into which {@link IStatus} problems should be placed
     * @throws UserCancelledException if the user cancelled the operation
     */
    @Override
    protected void doMergeOutputIntoOriginal( final IProgressMonitor monitor,
                                              final LinkedList problems ) throws UserCancelledException {
        CoreArgCheck.isNotNull(monitor);

        // Set up the progress monitor
        final Object[] taskParams = new Object[] {this.getDescription()};
        final String taskName = ModelerComparePlugin.Util.getString("ModelGenerator.Performing_{0}", taskParams); //$NON-NLS-1$
        final int numUnits = UNITS_MERGE;
        monitor.beginTask(null, numUnits);
        monitor.setTaskName(taskName);

        // See if cancelled ...
        if (monitor.isCanceled()) {
            throw new UserCancelledException();
        }

        // ------------------------------------------------------------------
        // Merge the differences (make the original look like the output) ...
        // ------------------------------------------------------------------
        if (getAllDifferenceReports().size() != 0) {
            final DifferenceReport diffReport = (DifferenceReport)getDifferenceReports().get(0);
            final ModelSelector output = this.producer.getOutputSelector();
            boolean moveRatherThanCopyAdds = isNewModelCase();
            EObject[] dummyArray = null;
            this.mergeProcessor = new MergeProcessorImpl(diffReport, this.original, output, dummyArray, moveRatherThanCopyAdds);
            this.mergeProcessor.setEndingToSourceMapping(this.getProducedToOutputMapping());
            final IStatus mergeStatus = mergeProcessor.execute(monitor);

            if (mergeStatus.getSeverity() == IStatus.ERROR) {
                // There was a catastrophic problem ...
                problems.add(mergeStatus);
            }

            // Close the processor's temporary resource ...
            output.close();
        }
        // Record work performed ...
        monitor.worked(UNITS_MERGE);
    }

    /**
     * @see org.teiid.designer.compare.generator.AbstractModelGenerator#doReresolveAndRebuildImports()
     * @since 4.2
     */
    @Override
    protected void doReresolveAndRebuildImports() {
        if (this.mergeProcessor != null) {
            this.mergeProcessor.reresolve(null);
        }
    }

    /**
     * @see org.teiid.designer.compare.ModelGenerator#saveModel()
     * @since 5.0.2
     */
    @Override
    public void saveModel() {
        super.saveModel();

        final ModelSelector selector = getOriginalModelSelector();
        if (selector instanceof ModelResourceSelector) {
            try {
                // System.out.println(" - - - - - - - BasicModelGenerator.saveModel() Saving Resource = " +
                // ((ModelResourceSelector)selector).getModelResource().getItemName());
                ((ModelResourceSelector)selector).getModelResource().save(null, true);
            } catch (ModelWorkspaceException err) {
                ModelerComparePlugin.Util.log(err);
            }
        }
    }
}
