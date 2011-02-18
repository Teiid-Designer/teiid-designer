/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.webservice;

import java.util.Iterator;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xsd.XSDDiagnosticSeverity;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.impl.XSDDiagnosticImpl;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.modeler.webservice.IWebServiceModelBuilder;
import com.metamatrix.modeler.webservice.IWebServiceXsdResource;
import com.metamatrix.modeler.webservice.WebServicePlugin;


/** 
 * @since 4.2
 */
public class WebServiceXsdResource implements IWebServiceXsdResource, IInternalWebServiceXsdResource {

    private final String targetNamespace;
    private final String originalPath;
    private IPath destinationPath;
    private final Validator validator;
    private final XSDSchema schema;
    
    /** 
     * 
     * @since 4.2
     */
    public WebServiceXsdResource( final XSDSchema schema, final String targetNamespace, final String originalPath ) {
        this(schema,targetNamespace,originalPath,null);
    }

    /** 
     * 
     * @since 4.2
     */
    public WebServiceXsdResource( final XSDSchema schema, final String targetNamespace, final String originalPath, final Validator validator ) {
        super();
        CoreArgCheck.isNotNull(schema);
        this.schema = schema;
        this.targetNamespace = targetNamespace;
        this.originalPath = originalPath;
        this.validator = validator != null ? validator : new WorkspaceValidator();
    }
    
    public XSDSchema getSchema() {
        return this.schema;
    }

    /** 
     * @see com.metamatrix.modeler.webservice.IWebServiceXsdResource#getTargetNamespace()
     * @since 4.2
     */
    public String getTargetNamespace() {
        return this.targetNamespace;
    }

    /** 
     * @see com.metamatrix.modeler.webservice.IWebServiceXsdResource#getOriginalPath()
     * @since 4.2
     */
    public String getOriginalPath() {
        return this.originalPath;
    }

    /** 
     * @see com.metamatrix.modeler.webservice.IWebServiceXsdResource#getDestinationPath()
     * @since 4.2
     */
    public IPath getDestinationPath() {
        return this.destinationPath;
    }

    /** 
     * @see com.metamatrix.modeler.webservice.IWebServiceXsdResource#setDestinationPath(org.eclipse.core.runtime.IPath)
     * @since 4.2
     */
    public void setDestinationPath( final IPath workspacePathForXsd) {
        this.destinationPath = workspacePathForXsd;
    }

    /** 
     * @see com.metamatrix.modeler.webservice.IWebServiceXsdResource#isValid()
     * @since 4.2
     */
    public IStatus isValid() {
                
        IStatus stResult = null;
        IStatus stSchemaStatus = null;
        IStatus stValidDestination = this.validator.isValid(this.getDestinationPath());
        
        // check to see if schema has errors
        EList lstDiags = getSchema().getDiagnostics();
        if( lstDiags != null
         && lstDiags.size() > 0 ) {
            Iterator it = lstDiags.iterator();
            
            while( it.hasNext() ) {
                XSDDiagnosticImpl diag = (XSDDiagnosticImpl)it.next();
                if ( diag.getSeverity().getValue() == XSDDiagnosticSeverity.ERROR 
                  || diag.getSeverity().getValue() == XSDDiagnosticSeverity.FATAL ) {
                    String sMessage = diag.getMessage();
                    
                    // if schema has errors, create an error IStatus for it
                    stSchemaStatus 
                        = new Status( IStatus.ERROR, 
                                      WebServicePlugin.PLUGIN_ID, 
                                      IWebServiceModelBuilder.UNRESOLVED_SCHEMA_IMPORT,
                                      sMessage, 
                                      null );
                }
            }            
        }
        
        // resolve the status objects down to one
        if ( stValidDestination != null &&  stSchemaStatus != null ) {

                IStatus[] errors = new IStatus[ 2 ];
        
                errors[ 0 ] = stValidDestination;
                errors[ 1 ] = stSchemaStatus;
                stResult 
                    = new MultiStatus( WebServicePlugin.PLUGIN_ID, 
                                       IWebServiceModelBuilder.UNRESOLVED_SCHEMA_IMPORT, 
                                       errors, 
                                       stSchemaStatus.getMessage(), 
                                       null );
        } 
        else 
        if ( stValidDestination != null ) {
            stResult = stValidDestination;
        }
        else 
        if ( stSchemaStatus != null ) {
            stResult = stSchemaStatus;
        }
        
        return stResult;
    }
    
