/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.runtime.preview.jobs;

import static org.teiid.designer.runtime.DqpPlugin.PLUGIN_ID;

import java.util.Properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.teiid.designer.core.util.StringUtilities;
import org.teiid.designer.runtime.DqpPlugin;
import org.teiid.designer.runtime.preview.Messages;
import org.teiid.designer.runtime.preview.PreviewContext;
import org.teiid.designer.runtime.spi.ITeiidDataSource;
import org.teiid.designer.runtime.spi.ITeiidServer;

/**
 * The <code>DeleteDeployedPreviewVdbJob</code> deletes a Preview VDB from a Teiid server and also deletes its associated JNDI
 * name.
 *
 * @since 8.0
 */
public final class DeleteDeployedPreviewVdbJob extends TeiidPreviewVdbCleanupJob {

	private static final String CLASS_NAME_KEY = "class-name"; //$NON-NLS-1$
	private static final String RESRC_ADAPTER_VALUE = ".resource.adapter."; //$NON-NLS-1$
	
    /**
     * The data source name (never <code>null</code> or empty).
     */
    private final String jndiName;

    /**
     * The Preview VDB name (never <code>null</code> or empty).
     */
    private final String pvdbName;

    /**
     * The version of the Preview VDB.
     */
    private final int pvdbVersion;

    /**
     * @param pvdbName the name of the Preview VDB (may not be <code>null</code> or empty)
     * @param pvdbVersion the Preview VDB version
     * @param jndiName the data source name (may not be <code>null</code> or empty)
     * @param context the preview context (may not be <code>null</code>)
     * @param previewServer the preview server (may be <code>null</code>)
     */
    public DeleteDeployedPreviewVdbJob( String pvdbName,
                                        int pvdbVersion,
                                        String jndiName,
                                        PreviewContext context,
                                        ITeiidServer previewServer ) {
        super(NLS.bind(Messages.DeleteDeployedPreviewVdbJob, pvdbName), context, previewServer);

        assert !StringUtilities.isEmpty(pvdbName) : "Preview VDB name is null or empty"; //$NON-NLS-1$
        assert !StringUtilities.isEmpty(jndiName) : "JNDI name is null or empty"; //$NON-NLS-1$

        this.pvdbName = pvdbName;
        this.pvdbVersion = pvdbVersion;
        this.jndiName = jndiName;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.preview.jobs.TeiidPreviewVdbJob#runImpl(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    protected IStatus runImpl( IProgressMonitor monitor ) throws Exception {
        if (monitor.isCanceled()) {
            return Status.CANCEL_STATUS;
        }

        ITeiidServer teiidServer = getPreviewServer();
        int errors = 0;
        IStatus deleteVdbErrorStatus = null;

        // delete PVDB from server
        try {
            if (teiidServer.hasVdb(this.pvdbName)) {
                teiidServer.undeployVdb(this.pvdbName);
            }
        } catch (Exception e) {
            ++errors;
            deleteVdbErrorStatus = new Status(IStatus.ERROR, PLUGIN_ID, NLS.bind(Messages.DeleteDeployedPreviewVdbJobError,
                                                                                 this.pvdbName), e);
        }

        if (monitor.isCanceled()) {
            return Status.CANCEL_STATUS;
        }

        // check RA Source delete preference
        boolean allowRASourceDeletes = DqpPlugin.getInstance().getPreferences().getBoolean(PREVIEW_DELETE_RA_SOURCES_ENABLED,
        		PREVIEW_DELETE_RA_SOURCES_ENABLED_DEFAULT);
        // Determine if this is RA source
        boolean isRASource = isResourceAdapterSource(teiidServer,this.jndiName);
        // Delete DataSource if not a Resource Adapter
        IStatus deleteDataSourceErrorStatus = null;
        if( !isRASource || (isRASource && allowRASourceDeletes) ) {
        	try {
        		if (teiidServer.dataSourceExists(this.jndiName)) {
        			teiidServer.deleteDataSource(this.jndiName);
        		}
        	} catch (Exception e) {
        		++errors;
        		deleteDataSourceErrorStatus = new Status(IStatus.ERROR, PLUGIN_ID,
        				NLS.bind(Messages.DeleteDeployedPreviewVdbJobError, this.pvdbName), e);
        	}
        }

        // Handle Delete Errors
        if ( !isRASource || (isRASource && allowRASourceDeletes) ) {
        	if(errors == 2) {
        		IStatus[] statuses = new IStatus[2];
        		statuses[0] = deleteVdbErrorStatus;
        		statuses[1] = deleteDataSourceErrorStatus;
        		return new MultiStatus(PLUGIN_ID, IStatus.OK, statuses, NLS.bind(Messages.DeleteDeployedPreviewVdbJobError,
        				this.pvdbName), null);
        		// just couldn't delete VDB
        	} else if (deleteVdbErrorStatus != null) {
        		throw new CoreException(deleteVdbErrorStatus);
        	}
        } else if (deleteVdbErrorStatus != null) {
    		throw new CoreException(deleteVdbErrorStatus);
        }

        return new Status(IStatus.INFO, PLUGIN_ID, NLS.bind(Messages.DeleteDeployedPreviewVdbJobSuccessfullyCompleted,
                this.pvdbName));
    }

    private boolean isResourceAdapterSource(ITeiidServer teiidServer, String sourceJndiName) {
    	boolean isRASource = false;
    	
    	// Get the DataSource
    	ITeiidDataSource dataSource;
		try {
			dataSource = teiidServer.getDataSource(this.jndiName);
		} catch (Exception ex) {
			return false;
		}
		
		if(dataSource!=null) {
			// Get DataSource Properties.  If DataSource has class-name property containing ".resource.adapter." its RA source
			Properties props = dataSource.getProperties();
			if(props.containsKey(CLASS_NAME_KEY)) {
				String propValue = props.getProperty(CLASS_NAME_KEY);
				if(propValue!=null && propValue.indexOf(RESRC_ADAPTER_VALUE)>-1) {
					isRASource = true;
				}
			}
		}
    	return isRASource;
    }
}
