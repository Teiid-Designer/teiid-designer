/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.dqp.internal.workspace;

import static com.metamatrix.modeler.dqp.DqpPlugin.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.teiid.designer.runtime.TeiidTranslator;
import org.teiid.designer.runtime.ExecutionAdmin;
import org.teiid.designer.vdb.connections.VdbSourceConnection;

import com.metamatrix.core.util.CoreArgCheck;

/**
 * The binding between a model and one or more translators.
 */
public class SourceConnectionBinding {

    private final String modelName;
    private final String modelLocation;
    private final Set<TeiidTranslator> translators = new HashSet<TeiidTranslator>();
    
    private final List<VdbSourceConnection> vdbSources = new ArrayList<VdbSourceConnection>();

    /**
     * 
     * @param modelName the model name (never <code>null</code> or empty)
     * @param modelLocation the parent path of the model (never <code>null</code> or empty)
     * @param vdbSource the VdbSource object containing the source name, translator name and jndi name
     */
    public SourceConnectionBinding(String modelName, String modelLocation, VdbSourceConnection vdbSource ) {
    	super();
    	this.modelName = modelName;
    	this.modelLocation = modelLocation;
    	
    	addVdbSource(vdbSource);
    }
    
    /**
     * @param modelName the model name (never <code>null</code> or empty)
     * @param modelLocation the parent path of the model (never <code>null</code> or empty)
     * @param translator the translator added to the binding (never <code>null</code>)
     */
    public SourceConnectionBinding( String modelName,
                          String path,
                          TeiidTranslator translator ) {
        this(modelName, path, Collections.singleton(translator));
        CoreArgCheck.isNotNull(translator, "translator"); //$NON-NLS-1$
    }

    /**
     * @param modelName the model name (never <code>null</code> or empty)
     * @param modelLocation the parent path of the model (never <code>null</code> or empty)
     * @param translators the set of translators used in the model binding (never <code>null</code> or empty)
     * @throws IllegalArgumentException if translators are from different servers
     */
    public SourceConnectionBinding( String modelName,
                          String modelLocation,
                          Set<TeiidTranslator> translators ) {
        CoreArgCheck.isNotEmpty(modelName, "modelName"); //$NON-NLS-1$
        CoreArgCheck.isNotEmpty(modelLocation, "modelLocation"); //$NON-NLS-1$
        CoreArgCheck.isNotEmpty(translators, "translators"); //$NON-NLS-1$

        this.modelName = modelName;
        this.modelLocation = modelLocation;

        ExecutionAdmin admin = null;

        // make sure all translators from same server
        for (TeiidTranslator translator : translators) {
            if (translator == null) {
                throw new IllegalArgumentException(Util.getString("translatorCannotBeNullForSourceBinding")); //$NON-NLS-1$
            }
            if (admin == null) {
                admin = translator.getAdmin();
            } else if (admin != translator.getAdmin()) {
                throw new IllegalArgumentException(Util.getString("sourceBindingWithTranslatorsFromDifferentServers")); //$NON-NLS-1$
            }

            this.translators.add(translator);
        }
    }

    /**
     * @param translator the translator being added to the model binding (never <code>null</code>)
     * @return <code>true</code> if the translator was added
     * @throws IllegalArgumentException if translator being added is from a different server
     */
    public boolean addTranslator( TeiidTranslator translator ) {
        CoreArgCheck.isNotNull(translator, "translator"); //$NON-NLS-1$

        // make sure server is the same
        if (this.translators.iterator().next().getAdmin() != translator.getAdmin()) {
            throw new IllegalArgumentException(Util.getString("sourceBindingWithTranslatorsFromDifferentServers")); //$NON-NLS-1$
        }

        return this.translators.add(translator);
    }

    /**
     * @return an unmodifiable collection of translators (never <code>null</code> or empty)
     * @since 7.0
     */
    public Set<TeiidTranslator> getTranslators() {
        return Collections.unmodifiableSet(this.translators);
    }

    /**
     * @return the model name (never <code>null</code> or empty)
     * @since 7.0
     */
    public String getModelName() {
        return this.modelName;
    }

    /**
     * @return the parent path of the model (never <code>null</code> or empty)
     * @since 7.0
     */
    public String getModelLocation() {
        return this.modelLocation;
    }

    /**
     * @param translator the translator being removed from the model binding (never <code>null</code>)
     * @throws IllegalArgumentException if trying to remove the last translator in the model binding or if removing a translator
     *         that is not part of this model binding
     */
    public void removeTranslator( TeiidTranslator translator ) {
        CoreArgCheck.isNotNull(translator, "translator"); //$NON-NLS-1$

        // don't allow last translator to be removed
        if ((this.translators.size() == 1) && this.translators.contains(translator)) {
            throw new IllegalArgumentException(Util.getString("lastTranslatorOfSourceBindingCannotBeRemoved")); //$NON-NLS-1$
        }

        // error if translator not found
        if (!this.translators.remove(translator)) {
            throw new IllegalArgumentException(Util.getString("translatorToRemoveNotFoundInSourceBinding", translator.getName(), this.modelName)); //$NON-NLS-1$
        }
    }
    
    public void addVdbSource(VdbSourceConnection vdbSource) {
    	this.vdbSources.add(vdbSource);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("model: " + this.getModelName());//$NON-NLS-1$
        sb.append(", path: " + this.getModelLocation());//$NON-NLS-1$
        sb.append(", translators: ");//$NON-NLS-1$

        int count = this.translators.size();
        int i = 1;

        for (TeiidTranslator translator : this.translators) {
            sb.append(translator.getName());

            if (i < count) {
                sb.append(", "); //$NON-NLS-1$
            }

            ++i;
        }

        return sb.toString();
    }

}
