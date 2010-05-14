/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.relational.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.metamodels.relational.RelationalEntity;
import com.metamatrix.modeler.jdbc.metadata.JdbcNode;
import com.metamatrix.modeler.jdbc.relational.JdbcRelationalPlugin;

/**
 * This ObjectMatcher class can be used to find the {@link EObject model object} that best matches a set of {@link JdbcNode
 * JdbcNode database objects}. Generally, a single instance of this class is created and used to match all of the JdbcNode
 * instances to the {@link EObject objects} in a model, but the {@link #findBestMatches(List, List)} method is invoked once with
 * the children from each model object and the children from the corresponding database object. This is because the
 * {@link #findBestMatches(List, List)} method only compares the name (rather than the full name or path to the object).
 */
public class ObjectMatcher {

    /**
     * Default implementation of {@link MatchValueProvider} that simply performs a {@link Object#toString() toString()} on the
     * supplied object.
     */
    static MatchValueProvider DEFAULT_MATCH_VALUE_PROVIDER = new MatchValueProvider() {
        public String getMatchValue( final Object obj ) {
            CoreArgCheck.isNotNull(obj);
            return obj.toString();
        }
    };

    /**
     * Implementation of {@link MatchValueProvider} that returns the name of a {@link JdbcNode} object. This method assumes the
     * supplied object is an instance of {@link JdbcNode}.
     */
    static MatchValueProvider JDBC_NODE_VALUE_PROVIDER = new MatchValueProvider() {
        public String getMatchValue( final Object obj ) {
            CoreArgCheck.isNotNull(obj);
            return ((JdbcNode)obj).getName();
        }
    };

    /**
     * Implementation of {@link MatchValueProvider} that returns the name of a {@link RelationalEntity} object. This method
     * assumes the supplied object is an instance of {@link RelationalEntity}.
     */
    static MatchValueProvider RELATION_OBJECT_NAME_PROVIDER = new MatchValueProvider() {
        public String getMatchValue( final Object obj ) {
            CoreArgCheck.isNotNull(obj);
            return ((RelationalEntity)obj).getName();
        }
    };

    private final Map destination;
    private final LinkedList unmatchedModelObjects;
    private final LinkedList unmatchedNodes;
    private MatchValueProvider modelObjectNameProvider;
    private MatchValueProvider jdbcNodeNameProvider;

    /**
     * Construct an instance of MatchUtil.
     */
    public ObjectMatcher( final Map destination ) {
        super();
        this.destination = (destination != null ? destination : new HashMap());
        this.unmatchedModelObjects = new LinkedList();
        this.unmatchedNodes = new LinkedList();
        this.modelObjectNameProvider = RELATION_OBJECT_NAME_PROVIDER;
        this.jdbcNodeNameProvider = JDBC_NODE_VALUE_PROVIDER;
    }

    /**
     * Find the best matches between the supplied lists of {@link JdbcNode} instances and {@link RelationalEntity} instances. Each
     * time a match between a JdbcNode and RelationalEntity, the pair are inserted into the {@link #getDestination() destination
     * map} with the JdbcNode as the key and the RelationalEntity as the value.
     * <p>
     * Any JdbcNode instances for which no matching RelationalEntity can be found are placed in the {@link #getUnmatchedNodes()
     * unmatched nodes} list. Similarly, any RelationalEntity instances for which no matching JdbcNode cannot be found are placed
     * in the {@link #getUnmatchedNodes() unmatched nodes} list.
     * </p>
     * <p>
     * This method can be called repeatedly on the same instance.
     * </p>
     * 
     * @param jdbcNodes the list of JdbcNode instances; may not be null
     * @param modelObjects the list of RelationalEntity instances; may not be null
     */
    public void findBestMatches( final List jdbcNodes,
                                 final List modelObjects ) {
        // See if we can quit quickly ...
        if (jdbcNodes.isEmpty()) {
            // Put all of the model objects in the unmatched list ...
            this.unmatchedModelObjects.addAll(modelObjects);
            return;
        }
        if (modelObjects.isEmpty()) {
            // Put all of the nodes in the unmatched list ...
            this.unmatchedNodes.addAll(jdbcNodes);
            return;
        }

        final List tempUnmatchedNodes = new LinkedList();
        final List tempUnmatchedObjs = new LinkedList();

        // -------------------------------------------------------------------------
        // Find exact matches first
        // -------------------------------------------------------------------------
        // Theoretically, any time a model is refreshed/updated, the names should not
        // differ by case. Therefore, by doing a case-sensitive match first, we're
        // most likely to find the majority of the matches.

        process(true, jdbcNodes, modelObjects, tempUnmatchedNodes, tempUnmatchedObjs);

        // -------------------------------------------------------------------------
        // Find case-insensitive matches next
        // -------------------------------------------------------------------------
        process(false, tempUnmatchedNodes, tempUnmatchedObjs, this.unmatchedNodes, this.unmatchedModelObjects);

    }

