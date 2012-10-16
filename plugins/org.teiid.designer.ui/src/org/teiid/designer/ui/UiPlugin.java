/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui;

import static org.teiid.designer.ui.UiConstants.TableEditorAttributes.COLUMN_ORDER;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.osgi.framework.BundleContext;
import org.teiid.core.designer.PluginUtil;
import org.teiid.core.designer.event.EventBroker;
import org.teiid.core.designer.event.SynchEventBroker;
import org.teiid.core.designer.util.PluginUtilImpl;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.notification.util.NotificationUtilities;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceManager;
import org.teiid.designer.core.workspace.ModelWorkspaceNotification;
import org.teiid.designer.core.workspace.ModelWorkspaceNotificationListener;
import org.teiid.designer.ui.actions.ModelerActionService;
import org.teiid.designer.ui.common.AbstractUiPlugin;
import org.teiid.designer.ui.common.PreferenceKeyAndDefaultValue;
import org.teiid.designer.ui.common.actions.ActionService;
import org.teiid.designer.ui.common.product.ProductCustomizerMgr;
import org.teiid.designer.ui.common.util.UiUtil;
import org.teiid.designer.ui.editors.ModelEditorProjectListener;
import org.teiid.designer.ui.event.ModelResourceEvent;
import org.teiid.designer.ui.favorites.EObjectModelerCache;
import org.teiid.designer.ui.product.IModelerProductContexts;
import org.teiid.designer.ui.table.EObjectPropertiesOrderPreferences;
import org.teiid.designer.ui.undo.ModelerUndoManager;
import org.teiid.designer.ui.viewsupport.ModelerNotificationHelper;


/**
 * The main plugin class to be used in the desktop.
 * 
 * @since 8.0
 */
public final class UiPlugin extends AbstractUiPlugin implements PluginConstants, UiConstants, IModelerProductContexts {

    // The shared instance.
    private static UiPlugin plugin;

    /**
     * Returns the shared instance.
     * 
     * @since 4.0
     */
    public static UiPlugin getDefault() {
        return UiPlugin.plugin;
    }

    // This plugin's EventBroker.
    private EventBroker eventBroker;

    private EObjectModelerCache eObjCache;

    // The previous selection in this plugin's workbench
    ISelection cachedSelection = null;

    // ImageDescriptors
    ImageDescriptor errorDecoratorImage;
    ImageDescriptor warningDecoratorImage;
    ImageDescriptor extensionDecoratorImage;
    ImageDescriptor previewableDecoratorImage;

    private final ModelEditorProjectListener projectListener = new ModelEditorProjectListener();
    private EObjectPropertiesOrderPreferences eObjectPropertiesOrderPreferences;

    private ModelerActionService service;
    
    private IPropertyChangeListener tablePrefPropListener;

    /**
     * The constructor.
     * 
     * @since 4.0
     */
    public UiPlugin() {
        UiPlugin.plugin = this;
    }

    /**
     * @see org.teiid.designer.ui.common.AbstractUiPlugin#createActionService(org.eclipse.ui.IWorkbenchPage)
     * @since 4.0
     */
    @Override
    protected ActionService createActionService( final IWorkbenchPage page ) {
        if (this.service == null) {
            page.getWorkbenchWindow().getSelectionService().addSelectionListener(new ViewSelectionCache());
            this.service = new ModelerActionService(page);
            this.service.initializeGlobalActions();
        }

        return this.service;
    }

    public void extractModelTableColumnUtilsToPreferenceStore() {
        getPreferenceStore().setValue(COLUMN_ORDER, getEObjectPropertiesOrderPreferences().toString());
    }

    /**
     * Obtains the cache used to hold {@link org.eclipse.emf.ecore.EObject}s.
     * 
     * @return the shared cache
     * @since 4.2
     */
    public EObjectModelerCache getEObjectCache() {
        if (this.eObjCache == null) this.eObjCache = new EObjectModelerCache();

        return this.eObjCache;
    }

