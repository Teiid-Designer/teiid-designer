/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.mapping.factory;

import java.util.Map;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.modeler.internal.mapping.factory.CompositorBasedBuilderStrategy;
import com.metamatrix.modeler.internal.mapping.factory.IterationBasedBuilderStrategy;
import com.metamatrix.modeler.mapping.PluginConstants;


/** 
 * MappingClassBuilderStrategy is an interface for the MappingClassBuilder extension point, to
 * provide different algorithms for generating mapping classes in virtual xml documents.
 * @since 5.0.1
 */
public interface MappingClassBuilderStrategy {

    public static final MappingClassBuilderStrategy compositorStrategy = new CompositorBasedBuilderStrategy();
    public static final MappingClassBuilderStrategy iterationStrategy = new IterationBasedBuilderStrategy();
   
    public static final String compositorStrategyDescription = PluginConstants.Util.getString("MappingClassBuilderStrategy.compositorStrategyDescription"); //$NON-NLS-1$
    public static final String iterationStrategyDescription = PluginConstants.Util.getString("MappingClassBuilderStrategy.iterationStrategyDescription"); //$NON-NLS-1$
    
    
    /**
     * Generate a Map of nodes in the specified IMappableTree where mapping classes should be
     * created.  The keys of the Map are XML Document nodes that should be the location of a Mapping
     * Class.  The values in the map is a Collection of XML Document nodes that should be mapped to
     * Mapping Class Columns inside the key node.
     * @param theTopNode The XML Document node that should begin this algorithm.
     * @param tree The IMappableTree that contains theTopNode.
     * @param mapper The ITreeToRelationalMapper 
     * @return a Map of Mapping Class locations and content. keys: XmlDocumentNode instances, values: 
     * Collection of XmlDocumentNode instances mapped into the MappingClassColumns of the Mapping Class. 
     * @since 4.3
     */
    public Map buildMappingClassMap(EObject theTopNode, IMappableTree tree, ITreeToRelationalMapper mapper);

}
