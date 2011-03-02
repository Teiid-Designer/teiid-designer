/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.connection;

import static com.metamatrix.modeler.dqp.ui.DqpUiConstants.UTIL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.teiid.designer.runtime.ExecutionAdmin;
import org.teiid.designer.runtime.Server;
import org.teiid.designer.runtime.TeiidDataSource;
import org.teiid.designer.runtime.TeiidTranslator;
import org.teiid.designer.runtime.connection.ModelConnectionMapper;
import org.teiid.designer.vdb.VdbModelEntry;
import org.teiid.designer.vdb.connections.SourceHandler;
import org.teiid.designer.vdb.connections.VdbSourceConnection;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.dqp.DqpPlugin;

/**
 * Implements the SourceHandler interface which provides the VDB Editor the ability to access DQP-related connection
 * info.
 * 
 */
public class VdbSourceConnectionHandler implements SourceHandler {
	static final String PREFIX = I18nUtil.getPropertyPrefix(VdbSourceConnectionHandler.class);
	
	private static SelectTranslatorAction selectTranslatorAction;
	
	private static SelectJndiDataSourceAction selectJndiDataSourceAction;
	
	private static Object[] actions;
	
	private static boolean initialized = false;

    static String getString( final String stringId ) {
        return UTIL.getString(PREFIX + stringId);
    }

	@Override
	public VdbSourceConnection ensureVdbSourceConnection (
			String sourceModelname, Properties properties) throws Exception {
    	CoreArgCheck.isNotNull(properties, "properties"); //$NON-NLS-1$
    	
        ModelConnectionMapper mapper = new ModelConnectionMapper(sourceModelname, properties);
        
        VdbSourceConnection vdbSourceConnection = null;
        
        ExecutionAdmin defaultAdmin = getDefaultServer().getAdmin();
        
        String uuid = ModelerCore.workspaceUuid().toString();
        
        try {
			vdbSourceConnection = mapper.getVdbSourceConnection(defaultAdmin, uuid);
		} catch (ModelWorkspaceException e) {
            UTIL.log(IStatus.ERROR,
                     e,
                     UTIL.getString("VdbSourceConnectionHandler.Error_could_not_find_source_connection_info_for_{0}_model", sourceModelname)); //$NON-NLS-1$
		}
        
		// TODO: vdbSourceConnection may be NULL, so query the user for translator name & jndi name
		
        return vdbSourceConnection;
	}

	@Override
	public Object[] getApplicableActions(Object obj) {
		if( !initialized ) {
			initialize();
		}
		Server defServer = getDefaultServer();
		if( defServer == null || !defServer.isConnected() ) {
			return null;
		}
		
		if( obj instanceof IStructuredSelection) {
			IStructuredSelection sel = (IStructuredSelection)obj;
			if( sel.getFirstElement() instanceof VdbModelEntry) {
				if( ((VdbModelEntry)sel.getFirstElement()).getType() == ModelType.PHYSICAL_LITERAL) {
					selectTranslatorAction.setSelection((VdbModelEntry)sel.getFirstElement());
					selectJndiDataSourceAction.setSelection((VdbModelEntry)sel.getFirstElement());
					return actions;
				}
			}
		}
		selectTranslatorAction.setSelection(null);
		selectJndiDataSourceAction.setSelection(null);
		return null;
	}
	
	private void initialize() {
		// Construct the two actions
		selectTranslatorAction = new SelectTranslatorAction(getString("selectTranslatorAction.label")); //$NON-NLS-1$

        selectJndiDataSourceAction = new SelectJndiDataSourceAction(getString("selectJndiDataSourceAction.label")); //$NON-NLS-1$
		
        Collection<IAction> actionsList= new ArrayList();
        actionsList.add(selectTranslatorAction);
        actionsList.add(selectJndiDataSourceAction);
        
        actions = actionsList.toArray();
	}
	
	class SelectJndiDataSourceAction extends Action {
		
		public SelectJndiDataSourceAction(String text) {
			super(text);
			// TODO Auto-generated constructor stub
		}

		private VdbModelEntry vdbModelEntry;
		
		public void setSelection(VdbModelEntry vdbModelEntry) {
			this.vdbModelEntry = vdbModelEntry;
		}
    	
