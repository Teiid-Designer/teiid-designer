/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.webservice;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.teiid.core.designer.HashCodeUtil;


/** 
 * @since 8.0
 */
public abstract class AbstractWebServiceResource implements IWebServiceResource {

    private final String fullPath;
    private final String namespace;
    
    private final Set referencingResources;
    private final Set referencedResources;
    private final Set resolvedByThis;
    
    private IWebServiceResource resolvedResource;
    
    /** 
     * 
     * @since 4.2
     */
    protected AbstractWebServiceResource( final String namespace, final String fullPath ) {
        super();
        this.referencedResources = new HashSet();
        this.referencingResources = new HashSet();
        this.resolvedByThis = new HashSet();
        this.fullPath = fullPath != null && fullPath.trim().length() != 0 ? fullPath : null;
        this.namespace = namespace;
    }


    protected abstract boolean exists();

    protected abstract File doGetFile();

    protected abstract InputStream doGetRawInputStream() throws Exception;
    
    
    /** 
     * @see org.teiid.designer.webservice.IWebServiceResource#getFullPath()
     * @since 4.2
     */
    @Override
	public String getFullPath() {
        if ( this.fullPath != null && isResolvedToSelf() ) {
            return this.fullPath;
        }
        if ( this.getResolvedResource() != null ) {
            return this.getResolvedResource().getFullPath();
        }
        return null;
    }
    
    
    /** 
     * @see org.teiid.designer.webservice.IWebServiceResource#getFile()
     * @since 4.2
     */
    @Override
	public File getFile() {
        if ( this.resolvedResource != null ) {
            return this.getResolvedResource().getFile();
        }
        return doGetFile();
    }
    
    /** 
     * @see org.teiid.designer.webservice.IWebServiceResource#getNamespace()
     * @since 4.2
     */
    @Override
	public String getNamespace() {
        return namespace;
    }

    /** 
     * @see org.teiid.designer.webservice.IWebServiceResource#isWsdl()
     * @since 4.2
     */
    @Override
	public boolean isWsdl() {
        final String path = this.getFullPath();
        if ( path != null && path.length() > EXTENSION_WSDL.length() ) {
            if ( path.toLowerCase().endsWith("." + EXTENSION_WSDL) ) { //$NON-NLS-1$
                return true;
            }
        }
        return false;
    }
    
    
    /** 
     * @see org.teiid.designer.webservice.IWebServiceResource#getInputStream()
     * @since 4.2
     */
    @Override
	public InputStream getInputStream() throws Exception {
        if ( this.isResolvedToSelf() ) {
            if ( this.exists() ) {
                final InputStream rawStream = doGetRawInputStream();
                return new BufferedInputStream(rawStream);
            }
            return null;
        }
        if ( this.resolvedResource != null ) {
            return this.resolvedResource.getInputStream();
        }
        return null;
    }

    /** 
     * @see org.teiid.designer.webservice.IWebServiceResource#isXsd()
     * @since 4.2
     */
    @Override
	public boolean isXsd() {
        final String path = this.getFullPath();
        if ( path != null && path.length() > EXTENSION_XSD.length() ) {
            if ( path.toLowerCase().endsWith("." + EXTENSION_XSD) ) { //$NON-NLS-1$
                return true;
            }
        }
        return false;
    }

    /** 
     * @see org.teiid.designer.webservice.IWebServiceResource#getReferencingResources()
     * @since 4.2
     */
    @Override
	public Collection getReferencingResources() {
        return this.referencingResources;
    }

    /** 
     * @see org.teiid.designer.webservice.IWebServiceResource#getReferencedResources()
     * @since 4.2
     */
    @Override
	public Collection getReferencedResources() {
        return this.referencedResources;
    }
    
    public synchronized void removeFromAllReferencers() {
        final Iterator iter = getReferencingResources().iterator();
        while (iter.hasNext()) {
            final AbstractWebServiceResource referencing = (AbstractWebServiceResource)iter.next();
            referencing.removeReferencedResource(this);
        }
    }
    
