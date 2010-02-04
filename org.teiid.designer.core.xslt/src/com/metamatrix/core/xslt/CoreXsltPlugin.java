/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core.xslt;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ResourceBundle;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamSource;
import org.eclipse.core.runtime.Plugin;
import org.jdom.Document;
import org.jdom.output.XMLOutputter;
import org.osgi.framework.BundleContext;
import com.metamatrix.core.MetaMatrixCoreException;
import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.core.util.PluginUtilImpl;

/**
 * CoreXsltPlugin
 */
public class CoreXsltPlugin extends Plugin {

    public static final String PLUGIN_ID = "org.teiid.designer.core.xslt" ; //$NON-NLS-1$
    
    public static final String PACKAGE_ID = CoreXsltPlugin.class.getPackage().getName();
    
    /**
     * Provides access to the plugin's log and to it's resources.
     */
	private static final String I18N_NAME = PACKAGE_ID + ".i18n"; //$NON-NLS-1$
    public static PluginUtil Util = new PluginUtilImpl(PLUGIN_ID, I18N_NAME, ResourceBundle.getBundle(I18N_NAME));

    public CoreXsltPlugin() {
    }
    
    /**
	 * <p>
	 * {@inheritDoc}
	 * </p>
	 *
	 * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
	 */
    @Override
	public void start( BundleContext context ) throws Exception {
		super.start(context);
        ((PluginUtilImpl)Util).initializePlatformLogger(this);   // This must be called to initialize the platform logger!
    }

    /**
     * Create an instance of TransformerFactory.
     * @return a new TransformerFactory
     * @throws TransformerFactoryConfigurationError if there is a problem configuring the factory
     */
    public static TransformerFactory createFactory() throws TransformerFactoryConfigurationError {
//        final TransformerFactory factory = TransformerFactory.newInstance();
//        final TransformerFactory factory = new oracle.xml.jaxp.JXSAXTransformerFactory();
//        final TransformerFactory factory = new org.apache.xalan.processor.TransformerFactoryImpl();
        final TransformerFactory factory = new net.sf.saxon.TransformerFactoryImpl();
        //factory.setAttribute(FeatureKeys.RECOVERY_POLICY, new Integer(Controller.RECOVER_SILENTLY));
        return factory;
    }

    /**
     * Transform the supplied source document (or fragment of the source document, if the URI of
     * a fragment root is specified) using the XSLT.
     * @param sourceDoc the source document; may not be null
     * as the root of the fragment to be transformed; may be null if the whole document is to be transformed.
     * @param output the stream to which the transformed
     */
    public static Source createSource(final Document sourceDoc) throws MetaMatrixCoreException {
        ArgCheck.isNotNull(sourceDoc);

        /*
        * Here we convert the JDOM Document to an outputStream and feed it
        * to the XSLT processor. it is possible to conver the JDOM Document
        * to a DOM document, but using streams is just as fast when using
        * a Xalan processor.  This is due to the processing required to
        * convert a DOM Document into the proper internal representation
        * that is required by the Xalan processor (Our default XSLT processor).
        */

        // Read in the document and convert to something that can be used as a StreamSource
        try {

            // Get a means for output of the JDOM Document
            final XMLOutputter xmlOutputter = new XMLOutputter();

            // Output to the input stream
            final StringWriter sourceOut = new StringWriter();
            xmlOutputter.output(sourceDoc, sourceOut);
            StringReader transformSource = new StringReader(sourceOut.toString());
            // Create the source ...
            return new StreamSource(transformSource);
        } catch ( Throwable e ) {
            final String msg = CoreXsltPlugin.Util.getString("CoreXsltPlugin.Error_loading_the_XSLT_transform"); //$NON-NLS-1$
            throw new MetaMatrixCoreException(e,msg);
        }
    }

}
