/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.ui.model;

import java.util.Collection;

import org.teiid.core.HashCodeUtil;
import org.teiid.designer.extension.definition.ModelExtensionDefinition;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;

import com.metamatrix.core.util.CoreArgCheck;

/**
 * The <code>MedModelNode</code> wraps attributes and propertes of a {@link ModelExtensionDefinition}.
 */
public class MedModelNode {

    private static final MedModelNode[] NO_KIDS = new MedModelNode[0];

    /**
     * @param medNode the MED node (cannot be <code>null</code> and must wrap the MED)
     * @return the MED description node (never <code>null</code>)
     */
    public static MedModelNode createDescriptionNode( MedModelNode medNode ) {
        CoreArgCheck.isTrue(medNode.isMed(), "node is not a MED node"); //$NON-NLS-1$
        return new MedModelNode(medNode, ModelType.DESCRIPTION);
    }

    /**
     * @param med the model extension definition (cannot be <code>null</code>)
     * @return the MED node (never <code>null</code>)
     */
    public static MedModelNode createMedNode( ModelExtensionDefinition med ) {
        CoreArgCheck.isNotNull(med, "MED is null"); //$NON-NLS-1$
        return new MedModelNode(med);
    }

    /**
     * @param medNode the MED node (cannot be <code>null</code> and must wrap the MED)
     * @param metaclass the extended metaclass name (cannot be <code>null</code> or empty)
     * @return the MED metaclass node (never <code>null</code>)
     */
    public static MedModelNode createMetaclassNode( MedModelNode medNode,
                                                    String metaclass ) {
        CoreArgCheck.isTrue(medNode.isMed(), "node is not a MED node"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(metaclass, "metaclass is null"); //$NON-NLS-1$
        return new MedModelNode(medNode, ModelType.METACLASS, metaclass);
    }

    /**
     * @param medNode the MED node (cannot be <code>null</code> and must wrap the MED)
     * @return the MED metamodel URI node (never <code>null</code>)
     */
    public static MedModelNode createMetamodelUriNode( MedModelNode medNode ) {
        CoreArgCheck.isTrue(medNode.isMed(), "node is not a MED node"); //$NON-NLS-1$
        return new MedModelNode(medNode, ModelType.METAMODEL_URI);
    }

    /**
     * @param medNode the MED node (cannot be <code>null</code> and must wrap the MED)
     * @return the model types node (never <code>null</code>)
     */
    public static MedModelNode createModelTypesNode( MedModelNode medNode ) {
        CoreArgCheck.isTrue(medNode.isMed(), "node is not a MED node"); //$NON-NLS-1$
        return new MedModelNode(medNode, ModelType.MODEL_TYPES);
    }

    /**
     * @param medNode the MED node (cannot be <code>null</code> and must wrap the MED)
     * @return the MED namespace prefix node (never <code>null</code>)
     */
    public static MedModelNode createNamespacePrefixNode( MedModelNode medNode ) {
        CoreArgCheck.isTrue(medNode.isMed(), "node is not a MED node"); //$NON-NLS-1$
        return new MedModelNode(medNode, ModelType.NAMESPACE_PREFIX);
    }

    /**
     * @param medNode the MED node (cannot be <code>null</code> and must wrap the MED)
     * @return the MED namespace URI node (never <code>null</code>)
     */
    public static MedModelNode createNamespaceUriNode( MedModelNode medNode ) {
        CoreArgCheck.isTrue(medNode.isMed(), "node is not a MED node"); //$NON-NLS-1$
        return new MedModelNode(medNode, ModelType.NAMESPACE_URI);
    }

    /**
     * @param metaclassNode the MED metaclass node (cannot be <code>null</code> and must wrap the metaclass with the specified
     *            property)
     * @param propDefn the property definition
     * @return the MED property definition node (never <code>null</code>)
     */
    public static MedModelNode createPropertyNode( MedModelNode metaclassNode,
                                                   ModelExtensionPropertyDefinition propDefn ) {
        CoreArgCheck.isTrue(metaclassNode.isMetaclass(), "node is not a metaclass node"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(propDefn, "propDefn is null"); //$NON-NLS-1$
        return new MedModelNode(metaclassNode, ModelType.PROPERTY_DEFINITION, propDefn);
    }

    /**
     * @param medNode the MED node (cannot be <code>null</code> and must wrap the MED)
     * @return the MED version node (never <code>null</code>)
     */
    public static MedModelNode createVersionNode( MedModelNode medNode ) {
        CoreArgCheck.isTrue(medNode.isMed(), "node is not a MED node"); //$NON-NLS-1$
        return new MedModelNode(medNode, ModelType.VERSION);
    }

    private Object data;

    private MedModelNode[] kids;

    private final MedModelNode parent;

    private final ModelType type;

    private MedModelNode( MedModelNode parent,
                          ModelType type ) {
        this.parent = parent;
        this.type = type;
    }

    private MedModelNode( MedModelNode parent,
                          ModelType type,
                          Object data ) {
        this(parent, type);
        this.data = data;
    }

    /**
     * @param med the MED being wrapped
     */
    private MedModelNode( ModelExtensionDefinition med ) {
        this(null, ModelType.MODEL_EXTENSION_DEFINITION, med);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object obj ) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || !getClass().equals(obj.getClass())) {
            return false;
        }

        MedModelNode that = (MedModelNode)obj;

        if (this.type != that.type) {
            return false;
        }

        if (this.parent == null) {
            if (that.parent != null) {
                return false;
            }
        } else if (!this.parent.equals(that.parent)) {
            return false;
        }

        if (this.data == null) {
            if (that.data != null) {
                return false;
            }
        } else if (!this.data.equals(that.data)) {
            return false;
        }

        return true;
    }