    public synchronized boolean removeReferencedResource( final IWebServiceResource referenced ) {
        if ( referenced == null ) {
            return false;
        }
        if ( referenced == this ) {
            return false;
        }
        final boolean removedReferenced = this.referencedResources.remove(referenced);
        if ( removedReferenced && referenced instanceof AbstractWebServiceResource ) {
            ((AbstractWebServiceResource)referenced).referencingResources.remove(this);
        }
        return removedReferenced;
    }
    
    public synchronized boolean addReferencedResource( final IWebServiceResource referenced ) {
        if ( referenced == null ) {
            return false;
        }
        if ( referenced == this ) {
            return false;
        }
        // Make sure there is no cirularity ...
        final boolean directOrIndirect = this.isReferencedDirectlyOrIndirectly(referenced);
        if ( directOrIndirect ) {
            return false;
        }
        
        this.referencedResources.add(referenced);
        if ( referenced instanceof AbstractWebServiceResource ) {
            ((AbstractWebServiceResource)referenced).referencingResources.add(this);
        }
        return true;
    }
    
    protected boolean isReferencedDirectlyOrIndirectly( final IWebServiceResource referenced ) {
        final Iterator iter = this.referencedResources.iterator();
        while (iter.hasNext()) {
            final AbstractWebServiceResource directRef = (AbstractWebServiceResource)iter.next();
            if ( directRef.equals(referenced) ) {
                return true;
            }

            // See if the direct references the supplied
            final boolean indirect = directRef.isReferencedDirectlyOrIndirectly(referenced);
            if ( indirect ) {
                return true;
            }
        }
        return false;
    }

    /** 
     * @see org.teiid.designer.webservice.IWebServiceResource#getStatus()
     * @since 4.2
     */
    @Override
	public IStatus getStatus() {
        // Check that this is resolved ...
        if ( this.isResolvedToSelf() ) {
            final Object[] params = new Object[] {this.getNamespace(),this.getFullPath()};
            final String msg = WebServicePlugin.Util.getString("AbstractWebServiceResource.NamespaceIsResolvedToSelf",params); //$NON-NLS-1$
            return new Status(IStatus.OK,WebServicePlugin.PLUGIN_ID,STATUS_RESOLVED_TO_SELF,msg,null);
        }
        if ( this.isResolved() ) {
            final Object[] params = new Object[] {this.getNamespace(),this.getFullPath()};
            final String msg = WebServicePlugin.Util.getString("AbstractWebServiceResource.NamespaceIsResolved",params); //$NON-NLS-1$
            return new Status(IStatus.OK,WebServicePlugin.PLUGIN_ID,STATUS_RESOLVED,msg,null);
        }
        // It's not resolved at all, but maybe there is a path that is invalid ...
        if ( this.getFullPath() != null ) {
            final Object[] params = new Object[] {this.getNamespace(),this.getFullPath()};
            final String msg = WebServicePlugin.Util.getString("AbstractWebServiceResource.NamespaceIsResolvedToNonExistantFile",params); //$NON-NLS-1$
            return new Status(IStatus.ERROR,WebServicePlugin.PLUGIN_ID,STATUS_RESOLVED_PATH_DOESNT_EXIST,msg,null);
        }
        // There is no path at all
        final Object[] params = new Object[] {this.getNamespace()};
        final String msg = WebServicePlugin.Util.getString("AbstractWebServiceResource.NamespaceIsUnresolved",params); //$NON-NLS-1$
        return new Status(IStatus.ERROR,WebServicePlugin.PLUGIN_ID,STATUS_UNRESOLVED,msg,null);
    }
    
    /** 
     * @see org.teiid.designer.webservice.IWebServiceResource#isResolved()
     * @since 4.2
     */
    @Override
	public boolean isResolved() {
        if ( isResolvedToSelf() ) {
            return true;
        }
        if ( this.resolvedResource == null ) {
            // If this is not resolved by another resource, then this resource must exist to be resolved
            return this.exists();
        }
        
        // This is resolved by another resource, so simply delegate ...
        return this.resolvedResource.isResolved();
    }

    /** 
     * @see org.teiid.designer.webservice.IWebServiceResource#isResolvedToSelf()
     * @since 4.2
     */
    @Override
	public boolean isResolvedToSelf() {
        return resolvedResource == null && this.exists();
    }

