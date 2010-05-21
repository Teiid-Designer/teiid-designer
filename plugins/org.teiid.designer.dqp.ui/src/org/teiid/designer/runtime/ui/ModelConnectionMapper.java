/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui;

import java.util.Collection;
import java.util.Properties;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.teiid.designer.runtime.Connector;
import org.teiid.designer.runtime.ExecutionAdmin;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.modeler.dqp.DqpPlugin;

/**
 * 
 */
public class ModelConnectionMapper {
    /**
     * 
     */
    String modelName;
    Properties properties;
    private ExecutionAdmin executionAdmin;

    /**
     * ModelConnectionFactoryMapper
     * 
     * @param modelName
     * @param properties
     */
    public ModelConnectionMapper( String modelName,
                                         Properties properties ) {
        CoreArgCheck.isNotEmpty(modelName);
        CoreArgCheck.isNotEmpty(properties);
        this.modelName = modelName;
        this.properties = (Properties)properties.clone();
    }

    /**
     * This method returns a string representing the name of the connection factory that most closely matches the model name and
     * properties provided via the constructor. In the simplest case, JDBC information like URL, username, database name, etc. can
     * be used.
     * 
     * @return
     */
    public String findConnectionFactoryName() throws Exception {
        /*
         * This JDBC data may be stored in the relational model as JDBC import data similar to the following:
         * <jdbc:JdbcSource xmi:uuid="xxxxxxxx" 
         *      name="BooksSQL" 
         *      driverName="SQL Server"
         *      driverClass="com.microsoft.sqlserver.jdbc.SQLServerDriver" 
         *      username="books"
         *      url="jdbc:sqlserver://slntdb02.mm.atl2.redhat.com:1433;databaseName=books"> 
         * </jdbc:JdbcSource>.
         *
         *  
         *  <config-property> 
         *      <description>{$display:"Connector Class",$advanced:"true"}</description>
         *      <config-property-name>ConnectorClass</config-property-name> 
         *      <config-property-type>java.lang.String</config-property-type>
         *      <config-property-value>org.teiid.connector.jdbc.JDBCConnector</config-property-value> 
         *  </config-property>
         *  <connection-url>jdbc:mysql://localhost:3306/BQT1</connection-url> 
         *  <driver-class>com.mysql.jdbc.Driver</driver-class>
         */

        // Find Source
    	String connectionFactoryName = modelName + "_XXXX";
    	
    	if( getExecutionAdmin() != null ) {
	        Collection<Connector> connectors = getExecutionAdmin().getSourceBindingsManager().getConnectorsForModel(this.modelName);
	
	        if (connectors == null) {
	            // Create a connector
	             MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Select Connection Profile", "TBD  SELECT CONNECTION PROFILE DIALOG");
	        } else {
	            // May have to ask user to choose a name if multiple??
	            connectionFactoryName = connectors.iterator().next().getName();
	        }
    	}
        return connectionFactoryName;
    }

    /**
     * Get the <code>ExecutionAdmin</code>
     * 
     * @return executionAdmin the <code>ExecutionAdmin</code>
     * @throws Exception
     */
    private ExecutionAdmin getExecutionAdmin() throws Exception {
        if (this.executionAdmin == null && DqpPlugin.getInstance().getServerManager().getDefaultServer() != null) {
            this.executionAdmin = DqpPlugin.getInstance().getServerManager().getDefaultServer().getAdmin();
        }
        return this.executionAdmin;
    }

    /**
     * @return modelName
     */
    public String getModelName() {
        return modelName;
    }

    /**
     * @return properties
     */
    public Properties getProperties() {
        return properties;
    }

}
