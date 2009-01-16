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

package com.metamatrix.modeler.compare.generator;

import java.util.Collections;
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

import com.metamatrix.core.UserCancelledException;
import com.metamatrix.modeler.compare.DifferenceReport;
import com.metamatrix.modeler.compare.ModelGenerator;
import com.metamatrix.modeler.compare.ModelerComparePlugin;

/**
 * The AbstractModelGenerator class is the abstract base class that can be used to implement 
 * {@link ModelGenerator}.  It provide an implementation of the public methods by defining and using
 * a (fewer) number of abstract methods.
 */
public abstract class AbstractModelGenerator implements ModelGenerator {
    
    private final List matcherFactories;
    private String description;
    private final List differenceReports;
    private Map producedToOutputMapping;
    private boolean isNewModelCase = false;
    private boolean saveAllBeforeFinish  = false;

    /**
     * Construct an instance of AbstractModelGenerator.
     * @param matcherFactories the list of {@link com.metamatrix.modeler.core.util.EObjectMappingAdapter mapping adapter}
     * instances used during the difference processing; may not be null or empty
     */
    public AbstractModelGenerator( final List matcherFactories ) {
        super();
        this.differenceReports = new LinkedList();
        this.matcherFactories = matcherFactories != null ? matcherFactories : Collections.EMPTY_LIST;
        this.producedToOutputMapping = new HashMap();
    }
    
    public void setProducedToOutputMapping( final Map mapping ) {
        this.producedToOutputMapping = mapping != null ? mapping : new HashMap();
    }
    
    public Map getProducedToOutputMapping() {
        return this.producedToOutputMapping;
    }
    
    /**
     * Obtain the description for this generator.  This method is used to help generate the status messages,
     * and therefore should never be null or zero-length.  Consequently, if the description is never set
     * or is {@link #setDescription(String) set} with a null or zero-length string, a 
     * {@link #getDefaultDescription() default description} is used.
     * @return the description; may not be null
     */
    public String getDescription() {
        if ( this.description != null && this.description.trim().length() != 0 ) {
            return this.description;
        }
        final String desc = getDefaultDescription();
        if ( desc != null && desc.trim().length() != 0 ) {
            return desc;
        }
        return "the operation"; //$NON-NLS-1$
    }
    
    /**
     * Set the description for this generator.
     * @param desc the description; may be null
     */
    public void setDescription( final String desc ) {
        this.description = desc;
    }
    
    /**
     * Execute the generator.  This method invokes the producer to populate the output model,
     * performs a difference analysis between the output and original models,
     * and merges all changes so that the original is a mirror of the output.
     * @param monitor the progress monitor; may be null
     * @return the status containing the result of the generation and merge process
     */
    public IStatus execute( final IProgressMonitor progressMonitor ) {
        final LinkedList problems = new LinkedList();

        // Start the progress monitor tasks ...
        final IProgressMonitor monitor = progressMonitor != null ? progressMonitor : new NullProgressMonitor();
        IStatus diffStatus = null;

        // PHASE 1: Generate ...
        try {
            doClearDifferenceReports();
            doGenerateOutput(monitor,problems);
            diffStatus = createStatus(problems);
        } catch (UserCancelledException e) {
            diffStatus = newUserCancelledStatus();
        }
        if ( diffStatus.getSeverity() == IStatus.ERROR ) {
            // There was a catastrophic problem ...
            return diffStatus;
        }
        
        // Only process/update for differences if it's NOT a new model case;

        // PHASE 2: Compute the differences ...
        problems.add(diffStatus);
        try {
            doComputeDifferenceReport(monitor,problems);
            if ( this.differenceReports.size() != 0 ) {
                doPostProcessDifferenceReports();
            }
            diffStatus = createStatus(problems);
        } catch (UserCancelledException e) {
            diffStatus = newUserCancelledStatus();
        }
        if ( diffStatus.getSeverity() == IStatus.ERROR ) {
            // There was a catastrophic problem ...
            return diffStatus;
        }

        // PHASE 3: Merge the differences ...
        problems.add(diffStatus);
        try {
            doMergeOutputIntoOriginal(monitor,problems);
            doPostMerge();
        } catch (UserCancelledException e1) {
            final IStatus mergeStatus = newUserCancelledStatus();
            problems.addFirst(mergeStatus);
        } finally {
            try {
                monitor.done();
            } catch (RuntimeException e) {
                ModelerComparePlugin.Util.log(e);
            }
        }

        return createStatus(problems);
    }
    
