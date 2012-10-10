/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.webservice.gen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xsd.XSDComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDNamedComponent;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.compare.selector.ModelSelector;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.resource.EmfResource;
import org.teiid.designer.core.util.ModelContents;
import org.teiid.designer.core.util.ModelResourceContainerFactory;
import org.teiid.designer.core.util.ModelVisitor;
import org.teiid.designer.core.util.ModelVisitorProcessor;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.mapping.factory.ITreeToRelationalMapper;
import org.teiid.designer.mapping.factory.MappingClassBuilderStrategy;
import org.teiid.designer.mapping.factory.MappingClassFactory;
import org.teiid.designer.mapping.factory.ModelMapperFactory;
import org.teiid.designer.metamodels.transformation.SqlTransformation;
import org.teiid.designer.metamodels.transformation.SqlTransformationMappingRoot;
import org.teiid.designer.metamodels.transformation.TransformationContainer;
import org.teiid.designer.metamodels.transformation.TransformationFactory;
import org.teiid.designer.metamodels.webservice.Interface;
import org.teiid.designer.metamodels.webservice.Operation;
import org.teiid.designer.metamodels.webservice.Output;
import org.teiid.designer.metamodels.webservice.SampleMessages;
import org.teiid.designer.metamodels.webservice.WebServiceComponent;
import org.teiid.designer.metamodels.webservice.WebServiceFactory;
import org.teiid.designer.metamodels.xml.XmlDocument;
import org.teiid.designer.metamodels.xml.XmlDocumentBuilderImpl;
import org.teiid.designer.metamodels.xml.XmlDocumentFactory;
import org.teiid.designer.metamodels.xml.XmlRoot;
import org.teiid.designer.transformation.util.TransformationHelper;
import org.teiid.designer.webservice.IWebServiceXmlDocumentGenerator;
import org.teiid.designer.webservice.WebServicePlugin;
import org.teiid.designer.webservice.util.WebServiceUtil;


/**
 * @since 8.0
 */
public class BasicWebServiceXmlDocumentGenerator implements IWebServiceXmlDocumentGenerator {

    private Resource xmlDocResource;
    private final List webServiceComponents;
    private boolean reuseExistingDocuments;
    private ModelSelector webServiceModelSelector;

    /**
     * @since 4.2
     */
    public BasicWebServiceXmlDocumentGenerator() {
        super();
        this.webServiceComponents = new ArrayList();
    }

    /**
     * @see org.teiid.designer.webservice.IWebServiceXmlDocumentGenerator#addWebServiceComponent(org.teiid.designer.metamodels.webservice.WebServiceComponent)
     * @since 4.2
     */
    @Override
	public void addWebServiceComponent( WebServiceComponent webServiceComponent ) {
        CoreArgCheck.isNotNull(webServiceComponent);
        if (!this.webServiceComponents.contains(webServiceComponent)) {
            this.webServiceComponents.add(webServiceComponent);
        }
    }

    /**
     * @see org.teiid.designer.webservice.IWebServiceXmlDocumentGenerator#addWebServiceComponents(java.util.List)
     * @since 4.2
     */
    @Override
	public void addWebServiceComponents( List webServiceComponents ) {
        CoreArgCheck.isNotNull(webServiceComponents);
        final Iterator iter = webServiceComponents.iterator();
        while (iter.hasNext()) {
            final WebServiceComponent webServiceComponent = (WebServiceComponent)iter.next();
            if (webServiceComponent != null && !this.webServiceComponents.contains(webServiceComponent)) {
                this.webServiceComponents.add(webServiceComponent);
            }
        }
    }

    /**
     * @see org.teiid.designer.webservice.IWebServiceXmlDocumentGenerator#getWebServiceModelSelector()
     * @since 4.2
     */
    @Override
	public ModelSelector getWebServiceModelSelector() {
        return this.webServiceModelSelector;
    }

    /**
     * @see org.teiid.designer.webservice.IWebServiceXmlDocumentGenerator#setWebServiceModelSelector(org.teiid.designer.compare.selector.ModelSelector)
     * @since 4.2
     */
    @Override
	public void setWebServiceModelSelector( ModelSelector wsModelSelector ) {
        this.webServiceModelSelector = wsModelSelector;
    }

    /**
     * @see org.teiid.designer.webservice.IWebServiceXmlDocumentGenerator#getWebServiceComponents()
     * @since 4.2
     */
    @Override
	public List getWebServiceComponents() {
        return this.webServiceComponents;
    }

