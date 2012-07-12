/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.xml.ui.editor;

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
import org.teiid.core.event.EventObjectListener;
import org.teiid.designer.core.notification.util.NotificationUtilities;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.diagram.ui.editor.DiagramController;
import org.teiid.designer.diagram.ui.editor.DiagramEditor;
import org.teiid.designer.mapping.choice.IChoiceObject;
import org.teiid.designer.mapping.factory.ChoiceFactoryManager;
import org.teiid.designer.mapping.factory.DefaultMappableTree;
import org.teiid.designer.mapping.factory.IChoiceFactory;
import org.teiid.designer.mapping.factory.IMappableTree;
import org.teiid.designer.mapping.factory.TreeMappingAdapter;
import org.teiid.designer.mapping.ui.UiPlugin;
import org.teiid.designer.mapping.ui.choice.ChoicePanel;
import org.teiid.designer.mapping.ui.editor.MappingDiagramController;
import org.teiid.designer.metamodels.xml.XmlChoice;
import org.teiid.designer.metamodels.xml.provider.XmlChoiceItemProvider;
import org.teiid.designer.metamodels.xml.provider.XmlDocumentItemProviderAdapterFactory;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.transformation.ui.actions.ITransformationDiagramActionConstants;
import org.teiid.designer.ui.editors.ModelEditor;
import org.teiid.designer.ui.editors.ModelEditorPage;
import org.teiid.designer.ui.editors.ModelObjectEditorPage;
import org.teiid.designer.ui.editors.MultiPageModelEditor;
import org.teiid.designer.ui.viewsupport.ModelUtilities;


/**
 * InputSetObjectEditorPage is the class for editing Transformation Objects.
 */
public class XmlChoiceObjectEditorPage
  implements ModelObjectEditorPage, INotifyChangedListener,
             UiConstants, ITransformationDiagramActionConstants,
             IAdaptable, EventObjectListener {


    private static final String TITLE_TEXT = org.teiid.designer.mapping.ui.UiConstants.Util.getString("ChoicePanel.title.text"); //$NON-NLS-1$
    private static final String TITLE_TOOLTIP = org.teiid.designer.mapping.ui.UiConstants.Util.getString("ChoicePanel.title.toolTip"); //$NON-NLS-1$        
    private static final String COLON = ": "; //$NON-NLS-1$ 
    private static final String SLASH = " / "; //$NON-NLS-1$ 
    private ChoicePanel pnlChoicePanel;
    private IChoiceObject icoChoiceObject;
    private ModelEditor parentModelEditor;
    
    /** 
     * @see org.teiid.designer.ui.editors.ModelObjectEditorPage#canClose()
     * @since 5.0.1
     */
    public boolean canClose() {
        return true;
    }
    
    /* (non-Javadoc)    
     * @See org.teiid.designer.ui.editors.ModelObjectEditor#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
        pnlChoicePanel  = new ChoicePanel( parent, icoChoiceObject );
        //       WHY DO WE NEED THIS LINE?: 
        pnlChoicePanel.setBusinessObject( icoChoiceObject );
        
        //Register to listen for Change Notifications
        ModelUtilities.addNotifyChangedListener( this );

    }
    
    /* (non-Javadoc)
     * @See org.teiid.designer.ui.editors.ModelObjectEditor#getControl()
     */
    public Control getControl() {
        return pnlChoicePanel;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.ui.editors.ModelObjectEditor#getTitle()
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
     * @See org.teiid.designer.ui.editors.ModelObjectEditor#getTitleToolTip()
     */
    public String getTitleToolTip() {
        return TITLE_TOOLTIP;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.ui.editors.ModelObjectEditor#getTitleImage()
     */
    public Image getTitleImage() {
        return null;
    }

    public boolean isDirty() {
        return false;
    }

    /**
     * @see org.teiid.designer.ui.editors.ModelObjectEditorPage#canEdit(java.lang.Object, org.eclipse.ui.IEditorPart)
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
     * @See org.teiid.designer.ui.editors.ModelObjectEditor#edit(org.eclipse.emf.ecore.EObject)
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
     * @See org.teiid.designer.ui.editors.ModelObjectEditor#deactivate()
     */
    public boolean deactivate() {
        //Unregister for Change Notifications
        ModelUtilities.removeNotifyChangedListener( this );

        return true;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.ui.editors.ModelObjectEditor#addPropertyListener(org.eclipse.ui.IPropertyListener)
     */
    public void addPropertyListener(IPropertyListener listener) {

    }

    /* (non-Javadoc)
     * @See org.teiid.designer.ui.editors.ModelObjectEditor#removePropertyListener(org.eclipse.ui.IPropertyListener)
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
     * @See org.teiid.designer.ui.editors.ModelObjectEditor#contributeToolbarActions(org.eclipse.jface.action.ToolBarManager)
     */
    public void contributeExportedActions( IMenuManager menu ) {
        // jhTODO: do we have any actions?  I do not think so...   
        if(menu == null) return;
    }

    
    /**
     *  
     * @see org.teiid.designer.ui.editors.IEditorActionExporter#getAdditionalModelingActions(org.eclipse.jface.viewers.ISelection)
     * @since 5.0
     */
    public List<IAction> getAdditionalModelingActions( ISelection selection ) {
        // jhTODO: do we have any actions?  I do not think so...   
        return Collections.EMPTY_LIST;
    }
    
    /* (non-Javadoc)
     * @See org.teiid.designer.ui.editors.ModelObjectEditor#contributeToolbarActions(org.eclipse.jface.action.ToolBarManager)
     */
    public void contributeToolbarActions(ToolBarManager toolBarMgr) {
//        System.out.println("[XmlChoiceObjectEditorPage.contributeToolbarActions] TOP"); //$NON-NLS-1$

        if(toolBarMgr == null) return;
        
//        System.out.println("[XmlChoiceObjectEditorPage.contributeToolbarActions] About to call the panel's contributeToobarActions"); //$NON-NLS-1$
        
        pnlChoicePanel.contributeToolbarActions( toolBarMgr );        
    }    

    /* (non-Javadoc)
     * @See org.teiid.designer.ui.editors.ModelObjectEditorPage#doSave()
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
     * @see org.teiid.designer.ui.editors.ModelObjectEditorPage#updateReadOnlyState()
     * @since 4.2
     */
    public void updateReadOnlyState() {
    }

    /* (non-Javadoc)
     *  
     * @See org.teiid.designer.ui.editors.ModelObjectEditorPage#isEditingObject(java.lang.Object)
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
     * @see org.teiid.designer.ui.editors.ModelObjectEditorPage#getEditableObject(java.lang.Object)
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
     * @see org.teiid.designer.ui.editors.ModelObjectEditorPage#isResourceValid()
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
     * @see org.teiid.designer.ui.editors.ModelObjectEditorPage#initialize(org.teiid.designer.ui.editors.MultiPageModelEditor)
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
     * @see org.teiid.designer.ui.editors.ModelObjectEditorPage#setOverride(org.teiid.designer.ui.editors.ModelObjectEditorPage)
     * @since 5.0.1
     */
    public void setOverride(ModelObjectEditorPage editor) {
    }
    
    public ModelEditor getParentModelEditor() {
    	return this.parentModelEditor;
    }
}
