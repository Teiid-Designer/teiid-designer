/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.mapping.ui.editor;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
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
import com.metamatrix.metamodels.transformation.InputBinding;
import com.metamatrix.metamodels.transformation.InputParameter;
import com.metamatrix.metamodels.transformation.InputSet;
import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.metamodels.transformation.MappingClassColumn;
import com.metamatrix.metamodels.transformation.MappingClassSet;
import com.metamatrix.modeler.core.notification.util.NotificationUtilities;
import com.metamatrix.modeler.core.transaction.SourcedNotification;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.diagram.ui.editor.DiagramController;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditor;
import com.metamatrix.modeler.internal.mapping.factory.DefaultMappableTree;
import com.metamatrix.modeler.internal.mapping.factory.TreeMappingAdapter;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.modeler.internal.ui.editors.MultiPageModelEditor;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.mapping.factory.IMappableTree;
import com.metamatrix.modeler.mapping.ui.UiPlugin;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.transformation.ui.actions.ITransformationDiagramActionConstants;
import com.metamatrix.modeler.ui.editors.ModelEditorPage;
import com.metamatrix.modeler.ui.editors.ModelObjectEditorPage;
import com.metamatrix.modeler.ui.viewsupport.StatusBarUpdater;

/**
 * InputSetObjectEditorPage is the class for editing ???.
 */
