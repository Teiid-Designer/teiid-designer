/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.xml.ui.editor;

import java.util.Collections;
import java.util.EventObject;
import java.util.List;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
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
import org.eclipse.ui.IWorkbenchWindow;
import com.metamatrix.core.event.EventObjectListener;
import com.metamatrix.metamodels.xml.XmlChoice;
import com.metamatrix.metamodels.xml.provider.XmlChoiceItemProvider;
import com.metamatrix.metamodels.xml.provider.XmlDocumentItemProviderAdapterFactory;
import com.metamatrix.modeler.core.notification.util.NotificationUtilities;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.diagram.ui.editor.DiagramController;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditor;
import com.metamatrix.modeler.internal.mapping.factory.ChoiceFactoryManager;
import com.metamatrix.modeler.internal.mapping.factory.DefaultMappableTree;
import com.metamatrix.modeler.internal.mapping.factory.TreeMappingAdapter;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.modeler.internal.ui.editors.MultiPageModelEditor;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.mapping.choice.IChoiceObject;
import com.metamatrix.modeler.mapping.factory.IChoiceFactory;
import com.metamatrix.modeler.mapping.factory.IMappableTree;
import com.metamatrix.modeler.mapping.ui.UiPlugin;
import com.metamatrix.modeler.mapping.ui.choice.ChoicePanel;
import com.metamatrix.modeler.mapping.ui.editor.MappingDiagramController;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.transformation.ui.actions.ITransformationDiagramActionConstants;
import com.metamatrix.modeler.ui.editors.ModelEditorPage;
import com.metamatrix.modeler.ui.editors.ModelObjectEditorPage;

/**
 * InputSetObjectEditorPage is the class for editing Transformation Objects.
 */