    /** 
     * @see org.teiid.designer.webservice.IWebServiceResource#getResolvedResource()
     * @since 4.2
     */
    @Override
	public IWebServiceResource getResolvedResource() {
        return exists() && resolvedResource == null ? this : resolvedResource;
    }
    
    
    /** 
     * @see org.teiid.designer.webservice.IWebServiceResource#getResourcesResolved()
     * @since 4.2
     */
    @Override
	public Collection getResourcesResolved() {
        return this.resolvedByThis;
    }
    
    @Override
	public IWebServiceResource getLastResolvedResource() {
        IWebServiceResource result = this;
        while ( result != null ) {
            final IWebServiceResource resolved = result.getResolvedResource();
            if ( resolved == null || resolved == result) {
                return resolved;
            }
            result = result.getResolvedResource();
        }
        return null;
    }
    
    /** 
     * @see org.teiid.designer.webservice.IWebServiceResource#setResolvedResource(org.teiid.designer.webservice.IWebServiceResource)
     * @since 4.2
     */
    @Override
	public boolean setResolvedResource( final IWebServiceResource resource ) {
        if ( resource == this.resolvedResource ) {
            return true;    // do nothing ...
        }
        
        // Remove from existing ...
        if ( this.resolvedResource instanceof AbstractWebServiceResource) {
            ((AbstractWebServiceResource)this.resolvedResource).resolvedByThis.remove(this);
        }
        
        
        if ( resource == null ) {
            this.resolvedResource = null;
            return true;
        }
        if ( resource == this ) {
            // If setting to itself, then it must exist ...
            if ( this.exists() ) {
                this.resolvedResource = this;
                return true;
            }
            return false;
        }
        this.resolvedResource = resource;
        if ( this.resolvedResource instanceof AbstractWebServiceResource) {
            ((AbstractWebServiceResource)this.resolvedResource).resolvedByThis.add(this);
        }
        return true;
    }
    
    /** 
     * @see java.lang.Object#equals(java.lang.Object)
     * @since 4.2
     */
    @Override
    public boolean equals(Object obj) {
        if ( obj == null ) {
            return false;
        }
        if ( obj == this ) {
            return true;
        }
        
        // Instances must be of the same class
        if ( !obj.getClass().equals(this.getClass()) ) {
            return false;
        }
        final AbstractWebServiceResource that = (AbstractWebServiceResource)obj;
        
        // Check the namespaces ...
        if ( !this.getNamespace().equals(that.getNamespace()) ) {
            return false;
        }
        
        // Namespaces are the same; check whether they both exist
        if ( this.exists() != that.exists() ) {
            return false;       // one exists, and one doesn't
        }
        
        // Namespaces are same and they both exist; check that the full paths are the same ...
        final String thisPath = this.getFullPath();
        final String thatPath = that.getFullPath();
        if ( thisPath == null ) {
            if ( thatPath != null ) {
                return false;
            }
            // Otherwise, both are null
        } else {
            if ( thatPath == null ) {
                return false;
            }
            // Otherwise, both are non-null
            if ( !this.getFullPath().equals(that.getFullPath()) ) {
                return false;       // different locations
            }
        }
        
        // Namespaces and full paths are identical, and both exist directly.  Therefore,
        // they are two objects that represent the exact same resource
        return true;
        
    }
    
    
    /** 
     * @see java.lang.Object#hashCode()
     * @since 4.2
     */
    @Override
    public int hashCode() {
        int hc = 0;   // or = super.hashCode();
        hc = HashCodeUtil.hashCode(hc, this.namespace);
        hc = HashCodeUtil.hashCode(hc, this.getFullPath());
        return hc;
    }

    
    /** 
     * @see java.lang.Object#toString()
     * @since 4.2
     */
    @Override
    public String toString() {
        if ( this.isResolvedToSelf() ) {
            return this.getNamespace() + "->self->" + this.getFullPath(); //$NON-NLS-1$
        }
        return this.getNamespace() + "->" + this.getFullPath(); //$NON-NLS-1$
    }
}
