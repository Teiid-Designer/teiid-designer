/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.webservice.lds;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.metamatrix.core.plugin.PluginUtilities;
import com.metamatrix.core.util.FileUtils;
import com.metamatrix.core.util.TempDirectory;
import com.metamatrix.modeler.webservice.WebServicePlugin;
import com.metamatrix.modeler.webservice.util.AntTasks;

/**
 * @since 4.4
 */
public class DefaultWebArchiveReaderImpl implements
                                        WebArchiveReader {

    /**
     * @see com.metamatrix.modeler.webservice.lds.WebArchiveReader#getVdb(java.lang.String) *
     * @since 4.4
     */
    public InputStream getVdb(String webArchiveFileName) throws Exception {

        return new FileInputStream(getVdbFile(webArchiveFileName));
    }
    
    /**
     * @see com.metamatrix.modeler.webservice.lds.WebArchiveReader#getVdbFile(java.lang.String) *
     * @since 4.4
     */
    public File getVdbFile(String webArchiveFileName) throws Exception {

        File vdbFile = null;

        try {

            // Get the plugin directory.
            final String webServicePluginPath = PluginUtilities
                                                               .getPluginProjectLocation("com.metamatrix.modeler.webservice", true); //$NON-NLS-1$
            
            // Get the work directory and try to empty it.
            final String workDirectoryName = webServicePluginPath
                                             + File.separator
                                             + WebArchiveReaderConstants.LDS_WAR_READER_DIR
                                             + File.separator
                                             + WebArchiveReaderConstants.LDS_WAR_READER_WORK_DIR;            
            File workDirectory = new File(workDirectoryName);
            FileUtils.removeChildrenRecursively(workDirectory);            

            // Create the temporary directory.
            final TempDirectory tempDirectory = TempDirectory.getTempDirectory(workDirectoryName);
            final String tempDirectoryName = tempDirectory.getPath();

            // Expand the WAR file into the work directory.
            AntTasks.unzip(webArchiveFileName, tempDirectoryName);

            // Create the DQP JAR file work directory.
            final String dqpJarFileName = tempDirectoryName + File.separator + WebArchiveReaderConstants.DQP_JAR_FILE_NAME;
            final File dqpJarFileWorkDirectory = new File(tempDirectoryName
                                                          + File.separator
                                                          + WebArchiveReaderConstants.DQP_JAR_FILE_WORK_DIR);
            dqpJarFileWorkDirectory.mkdir();

            // Expand the DQP JAR file.
            AntTasks.unzip(dqpJarFileName, dqpJarFileWorkDirectory.getPath());

            // Read the VDB name from the web.xml file.
            SAXBuilder builder = new SAXBuilder();
            Document webXmlDocument = builder.build(tempDirectoryName
                                                    + File.separator
                                                    + WebArchiveReaderConstants.LDS_WEB_XML_FILE_NAME);

            final Element webAppElement = webXmlDocument.getRootElement();
            final List contextParams = webAppElement.getChildren("context-param"); //$NON-NLS-1$

            final Iterator contextParamIterator = contextParams.iterator();
            String vdbName = null;
            while (contextParamIterator.hasNext()) {

                final Element contextParamElement = (Element)contextParamIterator.next();
                final Element paramNameElement = contextParamElement.getChild("param-name"); //$NON-NLS-1$
                final String paramName = paramNameElement.getValue();

                if (WebArchiveReaderConstants.CONTEXT_PARAM_VDB_NAME.equals(paramName)) {

                    final Element paramValueElement = contextParamElement.getChild("param-value"); //$NON-NLS-1$
                    vdbName = paramValueElement.getValue();

                    break;
                }
            }

            // Get the VDB file.
            final String vdbFileName = dqpJarFileWorkDirectory + File.separator + "config" + File.separator + vdbName + ".vdb"; //$NON-NLS-1$ //$NON-NLS-2$
            vdbFile = new File(vdbFileName);          
        } catch (Exception e) {

            // Log the error.
            WebServicePlugin.Util.log(e);

            throw e;
        }

        return vdbFile;
    }    

    /**
     * @see com.metamatrix.modeler.webservice.lds.WebArchiveReader#clean() *
     * @since 4.4
     */
    public IStatus clean() throws Exception {

        try {
            
            // Get the plugin directory.
            final String webServicePluginPath = PluginUtilities
                                                               .getPluginProjectLocation("com.metamatrix.modeler.webservice", true); //$NON-NLS-1$
            
            // Get the work directory and try to empty it.
            final String workDirectoryName = webServicePluginPath
                                             + File.separator
                                             + WebArchiveReaderConstants.LDS_WAR_READER_DIR
                                             + File.separator
                                             + WebArchiveReaderConstants.LDS_WAR_READER_WORK_DIR;            
            File workDirectory = new File(workDirectoryName);
            FileUtils.removeChildrenRecursively(workDirectory);    
        } catch (Exception e) {

            final String msg = WebServicePlugin.Util.getString("WebArchiveBuilder.WebArchiveCleanFailed"); //$NON-NLS-1$
            final IStatus status = new Status(IStatus.ERROR, WebServicePlugin.PLUGIN_ID,
                                              WebArchiveReaderConstants.STATUS_CODE_WAR_FILE_CLEAN_FAILED, msg, e);

            // Log the error.
            WebServicePlugin.Util.log(status);

            return status;
        }

        final String msg = WebServicePlugin.Util.getString("WebArchiveBuilder.WebArchiveCleanSucceeded"); //$NON-NLS-1$
        final IStatus status = new Status(IStatus.OK, WebServicePlugin.PLUGIN_ID,
                                          WebArchiveReaderConstants.STATUS_CODE_WAR_FILE_CLEAN_SUCCEEDED, msg, null);

        return status;
    }

}
