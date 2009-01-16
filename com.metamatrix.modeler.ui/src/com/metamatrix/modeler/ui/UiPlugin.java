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
package com.metamatrix.modeler.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.osgi.framework.BundleContext;
import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.CoreConstants.Debug;
import com.metamatrix.core.event.EventBroker;
import com.metamatrix.core.event.SynchEventBroker;
import com.metamatrix.core.util.PluginUtilImpl;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.notification.util.NotificationUtilities;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceNotification;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceNotificationListener;
import com.metamatrix.modeler.internal.core.workspace.ModelWorkspaceManager;
import com.metamatrix.modeler.internal.ui.PluginConstants;
import com.metamatrix.modeler.internal.ui.editors.ModelEditorProjectListener;
import com.metamatrix.modeler.internal.ui.favorites.EObjectModelerCache;
import com.metamatrix.modeler.internal.ui.settings.ModelerDialogSettingsInitializer;
import com.metamatrix.modeler.internal.ui.table.EObjectPropertiesOrderPreferences;
import com.metamatrix.modeler.internal.ui.undo.ModelerUndoManager;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelerNotificationHelper;
import com.metamatrix.modeler.ui.actions.ModelerActionService;
import com.metamatrix.modeler.ui.event.ModelResourceEvent;
import com.metamatrix.modeler.ui.product.IModelerProductContexts;
import com.metamatrix.ui.AbstractUiPlugin;
import com.metamatrix.ui.PreferenceKeyAndDefaultValue;
import com.metamatrix.ui.actions.ActionService;
import com.metamatrix.ui.internal.product.ProductCustomizerMgr;
import com.metamatrix.ui.internal.util.UiUtil;

/**
 * The main plugin class to be used in the desktop.
 * 
 * @since 4.0
 */
