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
import net.jcip.annotations.GuardedBy;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.teiid.core.util.I18nUtil;
import org.teiid.designer.runtime.DqpPlugin;
import org.teiid.designer.runtime.TeiidDataSource;
import org.teiid.designer.runtime.TeiidServer;
import org.teiid.designer.runtime.TeiidServerManager;
import org.teiid.designer.runtime.TeiidTranslator;
import org.teiid.designer.runtime.TeiidVdb;
import org.teiid.designer.runtime.connection.SourceConnectionBinding;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.DqpUiPlugin;
import org.teiid.designer.runtime.ui.views.TeiidServerContentProvider.TeiidFolder;

/**
 * Class provides content and label information for ConnectorBindings and ModelInfos in ConnectorsView
 * 
 * @since 8.0
 */

public class TeiidServerLabelProvider extends ColumnLabelProvider implements ILightweightLabelDecorator {

    /**
     * If a server connection cannot be established, wait this amount of time before trying again.
     */
    private static final long RETRY_DURATION = 2000;

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
            assert (element instanceof TeiidServer) : "element is not a server (check plugin.xml enablement)"; //$NON-NLS-1$
            TeiidServer teiidServer = (TeiidServer)element;

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
        if (element instanceof TeiidServer) {
            TeiidServer teiidServer = (TeiidServer)element;
            boolean isOKtoConnect = isOkToConnect(teiidServer);
            
            boolean isError = false;
            if (isOKtoConnect) {
                // decorate server if can't connect
                if (!teiidServer.isConnected()) {
                    addOfflineServer(teiidServer);
                    isError = true;
                }
            } else {
                if (!teiidServer.isConnected()) {
                    isError = true;
                }
            }
            
            if (getServerManager() != null) {
                if (this.serverMgr.isDefaultServer((TeiidServer)element)) {
                    if (isError) return DqpUiPlugin.getDefault().getAnImage(DqpUiConstants.Images.SET_DEFAULT_SERVER_ERROR_ICON);
                    return DqpUiPlugin.getDefault().getAnImage(DqpUiConstants.Images.SET_DEFAULT_SERVER_ICON);
                }
            }
            if (isError) {
                if (teiidServer.getConnectionError() != null) return DqpUiPlugin.getDefault().getAnImage(DqpUiConstants.Images.SERVER_ERROR_ICON);
                return DqpUiPlugin.getDefault().getAnImage(DqpUiConstants.Images.SET_DEFAULT_SERVER_ERROR_ICON);
            }
            return DqpUiPlugin.getDefault().getAnImage(DqpUiConstants.Images.SERVER_ICON);
        }

        if (element instanceof TeiidFolder) {
            return DqpUiPlugin.getDefault().getAnImage(DqpUiConstants.Images.FOLDER_OBJ);
        }

        if (element instanceof TeiidTranslator) {
            return DqpUiPlugin.getDefault().getAnImage(DqpUiConstants.Images.CONNECTOR_BINDING_ICON);
        }

        if (element instanceof TeiidDataSource) {
            return DqpUiPlugin.getDefault().getAnImage(DqpUiConstants.Images.CONNECTION_SOURCE_ICON);
        }

        if (element instanceof TeiidVdb) {
            if (((TeiidVdb)element).isActive()) {
                return DqpUiPlugin.getDefault().getAnImage(DqpUiConstants.Images.DEPLOY_VDB);
            }
            return DqpUiPlugin.getDefault().getAnImage(DqpUiConstants.Images.INACTIVE_DEPLOYED_VDB);
        }

        if (element instanceof SourceConnectionBinding) {
            return DqpUiPlugin.getDefault().getAnImage(DqpUiConstants.Images.SOURCE_CONNECTOR_BINDING_ICON);
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
        if (element instanceof TeiidServer) {
            TeiidServer teiidServer = (TeiidServer)element;

            if (teiidServer.getCustomLabel() == null) {
                return teiidServer.getUrl();
            }

            return teiidServer.getCustomLabel();
        }

        if (element instanceof TeiidFolder) {
            return ((TeiidFolder)element).getName();
        }

        if (element instanceof TeiidTranslator) {
            return ((TeiidTranslator)element).getName();
        }

        if (element instanceof TeiidDataSource) {
            if (((TeiidDataSource)element).getDisplayName() != null) {
                return ((TeiidDataSource)element).getDisplayName();
            }
            return ((TeiidDataSource)element).getName();
        }

        if (element instanceof TeiidVdb) {
            // if( !((TeiidVdb)element).isActive() ) {
            // return INACTIVE_VDB_PREFIX + ((TeiidVdb)element).getName();
            // }
            return ((TeiidVdb)element).getName();
        }

        if (element instanceof SourceConnectionBinding) {
            SourceConnectionBinding binding = (SourceConnectionBinding)element;
            return binding.getModelName();
        }
        if (element instanceof String) {
            return (String)element;
        }
        return DqpUiConstants.UTIL.getString(I18nUtil.getPropertyPrefix(TeiidServerLabelProvider.class),
                                             new Object[] {element.toString(), element.getClass().getName()});
    }

    /**
     * Determines if a try to connect to a server should be done based on the last time a try was done and failed.
     * 
     * @param teiidServer the server being checked
     * @return <code>true</code> if it is OK to try and connect
     */
    private boolean isOkToConnect(TeiidServer teiidServer) {
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
}