    /**
     * Run the generator.  This method invokes the producer to populate the output model, but does not
     * performs a difference analysis between the output and original models, nor does it
     * merge changes so that the original is a mirror of the output.
     * This method should be called before
     * {@link #computeDifferenceReport(IProgressMonitor)} and before {@link #mergeOutputIntoOriginal(IProgressMonitor)}.
     * @param monitor the progress monitor; may be null
     * @return the status containing the result of the generation and merge process
     */
    public IStatus generateOutput( final IProgressMonitor progressMonitor ) {
        final IProgressMonitor monitor = progressMonitor != null ? progressMonitor : new NullProgressMonitor();
        final LinkedList problems = new LinkedList();
        try {
            doClearDifferenceReports();
            doGenerateOutput(monitor,problems);
        } catch ( UserCancelledException e ) {
            final IStatus status = newUserCancelledStatus();
            problems.addFirst(status);
        } finally {
            try {
                monitor.done();
            } catch (RuntimeException e) {
                ModelerComparePlugin.Util.log(e);
            }
        }
        return createStatus(problems);
    }
    
    /**
     * After generating, compute the difference report.  This method should be called after
     * {@link #generateOutput(IProgressMonitor)} and before {@link #mergeOutputIntoOriginal(IProgressMonitor)}.
     * @param monitor the progress monitor; may be null
     * @return the status containing the result of the generation and merge process
     */
    public IStatus computeDifferenceReport( final IProgressMonitor progressMonitor ) {
        final IProgressMonitor monitor = progressMonitor != null ? progressMonitor : new NullProgressMonitor();
        final LinkedList problems = new LinkedList();
        try {
            doComputeDifferenceReport(monitor,problems);
            if ( this.differenceReports.size() != 0 ) {
                doPostProcessDifferenceReports();
            }
        } catch ( UserCancelledException e ) {
            final IStatus status = newUserCancelledStatus();
            problems.addFirst(status);
        } finally {
            try {
                monitor.done();
            } catch (RuntimeException e) {
                ModelerComparePlugin.Util.log(e);
            }
        }
        return createStatus(problems);
    }
    
    /**
     * @see com.metamatrix.modeler.compare.ModelGenerator#getAllDifferenceReports()
     */
    public List getAllDifferenceReports() {
        return this.differenceReports;
    }

    /**
     * @see com.metamatrix.modeler.compare.ModelGenerator#getDifferenceReports()
     */
    public List getDifferenceReports() {
        return getAllDifferenceReports();
    }
    
    protected void addDifferenceReport( final DifferenceReport report ) {
        this.differenceReports.add(report);
    }

    
    public List getEObjectMatcherFactories() {
        return this.matcherFactories;
    }
    
    /**
     * Execute the generator.  This method invokes the producer to populate the output model,
     * performs a difference analysis between the output and original models,
     * and merges all changes so that the original is a mirror of the output.
     * This method should be called after
     * {@link #generateOutput(IProgressMonitor)} and after {@link #computeDifferenceReport(IProgressMonitor)}.
     * @param monitor the progress monitor; may be null
     * @return the status containing the result of the generation and merge process
     */
    public IStatus mergeOutputIntoOriginal( final IProgressMonitor progressMonitor ) {
        final IProgressMonitor monitor = progressMonitor != null ? progressMonitor : new NullProgressMonitor();
        final LinkedList problems = new LinkedList();
        try {
            doMergeOutputIntoOriginal(monitor,problems);
            doPostMerge();
        } catch (UserCancelledException e1) {
            final IStatus mergeStatus = newUserCancelledStatus();
            problems.addFirst(mergeStatus);
        } finally {
            try {
                monitor.done();
            } catch (RuntimeException e) {
                ModelerComparePlugin.Util.log(e);
            }
        }
        return createStatus(problems);
    }
    
