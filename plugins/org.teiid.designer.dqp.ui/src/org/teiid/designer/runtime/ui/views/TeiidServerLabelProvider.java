/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.views;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.jcip.annotations.GuardedBy;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.IDescriptionProvider;
import org.teiid.designer.runtime.DqpPlugin;
import org.teiid.designer.runtime.TeiidServer;
import org.teiid.designer.runtime.TeiidServerManager;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.DqpUiPlugin;
import org.teiid.designer.runtime.ui.views.content.AbstractTeiidFolder;
import org.teiid.designer.runtime.ui.views.content.ITeiidContentNode;
import org.teiid.designer.runtime.ui.views.content.ITeiidResourceNode;
import org.teiid.designer.runtime.ui.views.content.TeiidDataNode;
import org.teiid.designer.runtime.ui.views.content.TeiidErrorNode;
import org.teiid.designer.runtime.ui.views.content.TeiidServerContainerNode;

/**
 * Class provides content and label information for ConnectorBindings and ModelInfos in ConnectorsView
 * 
 * @since 8.0
 */

public class TeiidServerLabelProvider extends ColumnLabelProvider implements ILightweightLabelDecorator, IDescriptionProvider {

    /**
     * If a server connection cannot be established, wait this amount of time before trying again.
     */
    private static final long RETRY_DURATION = 2000;

    /**
     * Pattern for use with modifying text for the description
     */
    private static Pattern pattern = Pattern.compile("[\\\n\\\t]+"); //$NON-NLS-1$
    
    private TeiidServerManager serverMgr;

    /**
     * Servers that a connection can't be established. Value is the last time establishing a connection was tried.
     */
    @GuardedBy( "offlineServersLock" )
    private final Map<TeiidServer, Long> offlineServerMap = new HashMap<TeiidServer, Long>();

    /**
     * Lock used for when accessing the offline server map. The map will be accessed in different threads as the decorator runs in
     * its own thread (not the UI thread).
     */
    private final ReadWriteLock offlineServersLock = new ReentrantReadWriteLock();

    /**
     * @param teiidServer the server that is offline
     */
    private void addOfflineServer(TeiidServer teiidServer) {
        try {
            this.offlineServersLock.writeLock().lock();
            this.offlineServerMap.put(teiidServer, System.currentTimeMillis());
        } finally {
            this.offlineServersLock.writeLock().unlock();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.viewers.ILightweightLabelDecorator#decorate(java.lang.Object, org.eclipse.jface.viewers.IDecoration)
     */
    @Override
    public void decorate(Object element, IDecoration decoration) {
        final Display display = Display.getDefault();

        if (display.isDisposed()) {
            return;
        }

        // the decorator framework actually constructs another instance of this provider and the server manager will not be set by
        // the getElements method
        if (getServerManager() != null) {
            assert (element instanceof TeiidServerContainerNode) : "element is not a server (check plugin.xml enablement)"; //$NON-NLS-1$
            TeiidServerContainerNode node = (TeiidServerContainerNode) element;
            TeiidServer teiidServer = node.getTeiidServer();
            
            if (isOkToConnect(teiidServer)) {
                // decorate server if can't connect
                if (!teiidServer.isConnected()) {
                    addOfflineServer(teiidServer);
                }
            }
        }
    }

    /**
     * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
     * @since 4.2
     */
    @Override
    public Image getImage(Object element) {
        if (element instanceof ITeiidResourceNode) {
            return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
        }
        
        if (element instanceof TeiidServerContainerNode) {
            return DqpUiPlugin.getDefault().getAnImage(DqpUiConstants.Images.SERVER_ICON);
        }

        if (element instanceof AbstractTeiidFolder) {
            return DqpUiPlugin.getDefault().getAnImage(DqpUiConstants.Images.FOLDER_OBJ);
        }
        
        if (element instanceof TeiidDataNode) {
            return ((TeiidDataNode) element).getImage();
        }
 
        if (element instanceof TeiidErrorNode) {
            TeiidServer teiidServer = ((TeiidErrorNode) element).getTeiidServer();
            
            if (getServerManager() != null && teiidServer != null && this.serverMgr.isDefaultServer(teiidServer))
                return DqpUiPlugin.getDefault().getAnImage(DqpUiConstants.Images.SET_DEFAULT_SERVER_ERROR_ICON);
            else
                return DqpUiPlugin.getDefault().getAnImage(DqpUiConstants.Images.SERVER_ERROR_ICON);
        }
        
        return null;
    }

    /**
     * @return the server manager (never <code>null</code>)
     */
    private TeiidServerManager getServerManager() {
        if (this.serverMgr == null) {
            this.serverMgr = DqpPlugin.getInstance().getServerManager();
        }

        return this.serverMgr;
    }

    /**
     * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
     * @since 4.2
     */
    @Override
    public String getText(Object element) {
        if (element instanceof ITeiidContentNode) {
            ITeiidContentNode node = (ITeiidContentNode) element;
            return node.getName();
        }

        if (element instanceof String) {
            return (String)element;
        }
        
        if (element == TeiidServerContentProvider.getPending()) {
            return DqpUiConstants.UTIL.getString(getClass().getSimpleName() + ".loading.label"); //$NON-NLS-1$
        }
        
        return super.getText(element);
    }

    /**
     * Determines if a try to connect to a server should be done based on the last time a try was done and failed.
     * 
     * @param teiidServer the server being checked
     * @return <code>true</code> if it is OK to try and connect
     */
    private synchronized boolean isOkToConnect(TeiidServer teiidServer) {
        boolean check = false; // check map for time

        try {
            this.offlineServersLock.readLock().lock();
            check = this.offlineServerMap.containsKey(teiidServer);
        } finally {
            this.offlineServersLock.readLock().unlock();
        }

        if (check) {
            try {
                this.offlineServersLock.writeLock().lock();

                if (this.offlineServerMap.containsKey(teiidServer)) {
                    long checkTime = this.offlineServerMap.get(teiidServer);

                    // OK to try and connect if last failed attempt was too long ago
                    if ((System.currentTimeMillis() - checkTime) > RETRY_DURATION) {
                        this.offlineServerMap.remove(teiidServer);
                        return true;
                    }

                    // don't try and connect because we just tried and failed
                    return false;
                }
            } finally {
                this.offlineServersLock.writeLock().unlock();
            }
        }

        // OK to try and connect
        return true;
    }

    @Override
    public String getDescription(Object element) {
        String text = element.toString();
  
        Matcher matcher = pattern.matcher(text);
        return matcher.replaceAll("  "); //$NON-NLS-1$
    }
}
