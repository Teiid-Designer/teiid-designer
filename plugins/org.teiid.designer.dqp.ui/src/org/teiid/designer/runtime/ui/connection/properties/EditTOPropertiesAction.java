package org.teiid.designer.runtime.ui.connection.properties;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.core.designer.properties.PropertyDefinition;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.translators.TranslatorOverrideProperty;
import org.teiid.designer.core.translators.TranslatorPropertyDefinition;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.datatools.connection.ConnectionInfoProviderFactory;
import org.teiid.designer.datatools.connection.IConnectionInfoProvider;
import org.teiid.designer.datatools.connection.ITranslatorOverridesProvider;
import org.teiid.designer.runtime.DqpPlugin;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.DqpUiPlugin;
import org.teiid.designer.runtime.ui.server.RuntimeAssistant;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.designer.ui.PluginConstants;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.actions.IConnectionAction;
import org.teiid.designer.ui.actions.SortableSelectionAction;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.viewsupport.DatatypeUtilities;
import org.teiid.designer.ui.viewsupport.ModelIdentifier;
import org.teiid.designer.ui.viewsupport.ModelUtilities;
import org.teiid.designer.vdb.connections.SourceHandler;
import org.teiid.designer.vdb.connections.SourceHandlerExtensionManager;
import org.teiid.designer.vdb.ui.VdbUiPlugin;

public class EditTOPropertiesAction  extends SortableSelectionAction  implements IConnectionAction, DqpUiConstants {
    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(EditTOPropertiesAction.class);
    private static final String label = DqpUiConstants.UTIL.getString("label"); //$NON-NLS-1$

    private static String getString( final String id ) {
        return DqpUiConstants.UTIL.getString(I18N_PREFIX + id);
    }

    private static String getString( final String id,
                                     final Object value ) {
        return DqpUiConstants.UTIL.getString(I18N_PREFIX + id, value);
    }
    private ConnectionInfoProviderFactory providerFactory = ConnectionInfoProviderFactory.getInstance();
    private ITeiidServer cachedServer;

