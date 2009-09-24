/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.webservice.procedure;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeGroupContent;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDAttributeUseCategory;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDCompositor;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDContentTypeCategory;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFeature;
import org.eclipse.xsd.XSDForm;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDParticleContent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTerm;
import org.eclipse.xsd.XSDTypeDefinition;

/**
 * Instances of this class represent the metadata for a component in a possible instance of an XML Schema (XSD) (i.e., a component
 * in an XML instance document). This class is <em>not</em> concerned about anything related to data values in an instance
 * document. The parent-child properties of instances of this class represent the possible resolved "paths" that can be taken in
 * an instance document, such that only a single iteration of recursive schema definitions are represented.
 * 
 * @since 5.0.1
 */
public class XsdInstanceNode {

    // ===========================================================================================================================
    // Constants

    // ===========================================================================================================================
    // Static Variables

    private static XSDComplexTypeDefinition anyType;

    // ===========================================================================================================================
    // Variables

    private XSDConcreteComponent xsdComponent;
    private XsdInstanceNode[] children;
    private XsdInstanceNode parent;
    private boolean selectable, selected, recursive, hasSelectableChildren;

    // ===========================================================================================================================
    // Constructors

    /**
     * Creates a root XsdInstanceNode.
     * 
     * @param element
     *            Must not be <code>null</code>.
     * @since 5.0.1
     */
    public XsdInstanceNode(XSDElementDeclaration element) {
        this(element, null);
    }

    /**
     * @param component
     *            Must not be <code>null</code>.
     * @param parent
     *            If <code>component</code> is not an <code>XSDElementDeclaration</code>, must not be <code>null</code>.
     * @since 5.0.1
     */
    public XsdInstanceNode(XSDConcreteComponent component,
                           XsdInstanceNode parent) {
        if (component == null) {
            throw new IllegalArgumentException();
        }
        if (!(component instanceof XSDElementDeclaration) && parent == null) {
            throw new IllegalArgumentException();
        }
        this.xsdComponent = component;
        this.parent = parent;
        if (component instanceof XSDParticle) {
            // Set XSD component to "real" value
            component = ((XSDParticle)component).getTerm();
        }
        // Determine if node is selectable
        if (component instanceof XSDElementDeclaration) {
            XSDElementDeclaration elem = (XSDElementDeclaration)component;
            if (elem.isElementDeclarationReference()) {
                elem = elem.getResolvedElementDeclaration();
            }
            XSDTypeDefinition type = elem.getType();
            if( type != null ) {
                if (type instanceof XSDSimpleTypeDefinition) {
                    this.selectable = true;
                } else {
                    XSDComplexTypeDefinition complexType = (XSDComplexTypeDefinition)type;
                    if (complexType.isMixed() || complexType.getContentTypeCategory() == XSDContentTypeCategory.SIMPLE_LITERAL) {
                        this.selectable = true;
                    }
                }
            }
        } else if (component instanceof XSDAttributeUse) {
            this.selectable = true;
        }
        // Determine if node is recursive and, if selectable, mark parents as having selectable children
        while (parent != null) {
            XSDConcreteComponent ancestorComp = parent.xsdComponent;
            if (ancestorComp instanceof XSDParticle) {
                ancestorComp = ((XSDParticle)ancestorComp).getTerm();
            }
            if (component == ancestorComp) {
                parent.recursive = this.recursive = true;
            }
            if (this.selectable) {
                parent.hasSelectableChildren = true;
            }
            parent = parent.parent;
        }
        // Resolve anyType
        if (anyType == null) {
            anyType = this.xsdComponent.getSchema().getSchemaForSchema().resolveComplexTypeDefinition("anyType"); //$NON-NLS-1$
        }
    }

    // ===========================================================================================================================
    // Methods

    /**
     * @since 5.0.1
     */
    private void addAttributes(List contents,
                               List children) {
        for (Iterator iter = contents.iterator(); iter.hasNext();) {
            XSDAttributeGroupContent content = (XSDAttributeGroupContent)iter.next();
            if (content instanceof XSDAttributeUse) {
                if (((XSDAttributeUse)content).getUse() != XSDAttributeUseCategory.PROHIBITED_LITERAL) {
                    children.add(new XsdInstanceNode(content, this));
                }
            } else {
                XSDAttributeGroupDefinition group = (XSDAttributeGroupDefinition)content;
                if (group.isAttributeGroupDefinitionReference()) {
                    group = group.getResolvedAttributeGroupDefinition();
                }
                addAttributes(group.getContents(), children);
            }
        } // for
    }

