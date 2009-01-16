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

package com.metamatrix.vdb.internal.edit;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;

import org.eclipse.emf.ecore.resource.Resource;

import com.metamatrix.common.util.WSDLServletUtil;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.metamodels.wsdl.WsdlPackage;
import com.metamatrix.vdb.edit.VdbArtifactGenerator;
import com.metamatrix.vdb.edit.VdbEditPlugin;
import com.metamatrix.vdb.edit.VdbGenerationContext;
import com.metamatrix.vdb.edit.manifest.ManifestFactory;
import com.metamatrix.vdb.edit.manifest.VirtualDatabase;
import com.metamatrix.vdb.edit.manifest.WsdlOptions;


/** 
 * @since 5.0
 */
public class WsdlArtifactGenerator implements VdbArtifactGenerator {
    
    public static final String GENERATED_WSDL_NAME     = WSDLServletUtil.GENERATED_WSDL_NAME;
    public static final String GENERATED_WSDL_FILENAME = WSDLServletUtil.GENERATED_WSDL_FILENAME;
    
    public static final String WSDL_DEFAULT_TARGET_NAMESPACE_URI_PREFIX = "http://com.metamatrix/"; //$NON-NLS-1$
    public static final String WSDL_DEFAULT_NAMESPACE_URI = WsdlPackage.eNS_URI;    // per WS-I
    
    public static final int CREATE_WSDL_GENERATOR_ERROR_CODE = 100601;
    public static final int WRITE_WSDL_FILE_ERROR_CODE       = 100602;
    public static final int GENERATE_WSDL_FILE_ERROR_CODE    = 100603;
    public static final int CLOSE_STREAM_ERROR_CODE          = 100604;
    public static final int WSDL_FILE_EXISTS_ERROR_CODE      = 100605;

    // ==================================================================================
    //                        C O N S T R U C T O R S
    // ==================================================================================

    /** 
     * @since 5.0
     */
    public WsdlArtifactGenerator() {
        super();
    }
    
    //==================================================================================
    //                     I N T E R F A C E   M E T H O D S
    //==================================================================================

