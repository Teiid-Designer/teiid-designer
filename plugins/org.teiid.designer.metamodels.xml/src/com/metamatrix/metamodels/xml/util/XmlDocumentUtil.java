/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xml.util;

import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDComponent;
import org.eclipse.xsd.XSDDerivationMethod;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFeature;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.metamodels.xml.BuildStatus;
import com.metamatrix.metamodels.xml.ProcessingInstruction;
import com.metamatrix.metamodels.xml.XmlAll;
import com.metamatrix.metamodels.xml.XmlAttribute;
import com.metamatrix.metamodels.xml.XmlBuildable;
import com.metamatrix.metamodels.xml.XmlChoice;
import com.metamatrix.metamodels.xml.XmlComment;
import com.metamatrix.metamodels.xml.XmlContainerNode;
import com.metamatrix.metamodels.xml.XmlDocumentEntity;
import com.metamatrix.metamodels.xml.XmlDocumentNode;
import com.metamatrix.metamodels.xml.XmlDocumentPlugin;
import com.metamatrix.metamodels.xml.XmlElement;
import com.metamatrix.metamodels.xml.XmlFragment;
import com.metamatrix.metamodels.xml.XmlNamespace;
import com.metamatrix.metamodels.xml.XmlSequence;

/**
 * XmlDocumentUtil
 */
public class XmlDocumentUtil {
    
    public static final int XSD_OCCURRENCE_NONE = 0;
    public static final int XSD_OCCURRENCE_N = 1;
    public static final int XSD_OCCURRENCE_NToM = 2;
    public static final int XSD_OCCURRENCE_NToUnbounded = 3;
    public static final int XSD_OCCURRENCE_One = 4;
    public static final int XSD_OCCURRENCE_OneToN = 5;
    public static final int XSD_OCCURRENCE_OneToUnbounded = 6;
    public static final int XSD_OCCURRENCE_Zero = 7;
    public static final int XSD_OCCURRENCE_ZeroToN = 8;
    public static final int XSD_OCCURRENCE_ZeroToOne = 9;
    public static final int XSD_OCCURRENCE_ZeroToUnbounded = 10;
    
    
    /** Moved from XmlDocumentBuilderImpl: */
    private static final String ANY_TYPE = "anyType"; //$NON-NLS-1$
    
    /**
     * Hidden constructor to prevent instantiation.
     */
    private XmlDocumentUtil() {
        super();
    }

    public static String createXmlPrefixFromUri(final String uri){
        if(uri == null){
            return ""; //$NON-NLS-1$
        }
        if (uri.equals(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001) ||
            uri.equals(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2000_10) ||
            uri.equals(XSDConstants.SCHEMA_FOR_SCHEMA_URI_1999)) {
            return "xs"; //$NON-NLS-1$
        }
        if (uri.equals(XSDConstants.SCHEMA_INSTANCE_URI_2001)) {
            return "xsi"; //$NON-NLS-1$
        }
        
        int index = uri.lastIndexOf("/"); //$NON-NLS-1$
        // No Fwd slash found 
        if(index == -1) {
        	// Look for Dbl backslash
        	index = uri.lastIndexOf("\\"); //$NON-NLS-1$
        }
    	// if Neither found, reset index to 0 (otherwise increment past it)
    	if (index==-1) {
    		index = 0; 
    	} else {
    		index++;
    	}
    	
    	// keep name past slashes.  If any colons, replace with underscores
        final String name = uri.substring(index).replace(':', '_');
        
        //remove file extension
        final int i2 = name.lastIndexOf("."); //$NON-NLS-1$
        if(i2 == -1){
            return name;
        }
        
        return name.substring(0, i2);        
    }
    