        @Override
        public void run() {
            // Get available servers and launch SelectTranslatorDialog
        	// vdbModelEntry should not be null and should be a Physical model only
        	if( vdbModelEntry != null ) {
        		String jndiName = vdbModelEntry.getJndiName();
        		
        		SelectJndiDataSourceDialog dialog = new SelectJndiDataSourceDialog(Display.getCurrent().getActiveShell());
        		
        		TeiidDataSource initialSelection = null;
        		Server defServer = getDefaultServer();
        		if( defServer != null && defServer.isConnected() ) {
        			try {
						initialSelection = defServer.getAdmin().getDataSource(jndiName);
					} catch (Exception e) {
                        UTIL.log(IStatus.ERROR,
                                 e,
                                 UTIL.getString("VdbSourceConnectionHandler.Error_could_not_find_data_source_for_name", jndiName)); //$NON-NLS-1$
					}
					dialog.setInitialSelection(initialSelection);
        		}
        		
        		dialog.open();
        		
        		if( dialog.getReturnCode() == Window.OK) {
        			Object result = dialog.getFirstResult();
        			if( result != null && result instanceof TeiidDataSource) {
        				vdbModelEntry.setJndiName(((TeiidDataSource)result).getName());
        			}
        		}
        	}
        }
	}
	
	
	class SelectTranslatorAction extends Action {
		
		public SelectTranslatorAction(String text) {
			super(text);
			// TODO Auto-generated constructor stub
		}
		
		private VdbModelEntry vdbModelEntry;
		
		public void setSelection(VdbModelEntry vdbModelEntry) {
			this.vdbModelEntry = vdbModelEntry;
		}
        @Override
        public void run() {
            // Get available servers and launch SelectTranslatorDialog
        	// vdbModelEntry should not be null and should be a Physical model only
        	
        	if( vdbModelEntry != null ) {
        		String transName = vdbModelEntry.getTranslator();
        		
        		SelectTranslatorDialog dialog = new SelectTranslatorDialog(Display.getCurrent().getActiveShell());
        		
        		TeiidTranslator initialSelection = null;
        		Server defServer = getDefaultServer();
        		if( defServer != null && defServer.isConnected() ) {
        			try {
						initialSelection = defServer.getAdmin().getTranslator(transName);
					} catch (Exception e) {
                        UTIL.log(IStatus.ERROR,
                                 e,
                                 UTIL.getString("VdbSourceConnectionHandler.Error_could_not_find_translator_for_name", transName)); //$NON-NLS-1$
					}
					dialog.setInitialSelection(initialSelection);
        		}

        		dialog.open();
        		
        		if( dialog.getReturnCode() == Window.OK) {
        			Object result = dialog.getFirstResult();
        			if( result != null && result instanceof TeiidTranslator) {
        				vdbModelEntry.setTranslator(((TeiidTranslator)result).getName());
        			}
        		}
        	}
        }
	}
    
    /**
     * {@inheritDoc}
     *
     * @see org.teiid.designer.vdb.connections.SourceHandler#getDataSourceNames()
     */
    @Override
    public String[] getDataSourceNames() {
        Server defaultServer = getDefaultServer();

        if ((defaultServer != null) && defaultServer.isConnected()) {
            Collection<TeiidDataSource> dataSources = null;

            try {
                dataSources = defaultServer.getAdmin().getDataSources();
            } catch (Exception e) {
                UTIL.log(IStatus.ERROR,
                         e,
                         UTIL.getString("VdbSourceConnectionHandler.errorObtainingDataSources", defaultServer.getHost())); //$NON-NLS-1$
            }

            if (dataSources != null) {
                Collection<String> dataSourceNames = new ArrayList<String>();

                for (TeiidDataSource dataSource : dataSources) {
                    dataSourceNames.add(dataSource.getName());
                }

                return dataSourceNames.toArray(new String[dataSourceNames.size()]);
            }
        }

        return null;
    }
    
    Server getDefaultServer() {
        return DqpPlugin.getInstance().getServerManager().getDefaultServer();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.teiid.designer.vdb.connections.SourceHandler#getTranslatorNames()
     */
    @Override
    public String[] getTranslatorNames() {
        Server defaultServer = getDefaultServer();

        if ((defaultServer != null) && defaultServer.isConnected()) {
            Collection<TeiidTranslator> translators = null;

            try {
                translators = defaultServer.getAdmin().getTranslators();
            } catch (Exception e) {
                UTIL.log(IStatus.ERROR,
                         e,
                         UTIL.getString("VdbSourceConnectionHandler.errorObtainingTranslators", defaultServer.getHost())); //$NON-NLS-1$
            }

            if (translators != null) {
                Collection<String> translatorNames = new ArrayList<String>();

                for (TeiidTranslator translator : translators) {
                    translatorNames.add(translator.getName());
                }

                return translatorNames.toArray(new String[translatorNames.size()]);
            }
        }

        return null;
    }
}
