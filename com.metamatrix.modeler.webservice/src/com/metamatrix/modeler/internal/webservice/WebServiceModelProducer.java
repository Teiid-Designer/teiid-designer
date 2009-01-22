/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.webservice;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.xerces.jaxp.DocumentBuilderFactoryImpl;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDSchema;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.webservice.WebServicePackage;
import com.metamatrix.metamodels.wsdl.Definitions;
import com.metamatrix.metamodels.wsdl.WsdlPackage;
import com.metamatrix.metamodels.wsdl.http.HttpPackage;
import com.metamatrix.metamodels.wsdl.mime.MimePackage;
import com.metamatrix.metamodels.wsdl.soap.SoapPackage;
import com.metamatrix.modeler.compare.ModelProducer;
import com.metamatrix.modeler.compare.selector.ModelSelector;
import com.metamatrix.modeler.compare.selector.TransientModelSelector;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.core.workspace.ModelWorkspaceManager;
import com.metamatrix.modeler.webservice.IWebServiceGenerator;
import com.metamatrix.modeler.webservice.IWebServiceModelBuilder;
import com.metamatrix.modeler.webservice.IWebServiceResource;
import com.metamatrix.modeler.webservice.IWebServiceXsdResource;
import com.metamatrix.modeler.webservice.WebServicePlugin;

/**
 * @since 4.2
 */
public class WebServiceModelProducer implements ModelProducer {

    public static final int WARNING_NO_WSDL_OBJECTS = 31301;

    private final IWebServiceModelBuilder builder;
    private final TransientModelSelector output;
    private final List roots;

    /**
     * @param matcherFactories
     * @since 4.2
     */
    public WebServiceModelProducer( final IWebServiceModelBuilder builder ) {
        this.builder = builder;
        this.roots = new ArrayList();

        // Create a temporary model selector with the same URI as the actual relational model
        // (the temporary will be placed in a separate "temporary" resource set, so same URI can be used)
        this.output = new TransientModelSelector(builder.getModelPath().toString());
    }

    /**
     * @see com.metamatrix.modeler.compare.ModelProducer#execute(org.eclipse.core.runtime.IProgressMonitor, java.util.List)
     * @since 4.2
     */
    public void execute( IProgressMonitor monitor,
                         List problems ) throws Exception {
        this.roots.clear();

        // Create the ModelAnnotation ...
        final ModelAnnotation modelAnnotation = this.output.getModelAnnotation();
        if (modelAnnotation.getPrimaryMetamodelUri() != null
            && !modelAnnotation.getPrimaryMetamodelUri().equals(WebServicePackage.eNS_URI)) {
            modelAnnotation.setPrimaryMetamodelUri(WebServicePackage.eNS_URI);
        }
        if (modelAnnotation.getModelType() != null && modelAnnotation.getModelType() != ModelType.VIRTUAL_LITERAL) {
            modelAnnotation.setModelType(ModelType.VIRTUAL_LITERAL);
        }

        // Create the generator ...
        final IWebServiceGenerator generator = WebServicePlugin.createWebServiceGenerator();
        generator.setWebServiceResource(this.output.getResource());

        // 
        generator.setSelectedOperations(builder.getSelectedOperations());

        // Make sure that all XSD are in the workspace and are addressable ...
        final Set allXsdSchemas = doProcessXsds(monitor, problems);

        // Go through the WSDL models and obtain all root-level WSDL objects ...
        final Collection resources = this.builder.getWSDLResources();
        final Iterator iter = resources.iterator();
        while (iter.hasNext()) {
            final IWebServiceResource wsr = (IWebServiceResource)iter.next();

            // Load the WSD ...
            final Resource emfResource = this.builder.getEmfResource(wsr);
            if (emfResource != null) {
                final List roots = emfResource.getContents();
                final Iterator rootIter = roots.iterator();
                while (rootIter.hasNext()) {
                    final EObject root = (EObject)rootIter.next();
                    if (root instanceof Definitions) {
                        generator.addWsdlDefinitions((Definitions)root);
                    }
                }
            }

        }

        final Iterator xsdIter = allXsdSchemas.iterator();
        while (xsdIter.hasNext()) {
            final XSDSchema schema = (XSDSchema)xsdIter.next();
            generator.addXsdSchema(schema);
        }

        if (generator.getWsdlDefinitions().isEmpty()) {
            // No objects were found ...
            final String msg = WebServicePlugin.Util.getString("WebServiceModelProducer.NoWsdlObjectsFound"); //$NON-NLS-1$
            problems.add(new Status(IStatus.WARNING, WebServicePlugin.PLUGIN_ID, WARNING_NO_WSDL_OBJECTS, msg, null));
        } else {
            // At least some objects were found ...
            generator.generate(monitor, problems);
        }
    }

