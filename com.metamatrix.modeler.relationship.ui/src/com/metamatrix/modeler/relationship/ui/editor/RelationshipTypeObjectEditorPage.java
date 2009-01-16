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

package com.metamatrix.modeler.relationship.ui.editor;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;

import com.metamatrix.metamodels.relationship.RelationshipType;
import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.ui.editors.MultiPageModelEditor;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.relationship.ui.UiConstants;
import com.metamatrix.modeler.ui.editors.ModelObjectEditorPage;
import com.metamatrix.ui.internal.util.WidgetFactory;


/**
 * RelationshipTypeObjectEditorPage
 */
public class RelationshipTypeObjectEditorPage implements ModelObjectEditorPage,
                                                         IStatusListener {

    private RelationshipType rtRelationshipTypeObject;

    private RelationshipTypePanel rpnlRelationshipType;

    private Composite editorComposite;
    private Composite pnlStatusLine;
    private ScrolledComposite scrolledComposite;

    private String FOUR_BLANKS_STRING = "   ";  //$NON-NLS-1$
//    private CLabel lblFiller1;

    private final String BASE_TITLE   
        = UiConstants.Util.getString("com.metamatrix.modeler.relationship.ui.editor.RelationshipTypeObjectEditorPage.baseTitle.title"); //$NON-NLS-1$
    
    /** 
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#canClose()
     * @since 5.0.1
     */
    public boolean canClose() {
        return true;
    }

    /**
     * Construct the Control for this ModelObjectEditor.  This method will be called
     * only once.
     * @param parent the parent Composite to use for constructing the Control.
     */
    public void createControl( Composite parent) {
     
        // 1. add outer panel to parent
        scrolledComposite  =  new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        scrolledComposite.setMinSize(400, 550);
        
        Composite child = new Composite(scrolledComposite, SWT.NONE);
        child.setLayout(new FillLayout());        
        
        scrolledComposite.setContent(child);
        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.setExpandVertical(true);        
        
        editorComposite =  new Composite(child, SWT.NONE);
        
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        gridLayout.marginWidth = gridLayout.marginHeight = 0;
        editorComposite.setLayout( gridLayout );
        
        // 2. add Rel Type panel to outer
        rpnlRelationshipType = new RelationshipTypePanel( editorComposite, rtRelationshipTypeObject, this );
        GridData gridData3 = new GridData( GridData.FILL_BOTH );
        gridData3.grabExcessVerticalSpace = true;
        rpnlRelationshipType.setLayoutData( gridData3 );
        
        // 3. add statusline panel to outer
        pnlStatusLine =  new Composite( editorComposite, SWT.BORDER );
        GridLayout gridLayout2 = new GridLayout();
        gridLayout2.numColumns = 1;
        gridLayout2.marginWidth = gridLayout2.marginHeight = 0;
        pnlStatusLine.setLayout( gridLayout2 );
        
        GridData gridData2 = new GridData( GridData.FILL_HORIZONTAL );
        gridData2.grabExcessVerticalSpace = false;
        pnlStatusLine.setLayoutData( gridData2 );
//        lblFiller1 = 
            WidgetFactory.createLabel( pnlStatusLine, FOUR_BLANKS_STRING );
        
       
        
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#getControl()
     */
    public Control getControl() {
        
        return scrolledComposite;        
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#getTitle()
     */
    public String getTitle() {
        
        String sTitle= BASE_TITLE;  
        if ( rtRelationshipTypeObject != null ) {
            sTitle += ": " + rtRelationshipTypeObject.getName();  //$NON-NLS-1$
        } 
        return sTitle;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#getTitleToolTip()
     */
    public String getTitleToolTip() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#getTitleImage()
     */
    public Image getTitleImage() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#canEdit(java.lang.Object, org.eclipse.ui.IEditorPart)
     * @since 5.0.1
     */
    public boolean canEdit(Object modelObject,
                           IEditorPart editor) {
//        System.out.println( "[RelationshipTypeObjectEditorPage.canEdit] about to return TRUE (forced) for: " + modelObject ); //$NON-NLS-1$
 
        return ( modelObject instanceof RelationshipType );
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#edit(java.lang.Object)
     */
    public void edit(Object modelObject) {
        if( modelObject instanceof RelationshipType ) {
            rtRelationshipTypeObject = (RelationshipType)modelObject;
            
            rpnlRelationshipType.setBusinessObject( rtRelationshipTypeObject );          
        }
    }

    public void setStatus( IStatus isStatus ) {
        if ( !isStatus.isOK() ) {
            // display the icon and message...
                    
        } else {
            pnlStatusLine.setVisible( false );
        }
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#deactivate()
     */
    public boolean deactivate() {
        return true;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#doSave(boolean)
     */
    public void doSave(boolean isClosing) {

    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#isDirty()
     */
    public boolean isDirty() {
        return false;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#addPropertyListener(org.eclipse.ui.IPropertyListener)
     */
    public void addPropertyListener(IPropertyListener listener) {

    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#removePropertyListener(org.eclipse.ui.IPropertyListener)
     */
    public void removePropertyListener(IPropertyListener listener) {

    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#contributeToolbarActions(org.eclipse.jface.action.ToolBarManager)
     */
    public void contributeToolbarActions(ToolBarManager toolBarMgr) {

    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.IEditorActionExporter#contributeExportedActions(org.eclipse.jface.action.IMenuManager)
     */
    public void contributeExportedActions(IMenuManager theMenuMgr) {

    }
    
    /**
     *  
     * @see com.metamatrix.modeler.ui.editors.IEditorActionExporter#getAdditionalModelingActions(org.eclipse.jface.viewers.ISelection)
     * @since 5.0
     */
    public List<IAction> getAdditionalModelingActions( ISelection selection ) {
        // jhTODO: do we have any actions?  I do not think so...   
        return Collections.EMPTY_LIST;
    }
    
    /** 
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#updateReadOnlyState()
     * @since 4.2
     */
    public void updateReadOnlyState() {
    }
    
    /* (non-Javadoc)
     *  
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#isEditingObject(java.lang.Object)
     * @since 4.2
     */
    public boolean isEditingObject(Object modelObject) {
        if( rtRelationshipTypeObject != null && modelObject instanceof MappingClass ) {
            if( modelObject.equals(rtRelationshipTypeObject))
                return true;
        }
        return false;
    }
    
    /**
     *  
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#getEditableObject(java.lang.Object)
     * @since 4.2
     */
    public Object getEditableObject(Object modelObject) {
        if( modelObject instanceof RelationshipType)
            return modelObject;
        
        return null;
    }
    
    /**
     *  
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#isResourceValid()
     * @since 4.2
     */
    public boolean isResourceValid() {
        if( rtRelationshipTypeObject != null ) {
            ModelResource mr = ModelUtilities.getModelResourceForModelObject(rtRelationshipTypeObject);
            if( mr != null )
                return true;
        }
        return false;
    }
    
    /**
     * Does nothing.
     *  
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#initialize(com.metamatrix.modeler.internal.ui.editors.MultiPageModelEditor)
     * @since 5.0.1
     */
    public void initialize(MultiPageModelEditor editor) {
    }
    
    /**
     * Does nothing.
     *  
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#setOverride(com.metamatrix.modeler.ui.editors.ModelObjectEditorPage)
     * @since 5.0.1
     */
    public void setOverride(ModelObjectEditorPage editor) {
    }
}
