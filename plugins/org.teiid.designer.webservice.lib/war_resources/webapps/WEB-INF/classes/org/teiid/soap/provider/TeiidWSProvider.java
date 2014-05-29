/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.soap.provider;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.soap.SOAPFaultException;

import org.teiid.soap.SoapPlugin;

// Only include following BindingType if using SOAP1.2
// @BindingType(value =
// "http://java.sun.com/xml/ns/jaxws/2003/05/soap/bindings/HTTP/")
public class TeiidWSProvider {

	/*
	 * These are the standard SOAP 1.1 fault codes that we use in the Data
	 * Service web service implementation to report fault conditions to the
	 * user.
	 */
	public static final String SOAP_11_STANDARD_CLIENT_FAULT_CODE = "Client"; //$NON-NLS-1$
	public static final String SOAP_11_STANDARD_SERVER_FAULT_CODE = "Server"; //$NON-NLS-1$

	protected WebServiceContext webServiceContext;
	private Connection conn;
	private PreparedStatement statement;
	private ResultSet set;
	String wsdlOperation;

	private static Logger logger = Logger.getLogger("org.teiid.soap"); //$NON-NLS-1$

	private static Properties properties = new Properties();

	@javax.annotation.Resource
	protected void setWebServiceContext(WebServiceContext wsc) {
		webServiceContext = wsc;
	}

	public DataSource getDataSource() throws NamingException {

		InitialContext ctx;
		DataSource ds = null;
		ctx = new InitialContext();
		ds = (DataSource) ctx.lookup(properties.getProperty("jndiName")); //$NON-NLS-1$

		return ds;
	}

