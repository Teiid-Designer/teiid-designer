/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xsd.aspects.uml;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.xsd.XSDBoundedFacet;
import org.eclipse.xsd.XSDCardinalityFacet;
import org.eclipse.xsd.XSDConstrainingFacet;
import org.eclipse.xsd.XSDEnumerationFacet;
import org.eclipse.xsd.XSDFractionDigitsFacet;
import org.eclipse.xsd.XSDFundamentalFacet;
import org.eclipse.xsd.XSDLengthFacet;
import org.eclipse.xsd.XSDMaxExclusiveFacet;
import org.eclipse.xsd.XSDMaxInclusiveFacet;
import org.eclipse.xsd.XSDMaxLengthFacet;
import org.eclipse.xsd.XSDMinExclusiveFacet;
import org.eclipse.xsd.XSDMinInclusiveFacet;
import org.eclipse.xsd.XSDMinLengthFacet;
import org.eclipse.xsd.XSDNumericFacet;
import org.eclipse.xsd.XSDOrderedFacet;
import org.eclipse.xsd.XSDPatternFacet;
import org.eclipse.xsd.XSDTotalDigitsFacet;
import org.eclipse.xsd.XSDWhiteSpaceFacet;
import org.eclipse.xsd.impl.XSDFacetImpl;
import com.metamatrix.core.MetaMatrixRuntimeException;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.metamodels.relational.RelationalPlugin;
import com.metamatrix.metamodels.xsd.XsdPlugin;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.aspect.AbstractMetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlProperty;

/**
 * ColumnAspect
 */
public class XSDFacetAspect extends AbstractMetamodelAspect implements UmlProperty {
    
    public static final String ASPECT_ID = ModelerCore.EXTENSION_POINT.UML_DIAGRAM_ASPECT.ID;

    /**
     * Construct an instance of ColumnAspect.
     * @param entity
     */
    public XSDFacetAspect(MetamodelEntity entity){
        super();
        setID(ASPECT_ID);
        setMetamodelEntity(entity);
    }
    