    public static String getPathInDocument( XmlDocumentEntity entity ) {
        String path = null;
        while ( entity != null ) {
            String segment = null;
            if ( entity instanceof XmlFragment ) {
                break;      // stop when we see the document/fragment
            }
            if ( entity instanceof XmlNamespace ) {
                final XmlNamespace namespace = (XmlNamespace)entity;
                segment = "@xmlns:" + namespace.getPrefix(); //$NON-NLS-1$
            } else if ( entity instanceof XmlElement ) {
                final XmlElement element = (XmlElement)entity;
                segment = element.getName();
            } else if ( entity instanceof XmlAttribute ) {
                final XmlAttribute attribute = (XmlAttribute)entity;
                segment = "@" + attribute.getName(); //$NON-NLS-1$
            } else if ( entity instanceof XmlContainerNode ) {
                if ( entity instanceof XmlSequence ) {
                    segment = XmlDocumentPlugin.Util.getString("_UI_XmlSequence_type"); //$NON-NLS-1$
                } else if ( entity instanceof XmlChoice ) {
                    segment = XmlDocumentPlugin.Util.getString("_UI_XmlChoice_type"); //$NON-NLS-1$
                } else if ( entity instanceof XmlAll ) {
                    segment = XmlDocumentPlugin.Util.getString("_UI_XmlAll_type"); //$NON-NLS-1$
                }
            } else if ( entity instanceof XmlComment ) {
                final XmlComment comment = (XmlComment)entity;
                String text = comment.getText();
                segment = "<!-- "; //$NON-NLS-1$
                if ( text != null ) {
                    segment = segment + text;
                }
                segment = segment + " -->"; //$NON-NLS-1$
                if ( segment.length() > 21 ) {
                    segment = segment.substring(0,20) + "... -->"; //$NON-NLS-1$
                }
            } else if ( entity instanceof ProcessingInstruction ) {
                final ProcessingInstruction instructions = (ProcessingInstruction)entity;
                String target = instructions.getTarget();
                String rawText = instructions.getRawText();
                segment = "<?"; //$NON-NLS-1$
                if ( target != null && target.trim().length() != 0 ) {
                    segment = segment + target;
                }
                if ( rawText != null && rawText.trim().length() != 0 ) {
                    segment = segment + " " + rawText; //$NON-NLS-1$
                }
                segment = segment + " ?>"; //$NON-NLS-1$
                if ( segment.length() > 21 ) {
                    segment = segment.substring(0,20) + "... ?>"; //$NON-NLS-1$
                }
            }
            if ( segment == null ) {
                segment = CoreStringUtil.Constants.EMPTY_STRING;
            }
            if ( path == null ) {
                path = segment;
            } else {
                path = segment + "/" + path; //$NON-NLS-1$
            }
            entity = (XmlDocumentEntity) entity.eContainer();
        }
        if ( path == null ) {
            return "/"; //$NON-NLS-1$
        }
        return "/" + path; //$NON-NLS-1$
    }
    
    public static String getXPath( XmlDocumentEntity entity ) {
        String path = null;
        while ( entity != null ) {
            String segment = null;
            if ( entity instanceof XmlFragment ) {
                break;      // stop when we see the document/fragment
            }
            if ( entity instanceof XmlNamespace ) {
                final XmlNamespace namespace = (XmlNamespace)entity;
                segment = "@xmlns:" + namespace.getPrefix(); //$NON-NLS-1$
            } else if ( entity instanceof XmlElement ) {
                final XmlElement element = (XmlElement)entity;
                segment = element.getName();
            } else if ( entity instanceof XmlAttribute ) {
                final XmlAttribute attribute = (XmlAttribute)entity;
                segment = "@" + attribute.getName(); //$NON-NLS-1$
            } else if ( entity instanceof XmlContainerNode ) {
                entity = (XmlDocumentEntity) entity.eContainer();
                continue;       // skip container nodes
            } else if ( entity instanceof XmlComment ) {
                entity = (XmlDocumentEntity) entity.eContainer();
                continue;       // skip container nodes
            } else if ( entity instanceof ProcessingInstruction ) {
                entity = (XmlDocumentEntity) entity.eContainer();
                continue;       // skip container nodes
            }
            if ( segment == null ) {
                segment = CoreStringUtil.Constants.EMPTY_STRING;
            }
            if ( path == null ) {
                path = segment;
            } else {
                path = segment + "/" + path; //$NON-NLS-1$
            }
            entity = (XmlDocumentEntity) entity.eContainer();
        }
        if ( path == null ) {
            return "/"; //$NON-NLS-1$
        }
        return "/" + path; //$NON-NLS-1$
    }

    /** Is the specified document node a complex type?
      * @param dnode the node to analyze
      * @return whether dnode has a complex type
      */
    public static boolean hasComplexType(Object object) {
        if (object instanceof XmlDocumentNode) {
            XmlDocumentNode dnode = (XmlDocumentNode) object;
            XSDComponent xcomp = dnode.getXsdComponent();
            XSDTypeDefinition xtype = findXSDType(xcomp);
    
            return xtype instanceof XSDComplexTypeDefinition;
        } 
        return false;
    }

