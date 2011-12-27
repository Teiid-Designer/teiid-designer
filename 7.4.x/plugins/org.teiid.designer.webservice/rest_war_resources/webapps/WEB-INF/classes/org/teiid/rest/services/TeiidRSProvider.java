/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.rest.services;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.xml.ws.WebServiceContext;

import org.teiid.rest.RestPlugin;

public class TeiidRSProvider {

    protected WebServiceContext webServiceContext;
    private Connection conn;
    private PreparedStatement statement;
    private ResultSet set;

    private static Logger logger = Logger.getLogger("org.teiid.rest"); //$NON-NLS-1$

    private static Properties properties = new Properties();

    @javax.annotation.Resource
    protected void setWebServiceContext( WebServiceContext wsc ) {
        webServiceContext = wsc;
        loadProperties();
    }

    public DataSource getDataSource() throws NamingException {

        InitialContext ctx;
        DataSource ds = null;
        ctx = new InitialContext();
        ds = (DataSource)ctx.lookup(properties.getProperty("jndiName")); //$NON-NLS-1$
        return ds;
    }

    public String execute( String procedureName,
                           Map<String, String> parameterMap ) throws WebApplicationException {

        // Load
        try {
            // Get the inputStream
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("teiidrest.properties"); //$NON-NLS-1$

            // load the inputStream using the Properties
            properties.load(inputStream);

        } catch (IOException e1) {
            String msg = RestPlugin.Util.getString("TeiidRSProvider.1"); //$NON-NLS-1$
            logger.logrb(Level.SEVERE, "TeiidRSProvider", "execute", RestPlugin.PLUGIN_ID, msg, new Throwable(e1)); //$NON-NLS-1$ //$NON-NLS-2$
            createWebApplicationException(e1, e1.getMessage());
        }

        String responseString = null;

        try {

            DataSource ds = getDataSource();
            conn = ds.getConnection();
            boolean noParm = false;
            if (parameterMap.isEmpty()) {
                noParm = true;
            }

            final String executeStatement = "call " + procedureName + (noParm ? "()" : createParmString(parameterMap)) + ";"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 

            statement = conn.prepareStatement(executeStatement);
            if (!noParm) {
                int i = 1;
                for (Object value : parameterMap.values()) {
                    statement.setString(i++, (String)value);
                }
            }

            final boolean hasResultSet = statement.execute();

            if (hasResultSet) {

                set = statement.getResultSet();

                if (set.next()) {
                    /*
                     * an XML result set that is appropriate for a Data Service
                     * web service will ALWAYS return a single XML Document
                     * result. The first row in the first column. If there are
                     * additional rows, we throw an exception as this resultset
                     * is not appropriate for a Data Service.
                     */
                    SQLXML sqlXml = (SQLXML)set.getObject(1);
                    responseString = sqlXml.getString();
                } else {
                    logger.log(Level.WARNING, RestPlugin.Util.getString("TeiidRSProvider.8") //$NON-NLS-1$
                                              + procedureName);
                    createWebApplicationException(new Exception(RestPlugin.Util.getString("TeiidRSProvider.2")), //$NON-NLS-1$
                                                  RestPlugin.Util.getString("TeiidRSProvider.2")); //$NON-NLS-1$
                }

                set.close();
            }

            statement.close();

        } catch (SQLException e) {
            String msg = RestPlugin.Util.getString("TeiidRSProvider.1"); //$NON-NLS-1$
            logger.logrb(Level.SEVERE, "TeiidRSProvider", "execute", RestPlugin.PLUGIN_ID, msg, new Throwable(e)); //$NON-NLS-1$ //$NON-NLS-2$
            createWebApplicationException(e, e.getMessage());

        } catch (Exception e) {
            String msg = RestPlugin.Util.getString("TeiidRSProvider.1"); //$NON-NLS-1$
            logger.logrb(Level.SEVERE, "TeiidRSProvider", "execute", RestPlugin.PLUGIN_ID, msg, new Throwable(e)); //$NON-NLS-1$ //$NON-NLS-2$
            createWebApplicationException(e, e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    /*
                     * In this case, we do not return an exception to the
                     * customer. We simply log this problem. If we do
                     * a return from the finally block, we will override the
                     * return in the try/catch that will either return the
                     * 'true' error or return a valid result document. Either
                     * way we do not want to return an exception just because
                     * closing the connection failed.
                     */
                    String msg = RestPlugin.Util.getString("TeiidRSProvider.1"); //$NON-NLS-1$
                    logger.logrb(Level.SEVERE, "TeiidRSProvider", "execute", RestPlugin.PLUGIN_ID, msg, new Throwable(e)); //$NON-NLS-1$ //$NON-NLS-2$

                }
            }
        }

        return responseString;
    }

    protected String createParmString( Map<String, String> parameterMap ) {
        StringBuilder sb = new StringBuilder();
        sb.append("(?"); //$NON-NLS-1$
        for (int i = 1; i < parameterMap.size(); i++) {
            sb.append(","); //$NON-NLS-1$
            sb.append("?"); //$NON-NLS-1$
        }
        sb.append(")"); //$NON-NLS-1$
        return sb.toString();
    }

    protected void createWebApplicationException( final Exception e,
                                                  final String faultSoapPluginString ) throws WebApplicationException {
        throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
    }

    private void loadProperties() {
        try {
            // Get the inputStream
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("teiidrest.properties"); //$NON-NLS-1$

            properties = new Properties();

            // load the inputStream using the Properties
            properties.load(inputStream);

        } catch (IOException e) {
            String msg = RestPlugin.Util.getString("TeiidRSProvider.1"); //$NON-NLS-1$
            logger.logrb(Level.SEVERE, "TeiidRSProvider", "loadProperties", RestPlugin.PLUGIN_ID, msg, new Throwable(e)); //$NON-NLS-1$ //$NON-NLS-2$
            throw new RuntimeException(e);
        }
    }

}
