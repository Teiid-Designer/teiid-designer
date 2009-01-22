/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.xml.factory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTypeDefinition;
import com.metamatrix.metamodels.internal.xml.XmlDocumentBuilderImpl;
import com.metamatrix.metamodels.xml.XmlDocument;
import com.metamatrix.metamodels.xml.XmlDocumentFactory;
import com.metamatrix.metamodels.xml.XmlFragment;
import com.metamatrix.metamodels.xml.XmlRoot;
import com.metamatrix.metamodels.xml.util.XmlDocumentUtil;
import com.metamatrix.metamodels.xsd.XsdUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.mapping.factory.MappingClassFactory;
import com.metamatrix.modeler.internal.mapping.factory.ModelMapperFactory;
import com.metamatrix.modeler.mapping.factory.ITreeToRelationalMapper;
import com.metamatrix.modeler.mapping.factory.MappingClassBuilderStrategy;
import com.metamatrix.modeler.xml.PluginConstants;

/**
 * VirtualDocumentModelPopulator
 */
public class VirtualDocumentModelPopulator implements IDocumentsAndFragmentsPopulator, PluginConstants {

    private static final XmlFragment[] EMPTY_DOC_ARRAY = new XmlFragment[0];
    private static final String TXN_NAME = Util.getString("VirtualDocumentModelPopulator.transactionName"); //$NON-NLS-1$
    private static final String DOCUMENT = Util.getString("VirtualDocumentModelPopulator.document"); //$NON-NLS-1$
    private static final String FRAGMENT = Util.getString("VirtualDocumentModelPopulator.fragment"); //$NON-NLS-1$
    private static final String DOC_ERROR_MSG = Util.getString("VirtualDocumentModelPopulator.documentErrorMessage"); //$NON-NLS-1$
    private static final String FRAG_ERROR_MSG = Util.getString("VirtualDocumentModelPopulator.fragmentErrorMessage"); //$NON-NLS-1$

    private IFile schemaModel;
    // private XSDSchema schemaModelObject;
    private Collection unhandledModelImports = new HashSet();
    private Collection allRootElements = new ArrayList();
    private Collection selectedRootElements = new ArrayList();
    private Collection allComplexTypes = new ArrayList();
    private Collection selectedComplexTypes = new ArrayList();
    private Set accumulatedDatatypes = new HashSet();
    private int estimatedNodeCount;

    private boolean doNotBuildDuplicates = false;

    /**
     * Construct an instance of VirtualDocumentModelPopulator.
     */
    public VirtualDocumentModelPopulator( IFile schemaModel ) {
        this.schemaModel = schemaModel;
        populateLists();
    }

    /**
     * Construct an instance of VirtualDocumentModelPopulator.
     */
    public VirtualDocumentModelPopulator( List initialRootElements ) {
        // Let's set the schema model

        if (initialRootElements != null && !initialRootElements.isEmpty()) {
            EObject firstRoot = (EObject)initialRootElements.get(0);
            ModelResource mr = null;

            try {
                mr = ModelUtil.getModel(firstRoot);

                if (mr != null) {
                    this.schemaModel = (IFile)mr.getUnderlyingResource();
                }
            } catch (ModelWorkspaceException theException) {

            }
        }

        setSelectedDocuments(initialRootElements);
    }

