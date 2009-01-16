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
package com.metamatrix.modeler.internal.ui.product;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PerspectiveAdapter;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.product.IModelerProductContexts;
import com.metamatrix.modeler.ui.product.IVetoableShutdownListener;
import com.metamatrix.ui.internal.product.ProductCustomizerMgr;

/**
 * @since 4.4
 */
public class ModelerRcpWindowAdvisor extends WorkbenchWindowAdvisor
    implements IModelerProductContexts.Window, UiConstants, UiConstants.ExtensionPoints {

    private static final String DESIGNER_WINDOW_SECTION = "designerWindowSection"; //$NON-NLS-1$

    /**
     * Setting indicating if the current launch is not the first launch.
     */
    private static final String NOT_FIRST_LAUNCH = "windowAdvisor.notFirstLaunch"; //$NON-NLS-1$

    /**
     * Setting for the window x position.
     */
    private static final String WINDOW_X = "windowAdvisor.windowX"; //$NON-NLS-1$

    /**
     * Setting for the window y positions.
     */
    private static final String WINDOW_Y = "windowAdvisor.windowY"; //$NON-NLS-1$

    /**
     * Setting for the window width.
     */
    private static final String WINDOW_WIDTH = "windowAdvisor.windowWidth"; //$NON-NLS-1$

    /**
     * Setting for the window height.
     */
    private static final String WINDOW_HEIGHT = "windowAdvisor.windowHeight"; //$NON-NLS-1$

    /**
     * Constructs a Modeler window advisor.
     * 
     * @param theConfigurer the window configurer
     * @since 4.4
     */
    public ModelerRcpWindowAdvisor( IWorkbenchWindowConfigurer theConfigurer ) {
        super(theConfigurer);
    }

    private IDialogSettings getDialogSettings() {
        IDialogSettings temp = UiPlugin.getDefault().getDialogSettings();
        IDialogSettings settings = temp.getSection(DESIGNER_WINDOW_SECTION);

        if (settings == null) {
            settings = temp.addNewSection(DESIGNER_WINDOW_SECTION);
        }

        return settings;
    }

    private Shell getShell() {
        return getWindow().getShell();
    }

    private IWorkbenchWindow getWindow() {
        return getWindowConfigurer().getWindow();
    }

    /**
     * @see org.eclipse.ui.application.WorkbenchWindowAdvisor#postWindowClose()
     * @since 5.0
     */
    @Override
    public void postWindowClose() {
        // save window bounds
        Rectangle bounds = getShell().getBounds();
        IDialogSettings settings = getDialogSettings();

        settings.put(WINDOW_X, bounds.x);
        settings.put(WINDOW_Y, bounds.y);
        settings.put(WINDOW_WIDTH, bounds.width);
        settings.put(WINDOW_HEIGHT, bounds.height);
    }

    /**
     * @see org.eclipse.ui.application.WorkbenchWindowAdvisor#postWindowCreate()
     * @since 5.0
     */
    @Override
    public void postWindowCreate() {
        IDialogSettings settings = getDialogSettings();

        // restore window bounds if previously launched
        if (settings.getBoolean(NOT_FIRST_LAUNCH)) {
            Shell shell = getShell();
            int x = settings.getInt(WINDOW_X);
            int y = settings.getInt(WINDOW_Y);

            if ((x != 0) && (y != 0)) {
                shell.setBounds(x, y, settings.getInt(WINDOW_WIDTH), settings.getInt(WINDOW_HEIGHT));
            }
        } else {
            // set size to full display if first launch
            getShell().setMaximized(true);
            settings.put(NOT_FIRST_LAUNCH, true);
        }
    }

    /**
     * @see org.eclipse.ui.application.WorkbenchWindowAdvisor#preWindowOpen()
     * @since 4.3
     */
    @Override
    public void preWindowOpen() {
        super.preWindowOpen();

        UiPlugin plugin = UiPlugin.getDefault();
        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();

        // show menu bar if supported by product
        configurer.setShowMenuBar(plugin.isProductContextSupported(MENU_BAR));

        // show toolbar if supported by product
        configurer.setShowCoolBar(plugin.isProductContextSupported(COOL_BAR));

        // show perspective bar if supported by product
        configurer.setShowPerspectiveBar(plugin.isProductContextSupported(PERSPECTIVE_BAR));

        // show status line if supported by product
        configurer.setShowStatusLine(plugin.isProductContextSupported(STATUS_BAR));

        // show progress bar if supported by product
        configurer.setShowProgressIndicator(plugin.isProductContextSupported(PROGRESS_BAR));

        // show fast view bars if supported by product
        configurer.setShowFastViewBars(plugin.isProductContextSupported(FAST_VIEW_BARS));

        // set window title
        configurer.setTitle(getApplicationTitle());

        hookToPerspectiveAndPartListeners(configurer);
    }

    /**
     * Hooks the listeners needed on the window
     * 
     * @param configurer
     */
    private void hookToPerspectiveAndPartListeners( IWorkbenchWindowConfigurer configurer ) {
        // hook up the listeners to update the window title

        configurer.getWindow().addPerspectiveListener(new PerspectiveAdapter() {
            /**
             * @see org.eclipse.ui.IPerspectiveListener3#perspectiveOpened(org.eclipse.ui.IWorkbenchPage,
             *      org.eclipse.ui.IPerspectiveDescriptor)
             */
            @Override
            public void perspectiveOpened( IWorkbenchPage page,
                                           IPerspectiveDescriptor perspective ) {
                handlePerspectiveEvent(UiConstants.PartEventID.OPENED_ID, page, perspective);
            }

            /**
             * @see org.eclipse.ui.IPerspectiveListener3#perspectiveClosed(org.eclipse.ui.IWorkbenchPage,
             *      org.eclipse.ui.IPerspectiveDescriptor)
             */
            @Override
            public void perspectiveClosed( IWorkbenchPage page,
                                           IPerspectiveDescriptor perspective ) {
                handlePerspectiveEvent(UiConstants.PartEventID.CLOSED_ID, page, perspective);
            }

            /**
             * @see org.eclipse.ui.IPerspectiveListener2#perspectiveChanged(org.eclipse.ui.IWorkbenchPage,
             *      org.eclipse.ui.IPerspectiveDescriptor, org.eclipse.ui.IWorkbenchPartReference, java.lang.String)
             */
            @Override
            public void perspectiveChanged( IWorkbenchPage page,
                                            IPerspectiveDescriptor perspective,
                                            IWorkbenchPartReference partRef,
                                            String changeId ) {
                handlePerspectiveEvent(UiConstants.PartEventID.CHANGED_ID, page, perspective);
            }

            /**
             * @see org.eclipse.ui.IPerspectiveListener#perspectiveActivated(org.eclipse.ui.IWorkbenchPage,
             *      org.eclipse.ui.IPerspectiveDescriptor)
             */
            @Override
            public void perspectiveActivated( IWorkbenchPage page,
                                              IPerspectiveDescriptor perspective ) {
                handlePerspectiveEvent(UiConstants.PartEventID.ACTIVATED_ID, page, perspective);
            }

            /**
             * @see org.eclipse.ui.IPerspectiveListener#perspectiveChanged(org.eclipse.ui.IWorkbenchPage,
             *      org.eclipse.ui.IPerspectiveDescriptor, java.lang.String)
             */
            @Override
            public void perspectiveChanged( IWorkbenchPage page,
                                            IPerspectiveDescriptor perspective,
                                            String changeId ) {
                handlePerspectiveEvent(UiConstants.PartEventID.CHANGED_ID, page, perspective);
            }

            /**
             * @see org.eclipse.ui.IPerspectiveListener3#perspectiveDeactivated(org.eclipse.ui.IWorkbenchPage,
             *      org.eclipse.ui.IPerspectiveDescriptor)
             */
            @Override
            public void perspectiveDeactivated( IWorkbenchPage page,
                                                IPerspectiveDescriptor perspective ) {
                handlePerspectiveEvent(UiConstants.PartEventID.DEACTIVATED_ID, page, perspective);
            }

            /**
             * @see org.eclipse.ui.IPerspectiveListener3#perspectiveSavedAs(org.eclipse.ui.IWorkbenchPage,
             *      org.eclipse.ui.IPerspectiveDescriptor, org.eclipse.ui.IPerspectiveDescriptor)
             */
            @Override
            public void perspectiveSavedAs( IWorkbenchPage page,
                                            IPerspectiveDescriptor oldPerspective,
                                            IPerspectiveDescriptor newPerspective ) {
                handlePerspectiveEvent(UiConstants.PartEventID.SAVED_AS_ID, page, newPerspective);
            }
        });
        configurer.getWindow().getPartService().addPartListener(new IPartListener2() {
            public void partActivated( IWorkbenchPartReference ref ) {
                if (ref instanceof IEditorReference) {
                    handlePartEvent(UiConstants.PartEventID.ACTIVATED_ID, ref);
                }
            }

            public void partBroughtToTop( IWorkbenchPartReference ref ) {
                if (ref instanceof IEditorReference) {
                    handlePartEvent(UiConstants.PartEventID.BROUGHT_TO_TOP_ID, ref);
                }
            }

            public void partClosed( IWorkbenchPartReference ref ) {
                handlePartEvent(UiConstants.PartEventID.CLOSED_ID, ref);
            }

            public void partDeactivated( IWorkbenchPartReference ref ) {
                handlePartEvent(UiConstants.PartEventID.DEACTIVATED_ID, ref);
            }

            public void partOpened( IWorkbenchPartReference ref ) {
                handlePartEvent(UiConstants.PartEventID.OPENED_ID, ref);
            }

            public void partHidden( IWorkbenchPartReference ref ) {
                handlePartEvent(UiConstants.PartEventID.HIDDEN_ID, ref);
            }

            public void partVisible( IWorkbenchPartReference ref ) {
                handlePartEvent(UiConstants.PartEventID.VISIBLE_ID, ref);
            }

            public void partInputChanged( IWorkbenchPartReference ref ) {
                handlePartEvent(UiConstants.PartEventID.INPUT_CHANGED_ID, ref);
            }
        });
    }

    void handlePartEvent( int eventId,
                          IWorkbenchPartReference ref ) {
        ProductCustomizerMgr.getInstance().getProductCharacteristics().handlePartEvent(eventId, ref);
    }

    void handlePerspectiveEvent( int eventId,
                                 IWorkbenchPage page,
                                 IPerspectiveDescriptor perspective ) {
        ProductCustomizerMgr.getInstance().getProductCharacteristics().handlePerspectiveEvent(eventId, page, perspective);
    }

    /**
     * @see org.eclipse.ui.internal.ide.IDEWorkbenchWindowAdvisor#preWindowShellClose()
     * @since 5.0
     */
    @Override
    public boolean preWindowShellClose() {
        boolean result = super.preWindowShellClose();

        if (result) {
            // if call to super returns true, make sure shutdown extensions want to continue
            if (continueWithShutdown()) {
                // if hidden project-centric product that is saving state, all editors must be closed prior to
                // the Eclipse's EditorManager saving it's state. The editors can't be restored since their
                // workspace location changes each time Eclipse is restarted.
                // this must be done after the call to super as this gives the chance for the
                // IViewParts and editors to save their state first.
                if (getWindowConfigurer().getWorkbenchConfigurer().getSaveAndRestore()) {
                    if (ProductCustomizerMgr.getInstance().getProductCharacteristics().isHiddenProjectCentric()) {
                        // if no perspective is open when the window is closed, there will not be an active page
                        if (getWindowConfigurer().getWindow().getActivePage() != null) {
                            getWindowConfigurer().getWindow().getActivePage().closeAllEditors(true);
                        }
                    }
                }
            } else {
                result = false;
            }
        }

        return result;
    }

    /**
     * @see org.eclipse.ui.application.WorkbenchWindowAdvisor#createActionBarAdvisor(org.eclipse.ui.application.IActionBarConfigurer)
     * @since 4.4
     */
    @Override
    public ActionBarAdvisor createActionBarAdvisor( IActionBarConfigurer theConfigurer ) {
        return new ModelerRcpActionBarAdvisor(theConfigurer);
    }

    /**
     * Obtains the localized title of the application.
     * 
     * @return the title
     * @since 4.4
     */
    protected String getApplicationTitle() {
        return Util.getStringOrKey(I18nUtil.getPropertyPrefix(getClass()) + "applicationTitle"); //$NON-NLS-1$
    }

    /**
     * Indicates if all the {@link IVetoableShutdownListener}s want to continue with shutdown.
     * 
     * @return <code>true</code> if shutdown should continue; <code>false</code> otherwise.
     * @since 5.0
     */
    private boolean continueWithShutdown() {
        boolean result = true;
        IVetoableShutdownListener[] listeners = getVetoableShutdownListeners();

        if (listeners.length != 0) {
            for (int i = 0; i < listeners.length; ++i) {
                try {
                    listeners[i].setWindow(getWindow());

                    if (!listeners[i].continueShutdown()) {
                        result = false;
                        break;
                    }
                } catch (Exception theException) {
                    final String PREFIX = I18nUtil.getPropertyPrefix(ModelerRcpWindowAdvisor.class);
                    Util.log(IStatus.ERROR, theException, Util.getString(PREFIX + "shutdownListenerError", //$NON-NLS-1$
                                                                         listeners[i].getClass().getName()));
                }
            }
        }

        return result;
    }

    /**
     * Process the {@link IVetoableShutdownListener} extensions.
     * 
     * @return the listeners (never <code>null</code>)
     * @since 5.0
     */
    private IVetoableShutdownListener[] getVetoableShutdownListeners() {
        IVetoableShutdownListener[] result = new IVetoableShutdownListener[0];
        final String PREFIX = I18nUtil.getPropertyPrefix(ModelerRcpWindowAdvisor.class);

        // get the general preference extension point from the plugin class
        IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(PLUGIN_ID, VetoableShutdownListener.ID);

        if (extensionPoint == null) {
            Util.log(IStatus.ERROR, Util.getString(PREFIX + "noExtensionPointFound", VetoableShutdownListener.ID)); //$NON-NLS-1$
        } else {
            try {
                // get all extensions
                IExtension[] extensions = extensionPoint.getExtensions();

                if (extensions.length != 0) {
                    List temp = new ArrayList(extensions.length);

                    for (int i = 0; i < extensions.length; ++i) {
                        IConfigurationElement[] elements = extensions[i].getConfigurationElements();

                        for (int j = 0; j < elements.length; ++j) {
                            try {
                                Object obj = elements[j].createExecutableExtension(VetoableShutdownListener.CLASS_ATTRIBUTE);

                                if (obj instanceof IVetoableShutdownListener) {
                                    temp.add(obj);
                                } else {
                                    // listener is wrong type
                                    Object[] params = new Object[] {
                                        elements[j].getAttribute(VetoableShutdownListener.CLASS_ATTRIBUTE),
                                        elements[j].getDeclaringExtension().getUniqueIdentifier()};
                                    Util.log(IStatus.ERROR, Util.getString(PREFIX + "incorrectClass", params)); //$NON-NLS-1$
                                }
                            } catch (Exception theException) {
                                // problem constructing the listener
                                Object[] params = new Object[] {
                                    elements[j].getAttribute(VetoableShutdownListener.CLASS_ATTRIBUTE),
                                    elements[j].getDeclaringExtension().getUniqueIdentifier()};
                                Util.log(IStatus.ERROR, Util.getString(PREFIX + "initError", params)); //$NON-NLS-1$
                            }
                        }
                    }

                    temp.toArray(result = new IVetoableShutdownListener[temp.size()]);
                }
            } catch (Exception theException) {
                Util.log(IStatus.ERROR, theException, Util.getString(PREFIX + "unexpectedErrorProcessingExtensions", //$NON-NLS-1$
                                                                     VetoableShutdownListener.ID));
            }
        }

        return result;
    }
}
