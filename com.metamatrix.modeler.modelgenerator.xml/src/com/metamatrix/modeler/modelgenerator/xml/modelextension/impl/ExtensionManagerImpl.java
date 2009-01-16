/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */

package com.metamatrix.modeler.modelgenerator.xml.modelextension.impl;

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
import com.metamatrix.metamodels.core.CoreFactory;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.core.extension.ExtensionFactory;
import com.metamatrix.metamodels.core.extension.ExtensionPackage;
import com.metamatrix.metamodels.core.extension.XAttribute;
import com.metamatrix.metamodels.core.extension.XClass;
import com.metamatrix.metamodels.core.extension.XPackage;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.modelgenerator.xml.XmlImporterUiPlugin;
import com.metamatrix.modeler.modelgenerator.xml.modelextension.ExtensionManager;

public abstract class ExtensionManagerImpl implements ExtensionManager {

    private XPackage thePackage;
    protected XClass theTableXClass;
    protected XClass theColumnXClass;

    public XPackage getPackage() {
        return thePackage;
    }

    /**
     * Loads the extension objects into memory so that they can be assigned to a physical model and values can be set. Creates the
     * model extension model in the project if it does not exist.
     */
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

        theTableXClass = xFactory.createXClass();
        theTableXClass.setExtendedClass(RelationalPackage.eINSTANCE.getBaseTable());
        theTableXClass.setName(getTableName());
        thePackage.getEClassifiers().add(theTableXClass);
        createTableExtensions(xFactory, theTableXClass);

        theColumnXClass = xFactory.createXClass();
        theColumnXClass.setExtendedClass(RelationalPackage.eINSTANCE.getColumn());
        theColumnXClass.setName(getColumnName());
        thePackage.getEClassifiers().add(theColumnXClass);
        createColumnExtensions(xFactory, theColumnXClass);

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
            cntr = com.metamatrix.modeler.core.ModelerCore.getModelContainer();
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

    public void assignAttribute( XAttribute attribute ) {

    }

    public void assignClassifier( EClassifier classifier ) {

    }

    public void createColumnExtensions( ExtensionFactory factory,
                                        XClass column ) {

    }

    public void createEnums( ExtensionFactory factory ) {

    }

    public void createTableExtensions( ExtensionFactory factory,
                                       XClass table ) {

    }
}
