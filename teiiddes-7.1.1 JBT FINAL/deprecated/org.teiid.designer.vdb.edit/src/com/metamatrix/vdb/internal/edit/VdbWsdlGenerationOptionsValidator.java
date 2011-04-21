/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.internal.edit;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.metamodels.wsdl.WsdlPackage;
import com.metamatrix.vdb.edit.VdbEditPlugin;
import com.metamatrix.vdb.edit.VdbWsdlGenerationOptions;
import com.metamatrix.vdb.edit.manifest.VirtualDatabase;
import com.metamatrix.vdb.edit.manifest.WsdlOptions;


/** 
 * @since 4.2
 */
public class VdbWsdlGenerationOptionsValidator {
    
    private VdbEditingContextImpl vdbEditingContextImpl;
    
    /**
     *  
     * @param vdbEditingContextImpl
     * @since 4.2
     */
    public VdbWsdlGenerationOptionsValidator(VdbEditingContextImpl vdbEditingContextImpl) {
        ArgCheck.isNotNull(vdbEditingContextImpl);
        this.vdbEditingContextImpl = vdbEditingContextImpl;
    }
    
    /**
     * Validate the WSDL generation options add any problems to the supplied list. 
     * @param problems
     * @since 4.3
     */
    public void validateVdbWsdlGenerationOptions(final List problems) {
        validateVdbWsdlGenerationOptions(new ArrayList(), problems);
    }
    
    /**
     *  
     * @param problemMarker
     * @param problems
     * @since 4.2
     */
    protected void validateVdbWsdlGenerationOptions(List problemMarker, List problems) {
        VdbWsdlGenerationOptions wsdlGenOptions = this.vdbEditingContextImpl.getVdbWsdlGenerationOptions();

        if (wsdlGenOptions.canWsdlBeGenerated()) {
            
            final VirtualDatabase vdb = getVirtualDatabase();
            final WsdlOptions wsdlOptions = vdb.getWsdlOptions();
            
            if (wsdlOptions == null) {
                final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.wsdlTargetNamespaceUriMissing"); //$NON-NLS-1$          
//                setProblems(vdb, msg, IStatus.WARNING, IStatus.WARNING, problemMarker, problems);
                // MyDefect : 14430 change the status from warning to error.
                setProblems(vdb, msg, IStatus.ERROR, IStatus.ERROR, problemMarker, problems);
            }else {            
//              There should (not must) be a target namespace ...
                validateTargetNamespaceUri(vdb, wsdlOptions, problemMarker, problems);
                
//              The default namespace SHOULD (not must) be the WSDL namespace (per WS-I)
                validateDefaultNamespaceUri(vdb, wsdlOptions, problemMarker, problems);  
            }
        }else {
            
        }
    }
    
    private VirtualDatabase getVirtualDatabase() {
        return this.vdbEditingContextImpl.getVirtualDatabase();
    }
    
    /**
     *  
     * @param vdb
     * @param wsdlOptions
     * @param problemMarker
     * @param problems
     * @since 4.2
     */
    private void validateTargetNamespaceUri(VirtualDatabase vdb, WsdlOptions wsdlOptions, List problemMarker, List problems) {

        final String targetNamespaceUri = wsdlOptions.getTargetNamespaceUri();
        if (targetNamespaceUri == null || targetNamespaceUri.trim().length() == 0) {
            // There is no target namespace URI
            final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.wsdlTargetNamespaceUriMissing"); //$NON-NLS-1$          
            //MyDefect : 14430 change the status from warning to error.
            setProblems(vdb, msg, IStatus.ERROR, IStatus.ERROR, problemMarker, problems);
        } else {
            // There is, so validate the URI ...
            try {
                new URI(targetNamespaceUri);
            } catch ( URISyntaxException e ) {
                final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.wsdlTargetNamespaceUriIsInvalid", targetNamespaceUri); //$NON-NLS-1$              
                setProblems(vdb, msg, IStatus.ERROR, IStatus.ERROR, problemMarker, problems);
            }
        }
    }
    
    /**
     *  
     * @param vdb
     * @param wsdlOptions
     * @param problemMarker
     * @param problems
     * @since 4.2
     */
    private void validateDefaultNamespaceUri(VirtualDatabase vdb, WsdlOptions wsdlOptions, List problemMarker, List problems) {
        
//      The default namespace SHOULD (not must) be the WSDL namespace (per WS-I)
        final String defaultNamespaceUri = wsdlOptions.getDefaultNamespaceUri();
        
        if (defaultNamespaceUri == null || defaultNamespaceUri.trim().length() == 0) {
            // There is no target namespace URI
            final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.wsdlDefaultNamespaceUriMissing"); //$NON-NLS-1$            
            setProblems(vdb, msg, IStatus.WARNING, IStatus.WARNING, problemMarker, problems);
        } else {
            // Compare to WSDL ...
            if (!WsdlPackage.eNS_URI.equals(defaultNamespaceUri)) {
                final String msg = VdbEditPlugin.Util.getString("VdbEditingContextImpl.wsdlDefaultNamespaceUriNotWsdlNamespace"); //$NON-NLS-1$                
                setProblems(vdb, msg, IStatus.WARNING, IStatus.WARNING, problemMarker, problems);
            }
        }
    }
    
    private void setProblems(VirtualDatabase vdb, String msg, int markerStatus, int problemStatus, 
                             List problemMarker, List problems) {
        problemMarker.add(new VdbEditingContextImplProblemMarker(vdb, markerStatus, msg, null));
        problems.add(new Status(problemStatus, VdbEditPlugin.PLUGIN_ID, 0, msg, null));
    }
}
