/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.wsdl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.wst.wsdl.validation.internal.IValidationMessage;
import org.eclipse.wst.wsdl.validation.internal.IValidationReport;
import org.eclipse.wst.wsdl.validation.internal.WSDLValidator;
import org.eclipse.wst.wsdl.validation.internal.eclipse.URIResolverWrapper;

import com.metamatrix.common.protocol.URLHelper;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Model;
import com.metamatrix.modeler.modelgenerator.wsdl.model.ModelGenerationException;
import com.metamatrix.modeler.modelgenerator.wsdl.model.internal.ModelBuilder;
import com.metamatrix.modeler.modelgenerator.wsdl.validation.WSDLValidationException;
import com.metamatrix.modeler.modelgenerator.wsdl.validation.WSDLValidationMessage;
import com.metamatrix.ui.ICredentialsCommon.SecurityType;

/**
 * This class is responsible for reading WSDL files from a URI or filesystem validating them and producing an OO representation of
 * the WSDL contents
 */
public class WSDLReader {

    private String wsdlURI;
    private SecurityType securityType = SecurityType.None;
    private String userName;
    private String password;

	private static WSDLValidator VALIDATOR;

    public static final int VALIDATION_SEVERITY_ERROR = 0;
    public static final int VALIDATION_SEVERITY_WARNING = 1;

    public WSDLReader() {
        this(null);
    }

    /**
     * @param fileUri the URI of the WSDL file
     */
    public WSDLReader( String fileUri ) {
        wsdlURI = fileUri;
    }

    /**
     * @return the Model based on the given WSDL
     * @throws ModelGenerationException
     */
    public Model getModel() throws ModelGenerationException {
        Model theModel = null;
        try {
            theModel = buildWSDLStructures();
        } catch (NullPointerException ex) {
            ModelGeneratorWsdlPlugin.Util.log(IStatus.ERROR,
                                              ex,
                                              getString("WSDLReader.unexpected.parsing.wsdl")); //$NON-NLS-1$
            Exception e = new Exception(getString("WSDLReader.unexpected.parsing.wsdl")); //$NON-NLS-1$
            throw new ModelGenerationException(e);
        } catch (Exception ex) {
            throw new ModelGenerationException(ex);
        }
        return theModel;
    }

    private Model buildWSDLStructures() throws Exception {
        ModelBuilder builder = new ModelBuilder();
        builder.setAuthentication(securityType, userName, password);
        builder.setWSDL(wsdlURI.replace("%20", " ")); //$NON-NLS-1$//$NON-NLS-2$
        if (!builder.isWSDLParsed()) {
            Exception myEx = builder.getWSDLException();
            throw myEx;
        }
        return builder.getModel();
    }

    /**
     * @return the URI of the given WSDL
     */
    public String getWSDLUri() {
        return wsdlURI;
    }

    /**
     * Set the optional authentication credentials
     *
     * @param securityType
     * @param userName
     * @param password
     */
    public void setAuthenticationCredentials(SecurityType securityType, String userName, String password) {
		this.securityType = securityType;
		this.userName = userName;
		this.password = password;
    }

    /**
     * @param fileUri the URI of the WSDL file
     */
    public void setWSDLUri( String fileUri ) {
        wsdlURI = fileUri;
    }

