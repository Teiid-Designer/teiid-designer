/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.xml.factory;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDComponent;
import org.eclipse.xsd.XSDContentTypeCategory;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.metamodels.xml.XmlAttribute;
import com.metamatrix.metamodels.xml.XmlChoice;
import com.metamatrix.metamodels.xml.XmlContainerNode;
import com.metamatrix.metamodels.xml.XmlDocumentEntity;
import com.metamatrix.metamodels.xml.XmlDocumentNode;
import com.metamatrix.metamodels.xml.XmlElement;
import com.metamatrix.metamodels.xml.XmlFragment;
import com.metamatrix.metamodels.xsd.XsdUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.util.ModelContents;
import com.metamatrix.modeler.mapping.factory.IMappableTree;
import com.metamatrix.modeler.mapping.factory.ITreeToRelationalMapper;
import com.metamatrix.modeler.xml.PluginConstants;

/**
 * XmlFragmentMapper
 */
public class XmlFragmentMapper implements ITreeToRelationalMapper, PluginConstants {
    
    /*----- DESIGN NOTES ----------------------------------------------------------------------------
     * - one TransformationMappingRoot for each MappingClass
     * - TranformationMappingRoot target=XmlFragment
     * - TransformationMappingRoot input=MappingClass
     * - TransformationMappingRoot output=XmlElement
     * - TransformationMappingRoot contains nested Mappings. If an attribute is mapped, it will have
     *   a mapping. If it is not mapped a Mapping could still exist if either input or output is null.
     *----------------------------------------------------------------------------------------------*/
     
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    private static final String PREFIX = I18nUtil.getPropertyPrefix(XmlFragmentMapper.class);
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    private XmlFragment fragment; // folder in old modeler
    private XmlMappableTree tree; // the IMappableTree for xml
//    private List mappingRoots; // TransformationMappingRoot collection
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    public XmlFragmentMapper() {
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.mapping.factory.ITreeToRelationalMapper#allowsMappingClass(org.eclipse.emf.ecore.EObject)
     */
    public boolean allowsMappingClass(EObject theTreeNode) {
        return ((theTreeNode instanceof XmlElement) || (theTreeNode instanceof XmlContainerNode));
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.mapping.factory.ITreeToRelationalMapper#allowsStagingTable(org.eclipse.emf.ecore.EObject)
     */
    public boolean allowsStagingTable(EObject theTreeNode) {
        // can put a StagingTable anywhere that a MappingClass is allowed.
        return allowsMappingClass(theTreeNode);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.mapping.factory.ITreeToRelationalMapper#canIterate(org.eclipse.emf.ecore.EObject)
     */
    public boolean canIterate(EObject theTreeNode) {
        checkNodeIsXmlDocumentEntity(theTreeNode);

        int maxOccurs = 0;
        if (theTreeNode instanceof XmlDocumentNode) {
           
            maxOccurs = XsdUtil.getMaxOccursLiteral(((XmlDocumentNode)theTreeNode).getXsdComponent());
        } else if (theTreeNode instanceof XmlContainerNode) {
            maxOccurs = XsdUtil.getMaxOccursLiteral(((XmlContainerNode)theTreeNode).getXsdComponent());
        }

        return ((maxOccurs > 1) || (maxOccurs == -1)); // -1 means unbounded 
    }

    
    /**
     * @throws IllegalArgumentException if the tree node is <code>null</code>
     * @throws IllegalArgumentException if the tree node is not an <code>XmlDocumentEntity</code>
     */
    private void checkNodeIsXmlDocumentEntity(EObject theTreeNode) {
        ArgCheck.isNotNull(theTreeNode);

        if (!(theTreeNode instanceof XmlDocumentEntity)) {
            throw new IllegalArgumentException(Util.getString(PREFIX + "invalidType", //$NON-NLS-1$
                                                              new Object[] {"XmlDocumentEntity", //$NON-NLS-1$
                                                                            theTreeNode.getClass().getName()}));
        }
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.mapping.factory.ITreeToRelationalMapper#getMappableTree()
     */
    public IMappableTree getMappableTree() {
        if ( tree == null ) {
            tree = new XmlMappableTree(this.fragment);
        }

        return tree;
    }


    /* (non-Javadoc)
     * @see com.metamatrix.modeler.mapping.factory.ITreeToRelationalMapper#isContainerNode()
     */
    public boolean isContainerNode(EObject theTreeNode) {
        //swjTODO: check this logic against the metamodel
        return theTreeNode instanceof XmlContainerNode;
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.mapping.factory.ITreeToRelationalMapper#isMappable(org.eclipse.emf.ecore.EObject)
     */
    public boolean isMappable(final EObject theTreeNode) {
        checkNodeIsXmlDocumentEntity(theTreeNode);

        if ( theTreeNode instanceof XmlAttribute ) {
            return isMappable((XmlAttribute)theTreeNode);
        }
        if ( theTreeNode instanceof XmlElement ) {
            return isMappable((XmlElement)theTreeNode);
        }
        return false;       // all other XML Document objects are not mappable
    }
    
    /**
     * Determine whether the supplied XmlAttribute is considered mappable.
     * @param attribute
     * @return
     */
    public boolean isMappable( final XmlAttribute attribute ) {
        ArgCheck.isNotNull(attribute);
        if ( attribute.isValueFixed() ) {
            // The attribute has a fixed value, so it should not be mappable
            return false;
        }
        return true;
    }
    
    /**
     * Determine whether the supplied XmlElement is considered mappable.
     * @param attribute
     * @return
     */
    public boolean isMappable( final XmlElement element ) {
        ArgCheck.isNotNull(element);
        
        // Obtain the schema component for the element ...
        final XSDComponent schemaComponent = element.getXsdComponent();
        if ( schemaComponent != null ) {
            return isElementMappable(schemaComponent);
        }
        // else there is no schema component, and ad hoc elements are mappable
        return true;
    }
    
    /**
     * Determine whether an XmlElement with the supplied XSDComponent is considered mappable.
     * @param attribute
     * @return
     */
    public boolean isElementMappable( final XSDComponent xsdComponent ) {
        ArgCheck.isNotNull(xsdComponent);

        if (xsdComponent instanceof XSDElementDeclaration) {
            final XSDElementDeclaration elementDecl = (XSDElementDeclaration)xsdComponent;
            // Obtain the type of the element declaration (which can be a ref, etc.) ...
            final XSDTypeDefinition type = XsdUtil.getType(elementDecl);

            if (type instanceof XSDSimpleTypeDefinition) {
                // Element with a simple type, therefore mappable ...
                return true;
            } else if (type instanceof XSDComplexTypeDefinition) {
                final XSDComplexTypeDefinition complexType = (XSDComplexTypeDefinition)type;
                // A Complex type that has mixed content is mappable ...
                if ( complexType.isMixed() ) {
                    return true;
                }
                final XSDContentTypeCategory category = complexType.getContentTypeCategory();
                final int categoryValue = category.getValue();
                switch(categoryValue) {
                    case XSDContentTypeCategory.MIXED:
                        return true;
                    case XSDContentTypeCategory.ELEMENT_ONLY:
                        return false;
                    case XSDContentTypeCategory.EMPTY:
                        return false;
                    case XSDContentTypeCategory.SIMPLE:
                        return true;
                }
                return false;
            }
        } else if (xsdComponent instanceof XSDComplexTypeDefinition) {
            // this is a fragment, so it should be mappable ...
            return true;
        }
        return false;
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.mapping.factory.ITreeToRelationalMapper#isMappingRequired(org.eclipse.emf.ecore.EObject)
     */
    public boolean isMappingRequired(EObject theTreeNode) {
      checkNodeIsXmlDocumentEntity(theTreeNode);
      throw new RuntimeException("XmlFragmentMapper.isMappingRequired not yet implemented"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.mapping.factory.ITreeToRelationalMapper#isRecursive(org.eclipse.emf.ecore.EObject)
     */
    public boolean isRecursive(EObject theTreeNode) {
      checkNodeIsXmlDocumentEntity(theTreeNode);
      return ((theTreeNode instanceof XmlElement) && ((XmlElement)theTreeNode).isRecursive());
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.mapping.factory.ITreeToRelationalMapper#isTreeRoot(org.eclipse.emf.ecore.EObject)
     */
    public boolean isTreeRoot(EObject theTreeNode) {
        ArgCheck.isNotNull(theTreeNode);
        return (theTreeNode instanceof XmlFragment);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.mapping.factory.ITreeToRelationalMapper#setTreeRoot(org.eclipse.emf.ecore.EObject)
     */
    public void setTreeRoot(EObject theFragment) {
        ArgCheck.isNotNull(theFragment);
        fragment = (XmlFragment) theFragment;
        
        // need to get all the transformations where the target is this fragment
        // each transformation will have a TreeMappingRoot for each MappingClass
        ModelContents modelContents = ModelerCore.getModelEditor().getModelContents(fragment);
        
//        mappingRoots = 
        modelContents.getTransformations(fragment);
    }
    
	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.mapping.factory.ITreeToRelationalMapper#isTreeRoot(org.eclipse.emf.ecore.EObject)
	 */
	public boolean isTreeNode(EObject theTreeNode) {
		ArgCheck.isNotNull(theTreeNode);
		return (theTreeNode instanceof XmlDocumentNode 
				|| isTreeRoot(theTreeNode) 
				|| theTreeNode instanceof XmlContainerNode );
	}

	public String getPathInDocument(EObject theTreeNode) {
		ArgCheck.isNotNull(theTreeNode);
		if( theTreeNode instanceof XmlDocumentNode ) {
			return ((XmlDocumentNode)theTreeNode).getPathInDocument();
		}
        if( theTreeNode instanceof XmlContainerNode ) {
            return ((XmlContainerNode)theTreeNode).getPathInDocument();
        }
		return null;
	}
	
	public String getXsdQualifiedName(EObject theTreeNode) {
		ArgCheck.isNotNull(theTreeNode);
		if( theTreeNode instanceof XmlDocumentNode ) {
			XSDComponent xsdComponent = ((XmlDocumentNode)theTreeNode).getXsdComponent();
			if ( xsdComponent != null && xsdComponent instanceof XSDNamedComponent ) {
				return ((XSDNamedComponent)xsdComponent).getQName();
			}
		}
		return null;
	}
	
	public String getXsdTargetNamespace(EObject theTreeNode) {
		ArgCheck.isNotNull(theTreeNode);
		if( theTreeNode instanceof XmlDocumentNode ) {
			XSDComponent xsdComponent = ((XmlDocumentNode)theTreeNode).getXsdComponent();
			if ( xsdComponent != null && xsdComponent instanceof XSDNamedComponent ) {
				return ((XSDNamedComponent)xsdComponent).getTargetNamespace();
			}
		}
		return null;
	}

    /** 
     * @see com.metamatrix.modeler.mapping.factory.ITreeToRelationalMapper#getXsdComponent(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    public EObject getXsdComponent(EObject theTreeNode) {
        ArgCheck.isNotNull(theTreeNode);
        if( theTreeNode instanceof XmlDocumentNode ) {
            EObject xsdComp = ((XmlDocumentNode)theTreeNode).getXsdComponent();
            EObject container = xsdComp.eContainer();
            if( container instanceof XSDParticle || container instanceof XSDAttributeUse) {
                return container;
            }
            
            return xsdComp;
        } else if( theTreeNode instanceof XmlContainerNode ) {
            return ((XmlContainerNode)theTreeNode).getXsdComponent();
        }
        
        return null;
    }

    public boolean isChoiceNode(EObject theTreeNode) {
        return theTreeNode instanceof XmlChoice;
    }
    
    
 }
