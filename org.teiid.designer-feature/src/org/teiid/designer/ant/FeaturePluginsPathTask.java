/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ant;

/**
 * Ant task that creates a path reference to the plug-ins to be built for the feature in which the task is being run.
 */
public class FeaturePluginsPathTask extends AbstractFeatureTask {

    private String name;

    /**
     * {@inheritDoc}
     * 
     * @see tools.metamatrix.pakkage.ant.AbstractTask#run()
     */
    @Override
    protected void run() throws Exception {
        createPath(name, InternalName.PLUGINS);
    }

    /**
     * @param name The property name to set.
     */
    public void setName( String name ) {
        this.name = name;
    }
}
