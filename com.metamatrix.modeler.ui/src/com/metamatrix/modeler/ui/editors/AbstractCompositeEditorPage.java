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

package com.metamatrix.modeler.ui.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.part.EditorPart;

import com.metamatrix.ui.internal.eventsupport.CompositeNotifyChangeListener;
import com.metamatrix.ui.internal.eventsupport.CompositeSelectionChangedListener;
import com.metamatrix.ui.internal.eventsupport.CompositeSelectionProvider;

/**
 * AbstractCompositeEditorPage
 */
public abstract class AbstractCompositeEditorPage extends EditorPart
                                                  implements ModelEditorPage, IGotoMarker {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /** Notification listener for all the sub-Editors. */
    private CompositeNotifyChangeListener notifyChangeListener;
    
    /** Collection of <code>IPropertyListener</code>s. */
    private List propertyListeners;
    
    /** Provider for all the sub-Editors. */
    private CompositeSelectionChangedListener selectionChangedListener;
    
    /** Provider for all the sub-Editors. */
    private CompositeSelectionProvider selectionProvider;
    
    /** Collection of <code>ModelEditorPage</code>s. */
    private List subEditors;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONTROLS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /** Sash separates each sub-editor. */
    private SashForm sash;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /** Constructs a <code>AbstractCompositeEditorPage</code>. */
    public AbstractCompositeEditorPage() {
        propertyListeners = new ArrayList();
        subEditors = new ArrayList();
        selectionProvider = new CompositeSelectionProvider();
        notifyChangeListener = new CompositeNotifyChangeListener();
        selectionChangedListener = new CompositeSelectionChangedListener();
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPart#addPropertyListener(org.eclipse.ui.IPropertyListener)
     */
    @Override
    public void addPropertyListener(IPropertyListener theListener) {
        // add to existing subeditors
        for (int size = subEditors.size(), i = 0; i < size; i++) {
            ModelEditorPage editor = (ModelEditorPage)subEditors.get(i);
            editor.addPropertyListener(theListener);
        }
    }
    
    /**
     * Adds a sub-editor.
     * @param theSubEditor the editor being added
     */
    protected void addSubEditor(ModelEditorPage theSubEditor) {
        if (!subEditors.contains(theSubEditor)) {
            subEditors.add(theSubEditor);
        }
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#canOpenContext(java.lang.Object)
     */
    public boolean canOpenContext(Object theInput) {
        boolean result = false;
        
        // just look at first editor. all sub-editors must be able to open the same inputs
        if (!subEditors.isEmpty()) {
            ModelEditorPage subEditor = (ModelEditorPage)subEditors.get(0);
            result = subEditor.canOpenContext(theInput);
        }
        
        return result;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPartControl(Composite theParent) {
        int eSize = subEditors.size();
        
        sash = new SashForm(theParent, SWT.HORIZONTAL);
        RowLayout layout = new RowLayout();
        sash.setLayout(layout);
        
        // create and add sub-editors
        for (int i = 0; i < eSize; i++) {
            ModelEditorPage subEditor = (ModelEditorPage)subEditors.get(i);
            subEditor.createPartControl(sash);
        }
        
        // now wire them all up
        for (int i = 0; i < eSize; i++) {
            ModelEditorPage subEditor = (ModelEditorPage)subEditors.get(i);

            // add all property listeners to new editor
            for (int plSize = propertyListeners.size(), j = 0; j < plSize; i++) {
                IPropertyListener listener = (IPropertyListener)propertyListeners.get(j);
                subEditor.addPropertyListener(listener);
            }
            
            // add selection provider to composite selection provider if necessary
            ISelectionProvider provider = subEditor.getModelObjectSelectionProvider();
            
            if (provider != null) {
                selectionProvider.addSelectionProvider(provider);
            }

            // add notify change listener to compositer listener if necessary
            INotifyChangedListener notifyListener = subEditor.getNotifyChangedListener();
            
            if (notifyListener != null) {
                notifyChangeListener.addNotifyChangeListener(notifyListener);
            }

            // add selection change listener to compositer listener if necessary
            ISelectionChangedListener selectionListener = subEditor.getModelObjectSelectionChangedListener();
            
            if (selectionListener != null) {
                selectionChangedListener.addSelectionChangedListener(selectionListener);
            }

        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPart#dispose()
     */
    @Override
    public void dispose() {
        // dispose sub-editors
        for (int size = subEditors.size(), i = 0; i < size; i++) {
            ModelEditorPage subEditor = (ModelEditorPage)subEditors.get(i);
            subEditor.dispose();
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void doSave(IProgressMonitor theMonitor) {
        // doSave sub-editors
        for (int size = subEditors.size(), i = 0; i < size; i++) {
            ModelEditorPage subEditor = (ModelEditorPage)subEditors.get(i);
            subEditor.doSave(theMonitor);
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.ISaveablePart#doSaveAs()
     */
    @Override
    public void doSaveAs() {
        // doSave sub-editors
        for (int size = subEditors.size(), i = 0; i < size; i++) {
            ModelEditorPage subEditor = (ModelEditorPage)subEditors.get(i);
            subEditor.doSaveAs();
        }
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#getActionBarContributor()
     */
    public AbstractModelEditorPageActionBarContributor getActionBarContributor() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    @Override
    public Object getAdapter(Class theAdapterClass) {
        Object adapter = super.getAdapter(theAdapterClass);
        
        // if super does not have adapter take first sub-editor that has one
        if (adapter == null) {
            for (int size = subEditors.size(), i = 0; i < size; i++) {
                ModelEditorPage subEditor = (ModelEditorPage)subEditors.get(i);
                adapter = subEditor.getAdapter(theAdapterClass);
                
                if (adapter != null) {
                    break;
                }
            }
        }
        
        return adapter;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#getControl()
     */
    public Control getControl() {
        return sash;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IEditorPart#getEditorInput()
     */
    @Override
    public IEditorInput getEditorInput() {
        IEditorInput result = null;

        // the same input goes to each sub-editor. so get it from first one
        if (!subEditors.isEmpty()) {
            ModelEditorPage subEditor = (ModelEditorPage)subEditors.get(0);
            result = subEditor.getEditorInput();
        }

        return result;        
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IEditorPart#getEditorSite()
     */
    @Override
    public IEditorSite getEditorSite() {
        return super.getEditorSite();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#getModelObjectSelectionChangedListener()
     */
    public ISelectionChangedListener getModelObjectSelectionChangedListener() {
        return selectionChangedListener;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#getModelObjectSelectionProvider()
     */
    public ISelectionProvider getModelObjectSelectionProvider() {
        return selectionProvider;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#getNotifyChangedListener()
     */
    public INotifyChangedListener getNotifyChangedListener() {
        return notifyChangeListener;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#getOutlineContribution()
     */
    public abstract ModelEditorPageOutline getOutlineContribution();

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPart#getSite()
     */
    @Override
    public IWorkbenchPartSite getSite() {
        return super.getSite();
    }
    
    /**
     * Gets the <code>ModelEditorPage</code> at the given index.
     * @param theIndex
     * @return the sub-editor
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public ModelEditorPage getSubEditor(int theIndex) {
        return (ModelEditorPage)subEditors.get(theIndex);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPart#getTitle()
     */
    @Override
    public abstract String getTitle();

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPart#getTitleImage()
     */
    @Override
    public abstract Image getTitleImage();

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPart#getTitleToolTip()
     */
    @Override
    public abstract String getTitleToolTip();

    /* (non-Javadoc)
    /* (non-Javadoc)
     * @see org.eclipse.ui.IEditorPart#gotoMarker(org.eclipse.core.resources.IMarker)
     */
    public void gotoMarker(IMarker theMarker) {
        // tell each sub-editor
        for (int size = subEditors.size(), i = 0; i < size; i++) {
            ModelEditorPage subEditor = (ModelEditorPage)subEditors.get(i);
			IDE.gotoMarker(subEditor, theMarker);
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IEditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
     */
    @Override
    public void init(IEditorSite theSite,
                     IEditorInput theInput) throws PartInitException {

        setSite(theSite);
        setInput(theInput);
        
        for (int size = subEditors.size(), i = 0; i < size; i++) {
            Object subEditor = subEditors.get(i);
            
            if (subEditor instanceof IEditorPart) {
                ((IEditorPart)subEditor).init(theSite, theInput);
            }
        }
        
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.ISaveablePart#isDirty()
     */
    @Override
    public boolean isDirty() {
        boolean result = false;
        
        // dirty if one sub-editor is dirty
        for (int size = subEditors.size(), i = 0; i < size; i++) {
            ModelEditorPage subEditor = (ModelEditorPage)subEditors.get(i);
            
            if (subEditor.isDirty()) {
                result = true;
                break;
            }
        }
        
        return result;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
     */
    @Override
    public boolean isSaveAsAllowed() {
        boolean result = false;
        
        // save allowed if one sub-editor allows save
        for (int size = subEditors.size(), i = 0; i < size; i++) {
            ModelEditorPage subEditor = (ModelEditorPage)subEditors.get(i);
            
            if (subEditor.isSaveAsAllowed()) {
                result = true;
                break;
            }
        }
        
        return result;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.ISaveablePart#isSaveOnCloseNeeded()
     */
    @Override
    public boolean isSaveOnCloseNeeded() {
        boolean result = false;
        
        // save need if one sub-editor needs save on close
        for (int size = subEditors.size(), i = 0; i < size; i++) {
            ModelEditorPage subEditor = (ModelEditorPage)subEditors.get(i);
            
            if (subEditor.isSaveOnCloseNeeded()) {
                result = true;
                break;
            }
        }
        
        return result;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#openContext(java.lang.Object)
     */
    public void openContext(Object theInput) {
        // send input to each sub-editor
        for (int size = subEditors.size(), i = 0; i < size; i++) {
            ModelEditorPage subEditor = (ModelEditorPage)subEditors.get(i);
            subEditor.openContext(theInput);
        }
    }
    
    /**
     * Removes a sub-editor.
     * @param theSubEditor the editor being added
     */
    protected void removeSubEditor(ModelEditorPage theSubEditor) {
        if (subEditors.contains(theSubEditor)) {
            subEditors.remove(theSubEditor);
            
            // remove all property listeners from new editor
            for (int size = propertyListeners.size(), i = 0; i < size; i++) {
                IPropertyListener listener = (IPropertyListener)propertyListeners.get(i);
                theSubEditor.removePropertyListener(listener);
            }
            
            // remove selection provider from composite selection provider
            ISelectionProvider provider = theSubEditor.getModelObjectSelectionProvider();
            
            if (provider != null) {
                selectionProvider.removeSelectionProvider(provider);
            }

            // remove notification listener from composite listener
            INotifyChangedListener listener = theSubEditor.getNotifyChangedListener();
            
            if (listener != null) {
                notifyChangeListener.removeNotifyChangeListener(listener);
            }

            // remove selection change listener from compositer listener if necessary
            ISelectionChangedListener selectionListener = theSubEditor.getModelObjectSelectionChangedListener();
            
            if (selectionListener != null) {
                selectionChangedListener.removeSelectionChangedListener(selectionListener);
            }
        }
    }

    /**
     * Removes a sub-editor.
     * @param theIndex the index of the editor being removed
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    protected void removeSubEditor(int theIndex) {
        removeSubEditor((ModelEditorPage)subEditors.get(theIndex));
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#setLabelProvider(org.eclipse.jface.viewers.ILabelProvider)
     */
    public void setLabelProvider(ILabelProvider theProvider) {
        // set provider on each sub-editor
        for (int size = subEditors.size(), i = 0; i < size; i++) {
            ModelEditorPage subEditor = (ModelEditorPage)subEditors.get(i);
            subEditor.setLabelProvider(theProvider);
        }
    }

    /* (non-Javadoc
     * @see org.eclipse.ui.IWorkbenchPart#removePropertyListener(org.eclipse.ui.IPropertyListener)
     */
    @Override
    public void removePropertyListener(IPropertyListener theListener) {
        // remove from existing subeditors
        for (int size = subEditors.size(), i = 0; i < size; i++) {
            ModelEditorPage editor = (ModelEditorPage)subEditors.get(i);
            editor.removePropertyListener(theListener);
        }
        
        // remove from our list
        propertyListeners.remove(theListener);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPart#setFocus()
     */
    @Override
    public void setFocus() {
        // set focus on first sub-editor
        if (!subEditors.isEmpty()) {
            ModelEditorPage subEditor = (ModelEditorPage)subEditors.get(0);
            subEditor.setFocus();
        }
    }
    
}
