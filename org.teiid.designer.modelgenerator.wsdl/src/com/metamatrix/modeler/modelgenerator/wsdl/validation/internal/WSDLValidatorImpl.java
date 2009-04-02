/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.wsdl.validation.internal;

import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.wst.wsdl.validation.internal.IValidationMessage;
import org.eclipse.wst.wsdl.validation.internal.IValidationReport;
import org.eclipse.wst.wsdl.validation.internal.WSDLValidator;
import com.metamatrix.core.PluginUtil;
import com.metamatrix.modeler.modelgenerator.wsdl.ModelGeneratorWsdlPlugin;
import com.metamatrix.modeler.modelgenerator.wsdl.validation.WSDLValidationException;

public class WSDLValidatorImpl implements com.metamatrix.modeler.modelgenerator.wsdl.validation.WSDLValidator {

    PluginUtil util = ModelGeneratorWsdlPlugin.Util;

    public MultiStatus validateWSDL( String fileUri ) {
        return validateWSDL(fileUri, null);
    }

    public MultiStatus validateWSDL( String fileUri,
                                     IProgressMonitor monitor ) {
        if (monitor != null) {
            monitor.beginTask("Validating WSDL", 100); //$NON-NLS-1$
        }
        MultiStatus status;
        WSDLValidator validator = new WSDLValidator();
        try {
			validator.addURIResolver(new NamespaceResolver());
		} catch (Exception e) {
			status = new MultiStatus(ModelGeneratorWsdlPlugin.PLUGIN_ID, 500, new IStatus[0],
                    util.getString("WSDLValidationImpl.validation.error"), e); //$NON-NLS-1$
            return status;
		}
        if (monitor != null) monitor.worked(20);
        IValidationReport report = validator.validate(fileUri);
        if (monitor != null) monitor.worked(60);
        boolean success = !report.hasErrors() && report.isWSDLValid();

        if (success) {
            final int code = 100;

            status = new MultiStatus(ModelGeneratorWsdlPlugin.PLUGIN_ID, code, new IStatus[0],
                                     util.getString("WSDLValidationImpl.validation.passed"), null); //$NON-NLS-1$
        } else {
            boolean warningsOnly = report.isWSDLValid();
            final int code = 500;
            IValidationMessage[] vmessages = report.getValidationMessages();
            IStatus[] messages = new WSDLValidationMessage[vmessages.length];
            for (int i = 0; i < vmessages.length; i++) {
                String message = buildValidationMessageString(vmessages[i]);
                int severity = vmessages[i].getSeverity();
                if (severity == IValidationMessage.SEV_ERROR) warningsOnly = false;
                int newSeverity = (severity == IValidationMessage.SEV_ERROR ? IStatus.ERROR : IStatus.WARNING);
                messages[i] = new WSDLValidationMessage(message, newSeverity);
            }
            if (warningsOnly) {
                status = new MultiStatus(ModelGeneratorWsdlPlugin.PLUGIN_ID, code, messages,
                                         util.getString("WSDLValidationImpl.validation.warning"), null); //$NON-NLS-1$
            } else {
                try {
                    URL url = new URL(fileUri);
                    URLConnection connection = url.openConnection();
                    String contentType = connection.getContentType();
                    if (messages.length == 0) {
                        if ((null != contentType) && (!contentType.contains("text/xml") || !contentType.contains("application/xml"))) { //$NON-NLS-1$ //$NON-NLS-2$
                            String messageSuffix = MessageFormat.format(util.getString("WSDLValidationImpl.validation.content.type.error"), //$NON-NLS-1$
                                                                        fileUri,
                                                                        contentType);
                            String fullMessage = util.getString("WSDLValidationImpl.wsdl.invalid") + messageSuffix; //$NON-NLS-1$
                            messages = new WSDLValidationMessage[] {new WSDLValidationMessage(fullMessage, IStatus.ERROR)};
                            status = new MultiStatus(ModelGeneratorWsdlPlugin.PLUGIN_ID, code, messages, fullMessage,
                                                     new WSDLValidationException());
                        } else {
                            messages = new WSDLValidationMessage[] {new WSDLValidationMessage(
                                                                                              util.getString("WSDLValidationImpl.wsdl.invalid"), IStatus.ERROR)}; //$NON-NLS-1$
                            status = new MultiStatus(
                                                     ModelGeneratorWsdlPlugin.PLUGIN_ID,
                                                     code,
                                                     messages,
                                                     util.getString("WSDLValidationImpl.wsdl.invalid"), new WSDLValidationException()); //$NON-NLS-1$
                        }
                    } else {
                        if (!url.getProtocol().equals("file") && !contentType.contains("text/xml") //$NON-NLS-1$//$NON-NLS-2$
                            || !contentType.equals("application/xml")) { //$NON-NLS-1$
                            String messageSuffix = MessageFormat.format(util.getString("WSDLValidationImpl.validation.content.type.error"), //$NON-NLS-1$
                                                                        fileUri,
                                                                        contentType);
                            status = new MultiStatus(
                                                     ModelGeneratorWsdlPlugin.PLUGIN_ID,
                                                     code,
                                                     messages,
                                                     util.getString("WSDLValidationImpl.validation.error") + messageSuffix, new WSDLValidationException()); //$NON-NLS-1$
                        } else {
                            status = new MultiStatus(
                                                     ModelGeneratorWsdlPlugin.PLUGIN_ID,
                                                     code,
                                                     messages,
                                                     util.getString("WSDLValidationImpl.validation.error"), new WSDLValidationException()); //$NON-NLS-1$
                        }
                    }
                } catch (Exception e) { // catches IOException which may happen and MalformedURLException which cannot happen
                    // because we've already checked.
                    if (messages.length == 0) {
                        messages = new WSDLValidationMessage[1];
                        messages[0] = new WSDLValidationMessage(util.getString("WSDLValidationImpl.open.connection.error"), //$NON-NLS-1$
                                                                IStatus.ERROR);
                    }
                    status = new MultiStatus(
                                             ModelGeneratorWsdlPlugin.PLUGIN_ID,
                                             code,
                                             messages,
                                             util.getString("WSDLValidationImpl.open.connection.error"), new WSDLValidationException()); //$NON-NLS-1$
                }
            }
        }
        if (monitor != null) monitor.done();
        return status;
    }

    private String buildValidationMessageString( IValidationMessage vmessage ) {
        StringBuffer buff = new StringBuffer();
        buff.append(vmessage.getLine());
        buff.append(":"); //$NON-NLS-1$
        buff.append(vmessage.getColumn());
        buff.append(" - "); //$NON-NLS-1$
        buff.append(vmessage.getMessage());
        return buff.toString();
    }

}
