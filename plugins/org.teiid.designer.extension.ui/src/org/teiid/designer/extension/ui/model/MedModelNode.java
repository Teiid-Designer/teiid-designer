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

        if (this.data == null) {
            return (that.data == null);
        }

        return this.data.equals(that.data);
    }

    /**
     * @return the child nodes (never <code>null</code>)
     */
    public MedModelNode[] getChildren() {
        if (this.kids == null) {
            if (this.type == ModelType.MODEL_EXTENSION_DEFINITION) {
                String[] metaclasses = getMed().getExtendedMetaclasses();
                int i = 0;

                this.kids = new MedModelNode[5 + metaclasses.length];
                this.kids[i++] = createNamespacePrefixNode(this);
                this.kids[i++] = createNamespaceUriNode(this);
                this.kids[i++] = createMetamodelUriNode(this);
                this.kids[i++] = createVersionNode(this);
                this.kids[i++] = createDescriptionNode(this);

                // create metaclass children
                for (String metaclass : metaclasses) {
                    this.kids[i++] = createMetaclassNode(this, metaclass);
                }
            } else if (this.type == ModelType.METACLASS) {
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
        if (this.type == ModelType.NAMESPACE_PREFIX) {
            return getMed().getNamespacePrefix();
        }

        if (this.type == ModelType.NAMESPACE_URI) {
            return getMed().getNamespaceUri();
        }

        if (this.type == ModelType.METAMODEL_URI) {
            return getMed().getMetamodelUri();
        }

        if (this.type == ModelType.DESCRIPTION) {
            return getMed().getDescription();
        }

        if (this.type == ModelType.VERSION) {
            return getMed().getVersion();
        }

        return this.data;
    }

    /**
     * @return the MED (never <code>null</code>)
     */
    public ModelExtensionDefinition getMed() {
        if (this.type == ModelType.MODEL_EXTENSION_DEFINITION) {
            return (ModelExtensionDefinition)getData();
        }

        return this.parent.getMed();
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
        CoreArgCheck.isTrue(((this.type == ModelType.METACLASS) || (this.type == ModelType.PROPERTY_DEFINITION)), null);

        if (this.type == ModelType.METACLASS) {
            return getData().toString();
        }

        return getParent().getMetaclass();
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
        CoreArgCheck.isTrue((this.type == ModelType.PROPERTY_DEFINITION), null);
        return (ModelExtensionPropertyDefinition)getData();
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return HashCodeUtil.hashCode(HashCodeUtil.hashCode(0, this.type), this.data);
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

    private enum ModelType {

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