    /**
     * Close any resources that were opened by this generator.  This method should be called when finished
     * with this object.  Subclasses should override this method if they have resources that should be closed.
     */
    public void close() {
    }
    
    // =========================================================================
    //                Methods to implement basic behavior
    // =========================================================================

    protected void doClearDifferenceReports() {
        // Call the method so that if it is overridden, this implementation might work without being overridden
        this.getAllDifferenceReports().clear();
        this.producedToOutputMapping.clear();
    }

    /**
     * Execute the generator.  This method invokes the producer to populate the output model,
     * performs a difference analysis between the output and original models,
     * and merges all changes so that the original is a mirror of the output.
     * @param monitor the progress monitor; may be null
     * @param problems the list into which {@link IStatus} problems should be placed
     * @throws UserCancelledException if the user cancelled the operation
     */
    protected abstract void doGenerateOutput( final IProgressMonitor monitor, 
                            final LinkedList problems ) throws UserCancelledException;
    
    /**
     * Execute the generator.  This method invokes the producer to populate the output model,
     * performs a difference analysis between the output and original models,
     * and merges all changes so that the original is a mirror of the output.
     * @param monitor the progress monitor; may be null
     * @param problems the list into which {@link IStatus} problems should be placed
     * @throws UserCancelledException if the user cancelled the operation
     */
    protected abstract void doComputeDifferenceReport( final IProgressMonitor monitor, 
                            final LinkedList problems ) throws UserCancelledException;
    
    /**
     * Method that can be overridden to post process the difference reports.  This implementation merely
     * does nothing.
     */
    protected abstract void doPostProcessDifferenceReports();
    
    /**
     * Method that is executed after the {@link #doMergeOutputIntoOriginal(IProgressMonitor, LinkedList)}
     * method.
     */
    protected void doPostMerge() {
        // do nothing
    }
    
    protected abstract void doReresolveAndRebuildImports();
    
    /**
     * Execute the generator.  This method invokes the producer to populate the output model,
     * performs a difference analysis between the output and original models,
     * and merges all changes so that the original is a mirror of the output.
     * @param monitor the progress monitor; may be null
     * @param problems the list into which {@link IStatus} problems should be placed
     * @throws UserCancelledException if the user cancelled the operation
     */
    protected abstract void doMergeOutputIntoOriginal( final IProgressMonitor monitor, 
                                        final LinkedList problems ) throws UserCancelledException;
    
    protected String getDefaultDescription() {
        return ModelerComparePlugin.Util.getString("AbstractModelGenerator.Default_description"); //$NON-NLS-1$
    }
    
    protected IStatus newUserCancelledStatus() {
        final Object[] params = new Object[]{this.getDescription()};
        final String msg = ModelerComparePlugin.Util.getString("AbstractModelGenerator.User_cancelled_operation",params); //$NON-NLS-1$
        final String pluginId = ModelerComparePlugin.PLUGIN_ID;
        final int code = USER_CANCELLED;
        final IStatus status = new Status(IStatus.WARNING,pluginId,code,msg,null);
        return status;
    }
    
    protected IStatus newSuccessStatus() {
        final Object[] params = new Object[]{this.getDescription()};
        final String msg = ModelerComparePlugin.Util.getString("AbstractModelGenerator.Success_msg",params); //$NON-NLS-1$
        final String pluginId = ModelerComparePlugin.PLUGIN_ID;
        final int code = USER_CANCELLED;
        final IStatus status = new Status(IStatus.WARNING,pluginId,code,msg,null);
        return status;
    }
    
