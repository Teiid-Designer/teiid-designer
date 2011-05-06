/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.udf.ui;

import java.util.EventObject;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.osgi.framework.BundleContext;
import org.teiid.designer.udf.UdfManager;

import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.event.EventObjectListener;
import com.metamatrix.core.event.EventSourceException;
import com.metamatrix.core.util.PluginUtilImpl;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;
import com.metamatrix.modeler.ui.event.ModelResourceEvent;
import com.metamatrix.ui.AbstractUiPlugin;
import com.metamatrix.ui.actions.ActionService;

/**
 * 
 */
public final class UdfUiPlugin extends AbstractUiPlugin implements EventObjectListener {

    public static final String PLUGIN_ID = UdfUiPlugin.class.getPackage().getName();

    private static final String I18N_NAME = PLUGIN_ID + ".i18n"; //$NON-NLS-1$

    static final PluginUtil UTIL = new PluginUtilImpl(PLUGIN_ID, I18N_NAME, ResourceBundle.getBundle(I18N_NAME));

    private static UdfUiPlugin plugin;

    static UdfUiPlugin getInstance() {
        return plugin;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.ui.AbstractUiPlugin#createActionService(org.eclipse.ui.IWorkbenchPage)
     */
    @Override
    protected ActionService createActionService( IWorkbenchPage page ) {
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.ui.AbstractUiPlugin#getPluginUtil()
     */
    @Override
    public PluginUtil getPluginUtil() {
        return UTIL;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start( BundleContext context ) throws Exception {
        super.start(context);
        plugin = this;
        ((PluginUtilImpl)UTIL).initializePlatformLogger(this);
        
        try {
            com.metamatrix.modeler.ui.UiPlugin.getDefault().getEventBroker().addListener(ModelResourceEvent.class, this);
        } catch (EventSourceException e) {
        	((PluginUtilImpl)UTIL).log(IStatus.ERROR, e, e.getMessage());
        }
    }
    
    @Override
	public void stop(BundleContext context) throws Exception {
    	com.metamatrix.modeler.ui.UiPlugin.getDefault().getEventBroker().removeListener(ModelResourceEvent.class, this);
		super.stop(context);
	}

	/**
     * @see com.metamatrix.core.event.EventObjectListener#processEvent(java.util.EventObject)
     * @since 4.2
     */
    public void processEvent( EventObject obj ) {
    	// The UDF manager needs to know when Function Models are ADDED, REMOVED or RELOADED so the models can be
    	// registered or unregistered from the FunctionLibrary
        ModelResourceEvent event = (ModelResourceEvent)obj;
        if( ModelIdentifier.isFunctionModel(event.getModelResource())) {

        	try {
        	final IPath path = ((IFile)event.getModelResource().getCorrespondingResource()).getRawLocation();
	        	if( path != null ) {
			        if (event.getType() == ModelResourceEvent.RELOADED || event.getType() == ModelResourceEvent.ADDED) {
			        	if( !event.getModelResource().isOpen() ) {
			        		event.getModelResource().open(new NullProgressMonitor());
			        	}
			        	registerFunctionModel(event.getModelResource(), false);
			        } else if( event.getType() == ModelResourceEvent.REMOVED) {
			        	registerFunctionModel(event.getModelResource(), true);
			        } else if( event.getType() == ModelResourceEvent.CHANGED) {
			        	registerFunctionModel(event.getModelResource(), true);
			        	registerFunctionModel(event.getModelResource(), false);
			        }
	        	} else {
	        		UdfUiPlugin.UTIL.log(IStatus.ERROR, "Error registering function model: " + event.getModelResource().getItemName()); //$NON-NLS-1$
	        	}
        	} catch(ModelWorkspaceException ex) {
        		UdfUiPlugin.UTIL.log(ex);
        	}
        }
    }
    
    private void registerFunctionModel(final ModelResource modelResource, final boolean isDelete) {
    	Display.getDefault().syncExec(new Runnable() {
            public void run() {
            	try {
            		UdfManager.INSTANCE.registerFunctionModel(modelResource, isDelete);
            	} catch (Exception e) {
                    UdfUiPlugin.UTIL.log(e);
                }
            }
        });
    }
}