    /**
     * @since 5.0
     */
    public EditTOPropertiesAction() {
        super(label, SWT.DEFAULT);
        setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(Images.SOURCE_BINDING_ICON));
        
    }

    public void setTeiidServer( ITeiidServer teiidServer ) {
        this.cachedServer = teiidServer;
    }

    /**
     * @see org.teiid.designer.ui.actions.SortableSelectionAction#isValidSelection(org.eclipse.jface.viewers.ISelection)
     * @since 5.0
     */
    @Override
    public boolean isValidSelection( ISelection selection ) {
        // Enable for source models
        return sourceModelSelected(selection);
    }

    /**
     * @see org.eclipse.jface.action.IAction#run()
     * @since 5.0
     */
    @Override
    public void run() {
        final IWorkbenchWindow iww = VdbUiPlugin.singleton.getCurrentWorkbenchWindow();

        ModelResource modelResource = null;
        ITranslatorOverridesProvider provider = null;
        
        if (!getSelection().isEmpty()) {
            IFile modelFile = (IFile)SelectionUtilities.getSelectedObjects(getSelection()).get(0);
            modelResource = ModelUtilities.getModelResource(modelFile);
            if( modelResource == null ) {
            	// TODO: FAIL WITH MESSAGE DIALOG
            	return;
            }
            provider = getProvider(modelResource);
            if( provider == null ) {
            	MessageDialog.openWarning(getShell(), getString("translatorOverridesNotSupported.title"),  //$NON-NLS-1$
            			getString("translatorOverridesNotSupported.message1")); //$NON-NLS-1$
            	return;
            }
        }
        try {
        	// Check Server status. If none defined, query to create or cancel.
        	
            ITeiidServer teiidServer = cachedServer;
            if (teiidServer == null) {
            	teiidServer = DqpPlugin.getInstance().getServerManager().getDefaultServer();
            	if( teiidServer == null ) {
	            	if( RuntimeAssistant.ensureServerConnection(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
	            			getString("noServer.message"), true) ) { //$NON-NLS-1$
	            		teiidServer = DqpPlugin.getInstance().getServerManager().getDefaultServer();
	            		teiidServer.connect();	
	            	} else {
	            		// User has cancelled this action or decided not to create a new server
	            		return;
	            	}
            	} else {
            		if( RuntimeAssistant.ensureServerConnection(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
	            			getString("noServer.message"), false) ) { //$NON-NLS-1$
	            		teiidServer = DqpPlugin.getInstance().getServerManager().getDefaultServer();
	            		teiidServer.connect();	
	            	} else {
	            		// User has cancelled this action or decided not to create a new server
	            		return;
	            	}
            	}
            }
            
            // 1) Get existing Translator Override Properties
            Properties props = provider.getTranslatorOverrideProperties(modelResource);
            Properties filteredProperties = new Properties();
            String translatorType = "jdbc "; //$NON-NLS-1$
            for( Object key : props.keySet() ) {
            	if("name".equals((String)key) ) { //$NON-NLS-1$
            		translatorType = (String)props.getProperty((String)key);
            	} else {
            		filteredProperties.put(key, (String)props.getProperty((String)key));
            	}
            }

            TranslatorOverride override = createOverride(translatorType, filteredProperties);
            
            boolean overridesSupported = teiidServer.getServerVersion().isGreaterThanOrEqualTo(Version.TEIID_8_6);
            boolean noOverrideProperties = override.getProperties().length == 0;
            
            if( !overridesSupported) {
            	// Warn users that server version does not support built-ins
            	MessageDialog.openWarning(getShell(), getString("translatorOverridesNotSupported.title"), //$NON-NLS-1$
            			getString("translatorOverridesNotSupported.message2")); //$NON-NLS-1$
            	return;
            }
            
            if( noOverrideProperties ) {
            	MessageDialog.openWarning(getShell(), getString("translatorOverridesNotAvailable.title"), //$NON-NLS-1$
            			getString("translatorOverridesNotAvailable.message", translatorType)); //$NON-NLS-1$
            	return;
            }
            
            // 2) Get Translator Type to seed dialog
            final EditTOPropertiesDialog dialog = new EditTOPropertiesDialog(iww.getShell(), override);

            if (dialog.open() == Window.OK) {
            	// Get properties from dialog
            	editPropertiesInTxn(override, modelResource, provider);
            	
            	modelResource.save(null, true);
            } else {
            	return; // CANCELLED
            }


        } catch (Exception e) {
            if (modelResource != null) {
                MessageDialog.openError(getShell(),
                                        getString("errorCreatingDataSourceForModel", modelResource.getItemName()), e.getMessage()); //$NON-NLS-1$
                DqpUiConstants.UTIL.log(IStatus.ERROR, e, getString("errorCreatingDataSourceForModel", modelResource.getItemName())); //$NON-NLS-1$
            } else {
                MessageDialog.openError(getShell(), getString("errorCreatingDataSource"), e.getMessage()); //$NON-NLS-1$
                DqpUiConstants.UTIL.log(IStatus.ERROR, e, getString("errorCreatingDataSource")); //$NON-NLS-1$

            }
        }
    }
    
    private void editPropertiesInTxn(TranslatorOverride override, ModelResource modelResource, ITranslatorOverridesProvider provider) {
		boolean requiredStart = ModelerCore.startTxn(
				PluginConstants.Transactions.SIGNIFICANT,
				PluginConstants.Transactions.NOT_UNDOABLE, "Set Translator Override Properties", this);
		boolean succeeded = false;
		try {
	    	Properties newOverrideProperties = new Properties();
	    	if( override.getProperties().length > 0) {
	    		
	    		for( TranslatorOverrideProperty prop : override.getProperties()) {            			
	    			if( prop.hasOverridenValue() ) {
	    				String id = prop.getDefinition().getId();
	    				String value = prop.getOverriddenValue();
	    				newOverrideProperties.put(id, value);
	    			}
	    		}
	    		provider.replaceTranlatorOverrideProperties(modelResource, newOverrideProperties);
	    	} else {
	    		provider.clearTranslatorOverrideProperties(modelResource);
	    	}
			succeeded = true;
		} finally {
			if (requiredStart) {
				if (succeeded) {
					ModelerCore.commitTxn();
				} else {
					ModelerCore.rollbackTxn();
				}
			}
		}

    }

    /**
     * @see org.teiid.designer.ui.actions.ISelectionAction#isApplicable(org.eclipse.jface.viewers.ISelection)
     * @since 5.0
     */
    @Override
    public boolean isApplicable( ISelection selection ) {
        return sourceModelSelected(selection);
    }
    
    private TranslatorOverride createOverride(String type, Properties properties) {
    	TranslatorOverride override = new TranslatorOverride(type, properties);
    	SourceHandler handler = SourceHandlerExtensionManager.getVdbConnectionFinder();
        PropertyDefinition[] propertyDefinitionsFromServer = handler.getTranslatorDefinitions(override.getType());

        if (propertyDefinitionsFromServer != null) {
            List<PropertyDefinition> newServerProps = new ArrayList<PropertyDefinition>();

            // assume all server properties are new
            for (PropertyDefinition propDefn : propertyDefinitionsFromServer) {
            	//System.out.println("propDefn ID = " + propDefn.getId() + " Display Name = " + propDefn.getDisplayName());
                newServerProps.add(propDefn);
            }

            if (!properties.isEmpty()) {
                // translator properties already exist, match with server props
                for (Object key : properties.keySet()) {
                	String keyStr = (String)key;
                	String value = (String)properties.get(key);
                	
                    PropertyDefinition serverPropDefn = null;

                    // see if property definitions from server already exist in overridden translator
                    for (PropertyDefinition propDefn : propertyDefinitionsFromServer) {
                        // found a matching one
                        if (keyStr.equals(propDefn.getId())) {
                            serverPropDefn = propDefn;
                            newServerProps.remove(serverPropDefn); // Remove it from cached list
                            break;
                        }
                    }

                    if (serverPropDefn != null) {
                    	TranslatorOverrideProperty newProp = new TranslatorOverrideProperty(new TranslatorPropertyDefinition(serverPropDefn), value);
                        // found existing property so update defn and use value from old defn
                    	override.addProperty(newProp);
                    }
                }
            }
            
            for (PropertyDefinition propDefn : newServerProps) {
            	override.addProperty(new TranslatorOverrideProperty(new TranslatorPropertyDefinition(propDefn), null));
            }
        }
        
        return override;
    }

    private boolean sourceModelSelected( ISelection theSelection ) {
        boolean result = false;
        List<?> allObjs = SelectionUtilities.getSelectedObjects(theSelection);
        if (!allObjs.isEmpty() && allObjs.size() == 1) {
            Iterator<?> iter = allObjs.iterator();
            result = true;
            Object nextObj = null;
            while (iter.hasNext() && result) {
                nextObj = iter.next();

                if (nextObj instanceof IFile) {
                    result = ModelIdentifier.isRelationalSourceModel((IFile)nextObj);
                } else {
                    result = false;
                }
            }
        }

        return result;
    }

    public ITranslatorOverridesProvider getProvider( ModelResource modelResource ) {
        IConnectionInfoProvider provider = null;
        try {
			provider = providerFactory.getProvider(modelResource);
		} catch (Exception e) {
			return null;
		}

        if( provider != null && provider instanceof ITranslatorOverridesProvider ) {
        	return (ITranslatorOverridesProvider)provider;
        }
        
        return null;
    }

    private Shell getShell() {
        return Display.getCurrent().getActiveShell();
    }
}