    /**
     * @return the child nodes (never <code>null</code>)
     */
    public MedModelNode[] getChildren() {
        if (this.kids == null) {
            if (isMed()) {
                String[] metaclasses = getMed().getExtendedMetaclasses();
                int i = 0;

                this.kids = new MedModelNode[6 + metaclasses.length];
                this.kids[i++] = createNamespacePrefixNode(this);
                this.kids[i++] = createNamespaceUriNode(this);
                this.kids[i++] = createMetamodelUriNode(this);
                this.kids[i++] = createModelTypesNode(this);
                this.kids[i++] = createVersionNode(this);
                this.kids[i++] = createDescriptionNode(this);

                // create metaclass children
                for (String metaclass : metaclasses) {
                    this.kids[i++] = createMetaclassNode(this, metaclass);
                }
            } else if (isMetaclass()) {
                Collection<ModelExtensionPropertyDefinition> propDefns = getMed().getPropertyDefinitions((String)this.data);
                this.kids = new MedModelNode[propDefns.size()];
                int i = 0;

                for (ModelExtensionPropertyDefinition propDefn : propDefns) {
                    this.kids[i++] = createPropertyNode(this, propDefn);
                }
            } else {
                this.kids = NO_KIDS;
            }
        }

        return this.kids;
    }

    /**
     * @return the data specific to the node type (can be <code>null</code>)
     */
    public Object getData() {
        if (isNamespacePrefix()) {
            return getMed().getNamespacePrefix();
        }

        if (isNamespaceUri()) {
            return getMed().getNamespaceUri();
        }

        if (isMetamodelUri()) {
            return getMed().getMetamodelUri();
        }

        if (isModelTypes()) {
            return getMed().getSupportedModelTypes();
        }

        if (isDescription()) {
            return getMed().getDescription();
        }

        if (isVersion()) {
            return getMed().getVersion();
        }

        return this.data;
    }

    public MedModelNode getDescriptionNode() {
        if (isDescription()) {
            return this;
        }

        MedModelNode medNode = getMedNode();

        for (MedModelNode kid : medNode.getChildren()) {
            if (kid.isDescription()) {
                return kid;
            }
        }

        assert false : "description model node not found"; //$NON-NLS-1$
        return null;
    }

    /**
     * @return the MED (never <code>null</code>)
     */
    public ModelExtensionDefinition getMed() {
        MedModelNode medNode = getMedNode();
        return (ModelExtensionDefinition)medNode.getData();
    }

    /**
     * @return the MED node (never <code>null</code>)
     */
    public MedModelNode getMedNode() {
        if (isMed()) {
            return this;
        }

        return getParent().getMedNode();
    }

    /**
     * @return the metaclass name if either a metaclass node or a property definition node (never <code>null</code>)
     * @throws IllegalArgumentException if node is not a metaclass node or a property definition node
     */
    public String getMetaclass() {
        CoreArgCheck.isTrue((isMetaclass() || isPropertyDefinition()), null);

        if (isMetaclass()) {
            return getData().toString();
        }

        // must be a property definition node
        assert isPropertyDefinition() : "node is not a property definition model node"; //$NON-NLS-1$
        return getParent().getMetaclass();
    }

    public MedModelNode getMetaclassNode( String metaclass ) {
        MedModelNode medNode = getMedNode();

        for (MedModelNode child : medNode.getChildren()) {
            if (child.isMetaclass() && child.getMetaclass().equals(metaclass)) {
                return child;
            }
        }

        return null;
    }

    public MedModelNode getMetamodelUriNode() {
        if (isMetamodelUri()) {
            return this;
        }

        MedModelNode medNode = getMedNode();

        for (MedModelNode kid : medNode.getChildren()) {
            if (kid.isMetamodelUri()) {
                return kid;
            }
        }

        assert false : "metamodel URi model node not found"; //$NON-NLS-1$
        return null;
    }

