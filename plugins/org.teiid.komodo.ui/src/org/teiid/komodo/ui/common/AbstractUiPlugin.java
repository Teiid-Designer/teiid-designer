/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.komodo.ui.common;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.osgi.framework.BundleContext;


/**
 * The main plugin class to be used in the desktop.
 *
 * @since 1.0
 */
public abstract class AbstractUiPlugin extends org.eclipse.ui.plugin.AbstractUIPlugin {

    /** List of all opened windows in this application */
    static List<IWorkbenchWindow> windowList = new ArrayList<IWorkbenchWindow>(3);

    /** Map of action services keyed by the workbench window. */
    Map<IWorkbenchWindow, ActionService> windowServiceMap;

    /** Cache off the last active workbench window */
    IWorkbenchWindow lastActiveWorkbenchWindow;

    private FormColors formColors;
    private FormToolkit ftk;

    private static IWorkbenchPage lastPage;

    private void constructAbstractUiPlugin() {
        windowServiceMap = new HashMap<IWorkbenchWindow, ActionService>();

        // Each window will have their own action service since each window has there own selection service.
        // The services are created in the getActionService(IWorkbenchWindow) method and removed in the
        // following listener.
        IWorkbench workbench = getWorkbench();
        if (workbench == null) {
            return;
        }

        // add a perspective listener to synchronize the last active editor with each perspective
        IWorkbenchWindow theWindow = workbench.getActiveWorkbenchWindow();
        if (theWindow != null && !windowList.contains(theWindow)) {
//            theWindow.addPerspectiveListener(new EditorPerspectiveListener(theWindow));
            windowList.add(theWindow);
        }

        workbench.addWindowListener(new IWindowListener() {

            @Override
			public void windowActivated( IWorkbenchWindow theWindow ) {
                lastActiveWorkbenchWindow = theWindow;
            }

            @Override
			public void windowDeactivated( IWorkbenchWindow theWindow ) {
            }

            @Override
			public void windowClosed( IWorkbenchWindow theWindow ) {
                // remove the service from the map since the window closed
                windowServiceMap.remove(theWindow);
                windowList.remove(theWindow);
            }

            @Override
			public void windowOpened( IWorkbenchWindow theWindow ) {
                if (!windowList.contains(theWindow)) {
                    // add a listener to synchronize the active editor with the perspective
//                    theWindow.addPerspectiveListener(new EditorPerspectiveListener(theWindow));
                    windowList.add(theWindow);
                }
            }
        });
    }

    // ============================================================================================================================
    // Abstract Methods

    /**
     * Creates an <code>ActionService</code> for the given window.
     * 
     * @param theWindow the window whose <code>ActionService</code> is being requested
     * @return the newly created service
     */
    protected abstract ActionService createActionService( IWorkbenchPage page );

    /**
     * @since 4.0
     */
//    public abstract PluginUtil getPluginUtil();

    // ============================================================================================================================
    // Declared Methods

    /**
     * @since 4.0
     */
    private ImageDescriptor createImageDescriptor( final String key ) {
        try {
            final URL url = new URL(getBundle().getEntry("/").toString() + key); //$NON-NLS-1$
            final ImageDescriptor descriptor = ImageDescriptor.createFromURL(url);
            final ImageRegistry registry = getImageRegistry();
            registry.put(key, descriptor);
            return descriptor;
        } catch (final MalformedURLException err) {
//            getPluginUtil().log(err);
            return null;
        }
    }

    /**
     * Gets the <code>ActionService</code> associated with the given <code>IWorkbenchWindow</code>. If a service does not exist,
     * one is created.
     * 
     * @param theWindow the window whose <code>ActionService</code> is being requested
     * @return the action service
     */
    public ActionService getActionService( IWorkbenchPage pg ) {
        if (this.windowServiceMap == null) {
            constructAbstractUiPlugin();
        }

        if (pg != null) {
            // save off any valid pages that come through
            lastPage = pg;
        } else {
            // try to recover a valid page so we can continue:
            pg = lastPage;
        } // endif
        
        if( pg != null ) {
	        IWorkbenchWindow theWindow = pg.getWorkbenchWindow();
	        ActionService service = windowServiceMap.get(theWindow);
	
	        if (service == null) {
	            service = createActionService(pg);
	            windowServiceMap.put(theWindow, service);
	        }
	        return service;
        }

        return null;
    }

    /**
     * Retrieves the image associated with the specified key from the {@link org.eclipse.jface.resource.ImageRegistry image
     * registry}, creating the image and registering it if it doesn't already exist. A null key will cause the descriptor for the
     * "No image" image to be returned.
     * 
     * @param key The key associated with the image to retrieve. This must be in the form of the path to the image file relative
     *        to this plug-in's folder; may be null.
     * @return The image associated with the specified key.
     * @since 4.0
     */
    public final Image getImage( final String key ) {
        final ImageRegistry registry = getImageRegistry();
        Image img = registry.get(key);
        if (img != null) {
            return img;
        }
        try {
            createImageDescriptor(key);
            img = registry.get(key);
            return img != null ? img : ImageDescriptor.getMissingImageDescriptor().createImage();
        } catch (final SWTException err) {
//            getPluginUtil().log(IStatus.WARNING, err, err.getMessage());
            return null;
        }
    }

