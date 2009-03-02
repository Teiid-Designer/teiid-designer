/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.mapping.factory;

import org.eclipse.emf.ecore.EObject;

/**
 * The <code>ITreeToRelationalMapper</code> class defines the methods needed to transform a tree-based
 * model to/from relational model.
 */
public interface ITreeToRelationalMapper {

    /**
     * Indicates if the specified tree node may be used as the location for a
     * {@link com.metamatrix.metamodels.transformation.MappingClass}.
     * @param theTreeNode the tree node being checked
     * @return <code>true</code> if tree node can be used; <code>false</code> otherwise.
     */
    boolean allowsMappingClass(EObject theTreeNode);

    /**
     * Indicates if the specified tree node may be used as the location for a
     * {@link com.metamatrix.metamodels.transformation.StagingTable}.
     * @param theTreeNode the tree node being checked
     * @return <code>true</code> if tree node can be used; <code>false</code> otherwise.
     */
    boolean allowsStagingTable(EObject theTreeNode);

    /**
     * Indicates if the specified tree node has a maxOccurs property greater than one.
     * @param theTreeNode the tree node whose can iterate property is being requested
     * @return <code>true</code> if can iterate; <code>false</code> otherwise.
     */
    boolean canIterate(EObject theTreeNode);

    /**
     * Return an implementation of <code>IMappableTree</code> for this mapper.
     * @return the tree
     */
    IMappableTree getMappableTree();
    
    /**
     * Indicates if the specified tree node is a generic container node, like a folder.  Used
     * to help generate meaningful mapping class names.
     * @return <code>true</code> if this node is simply a container for complex types; <code>false</code> otherwise.
     */
    boolean isContainerNode(EObject theNode);

    /**
     * Indicates if the specified tree node can be mapped.
     * @param theTreeNode the tree node whose "can be mapped" property is being requested
     * @return <code>true</code> if mappable; <code>false</code> otherwise.
     */
    boolean isMappable(EObject theTreeNode);

    /**
     * Indicates if the specified tree node is required to be mapped to a
     * {@link com.metamatrix.metamodels.transformation.MappingClassColumn}.
     * @param theTreeNode the tree node whose "requires mapping" property is being requested
     * @return <code>true</code> if required to be mapped; <code>false</code> otherwise.
     */
    boolean isMappingRequired(EObject theTreeNode);

    /**
     * Indicates if the specified node is recursive.
     * @param theMappingClass the <code>MappingClassColumn</code> whose recursive property is being requested
     * @return <code>true</code> if recursive; <code>false</code> otherwise.
     */
    boolean isRecursive(EObject theTreeNode);
    
    /**
     * Indicates if the specifed tree node is a root.
     * @param theTreeNode the tree node being checked
     * @return <code>true</code> if a tree root; <code>false</code> otherwise.
     */
    boolean isTreeRoot(EObject theTreeNode);
    
    /**
     * Indicates if the specifed tree node is a choice.
     * @param theTreeNode the tree node being checked
     * @return <code>true</code> if a choice; <code>false</code> otherwise.
     */
    boolean isChoiceNode(EObject theTreeNode);

    /**
     * Set the root for this mapper.
     * @param theTreeRoot the tree root
     */
    void setTreeRoot(EObject theTreeRoot);
    
	/**
	 * Indicates if the specifed tree node is a tree node.
	 * @param theTreeNode the tree node being checked
	 * @return <code>true</code> if a tree node; <code>false</code> otherwise.
	 */
	boolean isTreeNode(EObject theTreeNode);
	
	/**
	 * Returns the current path from the input eObject tree node to the
	 * Xml document root
	 * @param theTreeNode the tree node being checked
	 * @return string
	 */
	String getPathInDocument(EObject theTreeNode);
	
	/**
	 * Returns the name of the xml document node xsd component
	 * @param theTreeNode the tree node being checked
	 * @return string
	 */
	String getXsdQualifiedName(EObject theTreeNode);
	
	/**
	 * Returns the target namespace of the xml document node xsd component
	 * @param theTreeNode the tree node being checked
	 * @return string
	 */
	String getXsdTargetNamespace(EObject theTreeNode);
    
    /**
     * Returns the EObject xsd component for the specified xml document node
     * @param theTreeNode the tree node being checked
     * @return string
     */
    EObject getXsdComponent(EObject theTreeNode);
}