public class InputSetObjectEditorPage
    implements ModelObjectEditorPage, INotifyChangedListener, UiConstants, ITransformationDiagramActionConstants {

    private static final String TITLE_TEXT = com.metamatrix.modeler.mapping.ui.UiConstants.Util.getString("InputSetObjectEditorPage.title.text"); //$NON-NLS-1$
    private static final String TITLE_TOOLTIP = com.metamatrix.modeler.mapping.ui.UiConstants.Util.getString("InputSetObjectEditorPage.title.toolTip"); //$NON-NLS-1$        

    private MappingClass currentMappingClass;
    private InputSet inputSet;
    private InputSetPanel ispInputSetPanel;
    private InputSetAdapter isoInputSetObject;

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
    public void createControl( Composite parent ) {
        ispInputSetPanel = new InputSetPanel(parent);
        ModelUtilities.addNotifyChangedListener(this);
    }

    public void setMappingAdapters( InputSet inputSet ) {
        this.inputSet = inputSet;
        // obtain the root tree node that is being displayed in the mapping diagram
        IWorkbenchWindow window = UiPlugin.getDefault().getCurrentWorkbenchWindow();
        IEditorPart editorPart = window.getActivePage().getActiveEditor();
        if (editorPart instanceof ModelEditor) {
            ModelEditorPage editorPage = (ModelEditorPage)((ModelEditor)editorPart).getCurrentPage();
            if (editorPage instanceof DiagramEditor) {
                DiagramController controller = ((DiagramEditor)editorPage).getDiagramController();
                if (controller instanceof MappingDiagramController) {
                    // get the adapters from the diagram controller
                    TreeMappingAdapter mapping = ((MappingDiagramController)controller).getMappingAdapter();
                    IMappableTree mappableTree = ((MappingDiagramController)controller).getMappableTree();
                    ispInputSetPanel.setMappingAdapters(mapping, mappableTree);
                } else {
                    // need to get one somehow
                    EObject docRoot = inputSet.getMappingClass().getMappingClassSet().getTarget();
                    TreeMappingAdapter mapping = new TreeMappingAdapter(docRoot);
                    IMappableTree mappableTree = new DefaultMappableTree(docRoot);
                    ispInputSetPanel.setMappingAdapters(mapping, mappableTree);
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditor#getControl()
     */
    public Control getControl() {
        return ispInputSetPanel;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditor#getTitle()
     */
    public String getTitle() {
        return TITLE_TEXT;
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
        // this panel changes the model directly, so no state is held in the panel.
        return false;
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#canEdit(java.lang.Object, org.eclipse.ui.IEditorPart)
     * @since 5.0.1
     */
    public boolean canEdit( Object modelObject,
                            IEditorPart editor ) {
        return (modelObject instanceof InputSet || modelObject instanceof InputParameter);
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditor#edit(org.eclipse.emf.ecore.EObject)
     */
    public void edit( Object input ) {
        if (input instanceof InputSet) {
            setMappingAdapters((InputSet)input);
            currentMappingClass = ((InputSet)input).getMappingClass();
            isoInputSetObject = new InputSetAdapter(currentMappingClass);
            ispInputSetPanel.setBusinessObject(isoInputSetObject);
        } else if (input instanceof InputParameter) {
            EObject parent = ((InputParameter)input).getInputSet();
            if (parent != null) {
                setMappingAdapters((InputSet)parent);
                currentMappingClass = ((InputSet)parent).getMappingClass();
                isoInputSetObject = new InputSetAdapter(currentMappingClass);
                ispInputSetPanel.setBusinessObject(isoInputSetObject);
            }
        }
    }

    public String getObjectText() {
        return StatusBarUpdater.formatEObjectMessage(currentMappingClass);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditor#deactivate()
     */
    public boolean deactivate() {
        // this panel changes the model directly, so no state is held in the panel.
        // Defect 22290 reflects memory (leaks) issues within designer.
        // remove this listener when deactivated.
        ModelUtilities.removeNotifyChangedListener(this);
        return true;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditor#addPropertyListener(org.eclipse.ui.IPropertyListener)
     */
    public void addPropertyListener( IPropertyListener listener ) {

    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditor#removePropertyListener(org.eclipse.ui.IPropertyListener)
     */
    public void removePropertyListener( IPropertyListener listener ) {

    }

    /* (non-Javadoc)    
     * @see org.eclipse.emf.edit.provider.INotifyChangedListener#notifyChanged(org.eclipse.emf.common.notify.Notification)
     */
    public void notifyChanged( Notification notification ) {
        boolean doUpdate = false;
        // Need to break this down into SourceNotification too
        if (notification instanceof SourcedNotification) {
            Collection notifications = ((SourcedNotification)notification).getNotifications();
            Iterator iter = notifications.iterator();
            Notification nextNotification = null;

            while (iter.hasNext()) {
                nextNotification = (Notification)iter.next();
                if (shouldProcessNotification(nextNotification)) {
                    doUpdate = true;
                }
            }

        } else if (shouldProcessNotification(notification)) {
            doUpdate = true;
        }

        if (doUpdate) {
            update();
        }
    }

    private void update() {
        if (!ispInputSetPanel.isDisposed() && ispInputSetPanel.isVisible()) {
            isoInputSetObject.refreshFromMetadata();
            ispInputSetPanel.refreshFromBusinessObject();
        }
    }

    /**
     * Determine whether a notification should be processed. For the InputSetEditorPanel, respond to the following 1)
     * InputParameter Add / Remove / Change - only if it's in the current MappingClass InputSet 2) MappingClassColumn Add / Remove
     * / Change - only if it's in one of the current bindings
     * 
     * @param notification the notification
     * @return 'true' if the Notification should be processed, false if not
     */
    private boolean shouldProcessNotification( Notification notification ) {
        boolean shouldProcess = false;
        if (notification != null && NotificationUtilities.isEObjectNotifier(notification)) {
            if (isoInputSetObject != null) {
                EObject eObj = NotificationUtilities.getEObject(notification);
                // -------------------------------------------------------------------------------
                // If the changed object is InputParameter in the current InputSet, process it.
                // -------------------------------------------------------------------------------
                if (eObj instanceof InputParameter) {
                    // Notification InputParameter's InputSet
                    EObject parent = eObj.eContainer();
                    // Currently Displayed InputSet
                    InputSet iSet = isoInputSetObject.getInputSet();
                    if (parent != null && parent.equals(iSet)) {
                        shouldProcess = true;
                    }
                }
                // -------------------------------------------------------------------------------
                // If the changed object is the current InputSet, process it.
                // -------------------------------------------------------------------------------
                else if (eObj instanceof InputSet) {
                    // Currently Displayed InputSet
                    InputSet iSet = isoInputSetObject.getInputSet();
                    if (eObj.equals(iSet)) {
                        // If InputParameter was added, add to binding list
                        if (NotificationUtilities.isAdded(notification)) {
                            // Get the added children - check whether they are InputParameters
                            EObject[] newChildren = NotificationUtilities.getAddedChildren(notification);
                            if (newChildren != null) {
                                int nChildren = newChildren.length;
                                for (int i = 0; i < nChildren; i++) {
                                    Object child = newChildren[i];
                                    // If the BindingList does not have binding, add new one.
                                    if (child != null && child instanceof InputParameter) {
                                        InputParameter iParm = (InputParameter)child;
                                        BindingList list = isoInputSetObject.getBindingList();
                                        if (!list.contains(iParm)) {
                                            list.add(new BindingAdapter(iParm));
                                        }
                                    }
                                }
                            }
                            // If InputParameter was removed, remove from binding list
                        } else if (NotificationUtilities.isRemoved(notification)) {
                            // Get the removed children - check whether they are InputParameters
                            EObject[] removedChildren = NotificationUtilities.getRemovedChildren(notification);
                            if (removedChildren != null) {
                                int nChildren = removedChildren.length;
                                for (int i = 0; i < nChildren; i++) {
                                    Object child = removedChildren[i];
                                    // If the BindingList has binding for the removed InputParameter, remove it.
                                    if (child != null && child instanceof InputParameter) {
                                        InputParameter iParm = (InputParameter)child;
                                        BindingList list = isoInputSetObject.getBindingList();
                                        BindingAdapter adapter = list.getBindingFor(iParm);
                                        if (adapter != null) {
                                            list.remove(adapter);
                                        }
                                    }
                                }
                            }
                        }
                        shouldProcess = true;
                    }
                }
                // -------------------------------------------------------------------------------
                // If the changed object is MappingClassColumn, process if it's bound to anything
                // -------------------------------------------------------------------------------
                else if (eObj instanceof MappingClassColumn) {
                    BindingList bList = isoInputSetObject.getBindingList();
                    int nBindings = bList.size();
                    for (int i = 0; i < nBindings; i++) {
                        BindingAdapter adapter = bList.get(i);
                        Object mappingObj = adapter.getMapping();
                        if (mappingObj != null && mappingObj instanceof MappingClassColumn) {
                            if (mappingObj.equals(eObj)) {
                                shouldProcess = true;
                                break;
                            }
                        }
                    }
                }
                // -------------------------------------------------------------------------------
                // If the changed object is MappingClassSet, process as if it's a new binding
                // -------------------------------------------------------------------------------
                else if (eObj instanceof MappingClassSet) {
                    EObject[] newChildren = NotificationUtilities.getAddedChildren(notification);
                    if (newChildren.length > 0) {
                        for (int i = 0; i < newChildren.length; i++) {
                            if (newChildren[i] instanceof InputBinding) {
                                InputBinding binding = (InputBinding)newChildren[i];
                                InputParameter newInputParameter = binding.getInputParameter();
                                if (newInputParameter != null) {
                                    InputSet inputSet = newInputParameter.getInputSet();
                                    // Check that same input set before proceeding
                                    InputSet iSet = isoInputSetObject.getInputSet();
                                    if (inputSet != null && inputSet.equals(iSet)) {
                                        // Add the binding?
                                        BindingList list = isoInputSetObject.getBindingList();
                                        if (!list.contains(newInputParameter)) {

                                            BindingAdapter newAdapter = new BindingAdapter(binding);
                                            list.add(newAdapter);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
        return shouldProcess;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditor#contributeToolbarActions(org.eclipse.jface.action.ToolBarManager)
     */
    public void contributeExportedActions( IMenuManager menu ) {
        // jhTODO: do we have any actions? I do not think so...
        if (menu == null) return;
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.IEditorActionExporter#getAdditionalModelingActions(org.eclipse.jface.viewers.ISelection)
     * @since 5.0
     */
    public List<IAction> getAdditionalModelingActions( ISelection selection ) {
        // jhTODO: do we have any actions? I do not think so...
        return Collections.EMPTY_LIST;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditor#contributeToolbarActions(org.eclipse.jface.action.ToolBarManager)
     */
    public void contributeToolbarActions( ToolBarManager toolBarMgr ) {
        if (toolBarMgr == null) return;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#doSave()
     */
    public void doSave( boolean isClosing ) {
        // there's no difference to this panel between isClosing true and false
        deactivate();
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
    public boolean isEditingObject( Object modelObject ) {
        if (inputSet != null && modelObject != null && modelObject instanceof InputSet) {
            if (modelObject.equals(inputSet)) return true;
        } else {
            if (inputSet != null && modelObject != null && modelObject instanceof InputParameter) {
                EObject parent = ((EObject)modelObject).eContainer();
                if (parent != null && parent.equals(inputSet)) return true;
            }
        }
        return false;
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#getEditableObject(java.lang.Object)
     * @since 4.2
     */
    public Object getEditableObject( Object modelObject ) {
        if (modelObject instanceof InputSet) {
            return modelObject;
        } else if (modelObject instanceof InputParameter) {
            EObject parent = ((InputParameter)modelObject).getInputSet();
            if (parent != null) {
                return parent;
            }
        }

        return null;
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#isResourceValid()
     * @since 4.2
     */
    public boolean isResourceValid() {
        if (inputSet != null) {
            ModelResource mr = ModelUtilities.getModelResourceForModelObject(inputSet);
            if (mr != null) return true;
        }
        return false;
    }

    /**
     * Does nothing.
     * 
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#initialize(com.metamatrix.modeler.internal.ui.editors.MultiPageModelEditor)
     * @since 5.0.1
     */
    public void initialize( MultiPageModelEditor editor ) {
    }

    /**
     * Does nothing.
     * 
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#setOverride(com.metamatrix.modeler.ui.editors.ModelObjectEditorPage)
     * @since 5.0.1
     */
    public void setOverride( ModelObjectEditorPage editor ) {
    }
}
