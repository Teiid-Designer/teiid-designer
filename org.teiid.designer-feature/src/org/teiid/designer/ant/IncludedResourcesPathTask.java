/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ant;

/**
 * 
 */
public class IncludedResourcesPathTask extends AbstractPluginTask {

    private String name;

    /**
     * {@inheritDoc}
     * 
     * @see tools.metamatrix.pakkage.ant.AbstractTask#run()
     */
    @Override
    protected void run() throws Exception {
        createPath(name, "bin.includes"); //$NON-NLS-1$
    }

    /**
     * @param name The path name to set.
     */
    public void setName( String name ) {
        this.name = name;
    }
}