    /**
     * @see org.teiid.designer.webservice.IWebServiceXmlDocumentGenerator#setXmlDocumentResource(org.eclipse.emf.ecore.resource.Resource)
     * @since 4.2
     */
    @Override
	public void setXmlDocumentResource( Resource wsModel ) {
        CoreArgCheck.isNotNull(wsModel);
        this.xmlDocResource = wsModel;
    }

    /**
     * @see org.teiid.designer.webservice.IWebServiceXmlDocumentGenerator#getXmlDocumentResource()
     * @since 4.2
     */
    @Override
	public Resource getXmlDocumentResource() {
        return this.xmlDocResource;
    }

    /**
     * @see org.teiid.designer.webservice.IWebServiceXmlDocumentGenerator#generate(org.eclipse.core.runtime.IProgressMonitor)
     * @since 4.2
     */
    @Override
	public IStatus generate( IProgressMonitor monitor ) {
        final List problems = new ArrayList();
        if (this.getXmlDocumentResource() == null) {
            final String msg = WebServicePlugin.Util.getString("BasicWebServiceXmlDocumentGenerator.NoXmlDocumentModelSpecified"); //$NON-NLS-1$
            problems.add(new Status(IStatus.ERROR, WebServicePlugin.PLUGIN_ID, 0, msg, null));
        } else {
            doGenerate(monitor, problems);
        }

        // Convert the problems to a status
        IStatus result = null;
        if (problems.size() == 0) {
            final String msg = WebServicePlugin.Util.getString("BasicWebServiceXmlDocumentGenerator.GenerationOfXmlDocumentsSucceeded"); //$NON-NLS-1$
            result = new Status(IStatus.OK, WebServicePlugin.PLUGIN_ID, 0, msg, null);
        } else if (problems.size() == 1) {
            result = (IStatus)problems.get(0);
        } else {
            // Iterate over the problems and find the number of warnings and errors ...
            int errors = 0;
            int warnings = 0;
            final Iterator iter = problems.iterator();
            while (iter.hasNext()) {
                final IStatus status = (IStatus)iter.next();
                switch (status.getSeverity()) {
                    case IStatus.ERROR: {
                        ++errors;
                        break;
                    }
                    case IStatus.WARNING: {
                        ++warnings;
                        break;
                    }
                }
            }

            final Object[] params = new Object[] {new Integer(errors), new Integer(warnings)};
            final String msg = WebServicePlugin.Util.getString("BasicWebServiceXmlDocumentGenerator.GeneratedXmlDocumentWithErrorsAndWarnings", params); //$NON-NLS-1$
            final IStatus[] children = (IStatus[])problems.toArray(new IStatus[problems.size()]);
            result = new MultiStatus(WebServicePlugin.PLUGIN_ID, 0, children, msg, null);
        }

        return result;
    }

    /**
     * @see org.teiid.designer.webservice.IWebServiceXmlDocumentGenerator#generate(org.eclipse.core.runtime.IProgressMonitor,
     *      java.util.List)
     * @since 4.2
     */
    @Override
	public void generate( IProgressMonitor monitor,
                          List problems ) {
        doGenerate(monitor, problems);
    }

    // ========================================================================================================
    // Overridable methods
    // ========================================================================================================

    protected void doGenerate( IProgressMonitor monitor,
                               final List problems ) {
        // Create the correct visitor for the desired version ...
        final List outputs = new ArrayList();
        final WebServiceVisitor visitor = new WebServiceVisitor(outputs);

        // Walk the objects and find all of the Output objects ...
        final ModelVisitorProcessor processor = new ModelVisitorProcessor(visitor);
        try {
            processor.walk(this.webServiceComponents, ModelVisitorProcessor.DEPTH_INFINITE);
        } catch (ModelerCoreException e) {
            // handle the exception
        }

        ModelContents wsContents = null;
        try {
            wsContents = this.getWebServiceModelSelector().getModelContents();
        } catch (ModelWorkspaceException err) {
            String message = WebServicePlugin.Util.getString("BasicWebServiceXmlDocumentGenerator.UnableToGetWebServiceContents"); //$NON-NLS-1$
            final IStatus status = new Status(IStatus.ERROR, WebServicePlugin.PLUGIN_ID, 0, message, err);
            problems.add(status);
        }

        // Now, iterate over each of the Output objects and generate an XmlDocument ...
        if (wsContents != null && outputs.size() != 0) {
            final List docs = new ArrayList();
            final ModelContents xmlContents = doGetModelContents();
            final Iterator iter = outputs.iterator();
            // EObject obj = null;
            while (iter.hasNext()) {
                final Output output = (Output)iter.next();
                final XmlDocument xmlDoc = doGenerateXmlDocument(output, monitor, problems, docs, xmlContents);
                if (xmlDoc != null) {
                    doGenerateTransformation(output, xmlDoc, monitor, problems, wsContents);
                }
                // Check for cancellation ...
                if (monitor.isCanceled()) {
                    return;
                }
                // obj = output;
            }
            // if ( obj != null ) {
            // try {
            // obj.eResource().save(System.out, new HashMap());
            // } catch (IOException err1) {
            // err1.printStackTrace();
            // }
            // }
        }
    }

