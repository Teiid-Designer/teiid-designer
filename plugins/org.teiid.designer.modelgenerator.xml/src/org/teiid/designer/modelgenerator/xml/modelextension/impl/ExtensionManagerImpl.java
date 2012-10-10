/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.xml.modelextension.impl;

import java.util.Iterator;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.resource.Resource;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.container.Container;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.metamodels.core.CoreFactory;
import org.teiid.designer.metamodels.core.ModelAnnotation;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.metamodels.core.extension.ExtensionFactory;
import org.teiid.designer.metamodels.core.extension.ExtensionPackage;
import org.teiid.designer.metamodels.core.extension.XAttribute;
import org.teiid.designer.metamodels.core.extension.XClass;
import org.teiid.designer.metamodels.core.extension.XPackage;
import org.teiid.designer.metamodels.relational.RelationalPackage;
import org.teiid.designer.modelgenerator.xml.XmlImporterUiPlugin;
import org.teiid.designer.modelgenerator.xml.modelextension.ExtensionManager;


/**
 * @since 8.0
 */
public abstract class ExtensionManagerImpl implements ExtensionManager {

    private XPackage thePackage;
    private XClass theCatalogXClass;
    protected XClass theTableXClass;
    protected XClass theColumnXClass;
    protected XClass theSchemaXClass;

    @Override
	public XPackage getPackage() {
        return thePackage;
    }

    /**
     * Loads the extension objects into memory so that they can be assigned to a physical model and values can be set. Creates the
     * model extension model in the project if it does not exist.
     */
    @Override
	public void loadModelExtensions( IContainer targetModelLocation,
                                     IProgressMonitor monitor ) throws ModelerCoreException {

        Path modelPath = new Path(validateFileName(getModelFileName()));
        IFile iFile = targetModelLocation.getFile(modelPath);
        if (!iFile.exists()) {
            createModelExtensions(iFile, targetModelLocation, monitor);
        } else {
            loadModelExtension(targetModelLocation);
        }
    }

    /**
     * Create the model extension file in the model project if it does not exist.
     * 
     * @param file The model file that will contain the extension definitions.
     * @param targetModelLocation The model project containing the file.
     * @param monitor
     * @throws ModelerCoreException
     */
    void createModelExtensions( IFile file,
                                IContainer targetModelLocation,
                                IProgressMonitor monitor ) throws ModelerCoreException {
        ModelResource modelExtension = ModelerCore.create(file);

        ModelAnnotation annotation = CoreFactory.eINSTANCE.createModelAnnotation();
        annotation.setPrimaryMetamodelUri(RelationalPackage.eINSTANCE.getNsURI());
        annotation.setModelType(ModelType.EXTENSION_LITERAL);

        ExtensionPackage xPackage = ExtensionPackage.eINSTANCE;
        ExtensionFactory xFactory = xPackage.getExtensionFactory();

        thePackage = xFactory.createXPackage();
        thePackage.setName(getPackageName());
        thePackage.setNsPrefix(getPackagePrefix());
        thePackage.setNsURI(getPackageNsUri());

        try {
            modelExtension.getEmfResource().getContents().add(annotation);
            modelExtension.getEmfResource().getContents().add(thePackage);
        } catch (ModelWorkspaceException e1) {
            ModelerCoreException mbe = new ModelerCoreException();
            mbe.initCause(e1);
            throw mbe;
        }

        createEnums(xFactory);

        if (null != getCatalogName() && "" != getCatalogName()) { //$NON-NLS-1$
            theCatalogXClass = xFactory.createXClass();
            theCatalogXClass.setExtendedClass(RelationalPackage.eINSTANCE.getCatalog());
            theCatalogXClass.setName(getCatalogName());
            thePackage.getEClassifiers().add(theCatalogXClass);
            createCatalogExtensions(xFactory, theCatalogXClass);
        }

        if (null != getSchemaName() && "" != getSchemaName()) { //$NON-NLS-1$
            theSchemaXClass = xFactory.createXClass();
            theSchemaXClass.setExtendedClass(RelationalPackage.eINSTANCE.getSchema());
            theSchemaXClass.setName(getSchemaName());
            thePackage.getEClassifiers().add(theSchemaXClass);
            createSchemaExtensions(xFactory, theSchemaXClass);
        }

        if (null != getTableName() && "" != getTableName()) { //$NON-NLS-1$
            theTableXClass = xFactory.createXClass();
            theTableXClass.setExtendedClass(RelationalPackage.eINSTANCE.getBaseTable());
            theTableXClass.setName(getTableName());
            thePackage.getEClassifiers().add(theTableXClass);
            createTableExtensions(xFactory, theTableXClass);
        }

        if (null != getColumnName() && "" != getColumnName()) { //$NON-NLS-1$
            theColumnXClass = xFactory.createXClass();
            theColumnXClass.setExtendedClass(RelationalPackage.eINSTANCE.getColumn());
            theColumnXClass.setName(getColumnName());
            thePackage.getEClassifiers().add(theColumnXClass);
            createColumnExtensions(xFactory, theColumnXClass);
        }

        try {
            modelExtension.save(monitor, false);
        } catch (ModelWorkspaceException e) {
            ModelerCoreException mbe = new ModelerCoreException();
            mbe.initCause(e);
            throw mbe;
        }
    }

