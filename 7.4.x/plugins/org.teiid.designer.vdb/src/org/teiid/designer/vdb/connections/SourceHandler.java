/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.vdb.connections;

import java.util.Properties;

import org.teiid.core.properties.PropertyDefinition;

/**
 * Interface intended to provide a mechanism for the org.teiid.designer.dqp plugin to contribute connection management to the
 * org.teiid.designer.vdb plugin. This keeps the VDB plugin a design-time plugin and the DQP a run-time plugin.
 * 
 * All the VDB needs is source connection "names" and not concrete connection objects, so the only interface method is findConnectioName()
 */
public interface SourceHandler {

    /**
     * This method provides a mechanism for Connection properties to be passed between non-dependent plugins for the purpose
     * of creating a <code>VdbSourceConnection</code> object containing data source name, translator name and
     * jndi connection name info.
     * @param sourceModelName the name of the source model. may not be null.
     * @param connectionProperties the connection <code>Properties</code> object. may not be null
     * 
     * @return the vdb source connection object
     * @throws Exception
     */
    VdbSourceConnection ensureVdbSourceConnection(String sourceModelName, Properties connectionProperties) throws Exception;

    /**
     * Method provides a means for the VDB Editor to get actions associated or applicable to a given object. The original
     * intent is for VdbModelEntry selections to return Select Translator or Select JNDI Name
     * 
     * @param iSelection
     * @return the list of applicable actions
     */
    Object[] getApplicableActions( Object iSelection );

    /**
     * @return the data source names (can be <code>null</code>)
     */
    String[] getDataSourceNames();

    /**
     * @return the translator types available on the current Teiid server that can be overridden (can be <code>null</code> or empty)
     */
    String[] getTranslatorTypes();

    /**
     * @param translatorName the name of the translator whose properties are being requested (may not be <code>null</code> or empty)
     * @return the properties (<code>null</code> if translator or properties could not be found)
     */
    PropertyDefinition[] getTranslatorDefinitions( String translatorName );

}
