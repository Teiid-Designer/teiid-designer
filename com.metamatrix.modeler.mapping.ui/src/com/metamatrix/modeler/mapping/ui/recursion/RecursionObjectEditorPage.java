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

package com.metamatrix.modeler.mapping.ui.recursion;

import java.util.Collections;
import java.util.EventObject;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.edit.provider.INotifyChangedListener;

import com.metamatrix.core.event.EventObjectListener;
import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.modeler.core.notification.util.NotificationUtilities;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.ui.editors.MultiPageModelEditor;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.transformation.ui.actions.ITransformationDiagramActionConstants;
import com.metamatrix.modeler.ui.editors.ModelObjectEditorPage;

/**
 * RecursionObjectEditorPage is the class for editing Recursion info in mapping classes.
 */
public class RecursionObjectEditorPage
  implements ModelObjectEditorPage, INotifyChangedListener,
             UiConstants, ITransformationDiagramActionConstants,
             IAdaptable, EventObjectListener {


    private static final String TITLE_TEXT = com.metamatrix.modeler.mapping.ui.UiConstants.Util.getString("RecursionObjectEditorPage.title.text"); //$NON-NLS-1$
    private static final String TITLE_TOOLTIP = com.metamatrix.modeler.mapping.ui.UiConstants.Util.getString("RecursionObjectEditorPage.title.toolTip"); //$NON-NLS-1$        
    private static final String COLON = ": ";  //$NON-NLS-1$

    private RecursionPanel pnlRecursionPanel;
    private MappingClass mcRecursiveObject;
    private RecursionObject roRecursionObject;
        
    private boolean isActive = false;
    
    /** 
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#canClose()
     * @since 5.0.1
     */
    public boolean canClose() {
        return true;
    }
    
    /* (non-Javadoc)    
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditor#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
//        System.out.println("[RecursionObjectEditorPage.createControl]"); //$NON-NLS-1$
        
        if ( mcRecursiveObject == null ) {
        
            System.out.println("[RecursionObjectEditorPage.createControl] mcRecursiveObject is NULL"); //$NON-NLS-1$
        }
        roRecursionObject = new RecursionObject( mcRecursiveObject );
        pnlRecursionPanel  = new RecursionPanel( parent, roRecursionObject );
        
//       WHY DO WE NEED THIS LINE?: 
//        pnlRecursionPanel.setBusinessObject( mcRecursiveObject );
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditor#getControl()
     */
    public Control getControl() {
        return pnlRecursionPanel;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditor#getTitle()
     */
    public String getTitle() {
        return TITLE_TEXT + COLON + roRecursionObject.getMappingClass().getName(); 
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditor#getTitleToolTip()
     */
    public String getTitleToolTip() {
        return TITLE_TOOLTIP;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditor#getTitleImage()
     */
    public Image getTitleImage() {
        return null;
    }

    public boolean isDirty() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#canEdit(java.lang.Object, org.eclipse.ui.IEditorPart)
     * @since 5.0.1
     */
    public boolean canEdit(Object modelObject,
                           IEditorPart editor) {
        if ( modelObject != null &&  modelObject instanceof MappingClass ) {
            mcRecursiveObject = (MappingClass)modelObject;            
            return ((MappingClass)modelObject).isRecursionAllowed();
        }
        return false;
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditor#edit(org.eclipse.emf.ecore.EObject)
     */
    public void edit( Object modelObject ) {
        mcRecursiveObject = (MappingClass)modelObject;
        roRecursionObject = new RecursionObject( mcRecursiveObject );            
        pnlRecursionPanel.setBusinessObject( roRecursionObject );
    }
    
    public RecursionObject getRecursionObject(  Object modelObject  ) {
        if ( roRecursionObject == null ) {
            mcRecursiveObject = (MappingClass)modelObject;
            roRecursionObject = new RecursionObject( mcRecursiveObject );            
        }
        return roRecursionObject;
    }
    
    
    public String getObjectText() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditor#deactivate()
     */
    public boolean deactivate() {
        return true;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditor#addPropertyListener(org.eclipse.ui.IPropertyListener)
     */
    public void addPropertyListener(IPropertyListener listener) {

    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditor#removePropertyListener(org.eclipse.ui.IPropertyListener)
     */
    public void removePropertyListener(IPropertyListener listener) {

    }

    /* (non-Javadoc)    jhTODO: leave the notifyChanged listening
     *                          here in the controller (RecursionObjectEditorPage).
     * @see org.eclipse.emf.edit.provider.INotifyChangedListener#notifyChanged(org.eclipse.emf.common.notify.Notification)
     */
    public void notifyChanged(Notification notification) {
        if( shouldProcessNotification(notification) && NotificationUtilities.isChanged(notification) ) {
            update();
        }
    }
    
    private void update() {

        if( isActive ) {
////            icoChoiceObject.refreshFromMetadata();            
////            pnlRecursionPanel.refreshFromBusinessObject();            
        }
    }



    
    private boolean shouldProcessNotification(Notification notification) {
////        final Object notifier = ModelerCore.getModelEditor().getChangedObject(notification);

////        //Object changedFeature = notification.getFeature();
////        if( notifier != null && NotificationUtilities.isEObjectNotifier(notification) ) {
////            EObject eObj = NotificationUtilities.getEObject(notification);
////            // Respond to changes in the Current Mapping Root
////            if(eObj instanceof TransformationMappingRoot) {
////                TransformationMappingRoot notifierMappingRoot = (TransformationMappingRoot)eObj;
////                if(notifierMappingRoot.equals(currentMappingRoot)) {
////                    return true;
////                }
////            } else if(eObj instanceof SqlTransformation) {
////                SqlTransformation notifierSqlTransformation = (SqlTransformation)notifier;
////                SqlTransformation currentSqlTransformation = (SqlTransformation)TransformationHelper.getMappingHelper(currentMappingRoot);
////                if(notifierSqlTransformation!=null && notifierSqlTransformation.equals(currentSqlTransformation)) {
////                    return true;
////                }
////            }
////        }
        return true;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditor#contributeToolbarActions(org.eclipse.jface.action.ToolBarManager)
     */
    public void contributeExportedActions( IMenuManager menu ) {
        // jhTODO: do we have any actions?  I do not think so...   
        if(menu == null) return;
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
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditor#contributeToolbarActions(org.eclipse.jface.action.ToolBarManager)
     */
    public void contributeToolbarActions(ToolBarManager toolBarMgr) {
//        System.out.println("[RecursionObjectEditorPage.contributeToolbarActions] TOP"); //$NON-NLS-1$

        if(toolBarMgr == null) return;
        
//        System.out.println("[RecursionObjectEditorPage.contributeToolbarActions] About to call the panel's contributeToobarActions"); //$NON-NLS-1$
        
        pnlRecursionPanel.contributeToolbarActions( toolBarMgr );        
    }    

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#doSave()
     */
    public void doSave(boolean isClosing) {
        // there's no difference to this panel between isClosing true and false
        deactivate();
    }
    
    public Object getAdapter( Class key ) {
//        System.out.println("[TranformationObjectEditorPage.getAdapter]"); //$NON-NLS-1$ 
        Object oResult = null;
//        if (key.equals(IFindReplaceTarget.class)) {
//            if ( hasFocus() ) {            
//                oResult = getCurrentSqlEditor().getTextViewer().getFindReplaceTarget();
//            }                       
//        }
//        
//        if ( StyledText.class.equals( key ) ) {
//            oResult = getTextWidget();
//        }   
                     
        return oResult;         
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
        if( mcRecursiveObject != null && modelObject instanceof MappingClass ) {
            if( modelObject.equals(mcRecursiveObject))
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
        if (modelObject != null && modelObject instanceof MappingClass) {
            if (((MappingClass)modelObject).isRecursionAllowed())
                return modelObject;
        }
        
        return null;
    }
    
    /**
     *  
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#isResourceValid()
     * @since 4.2
     */
    public boolean isResourceValid() {
        if( mcRecursiveObject != null ) {
            ModelResource mr = ModelUtilities.getModelResourceForModelObject(mcRecursiveObject);
            if( mr != null )
                return true;
        }
        return false;
    }

    /**
     * Method that handles Events from the SqlEditorPanel.  
     * @param e the EventObject
     */
    public void processEvent(EventObject e) {
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
