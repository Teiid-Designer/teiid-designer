/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.BundleContext;
import org.teiid.core.designer.PluginUtil;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.core.designer.util.PluginUtilImpl;
import org.teiid.designer.runtime.DqpPlugin;
import org.teiid.designer.runtime.TeiidServerManager;
import org.teiid.designer.runtime.connection.spi.IPasswordProvider;
import org.teiid.designer.runtime.preview.PreviewManager;
import org.teiid.designer.runtime.preview.jobs.TeiidPreviewVdbCleanupJob;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.ui.connection.PreviewMissingPasswordDialog;
import org.teiid.designer.runtime.ui.server.editor.TeiidServerEditor;
import org.teiid.designer.runtime.ui.server.editor.TeiidServerEditorInput;
import org.teiid.designer.ui.common.AbstractUiPlugin;
import org.teiid.designer.ui.common.actions.ActionService;
import org.teiid.designer.ui.common.util.UiUtil;


/**
 * The main plugin class to be used in the desktop.
 *
 * @since 8.0
 */
public class DqpUiPlugin extends AbstractUiPlugin implements DqpUiConstants {

    private static final String PREFIX = I18nUtil.getPropertyPrefix(DqpUiPlugin.class);

    /**
     * Returns the string from the plugin's resource bundle, or 'key' if not found.
     */
    public static String getResourceString( String key ) {
        ResourceBundle bundle = DqpUiPlugin.getDefault().getResourceBundle();
        try {
            return (bundle != null) ? bundle.getString(key) : key;
        } catch (MissingResourceException e) {
            return key;
        }
    }

    private static String getString( String theKey ) {
        return UTIL.getStringOrKey(PREFIX + theKey);
    }

    /**
     * Used in non-Eclipse environments to identify the install location of the <code>modeler.transformation</code> plugin.
     * <strong>To be used for testing purposes only.</strong>
     * 
     * @since 6.0.0
     */
    public String testInstallPath;

    // The shared instance.
    private static DqpUiPlugin plugin;

    /**
     * Returns the shared instance.
     */
    public static DqpUiPlugin getDefault() {
        return plugin;
    }

    public static void showErrorDialog( Shell shell,
                                        Exception error ) {
        MessageDialog.openError(shell, getString("errorDialogTitle"), error.getMessage()); //$NON-NLS-1$
    }

    // Resource bundle.
    private ResourceBundle resourceBundle;

    /**
     * The constructor.
     */
    public DqpUiPlugin() {
        plugin = this;
    }
    
    void cancelCleanupJobsRequested() {
        for (Job job : Job.getJobManager().find(TeiidPreviewVdbCleanupJob.TEIID_CLEANUP_FAMILY)) {
            // canceling the job will set the job's progress monitor state to canceled and the job can notice that
            job.cancel();
        }
    }

    @Override
    protected ActionService createActionService( IWorkbenchPage workbenchPage ) {
        return null;
    }

    public Image getAnImage( String key ) {
        return getOrCreateImage(key);
    }

    private Image getOrCreateImage( String key ) {
        Image image = getImageRegistry().get(key);
        if (image == null) {
            ImageDescriptor d = getImageDescriptor(key);

            // make sure we still need to put in registry (above call
            // seems to be registering the image):
            image = getImageRegistry().get(key);
            if (image == null) {
                image = d.createImage();
                getImageRegistry().put(key, image);
            } // endif
        }
        return image;
    }

    @Override
    public PluginUtil getPluginUtil() {
        return UTIL;
    }

    /**
     * Returns the plugin's resource bundle,
     */
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    /**
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     * @since 5.0
     */
    @Override
    public void start( BundleContext context ) throws Exception {
        super.start(context);
        // Initialize logging/i18n/debugging utility
        ((PluginUtilImpl)UTIL).initializePlatformLogger(this);

        DqpPlugin.getInstance().setPasswordProvider(new PasswordProvider());
    }

    @Override
    public void stop( BundleContext context ) throws Exception {
        try {
            
        	UiUtil.runInSwtThread(new Runnable() {
    			@Override
    			public void run() {
    				shutDownServer();
    			}
    		}, false);
        	
        } finally {
            super.stop(context);
        }
    }
    
    private void shutDownServer() {
        // the server manager shutdown can take a bit of time because of the preview jobs so listen for cancel
        // button selection in order to cancel any remaining cleanup jobs
        ProgressMonitorDialog dialog = new ProgressMonitorDialog(null) {
            @Override
            protected void cancelPressed() {
                super.cancelPressed();
                cancelCleanupJobsRequested();
            }
        };

        IRunnableWithProgress runnable = new IRunnableWithProgress() {
            @Override
            public void run( IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException {
                try {
                    if (DqpPlugin.getInstance().isServerManagerStarted()) {
                        TeiidServerManager serverMgr = DqpPlugin.getInstance().getServerManager();
                        serverMgr.shutdown(monitor);
                    }

                    // shutdown PreviewManager
                    PreviewManager.getInstance().shutdown(monitor);
                } catch (InterruptedException e) {
                    monitor.setCanceled(true);
                    throw e;
                } catch (Exception e) {
                    monitor.setCanceled(true);
                    throw new InvocationTargetException(e);
                }
            }
        };

        // show dialog if DqpPlugin is available to shutdown the server manager
        if (DqpPlugin.getInstance() != null) {
            try {
                dialog.run(true, true, runnable);
            } catch (Exception e) {
                DqpUiConstants.UTIL.log(e);
            }
        }
    }
    
    class PasswordProvider implements IPasswordProvider {
       
        /**
         * {@inheritDoc}
         *
         * @see org.teiid.designer.runtime.connection.spi.IPasswordProvider#getPassword(java.lang.String, java.lang.String)
         */
        @Override
        public String getPassword( final String modelName,
                                   final String profileName ) {
            final String[] password = new String[1];

            UiUtil.runInSwtThread(new Runnable() {
                /**
                 * {@inheritDoc}
                 * 
                 * @see java.lang.Runnable#run()
                 */
                @Override
                public void run() {
                    String message = DqpUiConstants.UTIL.getString("PasswordProvider.missingPasswordMessage", //$NON-NLS-1$
                                                                   new Object[] {modelName, profileName});
                    
                    Shell workbenchShell = UiUtil.getWorkbenchShellOnlyIfUiThread();
                    PreviewMissingPasswordDialog dialog = new PreviewMissingPasswordDialog(workbenchShell, message);

                    if (dialog.open() == Window.OK) {
                        password[0] = dialog.getPassword();
                    }
                }
            },
                                  false);
            return password[0];
        }
    }

    /**
     * Open the {@link TeiidServerEditor} for the given {@link ITeiidServer}
     * 
     * @param server 
     */
    public static void editTeiidServer(ITeiidServer server) {
        IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        IWorkbenchPage page = workbenchWindow.getActivePage();
        
        try {
            TeiidServerEditorInput input = new TeiidServerEditorInput(server.getId());
            page.openEditor(input, TeiidServerEditor.EDITOR_ID);
        } catch (Exception e) {
            DqpUiConstants.UTIL.log(e);
        }
    }
}