    protected String computeXmlDocumentName( Output output ) {
        StringBuffer sb = new StringBuffer();
        sb.append(output.getOperation().getInterface().getName());
        sb.append('_');
        sb.append(output.getOperation().getName());
        sb.append('_');
        sb.append(output.getName());
        return sb.toString();
    }

    protected String getFullName( EObject output ) {
        return ModelerCore.getModelEditor().getModelRelativePathIncludingModel(output).toString();
    }

    protected XmlDocument doGenerateXmlDocument( Output output,
                                                 IProgressMonitor monitor,
                                                 List problems,
                                                 List newDocs,
                                                 ModelContents contents ) {
        // Check for cancellation ...
        if (monitor.isCanceled()) {
            return null;
        }
        // Obtain the XSD type from the output ...
        final XSDElementDeclaration elementDecl = output.getContentElement();
        if (elementDecl == null) {
            return null; // can't generate at all
        }
        if (elementDecl.getURI() == null) {
            return null; // can't really generate
        }

        XmlDocument xmlDoc = null;
        if (reuseExistingDocuments) {
            // See if there is already an existing XML document ...
            final Iterator iter = this.xmlDocResource.getContents().iterator();
            while (iter.hasNext()) {
                final EObject root = (EObject)iter.next();
                if (root instanceof XmlDocument) {
                    final XmlDocument existingDoc = (XmlDocument)root;
                    final XmlRoot rootElement = existingDoc.getRoot();
                    final XSDComponent rootElementXsdComp = rootElement.getXsdComponent();
                    if (rootElementXsdComp instanceof XSDElementDeclaration) {
                        final XSDNamedComponent namedComponent = (XSDElementDeclaration)rootElementXsdComp;
                        if (elementDecl.getURI().equals(namedComponent.getURI())) {
                            // The URIs of the element declarations are the same, so they must be the same schemas
                            xmlDoc = existingDoc;
                            break;
                        }
                    }
                }
            }
        }

        if (monitor.isCanceled()) {
            return xmlDoc;
        }

        if (xmlDoc == null) {

            // Generate the XML document ...
            final XmlDocumentBuilderImpl builder = new XmlDocumentBuilderImpl();
            final XmlDocumentFactory factory = XmlDocumentFactory.eINSTANCE;
            try {
                // Create the document ...
                xmlDoc = factory.createXmlDocument();
                final String docName = computeXmlDocumentName(output);
                xmlDoc.setName(docName);
                this.xmlDocResource.getContents().add(xmlDoc);
                newDocs.add(xmlDoc); // add the document to the model ...

                // Create the root ...
                XmlRoot docRoot = factory.createXmlRoot();
                docRoot.setName(elementDecl.getName());
                docRoot.setXsdComponent(elementDecl);
                xmlDoc.setRoot(docRoot);

                // Build the rest of the document ...
                builder.buildDocument(docRoot, monitor);
                final ITreeToRelationalMapper mapper = ModelMapperFactory.createModelMapper(xmlDoc);
                if (mapper != null) {
                    final MappingClassFactory mcFactory = new MappingClassFactory(mapper);
                    mcFactory.generateMappingClasses(docRoot, MappingClassBuilderStrategy.iterationStrategy, true);
                }
            } catch (Exception e) {
                final Object[] params = new Object[] {getFullName(output)};
                String message = WebServicePlugin.Util.getString("BasicWebServiceXmlDocumentGenerator.ErrorWhileGeneratingXmlDocumentForWsModel", params); //$NON-NLS-1$
                final IStatus status = new Status(IStatus.ERROR, WebServicePlugin.PLUGIN_ID, 0, message, e);
                problems.add(status);
            }
        }

        if (xmlDoc != null) {
            // Set the reference on the output ...
            output.setXmlDocument(xmlDoc);
            return xmlDoc;
        }

        return xmlDoc;
    }