    /** Moved from XmlDocumentBuilderImpl: */
    public static boolean isAnyType(final EObject obj){
        if(obj instanceof XSDTypeDefinition){
            if (ANY_TYPE.equals(((XSDTypeDefinition)obj).getName()) ){ 
                return true;
            }
        }
        
        return false;
    }

    /** 
      * Return the type definition for the given XmlDocumentNode
      * @param docNode the type definition for the given XmlDocumentNode - may not be null
      * @return the XSDTypeDefinition of the node, if it is an XmlDocumentNode, or null
      *   otherwise.
      */
    public static XSDTypeDefinition findXSDType(Object node) {
        if (node instanceof XmlDocumentNode) {
            XmlDocumentNode docNode = (XmlDocumentNode) node;
            return findXSDType(docNode.getXsdComponent());
        } //endif
        
        return null;
    }

    /** Moved from XmlDocumentBuilderImpl:
      * Return the type definition for the given schemaComponent - may be null
      * @param the type definition for the given schemaComponent - may be null
      */
    public static XSDTypeDefinition findXSDType(final XSDComponent schemaComponent) {
        if(schemaComponent instanceof XSDElementDeclaration){
            Object ref = ((XSDElementDeclaration)schemaComponent).getResolvedElementDeclaration();
            return (ref == null) ? null : ((XSDElementDeclaration)ref).getType();
        }else if(schemaComponent instanceof XSDTypeDefinition){
            if(schemaComponent instanceof XSDComplexTypeDefinition) {
                final XSDDerivationMethod der = ((XSDComplexTypeDefinition)schemaComponent).getDerivationMethod();
                if(der != null && der.getValue() == XSDDerivationMethod.RESTRICTION) {
                    return null;
                }
            }
            
            final XSDTypeDefinition baseType = ((XSDTypeDefinition)schemaComponent).getBaseType();
            if(baseType == null || baseType == schemaComponent || isAnyType(baseType) ){
                return null;
            }
            
            return baseType;
        }else if(schemaComponent instanceof XSDFeature){
            return ((XSDFeature)schemaComponent).getType();
        }
                
        return null;
    }

    /** Examine whether the Exclude from Document property is set.
      * @param element the element to analyze
      * @param checkHierarchy if true, examine parents for isExcluded until
      *   an excluded parent is found (returns true) or XMLRoot is
      *   encountered (returns false)
      * @return the value of the ExcludeFromDocument property if
      *   element is an XmlElement or XmlContainerNode, false otherwise
      */
    public static boolean isExcluded(Object element, boolean checkHierarchy) {
        // enclose with a generic instanceof to weed things out early
        //  if they aren't even in the XML hierarchy.
        if (element instanceof XmlDocumentEntity) {
            if (element instanceof XmlElement) {
                XmlElement e = (XmlElement) element;
                return e.isExcludeFromDocument() || (checkHierarchy && isExcluded(e.getParent(), checkHierarchy));

            } else if (element instanceof XmlContainerNode) {
                XmlContainerNode e = (XmlContainerNode) element;
                return e.isExcludeFromDocument() || (checkHierarchy && isExcluded(e.getParent(), checkHierarchy));

            // Look for other things, like attributes, namespace declarations, etc:
            } else if (element instanceof XmlAttribute) {
                XmlAttribute e = (XmlAttribute)element;
                return e.isExcludeFromDocument() || (checkHierarchy && isExcluded(e.getElement(), checkHierarchy));

            } else if (element instanceof XmlComment) {
                XmlComment e = (XmlComment)element;
                return checkHierarchy && isExcluded(e.getParent(), checkHierarchy);

            } else if (element instanceof XmlNamespace) {
                XmlNamespace e = (XmlNamespace)element;
                return checkHierarchy && isExcluded(e.getElement(), checkHierarchy);

            } else if (element instanceof ProcessingInstruction) {
                ProcessingInstruction e = (ProcessingInstruction)element;
                return checkHierarchy && isExcluded(e.getParent(), checkHierarchy);

            } //endif
        } // endif
    
        return false;
    }