    /**
     * Retrieves the image descriptor associated with the specified key from the {@link org.eclipse.jface.resource.ImageRegistry
     * image registry}, creating the descriptor and registering it if it doesn't already exist. A null key will cause the
     * descriptor for the "No image" image to be returned.
     * 
     * @param key The key associated with the image descriptor to retrieve. This must be in the form of the path to the image file
     *        relative to this plug-in's folder; may be null.
     * @return The image descriptor associated with the specified key.
     * @since 4.0
     */
    public final ImageDescriptor getImageDescriptor( final String key ) {
        final ImageRegistry registry = getImageRegistry();
        final ImageDescriptor descriptor = registry.getDescriptor(key);
        if (descriptor != null) {
            return descriptor;
        }
        return createImageDescriptor(key);
    }

    /**
     * Convenience method to obtain the last active IWorkbenchWindow. Since Workbench.getActiveWorkbenchWindow frequently returns
     * null, this method will return the last WorkbenchWindow that was activated. The method will always try to obtain the active
     * IWorkbenchWindow from the Workbench, but if it is null the method will return the cached value.
     * 
     * @return the last active WorkbenchWindow, if one was ever activated.
     */
    public final IWorkbenchWindow getCurrentWorkbenchWindow() {
        IWorkbenchWindow result = getWorkbench().getActiveWorkbenchWindow();
        if (result == null) {
            result = lastActiveWorkbenchWindow;
        }
        return result;
    }
    
//    /**
//     * Obtains the current values of all plugin preferences.
//     * <p>
//     * <strong>This method should be used instead of <code>getPluginPreferences()</code>.</strong>
//     * 
//     * @return the preferences (never <code>null</code>)
//     */
//    public final IEclipsePreferences getPreferences() {
//        return ModelerCore.getPreferences(getBundle().getSymbolicName());
//    }
//    
//    /**
//     * Persists plugin preferences.
//     */
//    public final void savePreferences() {
//        try {
//            ModelerCore.savePreferences(getBundle().getSymbolicName());
//        } catch (BackingStoreException e) {
//            getPluginUtil().log(e);
//        }
//    }
//
//    /**
//     * Obtains the default values of all plugin preferences.
//     * <p>
//     * <strong>This method should be used instead of <code>getPluginPreferences()</code>.</strong>
//     * 
//     * @return the preferences (never <code>null</code>)
//     */
//    public final IEclipsePreferences getDefaultPreferences() {
//        return ModelerCore.getDefaultPreferences(getBundle().getSymbolicName());
//    }
    
    /**
     * Indicates if the image registry contains an image accessed by the specified identifier.
     * 
     * @param imageId the identifier of the image
     * @return <code>true</code> if exists; <code>false</code> otherwise.
     */
    public boolean isImageRegistered( final String imageId ) {
        return (getImageRegistry().get(imageId) != null);
    }

    /**
     * @since 4.0
     */
    protected void registerPluginImage( final String plugin,
                                        final String key ) {
        final ImageRegistry registry = getImageRegistry();
        final URL viewsUrl = Platform.getBundle(plugin).getEntry("/"); //$NON-NLS-1$
        try {
            final URL url = new URL(viewsUrl + key);
            final ImageDescriptor descriptor = ImageDescriptor.createFromURL(url);
            registry.put(key, descriptor);
        } catch (final MalformedURLException err) {
//            getPluginUtil().log(err);
        }
    }

    /**
     * Puts the specified <code>Image</code> in this plugin's image registry with the specified identifier.
     * 
     * @param key the identifier of the image in the registry
     * @param image the image being put in the image registry
     */
    public void registerPluginImage( final String key,
                                     final Image image ) {
        final ImageRegistry registry = getImageRegistry();
        //registry.put(key, new ImageImageDescriptor(image));
    }

    /**
     * <p>
     * {@inheritDoc}
     * </p>
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop( BundleContext context ) throws Exception {
        // shutdown action services before shutdown
        if (this.windowServiceMap != null) {
            Iterator<ActionService> itr = windowServiceMap.values().iterator();

            while (itr.hasNext()) {
                ActionService actionService = itr.next();
                actionService.shutdown();
            }
        }

        super.stop(context);
    }

    public FormToolkit getFormToolkit( Display display ) {
        if (ftk == null) {
            ftk = new FormToolkit(getFormColors(display));
        } // endif

        return ftk;
    }

    private FormColors getFormColors( Display display ) {
        if (formColors == null) {
            formColors = new FormColors(display);
            formColors.markShared();
        }
        return formColors;
    }

//    /**
//     * Indicates if the specified {@link IProductContext} is supported.
//     * 
//     * @param theContext the context whose support status is being requested
//     * @return <code>true</code> if supported; <code>false</code> otherwise.
//     * @since 4.3
//     */
//    public boolean isProductContextSupported( IProductContext theContext ) {
//        return ProductCustomizerMgr.getInstance().supports(theContext);
//    }
//
//    /**
//     * Indicates if the specified value is supported by the given {@link IProductContext}.
//     * 
//     * @param theContext the context containing the value
//     * @param theValue the value whose support status is being requested
//     * @return <code>true</code> if supported; <code>false</code> otherwise.
//     * @since 4.3
//     */
//    public boolean isProductContextValueSupported( IProductContext theContext,
//                                                   Object theValue ) {
//        return ProductCustomizerMgr.getInstance().supports(theContext, theValue);
//    }

    public static IWorkbenchPage getLastValidPage() {
        return lastPage;
    }

}