    protected void doGenerateTransformation( final Output output,
                                             final XmlDocument xmlDoc,
                                             final IProgressMonitor monitor,
                                             final List problems,
                                             final ModelContents wsContents ) {
        if (output == null || xmlDoc == null) {
            return;
        }

        // Find the corresponding Input ...
        final Operation op = output.getOperation();
        if (op == null) {
            return;
        }

        // Generate the transformation SQL ...
        final WebServiceUtil util = new WebServiceUtil();
        final String sql = util.generateTransformationSql(output, xmlDoc);
        if (sql != null) {
            // Find the mapping root ...
            SqlTransformationMappingRoot mappingRoot = (SqlTransformationMappingRoot)TransformationHelper.getTransformationMappingRoot(op,
                                                                                                                                       false,
                                                                                                                                       false);
            if (mappingRoot == null) {
                mappingRoot = TransformationFactory.eINSTANCE.createSqlTransformationMappingRoot();

                try {
                    TransformationContainer tc = ModelResourceContainerFactory.getTransformationContainer(op.eResource(), true);
                    mappingRoot.setTarget(op);
                    ModelerCore.getModelEditor().addValue(mappingRoot, op, mappingRoot.getOutputs());
                    ModelerCore.getModelEditor().addValue(tc, mappingRoot, tc.getTransformationMappings());
                } catch (ModelerCoreException err) {
                    ModelerCore.Util.log(IStatus.ERROR,
                                         err,
                                         WebServicePlugin.Util.getString("BasicWebServiceXmlDocumentGenerator.ErrorWhileGeneratingTransformationsForWsModel", op)); //$NON-NLS-1$
                }
            }

            // Set the transformation for the operation ...
            // Note, use the explicit methods rather than TransformationHelper,
            // which presumes the existance of a ModelResource ...
            SqlTransformation sqlTransformation = (SqlTransformation)TransformationHelper.getMappingHelper(mappingRoot);
            if (sqlTransformation == null) {
                sqlTransformation = TransformationFactory.eINSTANCE.createSqlTransformation();
                mappingRoot.setHelper(sqlTransformation);
            }
            // Create Nested SqlTransformation for User SQL
            SqlTransformation userSqlTrans = TransformationHelper.getUserSqlTransformation(mappingRoot);

            if (userSqlTrans == null) {
                userSqlTrans = TransformationFactory.eINSTANCE.createSqlTransformation();
                userSqlTrans.setNestedIn(sqlTransformation);
            }
            userSqlTrans.setSelectSql(sql);

            // Add the XML document as an input to the mappingRoot ...
            mappingRoot.getInputs().add(xmlDoc);
            if (!mappingRoot.getOutputs().contains(op)) {
                mappingRoot.getOutputs().add(op);
            }

            // Add the nested mapping between the SELECT's "xml" and the
            // Output's sample messages (represents the ResultSetColumn)
            SampleMessages resultSetColumn = output.getSamples();
            if (resultSetColumn == null) {
                // Create it since it's not there ...
                resultSetColumn = WebServiceFactory.eINSTANCE.createSampleMessages();
                output.setSamples(resultSetColumn);
            }

            // final Mapping nested = MappingFactory.eINSTANCE.createMapping();
            // nested.getOutputs().add(resultSetColumn);
            // //nested.getInputs().add(???); // nothing to add as input, since it's the sole column in the result set
            // nested.setNestedIn(mappingRoot);
        }

    }

    protected ModelContents doGetModelContents() {
        final ModelContents contents = this.xmlDocResource instanceof EmfResource ? ((EmfResource)this.xmlDocResource).getModelContents() : new ModelContents(
                                                                                                                                                              this.xmlDocResource);
        return contents;
    }

    // ========================================================================================================
    // XmlDocument Generator
    // ========================================================================================================

    protected class WebServiceVisitor implements ModelVisitor {

        private final List outputs;

        protected WebServiceVisitor( final List outputs ) {
            this.outputs = outputs;
        }

        @Override
		public boolean visit( EObject object ) {
            if (object instanceof Output) {
                this.outputs.add(object);
                return false;
            }
            if (object instanceof Interface) {
                return true;
            }
            if (object instanceof Operation) {
                return true;
            }
            return false;
        }

        @Override
		public boolean visit( Resource resource ) {
            return true;
        }
    }

}