    /**
     * @return true if the wsdl is valid, false otherwise
     */
	public MultiStatus validateWSDL(IProgressMonitor monitor) {
    	
		monitor.beginTask(
				getString("WSDLReader.validating.wsdl"), //$NON-NLS-1$
				IProgressMonitor.UNKNOWN);
		monitor.worked(1);
		if (VALIDATOR == null) {
			VALIDATOR = new WSDLValidator();
			VALIDATOR.addURIResolver(new URIResolverWrapper());
        }
		
		String wsdlUri = getWSDLUri();
		MultiStatus status;
		List<WSDLValidationMessage> messages = new ArrayList<WSDLValidationMessage>();
		URL remoteURL;
		URLConnection urlConn = null;
		InputStream inputStream = null;
		int code;
		
		try {
		    remoteURL = URLHelper.buildURL(wsdlUri);
		    monitor.worked(1);

            remoteURL = new URL(wsdlUri);
            urlConn = remoteURL.openConnection();

            if (securityType != null && ! SecurityType.None.equals(securityType)) {
                URLHelper.setCredentials(urlConn, userName, password);
            }

            inputStream = urlConn.getInputStream();
            IValidationReport report = VALIDATOR.validate(wsdlUri, inputStream, null);

            monitor.worked(1);
            boolean success = !report.hasErrors() && report.isWSDLValid();

            if (success) {
                code = 100;

                monitor.worked(1);
                monitor.done();
                status = buildStatus(code, null, getString("WSDLReader.validation.passed"), null); //$NON-NLS-1$
                return status;
            }

            // Something went wrong
            boolean warningsOnly = report.isWSDLValid();
            code = 500;

            // Log all of the report's validation messages
            IValidationMessage[] vmessages = report.getValidationMessages();
            for (int i = 0; i < vmessages.length; i++) {
                String message = buildValidationMessageString(vmessages[i]);
                int severity = vmessages[i].getSeverity();
                if (severity == IValidationMessage.SEV_ERROR) {
                    warningsOnly = false;
                }

                int newSeverity = (severity == IValidationMessage.SEV_ERROR ? IStatus.ERROR : IStatus.WARNING);
                messages.add(new WSDLValidationMessage(message, newSeverity));
            }

            if (warningsOnly) {
                monitor.worked(1);
                monitor.done();

                status = buildStatus(code, messages, getString("WSDLReader.validation.warning"), null); //$NON-NLS-1$
                return status;
            }

            // Errors occurred
            String contentType = urlConn.getContentType();
            if (messages.size() == 0) {
                // WSDL so invalid that the validator could not even read the file
                String invalidMsgProperty = "WSDLReader.wsdl.invalid"; //$NON-NLS-1$

                if ((null != contentType)
                        && (!contentType.contains("text/xml") || !contentType.contains("application/xml"))) { //$NON-NLS-1$ //$NON-NLS-2$
                    String messageSuffix = MessageFormat
                            .format(getString("WSDLReader.validation.content.type.error"), //$NON-NLS-1$
                                    wsdlUri, contentType);
                    String fullMessage = getString(invalidMsgProperty) + messageSuffix;
                    messages.add(new WSDLValidationMessage(fullMessage, IStatus.ERROR));

                    status = buildStatus(code, messages, fullMessage, new WSDLValidationException());

                } else {
                    messages.add(new WSDLValidationMessage(getString(invalidMsgProperty), IStatus.ERROR));
                    status = buildStatus(code, messages, getString(invalidMsgProperty), new WSDLValidationException());
                }
            } else {
                // Validation report returned errors
                String validationErrorProperty = "WSDLReader.validation.error"; //$NON-NLS-1$

                if (!remoteURL.getProtocol().equals("file") && !contentType.contains("text/xml") //$NON-NLS-1$//$NON-NLS-2$
                        || !contentType.equals("application/xml")) { //$NON-NLS-1$
                    String messageSuffix = MessageFormat
                            .format(getString("WSDLReader.validation.content.type.error"), //$NON-NLS-1$
                                    wsdlUri, contentType);

                    status = buildStatus(code, messages, getString(validationErrorProperty) + messageSuffix, new WSDLValidationException());		
                } else {
                    status = buildStatus(code, messages, getString(validationErrorProperty), new WSDLValidationException());
                }
            }
		} catch (Exception e) { 
		    code = 500;
		    String msgProperty = "WSDLReader.open.connection.error"; //$NON-NLS-1$
		    messages.add(new WSDLValidationMessage(getString(msgProperty), IStatus.ERROR));
		    status = buildStatus(code, messages, getString(msgProperty), e);

		} finally {
		    if (inputStream != null) {
		        try {
                    inputStream.close();
                } catch (IOException ex) {
                    code = 500;
                    String msgProperty = "WSDLReader.close.connection.error"; //$NON-NLS-1$
                    messages.add(new WSDLValidationMessage(getString(msgProperty), IStatus.ERROR));
                    status = buildStatus(code, messages, getString(msgProperty), ex);
                }
		    }
		}

		monitor.worked(1);
		monitor.done();
        return status;
    }
	
	private MultiStatus buildStatus(int code, List<? extends IStatus> childMessages, String message, Exception exception) {
	    return new MultiStatus(
            ModelGeneratorWsdlPlugin.PLUGIN_ID, code, 
            childMessages == null ? new IStatus[0] : childMessages.toArray(new IStatus[0]),
            message, exception);
	}
	
	private String getString(String property) {
	    return ModelGeneratorWsdlPlugin.Util.getString(property);
	}
	
	private String buildValidationMessageString(IValidationMessage vmessage) {
		StringBuffer buff = new StringBuffer();
		buff.append(vmessage.getLine());
		buff.append(":"); //$NON-NLS-1$
		buff.append(vmessage.getColumn());
		buff.append(" - "); //$NON-NLS-1$
		buff.append(vmessage.getMessage());
		return buff.toString();
	}
}