    private void populateLists() {
        try {
            // Read the models ...
            final URI uri = URI.createFileURI(schemaModel.getRawLocation().toString());
            Resource resource = ModelerCore.getModelContainer().getResource(uri, true);
            for (Iterator iter = resource.getContents().iterator(); iter.hasNext();) {
                Object obj = iter.next();
                if (obj instanceof XSDSchema) {

                    // Accumulate Root ElementDeclarations
                    List elements = ((XSDSchema)obj).getElementDeclarations();
                    Iterator elemIter = elements.iterator();
                    while (elemIter.hasNext()) {
                        XSDElementDeclaration elemDecl = (XSDElementDeclaration)elemIter.next();
                        allRootElements.add(elemDecl);
                    }
                    // Accumulate ComplexTypes
                    List types = ((XSDSchema)obj).getTypeDefinitions();
                    Iterator typeIter = types.iterator();
                    while (typeIter.hasNext()) {
                        XSDTypeDefinition typeDefn = (XSDTypeDefinition)typeIter.next();
                        if (typeDefn instanceof XSDComplexTypeDefinition) {
                            allComplexTypes.add(typeDefn);
                        }
                    }
                }

            }

        } catch (Exception e) {
            Util.log(e);
        }
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.xml.internal.ui.wizards.IDocumentsAndFragmentsPopulator#getItem()
     */
    public Object getItem() {
        return this.schemaModel;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.xml.internal.ui.wizards.IDocumentsAndFragmentsPopulator#getItemName()
     */
    public String getItemName() {
        return schemaModel.getFullPath().toOSString();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.xml.internal.ui.wizards.IDocumentsAndFragmentsPopulator#getInitialAvailableDocuments()
     */
    public Collection getInitialAvailableDocuments() {
        return allRootElements;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.xml.internal.ui.wizards.IDocumentsAndFragmentsPopulator#getInitialSelectedDocuments()
     */
    public Collection getSelectedDocuments() {
        return selectedRootElements;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.xml.internal.ui.wizards.IDocumentsAndFragmentsPopulator#getInitialAvailableFragments()
     */
    public Collection getInitialAvailableFragments() {
        return allComplexTypes;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.xml.internal.ui.wizards.IDocumentsAndFragmentsPopulator#getInitialSelectedFragments()
     */
    public Collection getSelectedFragments() {
        return selectedComplexTypes;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.xml.internal.ui.wizards.IDocumentsAndFragmentsPopulator#setSelectedDocuments(java.util.Collection)
     */
    public void setSelectedDocuments( Collection selectedItems ) {
        this.selectedRootElements = selectedItems;
    }

    /**
     * Set of Datatypes accumulated by the MappingClassFactory. These are used to determine SimpleTypes that need to be converted
     * to Enterprise Datatypes.
     * 
     * @param accumulatedDatatypes
     */
    public void setAccumulatedDatatypes( Set accumulatedDatatypes ) {
        this.accumulatedDatatypes = accumulatedDatatypes;
    }

    /**
     * Get the Set of Datatypes accumulated by the MappingClassFactory.
     * 
     * @return accumulatedDatatypes
     */
    public Set getAccumulatedDatatypes() {
        if (accumulatedDatatypes != null) {
            return this.accumulatedDatatypes;
        }
        // We don't want to return NULL here.
        return new HashSet(0);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.xml.internal.ui.wizards.IDocumentsAndFragmentsPopulator#setSelectedFragments(java.util.Collection)
     */
    public void setSelectedFragments( Collection selectedItems ) {
        this.selectedComplexTypes = selectedItems;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.xml.internal.ui.wizards.IDocumentsAndFragmentsPopulator#buildModel(org.eclipse.core.resources.IResource)
     */
    public XmlFragment[] buildModel( ModelResource modelResource,
                                     boolean buildEntireDocument,
                                     boolean buildMappingClasses,
                                     MappingClassBuilderStrategy strategy,
                                     final IProgressMonitor progressMonitor ) {
        List rv = new ArrayList(selectedComplexTypes.size() + selectedRootElements.size());
        estimatedNodeCount = 0;
        final IProgressMonitor monitor = progressMonitor != null ? progressMonitor : new NullProgressMonitor();

        // Ensure that the XSD resource used to build the XML document model is fully resolved
        try {
            final URI uri = URI.createFileURI(schemaModel.getRawLocation().toString());
            Resource xsdResource = ModelerCore.getModelContainer().getResource(uri, true);
            if (xsdResource != null) {
                XsdUtil.resolveSchemaDirectives(xsdResource);
            }
        } catch (Exception e) {
            Util.log(e);
        }

        XmlDocumentBuilderImpl builder = new XmlDocumentBuilderImpl();
        XmlDocumentFactory factory = XmlDocumentFactory.eINSTANCE;
        ArrayList fragmentRoots = new ArrayList();

        boolean startedTxn = ModelerCore.startTxn(false, false, TXN_NAME, this);
        boolean succeeded = false;
        try {
            // set the resource if it is available:
            Resource resource = (modelResource != null) ? modelResource.getEmfResource() : null;

            for (Iterator iter = selectedComplexTypes.iterator(); iter.hasNext();) {
                // Check for cancellation ...
                if (monitor.isCanceled()) {
                    break;
                }

                XSDComplexTypeDefinition schemaComplexType = (XSDComplexTypeDefinition)iter.next();
                try {
                    String suggestedName = schemaComplexType.getName() + FRAGMENT;
                    if (shouldBuildDocument(modelResource, suggestedName)) {
                        XmlFragment fragment = factory.createXmlFragment();
                        fragment.setName(schemaComplexType.getName() + FRAGMENT);
                        if (resource != null) {
                            resource.getContents().add(fragment);
                        } // endif

                        XmlRoot docRoot = factory.createXmlRoot();
                        docRoot.setName(schemaComplexType.getName());
                        docRoot.setXsdComponent(schemaComplexType);
                        fragment.setRoot(docRoot);
                        if (buildEntireDocument) {
                            estimatedNodeCount += builder.buildDocument(docRoot, monitor);
                            fragmentRoots.add(docRoot);
                            if (buildMappingClasses) {
                                buildMappingClasses(fragment, strategy);

                                // Force GC
                                System.gc();
                                Thread.yield();
                            }
                        } else {
                            // not building entire document; leave it as BuildStatus incomplete
                            XmlDocumentUtil.setIncomplete(docRoot, true);
                            estimatedNodeCount++;
                        }
                        rv.add(fragment);
                    } else {
                        XmlFragment document = getExistingDocument(modelResource, suggestedName);
                        if (document != null) {
                            rv.add(document);
                        }
                    }
                } catch (Exception e) {
                    String message = FRAG_ERROR_MSG + '\n' + schemaComplexType.getName();
                    Util.log(IStatus.ERROR, e, message);
                    // MessageDialog.openError(null, FRAG_ERROR_TITLE, message);
                }

            }

            // force GC
            System.gc();
            Thread.yield();

            // swjTODO: load fragmentRoots into the builder before creating documents
            // try {
            // builder.buildXmlFragments(schemaModelObject);
            // } catch (Exception e) {
            // String message = FRAG_ERROR_MSG + '\n' + schemaModelObject.toString();
            // ModelerXmlUiPlugin.Util.log(IStatus.ERROR, e, message);
            // MessageDialog.openError(null, FRAG_ERROR_TITLE, message);
            // }

            for (Iterator iter = selectedRootElements.iterator(); iter.hasNext();) {
                // Check for cancellation ...
                if (monitor.isCanceled()) {
                    break;
                }

                XSDElementDeclaration schemaRootElement = (XSDElementDeclaration)iter.next();
                try {
                    String suggestedName = schemaRootElement.getName() + DOCUMENT;
                    if (shouldBuildDocument(modelResource, suggestedName)) {
                        XmlDocument document = factory.createXmlDocument();

                        if (resource != null) {
                            ModelerCore.getModelEditor().addValue(resource, document, resource.getContents());
                        }

                        String baseName = schemaRootElement.getName() + DOCUMENT;
                        String newName = generateInitialUniqueName(document, baseName);
                        document.setName(newName);

                        XmlRoot docRoot = factory.createXmlRoot();
                        docRoot.setName(schemaRootElement.getName());
                        docRoot.setXsdComponent(schemaRootElement);
                        document.setRoot(docRoot);
                        if (buildEntireDocument) {
                            estimatedNodeCount += builder.buildDocument(docRoot, monitor);
                            if (buildMappingClasses) {
                                buildMappingClasses(document, strategy);

                                // Force GC
                                System.gc();
                                Thread.yield();
                            }
                        } else {
                            // not building entire document; leave it as BuildStatus incomplete
                            XmlDocumentUtil.setIncomplete(docRoot, true);
                            estimatedNodeCount++;
                        }
                        rv.add(document);
                    } else {
                        XmlDocument document = getExistingDocument(modelResource, suggestedName);
                        if (document != null) {
                            rv.add(document);
                        }
                    }
                } catch (Exception e) {
                    String message = DOC_ERROR_MSG + '\n' + schemaRootElement.getName();
                    Util.log(IStatus.ERROR, e, message);
                    // MessageDialog.openError(null, DOC_ERROR_TITLE, message);
                }

            }

            // force GC
            System.gc();
            Thread.yield();
            succeeded = true;

        } catch (ModelWorkspaceException e) {
            Util.log(e);
        } finally {
            if (startedTxn) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
            monitor.done();

            // Force GC
            System.gc();
            Thread.yield();

        }

        unhandledModelImports = builder.getUnhandledModelImports();

        // Clean up
        builder = null;

        if (modelResource != null) {
            try {
                modelResource.save(monitor, true);
                modelResource.getEmfResource().setModified(false);

                // Force GC
                System.gc();
                Thread.yield();
            } catch (ModelWorkspaceException e) {
                Util.log(e);
            }
        }
        return (XmlFragment[])rv.toArray(EMPTY_DOC_ARRAY);
    }

    private String generateInitialUniqueName( final EObject eObject,
                                              final String proposedName ) {
        String newName = proposedName;

        final EStructuralFeature nameFeature = ModelerCore.getModelEditor().getNameFeature(eObject);
        if (nameFeature != null && eObject.eResource() != null) {
            EList siblings = null;
            if (eObject.eContainer() == null) {
                siblings = eObject.eResource().getContents();
            } else {
                siblings = eObject.eContainer().eContents();
            }
            if (siblings != null && !siblings.isEmpty() && eObject.eGet(nameFeature) == null) {
                final Set siblingNames = new HashSet();
                for (Iterator it = siblings.iterator(); it.hasNext();) {
                    final EObject child = (EObject)it.next();
                    if (eObject.getClass().equals(child.getClass())) {
                        siblingNames.add(child.eGet(nameFeature));
                    }
                }
                boolean foundUniqueName = false;
                int index = 1;
                while (!foundUniqueName) {
                    if (siblingNames.contains(newName)) {
                        newName = proposedName + String.valueOf(index++);
                    } else {
                        foundUniqueName = true;
                    }
                }
            }
        }
        return newName;
    }

    public Collection getUnhandledModelImports() {
        if (unhandledModelImports == null) {
            unhandledModelImports = new HashSet();
        }
        return unhandledModelImports;
    }

    public void buildMappingClasses( XmlFragment treeNode,
                                     MappingClassBuilderStrategy strategy ) {
        XmlRoot docRoot = treeNode.getRoot();
        ITreeToRelationalMapper mapper = ModelMapperFactory.createModelMapper(treeNode);

        getAccumulatedDatatypes().addAll(new MappingClassFactory(mapper).generateMappingClasses(docRoot, strategy, true));

    }

    public int getLastEstimatedNodeCount() {
        return estimatedNodeCount;
    }

    /**
     * @param theDoNotBuildDuplicates The doNotBuildDuplicates to set.
     * @since 5.0
     */
    public void setDoNotBuildDuplicates( boolean theDoNotBuildDuplicates ) {
        this.doNotBuildDuplicates = theDoNotBuildDuplicates;
    }

    private boolean shouldBuildDocument( ModelResource modelResource,
                                         String newDocName ) {
        if (newDocName != null && doNotBuildDuplicates) {
            // Need to do a check for existing document with name
            List rootObjs = null;

            try {
                rootObjs = modelResource.getAllRootEObjects();
            } catch (ModelWorkspaceException theException) {
                Util.log(theException);
            }
            if (rootObjs != null && !rootObjs.isEmpty()) {
                Iterator iter = rootObjs.iterator();
                Object nextObj = null;
                while (iter.hasNext()) {
                    nextObj = iter.next();
                    if (nextObj instanceof XmlDocument) {
                        String docName = ModelerCore.getModelEditor().getName((EObject)nextObj);
                        if (docName != null && docName.equalsIgnoreCase(newDocName)) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }
        return true;
    }

    private XmlDocument getExistingDocument( ModelResource modelResource,
                                             String newDocName ) {
        if (newDocName != null && doNotBuildDuplicates) {
            // Need to do a check for existing document with name
            List rootObjs = null;

            try {
                rootObjs = modelResource.getAllRootEObjects();
            } catch (ModelWorkspaceException theException) {
                Util.log(theException);
            }
            if (rootObjs != null && !rootObjs.isEmpty()) {
                Iterator iter = rootObjs.iterator();
                Object nextObj = null;
                while (iter.hasNext()) {
                    nextObj = iter.next();
                    if (nextObj instanceof XmlDocument) {
                        String docName = ModelerCore.getModelEditor().getName((EObject)nextObj);
                        if (docName != null && docName.equalsIgnoreCase(newDocName)) {
                            return (XmlDocument)nextObj;
                        }
                    }
                }
            }
        }
        return null;
    }
}