    public MedModelNode getModelTypesNode() {
        if (isModelTypes()) {
            return this;
        }

        MedModelNode medNode = getMedNode();

        for (MedModelNode kid : medNode.getChildren()) {
            if (kid.isModelTypes()) {
                return kid;
            }
        }

        assert false : "model types model node not found"; //$NON-NLS-1$
        return null;
    }

    public MedModelNode getNamespacePrefixNode() {
        if (isNamespacePrefix()) {
            return this;
        }

        MedModelNode medNode = getMedNode();

        for (MedModelNode kid : medNode.getChildren()) {
            if (kid.isNamespacePrefix()) {
                return kid;
            }
        }

        assert false : "namespace prefix model node not found"; //$NON-NLS-1$
        return null;
    }

    public MedModelNode getNamespaceUriNode() {
        if (isNamespaceUri()) {
            return this;
        }

        MedModelNode medNode = getMedNode();

        for (MedModelNode kid : medNode.getChildren()) {
            if (kid.isNamespaceUri()) {
                return kid;
            }
        }

        assert false : "namespace URi model node not found"; //$NON-NLS-1$
        return null;
    }

    /**
     * @return the parent node (<code>null</code> if a MED node)
     */
    public MedModelNode getParent() {
        return this.parent;
    }

    /**
     * @return the property definition if a property definition node (never <code>null</code>)
     * @throws IllegalArgumentException if node is not a property definition node
     */
    public ModelExtensionPropertyDefinition getPropertyDefinition() {
        CoreArgCheck.isTrue(isPropertyDefinition(), null);
        return (ModelExtensionPropertyDefinition)getData();
    }

    public MedModelNode getPropertyDefinitionNode( String metaclass,
                                                   ModelExtensionPropertyDefinition propDefn ) {
        MedModelNode metaclassNode = getMetaclassNode(metaclass);

        if (metaclassNode != null) {
            for (MedModelNode propDefnNode : metaclassNode.getChildren()) {
                if (propDefn.equals(propDefnNode.getPropertyDefinition())) {
                    return propDefnNode;
                }
            }
        }

        return null;
    }

    public ModelType getType() {
        return this.type;
    }

    public MedModelNode getVersionNode() {
        if (isVersion()) {
            return this;
        }

        MedModelNode medNode = getMedNode();

        for (MedModelNode kid : medNode.getChildren()) {
            if (kid.isVersion()) {
                return kid;
            }
        }

        assert false : "version model node not found"; //$NON-NLS-1$
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = HashCodeUtil.hashCode(0, this.type);
        result = HashCodeUtil.hashCode(0, this.parent);
        return HashCodeUtil.hashCode(result, getData());
    }

    /**
     * @return <code>true</code> if a description node
     */
    public boolean isDescription() {
        return (this.type == ModelType.DESCRIPTION);
    }

    /**
     * @return <code>true</code> if a MED node
     */
    public boolean isMed() {
        return (this.type == ModelType.MODEL_EXTENSION_DEFINITION);
    }

    /**
     * @return <code>true</code> if a metaclass node
     */
    public boolean isMetaclass() {
        return (this.type == ModelType.METACLASS);
    }

    /**
     * @return <code>true</code> if a metamodel URI node
     */
    public boolean isMetamodelUri() {
        return (this.type == ModelType.METAMODEL_URI);
    }

    /**
     * @return <code>true</code> if a model types node
     */
    public boolean isModelTypes() {
        return (this.type == ModelType.MODEL_TYPES);
    }

    /**
     * @return <code>true</code> if a namespace prefix node
     */
    public boolean isNamespacePrefix() {
        return (this.type == ModelType.NAMESPACE_PREFIX);
    }

    /**
     * @return <code>true</code> if a namespace URI node
     */
    public boolean isNamespaceUri() {
        return (this.type == ModelType.NAMESPACE_URI);
    }

    /**
     * @return <code>true</code> if a property definition node
     */
    public boolean isPropertyDefinition() {
        return (this.type == ModelType.PROPERTY_DEFINITION);
    }

    /**
     * @return <code>true</code> if a version node
     */
    public boolean isVersion() {
        return (this.type == ModelType.VERSION);
    }

    public enum ModelType {

        /**
         * The description.
         */
        DESCRIPTION,

        /**
         * The metaclass name being extended.
         */
        METACLASS,

        /**
         * The metamodel URI.
         */
        METAMODEL_URI,

        /**
         * A MED.
         */
        MODEL_EXTENSION_DEFINITION,

        /**
         * The supported model types (empty if all model types are supported).
         */
        MODEL_TYPES,

        /**
         * The namespace prefix.
         */
        NAMESPACE_PREFIX,

        /**
         * The namespace URI.
         */
        NAMESPACE_URI,

        /**
         * A property definition.
         */
        PROPERTY_DEFINITION,

        /**
         * The version.
         */
        VERSION
    }

}