    /** Sets the ExcludeFromDocument property on XmlElements and XmlContainerNodes.
      * @param element the element to change
      * @param exclude the value to set
      */
    public static void setExcluded(Object element, boolean exclude) {
        if (element instanceof XmlDocumentNode) {
            XmlDocumentNode xe = (XmlDocumentNode) element;
            xe.setExcludeFromDocument(exclude);
        } else if (element instanceof XmlContainerNode) {
            XmlContainerNode xcn = (XmlContainerNode) element;
            xcn.setExcludeFromDocument(exclude);
        }
    }

    /** Examine whether the Recursive property is set.
      * @param element the element to analyze
      * @return the value of the Recursive property if
      *   element is an XmlElement, false otherwise
      */
    public static boolean isRecursive(Object element) {
        if (element instanceof XmlElement) {
            XmlElement xe = (XmlElement) element;
            return xe.isRecursive();
        } // endif
    
        return false;
    }

    /** Sets the Recursive property on XmlElements.
      * @param element the element to change
      * @param recurse the value to set
      */
    public static void setRecursive(Object element, boolean recurse) {
        if (element instanceof XmlElement) {
            XmlElement xe = (XmlElement) element;
            xe.setRecursive(recurse);
        } // endif
    }
    
    /** Examine whether the BuildStatus property is set to INCOMPLETE.
     * @param element the element to analyze
     * @return true if element is an XmlBuildable and the value of the 
     *   BuildStatus property is INCOMPLETE, false otherwise
     */
   public static boolean isIncomplete(Object element) {
       if (element instanceof XmlBuildable) {
           XmlBuildable xb = (XmlBuildable) element;
           return xb.getBuildState() == BuildStatus.INCOMPLETE_LITERAL;
       } // endif

       return false;
   }
   
   /** Sets the BuildStatus property on XmlBuildables.
     * @param element the element to change
     * @param stopped the value to set.  If true, the BuildStatus
     *   property will be set to INCOMPLETE.  If false, COMPLETE.
     */
   public static void setIncomplete(Object element, boolean stopped) {
       if (element instanceof XmlBuildable) {
           XmlBuildable xb = (XmlBuildable) element;
           BuildStatus bs = (stopped)?BuildStatus.INCOMPLETE_LITERAL:BuildStatus.COMPLETE_LITERAL;
           xb.setBuildState(bs);
       } // endif
   }
    

    public static boolean hasElementChildren(XmlDocumentEntity node) {
        Iterator itor = node.eContents().iterator();
        while (itor.hasNext()) {
            XmlDocumentEntity kid = (XmlDocumentEntity) itor.next();
            if (kid instanceof XmlElement) {
                return true;
            } // endif
        } // endwhile
        
        return false;
    }
    
    public static int getCardinality(Object element) {
        if (element instanceof XmlDocumentNode) {
            int min = ((XmlDocumentNode) element).getMinOccurs();
            int max = ((XmlDocumentNode) element).getMaxOccurs();
            
            if (min == 0) {
                if (min == max) {
                    return XSD_OCCURRENCE_Zero;
                } else if (max == 1) {
                    return XSD_OCCURRENCE_ZeroToOne;
                } else if (max == -1) {
                    return XSD_OCCURRENCE_ZeroToUnbounded;
                } else if (max > min) {
                    return XSD_OCCURRENCE_ZeroToN;
                }
            } else if (min == 1) {
                if (min == max) {
                    return XSD_OCCURRENCE_One;
                } else if (max == -1) {
                    return XSD_OCCURRENCE_OneToUnbounded;
                } else if (max > min) {
                    return XSD_OCCURRENCE_OneToN;
                }
            } else if (min > 1) {
                if (min == max) {
                    return XSD_OCCURRENCE_N;
                } else if (max == -1) {
                    return XSD_OCCURRENCE_NToUnbounded;
                } else if (max > min) {
                    return XSD_OCCURRENCE_NToM;
                }
            }
        }
        return XSD_OCCURRENCE_NONE;
    }

    public static void setAllExcluded(XmlDocumentEntity element, boolean excluded) {
        List l = element.eContents();
        Iterator itor = l.iterator();
        while (itor.hasNext()) {
            XmlDocumentEntity node = (XmlDocumentEntity) itor.next();
            setAllExcluded(node, excluded);
        } // endwhile
        
        // set my own excluded property:
        setExcluded(element, excluded);
    }
}