    public EObjectPropertiesOrderPreferences getEObjectPropertiesOrderPreferences() {
        if (this.eObjectPropertiesOrderPreferences == null) {
            this.eObjectPropertiesOrderPreferences = new EObjectPropertiesOrderPreferences();
            this.tablePrefPropListener = new IPropertyChangeListener() {
                @Override
				public void propertyChange( PropertyChangeEvent event ) {
                    // update if new prefs have been imported
                    if (event.getProperty().equals(COLUMN_ORDER)) {
                        initializeModelTableColumnUtilsFromPreferenceStore();
                    }
                }
            };
            getPreferenceStore().addPropertyChangeListener(this.tablePrefPropListener);
        }

        return this.eObjectPropertiesOrderPreferences;
    }

    public ImageDescriptor getErrorDecoratorImage() {
        return errorDecoratorImage;
    }

    public EventBroker getEventBroker() {
        if (this.eventBroker == null) eventBroker = new SynchEventBroker(); // only visible in debug
        return eventBroker;
    }

    /**
     * @see org.teiid.designer.ui.common.AbstractUiPlugin#getPluginUtil()
     * @since 4.0
     */
    @Override
    public PluginUtil getPluginUtil() {
        return Util;
    }

    /**
     * Return the last selection that occurred in a ViewPart (rather than in an editor).
     * 
     * @return
     */
    public ISelection getPreviousViewSelection() {
        return cachedSelection;
    }

    public Image getProjectImage() {
        return getImage(Images.MODEL_PROJECT);
    }

    public Image getSimpleProjectImage() {
        return getImage(Images.SIMPLE_PROJECT);
    }

    public ImageDescriptor getWarningDecoratorImage() {
        return warningDecoratorImage;
    }
    
    public ImageDescriptor getExtensionDecoratorImage() {
        return extensionDecoratorImage;
    }

    public ImageDescriptor getPreviewableDecoratorImage() {
        return previewableDecoratorImage;
    }

    /**
     * @return
     * @since 4.0
     */
    public static void registerActionForSelection( ISelectionListener action ) {
        ActionService actionService = UiPlugin.getDefault().getActionService(UiPlugin.getDefault().getCurrentWorkbenchWindow().getActivePage());
        actionService.addWorkbenchSelectionListener(action);
    }

    /**
     * @return
     * @since 4.0
     */
    public static void unregisterActionForSelection( ISelectionListener action ) {
        ActionService actionService = UiPlugin.getDefault().getActionService(UiPlugin.getDefault().getCurrentWorkbenchWindow().getActivePage());
        actionService.removeWorkbenchSelectionListener(action);
    }

    void initializeModelTableColumnUtilsFromPreferenceStore() {
        getEObjectPropertiesOrderPreferences().initializeFromString(getPreferenceStore().getString(COLUMN_ORDER));
    }

