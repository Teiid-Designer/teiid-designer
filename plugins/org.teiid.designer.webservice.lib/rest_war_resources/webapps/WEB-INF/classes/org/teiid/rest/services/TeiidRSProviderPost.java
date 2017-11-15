/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.rest.services;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.nio.charset.Charset;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLXML;
import java.sql.Statement;
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

import org.teiid.core.types.BlobType;
import org.teiid.core.types.XMLType;
import org.teiid.core.util.ReaderInputStream;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion;
import org.teiid.query.parser.TeiidNodeFactory;
import org.teiid.query.parser.TeiidNodeFactory.ASTNodes;
import org.teiid.query.sql.symbol.XMLSerialize;
import org.teiid.query.function.source.XMLSystemFunctions;
import org.teiid.rest.RestPlugin;

public class TeiidRSProviderPost {

    protected WebServiceContext webServiceContext;

    private static Logger logger = Logger.getLogger("org.teiid.rest"); //$NON-NLS-1$
    
    @javax.annotation.Resource
    protected void setWebServiceContext( WebServiceContext wsc ) {
        webServiceContext = wsc;
    }

    public DataSource getDataSource(String jndiName) throws NamingException {

        InitialContext ctx;
        DataSource ds = null;
        ctx = new InitialContext();
        ds = (DataSource)ctx.lookup(jndiName); //$NON-NLS-1$
        return ds;
    }

    public InputStream execute( String procedureName,
                           Map<String, String> parameterMap, String charSet, Properties properties) throws WebApplicationException {

    	Connection conn = null;
    	PreparedStatement statement = null;
    	Object result = null;
    	InputStream resultStream = null;
     
        try {

            DataSource ds = getDataSource(properties.getProperty("jndiName"));
            conn = ds.getConnection();
            boolean noParm = false;
            if (parameterMap.isEmpty()) {
                noParm = true;
            }
            
            final String lobSetting = "SELECT teiid_session_set('clean_lobs_onclose', false)";
            
            Statement lobSettingStatement = conn.createStatement();
            
            lobSettingStatement.execute(lobSetting);

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
                ResultSet rs = statement.getResultSet();
                if (rs.next()) {
                    result = rs.getObject(1);
                } else {
                    logger.log(Level.WARNING, RestPlugin.Util.getString("TeiidRSProvider.8") //$NON-NLS-1$
                                              + procedureName);
                    createWebApplicationException(new Exception(RestPlugin.Util.getString("TeiidRSProvider.2")), //$NON-NLS-1$
                                                  RestPlugin.Util.getString("TeiidRSProvider.2")); //$NON-NLS-1$
                }

                rs.close();
            }

            statement.close(); 
            resultStream = handleResult(charSet, result, properties);

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
        
        return resultStream; 
    }
    
    private InputStream handleResult(String charSet, Object result, Properties properties) throws SQLException, UnsupportedEncodingException {
        if (result == null) {
        	return null;
        }
        
        String teiidVersion = properties.getProperty("teiidVersion");
        ITeiidServerVersion version = new TeiidServerVersion(teiidVersion);
        InputStream iStreamResult = null;
        
		if (result instanceof SQLXML) {
			
			if (charSet != null) {
                XMLSerialize serialize = (XMLSerialize)TeiidNodeFactory.getInstance().create(version, ASTNodes.XML_SERIALIZE);
		    	serialize.setTypeString("blob"); //$NON-NLS-1$
		    	serialize.setDeclaration(true);
		    	serialize.setEncoding(charSet);
		    	serialize.setDocument(true);

		    	XMLType type = new XMLType((SQLXML)result);
		    	
		    	type.setEncoding(charSet);

		    	try {
		    		iStreamResult = ((BlobType)XMLSystemFunctions.serialize(serialize, type)).getBinaryStream();
				} catch (Exception e) {
					throw new SQLException(e);
				}
			}
			iStreamResult = ((SQLXML)result).getBinaryStream();
		}
		else if (result instanceof Blob) {
			iStreamResult =  ((Blob)result).getBinaryStream();
		}
		else if (result instanceof Clob) {
			iStreamResult =  new ReaderInputStream(((Clob)result).getCharacterStream(), Charset.forName(charSet));
		}else{
			iStreamResult =  new ByteArrayInputStream(result.toString().getBytes(charSet));
		}
		return iStreamResult;
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

}
