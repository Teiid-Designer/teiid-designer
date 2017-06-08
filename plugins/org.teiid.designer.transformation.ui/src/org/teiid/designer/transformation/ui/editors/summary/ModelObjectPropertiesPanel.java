/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 
 * Copied from org.eclipse.ui.views.* packages
 * 
 * The Properties View source was restrictive to use in a dockable View's page object
 * 
 * PropertySheetViewer was tightly connected to our extended ModelObjectPropertySheetPage and it's
 * functionality met our needs to embed this viewer in an Editor page.
 * 
 * So copied over this class and minimum number of associated classes to utilize this viewer in our
 * editor
 */
package org.teiid.designer.transformation.ui.editors.summary;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.emf.edit.ui.provider.PropertySource;
import org.eclipse.help.IContext;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.HelpEvent;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IContextComputer;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.ui.part.CellEditorActionHandler;
import org.eclipse.ui.views.properties.IPropertySheetEntry;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;
import org.teiid.core.designer.event.EventObjectListener;
import org.teiid.core.designer.event.EventSourceException;
import org.teiid.designer.core.notification.util.SourcedNotificationUtilities;
import org.teiid.designer.core.transaction.SourcedNotification;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.metamodels.core.Annotation;
import org.teiid.designer.metamodels.core.AnnotationContainer;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.transformation.ui.editors.summary.properties.ICellEditorActivationListener;
import org.teiid.designer.transformation.ui.editors.summary.properties.PropertySheetEntry;
import org.teiid.designer.transformation.ui.editors.summary.properties.PropertySheetSorter;
import org.teiid.designer.transformation.ui.editors.summary.properties.PropertySheetViewer;
import org.teiid.designer.ui.UiPlugin;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.event.ModelResourceEvent;
import org.teiid.designer.ui.properties.ModelObjectPropertySource;
import org.teiid.designer.ui.properties.ModelPropertySource;
import org.teiid.designer.ui.viewsupport.IExtendedModelObject;
import org.teiid.designer.ui.viewsupport.ModelObjectUtilities;
import org.teiid.designer.ui.viewsupport.ModelUtilities;

public class ModelObjectPropertiesPanel implements UiConstants, INotifyChangedListener, EventObjectListener, IPropertySourceProvider {

    private PropertySheetViewer viewer;

    private PropertySheetSorter sorter;

    private IPropertySheetEntry rootEntry;

    private IPropertySourceProvider provider;

//    private DefaultsAction defaultsAction;

//    private FilterAction filterAction;

//    private CategoriesAction categoriesAction;

//    private CopyPropertyAction copyAction;

    private ICellEditorActivationListener cellEditorActivationListener;

    private CellEditorActionHandler cellEditorActionHandler;
    
    private boolean handlingNotification = false;
    
    private EObject currentSelection;
    
    /**
     * DataSourcePropertiesPanel constructor
     * @param parent the parent composite
     * @param teiidImportServer the TeiidServer
     * @param isReadOnly whether the panel is readonly
     * @param isCreateNew 'true' if creating a new source, 'false' if not
     * @param initialSelection the initialSelection to display
     */
    public ModelObjectPropertiesPanel( Composite parent,  boolean isReadOnly ) {

        createMainPanel(parent,isReadOnly);
        
        ModelUtilities.addNotifyChangedListener(this);
        
		setPropertySourceProvider(this);
		
        setupTooltip();
        
        viewer.setColumnWidths();
    }

    private void createMainPanel(Composite parent, boolean readOnly) {
        // create a new viewer
        viewer = new PropertySheetViewer(parent);
        viewer.setSorter(sorter);

        // set the model for the viewer
        if (rootEntry == null) {
            // create a new root
            PropertySheetEntry root = new PropertySheetEntry();
            if (provider != null) {
				// set the property source provider
                root.setPropertySourceProvider(provider);
			}
            rootEntry = root;
        }
        viewer.setRootEntry(rootEntry);
        viewer.addActivationListener(getCellEditorActivationListener());
        // add a listener to track when the entry selection changes
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
			public void selectionChanged(SelectionChangedEvent event) {
                handleEntrySelection(event.getSelection());
            }
        });