public class XmlChoiceObjectEditorPage
  implements ModelObjectEditorPage, INotifyChangedListener,
             UiConstants, ITransformationDiagramActionConstants,
             IAdaptable, EventObjectListener {


    private static final String TITLE_TEXT = com.metamatrix.modeler.mapping.ui.UiConstants.Util.getString("ChoicePanel.title.text"); //$NON-NLS-1$
    private static final String TITLE_TOOLTIP = com.metamatrix.modeler.mapping.ui.UiConstants.Util.getString("ChoicePanel.title.toolTip"); //$NON-NLS-1$        
    private static final String COLON = ": "; //$NON-NLS-1$ 
    private static final String SLASH = " / "; //$NON-NLS-1$ 
    private ChoicePanel pnlChoicePanel;
    private IChoiceObject icoChoiceObject;
    private ModelEditor parentModelEditor;
    
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
        pnlChoicePanel  = new ChoicePanel( parent, icoChoiceObject );
        //       WHY DO WE NEED THIS LINE?: 
        pnlChoicePanel.setBusinessObject( icoChoiceObject );
        
        //Register to listen for Change Notifications
        ModelUtilities.addNotifyChangedListener( this );

    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditor#getControl()
     */
    public Control getControl() {
        return pnlChoicePanel;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditor#getTitle()
     */
    public String getTitle() {
        XmlDocumentItemProviderAdapterFactory xdipaf
            = new XmlDocumentItemProviderAdapterFactory();
        XmlChoiceItemProvider ipChoiceItemProvider 
            = (XmlChoiceItemProvider)xdipaf.createXmlChoiceAdapter();
        String sChoiceText = ipChoiceItemProvider.getText( icoChoiceObject.getChoice() );

        String sParentText = ModelUtilities.getEMFLabelProvider().getText( icoChoiceObject.getParent() );

        return TITLE_TEXT + COLON + sParentText + SLASH + sChoiceText; 
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
//        System.out.println("[XmlChoiceObjectEditorPage.canEdit] object is: " + modelObject ); //$NON-NLS-1$
//        System.out.println("[XmlChoiceObjectEditorPage.canEdit] object class is: " + modelObject.getClass().getName() ); //$NON-NLS-1$

        if ( modelObject != null &&  modelObject instanceof XmlChoice ) {
//            System.out.println("[XmlChoiceObjectEditorPage.canEdit] returning TRUE"); //$NON-NLS-1$
            icoChoiceObject = getChoiceObjectForInput( modelObject );
            if ( icoChoiceObject == null ) {                
//                System.out.println("[XmlChoiceObjectEditorPage.canEdit] icoChoiceObject == NULL!!!!!"); //$NON-NLS-1$
            }
            return true;
        }
//            System.out.println("[XmlChoiceObjectEditorPage.canEdit] returning FALSE"); //$NON-NLS-1$
        return false;

    }
    
    public void setMappingAdapters() {
        // obtain the root tree node that is being displayed in the mapping diagram
        IWorkbenchWindow window = UiPlugin.getDefault().getCurrentWorkbenchWindow();
        IEditorPart editorPart = window.getActivePage().getActiveEditor();
        if ( editorPart instanceof ModelEditor ) {
            ModelEditorPage editorPage = (ModelEditorPage)((ModelEditor)editorPart).getCurrentPage();
            if ( editorPage instanceof DiagramEditor ) {
                DiagramController controller = ((DiagramEditor) editorPage).getDiagramController();
                if ( controller instanceof MappingDiagramController ) {
                    // get the adapters from the diagram controller
                    TreeMappingAdapter mapping = ((MappingDiagramController) controller).getMappingAdapter();
                    IMappableTree mappableTree = ((MappingDiagramController) controller).getMappableTree();
                    pnlChoicePanel.setMappingAdapters(mapping, mappableTree);
                } else {
                    // need to get one somehow
                    EObject docRoot = icoChoiceObject.getRoot();
                    TreeMappingAdapter mapping = new TreeMappingAdapter(docRoot);
                    IMappableTree mappableTree = new DefaultMappableTree(docRoot);
                    pnlChoicePanel.setMappingAdapters(mapping, mappableTree);
                }
            }
        }
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditor#edit(org.eclipse.emf.ecore.EObject)
     */
    public void edit( Object modelObject ) {
        icoChoiceObject = getChoiceObjectForInput( modelObject );
        // jh fix for Defect 11991
        pnlChoicePanel.setBusinessObject(icoChoiceObject);
        setMappingAdapters();
    }
    
    private IChoiceObject getChoiceObjectForInput( Object modelObject ) {
        IChoiceObject ico = null;

        if ( modelObject instanceof EObject ) {
            EObject eo = (EObject)modelObject;
            
            IChoiceFactory icfFactory = ChoiceFactoryManager.getChoiceFactory( eo );
            
            if ( icfFactory != null ) {
                ico = icfFactory.createChoiceObject( eo );            
            }
        
        }
        return ico;       
    }
    
    public String getObjectText() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditor#deactivate()
     */
    public boolean deactivate() {
        //Unregister for Change Notifications
        ModelUtilities.removeNotifyChangedListener( this );

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
     *                          here in the controller (XmlChoiceObjectEditorPage).
     * @see org.eclipse.emf.edit.provider.INotifyChangedListener#notifyChanged(org.eclipse.emf.common.notify.Notification)
     */
    public void notifyChanged(Notification notification) {
////        System.out.println("[XmlChoiceObjectEditorPage.notifyChanged] TOP" ); //$NON-NLS-1$
        if( shouldProcessNotification(notification) && NotificationUtilities.isChanged(notification) ) {
            update();
        }
    }
    
    private void update() {
////        System.out.println("[XmlChoiceObjectEditorPage.update] TOP" ); //$NON-NLS-1$

//        if( isActive ) {
            pnlChoicePanel.refreshFromBusinessObject();            
//        }
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
//        System.out.println("[XmlChoiceObjectEditorPage.contributeToolbarActions] TOP"); //$NON-NLS-1$

        if(toolBarMgr == null) return;
        
//        System.out.println("[XmlChoiceObjectEditorPage.contributeToolbarActions] About to call the panel's contributeToobarActions"); //$NON-NLS-1$
        
        pnlChoicePanel.contributeToolbarActions( toolBarMgr );        
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
        if( icoChoiceObject != null && modelObject != null ) {
            IChoiceObject tempChoiceObject = getChoiceObjectForInput( modelObject );
            if( tempChoiceObject != null && tempChoiceObject.equals(icoChoiceObject))
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
        if (modelObject != null && modelObject instanceof XmlChoice) {
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
        if( icoChoiceObject != null ) {
            ModelResource mr = ModelUtilities.getModelResourceForModelObject(icoChoiceObject.getParent());
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
    	if( editor instanceof ModelEditor ) {
    		this.parentModelEditor = (ModelEditor)editor;
    	}
    }
    
    /**
     * Does nothing.
     *  
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#setOverride(com.metamatrix.modeler.ui.editors.ModelObjectEditorPage)
     * @since 5.0.1
     */
    public void setOverride(ModelObjectEditorPage editor) {
    }
    
    public ModelEditor getParentModelEditor() {
    	return this.parentModelEditor;
    }
}