    /** 
     * @see com.metamatrix.modeler.webservice.IWebServiceXsdResource#isValid(org.eclipse.core.runtime.IPath)
     * @since 4.2
     */
    public IStatus isValid(final IPath proposedDestination) {
        return this.validator.isValid(proposedDestination);
    }
    
    public interface Validator {
        IStatus isValid(final IPath destPath);
    }
    
    public class WorkspaceValidator implements Validator {
        public IStatus isValid(final IPath destPath) {
            return WebServiceXsdResource.isValidInWorkspace(destPath);
        }
    }

    public static IStatus isValidInWorkspace(final IPath destPath) {
        if ( destPath == null ) {
            final String msg = WebServicePlugin.Util.getString("WebServiceXsdResource.LocationInWorkspaceRequired"); //$NON-NLS-1$
            return new Status(IStatus.ERROR,WebServicePlugin.PLUGIN_ID,0,msg,null);
        }
        
        // See if an IResource already exists at that location ...
        if ( destPath.segmentCount() == 0 ) {
            final Object[] params = new Object[] {destPath.toString()};
            final String msg = WebServicePlugin.Util.getString("WebServiceXsdResource.PathNotValid",params); //$NON-NLS-1$
            return new Status(IStatus.ERROR,WebServicePlugin.PLUGIN_ID,0,msg,null);
        }
        
        // Make sure the path has at least 2 segments (at least 1 project and 1 file segment) ...
        if ( destPath.segmentCount() == 1 ) {
            final Object[] params = new Object[] {destPath.toString()};
            final String msg = WebServicePlugin.Util.getString("WebServiceXsdResource.PathSpecifiesProject",params); //$NON-NLS-1$
            return new Status(IStatus.ERROR,WebServicePlugin.PLUGIN_ID,0,msg,null);
        }
        
        // See if an IResource already exists at that location ...
        IResource existing = ResourcesPlugin.getWorkspace().getRoot().findMember(destPath, false);
        if ( existing != null && existing.exists() ) {
            if ( existing instanceof IFolder ) {
                final Object[] params = new Object[] {destPath.toString()};
                final String msg = WebServicePlugin.Util.getString("WebServiceXsdResource.PathSpecifiesFolder",params); //$NON-NLS-1$
                return new Status(IStatus.ERROR,WebServicePlugin.PLUGIN_ID,0,msg,null);
            } else if ( existing instanceof IFile ) {
                // Check the type ...
                if ( "xsd".equalsIgnoreCase(existing.getFullPath().getFileExtension()) ) { //$NON-NLS-1$
                    final Object[] params = new Object[] {destPath.toString()};
                    final String msg = WebServicePlugin.Util.getString("WebServiceXsdResource.PathSpecifiesExistingXsdWillBeOverwritten",params); //$NON-NLS-1$
                    return new Status(IStatus.WARNING,WebServicePlugin.PLUGIN_ID,0,msg,null);
                }
                final Object[] params = new Object[] {destPath.toString()};
                final String msg = WebServicePlugin.Util.getString("WebServiceXsdResource.PathSpecifiesExistingNonXsd",params); //$NON-NLS-1$
                return new Status(IStatus.ERROR,WebServicePlugin.PLUGIN_ID,0,msg,null);
            }
        }
        
        // Check the extension ...
        if ( !"xsd".equalsIgnoreCase(destPath.getFileExtension()) ) { //$NON-NLS-1$
            final Object[] params = new Object[] {destPath.toString()};
            final String msg = WebServicePlugin.Util.getString("WebServiceXsdResource.PathMustHaveAnXsdExtension",params); //$NON-NLS-1$
            return new Status(IStatus.ERROR,WebServicePlugin.PLUGIN_ID,0,msg,null);
        }
        
        
        // Else it doesn't exist ...
        final Object[] params = new Object[] {destPath.toString()};
        final String msg = WebServicePlugin.Util.getString("WebServiceXsdResource.XsdWillBePlacedAt",params); //$NON-NLS-1$
        return new Status(IStatus.OK,WebServicePlugin.PLUGIN_ID,0,msg,null);
    }
}
