/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.dqp;

import java.io.File;
import junit.framework.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import com.metamatrix.core.util.SmartTestSuite;
import com.metamatrix.modeler.dqp.config.ConfigurationManager;
import com.metamatrix.modeler.dqp.internal.config.ConfigurationManagerImpl;
import com.metamatrix.modeler.dqp.internal.config.DqpExtensionsHandler;
import com.metamatrix.modeler.dqp.internal.config.DqpPath;


/** 
 * @since 4.3
 */
public class ConfigTestHelper {

    public final static String CONFIG_FILE_NAME         = "configuration.xml"; //$NON-NLS-1$ 
    public final static String BASE_CONFIG_PATH         = SmartTestSuite.getTestDataPath();    
    public final static String CONFIG_WORKING_DIR       = BASE_CONFIG_PATH + "\\DqpConfiguration";  //$NON-NLS-1$
    
    private ConfigurationManager configMgr;
    private DqpExtensionsHandler extensionsHandler;
    
    public ConfigTestHelper() {}
    
    public void tearDown() {
        File testDir = new File(CONFIG_WORKING_DIR);
        
        if (testDir.exists()) {
            File[] files = testDir.listFiles();
            
            for (int i = 0; i < files.length; ++i) {
                files[i].delete();
            }
        }
    }
    
    public IPath getPath() {
        
        IPath path = new Path(BASE_CONFIG_PATH);
        
        return (IPath)path.clone();   
    }
    
    public IPath getConfigPath() {
        return DqpPath.getRuntimeConfigPath();
    }
    
    public IPath getExtensionsPath() {
        return DqpPath.getRuntimeConnectorsPath();
    }

    public ConfigurationManager getConfigurationManager()throws Exception {
        IPath path = getConfigPath();
        Assert.assertNotNull(path);        

        if (this.configMgr == null) {
        	this.configMgr = new ConfigurationManagerImpl(getConfigPath());
    	}
        
        return this.configMgr;
    }
    
    public DqpExtensionsHandler getExtensionsHandler() {
        IPath path = getConfigPath();
        Assert.assertNotNull(path);        

        if (this.extensionsHandler == null) {
    		this.extensionsHandler = new DqpExtensionsHandler();
    	}
    	
    	return this.extensionsHandler;
    }
    
    /**
     *  
     * @return
     * @since 4.3
     */
    public String getUdfExtensionClasspath() {
        String path = DqpPath.getRuntimeConfigPath().toString();
        if(path.indexOf("\\") != -1){                   //$NON-NLS-1$
            path = path.replace('\\', IPath.SEPARATOR); 
        }
        
        return path + "/jdbcconn.jar;" +                //$NON-NLS-1$
                        path + "/loopbackconn.jar;" +    //$NON-NLS-1$
                        path + "/sampleconn.jar";        //$NON-NLS-1$
    }
}
