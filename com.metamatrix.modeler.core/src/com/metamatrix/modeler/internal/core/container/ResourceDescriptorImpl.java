/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.container;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.osgi.framework.Bundle;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.container.ResourceDescriptor;

/**
 * ResourceDescriptorImpl
 */
public class ResourceDescriptorImpl implements ResourceDescriptor {

    private Bundle factoryBundle;
    private String factoryClassName;
    private final String uniqueID;
    private final List protocols;
    private final List extensions;

    public ResourceDescriptorImpl( final String uniqueID ) {
        Assertion.isNotNull(uniqueID);
        Assertion.isNotZeroLength(uniqueID);
        this.uniqueID = uniqueID;
        this.protocols = new ArrayList();
        this.extensions = new ArrayList();
    }

    /**
     * Return the unique identifier of the resource type.
     * @return the unique identifier
     */
    public String getUniqueIdentifier() {
        return uniqueID;
    }

    /**
     * @return
     */
    public List getExtensions() {
        return extensions;
    }

    /**
     * @return
     */
    public List getProtocols() {
        return protocols;
    }

    /**
     * @see com.metamatrix.api.mtk.core.MetamodelDescriptor#getFactoryClass()
     */
    public Resource.Factory getResourceFactory() throws ModelerCoreException {
        try {
            final Class factoryClass = this.factoryBundle.loadClass(this.factoryClassName);
            return (Resource.Factory) factoryClass.newInstance();
        } catch (ClassNotFoundException e) {
            final Object[] params = new Object[] {this.factoryClassName, this.factoryBundle};
            throw new ModelerCoreException(e,
			                               ModelerCore.Util.getString("ResourceDescriptorImpl.Unable_to_load_class_using_bundle", //$NON-NLS-1$
			                                                          params));
        } catch (InstantiationException e) {
            final Object[] params = new Object[] {this.factoryClassName, this.factoryBundle};
            throw new ModelerCoreException(
			                               e,
			                               ModelerCore.Util.getString("ResourceDescriptorImpl.Unable_to_instantiate_class_using_bundle", //$NON-NLS-1$
			                                                          params));
        } catch (IllegalAccessException e) {
            final Object[] params = new Object[] {this.factoryClassName, this.factoryBundle};
            throw new ModelerCoreException(
			                               e,
			                               ModelerCore.Util.getString("ResourceDescriptorImpl.Unable_to_instantiate_and_access_class_using_bundle", //$NON-NLS-1$
			                                                          params));
        }
    }

    public void setResourceFactoryClass(final String className) {
        this.setResourceFactoryClass(className,null);
    }

    public void setResourceFactoryClass( final String className,
	                                     final Bundle bundle ) {
        if(className == null){
            ArgCheck.isNotNull(className,ModelerCore.Util.getString("ResourceDescriptorImpl.The_class_name_string_may_not_be_null")); //$NON-NLS-1$
        }

        if (bundle != null) {
			this.factoryBundle = bundle;
        }
        this.factoryClassName = className;
    }

    protected String getResourceFactoryClassName() {
        return this.factoryClassName;
    }

    public static void register(final ResourceDescriptor resourceDescriptor, final ResourceSet resourceSet ) throws ModelerCoreException {
        final List protocols = resourceDescriptor.getProtocols();
        final List fileExtensions = resourceDescriptor.getExtensions();
        final String factoryExtensionID = resourceDescriptor.getUniqueIdentifier();

        if ( protocols.size() != 0 || fileExtensions.size() != 0 ) {
            try {
                final Resource.Factory factory = resourceDescriptor.getResourceFactory();   // may throw exception
                final String factoryClassName = factory.getClass().getName();

                // Get references to useful things ...
                final Resource.Factory.Registry registry = resourceSet.getResourceFactoryRegistry();
                final Map extensionToFactoryMap = registry.getExtensionToFactoryMap();
                final Map protocolToFactoryMap = registry.getProtocolToFactoryMap();

                // Add the factory for each of the protocols ...
                final Iterator iter = protocols.iterator();
                while (iter.hasNext()) {
                    final String protocol = (String) iter.next();
                    if ( ModelerCore.DEBUG ) {
                        ModelerCore.Util.log(IStatus.INFO,ModelerCore.Util.getString("ResourceDescriptorImpl.DEBUG.Registering_resource_factory_for_URI_protocol",factoryClassName,protocol)); //$NON-NLS-1$
                    }
                    final Object prevFactory = protocolToFactoryMap.put(protocol,factory);
                    if ( prevFactory != null && prevFactory.getClass() != factory.getClass() ) {
                        ModelerCore.Util.log(IStatus.WARNING,ModelerCore.Util.getString("ResourceDescriptorImpl.Replaced_resource_factory_for_URI_protocol",prevFactory.getClass().getName(),factoryClassName,protocol)); //$NON-NLS-1$
                    }
                }

                // Add the factory for each of the extensions ...
                final Iterator iter2 = fileExtensions.iterator();
                while (iter2.hasNext()) {
                    final String fileExtension = (String) iter2.next();
                    if ( ModelerCore.DEBUG ) {
                        ModelerCore.Util.log(IStatus.INFO,ModelerCore.Util.getString("ResourceDescriptorImpl.DEBUG.Registering_resource_factory_for_URI_extension",factoryClassName,fileExtension)); //$NON-NLS-1$
                    }
                    final Object prevFactory = extensionToFactoryMap.put(fileExtension,factory);
                    if ( prevFactory != null && prevFactory.getClass() != factory.getClass() ) {
                        ModelerCore.Util.log(IStatus.WARNING,ModelerCore.Util.getString("ResourceDescriptorImpl.Replaced_resource_factory_for_URI_extension",prevFactory.getClass().getName(),factoryClassName,fileExtension)); //$NON-NLS-1$
                    }
                }
            } catch ( CoreException e ) {
                final String msg = ModelerCore.Util.getString("ResourceDescriptorImpl.Error_while_loading_resource_factory_extension",factoryExtensionID); //$NON-NLS-1$
                throw new ModelerCoreException(msg);
            }
        }
    }


    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        final ResourceDescriptorImpl that = (ResourceDescriptorImpl)obj;
        if ( !this.uniqueID.equals(that.uniqueID) ) {
            return false;
        }
        if ( !this.factoryClassName.equals(that.factoryClassName) ) {
            return false;
        }
        if ( !this.protocols.containsAll(that.protocols) || !that.protocols.containsAll(this.protocols) ) {
            return false;
        }
        if ( !this.extensions.containsAll(that.extensions) || !that.extensions.containsAll(this.extensions) ) {
            return false;
        }
        if (!this.factoryBundle.equals(that.factoryBundle)) {
            return false;
        }
        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return this.uniqueID.hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("ResourceDescriptor "); //$NON-NLS-1$
        sb.append(this.uniqueID);
        sb.append(" protocols=["); //$NON-NLS-1$
        boolean first = true;
        for (Iterator iter = this.protocols.iterator(); iter.hasNext();) {
            if ( !first ) {
                sb.append(',');
            }
            sb.append(iter.next());
            first = false;
        }
        sb.append("], extensions=["); //$NON-NLS-1$
        first = true;
        for (Iterator iter = this.extensions.iterator(); iter.hasNext();) {
            if ( !first ) {
                sb.append(',');
            }
            sb.append(iter.next());
            first = false;
        }
        sb.append(']');
        return sb.toString();
    }

}

