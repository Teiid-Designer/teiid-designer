/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ant;

/**
 * Ant task that creates an Ant property containing the ID of the feature in which this task is run.
 */
public class FeatureIdPropertyTask extends AbstractFeatureTask {

    private String name;

    /**
     * {@inheritDoc}
     * 
     * @see tools.metamatrix.pakkage.ant.AbstractTask#run()
     */
    @Override
    protected void run() throws Exception {
        createProperty(name, InternalName.ID);
    }

    /**
     * @param name The property name to set.
     */
    public void setName( String name ) {
        this.name = name;
    }
}
