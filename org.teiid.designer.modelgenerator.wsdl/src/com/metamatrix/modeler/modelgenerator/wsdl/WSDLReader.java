/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.wsdl;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import com.metamatrix.core.log.Logger;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Model;
import com.metamatrix.modeler.modelgenerator.wsdl.model.ModelGenerationException;
import com.metamatrix.modeler.modelgenerator.wsdl.model.internal.ModelBuilder;
import com.metamatrix.modeler.modelgenerator.wsdl.validation.WSDLValidator;
import com.metamatrix.modeler.modelgenerator.wsdl.validation.internal.WSDLValidatorImpl;

/**
 * This class is responsible for reading WSDL files from a URI or filesystem validating them and producing an OO representation of
 * the WSDL contents
 * 
 * @author JChoate
 */
public class WSDLReader {

    private String m_fileURI;
    private Logger logger;
    private static WSDLValidator VALIDATOR;

    public static final int VALIDATION_SEVERITY_ERROR = 0;
    public static final int VALIDATION_SEVERITY_WARNING = 1;

    public WSDLReader( Logger logger ) {
        this(null, logger);
    }

    /**
     * @param fileUri the URI of the WSDL file
     */
    public WSDLReader( String fileUri,
                       Logger logger ) {
        m_fileURI = fileUri;
        this.logger = logger;
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
            logger.log(IStatus.ERROR, ex, ModelGeneratorWsdlPlugin.Util.getString("WSDLReader.unexpected.parsing.wsdl")); //$NON-NLS-1$
            Exception e = new Exception(ModelGeneratorWsdlPlugin.Util.getString("WSDLReader.unexpected.parsing.wsdl")); //$NON-NLS-1$
            throw new ModelGenerationException(e);
        } catch (Exception ex) {
            throw new ModelGenerationException(ex);
        }
        return theModel;
    }

    private Model buildWSDLStructures() throws Exception {
        ModelBuilder builder = new ModelBuilder();
        builder.setWSDL(m_fileURI);
        if (!builder.isWSDLParsed()) {
            Exception myEx = builder.getWSDLException();
            throw myEx;
        }
        return builder.getModel(logger);
    }

    /**
     * @return the URI of the given WSDL
     */
    public String getFileUri() {
        return m_fileURI;
    }

    /**
     * @param fileUri the URI of the WSDL file
     */
    public void setFileUri( String fileUri ) {
        m_fileURI = fileUri;
    }

    /**
     * @return true if the wsdl is valid, false otherwise
     */
    public MultiStatus validateWSDL( IProgressMonitor monitor ) {

        // lazy
        if (VALIDATOR == null) {
            VALIDATOR = new WSDLValidatorImpl();
        }
        MultiStatus success = VALIDATOR.validateWSDL(getFileUri(), monitor);
        return success;

        /*
        // TEMP - RETURN MULTISTATUS WITH OK SEVERITY
        final int embeddedSeverity1 = IStatus.OK;
        final int code = 100;
        final String embeddedMessage1 = "This is the embedded warning message 1"; //$NON-NLS-1$
        final String message = "This is the message for the outter multistatus"; //$NON-NLS-1$
        final Throwable t = new Throwable("This is the throwable"); //$NON-NLS-1$
        t.fillInStackTrace();
        final String pluginID = ModelGeneratorWsdlPlugin.PLUGIN_ID;
        final IStatus embeddedStatus1 = new Status(embeddedSeverity1,pluginID,code+1,embeddedMessage1,t);
        final IStatus[] embedded = new IStatus[]{embeddedStatus1};
        return new MultiStatus(pluginID,code,embedded,message,t);
        */
    }
}
