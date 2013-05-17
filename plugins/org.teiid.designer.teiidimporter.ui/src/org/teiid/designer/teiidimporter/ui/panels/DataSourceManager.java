/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.teiidimporter.ui.panels;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.runtime.spi.TeiidPropertyDefinition;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.teiidimporter.ui.Messages;
import org.teiid.designer.teiidimporter.ui.UiConstants;
import org.teiid.designer.teiidimporter.ui.wizard.ITeiidImportServer;

/**
 * DataSourceManager - handles DataSource operations with the supplied TeiidServer
 */
public class DataSourceManager implements UiConstants {
    
    private static final String DRIVER_KEY = "driver-name";  //$NON-NLS-1$
    private static final String CLASSNAME_KEY = "class-name";  //$NON-NLS-1$
    private static final String CONN_FACTORY_CLASS_KEY = "managedconnectionfactory-class";  //$NON-NLS-1$
    private static final String DOT_RAR = ".rar";  //$NON-NLS-1$
    
    private ITeiidImportServer teiidImportServer;
    
    /**
     * DataSourceManager constructor
     * @param teiidImportServer the TeiidServer
     */
    public DataSourceManager(ITeiidImportServer teiidImportServer) {
        this.teiidImportServer = teiidImportServer;
    }
    
    /**
     * Create a DataSource 
     * @param dataSourceName the name of the DataSource
     * @param driverName the name of the Driver
     * @param properties the dataSource properties
     * @return the result status of the operation
     */
    public IStatus createDataSource(final String dataSourceName, final String driverName, final Properties properties) {
        IStatus resultStatus = new Status(IStatus.OK, PLUGIN_ID, Messages.dataSourceManager_createOk);        
 
        IRunnableWithProgress op = new IRunnableWithProgress() {
            @Override
            public void run( IProgressMonitor monitor ) throws InvocationTargetException {
                try {
                    teiidImportServer.getOrCreateDataSource(dataSourceName, dataSourceName, driverName, properties);
                } catch (Throwable e) {
                    throw new InvocationTargetException(e);
                } finally {
                    monitor.done();
                }
            }
        };

        try {
            new ProgressMonitorDialog(Display.getCurrent().getActiveShell()).run(true, true, op);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            resultStatus = new Status(IStatus.ERROR, UiConstants.PLUGIN_ID, 0, cause.getLocalizedMessage(), cause);
            UTIL.log(resultStatus);
        } catch (InterruptedException e) {
            resultStatus = new Status(IStatus.ERROR,UiConstants.PLUGIN_ID, Messages.dataSourceManager_createInterruptedMsg);
            UTIL.log(resultStatus);
        }
        // Give a 1 sec pause for the file to deploy
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        } 
        return resultStatus;        
    }
    
    /**
     * Delete a DataSource 
     * @param dataSourceName the name of the DataSource
     * @return the result status of the operation
     */
    public IStatus deleteDataSource(final String dataSourceName) {
        IStatus resultStatus = new Status(IStatus.OK, PLUGIN_ID, Messages.dataSourceManager_deleteOk);        
 
        IRunnableWithProgress op = new IRunnableWithProgress() {
            @Override
            public void run( IProgressMonitor monitor ) throws InvocationTargetException {
                try {
                    teiidImportServer.deleteDataSource(dataSourceName);
                } catch (Throwable e) {
                    throw new InvocationTargetException(e);
                } finally {
                    monitor.done();
                }
            }
        };

        try {
            new ProgressMonitorDialog(Display.getCurrent().getActiveShell()).run(true, true, op);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            resultStatus = new Status(IStatus.ERROR, UiConstants.PLUGIN_ID, 0, cause.getLocalizedMessage(), cause);
            UTIL.log(resultStatus);
        } catch (InterruptedException e) {
            resultStatus = new Status(IStatus.ERROR,UiConstants.PLUGIN_ID, Messages.dataSourceManager_deleteInterruptedMsg);
            UTIL.log(resultStatus);
        }
        // Give a 1 sec pause for the file to deploy
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        } 
        return resultStatus;        
    }
    
    /**
     * Delete a DataSource, then Create a new DataSource with specified properties
     * @param deleteSourceName the name of the DataSource to delete
     * @param createSourceName the name of the DataSource to create
     * @param driverName the name of the Driver
     * @param properties the dataSource properties
     * @return the result status of the operation
     */
    public IStatus deleteAndCreateDataSource(final String deleteSourceName,
                                             final String createSourceName, final String driverName, final Properties properties) {
        IStatus resultStatus = new Status(IStatus.OK, PLUGIN_ID, Messages.dataSourceManager_deleteCreateOk);        
 
        IRunnableWithProgress op = new IRunnableWithProgress() {
            @Override
            public void run( IProgressMonitor monitor ) throws InvocationTargetException {
                try {
                    teiidImportServer.deleteDataSource(deleteSourceName);
                    
                    teiidImportServer.getOrCreateDataSource(createSourceName, createSourceName, driverName, properties);
                } catch (Throwable e) {
                    throw new InvocationTargetException(e);
                } finally {
                    monitor.done();
                }
            }
        };

        try {
            new ProgressMonitorDialog(Display.getCurrent().getActiveShell()).run(true, true, op);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            resultStatus = new Status(IStatus.ERROR, UiConstants.PLUGIN_ID, 0, cause.getLocalizedMessage(), cause);
            UTIL.log(resultStatus);
        } catch (InterruptedException e) {
            resultStatus = new Status(IStatus.ERROR,UiConstants.PLUGIN_ID, Messages.dataSourceManager_deleteCreateInterruptedMsg);
            UTIL.log(resultStatus);
        }
        // Give a 1 sec pause for the file to deploy
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        } 
        return resultStatus;        
    }
    
    /**
     * Copy a DataSource 
     * @param dataSourceToCopyName the name of the DataSource to copy
     * @param dataSourceToCopyDriver the driver name for the DataSource being copied
     * @param newDataSourceName the name of the new dataSource
     * @return the result status of the operation
     */
    public IStatus copyDataSource(final String dataSourceToCopyName, final String dataSourceToCopyDriver, final String newDataSourceName) {
        IStatus resultStatus = new Status(IStatus.OK, PLUGIN_ID, Messages.dataSourceManager_copyOk);        
 
        IRunnableWithProgress op = new IRunnableWithProgress() {
            @Override
            public void run( IProgressMonitor monitor ) throws InvocationTargetException {
                try {
                    // Get properties for the source to copy
                    Properties dsProps = teiidImportServer.getDataSourceProperties(dataSourceToCopyName);
                    // Create source with the new name
                    teiidImportServer.getOrCreateDataSource(newDataSourceName, newDataSourceName, dataSourceToCopyDriver, dsProps);
                } catch (Throwable e) {
                    throw new InvocationTargetException(e);
                } finally {
                    monitor.done();
                }
            }
        };

        try {
            new ProgressMonitorDialog(Display.getCurrent().getActiveShell()).run(true, true, op);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            resultStatus = new Status(IStatus.ERROR, UiConstants.PLUGIN_ID, 0, cause.getLocalizedMessage(), cause);
            UTIL.log(resultStatus);
        } catch (InterruptedException e) {
            resultStatus = new Status(IStatus.ERROR,UiConstants.PLUGIN_ID, Messages.dataSourceManager_copyInterruptedMsg);
            UTIL.log(resultStatus);
        }
        // Give a 1 sec pause for the file to deploy
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        } 
        return resultStatus;        
    }
    
    /**
     * Get the list of PropertyItems for the supplied DataSource name - from the TeiidServer
     * @param dsName the data source name
     * @return the list of PropertyItem
     */
    public List<PropertyItem> getDataSourcePropertyItems(String dsName) {
        
        // Get the driver template properties
        String driverName = getDataSourceDriver(dsName);
        List<PropertyItem> propertyItems = getDriverPropertyItems(driverName);
        
        if(!propertyItems.isEmpty()) {
            
            // Get the data source specific properties
            Properties props = null;
            try {
                // Get the specific property values for this data source
                props = teiidImportServer.getDataSourceProperties(dsName);
            } catch (Exception ex) {
                props = new Properties();
                UTIL.log(ex);
            }
            
            // Set the template property values to data source specific value
            for(PropertyItem propItem: propertyItems) {
                String propName = propItem.getName();
                String propValue = props.getProperty(propName);
                if(props.containsKey(propName)) {
                    propValue = props.getProperty(propName);
                    if(propValue!=null) {
                        propItem.setValue(propValue);
                        propItem.setOriginalValue(propValue);
                    }
                }
            }
        }
        
        return propertyItems;
    }
    
    /**
     * Get the list of PropertyItems for the supplied driver name - from the TeiidServer
     * @param driverName the driver name
     * @return the list of PropertyItem
     */
    public List<PropertyItem> getDriverPropertyItems(String driverName) {
        List<PropertyItem> propertyItemList = new ArrayList<PropertyItem>();
        Collection<TeiidPropertyDefinition> propDefns;
        ITeiidServerVersion teiidVersion = null;
        try {
            // Get the driver template properties
            propDefns = teiidImportServer.getTemplatePropertyDefns(driverName);
            teiidVersion = teiidImportServer.getTeiidServerVersion();
        } catch (Exception ex) {
            propDefns = new ArrayList<TeiidPropertyDefinition>();
            UTIL.log(ex);
        }
        
        // Get the Managed connection factory class for rars
        String rarConnFactoryValue = null;
        if(isRarDriver(driverName,teiidVersion)) {
            rarConnFactoryValue = getManagedConnectionFactoryClassDefault(propDefns);
        }
        
        // Create the PropertyItems, setting the template values for this source
        for(TeiidPropertyDefinition propDefn: propDefns) {
            PropertyItem propItem = new PropertyItem();
            
            // ------------------------
            // Set PropertyItem fields
            // ------------------------
            // Name
            String name = propDefn.getName();
            propItem.setName(name);
            // DisplayName
            String displayName = propDefn.getDisplayName();
            propItem.setDisplayName(displayName);
            // isModifiable 
            // TODO: remove this workaround (due to erroneous .rar values)
            boolean isModifiable = isModifiable(driverName,propDefn);
            propItem.setModifiable(isModifiable);
            // isRequired
            boolean isRequired = propDefn.isRequired();
            propItem.setRequired(isRequired);
            // isMasked
            boolean isMasked = propDefn.isMasked();
            propItem.setMasked(isMasked);
            // defaultValue
            Object defaultValue = propDefn.getDefaultValue();
            if(defaultValue!=null) {
                propItem.setDefaultValue(defaultValue.toString());
            }
            // Set the value and original Value
            if(defaultValue!=null) {
                propItem.setValue(defaultValue.toString());
                propItem.setOriginalValue(defaultValue.toString());
            // Set Connection URL to template if available and value was null
            } else if(displayName.equalsIgnoreCase(PropertyItem.CONNECTION_URL_DISPLAYNAME)) {
                String urlTemplate = TranslatorHelper.getUrlTemplate(driverName);
                if(!CoreStringUtil.isEmpty(urlTemplate)) {
                    propItem.setValue(urlTemplate);
                    propItem.setOriginalValue(urlTemplate);
                }
            }
                            
            // Copy the 'managedconnectionfactory-class' default value into the 'class-name' default value
            if(name.equals(CLASSNAME_KEY)) {
                propItem.setDefaultValue(rarConnFactoryValue);
                propItem.setValue(rarConnFactoryValue);
                propItem.setOriginalValue(rarConnFactoryValue);
                propItem.setRequired(true);
            }
            // ------------------------
            // Add PropertyItem to List
            // ------------------------
            propertyItemList.add(propItem);
        }
        return propertyItemList;
    }
    
    /**
     * Get the Driver name for the supplied DataSource name - from the TeiidServer
     * @param dsName the data source name
     * @return the dataSource driver name
     */
    public String getDataSourceDriver(String dsName) {
        String driverName = null;
        ITeiidServerVersion teiidVersion = null;
        Properties props = new Properties();
        try {
            props = teiidImportServer.getDataSourceProperties(dsName);
            teiidVersion = teiidImportServer.getTeiidServerVersion();
        } catch (Exception ex) {
            UTIL.log(ex);
            return null;
        }
        driverName = props.getProperty(DRIVER_KEY);
        // If driver-name not found, look for class name and match up the .rar
        if(CoreStringUtil.isEmpty(driverName)) {
            String className = props.getProperty(CLASSNAME_KEY);
            if(!CoreStringUtil.isEmpty(className)) {
            	driverName = TranslatorHelper.getDriverNameForClass(className, teiidVersion);
            }
        }
        return driverName;
    }
    
    /**
     * Determine if this is a 'rar' type driver that is deployed with Teiid
     * @param driverName the name of the driver
     * @param teiidVersion the teiid server version
     * @return 'true' if the driver is a rar driver, 'false' if not.
     */
    private boolean isRarDriver(String driverName, ITeiidServerVersion teiidVersion) {
    	boolean isRarDriver = false;
    	if(!CoreStringUtil.isEmpty(driverName)) {
    		// Teiid 8.3 and before
    		if(!TranslatorHelper.isTeiid84OrHigher(teiidVersion)) {
    			if (driverName.endsWith(DOT_RAR)) {
    				isRarDriver = true;
    			}
    		// Teiid 8.4 and later
     		} else  {
    			if(   driverName.equals(TranslatorHelper.TEIID_FILE_DRIVER_84UP) || driverName.equals(TranslatorHelper.TEIID_GOOGLE_DRIVER_84UP)
    			   || driverName.equals(TranslatorHelper.TEIID_INFINISPAN_DRIVER_84UP) || driverName.equals(TranslatorHelper.TEIID_LDAP_DRIVER_84UP)
    			   || driverName.equals(TranslatorHelper.TEIID_SALESORCE_DRIVER_84UP) || driverName.equals(TranslatorHelper.TEIID_WEBSERVICE_DRIVER_84UP)) {
    				isRarDriver = true;
    			}
    		}
    	}
    	
    	return isRarDriver;
    }
    
    /*
     * Return the isModifiable property value.  Need this currently because Teiid isModifiable values
     * are inverted for all .rar sources
     * @param driverName the driver name
     * @param propDefn the property definition
     * @return 'true' if modifiable, 'false' if not
     */
    private boolean isModifiable(String driverName, TeiidPropertyDefinition propDefn) {
        boolean isModifiable = propDefn.isModifiable();
        if(driverName!=null && driverName.endsWith(DOT_RAR)) {
            isModifiable = !isModifiable;
        }
        return isModifiable;
    }
    
    /*
     * Get the default value for the Managed ConnectionFactory class
     * @param propDefns the collection of property definitions
     * @return default value of the ManagedConnectionFactory, null if not found.
     */
    private String getManagedConnectionFactoryClassDefault (Collection<TeiidPropertyDefinition> propDefns) {
        String resultValue = null;
        for(TeiidPropertyDefinition pDefn : propDefns) {
            if(pDefn.getName().equalsIgnoreCase(CONN_FACTORY_CLASS_KEY)) {
                resultValue=(String)pDefn.getDefaultValue();
                break;
            }
        }
        return resultValue;
    }
    
}
