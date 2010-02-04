/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.viewsupport;

import java.net.MalformedURLException;
import java.net.URL;
import org.eclipse.help.HelpSystem;
import org.eclipse.help.IContext;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;


/** 
 * @since 4.3
 */
public abstract class ModelerHelpUtil {

    public static void openInfopop(Widget widget, String contextId) {
        if (contextId == null) {
            return;
        }

        IContext context = HelpSystem.getContext(contextId);

        if (context != null) {
            // determine a location in the upper right corner of the widget
            Point point = widget.getDisplay().getCursorLocation();
            point = new Point(point.x + 15, point.y);
            // display the help
            PlatformUI.getWorkbench().getHelpSystem().displayContext(context, point.x, point.y);
        }
    }
    
    /**
     * Open a help topic
     */
    public static void openHelpTopic(String hRef) {
        if (hRef == null) {
            return;
        }

        PlatformUI.getWorkbench().getHelpSystem().displayHelpResource(hRef);
    }
    
    public static void openUrl(String urlString) {
        Assertion.isNotNull(urlString);
        Assertion.assertTrue(urlString.length() > 0 );
        
        URL url = null;
        
        try {
            url = new URL(urlString);
        } catch (MalformedURLException theException) {
        } finally {
            if( url != null ) {
                openUrl(url);
            }
        }
        
        
    }
    
    public static void openUrl(URL url) {
        Assertion.isNotNull(url);
        
        try {
            IWebBrowser browser = UiPlugin.getDefault().getWorkbench().getBrowserSupport()
                                                 .getExternalBrowser();
            if (browser != null) {
                browser.openURL(url);
            }
        } catch (PartInitException theException) {
            UiConstants.Util.log(theException);
        }
    }

}