    /**
     * @see com.metamatrix.modeler.compare.ModelProducer#getOutputSelector()
     * @since 4.2
     */
    public ModelSelector getOutputSelector() {
        return this.output;
    }

    /**
     * This method is called by {@link #execute(IProgressMonitor, List)} to process the {@link IWebServiceResource} instances and
     * make sure that all XSDs are located in the workspace. This includes:
     * <ul>
     * <li>copying into the workspace all XSDs that are outside of the workspace</li>
     * <li>creating XSD files in the workspace for all XSD definitions within the WSDL files</li>
     * </ul>
     * <p>
     * Note that when doing so, it may be required to add to the <code>schemaLocation</code> within the XSDs.
     * </p>
     * 
     * @param monitor
     * @param problems
     * @return the set of {@link XSDSchema} instances that should be used by the generator
     * @since 4.2
     */
    protected Set doProcessXsds( final IProgressMonitor monitor,
                                 final List problems ) {
        // Create the set that will be returned ...
        final Set results = new HashSet();

        // Copy the schemas that are to be copied ...
        final Collection xsdDestinations = this.builder.getXsdDestinations();
        final Iterator iter = xsdDestinations.iterator();
        Set newXsdFiles = new HashSet();
        Set destProjectRelativePaths = getDestProjectRelativePaths(xsdDestinations);
        while (iter.hasNext()) {
            final IWebServiceXsdResource xsdDest = (IWebServiceXsdResource)iter.next();
            if (xsdDest == null || xsdDest.getDestinationPath() == null) {
                continue;
            }
            final IPath destFilePath = xsdDest.getDestinationPath();

            /*
             * First we remove the 'project' part of the Path from the dest path all destinations include the project part of the
             * path for validation and display purposes.
             */
            final IPath destProjectRelativePath = destFilePath.removeFirstSegments(1);

            /*
             * we also remove the filename from this path as we are only interested in creating the path TO the file, not the file
             * itself.
             */
            final IPath destPath = destProjectRelativePath.removeLastSegments(1);

            /*
             * we must now add the 'absolute' part of the path to the destPath. The destPath in the XSD destinations is relative
             * to the project in the workspace. We need to add the absolute path component for the project location in order to be
             * able to physically create the XSD's in the proper location.
             */
            final IPath absoluteProjectPath = builder.getParentResource().getLocation();

            final IPath absoluteDestPath = absoluteProjectPath.append(destPath);

            /*
             * This call will ensure that the destination path for the XSD actually exists on the physical drive. If it does not
             * exist, it will create the path so that the resrource can be saved in the proper relative location.
             */

            final File pathFile = new File(absoluteDestPath.toOSString());

            pathFile.mkdirs();

            try {
                /*
                 * now we must refresh the project with the local file system so that the internal representations of the
                 * resources in the project can be reflecting the physical structure on the file system that we just created (the
                 * folders).
                 */
                builder.getParentResource().refreshLocal(IResource.DEPTH_INFINITE, null);
            } catch (CoreException err1) {
                // record a problem ...
                final Object[] params = new Object[] {builder.getParentResource().getName(), err1.getMessage()};
                final String msg = WebServicePlugin.Util.getString("WebServiceModelProducer.ErrorRefreshingWorkspace", params); //$NON-NLS-1$
                final IStatus status = new Status(IStatus.ERROR, WebServicePlugin.PLUGIN_ID, 0, msg, null);
                problems.add(status);
            }

            final XSDSchema schema = xsdDest.getSchema();

            // Get the destination ModelResource ...
            final IFile file = ((IContainer)builder.getParentResource()).getFile(destProjectRelativePath);

            if (file == null) {
                // record a problem ...
                final Object[] params = new Object[] {xsdDest.getOriginalPath(), destProjectRelativePath};
                final String msg = WebServicePlugin.Util.getString("WebServiceModelProducer.UnableToCopyXsdFrom0To1", params); //$NON-NLS-1$
                final IStatus status = new Status(IStatus.ERROR, WebServicePlugin.PLUGIN_ID, 0, msg, null);
                problems.add(status);
            } else {
                File tempFile = null;
                try {
                    // Get the DOM document for the schema ...
                    // Comment out following line for defect 17308 Fix
                    // schema.updateElement(true); // update deeply

                    OutputStream stream = null;
                    InputStream inputStream = null;
                    try {

                        // set import location as relative path
                        Iterator iter1 = schema.getContents().iterator();
                        while (iter1.hasNext()) {
                            Object content = iter1.next();
                            if (content instanceof XSDImport) {
                                XSDImport xsdImport = ((XSDImport)content);
                                String schemaLocation = xsdImport.getSchemaLocation();
                                IPath importLocation = null;
                                if (schemaLocation != null) {
                                    importLocation = new Path(URI.createURI(xsdImport.getSchemaLocation()).path()).makeRelative();
                                } else {
                                    // This is a namespace import.
                                    continue;
                                }

                                if (destProjectRelativePaths.contains(importLocation)) {
                                    xsdImport.setSchemaLocation(ModelUtil.getRelativePath(importLocation, destProjectRelativePath));
                                }
                            }
                        }

                        // Write the document to a temp file ...
                        tempFile = File.createTempFile("XSDCopy", ".xsd"); //$NON-NLS-1$ //$NON-NLS-2$
                        stream = new FileOutputStream(tempFile);
                        stream = new BufferedOutputStream(stream);

                        final Element schemaElement = schema.getElement();
                        doWriteXmlDocument(xsdDest.getTargetNamespace(), stream, schemaElement);

                        stream.close();
                        stream = null;

                        // Read the temporary file ...
                        inputStream = new FileInputStream(tempFile);
                        inputStream = new BufferedInputStream(inputStream);
                        if (file.exists()) {
                            file.setContents(inputStream, true, true, monitor);
                        } else {
                            file.create(inputStream, true, monitor);
                        }

                        newXsdFiles.add(file);

                    } finally {
                        if (stream != null) {
                            try {
                                stream.close();
                            } catch (IOException err) {
                                WebServicePlugin.Util.log(err);
                            }
                        }
                        if (inputStream != null) {
                            try {
                                inputStream.close();
                            } catch (IOException err) {
                                WebServicePlugin.Util.log(err);
                            }
                        }
                        if (tempFile != null) {
                            // RMH: this seems to work fine
                            try {
                                tempFile.delete();
                            } catch (RuntimeException err) {
                                WebServicePlugin.Util.log(err);
                            }
                        }
                    }

                    // Obtain the underlying file and write out the DOM document ...

                } catch (Exception e) {
                    final Object[] params = new Object[] {xsdDest.getOriginalPath(), destProjectRelativePath,
                        e.getLocalizedMessage()};
                    final String msg = WebServicePlugin.Util.getString("WebServiceModelProducer.ErrorCopyingXsdFrom0To1", params); //$NON-NLS-1$
                    final IStatus status = new Status(IStatus.ERROR, WebServicePlugin.PLUGIN_ID, 0, msg, e);
                    problems.add(status);
                }

            }
        }

        Iterator iter1 = newXsdFiles.iterator();
        while (iter1.hasNext()) {
            IFile xsdFile = (IFile)iter1.next();
            try {
                // Read int the new XSDSchema and register it ...
                final ModelResource modelResource = (ModelResource)ModelWorkspaceManager.getModelWorkspaceManager().findModelWorkspaceItem(xsdFile,
                                                                                                                                           true);
                final List allRoots = modelResource.getAllRootEObjects();
                final Iterator rootIter = allRoots.iterator();
                while (rootIter.hasNext()) {
                    final EObject root = (EObject)rootIter.next();
                    if (root instanceof XSDSchema) {
                        results.add(root);
                    }
                }

            } catch (Exception e) {
                final String msg = WebServicePlugin.Util.getString("WebServiceModelProducer.ErrorLoadingXsd1", xsdFile); //$NON-NLS-1$
                final IStatus status = new Status(IStatus.ERROR, WebServicePlugin.PLUGIN_ID, 0, msg, e);
                problems.add(status);
            }
        }
        return results;
    }

