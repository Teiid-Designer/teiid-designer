/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.dqp.internal.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;

import com.metamatrix.common.config.api.ComponentType;
import com.metamatrix.common.config.api.Configuration;
import com.metamatrix.common.config.api.ConfigurationModelContainer;
import com.metamatrix.common.config.model.BasicConfigurationObjectEditor;
import com.metamatrix.common.config.model.ConfigurationModelContainerAdapter;
import com.metamatrix.common.config.model.ConfigurationModelContainerImpl;
import com.metamatrix.common.config.xml.XMLConfigurationImportExportUtility;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;


/** 
 * @since 4.3
 */
public class ConfigFileManager {
    
    public final static String CONFIG_FILE_NAME = "configuration.xml";             //$NON-NLS-1$
    
    private Map   configFileList;
    private IPath configFilePath;
    private ConfigurationModelContainerAdapter configurationReader;
    private BasicConfigurationObjectEditor editor = new BasicConfigurationObjectEditor(false);
    
    /**
     *  
     * @param configFilePath
     * @throws Exception
     * @since 4.3
     */
    public ConfigFileManager(IPath configFilePath) throws Exception {
        
        this.configFilePath = configFilePath;
        configurationReader = new ConfigurationModelContainerAdapter();
    }
    
    /**
     *  
     * @return ConfigurationModelContainer
     * @throws Exception
     * @since 4.3
     */
    public ConfigurationModelContainer loadConfig(String fileName) throws Exception {
        
        IPath path = getPath().append(fileName);
        return loadConfig( path.toFile() );
    }    
    
    /**
     *  
     * @param configFile
     * @throws Exception
     * @since 4.3
     */
    private ConfigurationModelContainer loadConfig(File configFile) throws Exception {
        
        if(!configFile.exists())
            throw new FileNotFoundException();
        
        FileInputStream in = new FileInputStream(configFile);                        
        ConfigurationModelContainerImpl cmc = (ConfigurationModelContainerImpl)configurationReader.readConfigurationModel(in, Configuration.NEXT_STARTUP_ID);
         
        return cmc;
    }
        
    /**
     *  
     * @param configFile
     * @throws Exception
     * @since 4.3
     */
    protected ConfigurationModelContainer importConfig(File configFile) throws Exception {        
        return loadConfig(configFile);
    }
        
    /**
     *  
     * @param cmc
     * @return boolean
     * @throws Exception
     * @since 4.3
     */
    public boolean saveConfig(ConfigurationModelContainer cmc, String fileName) throws Exception {
              
        IPath path = getPath().append(fileName);                
        FileOutputStream out = new FileOutputStream(path.toFile());          
        configurationReader.writeConfigurationModel(out, cmc, "Configuration"); //$NON-NLS-1$
        
        return true;
    }
        
    /**
     *  
     * @return BasicConfigurationObjectEditor
     * @since 4.3
     */
    protected BasicConfigurationObjectEditor getBasicConfigurationObjectEditor() {
        return editor;
    }
        
    /**
     *  
     * @param connectorFile
     * @return ComponentType
     * @throws Exception
     * @since 4.3
     */
    public ComponentType loadConnectorType(File connectorFile)throws Exception {
                
        if(!connectorFile.exists())
            throw new FileNotFoundException();
                                
        FileInputStream in = new FileInputStream(connectorFile);
        XMLConfigurationImportExportUtility io = new XMLConfigurationImportExportUtility();
        return io.importConnector(in, editor, null); 
    }
        
    /**
     *  
     * @return IPath
     * @since 4.3
     */
    protected IPath getPath() {
        return (IPath)configFilePath.clone();
    }
    
    /**
     *  
     * @param configFileManager
     * @return
     * @throws Exception
     * @since 4.3
     */
    protected Map loadConfigFiles() throws Exception{        
        
        File dqpFolderPath = getPath().toFile();
        
        if(!dqpFolderPath.exists()) throw new FileNotFoundException();
        
        String[] fileNameList = dqpFolderPath.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return (name.endsWith(ModelUtil.DOT_EXTENSION_XML) && !name
						.endsWith(DqpPath.PLUGIN_XML));
			}
		});
        
        if(fileNameList.length == 0) throw new FileNotFoundException();
        
        ConfigurationModelContainer cmc;                
        ConnectionManager conn;
        
        configFileList = new HashMap(fileNameList.length);
        
        for(int idx = 0; idx < fileNameList.length; idx++) {
            try {
                cmc  = loadConfig(fileNameList[idx]);                
                conn = new ConnectionManager(cmc, fileNameList[idx]);
                configFileList.put(fileNameList[idx], conn);
            }catch(Exception e) {
                DqpPlugin.Util.log(IStatus.ERROR, e, DqpPlugin.Util.getString("ConfigFileManager.errorProcessingFile", fileNameList[idx])); //$NON-NLS-1$
            }
        }
        
        return configFileList;
    }
    
}
