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

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;

import org.eclipse.emf.edit.provider.INotifyChangedListener;

import com.metamatrix.core.event.EventObjectListener;

/**
 * ModelEditorPage is the interface that all Contributors to the multi-page ModelEditor must implement. The interface defines the
 * integration behavior between the active ModelEditorPage and other open views, and cross-communication between the set of
 * ModelEditorPages within the ModelEditor multi-page editor.
 */
public interface ModelEditorPage extends
                                IEditorPart,
                                EventObjectListener {

    /**
     * <p>
     * Determine if this Editor can display the specified IEditorInput. Typically the input is a metadata model, and this method
     * gives the Editor a chance to inspect the model and determine if it is of a type that it can display. For example, the
     * Transformation Table Editor class will only return true when it detects that the input model is a virtual model which can
     * contain transformations.
     * </p>
     * <p>
     * This method will be called before the call to <code>init</code>.
     * 
     * @param input
     *            the metadata model that will be displayed in this Editor if the method returns true.
     * @return true if this editor wishes to be displayed in the multi-page ModelEditor, false if it is not applicatble to the
     *         specified input and should not be used.
     */
    boolean canDisplay(IEditorInput input);

    /**
     * <p>
     * Determine if this Editor can open the specified model object. This method is called in when any object inside this editor's
     * model resource is double clicked from the Outline or other View, indicating the desire to activate and focus a
     * ModelEditorPage to the input object.
     * </p>
     * <p>
     * The implementation of this method should return true if the ModelEditorPage wishes to be considered the "default" editor
     * page for this type of object. The editor may or may not be the active editor when this method is called.
     * </p>
     * <p>
     * No action should be taken to show the input object, and no state should be maintained; if the method returns true, then
     * openContext will subsequently be called.
     * </p>
     * 
     * @return true if this ModelEditorPage can open the object; if so, openContext() may be called.
     */
    boolean canOpenContext(Object input);

    /**
     * <p>
     * Open and display the specified model object in this ModelEditorPage. When this method is called, this ModelEditorPage will
     * be the active editor page in the ModelEditor.
     * </p>
     */
    void openContext(Object input);

    /**
     * <p>
     * Open and display the specified model object in this ModelEditorPage. When this method is called, this ModelEditorPage will
     * be the active editor page in the ModelEditor.
     * </p>
     */
    void openContext(Object input,
                     boolean forceRefresh);

    /**
     * <p>
     * Initialize ModelEditorPage. This method provides each page the ability to customize how it's constructed. In the case of
     * OperationsEditorPage, the Object Editor Page needs to get initialized.
     * </p>
     * @param input
     * @param autoSelect
     * @since 5.0.2
     */
    void initializeEditorPage();

    /**
     * Gets the SWT control for this editor.
     * 
     * @return the control
     */
    Control getControl();

    /**
     * Get an ISelectionProvider that can be registered with the platform to generate ISelection events containing model objects.
     * 
     * @return an ISelectionProvider that will fire selection events when this ModelEditorPage is the active editor. May return
     *         null if the editor does not provide selection events.
     */
    ISelectionProvider getModelObjectSelectionProvider();

    /**
     * Get an ISelectionChangedListener for this editor that wishes to listen for selection events that occur in other Modeler
     * Views. The listener will receive events only when this ModelEditorPage is the active editor. ModelEditorPage
     * implementations are expected to display selected items when they receive selection events via this listener, provided that
     * the object is visible in their display. Synchronizaiton of this listener with the external views may be controlled by the
     * ModelEditor.
     * 
     * @return an ISelectionChangedListener, or null if this editor does not display any model object selection.
     */
    ISelectionChangedListener getModelObjectSelectionChangedListener();

    /**
     * Get the ActionBarContributor for this editor page, if one exists. The contributor will be activated and deactivated in
     * concert with the page.
     * 
     * @return an AbstractModelEditorPageActionBarContributor for this ModelEditorPage; null if one is not necessary.
     */
    AbstractModelEditorPageActionBarContributor getActionBarContributor();

    /**
     * Sets an ILabelProvider onto this editor for rendering icons and text for EObjects. This will be called immediately after
     * the extension is instansiated.
     */
    void setLabelProvider(ILabelProvider provider);

    /**
     * Obtains a change listener that will be hooked up with the UI for listening to model object change events.
     * 
     * @return
     */
    INotifyChangedListener getNotifyChangedListener();

    /**
     * Obtains an additional control for the Outline PageBook, if one exists for this ModelEditorPage
     * 
     * @return an outline contribution if one exists; otherwise null
     */
    ModelEditorPageOutline getOutlineContribution();

    /**
     * method that the model editor can use during setFocus() when it touches the resource file and verifies the read-only state
     * of the file. We have no other way to update the editor.
     * 
     * @param isReadOnly
     */
    void updateReadOnlyState(boolean isReadOnly);

    /**
     * method that model editor can use during construction to set the initial title to be displayed on page tab.
     * 
     * @param title
     */
    void setTitleText(String title);

    /**
     * Method that model editor can use to give uninitialized pages the chance to clean things up. (i.e cached objects, Objects
     * with Threads (like Timer)
     * 
     * @since 4.2
     */
    void preDispose();

    /**
     * Method that can be called on a model editor page to allow it to do any post-display operations. Model Object Editors, for
     * instance, may be opened after the editor has been opened. This gives the diagram editor, for instance, the chance to reveal
     * an object because the final scrolled viewport would have changed.
     * 
     * @since 4.2
     */
    void openComplete();

    /**
     * @return True if this editor page should be selected first after the editor is first opened and when editing the specified
     *         input. If all pages return false, the first page will be selected.
     * @since 5.0.1
     */
    boolean isSelectedFirst(IEditorInput input);
}
