/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.views;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IFindReplaceTarget;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.texteditor.ITextEditorExtension2;
import org.eclipse.ui.views.properties.PropertySheet;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.util.XSDResourceImpl;

import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.metamodels.core.Annotation;
import com.metamatrix.metamodels.core.AnnotationContainer;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.xsd.XsdUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.notification.util.NotificationUtilities;
import com.metamatrix.modeler.core.transaction.SourcedNotification;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.PluginConstants;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.text.StyledTextEditor;

/**
 * DescriptionView displays a read-only text viewer of textual content of a selected model or model object.
 * 
 * Editing (or clearing) the text is done via Edit and Clear actions located on the toolbar or via a context menu.
 * 
 *
 */

public class DescriptionView extends ModelerView
    implements ISelectionListener, INotifyChangedListener, IMenuListener,
    ITextEditorExtension2 {

	private static final String DESCRIPTION_TXN_LABEL = UiConstants.Util.getString("DescriptionView.setDescriptionTransactionLabel"); //$NON-NLS-1$
	
    /**
     * The object whose description is being shown. Will either be an {@link EObject}, {@link ModelResource}, or <code>null</code>
     * .
     */
    private Object currentObject;

    private ModelResource currentModel;
    
    private StyledTextEditor textViewerPanel;
    
    // ---------- Description View actions --------------------
    private Action editDescriptionAction;
    private Action clearDescriptionAction;

    private void addListeners() {
        IWorkbenchWindow window = UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
        window.getSelectionService().addSelectionListener(this);
        ModelUtilities.addNotifyChangedListener(this);
        this.textViewerPanel.addMenuListener(this);
    }

    void clear( ) {
        if (!this.textViewerPanel.isDisposed()) {
            setText(CoreStringUtil.Constants.EMPTY_STRING);
        }
    }
    
    private void clearDescription() {
    	boolean cancelled = openEditorIfNeeded();
    	
    	if( !cancelled ) {
    		saveChangedObjectDescription(CoreStringUtil.Constants.EMPTY_STRING);
    	}
    }
    
    private void contributeToActionBars() {
        IActionBars bars = getViewSite().getActionBars();
        fillLocalToolBar(bars.getToolBarManager());
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPartControl( Composite parent ) {
        super.createPartControl(parent);
        
        GridData gd = new GridData(GridData.FILL_BOTH);
        parent.setLayoutData(gd);
        parent.setLayout(new GridLayout(2, false));
        

        // Create a Text Viewer
        
        this.textViewerPanel = new StyledTextEditor(parent, SWT.MULTI | SWT.V_SCROLL | SWT.WRAP | SWT.BORDER);
        this.textViewerPanel.setAllowCut(false);
        this.textViewerPanel.setAllowPaste(false);
        this.textViewerPanel.setAllowUndoRedo(false);
        this.textViewerPanel.setAllowFind(true);
        
        GridData tvGD = new GridData(GridData.FILL_BOTH);
        tvGD.horizontalSpan = 2;
        this.textViewerPanel.setLayoutData(tvGD);
        this.textViewerPanel.setEditable(false);
        Color newColor = UiUtil.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
        this.textViewerPanel.getTextWidget().setBackground(newColor);

        contributeToActionBars();
        
        addListeners();
    }
    
    /**
     * @see com.metamatrix.modeler.internal.ui.views.ModelerView#dispose()
     * @since 5.5
     */
    @Override
    public void dispose() {
    	this.textViewerPanel.addMenuListener(this);
    	
        IWorkbenchWindow workbenchWindow = getSite().getWorkbenchWindow();
        workbenchWindow.getSelectionService().removeSelectionListener(this);

        ModelUtilities.removeNotifyChangedListener(this);

        this.textViewerPanel.dispose();

        super.dispose();
    }
    
    private void editDescription() {
    	boolean cancelled = openEditorIfNeeded();
    	
    	if( !cancelled ) {
	    	Shell shell = UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
	    	EditDescriptionDialog dialog = new EditDescriptionDialog(shell, getCurrentObjectName(), textViewerPanel.getText());
//	        try {
//          UiPlugin.getDefault().getEventBroker().addListener(ModelResourceEvent.class, this);
//      } catch (EventSourceException e) {
//          UiConstants.Util.log(IStatus.ERROR, e, e.getMessage());
//      }
	        if (dialog.open() == Window.OK) {
	        	String newDescription = dialog.getChangedDescription();
	        	saveChangedObjectDescription(newDescription);
	        	refresh();
	        }
    	}
    }
    
    private void fillLocalToolBar( IToolBarManager manager ) {
    	this.editDescriptionAction = new Action(null) {
            @Override
            public void run() {
                editDescription();
            }
        };
        this.editDescriptionAction.setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(PluginConstants.Images.EDIT_DOCUMENT_ICON));
        this.editDescriptionAction.setToolTipText(UiConstants.Util.getString("DescriptionView.edit.tooltip")); //$NON-NLS-1$
        this.editDescriptionAction.setText(UiConstants.Util.getString("DescriptionView.edit.label")); //$NON-NLS-1$
        manager.add(editDescriptionAction);
        
        clearDescriptionAction = new Action(null) {
            @Override
            public void run() {
            	clearDescription();
            }
        };
        this.clearDescriptionAction.setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(PluginConstants.Images.CLEAR_DOCUMENT_ICON));
        this.clearDescriptionAction.setToolTipText(UiConstants.Util.getString("DescriptionView.clear.tooltip")); //$NON-NLS-1$
        this.clearDescriptionAction.setText(UiConstants.Util.getString("DescriptionView.clear.label")); //$NON-NLS-1$
        manager.add(clearDescriptionAction);
    }

    /**
     * @see com.metamatrix.modeler.internal.ui.views.ModelerView#getAdapter(java.lang.Class)
     * @since 5.5
     */
    @Override
    public Object getAdapter( Class key ) {
    	if (key.equals(IFindReplaceTarget.class)) {
            return this.textViewerPanel.getTextViewer().getFindReplaceTarget();
        }

        return super.getAdapter(key);
    }

    /**
     * @return the currently selected {@link EObject}'s or {@link ModelResource}'s description
     * @since 5.5
     */
    String getCurrentObjectDescription() {
        if (this.currentObject instanceof EObject) {
            return ModelObjectUtilities.getDescription((EObject)this.currentObject);
        } else if (this.currentObject instanceof ModelResource) {
            return ModelUtilities.getModelDescription(this.currentModel);
        } else {
        	// TODO: Check for VDB SELECTION
        }

        return null;
    }
    
    String getCurrentObjectName() {
        if (this.currentObject instanceof EObject) {
            return ModelerCore.getModelEditor().getName((EObject)this.currentObject);
        } else if (this.currentObject instanceof ModelResource) {
            return ((ModelResource)this.currentModel).getItemName();
        } else {
            // TODO: Check for VDB SELECTION
        }

        return null;
    }

    /**Shell shell = UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
     * @param obj1 the first object being compared
     * @param obj2 the second object being compared
     * @return <code>true</code> if the objects are equal and both not <code>null</code>
     * @since 5.5.3
     */
    private boolean haveSameState( Object obj1,
                                   Object obj2 ) {
        if (obj1 == obj2) {
            return ((obj1 != null) && (obj2 != null));
        }

        if (obj1 == null) {
            return (obj2 == null);
        }

        if (obj2 == null) {
            return false;
        }

        return obj1.equals(obj2);
    }

    /**
     * @return <code>true</code> if the model editor is open for the current object
     * @since 5.5.3
     */
    private boolean isEditorOpen() {
        if ((this.currentObject != null) && (this.currentModel != null)) {
            IFile modelFile = (IFile)this.currentModel.getResource();
            return (ModelEditorManager.isOpen(modelFile));
        }

        return false;
    }
    
	@Override
	public void menuAboutToShow(IMenuManager manager) {
		manager.add(new Separator());
		manager.add(editDescriptionAction);
		manager.add(clearDescriptionAction);
	}

    /**
     * @see org.eclipse.emf.edit.provider.INotifyChangedListener#notifyChanged(org.eclipse.emf.common.notify.Notification)
     * @since 5.5
     */
    public void notifyChanged( Notification notification ) {
        boolean descriptionChanged = false;

        // if the target of the notification is this object's annotation, refresh the display
        if (notification instanceof SourcedNotification) {
            Collection nList = ((SourcedNotification)notification).getNotifications();
            Notification nextNotification = null;
            EObject eObj = null;

            for (Iterator iter = nList.iterator(); iter.hasNext();) {
                nextNotification = (Notification)iter.next();
                eObj = NotificationUtilities.getEObject(nextNotification);

                if (eObj == null) {
                    Resource resource = NotificationUtilities.getResource(nextNotification);
                    
                    if ((resource != null) && (this.currentModel != null)) {
                        try {
                            if (this.currentModel.getEmfResource().equals(resource)) {
                                descriptionChanged = true;
                                break;
                            }
                        } catch (ModelWorkspaceException e) {
                            UiConstants.Util.log(IStatus.ERROR, e, e.getClass().getName());
                        }
                    }
                } else if (eObj instanceof Annotation) {
                    EObject target = ((Annotation)eObj).getAnnotatedObject();

                    if ((target != null) && target.equals(this.currentObject)) {
                        descriptionChanged = true;
                        break;
                    }
                } else if (eObj instanceof AnnotationContainer) {
                    if (NotificationUtilities.isAdded(nextNotification)) {
                        EObject[] newChildren = NotificationUtilities.getAddedChildren(nextNotification);

                        for (int iChild = 0; iChild < newChildren.length; iChild++) {
                            if (newChildren[iChild] instanceof Annotation) {
                                EObject target = ((Annotation)newChildren[iChild]).getAnnotatedObject();

                                if ((target != null) && target.equals(this.currentObject)) {
                                    descriptionChanged = true;
                                    break;
                                }
                            }
                        }
                    } else if (NotificationUtilities.isRemoved(nextNotification)) {
                        EObject[] oldChildren = NotificationUtilities.getRemovedChildren(nextNotification);

                        for (int iChild = 0; iChild < oldChildren.length; iChild++) {
                            if (oldChildren[iChild] instanceof Annotation) {
                                EObject target = ((Annotation)oldChildren[iChild]).getAnnotatedObject();

                                if (target != null && target.equals(this.currentObject)) {
                                    descriptionChanged = true;
                                    break;
                                }
                            }
                        }
                    }
                } else if( eObj instanceof ModelAnnotation && ((SourcedNotification)notification).getSource() == this ) {
                	descriptionChanged =  true;
                }

                if (descriptionChanged) {
                    break;
                }
            }
        } else {
            EObject target = NotificationUtilities.getEObject(notification);
            if (target instanceof Annotation) {
                target = ((Annotation)target).getAnnotatedObject();

                if ((target != null) && target.equals(this.currentObject)) {
                    descriptionChanged = true;
                }
            }
        }

        if (descriptionChanged) {
            refresh();
        }
    }

    /**
     * Should only be called if current object and model are not <code>null</code>.
     * 
     * @since 5.5.3
     */
    private boolean openEditorIfNeeded() {
    	boolean openEditorCancelled = false;
        // we only need to worry about the readonly status if the file is not currently open,
        // and its underlying IResource is not read only
        
    	if( this.currentModel == null ) {
    		
    	} else if (!isEditorOpen() && !this.currentModel.getResource().getResourceAttributes().isReadOnly()) {
            final IFile modelFile = (IFile)this.currentModel.getResource();
            Shell shell = UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();

            // may want to change these text strings eventually:
            if (MessageDialog.openQuestion(shell,
                                           ModelEditorManager.OPEN_EDITOR_TITLE,
                                           ModelEditorManager.OPEN_EDITOR_MESSAGE)) {
                // load and activate, not async (to prevent multiple dialogs from coming up):
                // Changed to use method that insures Object editor mode is on
                ModelEditorManager.openInEditMode(modelFile, true, UiConstants.ObjectEditor.IGNORE_OPEN_EDITOR);

                Display.getDefault().asyncExec(new Runnable() {

                    public void run() {
                        setFocus();
                    }
                });
            } else {
            	openEditorCancelled = true;
            }
        }
        
        return openEditorCancelled;
    }

    /**
     * Refreshes the enabled state and content of the GUI controls.
     * 
     * @since 5.5.3
     */
    void refresh() {
        Runnable work = new Runnable() {
            public void run() {
                setText(getCurrentObjectDescription());
                updateReadOnlyState();
            }
        };

        UiUtil.runInSwtThread(work, true);
    }

    /**
     * Saves the text editor content to the {@link EObject}'s description if necessary.
     * 
     * @since 5.5
     */
    private void saveChangedObjectDescription(String changedDescription) {
        // nothing to save if no current object
        if (this.currentObject == null) {
            return;
        }

        String currentDescription = changedDescription; //this.textEditor.getText();
        String savedDescription = null;

        if (this.currentObject instanceof EObject) {
            savedDescription = ModelObjectUtilities.getDescription((EObject)this.currentObject);

            if (!haveSameState(currentDescription, savedDescription)) {
                setDescription((EObject)this.currentObject, currentDescription, this);
            }
        } else if (this.currentObject instanceof ModelResource) {
            savedDescription = ModelUtilities.getModelDescription((ModelResource)this.currentObject);

            if (!haveSameState(currentDescription, savedDescription)) {
                setDescription((ModelResource)this.currentObject, currentDescription, this);
            }
        } else if (this.currentObject != null) {
            // TODO: - support selection of a VDB
        }
    }

    /**
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart,
     *      org.eclipse.jface.viewers.ISelection)
     * @since 5.5
     */
    public void selectionChanged( IWorkbenchPart part,
                                  ISelection selection ) {
        if ((part != this) && !(part instanceof PropertySheet)) {
            setCurrentObject(SelectionUtilities.getSelectedObject(selection));
            updateReadOnlyState();
        }
    }

    private void setCurrentObject( Object object ) {

        this.currentObject = null;
        this.currentModel = null;

        try {
            if (object instanceof EObject) {
                EObject eObj = (EObject)object;

                if (ModelObjectUtilities.supportsDescription(eObj)) {
                    this.currentObject = object;
                    this.currentModel = ModelUtilities.getModelResourceForModelObject(eObj);
                }
            } else if ((object instanceof IFile) && ModelUtilities.supportsModelDescription((IResource)object)) {
                this.currentModel = ModelUtil.getModelResource((IFile)object, false);
                this.currentObject = this.currentModel;
            } else {
                // TODO: - support selection of a VDB
            }
        } catch (Exception e) {
            UiConstants.Util.log(IStatus.ERROR, e, e.getClass().getName());
        }

        refresh();
    }
    
    /*
     *  Set up a transaction to set description on a model object
     */
    private void setDescription( EObject eObject, String description, Object eventSource ) {
        if (!ModelObjectUtilities.isReadOnly(eObject)) {
            boolean requiredStart = ModelerCore.startTxn(true, true, DESCRIPTION_TXN_LABEL, eventSource);
            boolean succeeded = false;
            try {
                if (eObject.eResource() instanceof XSDResourceImpl) {
                    if (eObject instanceof XSDConcreteComponent) {
                        XsdUtil.addUserInfoAttribute((XSDConcreteComponent)eObject, description);
                    }
                } else {
                    ModelerCore.getModelEditor().setDescription(eObject, description);
                }
                succeeded = true;
            } catch (ModelerCoreException ex) {
                String message = UiConstants.Util.getString("DescriptionView.errorSetDescription", eObject.toString()); //$NON-NLS-1$
                UiConstants.Util.log(IStatus.ERROR, ex, message);
            } finally {
                if (requiredStart) {
                    if (succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
        }
    }
    
    /*
     *  Set up a transaction to set description on a ModelResource
     */
	private void setDescription(ModelResource modelResource, String description,
			Object eventSource) {
		if (!ModelUtilities.isReadOnly(modelResource)) {
			boolean requiredStart = ModelerCore.startTxn(true, true, DESCRIPTION_TXN_LABEL, eventSource);
			boolean succeeded = false;
			try {

                ModelAnnotation annotation = modelResource.getModelAnnotation();
                if (annotation != null) {
                    annotation.setDescription(description);
                } else {
                	String message = 
                		UiConstants.Util.getString("DescriptionView.nullModelAnnotation", modelResource.getPath().toString()); //$NON-NLS-1$)
                    UiConstants.Util.log(IStatus.ERROR, message);
                }
                succeeded = true;
            } catch (ModelWorkspaceException ex) {
                String message = UiConstants.Util.getString("DescriptionView.errorSetDescriptionModel", modelResource.toString()); //$NON-NLS-1$
                UiConstants.Util.log(IStatus.ERROR, ex, message);
            } finally {
				if (requiredStart) {
					if (succeeded) {
						ModelerCore.commitTxn();
					} else {
						ModelerCore.rollbackTxn();
					}
				}
			}
		}
	}

    void setText( String newDescription ) {
        if (!this.textViewerPanel.isDisposed()) {
            String newText = (newDescription == null ? CoreStringUtil.Constants.EMPTY_STRING : newDescription);

            // change the text if necessary
            if (!newText.equals(this.textViewerPanel.getText())) {
                this.textViewerPanel.setText(newText);
            }
        }
    }


    void updateReadOnlyState() {

        boolean readOnly = false;

        if (this.currentModel != null) {
            readOnly = this.currentModel.isReadOnly();//ModelUtilities.isReadOnly(this.currentModel);
        } else if (this.currentObject != null) {
        	// TODO: - support selection of a VDB
        } else {
        	readOnly = true;
        }

        this.editDescriptionAction.setEnabled(!readOnly);
        this.clearDescriptionAction.setEnabled(!readOnly);
    }

    /**
     * @see org.eclipse.ui.texteditor.ITextEditorExtension2#isEditorInputModifiable()
     * @since 5.5.3
     */
    public boolean isEditorInputModifiable() {
        return false; //this.textEditor.isEditable();
    }

    /**
     * @see org.eclipse.ui.texteditor.ITextEditorExtension2#validateEditorInputState()
     * @since 5.5.3
     */
    public boolean validateEditorInputState() {
        return false;
    }

	@Override
	public void setFocus() {
		// DO NOTHING
	}


}