    void loadModelExtension( IContainer targetModelLocation ) throws ModelerCoreException {
        Container cntr;
        try {
            cntr = org.teiid.designer.core.ModelerCore.getModelContainer();
        } catch (CoreException e) {
            ModelerCoreException mbe = new ModelerCoreException();
            mbe.initCause(e);
            throw mbe;
        }

        cntr.getPackageRegistry().put(ExtensionPackage.eNS_URI, ExtensionPackage.eINSTANCE);

        IFile extensionFile = targetModelLocation.getProject().getFile(new Path(validateFileName(getModelFileName())));
        String extPath = extensionFile.getRawLocation().toOSString();
        URI fileURI = URI.createFileURI(extPath);
        Resource xPkg = cntr.getResource(fileURI, true);
        EList resources = xPkg.getContents();
        thePackage = null;
        for (Iterator resIter = resources.iterator(); resIter.hasNext();) {
            Object next = resIter.next();
            if (next instanceof XPackage) thePackage = (XPackage)next;
        }

        if (null == thePackage) throw new RuntimeException(
                                                           XmlImporterUiPlugin.getDefault().getPluginUtil().getString("ExtensionManager.package.null.after.load")); //$NON-NLS-1$

        EList enums = getPackage().getEClassifiers();
        Iterator iter = enums.iterator();
        while (iter.hasNext()) {
            assignClassifier((EClassifier)iter.next());
        }

        theTableXClass = thePackage.findXClass(RelationalPackage.eINSTANCE.getBaseTable());
        EList attributes = theTableXClass.getEAllAttributes();
        iter = attributes.iterator();
        while (iter.hasNext()) {
            assignAttribute((XAttribute)iter.next());
        }

        theColumnXClass = thePackage.findXClass(RelationalPackage.eINSTANCE.getColumn());
        attributes = theColumnXClass.getEAllAttributes();
        iter = attributes.iterator();
        while (iter.hasNext()) {
            assignAttribute((XAttribute)iter.next());
        }
    }

    private String validateFileName( String fileName ) {
        String result;
        if (fileName.endsWith(".xmi")) { //$NON-NLS-1$
            result = fileName;
        } else {
            result = fileName + ".xmi"; //$NON-NLS-1$
        }
        return result;
    }

    @Override
	public void assignAttribute( XAttribute attribute ) {

    }

    @Override
	public void assignClassifier( EClassifier classifier ) {

    }

    @Override
	public void createColumnExtensions( ExtensionFactory factory,
                                        XClass column ) {

    }

    @Override
	public void createEnums( ExtensionFactory factory ) {

    }

    @Override
	public void createTableExtensions( ExtensionFactory factory,
                                       XClass table ) {

    }

    @Override
	public void createCatalogExtensions( ExtensionFactory factory,
                                         XClass catalog ) {
    }

    @Override
	public void createSchemaExtensions( ExtensionFactory factory,
                                        XClass schema ) {
    }

    @Override
	public String getColumnName() {
        return ""; //$NON-NLS-1$
    }

    @Override
	public String getCatalogName() {
        return ""; //$NON-NLS-1$
    }

    @Override
	public String getSchemaName() {
        return ""; //$NON-NLS-1$
    }

    @Override
	public String getTableName() {
        return ""; //$NON-NLS-1$
    }
}
