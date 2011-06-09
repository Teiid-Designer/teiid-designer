/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.dqp.webservice.war.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Properties;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDParser;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.WorkspaceResourceFinderUtil;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;
import com.metamatrix.modeler.internal.webservice.gen.BasicWsdlGenerator;

/**
 * 
 */
public class WarArchiveUtil {

    public static String TARGETNS = "targetNs"; //$NON-NLS-1$
    public static String WEBSERVICENAME = "webserviceName"; //$NON-NLS-1$
    public static String URLROOT = "urlRoot"; //$NON-NLS-1$
    public static String SERVICEURL = "serviceUrl"; //$NON-NLS-1$
    public static String WSDLFILE_EXT = "wsdl"; //$NON-NLS-1$

    public XSDSchema importSchema( String path ) {
        XSDParser parser = new XSDParser(null);
        parser.parse(path);
        XSDSchema schema = parser.getSchema();
        schema.setSchemaLocation(path);
        return schema;
    }

    public static ArrayList<String> getPathParameters( String uri ) {
        ArrayList pathParams = new ArrayList();
        String param;
        if (uri.contains("{")) { //$NON-NLS-1$
            while (uri.indexOf("}") > -1) { //$NON-NLS-1$
                int start = uri.indexOf("{"); //$NON-NLS-1$
                int end = uri.indexOf("}"); //$NON-NLS-1$
                param = uri.substring(start + 1, end);
                uri = uri.substring(end + 1);
                pathParams.add(param);
            }
        }
        return pathParams;
    }

    /**
     * Generate a WSDL file using passed in WS Model Resources and user supplied values
     * 
     * @since 7.1
     */
    public void generateWSDL( ArrayList<ModelResource> wsModelResourceList,
                              Properties userSuppliedValues ) {

        BasicWsdlGenerator wsdlGenerator = new BasicWsdlGenerator();
        ModelResource wsModel = null;
        // This will be overwritten by the web service model name
        String webServiceName = userSuppliedValues.getProperty(WEBSERVICENAME, "TeiidWS"); //$NON-NLS-1$
        for (ModelResource webServiceModel : wsModelResourceList) {
            try {
                wsModel = webServiceModel;
                wsdlGenerator.addWebServiceModel(webServiceModel.getEmfResource());
                webServiceName = webServiceModel.getItemName();
                IResource[] iResources = WorkspaceResourceFinderUtil.getDependentResources(webServiceModel.getResource());
                for (IResource iResource : iResources) {
                    if (ModelIdentifier.isSchemaModel(iResource)) {
                        wsdlGenerator.addXsdModel(importSchema(iResource.getLocation().toOSString()), iResource.getLocation());
                    }
                }
            } catch (ModelWorkspaceException e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        // TODO: Create wizard to override these default values as part of the soap war generator in 7.1
        webServiceName = webServiceName.substring(0, webServiceName.lastIndexOf(".")); //$NON-NLS-1$
        wsdlGenerator.setName(webServiceName);
        wsdlGenerator.setTargetNamespace(userSuppliedValues.getProperty(TARGETNS, "http://teiid.org")); //$NON-NLS-1$
        wsdlGenerator.setUrlRootForReferences(userSuppliedValues.getProperty(URLROOT, "")); //$NON-NLS-1$
        wsdlGenerator.setUrlSuffixForReferences(""); //$NON-NLS-1$
        wsdlGenerator.setUrlForWsdlService(userSuppliedValues.getProperty(SERVICEURL, "http://serverName:port/warName/")); //$NON-NLS-1$
        final IStatus status = wsdlGenerator.generate(new NullProgressMonitor());

        // nothing more to do if an error is expected
        if (status.getSeverity() == IStatus.ERROR) {
            throw new RuntimeException("Unable to generate WSDL"); //$NON-NLS-1$
        }

        String fileName = webServiceName + "." + WSDLFILE_EXT; //$NON-NLS-1$
        try {
            // Create our WSDL file and write to it
            String path = wsModel.getResource().getLocation().toOSString();
            OutputStream stream = new FileOutputStream(new File(path.substring(0, path.lastIndexOf("/")), fileName)); //$NON-NLS-1$
            wsdlGenerator.write(stream);
            // Get an iFile instance to refresh our workspace
            IFile iFile = wsModel.getModelProject().getProject().getFile(fileName);
            iFile.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());

        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        } catch (CoreException e) {
            throw new RuntimeException(e.getMessage());
        }

    }

}