    /**
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     * @since 5.0
     */
    @Override
    public void start( final BundleContext context ) throws Exception {
        super.start(context);

        // Initialize logging/i18n utility
        ((PluginUtilImpl)Util).initializePlatformLogger(this);

        // // Test widget listener
        // getWorkbench().addWindowListener(new IWindowListener() {
        //
        // @Override
        // public void windowActivated( final IWorkbenchWindow window ) {
        // Display.getCurrent().addFilter(SWT.MouseDown, new Listener() {
        //
        // @Override
        // public void handleEvent( final Event event ) {
        // final StringBuilder builder = new StringBuilder();
        // if (event.widget instanceof Control) {
        // Control control = (Control)event.widget;
        // for (Composite parent = control.getParent(); parent != null; parent = control.getParent()) {
        // final Control[] children = parent.getChildren();
        // for (int ndx = 0, len = children.length; ndx < len; ++ndx)
        // if (children[ndx] == control) {
        // if (builder.length() > 0) builder.insert(0, '.');
        // builder.insert(0, ']');
        // builder.insert(0, ndx);
        // builder.insert(0, '[');
        // builder.insert(0, control.getClass().getSimpleName());
        // }
        // control = parent;
        // }
        //                            builder.insert(0, "."); //$NON-NLS-1$
        // builder.insert(0, control.getClass().getSimpleName());
        // }
        // System.out.println(builder);
        // }
        // });
        // }
        //
        // @Override
        // public void windowClosed( final IWorkbenchWindow window ) {
        // }
        //
        // @Override
        // public void windowDeactivated( final IWorkbenchWindow window ) {
        // }
        //
        // @Override
        // public void windowOpened( final IWorkbenchWindow window ) {
        // }
        // });

        try {
            UiUtil.runInSwtThread(new Runnable() {

                @Override
				public void run() {
                    UiPlugin.this.errorDecoratorImage = getImageDescriptor(Images.ERROR_DECORATOR);
                    UiPlugin.this.warningDecoratorImage = getImageDescriptor(Images.WARNING_DECORATOR);
                    UiPlugin.this.extensionDecoratorImage = getImageDescriptor(Images.EXTENSION_DECORATOR);
                    UiPlugin.this.previewableDecoratorImage = getImageDescriptor(Images.PREVIEWABLE_DECORATOR);
                }
            }, true);
        } catch (final Throwable err) {
            throw new CoreException(new Status(IStatus.ERROR, PLUGIN_ID, 0, err.getLocalizedMessage(), err));
        }

        // Initialize product customizer
        try {
            ProductCustomizerMgr.getInstance().loadCustomizations();
        } catch (final Exception theException) {
            // just log. no need to stop plugin startup for customization problem
            Util.log(theException);
        }

        // Initialize the ModelerUndoManager
        ModelerUndoManager.getInstance();

        // Initialize the ModelEditorProjectListener
        ModelerCore.getWorkspace().addResourceChangeListener(projectListener);

        // Initialize the NotificationUtilities INotificationHandler to the modeler;
        NotificationUtilities.setNotificationHelper(new ModelerNotificationHelper());

        // Initialize the ModelWorkspaceManager
        ModelWorkspaceManager.getModelWorkspaceManager();

        // Register a listener for ModelWorkspaceNotifications ...
        final ModelWorkspaceNotificationListener modelWsListener = new ModelWorkspaceNotificationListener() {

            protected ModelResource getModelResource( final Notification notification ) {
                final IResource resource = (IResource)notification.getNotifier();
                return ModelerCore.getModelWorkspace().findModelResource(resource);
            }

            @Override
			public void notifyAdd( final ModelWorkspaceNotification notification ) {
                // act on event only if a "final," after-change event:
                if (notification.isPostChange()) {
                    final ModelResource modelResource = getModelResource(notification);
                    // Fire an event ...if the modelResource != null
                    if (modelResource != null) {
                        final ModelResourceEvent event = new ModelResourceEvent(modelResource, ModelResourceEvent.ADDED, this);
                        UiPlugin.getDefault().getEventBroker().processEvent(event);
                    } // endif -- mRes found
                } // endif -- isPostChange
            }

            @Override
			public void notifyChanged( final Notification notification ) {
                if (notification instanceof ModelWorkspaceNotification
                    && ((ModelWorkspaceNotification)notification).isPreAutoBuild()) {
                    final ModelResource modelResource = getModelResource(notification);
                    // Fire a closing event ...if the modelResource != null
                    if (modelResource != null) {
                        final ModelResourceEvent event = new ModelResourceEvent(modelResource, ModelResourceEvent.CHANGED, this);
                        UiPlugin.getDefault().getEventBroker().processEvent(event);
                    }
                }
            }

            @Override
			public void notifyClean( final IProject proj ) {
                // Project clean does nothing here...
            }

            @Override
			public void notifyClosing( final ModelWorkspaceNotification notification ) {
                final ModelResource modelResource = getModelResource(notification);
                // Fire a closing event ...if the modelResource != null
                if (modelResource != null) {
                    final ModelResourceEvent event = new ModelResourceEvent(modelResource, ModelResourceEvent.CLOSING, this);
                    UiPlugin.getDefault().getEventBroker().processEvent(event);
                }
            }

            @Override
			public void notifyMove( final ModelWorkspaceNotification notification ) {
                // act on event only if a "final," after-change event:
                if (notification.isPostChange()) {
                    final ModelResource modelResource = getModelResource(notification);
                    // Fire an event ...if the modelResource != null
                    if (modelResource != null) {
                        /*
                         * - use a new kind of ModelResourceEvent for Move and Rename, that takes an extra
                         *   IPath parm representing the Old path,
                         * - Old path is: notification.getDelta().getMovedFromPath()
                         * - Then go to the code that handles MOVED (see VdbView.java)
                         */
                        final IPath oldPath = notification.getDelta().getMovedFromPath();
                        final ModelResourceEvent event = new ModelResourceEvent(modelResource, ModelResourceEvent.MOVED, this,
                                                                                oldPath);
                        UiPlugin.getDefault().getEventBroker().processEvent(event);
                    } // endif -- mRes found
                } // endif -- isPostChange
            }

            @Override
			public void notifyOpen( final ModelWorkspaceNotification notification ) {
                final ModelResource modelResource = getModelResource(notification);
                // Fire an opening event ...if the modelResource != null
                if (modelResource != null) {
                    final ModelResourceEvent event = new ModelResourceEvent(modelResource, ModelResourceEvent.OPENED, this);
                    UiPlugin.getDefault().getEventBroker().processEvent(event);
                }
            }

            @Override
			public void notifyReloaded( final ModelWorkspaceNotification notification ) {
                final ModelResource modelResource = getModelResource(notification);
                // Fire a closing event ...if the modelResource != null
                if (modelResource != null) {
                    final ModelResourceEvent event = new ModelResourceEvent(modelResource, ModelResourceEvent.RELOADED, this);
                    UiPlugin.getDefault().getEventBroker().processEvent(event);
                }
            }

            @Override
			public void notifyRemove( final ModelWorkspaceNotification notification ) {
                // act on event only if a "final," after-change event:
                if (notification.isPostChange()) {
                    final Object notifier = notification.getNotifier();
                    if (notifier instanceof IResource) {
                        // don't bother trying to get the ModelResource.. doesn't exist.
                        // use the source instead:
                        final IResource srcFile = (IResource)notifier;
                        final ModelResourceEvent event = new ModelResourceEvent(srcFile, ModelResourceEvent.REMOVED, this);
                        UiPlugin.getDefault().getEventBroker().processEvent(event);
                    } // endif -- ifile instance
                } // endif -- isPostChange
            }

            @Override
			public void notifyRename( final ModelWorkspaceNotification notification ) {
                // act on event only if a "final," after-change event:
                if (notification.isPostChange()) {
                    final ModelResource modelResource = getModelResource(notification);
                    // Fire an event ...if the modelResource != null
                    if (modelResource != null) {
                        /*
                         * - use a new kind of ModelResourceEvent for Move and Rename, that takes an extra
                         *   IPath parm representing the Old path,
                         * - Old path is: notification.getDelta().getMovedFromPath()
                         * - Then go to the code that handles MOVED (see VdbView.java)
                         */
                        final IPath oldPath = notification.getDelta().getMovedFromPath();
                        final ModelResourceEvent event = new ModelResourceEvent(modelResource, ModelResourceEvent.MOVED, this,
                                                                                oldPath);
                        UiPlugin.getDefault().getEventBroker().processEvent(event);
                    } // endif -- mRes found
                } // endif -- isPostChange
            }
        };
        ModelerCore.getModelWorkspace().addNotificationListener(modelWsListener);

        // Store default preference values if necessary
        storeDefaultPreferenceValues();
        initializeModelTableColumnUtilsFromPreferenceStore();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.ui.common.AbstractUiPlugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop( final BundleContext context ) throws Exception {
        // unregister property change listener before saving table prefs
        if (this.tablePrefPropListener != null) {
            getPreferenceStore().removePropertyChangeListener(this.tablePrefPropListener);
        }
        extractModelTableColumnUtilsToPreferenceStore();
        ModelerCore.getWorkspace().removeResourceChangeListener(projectListener);
        super.stop(context);
    }

    private void storeDefaultPreferenceValues() {
        // Store default values of preferences. Needs to be done once. Does not change current
        // values of preferences if any are already stored.
        final IPreferenceStore preferenceStore = UiPlugin.getDefault().getPreferenceStore();
        for (final PreferenceKeyAndDefaultValue element : PluginConstants.Prefs.General.PREFERENCES)
            PreferenceKeyAndDefaultValue.storePreferenceDefault(preferenceStore, element);
        UiPlugin.getDefault().savePreferences();
    }

    /**
     * SelectionCache is a mechanism for hanging onto the previous selection. This was implemented for the active help actions,
     * since clicking on active help in the welcome page fires a selection that overrides a desired selection elsewhere in the
     * workbench.
     */
    class ViewSelectionCache implements ISelectionListener {

        @Override
		public void selectionChanged( final IWorkbenchPart part,
                                      final ISelection selection ) {
            if (!(part instanceof IEditorPart)) cachedSelection = selection;
        }
    }
}