	public Source execute(String procedureName, String inputMessage)
			throws SOAPFaultException, SOAPException {

		// Load
		try {
			// Get the inputStream
			InputStream inputStream = getClass().getClassLoader()
					.getResourceAsStream("teiidsoap.properties"); //$NON-NLS-1$

			Properties properties = new Properties();

			// load the inputStream using the Properties
			properties.load(inputStream);

		} catch (IOException e1) {
			String msg = SoapPlugin.Util.getString(
					"TeiidWSProvider.1"); //$NON-NLS-1$
			logger.logrb(Level.SEVERE,  "TeiidWSProvider", "execute", SoapPlugin.PLUGIN_ID, msg, new Throwable(e1)); //$NON-NLS-1$ //$NON-NLS-2$
			createSOAPFaultMessage(e1, e1.getMessage(),
					SOAP_11_STANDARD_SERVER_FAULT_CODE);
		}

		// Get a connection
		Source returnFragment = null;

		try {

			DataSource ds = getDataSource();
			conn = ds.getConnection();
			String responseString;
			boolean noParm = false;
			if (inputMessage.equals("")) { //$NON-NLS-1$
				noParm = true;
			}

			final String executeStatement = "call " + procedureName + (noParm ? "()" : "(?)") + ";"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

			statement = conn.prepareStatement(executeStatement);
			if (!noParm) {
				statement.setString(1, inputMessage);
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
					responseString = ((SQLXML)sqlXml).getString();
					InputStream is = null;
					try {
						is = new ByteArrayInputStream(responseString
								.getBytes("UTF-8")); //$NON-NLS-1$
					} catch (UnsupportedEncodingException e) {
						logger.log(Level.SEVERE, SoapPlugin.Util
								.getString("TeiidWSProvider.1") //$NON-NLS-1$
								+ procedureName);
						createSOAPFaultMessage(e, e.getMessage(),
								SOAP_11_STANDARD_SERVER_FAULT_CODE);
					}
					returnFragment = new StreamSource(is);

				} else {
					logger.log(Level.WARNING, SoapPlugin.Util
							.getString("TeiidWSProvider.8") //$NON-NLS-1$
							+ procedureName);
					createSOAPFaultMessage(new Exception(SoapPlugin.Util
							.getString("TeiidWSProvider.2")), //$NON-NLS-1$
							SoapPlugin.Util.getString("TeiidWSProvider.3"), //$NON-NLS-1$
							SOAP_11_STANDARD_SERVER_FAULT_CODE);
				}

				if (set.next()) {
					createSOAPFaultMessage(new Exception(SoapPlugin.Util
							.getString("TeiidWSProvider.4") //$NON-NLS-1$
							+ wsdlOperation
							+ SoapPlugin.Util.getString("TeiidWSProvider.5")), //$NON-NLS-1$
							SoapPlugin.Util.getString("TeiidWSProvider.6"), //$NON-NLS-1$
							SOAP_11_STANDARD_SERVER_FAULT_CODE);
				}

				set.close();
			}

			statement.close();

			/*
			 * If we fall through to here and no XML Fragment has been set on
			 * the returnMessage instance because 'hasResults' was false, then
			 * the return message is an empty message with no body contents. We
			 * do this only because we do not know what to do with a returned
			 * update count. We cannot return it as the body of the message
			 * because we will likely violate the schema type that defines the
			 * return message. The only thing i can think to do is to return an
			 * empty message in this instance. We really should handle this
			 * situation more explicitly in the future. (ie Operations in Web
			 * Service Models in the modeler should be able to be considered
			 * 'update' type operations and return a simple int).
			 */

		} catch (SQLException e) {
			String faultcode = SOAP_11_STANDARD_SERVER_FAULT_CODE;
			String msg = SoapPlugin.Util.getString("TeiidWSProvider.1"); //$NON-NLS-1$
			logger.logrb(Level.SEVERE,  "TeiidWSProvider", "execute", SoapPlugin.PLUGIN_ID, msg, new Throwable(e)); //$NON-NLS-1$ //$NON-NLS-2$
			if (e instanceof SQLException) {
				final SQLException sqlException = (SQLException) e;
				if (SQLStates.isUsageErrorState(sqlException.getSQLState())) {
					faultcode = SOAP_11_STANDARD_CLIENT_FAULT_CODE;
				}
			}
			createSOAPFaultMessage(e, e.getMessage(), faultcode);
		

		} catch (Exception e) {
			String faultcode = SOAP_11_STANDARD_SERVER_FAULT_CODE;
			String msg = SoapPlugin.Util.getString("TeiidWSProvider.1"); //$NON-NLS-1$
			logger.logrb(Level.SEVERE,  "TeiidWSProvider", "execute", SoapPlugin.PLUGIN_ID, msg, new Throwable(e)); //$NON-NLS-1$ //$NON-NLS-2$
			if (e instanceof SQLException) {
				final SQLException sqlException = (SQLException) e;
				if (SQLStates.isUsageErrorState(sqlException.getSQLState())) {
					faultcode = SOAP_11_STANDARD_CLIENT_FAULT_CODE;
				}
			}

			createSOAPFaultMessage(e, e.getMessage(), faultcode);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					/*
					 * In this case, we do not return a SOAP fault to the
					 * customer. We simply log this problem in the log. If we do
					 * a return from the finally block, we will override the
					 * return in the try/catch that will either return the
					 * 'true' error or return a valid result document. Either
					 * way we do not want to return a SOAP fault just because
					 * closing the connection failed.
					 */
					String msg = SoapPlugin.Util.getString(
							"TeiidWSProvider.1"); //$NON-NLS-1$
					logger.logrb(Level.SEVERE,  "TeiidWSProvider", "execute", SoapPlugin.PLUGIN_ID, msg, new Throwable(e)); //$NON-NLS-1$ //$NON-NLS-2$
					
				}
			}
		}

		return returnFragment;
	}

	protected void createSOAPFaultMessage(final Exception e,
			final String faultSoapPluginString, final String faultCode)
			throws SOAPFaultException, SOAPException {

		SOAPFactory fac = SOAPFactory.newInstance();
		SOAPFault sf = fac.createFault(faultSoapPluginString, new QName(
				"http://schemas.xmlsoap.org/soap/envelope/", faultCode)); //$NON-NLS-1$
		throw new SOAPFaultException(sf);
	}

	private void loadProperties() throws SOAPFaultException, SOAPException {
		try {
			// Get the inputStream
			InputStream inputStream = getClass().getClassLoader()
					.getResourceAsStream("teiidsoap.properties"); //$NON-NLS-1$

			properties = new Properties();

			// load the inputStream using the Properties
			properties.load(inputStream);

		} catch (IOException e) {
			String msg = SoapPlugin.Util.getString(
					"TeiidWSProvider.1"); //$NON-NLS-1$
			logger.logrb(Level.SEVERE,  "TeiidWSProvider", "loadProperties", SoapPlugin.PLUGIN_ID, msg, new Throwable(e)); //$NON-NLS-1$ //$NON-NLS-2$
			throw new RuntimeException(e);
		}
	}

	public javax.xml.transform.Source invoke(Source request) {
		javax.xml.transform.Source response = null; 
		String inputMessage = ""; //$NON-NLS-1$
		String procedureName = ""; //$NON-NLS-1$
		MessageContext mc = webServiceContext.getMessageContext();
		Object wsdlOperationQName = mc.get(MessageContext.WSDL_OPERATION);  
		
		// Load the properties object
		try {
			loadProperties();
		} catch (SOAPFaultException e) {
			logger.log(Level.SEVERE, e.getMessage());
			throw new RuntimeException(e);
		} catch (SOAPException e) {
			logger.log(Level.SEVERE, e.getMessage());
			throw new RuntimeException(e);
		}

		// Get procedure name from properties object based on operation name
		if (wsdlOperationQName != null) {
			wsdlOperation = ((QName) wsdlOperationQName).getLocalPart();
			procedureName = properties.getProperty(wsdlOperation);
		}
 
		if (request == null) {
			// No parameters
		} else {
			try {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				StreamResult sr = new StreamResult(bos);
				Transformer trans = TransformerFactory.newInstance()
						.newTransformer();
				trans.transform(request, sr);
				inputMessage = bos.toString();
				bos.close();
			} catch (Exception e) {
				logger.log(Level.WARNING, SoapPlugin.Util
						.getString("TeiidWSProvider.7") //$NON-NLS-1$
						+ procedureName);
				throw new RuntimeException(e);
			}
		}

		try {
			response = execute(procedureName, inputMessage);
		} catch (SOAPFaultException e) {
			throw new RuntimeException(e);
		} catch (SOAPException e) {
			logger.log(Level.SEVERE, SoapPlugin.Util
					.getString("TeiidWSProvider.9") //$NON-NLS-1$
					+ procedureName);
			throw new RuntimeException(e);
		}

		return response;
	}
	
	/** 
	 * Utility class containing 1) SQL state constants used to represent JDBC error state code, and
	 * 2) utility methods to check whether a SQL state belongs to a particular class of exception states.
	 * @since 4.3
	 */
	public static class SQLStates {
		
		/**
		 * Identifies the SQLState class Connection Exception (08).
		 */
		public static final SQLStateClass CLASS_CONNECTION_EXCEPTION = new SQLStateClass(
				"08"); //$NON-NLS-1$

		/**
		 * Connection Exception with no subclass (SQL-99 08000)
		 */
		public static final String CONNECTION_EXCEPTION_NO_SUBCLASS = "08000"; //$NON-NLS-1$

		/**
		 * SQL-client unable to establish SQL-connection (SQL-99 08001)
		 */
		public static final String CONNECTION_EXCEPTION_SQLCLIENT_UNABLE_TO_ESTABLISH_SQLCONNECTION = "08001"; //$NON-NLS-1$

		/**
		 * Connection name in use (SQL-99 08002)
		 */
		public static final String CONNECTION_EXCEPTION_CONNECTION_NAME_IN_USE = "08002"; //$NON-NLS-1$

		/**
		 * Connection does not exist (SQL-99 08003)
		 */
		public static final String CONNECTION_EXCEPTION_CONNECTION_DOES_NOT_EXIST = "08003"; //$NON-NLS-1$

		/**
		 * SQL-server rejected establishment of SQL-connection (SQL-99 08004)
		 */
		public static final String CONNECTION_EXCEPTION_SQLSERVER_REJECTED_ESTABLISHMENT_OF_SQLCONNECTION = "08004"; //$NON-NLS-1$

		/**
		 * Connection failure (SQL-99 08006)
		 */
		public static final String CONNECTION_EXCEPTION_CONNECTION_FAILURE = "08006"; //$NON-NLS-1$

		/**
		 * Transaction resolution unknown (SQL-99 08007)
		 */
		public static final String CONNECTION_EXCEPTION_TRANSACTION_RESOLUTION_UNKNOWN = "08007"; //$NON-NLS-1$

		/**
		 * Connection is stale and should no longer be used. (08S01)
		 * <p>
		 * The SQLState subclass S01 is an implementation-specified condition and
		 * conforms to the subclass DataDirect uses for SocketExceptions.
		 */
		public static final String CONNECTION_EXCEPTION_STALE_CONNECTION = "08S01"; //$NON-NLS-1$

		// Class 28 - invalid authorization specification

		/**
		 * Identifies the SQLState class Invalid Authorization Specification (28).
		 */
		public static final SQLStateClass CLASS_INVALID_AUTHORIZATION_SPECIFICATION = new SQLStateClass(
				"28"); //$NON-NLS-1$

		/**
		 * Invalid authorization specification with no subclass (SQL-99 28000)
		 */
		public static final String INVALID_AUTHORIZATION_SPECIFICATION_NO_SUBCLASS = "28000"; //$NON-NLS-1$
		
		
		// Class 38 - External Routine Exception (as defined by SQL spec):
	    /** External routine exception. This is the default unknown code */
	    public static final String DEFAULT = "38000"; //$NON-NLS-1$
	    
	    public static final String SUCESS = "00000"; //$NON-NLS-1$

	    // Class 50 - Query execution errors
	    public static final SQLStateClass CLASS_USAGE_ERROR = new SQLStateClass("50"); //$NON-NLS-1$
	    /** General query execution error*/
	    public static final String USAGE_ERROR = "50000"; //$NON-NLS-1$
	    /** Error raised by ERROR instruction in virtual procedure.*/
	    public static final String VIRTUAL_PROCEDURE_ERROR = "50001"; //$NON-NLS-1$
	    
	    private static final SQLStateClass[] stateClasses = {CLASS_USAGE_ERROR};
	    static {
	        CLASS_USAGE_ERROR.stateCodes.add(USAGE_ERROR);
	        CLASS_USAGE_ERROR.stateCodes.add(VIRTUAL_PROCEDURE_ERROR);
	    }

	    public static boolean isSystemErrorState(String sqlStateCode) {
	        return !isUsageErrorState(sqlStateCode);
	    }
	    
	    public static boolean isUsageErrorState(String sqlStateCode) {
	        return belongsToClass(sqlStateCode, CLASS_USAGE_ERROR);
	    }
	    
	    public static boolean belongsToClass(String sqlStateCode, SQLStateClass sqlStateClass) {
	        return sqlStateCode.startsWith(sqlStateClass.codeBeginsWith);
	    }
	    
	    public static SQLStateClass getClass(String sqlStateCode) {
	        for (int i = 0; i < stateClasses.length; i++) {
	            if (stateClasses[i].containsSQLState(sqlStateCode)) {
	                return stateClasses[i];
	            }
	        }
	        return null;
	    }
	    
	    public static final class SQLStateClass {
	        private String codeBeginsWith;
	        private Set<String> stateCodes = new HashSet<String>();
	        private SQLStateClass(String beginsWith) {
	            this.codeBeginsWith = beginsWith;
	        }
	        
	        public boolean containsSQLState(String sqlState) { 
	            return stateCodes.contains(sqlState);
	        }
	    }
	}

}