    /**
     * @since 5.0.1
     */
    private void addElementChildren(XSDElementDeclaration element,
                                    List children) {
        if (element.isElementDeclarationReference()) {
            element = element.getResolvedElementDeclaration();
        }
        XSDTypeDefinition type = element.getType();
        if (type instanceof XSDComplexTypeDefinition && type != anyType) {
            addTypeAttributes((XSDComplexTypeDefinition)type, children);
            XSDParticle particle = type.getComplexType();
            if (particle != null) {
                // Particle must represent a model group (i.e., for a compositor)
                addSequentialModelGroupChildren(particle, (XSDModelGroup)particle.getContent(), children);
            }
        }
        // else element must have simple type and therefore has no children
    }

    /**
     * @since 5.0.1
     */
    private void addModelGroupChildren(XSDModelGroup modelGroup,
                                       List children) {
        for (Iterator iter = modelGroup.getParticles().iterator(); iter.hasNext();) {
            XSDParticle particle = (XSDParticle)iter.next();
            XSDParticleContent content = particle.getContent();
            if (content instanceof XSDModelGroup) {
                addSequentialModelGroupChildren(particle, (XSDModelGroup)content, children);
            } else if (content instanceof XSDModelGroupDefinition) {
                XSDModelGroupDefinition group = (XSDModelGroupDefinition)content;
                if (group.isModelGroupDefinitionReference()) {
                    group = group.getResolvedModelGroupDefinition();
                }
                addSequentialModelGroupChildren(particle, group.getModelGroup(), children);
            } else if (content instanceof XSDElementDeclaration) {
                if (particle.getMaxOccurs() != 0) {
                    children.add(new XsdInstanceNode(particle, this));
                }
            } else {
                throw new RuntimeException("Unexpected content: " + content.getClass()); //$NON-NLS-1$
            }
        } // for
    }

    /**
     * @since 5.0.1
     */
    private void addSequentialModelGroupChildren(XSDParticle particle,
                                                 XSDModelGroup modelGroup,
                                                 List children) {
        if (modelGroup.getCompositor() == XSDCompositor.CHOICE_LITERAL) {
            children.add(new XsdInstanceNode(particle, this));
        } else {
            addModelGroupChildren(modelGroup, children);
        }
    }

    /**
     * @since 5.0.1
     */
    private void addTypeAttributes(XSDComplexTypeDefinition type,
                                   List children) {
        XSDTypeDefinition baseType = type.getBaseType();
        if (baseType instanceof XSDComplexTypeDefinition && baseType != anyType) {
            addTypeAttributes((XSDComplexTypeDefinition)baseType, children);
        }
        addAttributes(type.getAttributeContents(), children);
    }

    /**
     * @since 5.0.1
     */
    public XsdInstanceNode findRecursionRoot() {
        XSDConcreteComponent comp = this.xsdComponent;
        if (comp instanceof XSDParticle) {
            comp = ((XSDParticle)comp).getTerm();
        }
        for (XsdInstanceNode ancestor = this.parent; ancestor != null; ancestor = ancestor.parent) {
            if (ancestor.recursive) {
                XSDConcreteComponent ancestorComp = ancestor.xsdComponent;
                if (ancestorComp instanceof XSDParticle) {
                    ancestorComp = ((XSDParticle)ancestorComp).getTerm();
                }
                if (ancestorComp == comp) {
                    return ancestor;
                }
            }
        }
        return null;
    }

    /**
     * @since 5.0.1
     */
    public XsdInstanceNode[] getChildren() {
        if (this.children == null) {
            List children = new ArrayList();
            if (!this.recursive) {
                if (this.xsdComponent instanceof XSDElementDeclaration) {
                    addElementChildren((XSDElementDeclaration)this.xsdComponent, children);
                } else if (this.xsdComponent instanceof XSDAttributeGroupContent) {
                } else if (this.xsdComponent instanceof XSDParticle) {
                    XSDParticleContent content = ((XSDParticle)this.xsdComponent).getContent();
                    if (content instanceof XSDElementDeclaration) {
                        addElementChildren((XSDElementDeclaration)content, children);
                    } else if (content instanceof XSDModelGroupDefinition) {
                        // Particle must represent a choice
                        XSDModelGroupDefinition group = (XSDModelGroupDefinition)content;
                        if (group.isModelGroupDefinitionReference()) {
                            group = group.getResolvedModelGroupDefinition();
                        }
                        addModelGroupChildren(group.getModelGroup(), children);
                    } else if (content instanceof XSDModelGroup) {
                        addModelGroupChildren((XSDModelGroup)content, children);
                    }
                } else {
                    throw new RuntimeException("Unexpected component: " + this.xsdComponent.getClass()); //$NON-NLS-1$
                }
            }
            this.children = new XsdInstanceNode[children.size()];
            children.toArray(this.children);
        }
        return this.children;
    }

    /**
     * @return The name of this node's schema component.
     * @since 5.0.1
     */
    public String getName() {
        XSDConcreteComponent comp = getResolvedXsdComponent();
        if (comp instanceof XSDNamedComponent) {
            return ((XSDNamedComponent)comp).getName();
        }
        // Must be a choice
        return ((XSDModelGroup)comp).getCompositor().getName();
    }

