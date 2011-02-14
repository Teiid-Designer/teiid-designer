/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime;

/**
 * 
 */
public interface HostProvider {

    /**
     * The default connection host. Value is {@value}.
     */
    String DEFAULT_HOST = "localhost"; //$NON-NLS-1$
    
    /**
     * A <code>HostProvider</code> that provides the default host.
     */
    DefaultHostProvider DEFAULT_HOST_PROVIDER = new DefaultHostProvider();
    
    /**
     * @return the host (never <code>null</code>)
     */
    String getHost();
    
    /**
     * The <code>DefaultHostProvider</code> provides the default host.
     * @see HostProvider#DEFAULT_HOST
     */
    class DefaultHostProvider implements HostProvider {
        /**
         * {@inheritDoc}
         *
         * @see org.teiid.designer.runtime.ui.HostProvider#getHost()
         */
        @Override
        public String getHost() {
            return DEFAULT_HOST;
        }
    }

}