//        initDragAndDrop();
//        makeActions();
//
//        // Create the popup menu for the page.
//        MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
//        menuMgr.add(copyAction);
//        menuMgr.add(new Separator());
//        menuMgr.add(defaultsAction);
//        Menu menu = menuMgr.createContextMenu(viewer.getControl());
//        viewer.getControl().setMenu(menu);

        // Set help on the viewer
        viewer.getControl().addHelpListener(new HelpListener() {
            /*
             * @see HelpListener#helpRequested(HelpEvent)
             */
            @Override
			public void helpRequested(HelpEvent e) {
                // Get the context for the selected item
                IStructuredSelection selection = (IStructuredSelection) viewer
                        .getSelection();
                if (!selection.isEmpty()) {
                    IPropertySheetEntry entry = (IPropertySheetEntry) selection
                            .getFirstElement();
                    Object helpContextId = entry.getHelpContextIds();
                    if (helpContextId != null) {
                    	IWorkbenchHelpSystem helpSystem = PlatformUI.getWorkbench().getHelpSystem();

                        // Since 2.0 the only valid type for helpContextIds
                        // is a String (a single id).
                        if (helpContextId instanceof String) {
                            helpSystem.displayHelp((String) helpContextId);
                            return;
                        }

                        // For backward compatibility we have to handle
                        // and array of contexts (Strings and/or IContexts)
                        // or a context computer.
                        Object context= getFirstContext(helpContextId, e);
                        if (context instanceof IContext) {
							helpSystem.displayHelp((IContext) context);
						} else if (context instanceof String) {
							helpSystem.displayHelp((String) context);
						}
                        return;
                    }
                }

                // No help for the selection so show page help
//                PlatformUI.getWorkbench().getHelpSystem().displayHelp(HELP_CONTEXT_PROPERTY_SHEET_PAGE);
            }

			/**
			 * Returns the first help context.
			 *
			 * @param helpContext the help context which is either an array of contexts (strings
			 *            and/or {@linkplain IContext}s) or an {@link IContextComputer}
			 *
			 * @param e the help event
			 * @return the first context which is either a <code>String</code>, {@link IContext} or
			 *         <code>null</code> if none
			 * @deprecated As of 2.0, nested contexts are no longer supported by the help support
			 *             system
			 */
			@Deprecated
			private Object getFirstContext(Object helpContext, HelpEvent e) {
				Object[] contexts;
				if (helpContext instanceof IContextComputer) {
				    // get local contexts
					contexts= ((IContextComputer)helpContext)
				            .getLocalContexts(e);
				} else {
					contexts= (Object[])helpContext;
				}

				if (contexts.length > 0)
					return contexts[0];
				return null;
			}
        });
    }


    /**
     * Get the current panel Status
     * @return the current Status
     */
    public IStatus getStatus() {
    	IStatus resultStatus = new Status(IStatus.OK, PLUGIN_ID, "OK" /*Messages.dataSourcePropertiesPanelOk*/);

//        for(PropertyItem propObj : this.propertyItemList) {
//            if(isPropertyRequired(propObj,this.propertyItemList) && !propObj.hasValidValue()) {
//        		resultStatus = new Status(IStatus.ERROR, PLUGIN_ID, Messages.dataSourcePropertiesPanel_invalidPropertyMsg);
//            	break;
//            }
//        }
//        
//        if(applyButton.isEnabled()) {
//        	resultStatus = new Status(IStatus.ERROR, PLUGIN_ID, Messages.dataSourcePropertiesPanel_applyPropertyChangesMsg);
//        }
        
        return resultStatus;        
    }
    
    

    
    /**
     * Public access to refresh the contents of this panel based on external changes to the translator override
     * properties
     */
    public void refresh() {
        this.viewer.setInput(this);
        this.viewer.refresh();
    }

    /**
     * SelectionEvent is received from DataSourcePanel
     */

    public void selectionChanged(SelectionChangedEvent event) {
    	Object obj = SelectionUtilities.getSelectedObject(event.getSelection());
    	
    	if( obj != null ) {
    		Object[] input = new Object[1];
    		input[0] = obj;
    		viewer.setInput(input);
    		viewer.refresh();
    	} else {
    		viewer.setInput(null);
    		viewer.refresh();
    	}

        //setDataSourceOrDriverName(dataSourceOrDriverName);
    }

	@Override
	public void processEvent(EventObject obj) {
		// TODO Auto-generated method stub
		
	}

    /* (non-Javadoc)
     * Listens to notifications that change the currently displayed object so that the panel can be refreshed.
     * @see org.eclipse.emf.edit.provider.INotifyChangedListener#notifyChanged(org.eclipse.emf.common.notify.Notification)
     */
    @Override
    public void notifyChanged(Notification notification) {
        // to prevent looping, do not process any notifications that occur during refresh.
        if ( handlingNotification ) {
            return;
        }
        
        if ( notification instanceof SourcedNotification ) {
            Object source = ((SourcedNotification) notification).getSource(); 
            if( source == this || source instanceof ModelObjectPropertySource) {
                //If this is the source of the notification, don't process - just return
                return;
            }
        }
        
        if ( currentSelection != null ) {
            // Get the affectedObjects. And the annotated objects, if any affected objects are annotations.
            Set affectedObjects = SourcedNotificationUtilities.gatherNotifiers(notification, true);
            Set annotatedObjects = getAnnotatedObjects(affectedObjects);
            if (affectedObjects.contains(currentSelection) || annotatedObjects.contains(currentSelection)
                || isModelExtensionDefinitionRelated(notification)) {
                handlingNotification = true;
                try {
                    Display.getDefault().asyncExec(new Runnable() {
                        @Override
            			public void run() {
                            refresh();
                        }
                    });
                } catch (SWTException e) {
                    // swallow - a Widget is disposed exception may occur due to deactivateCellEditor call
                    //   but this is not even important enough to log.
                } finally {
                    handlingNotification = false;
                }
            }
        }
    }
    
    /* 
     * For any Annotation objects in the supplied set, get its annotated object and add it to the returned set of annotated objects.
     * @param objects the supplied set of objects
     * @return the set of annotated objects
     */
    private Set getAnnotatedObjects( Set objects ) {
        Set annotatedObjects = new HashSet();
        if (objects != null) {
            for (final Iterator it = objects.iterator(); it.hasNext();) {
                Object obj = it.next();
                if (obj instanceof Annotation) {
                    Object annotatedObject = ((Annotation)obj).getAnnotatedObject();
                    if (annotatedObject != null) annotatedObjects.add(annotatedObject);
                }
            }
        }
        return annotatedObjects;
    }
    
    private boolean isModelExtensionDefinitionRelated( Notification notification ) {
        Object notifier = notification.getNotifier();

        // model extension framework uses annotations
        if ((notifier instanceof AnnotationContainer)  || (notifier instanceof Annotation)) {
            List<Notification> notifications = new ArrayList<Notification>();

            if (notification instanceof SourcedNotification) {
                notifications.addAll(((SourcedNotification)notification).getNotifications());
            } else {
                notifications.add(notification);
            }

            for (Notification event : notifications) {
                Object modelObject = null;

                if (event.getEventType() == Notification.ADD) {
                    modelObject = event.getNewValue();
                } else if (event.getEventType() == Notification.REMOVE) {
                    modelObject = event.getOldValue();
                }

                if (modelObject!=null && ExtensionPlugin.getInstance().isModelExtensionDefinitionRelated(modelObject)) {
                    return true;
                }
            }
        }

        return false;
    }
    
    private void setupTooltip() {
        final Tree tree = (Tree)this.viewer.getControl();

        final Listener listener = new Listener() {
            Shell tip = null;
            Label label = null;

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
             */
            @Override
            public void handleEvent( Event event ) {
                switch (event.type) {
                case SWT.Dispose:
                case SWT.KeyDown:
                case SWT.MouseExit:
                case SWT.MouseDown:
                case SWT.MouseMove:
                    if (tip != null) {
                        tip.dispose();
                        tip = null;
                        label = null;
                    }

                    break;
                case SWT.MouseHover:
                    if (tip != null) {
                        tip.dispose();
                        tip = null;
                        label = null;
                    }

                    String tooltip = null;
                    TreeItem item = tree.getItem(new Point(event.x, event.y));

                    if (item != null) {
                        Object data = item.getData();

                        if (data instanceof PropertySheetEntry) {
                            tooltip = ((PropertySheetEntry)data).getDescription();
                        }

                        if (tooltip != null) {
                            Shell shell = tree.getShell();
                            Display display = tree.getDisplay();

                            tip = new Shell(shell, SWT.ON_TOP | SWT.NO_FOCUS | SWT.TOOL);
                            tip.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
                            FillLayout layout = new FillLayout();
                            layout.marginWidth = 2;
                            tip.setLayout(layout);
                            label = new Label(tip, SWT.NONE);
                            label.setForeground(display.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
                            label.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
                            label.setData("_TABLEITEM", item); //$NON-NLS-1$
                            label.setText(tooltip);
                            label.addListener(SWT.MouseExit, this);
                            label.addListener(SWT.MouseDown, this);
                            Point size = tip.computeSize(SWT.DEFAULT, SWT.DEFAULT);
                            Rectangle rect = item.getBounds(0);
                            // Display the tooltip on the same line as the property,
                            // but offset to the right of wherever the mouse cursor was,
                            // such that it does not obscure the list of properties.
                            Point pt = tree.toDisplay(event.x + 15, rect.y);
                            tip.setBounds(pt.x, pt.y, size.x, size.y);
                            tip.setVisible(true);
                        }
                    }
                }
            }
        };

        tree.addListener(SWT.Dispose, listener);
        tree.addListener(SWT.KeyDown, listener);
        tree.addListener(SWT.MouseMove, listener);
        tree.addListener(SWT.MouseHover, listener);
    }
    
    /**
     * Returns the cell editor activation listener for this page
     * @return ICellEditorActivationListener the cell editor activation listener for this page
     */
    private ICellEditorActivationListener getCellEditorActivationListener() {
        if (cellEditorActivationListener == null) {
            cellEditorActivationListener = new ICellEditorActivationListener() {
                @Override
				public void cellEditorActivated(CellEditor cellEditor) {
                    if (cellEditorActionHandler != null) {
						cellEditorActionHandler.addCellEditor(cellEditor);
					}
                }

                @Override
				public void cellEditorDeactivated(CellEditor cellEditor) {
                    if (cellEditorActionHandler != null) {
						cellEditorActionHandler.removeCellEditor(cellEditor);
					}
                }
            };
        }
        return cellEditorActivationListener;
    }
    
    /**
     * Handles a selection change in the entry table.
     *
     * @param selection the new selection
     */
    public void handleEntrySelection(ISelection selection) {
//        if (defaultsAction != null) {
//            if (selection.isEmpty()) {
//                defaultsAction.setEnabled(false);
//                return;
//            }
//            // see if item is editable
//            boolean editable = viewer.getActiveCellEditor() != null;
//            defaultsAction.setEnabled(editable);
//        }
    }
    

    // TODO:  Implement something to call dispose() ?
    public void dispose() {
        ModelUtilities.removeNotifyChangedListener(this);
        // remove event object listener
        try {
            UiPlugin.getDefault().getEventBroker().removeListener(ModelResourceEvent.class, this);
        } catch (EventSourceException e) {
            UiConstants.Util.log(IStatus.ERROR, e, e.getMessage());
        }

    }

	@Override
	public IPropertySource getPropertySource(Object object) {
        if ( object == null ) {
            return null;
        }
        
        if ( object instanceof IItemPropertySource ) {
            return new PropertySource(object, (IItemPropertySource)object);
        } else if ( object instanceof EObject ) {
            // use EMF's property source provider
            return ModelObjectUtilities.getEmfPropertySourceProvider().getPropertySource(object);
        } else if ( object instanceof IFile && ModelUtilities.isModelFile((IFile) object)) {
            return new ModelPropertySource((IFile) object);
        } else if( object instanceof IExtendedModelObject ) {
            return ((IExtendedModelObject)object).getPropertySource();
        } else {
            // look it up in the Platform's AdapterManager
            IAdapterManager manager = Platform.getAdapterManager();
            return (IPropertySource) manager.getAdapter(object, IPropertySource.class);
        }
	}
	
    public void setPropertySourceProvider(IPropertySourceProvider newProvider) {
        provider = newProvider;
        if (rootEntry instanceof PropertySheetEntry) {
            ((PropertySheetEntry) rootEntry).setPropertySourceProvider(provider);
            // the following will trigger an update
            viewer.setRootEntry(rootEntry);
        }
    }
}
