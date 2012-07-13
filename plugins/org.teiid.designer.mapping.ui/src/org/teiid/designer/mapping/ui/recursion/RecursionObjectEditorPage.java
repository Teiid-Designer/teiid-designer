/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.mapping.ui.recursion;

import java.util.Collections;
import java.util.EventObject;
import java.util.List;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;
import org.teiid.core.event.EventObjectListener;
import org.teiid.designer.core.notification.util.NotificationUtilities;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.metamodels.transformation.MappingClass;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.transformation.ui.actions.ITransformationDiagramActionConstants;
import org.teiid.designer.ui.editors.ModelEditor;
import org.teiid.designer.ui.editors.ModelObjectEditorPage;
import org.teiid.designer.ui.editors.MultiPageModelEditor;
import org.teiid.designer.ui.viewsupport.ModelUtilities;


/**
 * RecursionObjectEditorPage is the class for editing Recursion info in mapping classes.
 */
public class RecursionObjectEditorPage
  implements ModelObjectEditorPage, INotifyChangedListener,
             UiConstants, ITransformationDiagramActionConstants,
             IAdaptable, EventObjectListener {


    private static final String TITLE_TEXT = org.teiid.designer.mapping.ui.UiConstants.Util.getString("RecursionObjectEditorPage.title.text"); //$NON-NLS-1$
    private static final String TITLE_TOOLTIP = org.teiid.designer.mapping.ui.UiConstants.Util.getString("RecursionObjectEditorPage.title.toolTip"); //$NON-NLS-1$        
    private static final String COLON = ": ";  //$NON-NLS-1$

    private RecursionPanel pnlRecursionPanel;
    private MappingClass mcRecursiveObject;
    private RecursionObject roRecursionObject;
    private ModelEditor parentModelEditor;
        
    private boolean isActive = false;
    
    /** 
     * @see org.teiid.designer.ui.editors.ModelObjectEditorPage#canClose()
     * @since 5.0.1
     */
    @Override
	public boolean canClose() {
        return true;
    }
    
    /* (non-Javadoc)    
     * @See org.teiid.designer.ui.editors.ModelObjectEditor#createControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
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
     * @See org.teiid.designer.ui.editors.ModelObjectEditor#getControl()
     */
    @Override
	public Control getControl() {
        return pnlRecursionPanel;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.ui.editors.ModelObjectEditor#getTitle()
     */
    @Override
	public String getTitle() {
        return TITLE_TEXT + COLON + roRecursionObject.getMappingClass().getName(); 
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.ui.editors.ModelObjectEditor#getTitleToolTip()
     */
    @Override
	public String getTitleToolTip() {
        return TITLE_TOOLTIP;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.ui.editors.ModelObjectEditor#getTitleImage()
     */
    @Override
	public Image getTitleImage() {
        return null;
    }

    @Override
	public boolean isDirty() {
        return false;
    }

    /**
     * @see org.teiid.designer.ui.editors.ModelObjectEditorPage#canEdit(java.lang.Object, org.eclipse.ui.IEditorPart)
     * @since 5.0.1
     */
    @Override
	public boolean canEdit(Object modelObject,
                           IEditorPart editor) {
        if ( modelObject != null &&  modelObject instanceof MappingClass ) {
            mcRecursiveObject = (MappingClass)modelObject;            
            return ((MappingClass)modelObject).isRecursionAllowed();
        }
        return false;
    }

    /**
     * @See org.teiid.designer.ui.editors.ModelObjectEditor#edit(org.eclipse.emf.ecore.EObject)
     */
    @Override
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
     * @See org.teiid.designer.ui.editors.ModelObjectEditor#deactivate()
     */
    @Override
	public boolean deactivate() {
        return true;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.ui.editors.ModelObjectEditor#addPropertyListener(org.eclipse.ui.IPropertyListener)
     */
    @Override
	public void addPropertyListener(IPropertyListener listener) {

    }

    /* (non-Javadoc)
     * @See org.teiid.designer.ui.editors.ModelObjectEditor#removePropertyListener(org.eclipse.ui.IPropertyListener)
     */
    @Override
	public void removePropertyListener(IPropertyListener listener) {

    }

    /* (non-Javadoc)    jhTODO: leave the notifyChanged listening
     *                          here in the controller (RecursionObjectEditorPage).
     * @see org.eclipse.emf.edit.provider.INotifyChangedListener#notifyChanged(org.eclipse.emf.common.notify.Notification)
     */
    @Override
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
     * @See org.teiid.designer.ui.editors.ModelObjectEditor#contributeToolbarActions(org.eclipse.jface.action.ToolBarManager)
     */
    @Override
	public void contributeExportedActions( IMenuManager menu ) {
        // jhTODO: do we have any actions?  I do not think so...   
        if(menu == null) return;
    }

    /**
     *  
     * @see org.teiid.designer.ui.editors.IEditorActionExporter#getAdditionalModelingActions(org.eclipse.jface.viewers.ISelection)
     * @since 5.0
     */
    @Override
	public List<IAction> getAdditionalModelingActions( ISelection selection ) {
        // jhTODO: do we have any actions?  I do not think so...   
        return Collections.EMPTY_LIST;
    }
    
    /* (non-Javadoc)
     * @See org.teiid.designer.ui.editors.ModelObjectEditor#contributeToolbarActions(org.eclipse.jface.action.ToolBarManager)
     */
    @Override
	public void contributeToolbarActions(ToolBarManager toolBarMgr) {
//        System.out.println("[RecursionObjectEditorPage.contributeToolbarActions] TOP"); //$NON-NLS-1$

        if(toolBarMgr == null) return;
        
//        System.out.println("[RecursionObjectEditorPage.contributeToolbarActions] About to call the panel's contributeToobarActions"); //$NON-NLS-1$
        
        pnlRecursionPanel.contributeToolbarActions( toolBarMgr );        
    }    

    /* (non-Javadoc)
     * @See org.teiid.designer.ui.editors.ModelObjectEditorPage#doSave()
     */
    @Override
	public void doSave(boolean isClosing) {
        // there's no difference to this panel between isClosing true and false
        deactivate();
    }
    
    @Override
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
     * @see org.teiid.designer.ui.editors.ModelObjectEditorPage#updateReadOnlyState()
     * @since 4.2
     */
    @Override
	public void updateReadOnlyState() {
    }

    /* (non-Javadoc)
     *  
     * @See org.teiid.designer.ui.editors.ModelObjectEditorPage#isEditingObject(java.lang.Object)
     * @since 4.2
     */
    @Override
	public boolean isEditingObject(Object modelObject) {
        if( mcRecursiveObject != null && modelObject instanceof MappingClass ) {
            if( modelObject.equals(mcRecursiveObject))
                return true;
        }
        return false;
    }
    
    /**
     *  
     * @see org.teiid.designer.ui.editors.ModelObjectEditorPage#getEditableObject(java.lang.Object)
     * @since 4.2
     */
    @Override
	public Object getEditableObject(Object modelObject) {
        if (modelObject != null && modelObject instanceof MappingClass) {
            if (((MappingClass)modelObject).isRecursionAllowed())
                return modelObject;
        }
        
        return null;
    }
    
    /**
     *  
     * @see org.teiid.designer.ui.editors.ModelObjectEditorPage#isResourceValid()
     * @since 4.2
     */
    @Override
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
    @Override
	public void processEvent(EventObject e) {
    }
    
    /**
     * Does nothing.
     *  
     * @see org.teiid.designer.ui.editors.ModelObjectEditorPage#initialize(org.teiid.designer.ui.editors.MultiPageModelEditor)
     * @since 5.0.1
     */
    @Override
	public void initialize(MultiPageModelEditor editor) {
    	if( editor instanceof ModelEditor ) {
    		this.parentModelEditor = (ModelEditor)editor;
    	}
    }
    
    /**
     * Does nothing.
     *  
     * @see org.teiid.designer.ui.editors.ModelObjectEditorPage#setOverride(org.teiid.designer.ui.editors.ModelObjectEditorPage)
     * @since 5.0.1
     */
    @Override
	public void setOverride(ModelObjectEditorPage editor) {
    }
    
    @Override
	public ModelEditor getParentModelEditor() {
    	return this.parentModelEditor;
    }
}