    /**
     * @return Returns the parent.
     * @since 5.0.1
     */
    public XsdInstanceNode getParent() {
        return this.parent;
    }

    /**
     * @return Returns the xsdComponent.
     * @since 5.0.1
     */
    public XSDConcreteComponent getResolvedXsdComponent() {
        if (this.xsdComponent instanceof XSDElementDeclaration) {
            return this.xsdComponent;
        }
        if (this.xsdComponent instanceof XSDAttributeUse) {
            XSDAttributeDeclaration attr = ((XSDAttributeUse)this.xsdComponent).getAttributeDeclaration();
            if (attr.isAttributeDeclarationReference()) {
                return attr.getResolvedAttributeDeclaration();
            }
            return attr;
        }
        // Must be a particle
        XSDTerm term = ((XSDParticle)this.xsdComponent).getTerm();
        if (term instanceof XSDModelGroupDefinition) {
            XSDModelGroupDefinition group = (XSDModelGroupDefinition)term;
            if (group.isModelGroupDefinitionReference()) {
                group = group.getResolvedModelGroupDefinition();
            }
            return group.getModelGroup();
        }
        if (term instanceof XSDModelGroup) {
            return term;
        }
        // Must be an element
        XSDElementDeclaration elem = (XSDElementDeclaration)term;
        if (elem.isElementDeclarationReference()) {
            return elem.getResolvedElementDeclaration();
        }
        return elem;
    }

    /**
     * @return The target namespace of this node's schema component, or <code>null</code> if none is defined.
     * @since 5.0.1
     */
    public String getTargetNamespace() {
        return this.xsdComponent.getSchema().getTargetNamespace();
    }

    /**
     * @return Returns the xsdComponent.
     * @since 5.0.1
     */
    public XSDConcreteComponent getXsdComponent() {
        return this.xsdComponent;
    }

    /**
     * @return Returns the hasSelectableChildren.
     * @since 5.0.1
     */
    public boolean hasSelectableChildren() {
        return this.hasSelectableChildren;
    }

    /**
     * @return True if this node's schema component is a reference.
     * @since 5.0.1
     */
    public boolean isLocallyDefined() {
        if (this.xsdComponent instanceof XSDElementDeclaration) {
            return false;
        }
        if (this.xsdComponent instanceof XSDAttributeUse) {
            return !((XSDAttributeUse)this.xsdComponent).getAttributeDeclaration().isAttributeDeclarationReference();
        }
        // Must be a particle
        XSDTerm term = ((XSDParticle)this.xsdComponent).getTerm();
        if (term instanceof XSDModelGroupDefinition) {
            return !((XSDModelGroupDefinition)term).isModelGroupDefinitionReference();
        }
        if (term instanceof XSDModelGroup) {
            return true;
        }
        // Must be an element
        return !((XSDElementDeclaration)term).isElementDeclarationReference();
    }

    /**
     * @return True if instances of this node's schema component must be namespace-qualified in instance documents.
     * @since 5.0.1
     */
    public boolean isNamespaceQualifiedInDocument() {
        if (isLocallyDefined()) {
            XSDConcreteComponent comp = getResolvedXsdComponent();
            if (comp instanceof XSDFeature) {
                XSDFeature feature = (XSDFeature)comp;
                if (feature.isSetForm()) {
                    return (feature.getForm() == XSDForm.QUALIFIED_LITERAL);
                }
                XSDSchema schema = feature.getSchema();
                if (feature instanceof XSDAttributeDeclaration) {
                    if (schema.isSetAttributeFormDefault()) {
                        return (schema.getAttributeFormDefault() == XSDForm.QUALIFIED_LITERAL);
                    }
                } else if (schema.isSetElementFormDefault()) {
                    return (schema.getElementFormDefault() == XSDForm.QUALIFIED_LITERAL);
                }
            }
            return false;
        }
        return true;
    }

    /**
     * @return Returns the recursive.
     * @since 5.0.1
     */
    public boolean isRecursive() {
        return this.recursive;
    }

    /**
     * @return Returns the selectable.
     * @since 5.0.1
     */
    public boolean isSelectable() {
        return this.selectable;
    }

    /**
     * @return Returns the selected.
     * @since 5.0.1
     */
    public boolean isSelected() {
        return this.selected;
    }

    /**
     * @param selected
     *            The selected to set.
     * @since 5.0.1
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * @see java.lang.Object#toString()
     * @since 5.0.1
     */
    @Override
    public String toString() {
        StringBuffer text = new StringBuffer(this.xsdComponent.toString());
        text.append('#');
        text.append(getResolvedXsdComponent());
        return text.toString();
    }
}
