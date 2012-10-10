/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.properties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EventObject;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.views.properties.PropertySheetEntry;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.teiid.core.designer.event.EventObjectListener;
import org.teiid.core.designer.event.EventSourceException;
import org.teiid.designer.core.notification.util.NotificationUtilities;
import org.teiid.designer.core.notification.util.SourcedNotificationUtilities;
import org.teiid.designer.core.transaction.SourcedNotification;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.metamodels.core.Annotation;
import org.teiid.designer.metamodels.core.AnnotationContainer;
import org.teiid.designer.metamodels.core.ModelAnnotation;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.UiPlugin;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.event.ModelResourceEvent;
import org.teiid.designer.ui.viewsupport.ModelUtilities;


/**
 * ModelObjectPropertySheetPage is a specialization of PropertySheetPage that refreshes the content
 * of the PropertySheetPage when a notification occurs on the displayed object.  It also adds an
 * Action to the toolbar that can display the metamodel extension properties of a selected
 * model object.
 *
 * @since 8.0
 */
public class ModelObjectPropertySheetPage 
        extends PropertySheetPage 
        implements INotifyChangedListener, EventObjectListener {

    /** the current ISelection state in the PropertySheetPage */
    private ISelection currentSelection;
    
    private Control control;
    
    private boolean handlingNotification = false;

    
    
    /** 
     * 
     * @since 4.2
     */
    public ModelObjectPropertySheetPage() {
        super();
        // Wire up for event object listener
        try {
            UiPlugin.getDefault().getEventBroker().addListener(ModelResourceEvent.class, this);
        } catch (EventSourceException e) {
            UiConstants.Util.log(IStatus.ERROR, e, e.getMessage());
        }
    }
    /**
     * Overridden from the base class to keep track of the current selection and enable/disable
     * the extension properties toggle action. 
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
     */
    @Override
    public void selectionChanged(IWorkbenchPart part, ISelection selection) {

        if ( control != null && ! control.isDisposed() ) {
            if ( selection != null && ! selection.isEmpty() ) {
                try {
                    currentSelection = selection;
                    super.selectionChanged(part, selection);
                } catch (Exception e) {
                    UiConstants.Util.log(IStatus.ERROR, e, e.getClass().getName());
                }
            } else if ( selection != null ) {
                try {
                    super.selectionChanged(part, selection);
                } catch (Exception e) {
                    UiConstants.Util.log(IStatus.ERROR, e, e.getClass().getName());
                }
            }
        }

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

                if (ExtensionPlugin.getInstance().isModelExtensionDefinitionRelated(modelObject)) {
                    return true;
                }
            }
        }

        return false;
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
            EObject selectedObject = SelectionUtilities.getSelectedEObject(currentSelection);

            if (selectedObject != null) {
                // Get the affectedObjects. And the annotated objects, if any affected objects are annotations.
                Set affectedObjects = SourcedNotificationUtilities.gatherNotifiers(notification, true);
                Set annotatedObjects = getAnnotatedObjects(affectedObjects);
                if (affectedObjects.contains(selectedObject) || annotatedObjects.contains(selectedObject)
                    || isModelExtensionDefinitionRelated(notification)) {
                    handlingNotification = true;
                    try {
                        this.refresh();
                    } catch (SWTException e) {
                        // swallow - a Widget is disposed exception may occur due to deactivateCellEditor call
                        //   but this is not even important enough to log.
                    } finally {
                        handlingNotification = false;
                    }
                }
            } else if (!SelectionUtilities.getSelectedIResourceObjects(this.currentSelection).isEmpty()) {
                // if an IResource is selected and the EObject of the notification(s) is a ModelAnnotation
                // go ahead and refresh the property page. to determine that the ModelAnnotation is from
                // the model in the current selection is not worth the effort. just refresh.

                boolean isModelAnnotation = false;
                if (notification instanceof SourcedNotification) {
                    for (final Iterator it = ((SourcedNotification) notification).getNotifications().iterator(); it.hasNext();) {
                      if (((Notification) it.next()).getNotifier() instanceof ModelAnnotation) {
                          isModelAnnotation = true;
                          break;
                      }
                    }
                } else {
                    final EObject target = NotificationUtilities.getEObject(notification);
                    isModelAnnotation = target instanceof ModelAnnotation; 
                }
                
                // editable model resource properties are found in the ModelAnnotation
                if (isModelAnnotation) {
                    this.handlingNotification = true;

                    try {
                        refresh();
                    } catch (SWTException e) {
                        // swallow - a Widget is disposed exception may occur due to deactivateCellEditor call
                        //   but this is not even important enough to log.
                    } finally {
                        this.handlingNotification = false;
                    }
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

    /* (non-Javadoc)
     * Overridden to hook this page up as a notification listener.
     * @see org.eclipse.ui.part.IPageBookViewPage#init(org.eclipse.ui.part.IPageSite)
     */
    @Override
    public void init(IPageSite pageSite) {
        super.init(pageSite);
        ModelUtilities.addNotifyChangedListener(this);
    }

    /* (non-Javadoc)
     * Overridden to create the extensions action and add it to the toolbar.
     * @see org.eclipse.ui.part.IPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createControl(Composite parent) {
        super.createControl(parent);
        
        control = super.getControl();
//        if ( control instanceof TableTree ) {
//            new TableSizeAdapter(((TableTree) control).getTable());
//        }
        setupTooltip();
    }

    private void setupTooltip() {
        final Tree tree = (Tree)getControl();

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

    /* (non-Javadoc)
     * Overridden to unhook this page as a notification listener.
     * @see org.eclipse.ui.part.IPage#dispose()
     */
    @Override
    public void dispose() {
        ModelUtilities.removeNotifyChangedListener(this);
        // remove event object listener
        try {
            UiPlugin.getDefault().getEventBroker().removeListener(ModelResourceEvent.class, this);
        } catch (EventSourceException e) {
            UiConstants.Util.log(IStatus.ERROR, e, e.getMessage());
        }
        super.dispose();
    }
    /** 
     * @see org.teiid.core.designer.event.EventObjectListener#processEvent(java.util.EventObject)
     * @since 4.2
     */
    @Override
    public void processEvent(EventObject obj) {
        ModelResourceEvent event = (ModelResourceEvent) obj;
        if ( event.getType() == ModelResourceEvent.RELOADED ) {
            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                    selectionChanged(null, new StructuredSelection(Collections.EMPTY_LIST));
                }
            });
            
        }
    }
}