public final class UiPlugin extends AbstractUiPlugin
    implements Debug, PluginConstants, UiConstants, UiConstants.ProductInfo, UiConstants.ProductInfo.Capabilities,
    IModelerProductContexts {

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

    public static MultiStatus createMultiStatus( final String desc ) {
        return new MultiStatus(PLUGIN_ID, 0, desc, null);
    }

    public static void addStatus( final MultiStatus parent,
                                  final String msg,
                                  final int severity,
                                  final Throwable throwable ) {
        if (parent != null) {
            final Status child = new Status(severity, PLUGIN_ID, 0, msg, throwable);
            parent.add(child);
        }
    }

    public static void addStatus( final MultiStatus parent,
                                  final String msg,
                                  final int severity ) {
        if (parent != null) {
            final Status child = new Status(severity, PLUGIN_ID, 0, msg, null);
            parent.add(child);
        }

    }

    // This plugin's EventBroker.
    private EventBroker eventBroker;

    private EObjectModelerCache eObjCache;

    // The previous selection in this plugin's workbench
    ISelection cachedSelection = null;

    // ImageDescriptors
    ImageDescriptor errorDecoratorImage;
    ImageDescriptor warningDecoratorImage;

    private ModelEditorProjectListener projectListener = new ModelEditorProjectListener();
    private EObjectPropertiesOrderPreferences eObjectPropertiesOrderPreferences;

    private ModelerActionService service;

    /**
     * The constructor.
     * 
     * @since 4.0
     */
    public UiPlugin() {
        UiPlugin.plugin = this;
    }

    /**
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     * @since 5.0
     */
    @Override
    public void start( BundleContext context ) throws Exception {
        Util.debug(PLUGIN_ACTIVATION, "Plug-in " + getClass().getName() + " activated."); //$NON-NLS-1$ //$NON-NLS-2$
        super.start(context);

        // Initialize logging/i18n utility
        ((PluginUtilImpl)Util).initializePlatformLogger(this);

        try {
            UiUtil.runInSwtThread(new Runnable() {

                public void run() {
                    UiPlugin.this.errorDecoratorImage = getImageDescriptor(Images.ERROR_DECORATOR);
                    UiPlugin.this.warningDecoratorImage = getImageDescriptor(Images.WARNING_DECORATOR);
                }
            }, true);
        } catch (final Throwable err) {
            throw new CoreException(new Status(IStatus.ERROR, PLUGIN_ID, 0, err.getLocalizedMessage(), err));
        }

        // Initialize product customizer
        try {
            ProductCustomizerMgr.getInstance().loadCustomizations();
        } catch (Exception theException) {
            // just log. no need to stop plugin startup for customization problem
            Util.log(theException);
        }

        // Initialize the ModelerUndoManager
        ModelerUndoManager.getInstance();

        // Initialize the ModelEditorProjectListener
        ResourcesPlugin.getWorkspace().addResourceChangeListener(projectListener);

        // Initialize the NotificationUtilities INotificationHandler to the modeler;
        NotificationUtilities.setNotificationHelper(new ModelerNotificationHelper());

        // Initialize the ModelWorkspaceManager
        ModelWorkspaceManager.getModelWorkspaceManager();

        // Register a listener for ModelWorkspaceNotifications ...
        final ModelWorkspaceNotificationListener modelWsListener = new ModelWorkspaceNotificationListener() {

            public void notifyAdd( ModelWorkspaceNotification notification ) {
                // act on event only if a "final," after-change event:
                if (notification.isPostChange()) {
                    final ModelResource modelResource = getModelResource(notification);
                    // Fire an event ...if the modelResource != null
                    if (modelResource != null) {
                        ModelResourceEvent event = new ModelResourceEvent(modelResource, ModelResourceEvent.ADDED, this);
                        UiPlugin.getDefault().getEventBroker().processEvent(event);
                    } // endif -- mRes found
                } // endif -- isPostChange
            }

            public void notifyRemove( ModelWorkspaceNotification notification ) {
                // act on event only if a "final," after-change event:
                if (notification.isPostChange()) {
                    Object notifier = notification.getNotifier();
                    if (notifier instanceof IResource) {
                        // don't bother trying to get the ModelResource.. doesn't exist.
                        // use the source instead:
                        IResource srcFile = (IResource)notifier;
                        ModelResourceEvent event = new ModelResourceEvent(srcFile, ModelResourceEvent.REMOVED, this);
                        UiPlugin.getDefault().getEventBroker().processEvent(event);
                    } // endif -- ifile instance
                } // endif -- isPostChange
            }

            public void notifyRename( ModelWorkspaceNotification notification ) {
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
                        IPath oldPath = notification.getDelta().getMovedFromPath();
                        ModelResourceEvent event = new ModelResourceEvent(modelResource, ModelResourceEvent.MOVED, this, oldPath);
                        UiPlugin.getDefault().getEventBroker().processEvent(event);
                    } // endif -- mRes found
                } // endif -- isPostChange
            }

            public void notifyMove( ModelWorkspaceNotification notification ) {
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
                        IPath oldPath = notification.getDelta().getMovedFromPath();
                        ModelResourceEvent event = new ModelResourceEvent(modelResource, ModelResourceEvent.MOVED, this, oldPath);
                        UiPlugin.getDefault().getEventBroker().processEvent(event);
                    } // endif -- mRes found
                } // endif -- isPostChange
            }

            public void notifyOpen( ModelWorkspaceNotification notification ) {
                final ModelResource modelResource = getModelResource(notification);
                // Fire an opening event ...if the modelResource != null
                if (modelResource != null) {
                    ModelResourceEvent event = new ModelResourceEvent(modelResource, ModelResourceEvent.OPENED, this);
                    UiPlugin.getDefault().getEventBroker().processEvent(event);
                }
            }

            public void notifyClosing( ModelWorkspaceNotification notification ) {
                final ModelResource modelResource = getModelResource(notification);
                // Fire a closing event ...if the modelResource != null
                if (modelResource != null) {
                    ModelResourceEvent event = new ModelResourceEvent(modelResource, ModelResourceEvent.CLOSING, this);
                    UiPlugin.getDefault().getEventBroker().processEvent(event);
                }
            }

            public void notifyChanged( Notification notification ) {
                if (notification instanceof ModelWorkspaceNotification
                    && ((ModelWorkspaceNotification)notification).isPreAutoBuild()) {
                    final ModelResource modelResource = getModelResource(notification);
                    // Fire a closing event ...if the modelResource != null
                    if (modelResource != null) {
                        ModelResourceEvent event = new ModelResourceEvent(modelResource, ModelResourceEvent.CHANGED, this);
                        UiPlugin.getDefault().getEventBroker().processEvent(event);
                    }
                }
            }

            public void notifyReloaded( ModelWorkspaceNotification notification ) {
                final ModelResource modelResource = getModelResource(notification);
                // Fire a closing event ...if the modelResource != null
                if (modelResource != null) {
                    ModelResourceEvent event = new ModelResourceEvent(modelResource, ModelResourceEvent.RELOADED, this);
                    UiPlugin.getDefault().getEventBroker().processEvent(event);
                }
            }

            public void notifyClean( IProject proj ) {
                // Project clean does nothing here...
            }

            protected ModelResource getModelResource( Notification notification ) {
                final IResource resource = (IResource)notification.getNotifier();
                return ModelerCore.getModelWorkspace().findModelResource(resource);
            }
        };
        ModelerCore.getModelWorkspace().addNotificationListener(modelWsListener);

        // Store default preference values if necessary
        storeDefaultPreferenceValues();
        initializeModelTableColumnUtilsFromPreferenceStore();
        initializeDialogSettings();
    }

    private void initializeModelTableColumnUtilsFromPreferenceStore() {
        getEObjectPropertiesOrderPreferences().initializeFromString(getPreferenceStore().getString(UiConstants.TableEditorAttributes.COLUMN_ORDER));
    }

    private void initializeDialogSettings() {
        ModelerDialogSettingsInitializer initializer = new ModelerDialogSettingsInitializer();
        initializer.initialize();
    }

    /**
     * @see com.metamatrix.ui.AbstractUiPlugin#createActionService(org.eclipse.ui.IWorkbenchPage)
     * @since 4.0
     */
    @Override
    protected ActionService createActionService( IWorkbenchPage page ) {
        if (this.service == null) {
            page.getWorkbenchWindow().getSelectionService().addSelectionListener(new ViewSelectionCache());
            this.service = new ModelerActionService(page);
            this.service.initializeGlobalActions();
        }

        return this.service;
    }

    /**
     * @see com.metamatrix.ui.AbstractUiPlugin#getPluginUtil()
     * @since 4.0
     */
    @Override
    public PluginUtil getPluginUtil() {
        return Util;
    }

    public EventBroker getEventBroker() {
        if (this.eventBroker == null) {
            eventBroker = new SynchEventBroker(); // only visible in debug
        }
        return eventBroker;
    }

    /**
     * Obtains the cache used to hold {@link org.eclipse.emf.ecore.EObject}s.
     * 
     * @return the shared cache
     * @since 4.2
     */
    public EObjectModelerCache getEObjectCache() {
        if (this.eObjCache == null) {
            this.eObjCache = new EObjectModelerCache();
        }

        return this.eObjCache;
    }

    private void storeDefaultPreferenceValues() {
        // Store default values of preferences. Needs to be done once. Does not change current
        // values of preferences if any are already stored.
        IPreferenceStore preferenceStore = UiPlugin.getDefault().getPreferenceStore();
        for (int i = 0; i < PluginConstants.Prefs.General.PREFERENCES.length; i++) {
            PreferenceKeyAndDefaultValue.storePreferenceDefault(preferenceStore, PluginConstants.Prefs.General.PREFERENCES[i]);
        }
        UiPlugin.getDefault().savePluginPreferences();
    }

    public ImageDescriptor getErrorDecoratorImage() {
        return errorDecoratorImage;
    }

    public ImageDescriptor getWarningDecoratorImage() {
        return warningDecoratorImage;
    }

    public Image getProjectImage() {
        return getImage(Images.MODEL_PROJECT);
    }

    public Image getSimpleProjectImage() {
        return getImage(Images.SIMPLE_PROJECT);
    }

    /**
     * Return the last selection that occurred in a ViewPart (rather than in an editor).
     * 
     * @return
     */
    public ISelection getPreviousViewSelection() {
        return cachedSelection;
    }

    public EObjectPropertiesOrderPreferences getEObjectPropertiesOrderPreferences() {
        if (this.eObjectPropertiesOrderPreferences == null) {
            this.eObjectPropertiesOrderPreferences = new EObjectPropertiesOrderPreferences();
        }

        return this.eObjectPropertiesOrderPreferences;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.ui.AbstractUiPlugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop( BundleContext context ) throws Exception {
        extractModelTableColumnUtilsToPreferenceStore();
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(projectListener);
        super.stop(context);
    }

    private void extractModelTableColumnUtilsToPreferenceStore() {
        getPreferenceStore().setValue(UiConstants.TableEditorAttributes.COLUMN_ORDER,
                                      getEObjectPropertiesOrderPreferences().toString());
    }

    /**
     * SelectionCache is a mechanism for hanging onto the previous selection. This was implemented for the active help actions,
     * since clicking on active help in the welcome page fires a selection that overrides a desired selection elsewhere in the
     * workbench.
     */
    class ViewSelectionCache implements ISelectionListener {

        public void selectionChanged( IWorkbenchPart part,
                                      ISelection selection ) {
            if (!(part instanceof IEditorPart)) {
                cachedSelection = selection;
            }
        }
    }
}
