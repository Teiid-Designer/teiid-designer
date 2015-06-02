/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.vdb.ui.util;

import static org.teiid.designer.metamodels.relational.extension.RestModelExtensionConstants.NAMESPACE_PROVIDER;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.metamodel.aspect.sql.SqlAspectHelper;
import org.teiid.designer.core.workspace.ModelObjectAnnotationHelper;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.core.workspace.WorkspaceResourceFinderUtil;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.definition.ModelObjectExtensionAssistant;
import org.teiid.designer.metamodels.relational.Procedure;
import org.teiid.designer.metamodels.relational.extension.RestModelExtensionConstants;
import org.teiid.designer.metamodels.relational.impl.ProcedureImpl;
import org.teiid.designer.ui.viewsupport.ModelIdentifier;
import org.teiid.designer.vdb.Vdb;
import org.teiid.designer.vdb.VdbEntry;
import org.teiid.designer.vdb.VdbModelEntry;
import org.teiid.designer.vdb.XmiVdb;


/**
 * Validation for REST based VDB
 *
 * @since 8.5
 */
public class RestVdbUtil {

	public static final String VDB_EXTENSION = "vdb"; //$NON-NLS-1$
    private static final ModelObjectAnnotationHelper ANNOTATION_HELPER = new ModelObjectAnnotationHelper();

    /**
     * @param vdbFile
     * @return is the given file a rest war vdb
     * @throws Exception
     */
    public static boolean isRestWarVdb(IFile vdbFile) throws Exception {
        if (! isVdb(vdbFile))
            return false;

        boolean result = false;
        try {
            Vdb vdb = new XmiVdb(vdbFile, new NullProgressMonitor());
            Set<VdbEntry> modelEntrySet = vdb.getModelEntries();
            for (VdbEntry vdbModelEntry : modelEntrySet) {
                final ModelResource modelResource = ModelerCore.getModelWorkspace().findModelResource(vdbModelEntry.getName());
                if (! ModelIdentifier.isVirtualModelType(modelResource))
                    continue;

                result = hasRestProcedures(modelResource);
                if (result) {
                    break;
                }
            }
        } catch (Exception ex) {
            new RuntimeException(ex);
        }
        return result;
    }
    
    /**
     * @param result
     * @param obj
     * @return 
     */
    private static boolean isVdb(Object obj) {
        if (obj == null)
            return false;

        if (! (obj instanceof IFile))
            return false;

        return VDB_EXTENSION.equals(((IFile) obj).getFileExtension());
    }
    
    /**
     * @param eObjectList
     * @return boolean true if model contains a REST procedure
     */
    private static boolean hasRestProcedures(ModelResource modelResource) throws Exception {
        Collection<EObject> eObjectList = modelResource.getEObjects();
        boolean result = false;
        for (EObject eObject : eObjectList) {
            if (SqlAspectHelper.isProcedure(eObject)) {
                IPath path = ModelerCore.getModelEditor().getModelRelativePathIncludingModel(eObject);
                final StringBuffer sb = new StringBuffer();
                final String[] segments = path.segments();
                for (int i = 0; i < segments.length; i++) {
                    if (i != 0) {
                        sb.append('.');
                    }
                    final String segment = segments[i];
                    sb.append(segment);
                    Procedure procedure = (Procedure)eObject;
                    String restMethod = getRestMethod(procedure);
                    String uri = null;
                    if (restMethod != null) {
                        uri = getUri(procedure);
                    }
                    if (uri != null && restMethod != null){
                    	result = true;
                    	break;
                    }
                }
            }
        }

        return result;
    }
    
    /**
     * @param procedure the procedure
     * @return String uri value
     */
    public static String getUri( Procedure procedure ) {
        String uri = null;

        try {
            // try new way first
            ModelObjectExtensionAssistant assistant = (ModelObjectExtensionAssistant)ExtensionPlugin.getInstance()
                                                                                                    .getRegistry()
                                                                                                    .getModelExtensionAssistant(NAMESPACE_PROVIDER.getNamespacePrefix());
            uri = assistant.getPropertyValue(procedure, RestModelExtensionConstants.PropertyIds.URI);

            if (CoreStringUtil.isEmpty(uri)) {
                uri = (String)ANNOTATION_HELPER.getPropertyValueAnyCase(procedure,
                                                                        ModelObjectAnnotationHelper.EXTENDED_PROPERTY_NAMESPACE
                                                                                + "URI"); //$NON-NLS-1$
            }
        } catch (Exception e) {
        	ModelerCore.Util.log(e);
        }

        return uri;
    }
    
    /**
     * @param procedure the procedure
     * @return String rest method
     */
    public static String getRestMethod( Procedure procedure ) {
        String restMethod = null;

        try {
            // try new way first
            ModelObjectExtensionAssistant assistant = (ModelObjectExtensionAssistant)ExtensionPlugin.getInstance()
                                                                                                    .getRegistry()
                                                                                                    .getModelExtensionAssistant(NAMESPACE_PROVIDER.getNamespacePrefix());
            restMethod = assistant.getPropertyValue(procedure, RestModelExtensionConstants.PropertyIds.REST_METHOD);

            if (CoreStringUtil.isEmpty(restMethod.trim())) {
                // try old way
                restMethod = (String)ANNOTATION_HELPER.getPropertyValueAnyCase(procedure,
                                                                               ModelObjectAnnotationHelper.EXTENDED_PROPERTY_NAMESPACE
                                                                                       + "REST-METHOD"); //$NON-NLS-1$
            }
        } catch (Exception e) {
        	ModelerCore.Util.log(e);
        }

        return restMethod;
    }

}