    /** 
     * @see com.metamatrix.vdb.edit.VdbArtifactGenerator#execute(com.metamatrix.vdb.edit.VdbGenerationContext)
     * @since 5.0
     */
    public void execute(final VdbGenerationContext theContext) {
        ArgCheck.isNotNull(theContext);
        if (!(theContext instanceof InternalVdbGenerationContext)) {
            final String msg = VdbEditPlugin.Util.getString("WsdlArtifactGenerator.InternalVdbGenerationContext_required"); //$NON-NLS-1$
            throw new IllegalArgumentException(msg);
        }
        final InternalVdbGenerationContext context = (InternalVdbGenerationContext)theContext;
        
        // Set the monitor display message for this generator
        String displayMessage = "Generating WSDL file ..."; //$NON-NLS-1$
        context.setProgressMessage(displayMessage);
        
        // --------------------------------------------------------------------------------------------------------
        //                      Generate WSDL file for the web service models in the VDB
        // --------------------------------------------------------------------------------------------------------

        // Create a list of model resources to process ...
        final List eResources = new ArrayList( Arrays.asList(context.getModels()) );
        
        // Ensure that, at minimum, the target namespace is defined for the WSDL
        // otherwise the resultant VDB will will not be deployable - defect 20917
        WsdlOptions wsdlOptions = (WsdlOptions)context.getData(InternalVdbGenerationContext.WSDL_GENERATION_OPTIONS);
        if (wsdlOptions == null) {
            wsdlOptions = ManifestFactory.eINSTANCE.createWsdlOptions();
            wsdlOptions.setTargetNamespaceUri(WSDL_DEFAULT_TARGET_NAMESPACE_URI_PREFIX + getVirtualDatabase(context).getName()); 
            wsdlOptions.setDefaultNamespaceUri(WSDL_DEFAULT_NAMESPACE_URI); 
            getVirtualDatabase(context).setWsdlOptions(wsdlOptions);
        }
        
        // Create a map of model resource to its relative path in the VDB - required argument to WsdlGenerationOptionsHelper
        final Map eResourceToPath = new HashMap(eResources.size());
        for (Iterator i = eResources.iterator(); i.hasNext();) {
            final Resource model = (Resource)i.next();
            final String path    = context.getModelHelper().getPath(model);
            if (!StringUtil.isEmpty(path)) {
                eResourceToPath.put(model, createNormalizedPath(path));
            }
        }

        WsdlGenerationOptionsHelper helper = null;
        try {
            helper = new WsdlGenerationOptionsHelper(eResources, wsdlOptions, eResourceToPath);
        } catch (Throwable e) {
            final String msg = VdbEditPlugin.Util.getString("WsdlArtifactGenerator.Error_creating_wsdl_generator"); //$NON-NLS-1$
            context.addErrorMessage(msg, CREATE_WSDL_GENERATOR_ERROR_CODE, e);
            return;
        }
        
        // Generate and write the WSDL ...
        File wsdlFile = null;
        if (helper.canWsdlBeGenerated()) {
            IStatus genStatus = null;
            OutputStream wsdlStream = null;
            try {
                wsdlFile   = new File(context.getTemporaryDirectory(),GENERATED_WSDL_FILENAME);
                wsdlStream = new FileOutputStream(wsdlFile);
                wsdlStream = new BufferedOutputStream(wsdlStream);
                 
                genStatus = helper.generateWsdl(new NullProgressMonitor(), wsdlStream);
            } catch (IOException e) {
                final String msg = VdbEditPlugin.Util.getString("WsdlArtifactGenerator.Error_writing_wsdl_to_scratch",GENERATED_WSDL_FILENAME); //$NON-NLS-1$
                context.addErrorMessage(msg, WRITE_WSDL_FILE_ERROR_CODE, e);
            } catch (Throwable e) {
                final String msg = VdbEditPlugin.Util.getString("WsdlArtifactGenerator.Error_generating_wsdl",GENERATED_WSDL_FILENAME); //$NON-NLS-1$
                context.addErrorMessage(msg, GENERATE_WSDL_FILE_ERROR_CODE, e);
            } finally {
                if (wsdlStream != null) {
                    try {
                        wsdlStream.close();
                    } catch (IOException e) {
                        final String msg = VdbEditPlugin.Util.getString("WsdlArtifactGenerator.Error_closing_stream",GENERATED_WSDL_FILENAME); //$NON-NLS-1$
                        context.addErrorMessage(msg, CLOSE_STREAM_ERROR_CODE, e);
                    }
                }
                if (genStatus != null && !genStatus.isOK()) {
                    if (genStatus instanceof MultiStatus) {
                        final IStatus[] children = ((MultiStatus)genStatus).getChildren();
                        for (int i = 0; i < children.length; i++) {
                            addMessage(context, children[i], GENERATE_WSDL_FILE_ERROR_CODE);
                        }
                    } else {
                        addMessage(context, genStatus, GENERATE_WSDL_FILE_ERROR_CODE);
                    }
                }
            }
        }
        
        // --------------------------------------------------------------------------------------------------------
        //                             Add generated WSDL file to the VDB  
        // --------------------------------------------------------------------------------------------------------

        if (wsdlFile != null) {
            final String pathInVdb = wsdlFile.getName();
            boolean success = context.addGeneratedArtifact(pathInVdb, wsdlFile);
            if ( !success ) {
                final String msg = VdbEditPlugin.Util.getString("WsdlArtifactGenerator.Error_file_already_exists",pathInVdb);  //$NON-NLS-1$
                context.addWarningMessage(msg,WSDL_FILE_EXISTS_ERROR_CODE);
            }
        }
    }
    
    // ==================================================================================
    //                    P R O T E C T E D   M E T H O D S
    // ==================================================================================
    
    protected VirtualDatabase getVirtualDatabase(final InternalVdbGenerationContext context) {
        ArgCheck.isNotNull(context);
        return context.getVdbContext().getVirtualDatabase();
    }
    
    protected IPath createNormalizedPath(final String pathInArchive) {
        ArgCheck.isNotNull(pathInArchive);
        ArgCheck.isNotZeroLength(pathInArchive);
        return new Path(pathInArchive).makeAbsolute();
    }   
    
    protected void addMessage(final VdbGenerationContext context, final IStatus status, final int code) {
        if (context != null && status != null) {
            switch (status.getSeverity()) {
                case IStatus.INFO:
                    context.addInfoMessage(status.getMessage(), code);
                    break;
                case IStatus.WARNING:
                    context.addWarningMessage(status.getMessage(), code);
                    break;
                case IStatus.ERROR:
                    context.addErrorMessage(status.getMessage(), code, null);
                    break;
                default:
                    break;
            }
        }
    }

}