    public int getVisibility(Object eObject) {
        return VISIBILITY_PUBLIC;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect#getImage(java.lang.Object)
     * @since 4.2
     */
    public Object getImage(Object eObject) {
        // get the adapter factory
        final AdapterFactory adapterFactory = ModelerCore.getMetamodelRegistry().getAdapterFactory();
		// lookup item provider for the eobjet
		final IItemLabelProvider provider = (IItemLabelProvider)adapterFactory.adapt(eObject,IItemLabelProvider.class);
		// look up image
		return provider.getImage(eObject);
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.uml.UmlProperty#isAssociationEnd(java.lang.Object)
     */
    public boolean isAssociationEnd(Object property) {
        return false;
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodels.aspects.UmlDiagramAspect#getStereotype(java.lang.Object)
     */
    public String getStereotype(Object eObject) {
        XSDFacetImpl facet = assertFacet(eObject);
        String stereotype = null;
        if (facet instanceof XSDFundamentalFacet) {
            stereotype = XsdPlugin.getPluginResourceLocator().getString("_UI_XSDFundamentalFacet_type"); //$NON-NLS-1$
        } else if (facet instanceof XSDConstrainingFacet) {
            stereotype = XsdPlugin.getPluginResourceLocator().getString("_UI_XSDConstrainingFacet_type"); //$NON-NLS-1$
        }
        if (stereotype == null) {
            stereotype = XsdPlugin.getPluginResourceLocator().getString("_UI_XSDFacet_type"); //$NON-NLS-1$
        }
        return stereotype;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodels.aspects.UmlDiagramAspect#setSignature(java.lang.Object, java.lang.String)
     */
    public IStatus setSignature(Object eObject, String newSignature) {
        try {
            XSDFacetImpl facet = assertFacet(eObject);
            facet.setLexicalValue(newSignature);
        } catch (Throwable e) {
            return new Status(IStatus.ERROR, XsdPlugin.PLUGIN_ID, 0, e.getMessage(), e);
        }
        
        return new Status(IStatus.OK, XsdPlugin.PLUGIN_ID, 0, XsdPlugin.Util.getString("Aspect.ok"), null);  //$NON-NLS-1$
    }

    public String getSignature(Object eObject, int showMask) {
        XSDFacetImpl facet  = assertFacet(eObject);
        StringBuffer result = new StringBuffer();
        String facetName    = facet.getFacetName();
        String lexicalValue = (facet.getLexicalValue() == null ? "" : facet.getLexicalValue()); //$NON-NLS-1$
        String facetType    = this.getFacetType(facet);
        String stereotype   = this.getStereotype(facet);
        //case 16 is for properties, which should return an empty string, so 
        //it has been added in to the remaining cases where applicable.
        switch (showMask) {
            case 1 :
            case 17:
                //Name
                result.append(lexicalValue);
                break;
            case 2 :
            case 18:
                //Stereotype
                result.append("<<"); //$NON-NLS-1$
                result.append(stereotype);    
                result.append(">>"); //$NON-NLS-1$    
                break;
            case 3 :
            case 19:
                //Name and Stereotype
                result.append("<<"); //$NON-NLS-1$
                result.append(stereotype);     
                result.append(">> "); //$NON-NLS-1$ 
                result.append(lexicalValue);        
                break;
            case 4 :
            case 20: 
                //Type
                result.append(facetType);
                break;
            case 5 :
            case 21:
                //Name and type
                result.append(lexicalValue);
                result.append(" : "); //$NON-NLS-1$
                result.append(facetType);
                if ( lexicalValue != null && lexicalValue.length() > 0 ) {
                    result.append("(" + lexicalValue + ")"); //$NON-NLS-1$ //$NON-NLS-2$
                }
                break;
            case 6 :
            case 22:
                //Type and Stereotype
                result.append("<<"); //$NON-NLS-1$
                result.append(stereotype);     
                result.append(">>"); //$NON-NLS-1$                 
                result.append(" : "); //$NON-NLS-1$
                result.append(facetType);
                if ( lexicalValue != null && lexicalValue.length() > 0 ) {
                    result.append("(" + lexicalValue + ")"); //$NON-NLS-1$ //$NON-NLS-2$
                }
                break;
            case 7 :
            case 23:
                //Name, Stereotype and type
                result.append("<<"); //$NON-NLS-1$
                result.append(stereotype);     
                result.append(">> "); //$NON-NLS-1$                 
                result.append(facetName);
                result.append(" : "); //$NON-NLS-1$
                result.append(facetType);
                if ( lexicalValue != null && lexicalValue.length() > 0 ) {
                    result.append("(" + lexicalValue + ")"); //$NON-NLS-1$ //$NON-NLS-2$
                }
                break;
            case 8 :
            case 24:
                //Initial Value
                result.append(""); //$NON-NLS-1$
                break;
            case 9 :
            case 25:
                //Name and Initial Value
                result.append(facetName);
                result.append(" "); //$NON-NLS-1$
                result.append(lexicalValue);
                break;
            case 10 :
            case 26 :
                //Initial Value and Stereotype
                result.append(lexicalValue);
                result.append(" <<"); //$NON-NLS-1$
                result.append(stereotype);
                result.append(">>"); //$NON-NLS-1$
                break;
            case 11 :
            case 27 :
                //Stereotype, Name and Initial Value, 
                result.append("<<"); //$NON-NLS-1$
                result.append(stereotype);
                result.append(">> "); //$NON-NLS-1$
                result.append(facetName);
                result.append(" "); //$NON-NLS-1$
                result.append(lexicalValue);
                break;
            case 12 :
            case 28 :
                //Initial Value and Type
                result.append(lexicalValue);
                result.append(" : "); //$NON-NLS-1$
                result.append(facetType);
            break;
            case 13 :
            case 29 :
                //Name, Type, InitialValue 
                result.append(facetName);
                result.append(" : "); //$NON-NLS-1$
                result.append(facetType);
                result.append(" "); //$NON-NLS-1$
                result.append(lexicalValue);
                break;
            case 14 :
            case 30 :
                //Stereotype, Type and Initial Value
                result.append("<<"); //$NON-NLS-1$
                result.append(stereotype);
                result.append(">>"); //$NON-NLS-1$
                result.append(" : "); //$NON-NLS-1$
                result.append(facetType);
                result.append(" "); //$NON-NLS-1$
                result.append(lexicalValue);
                break;
            case 15 :
            case 31 :
                //Name, Stereotype, Type and Initial Value
                result.append("<<"); //$NON-NLS-1$
                result.append(stereotype);
                result.append(">> "); //$NON-NLS-1$
                result.append(facetName);
                result.append(" : "); //$NON-NLS-1$
                result.append(facetType);
                result.append(" "); //$NON-NLS-1$
                result.append(lexicalValue);
                break;
            case 16 :
                //Properties
                return (""); //$NON-NLS-1$
            default :
                throw new MetaMatrixRuntimeException(RelationalPlugin.Util.getString("Aspect.invalidShowMask", showMask)); //$NON-NLS-1$
        }
        return result.toString();
    }
    
    public String getFacetType(Object eObject) {
        XSDFacetImpl facet = assertFacet(eObject);
        String type = null;
        if (facet instanceof XSDOrderedFacet) {
            type = XsdPlugin.getPluginResourceLocator().getString("_UI_XSDOrderedFacet_type"); //$NON-NLS-1$

        } else if (facet instanceof XSDBoundedFacet) {
            type = XsdPlugin.getPluginResourceLocator().getString("_UI_XSDBoundedFacet_type"); //$NON-NLS-1$

        } else if (facet instanceof XSDCardinalityFacet) {
            type = XsdPlugin.getPluginResourceLocator().getString("_UI_XSDCardinalityFacet_type"); //$NON-NLS-1$

        } else if (facet instanceof XSDNumericFacet) {
            type = XsdPlugin.getPluginResourceLocator().getString("_UI_XSDNumericFacet_type"); //$NON-NLS-1$

        } else if (facet instanceof XSDLengthFacet) {
            type = XsdPlugin.getPluginResourceLocator().getString("_UI_XSDLengthFacet_type"); //$NON-NLS-1$

        } else if (facet instanceof XSDMinLengthFacet) {
            type = XsdPlugin.getPluginResourceLocator().getString("_UI_XSDMinLengthFacet_type"); //$NON-NLS-1$

        } else if (facet instanceof XSDMaxLengthFacet) {
            type = XsdPlugin.getPluginResourceLocator().getString("_UI_XSDMaxLengthFacet_type"); //$NON-NLS-1$

        } else if (facet instanceof XSDPatternFacet) {
            type = XsdPlugin.getPluginResourceLocator().getString("_UI_XSDPatternFacet_type"); //$NON-NLS-1$

        } else if (facet instanceof XSDEnumerationFacet) {
            type = XsdPlugin.getPluginResourceLocator().getString("_UI_XSDEnumerationFacet_type"); //$NON-NLS-1$

        } else if (facet instanceof XSDWhiteSpaceFacet) {
            type = XsdPlugin.getPluginResourceLocator().getString("_UI_XSDWhiteSpaceFacet_type"); //$NON-NLS-1$

        } else if (facet instanceof XSDMaxInclusiveFacet) {
            type = XsdPlugin.getPluginResourceLocator().getString("_UI_XSDMaxInclusiveFacet_type"); //$NON-NLS-1$

        } else if (facet instanceof XSDMaxExclusiveFacet) {
            type = XsdPlugin.getPluginResourceLocator().getString("_UI_XSDMaxExclusiveFacet_type"); //$NON-NLS-1$

        } else if (facet instanceof XSDMinInclusiveFacet) {
            type = XsdPlugin.getPluginResourceLocator().getString("_UI_XSDMinInclusiveFacet_type"); //$NON-NLS-1$

        } else if (facet instanceof XSDMinExclusiveFacet) {
            type = XsdPlugin.getPluginResourceLocator().getString("_UI_XSDMinExclusiveFacet_type"); //$NON-NLS-1$

        } else if (facet instanceof XSDTotalDigitsFacet) {
            type = XsdPlugin.getPluginResourceLocator().getString("_UI_XSDTotalDigitsFacet_type"); //$NON-NLS-1$

        } else if (facet instanceof XSDFractionDigitsFacet) {
            type = XsdPlugin.getPluginResourceLocator().getString("_UI_XSDFractionDigitsFacet_type"); //$NON-NLS-1$
        }
        if (type == null) {
            type = XsdPlugin.getPluginResourceLocator().getString("_UI_XSDConstrainingFacet_type"); //$NON-NLS-1$
        }
        return type;
    }

    public String getEditableSignature(Object eObject) {
        return getSignature(eObject, UmlProperty.SIGNATURE_NAME);
    }

    protected XSDFacetImpl assertFacet(Object eObject) {
        CoreArgCheck.isInstanceOf(XSDFacetImpl.class, eObject);
    
        return (XSDFacetImpl)eObject;
    }

}