    private Set getDestProjectRelativePaths( Collection xsdDestinations ) {
        Set destProjectRelativePaths = new HashSet();
        Iterator iter = xsdDestinations.iterator();
        while (iter.hasNext()) {
            IWebServiceXsdResource xsdDest = (IWebServiceXsdResource)iter.next();
            destProjectRelativePaths.add(xsdDest.getDestinationPath().removeFirstSegments(1));
        }
        return destProjectRelativePaths;
    }

    /**
     * @param stream the stream to which the document should be written
     * @param schemaElement the "xsd:schema" element, which may or may not be the root of the document
     * @throws Exception
     * @since 4.2
     */
    protected void doWriteXmlDocument( final String targetNamespace,
                                       final OutputStream stream,
                                       final Element schemaElement ) throws Exception {
        // Create a copy ...
        // NOTE: THIS WORKS IF WE USE THE org.apache.xerces LIBRARY.
        // The problem is that these other implementations don't write out the
        // namespace declarations for the imported nodes
        DocumentBuilderFactory documentBuilderFactory = new DocumentBuilderFactoryImpl();
        documentBuilderFactory.setNamespaceAware(true);
        documentBuilderFactory.setValidating(false);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        final Document docCopy = documentBuilder.newDocument();
        final Element schemaElementCopy = (Element)docCopy.importNode(schemaElement, true);
        docCopy.appendChild(schemaElementCopy);

        // Namespace declarations that are only needed by VALUES of attributes or element text
        // will not be automatically added to the document. Therefore, some of these have to
        // be copied explicitly.
        final NamedNodeMap nameMap = schemaElement.getOwnerDocument().getDocumentElement().getAttributes();
        for (int i = 0; i < nameMap.getLength(); i++) {
            final Node child = nameMap.item(i);
            final String value = child.getNodeValue();
            // final String name = child.getLocalName();
            final String prefix = child.getPrefix();
            // System.out.println(" attribute " + (prefix != null ? (prefix + ":") : "" )+ name + "=" + value );
            if ("xmlns".equals(prefix)) { //$NON-NLS-1$
                if (WsdlPackage.eNS_URI.equals(value) || SoapPackage.eNS_URI.equals(value) || HttpPackage.eNS_URI.equals(value)
                    || MimePackage.eNS_URI.equals(value)) {
                    // skip these namespace declarations; assume WSDL and it's associated namespaces aren't used in VALUES
                } else {
                    // Make a copy of this namespace declaration and add to the schema ...
                    final Node newChild = docCopy.importNode(child, false);
                    schemaElementCopy.setAttributeNode((Attr)newChild);
                    // final Attr attr = docCopy.createAttribute(prefix + ':' + name);
                    // attr.setValue(value);
                    // schemaElementCopy.setAttributeNode(attr);
                }
            }
        }

        // Write it out ...
        DOMImplementationLS impl = (DOMImplementationLS)DOMImplementationRegistry.newInstance().getDOMImplementation("LS"); //$NON-NLS-1$
        LSSerializer writer = impl.createLSSerializer();
        LSOutput output = impl.createLSOutput();
        output.setByteStream(stream);
        output.setEncoding("UTF-8"); //$NON-NLS-1$
        writer.write(docCopy, output);
    }
}
