/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.viewsupport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;

import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerContentProvider;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerLabelProvider;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.viewsupport.StatusInfo;
import com.metamatrix.ui.internal.widget.accumulator.IAccumulatorSource;

/**
 * WorkspaceTreeAccumulatorSource is an AccumulatorSource for the AccumulatorPanel that displays
 * the workspace, with models expandable.  Users may set an ISelectionStatusValidator on this
 * AccumulatorSource to validate the objects selected in the tree.  If no validator is used, then
 * all selections in this tree are considered valid.
 */
public class WorkspaceTreeAccumulatorSource implements IAccumulatorSource {
    
    private static final IStatus OK_STATUS = new StatusInfo(UiConstants.PLUGIN_ID);
    
    private TreeViewer treeViewer;
    private ISelectionStatusValidator validator;
    private IStatus currentStatus = OK_STATUS;
    private Collection currentValues;
    private ILabelProvider labelProvider;
    private ITreeContentProvider contentProvider;
    private ViewerFilter viewerFilter;

    /**
     * Construct an instance of WorkspaceTreeAccumulatorSource.
     */
    public WorkspaceTreeAccumulatorSource(Collection currentValues) {
        this(currentValues, new ModelExplorerLabelProvider(), new ModelExplorerContentProvider());
    }

    /**
     * Construct an instance of WorkspaceTreeAccumulatorSource.
     */
    public WorkspaceTreeAccumulatorSource(Collection currentValues, ILabelProvider labelProvider, ITreeContentProvider contentProvider) {
        this.currentValues = new ArrayList(currentValues);
        this.labelProvider = labelProvider;
        this.contentProvider = contentProvider;
    }

    public void setSelectionValidator(ISelectionStatusValidator validator) {
        this.validator = validator;
    }
    
    public void setViewerFilter(ViewerFilter filter) {
        this.viewerFilter = filter;
    }
    
    public ILabelProvider getLabelProvider() {
        return labelProvider;
    }
    
    /**
     * Obtain the Collection of current values being accumluated.  This method is available
     * for the validator to compare a selection against the current values to detect, among
     * other things, duplicate instances.
     * @return an unmodifiable collection of this accumulator's current values.
     */
    public Collection getCurrentValues() {
        return Collections.unmodifiableCollection(this.currentValues);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.ui.internal.widget.accumulator.IAccumulatorSource#accumulatedValuesRemoved(java.util.Collection)
     */
    public void accumulatedValuesRemoved(Collection values) {
        // this accumulator source does not modify the tree to reflect selected values
        this.currentValues.removeAll(values);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.ui.internal.widget.accumulator.IAccumulatorSource#accumulatedValuesAdded(java.util.Collection)
     */
    public void accumulatedValuesAdded(Collection values) {
        // this accumulator source does not modify the tree to reflect selected values
        this.currentValues.addAll(values);
        
        // (But we must deselect to avoid the 'current value already contains' validation error.
        //  See defect 13000.)        
        treeViewer.setSelection( null, false );
    }

    /* (non-Javadoc)
     * @see com.metamatrix.ui.internal.widget.accumulator.IAccumulatorSource#getAvailableValues()
     */
    public Collection getAvailableValues() {
        // since Add All is not supported, return an empty list.
        return Collections.EMPTY_LIST;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.ui.internal.widget.accumulator.IAccumulatorSource#getAvailableValuesCount()
     */
    public int getAvailableValuesCount() {
        // must return a non-0 value or the accumulator controls will not enable.
        return 10;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.ui.internal.widget.accumulator.IAccumulatorSource#getSelectedAvailableValues()
     */
    public Collection getSelectedAvailableValues() {
        ISelection selection = treeViewer.getSelection();
        Collection result = SelectionUtilities.getSelectedObjects(selection);
        if ( validator != null ) {
            currentStatus = validator.validate(result.toArray());
        }
        return result;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.ui.internal.widget.accumulator.IAccumulatorSource#getSelectedAvailableValuesCount()
     */
    public int getSelectedAvailableValuesCount() {
        return getSelectedAvailableValues().size();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.ui.internal.widget.accumulator.IAccumulatorSource#createControl(org.eclipse.swt.widgets.Composite)
     */
    public Control createControl(Composite parent) {
        treeViewer = new TreeViewer(parent);
        treeViewer.setLabelProvider(labelProvider);
        treeViewer.setContentProvider(contentProvider);
        if ( viewerFilter != null ) {
            treeViewer.addFilter(viewerFilter);
        }
        treeViewer.setInput(ResourcesPlugin.getWorkspace().getRoot());
        treeViewer.expandToLevel(2);
        return treeViewer.getControl();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.ui.internal.widget.accumulator.IAccumulatorSource#addSelectionListener(org.eclipse.swt.events.SelectionListener)
     */
    public void addSelectionListener(SelectionListener listener) {
        treeViewer.getTree().addSelectionListener(listener);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.ui.internal.widget.accumulator.IAccumulatorSource#supportsAddAll()
     */
    public boolean supportsAddAll() {
        return false;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.ui.internal.widget.accumulator.IAccumulatorSource#getSelectionStatus()
     */
    public IStatus getSelectionStatus() {
        getSelectedAvailableValues();
        return currentStatus;
    }

}
