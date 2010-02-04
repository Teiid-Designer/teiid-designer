/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.internal;

//import java.net.URL;

//import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import com.metamatrix.ui.UiPlugin;


/** 
 * @since 4.3
 */
public class PluginImages {

//    private final static ImageRegistry PLUGIN_REGISTRY =
//        UiPlugin.getDefault().getImageRegistry();

    public final static String ICONS_PATH = "icons/full/"; //$NON-NLS-1$

    private static final String PATH_OBJ = ICONS_PATH + "obj16/"; //$NON-NLS-1$
    private static final String PATH_LCL = ICONS_PATH + "elcl16/"; //$NON-NLS-1$
    private static final String PATH_LCL_DISABLED = ICONS_PATH + "dlcl16/"; //$NON-NLS-1$
    private static final String PATH_OVR = ICONS_PATH + "ovr16/"; //$NON-NLS-1$
    private static final String PATH_EVENTS = ICONS_PATH + "eview16/"; //$NON-NLS-1$


    public static final ImageDescriptor DESC_ERROR_ST_OBJ =
        create(PATH_OBJ, "error_st_obj.gif"); //$NON-NLS-1$
    public static final ImageDescriptor DESC_ERROR_STACK_OBJ =
        create(PATH_OBJ, "error_stack.gif"); //$NON-NLS-1$
    public static final ImageDescriptor DESC_INFO_ST_OBJ =
        create(PATH_OBJ, "info_st_obj.gif"); //$NON-NLS-1$
    public static final ImageDescriptor DESC_OK_ST_OBJ =
        create(PATH_OBJ, "ok_st_obj.gif"); //$NON-NLS-1$
    public static final ImageDescriptor DESC_WARNING_ST_OBJ =
        create(PATH_OBJ, "warning_st_obj.gif"); //$NON-NLS-1$

    /*
     * Local tool bar image descriptors
     */

    public static final ImageDescriptor DESC_PROPERTIES =
        create(PATH_LCL, "properties.gif"); //$NON-NLS-1$
    public static final ImageDescriptor DESC_OPEN_LOG =
        create(PATH_LCL, "open_log.gif"); //$NON-NLS-1$
    public static final ImageDescriptor DESC_OPEN_LOG_DISABLED =
        create(PATH_LCL_DISABLED, "open_log.gif"); //$NON-NLS-1$

    public static final ImageDescriptor DESC_PROPERTIES_DISABLED =
        create(PATH_LCL_DISABLED, "properties.gif"); //$NON-NLS-1$
    public static final ImageDescriptor DESC_REFRESH =
        create(PATH_LCL, "refresh.gif"); //$NON-NLS-1$
    public static final ImageDescriptor DESC_REFRESH_DISABLED =
        create(PATH_LCL_DISABLED, "refresh.gif"); //$NON-NLS-1$
    public static final ImageDescriptor DESC_CLEAR = create(PATH_LCL, "clear.gif"); //$NON-NLS-1$
    public static final ImageDescriptor DESC_CLEAR_DISABLED =
        create(PATH_LCL_DISABLED, "clear.gif"); //$NON-NLS-1$

    public static final ImageDescriptor DESC_READ_LOG =
        create(PATH_LCL, "restore_log.gif"); //$NON-NLS-1$
    public static final ImageDescriptor DESC_READ_LOG_DISABLED =
        create(PATH_LCL_DISABLED, "restore_log.gif"); //$NON-NLS-1$
        
    public static final ImageDescriptor DESC_REMOVE_LOG =
        create(PATH_LCL, "remove.gif"); //$NON-NLS-1$
    public static final ImageDescriptor DESC_REMOVE_LOG_DISABLED =
        create(PATH_LCL_DISABLED, "remove.gif"); //$NON-NLS-1$
        
    public static final ImageDescriptor DESC_FILTER =
        create(PATH_LCL, "filter_ps.gif"); //$NON-NLS-1$
    public static final ImageDescriptor DESC_FILTER_DISABLED =
        create(PATH_LCL_DISABLED, "filter_ps.gif"); //$NON-NLS-1$

    public static final ImageDescriptor DESC_EXPORT =
        create(PATH_LCL, "export_log.gif"); //$NON-NLS-1$
    public static final ImageDescriptor DESC_EXPORT_DISABLED =
        create(PATH_LCL_DISABLED, "export_log.gif"); //$NON-NLS-1$
        
    public static final ImageDescriptor DESC_IMPORT =
        create(PATH_LCL, "import_log.gif"); //$NON-NLS-1$
    public static final ImageDescriptor DESC_IMPORT_DISABLED =
        create(PATH_LCL_DISABLED, "import_log.gif"); //$NON-NLS-1$
    
    public static final ImageDescriptor DESC_COLLAPSE_ALL =
        create(PATH_LCL, "collapseall.gif"); //$NON-NLS-1$
    
    public static final ImageDescriptor DESC_HORIZONTAL_VIEW =
        create(PATH_LCL, "th_horizontal.gif"); //$NON-NLS-1$
    
    public static final ImageDescriptor DESC_HORIZONTAL_VIEW_DISABLED = 
        create(PATH_LCL_DISABLED, "th_horizontal.gif"); //$NON-NLS-1$
    
    public static final ImageDescriptor DESC_VERTICAL_VIEW =
        create(PATH_LCL, "th_vertical.gif"); //$NON-NLS-1$
    
    public static final ImageDescriptor DESC_VERTICAL_VIEW_DISABLED = 
        create(PATH_LCL_DISABLED, "th_vertical.gif"); //$NON-NLS-1$
    
    public static final ImageDescriptor DESC_HIDE_PANE =
        create(PATH_EVENTS, "hide_pane.gif"); //$NON-NLS-1$
    
    /*
     * Event Details
     */
    public static final ImageDescriptor DESC_PREV_EVENT =
        create(PATH_EVENTS, "event_prev.gif"); //$NON-NLS-1$
    public static final ImageDescriptor DESC_NEXT_EVENT = 
        create(PATH_EVENTS, "event_next.gif"); //$NON-NLS-1$
    /*
     * Overlays
     */
    public static final ImageDescriptor DESC_RUN_CO =
        create(PATH_OVR, "run_co.gif"); //$NON-NLS-1$

    private static ImageDescriptor create(String prefix, String name) {
        return UiPlugin.getDefault().getImageDescriptor(prefix + name);
//        return ImageDescriptor.createFromURL(makeIconURL(prefix, name));
    }

//    public static Image get(String key) {
//        return PLUGIN_REGISTRY.get(key);
//    }
//    private static URL makeIconURL(String prefix, String name) {
//        String path = prefix + name;
//        URL url = null;
//        try {
//            URL baseURL = Platform.resolve(UiPlugin.getDefault().getBundle().getEntry("/")); //$NON-NLS-1$
//            url = new URL(baseURL, path);
//        } catch (Exception e) {
//            return null;
//        }
//        return url;
//    }
//    public static Image manage(String key, ImageDescriptor desc) {
//        Image image = desc.createImage();
//        PLUGIN_REGISTRY.put(key, image);
//        return image;
//    }
}