    protected IStatus createStatus( final List problems ) {
//      // Put all of the problems into a single IStatus ...
      IStatus resultStatus = null;
      final String pluginId = ModelerComparePlugin.PLUGIN_ID;
      if ( problems == null || problems.isEmpty() ) {
          final Object[] params = new Object[]{this.getDescription()};
          final String msg = ModelerComparePlugin.Util.getString("AbstractModelGenerator.Completed_generation",params); //$NON-NLS-1$
          final IStatus status = new Status(IStatus.OK,pluginId,COMPLETED_WITH_NO_PROBLEMS,msg,null);
          resultStatus = status;
      } else if ( problems.size() == 1 ) {
          resultStatus = (IStatus) problems.get(0);
      } else {
          // There were problems, so determine whether there were warnings and errors ...
          int numErrors = 0;
          int numWarnings = 0;
          final Iterator iter = problems.iterator();
          while (iter.hasNext()) {
              final IStatus aStatus = (IStatus)iter.next();
              if ( aStatus.getSeverity() == IStatus.WARNING ) {
                  ++numWarnings;
              } else if ( aStatus.getSeverity() == IStatus.ERROR ) {
                  ++numErrors;
              }
          }
                
          // Create the final status ...
          final IStatus[] statusArray = (IStatus[]) problems.toArray(new IStatus[problems.size()]);
          if ( numWarnings != 0 && numErrors == 0 ) {
              final Object[] params = new Object[]{this.getDescription(),new Integer(numWarnings)};
              final String msg = ModelerComparePlugin.Util.getString("AbstractModelGenerator.Completed_generation_with_warning(s)",params); //$NON-NLS-1$
              resultStatus = new MultiStatus(pluginId,COMPLETED_WITH_WARNINGS,statusArray,msg,null);
          } else if ( numWarnings == 0 && numErrors != 0 ) {
              final Object[] params = new Object[]{this.getDescription(),new Integer(numErrors)};
              final String msg = ModelerComparePlugin.Util.getString("AbstractModelGenerator.Completed_generation_with_error(s)",params); //$NON-NLS-1$
              resultStatus = new MultiStatus(pluginId,COMPLETED_WITH_ERRORS,statusArray,msg,null);
          } else if ( numWarnings != 0 && numErrors != 0 ) {
              final Object[] params = new Object[]{this.getDescription(),new Integer(numErrors),new Integer(numWarnings)};
              final String msg = ModelerComparePlugin.Util.getString("AbstractModelGenerator.Completed_generation_with_error(s)_and_warning(s)",params); //$NON-NLS-1$
              resultStatus = new MultiStatus(pluginId,COMPLETED_WITH_WARNINGS_AND_ERRORS,statusArray,msg,null);
          } else {
              final Object[] params = new Object[]{this.getDescription()};
              final String msg = ModelerComparePlugin.Util.getString("AbstractModelGenerator.Completed_generation_with_no_errors_or_warnings",params); //$NON-NLS-1$
              resultStatus = new MultiStatus(pluginId,COMPLETED_WITH_NO_WARNINGS_AND_ERRORS,statusArray,msg,null);
          }
      }

      return resultStatus;
    }

    /**
     *  
     * @see com.metamatrix.modeler.compare.ModelGenerator#isNewModelCase()
     * @since 5.0.2
     */
    public boolean isNewModelCase() {
        return this.isNewModelCase;
    }

    /**
     *  
     * @see com.metamatrix.modeler.compare.ModelGenerator#setNewModelCase(boolean)
     * @since 5.0.2
     */
    public void setNewModelCase(boolean theIsNewModelCase) {
        this.isNewModelCase = theIsNewModelCase;
    }
    
    /**
     *  
     * @see com.metamatrix.modeler.compare.ModelGenerator#setSaveAllBeforeFinish(boolean)
     * @since 5.0.2
     */
    public void setSaveAllBeforeFinish(boolean theDoSave) {
        this.saveAllBeforeFinish = theDoSave;
    }

    /**
     *  
     * @see com.metamatrix.modeler.compare.ModelGenerator#isSaveAllBeforeFinish()
     * @since 5.0.2
     */
    public boolean isSaveAllBeforeFinish() {
        return this.saveAllBeforeFinish;
    }
    
    /**
     *  
     * @see com.metamatrix.modeler.compare.ModelGenerator#saveModel()
     * @since 5.0.2
     */
    public void saveModel() {
        // No Op
    }
}