    /**
     * Method used to determine whether the supplied {@link JdbcNode JdbcNode object} has a type that matches the supplied
     * {@link RelationalEntity model object}.
     * 
     * @param node
     * @param modelObject
     * @return
     */
    protected boolean isMatchingType( final Object node,
                                      final Object modelObject ) {
        if (node instanceof JdbcNode && modelObject instanceof RelationalEntity) {
            final JdbcNode theNode = (JdbcNode)node;
            final RelationalEntity entity = (RelationalEntity)modelObject;
            final EClass entityEClass = entity.eClass();
            final EClass nodeEClass = JdbcRelationalPlugin.getJdbcNodeToRelationalMapping().getRelationalClassForJdbcNode(theNode);
            if (entityEClass.equals(nodeEClass)) {
                return true;
            }
        }
        // If we don't know what the objects are, assume the types match
        return true;
    }

    protected void process( final boolean caseSensitive,
                            final List nodes,
                            final List objs,
                            final List unmatchedNodes,
                            final List unmatchedObjs ) {
        // Create a list of objects by name. In the situation where there are multiple objects
        // with the same name, the value in the map is changed to a List
        final Map objByName = new HashMap();

        final Iterator iter = objs.iterator();
        while (iter.hasNext()) {
            final Object obj = iter.next();
            if (obj != null /*&& obj instanceof RelationalEntity*/) {
                final String name = caseSensitive ? this.modelObjectNameProvider.getMatchValue(obj) : this.modelObjectNameProvider.getMatchValue(obj).toUpperCase();
                final Object existing = objByName.put(name, obj);
                if (existing != null) {
                    // There were at least two objects with duplicate names
                    if (existing instanceof List) {
                        ((List)existing).add(obj); // add the object to the list
                        objByName.put(name, existing); // put the list back into the map
                    } else {
                        List listOfExisting = new LinkedList();
                        listOfExisting.add(obj); // add the object to the list
                        objByName.put(name, listOfExisting); // put the list back into the map
                    }
                }
            }
        }

        // Iterator over the nodes and look up exact matches ...
        final Iterator nodeIter = nodes.iterator();
        while (nodeIter.hasNext()) {
            final Object node = nodeIter.next();
            final String name = caseSensitive ? this.jdbcNodeNameProvider.getMatchValue(node) : this.jdbcNodeNameProvider.getMatchValue(node).toUpperCase();

            // Look up the object(s) with the same name ...
            Object objWithExactMatch = objByName.get(name);
            if (objWithExactMatch == null) {
                // Put into the unmatched
                unmatchedNodes.add(node);
            } else {
                if (objWithExactMatch instanceof LinkedList) {
                    // Then there are multiple model objects with this name ...
                    final LinkedList multipleMatches = (LinkedList)objWithExactMatch;

                    // Find the first one that has a matching type ...
                    objWithExactMatch = null; // clear this, in case no types match either
                    final Iterator iterator = multipleMatches.iterator();
                    while (iterator.hasNext()) {
                        final Object matchingObj = iterator.next();
                        if (isMatchingType(node, matchingObj)) {
                            // This object matches by name and by type
                            iterator.remove();
                            objWithExactMatch = matchingObj;
                            break;
                        }
                    }

                    // If there are no more items in the list, the clean up the list
                    if (multipleMatches.isEmpty()) {
                        objByName.remove(name);
                    }

                } else {
                    // There's only one match by name ...
                    if (isMatchingType(node, objWithExactMatch)) {
                        // The type also matches, so remove from the working map ...
                        objByName.remove(name);
                    } else {
                        // Otherwise the type doesn't match
                        objWithExactMatch = null;
                    }
                }

                if (objWithExactMatch != null) {
                    // Found a match, so put in the destination ...
                    this.destination.put(node, objWithExactMatch);
                }
            }
        }

        // Put any remaining objs in the list ...
        final Iterator remainingObjsIter = objByName.entrySet().iterator();
        while (remainingObjsIter.hasNext()) {
            final Map.Entry entry = (Map.Entry)remainingObjsIter.next();
            final Object value = entry.getValue();
            if (value instanceof List) {
                unmatchedObjs.addAll((List)value);
            } else {
                unmatchedObjs.add(value);
            }
        }
    }

    /**
     * @return
     */
    public MatchValueProvider getJdbcNodeNameProvider() {
        return jdbcNodeNameProvider;
    }

    /**
     * @return
     */
    public MatchValueProvider getModelObjectNameProvider() {
        return modelObjectNameProvider;
    }

    /**
     * @param provider
     */
    public void setJdbcNodeNameProvider( MatchValueProvider provider ) {
        jdbcNodeNameProvider = provider;
    }

    /**
     * @param provider
     */
    public void setModelObjectNameProvider( MatchValueProvider provider ) {
        modelObjectNameProvider = provider;
    }

    /**
     * @return
     */
    public Map getDestination() {
        return destination;
    }

    /**
     * @return
     */
    public LinkedList getUnmatchedModelObjects() {
        return unmatchedModelObjects;
    }

    /**
     * @return
     */
    public LinkedList getUnmatchedJdbcNodes() {
        return unmatchedNodes;
    }

}
