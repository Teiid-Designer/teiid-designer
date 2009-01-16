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

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;

import com.metamatrix.modeler.internal.ui.editors.MultiPageModelEditor;

/**
 * ModelObjectEditor is an interface for classes to display a complex control that edits a particular type of object.
 */
public interface ModelObjectEditorPage extends
                                      IEditorActionExporter {

    // ===========================================================================================================================
    // Methods

    void addPropertyListener(IPropertyListener listener);

    /**
     * @return True if this model object editor can be closed.
     * @since 5.0.1
     */
    boolean canClose();

    /**
     * Determine if this ModelObjectEditor can edit the specified model object selected in the specified active model editor page.
     * Implementations should not take any action based on this call, instead wait for the edit() method to be called.
     * 
     * @param modelObject
     * @param editor
     *            The active model editor page
     * @return True if this model object editor page can edit the specified object.
     * @see #setOverride(ModelObjectEditorPage)
     */
    boolean canEdit(Object modelObject,
                    IEditorPart editor);

    /**
     * Provides the ModelObjectEditor panel's ToolBarManager to this class so that it may contribute actions to it. This method
     * will be called every time the getControl method is called. Any actions contributed to the toolbar will be removed after
     * deactivate is called, provided it returns true.
     * 
     * @param toolBarMgr
     *            the toolBarMgr for the ModelObjectEditor panel. The manager will be updated by the framework after this method
     *            returns.
     */
    void contributeToolbarActions(ToolBarManager toolBarMgr);

    /**
     * Construct the Control for this ModelObjectEditor. This method will be called only once.
     * 
     * @param parent
     *            the parent Composite to use for constructing the Control.
     */
    void createControl(Composite parent);

    /**
     * Notifies this ModelObjectEditor that it is being deactivated because either this editor is being replaced by another, or
     * the user has closed the panel. The implementation may veto the deactivation by returning false.
     * 
     * @return true to continue with deactivation, false to cancel.
     */
    boolean deactivate();

    /**
     * <p>
     * Called by the ModelEditor to notifiy implementations that the model is about to be saved. If a temporary state is being
     * held in this page, then the state should be saved or abandoned when this method is called. Typically a page will launch a
     * dialog to notify the user that changes are pending and request the user decide to save ? yes : no
     * <p>
     * This method differs from deactivate in that 1) there is no opportunity to veto, and 2) the panel may or may not be
     * destroyed after this call.
     * </p>
     * 
     * @param isClosing
     *            true if the editor is closing, false if this is a simple save operation and the panel will remain active.
     */
    void doSave(boolean isClosing);

    /**
     * Populate this class's Control with the specified model object.
     * 
     * @param modelObject
     */
    void edit(Object modelObject);

    /**
     * Return ths Control to be used in the ModelObjectEditor panel. This method will be called repeatedly, immediately after the
     * call to edit(EObject modelObject)
     * 
     * @return
     */
    Control getControl();

    /**
     * method used as a generic way to ask an editor for an editable object Initially designed to handle the "I have a
     * transformation diagram, and I want to edit the transformation at the same time" case.
     * 
     * @return boolean
     * @since 4.2
     */
    Object getEditableObject(Object modelObject);

    /**
     * Return the Title text for this ModelObjectEditor. If the title of this instance changes, the instance can call
     * propertyChanged on the IPropertyListener with propertyId == IEditorPart.PROP_TITLE
     * 
     * @return
     */
    String getTitle();

    /**
     * Return the image to be displayed on the title bar for this ModelObjectEditor.
     * 
     * @return
     */
    Image getTitleImage();

    /**
     * Return the tooltip text for this ModelObjectEditor.
     * 
     * @return
     */
    String getTitleToolTip();

    /**
     * Called by specified editor immediately after all object editors have been created.
     *  
     * @param editor
     * @since 5.0.1
     */
    void initialize(MultiPageModelEditor editor);
    
    /**
     * Return true if this ModelObjectEditorPage is holding a state that has not yet been saved on the model. When this state
     * changes, this instance should call propertyChanged on the IPropertyListener with propertyId == IEditorPart.PROP_DIRTY
     * 
     * @return
     */
    boolean isDirty();

    /**
     * method used as a generic way to ask an editor if it's currently editing an object
     * 
     * @return boolean
     * @since 4.2
     */
    boolean isEditingObject(Object modelObject);

    /**
     * Method used to ask the current active editor if the editable object has a valid resource or not. Things like "Delete" will
     * nullify the ModelResource and we need to close the editor.
     * 
     * @return
     * @since 4.2
     */
    boolean isResourceValid();

    void removePropertyListener(IPropertyListener listener);

    /**
     * method that the model object editor can use during setFocus() when it touches the resource file and verifies the read-only
     * state of the file. We have no other way to update the editor.
     * 
     * @param isReadOnly
     */
    void updateReadOnlyState();

    /**
     * Sets the specified editor as able to override whether this editor can edit a particular object. If the override editor
     * returns <code>true</code> for {@link #canEdit(Object, IEditorPart)}, then this editor's
     * {@link #canEdit(Object, IEditorPart)} method should return <code>false</code>;
     * 
     * @param editor
     * @since 5.0.1
     */
    void setOverride(ModelObjectEditorPage editor);
}
