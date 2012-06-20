/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.compare.generator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.teiid.core.UserCancelledException;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.compare.ModelGenerator;
import org.teiid.designer.core.compare.EObjectMatcherFactory;


/**
 * CompositeModelGenerator
 */
public class CompositeModelGenerator extends AbstractModelGenerator {

    private final List modelGenerators;

    /**
     * Construct an instance of AbstractModelGenerator.
     * @param modelGenerators the list of {@link EObjectMatcherFactory mapping adapter}
     * instances used during the difference processing; may not be null or empty
     */
    public CompositeModelGenerator( final List modelGenerators ) {
        super(null);
        CoreArgCheck.isNotNull(modelGenerators);
        this.modelGenerators = modelGenerators != null ? modelGenerators : new ArrayList();
    }
    
    /**
     * Return the ordered list of {@link ModelGenerator} instances that comprise this generator.
     * @return the list of generators; never null
     */
    public List getModelGenerators() {
        return this.modelGenerators;
    }

    /**
     * @see org.teiid.designer.compare.generator.AbstractModelGenerator#getAllDifferenceReports()
     */
    @Override
    public List getAllDifferenceReports() {
        final List reports = new LinkedList();
        final Iterator iter = getModelGenerators().iterator();
        while (iter.hasNext()) {
            final ModelGenerator generator = (ModelGenerator)iter.next();
            reports.addAll(generator.getAllDifferenceReports());
        }
        return reports;
    }


    /**
     * This method closes each of the {@link #getModelGenerators() ModelGenerators} that make up this
     * object.  This metod overrides the inherited behavior.
     * @see org.teiid.designer.compare.generator.AbstractModelGenerator#close()
     */
    @Override
    public void close() {
        super.close();
        final Iterator iter = getModelGenerators().iterator();
        while (iter.hasNext()) {
            final ModelGenerator generator = (ModelGenerator)iter.next();
            generator.close();
        }
    }
    
    // =========================================================================
    //                Methods to implement basic behavior
    // =========================================================================

    /**
     * @see org.teiid.designer.compare.generator.AbstractModelGenerator#doClearDifferenceReports()
     */
    @Override
    protected void doClearDifferenceReports() {
        // do nothing; they are always regenerated from the contained ...
    }


    /**
     * @see org.teiid.designer.compare.generator.AbstractModelGenerator#doGenerateOutput(org.eclipse.core.runtime.IProgressMonitor, java.util.LinkedList)
     */
    @Override
    protected void doGenerateOutput(final IProgressMonitor monitor, 
                                      final LinkedList problems) throws UserCancelledException {
        // Generate all of the contained ...
        final Iterator iter = getModelGenerators().iterator();
        while (iter.hasNext()) {
            final ModelGenerator generator = (ModelGenerator)iter.next();
            final IStatus status = generator.generateOutput(monitor);
            problems.add(status);
        }
    }
    
    /**
     * @see org.teiid.designer.compare.generator.AbstractModelGenerator#doComputeDifferenceReport(org.eclipse.core.runtime.IProgressMonitor, java.util.LinkedList)
     */
    @Override
    protected void doComputeDifferenceReport(final IProgressMonitor monitor, 
                                               final LinkedList problems) throws UserCancelledException {
        // Generate all of the contained ...
        final Iterator iter = getModelGenerators().iterator();
        while (iter.hasNext()) {
            final ModelGenerator generator = (ModelGenerator)iter.next();
            final IStatus status = generator.computeDifferenceReport(monitor);
            problems.add(status);
        }
    }
    
    /**
     * @see org.teiid.designer.compare.generator.AbstractModelGenerator#doPostProcessDifferenceReports()
     */
    @Override
    protected void doPostProcessDifferenceReports() {
        // do nothing, since each generator in this composite will each tell themselves to post-process
        // their own
    }

    

    /**
     * @see org.teiid.designer.compare.generator.AbstractModelGenerator#doMergeOutputIntoOriginal(org.eclipse.core.runtime.IProgressMonitor, java.util.LinkedList)
     */
    @Override
    protected void doMergeOutputIntoOriginal(final IProgressMonitor monitor, 
                                             final LinkedList problems) throws UserCancelledException {
        // Generate all of the contained ...
        final Iterator iter = getModelGenerators().iterator();
        while (iter.hasNext()) {
            final ModelGenerator generator = (ModelGenerator)iter.next();
            if ( generator instanceof AbstractModelGenerator ) {
                final AbstractModelGenerator amg = (AbstractModelGenerator)generator;
                // Tell the generators to use this composite generator's mapping
                amg.setProducedToOutputMapping( this.getProducedToOutputMapping() );
            }
            final IStatus status = generator.mergeOutputIntoOriginal(monitor);
            problems.add(status);
        }
        
        // Reresolve imports ...
        this.doReresolveAndRebuildImports();
        
        if( isSaveAllBeforeFinish() ) {
            this.saveModel();
        }
    }
    
    
    /** 
     * @see org.teiid.designer.compare.generator.AbstractModelGenerator#doReresolveAndRebuildImports()
     * @since 4.2
     */
    @Override
    protected void doReresolveAndRebuildImports() {
        
        // Go through all of the  contained generators and invoke their postMerge ...
        final Iterator iter2 = getModelGenerators().iterator();
        while (iter2.hasNext()) {
            final ModelGenerator generator = (ModelGenerator)iter2.next();
            if ( generator instanceof AbstractModelGenerator ) {
                final AbstractModelGenerator amg = (AbstractModelGenerator)generator;
                amg.doReresolveAndRebuildImports();
            }
        }
    }

    /**
     *  
     * @see org.teiid.designer.compare.ModelGenerator#setNewModelCase(boolean)
     * @since 5.0.2
     */
    @Override
    public void setNewModelCase(boolean theIsNewModelCase) {
        super.setNewModelCase(theIsNewModelCase);
        
        // Commumnicate the boolean to child generators
        final Iterator iter = getModelGenerators().iterator();
        while (iter.hasNext()) {
            final ModelGenerator generator = (ModelGenerator)iter.next();
            generator.setNewModelCase(theIsNewModelCase);
        }
    }

    /**
     *  
     * @see org.teiid.designer.compare.ModelGenerator#saveModel()
     * @since 5.0.2
     */
    @Override
    public void saveModel() {
        // Commumnicate the saveModel() to child generators
        final Iterator iter = getModelGenerators().iterator();
        while (iter.hasNext()) {
            final ModelGenerator generator = (ModelGenerator)iter.next();
            generator.saveModel();
        }
    }
    
    /**
     *  
     * @see org.teiid.designer.compare.ModelGenerator#setSaveAllBeforeFinish(boolean)
     * @since 5.0.2
     */
    @Override
    public void setSaveAllBeforeFinish(boolean theDoSave) {
        super.setSaveAllBeforeFinish(theDoSave);
        
        final Iterator iter = getModelGenerators().iterator();
        while (iter.hasNext()) {
            final ModelGenerator generator = (ModelGenerator)iter.next();
            generator.setSaveAllBeforeFinish(theDoSave);
        }
    }
    

}
