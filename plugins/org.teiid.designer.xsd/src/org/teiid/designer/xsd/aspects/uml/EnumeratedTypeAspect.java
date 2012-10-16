/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.xsd.aspects.uml;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xsd.XSDLengthFacet;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.teiid.core.designer.TeiidDesignerRuntimeException;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.uml.UmlClassifier;
import org.teiid.designer.core.metamodel.aspect.uml.UmlProperty;
import org.teiid.designer.core.types.DatatypeManager;
import org.teiid.designer.xsd.PluginConstants;
import org.teiid.designer.xsd.util.ModelerXsdUtils;


/**
 * ViewAspect
 *
 * @since 8.0
 */
public class EnumeratedTypeAspect extends AbstractXsdEntityAspect
                                  implements PluginConstants,
                                             UmlClassifier {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    public EnumeratedTypeAspect(MetamodelEntity theEntity) {
        super(theEntity);
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    private XSDSimpleTypeDefinition assertEnumeratedType(Object theObject) {
        if ((theObject != null) && (theObject instanceof EObject) && ModelerXsdUtils.isEnumeratedType((EObject)theObject)) {
            return (XSDSimpleTypeDefinition)theObject;
        }
        
        String msg = Util.getString(I18nUtil.getPropertyPrefix(EnumeratedTypeAspect.class) + "nullOrWrongType"); //$NON-NLS-1$
        throw new IllegalArgumentException(msg);
    }
    
    /** 
     * @see org.teiid.designer.xsd.aspects.uml.AbstractXsdEntityAspect#getEditableSignature(java.lang.Object)
     * @since 5.0.2
     */
    @Override
	public String getEditableSignature(Object theObject) {
        return getSignature(theObject, UmlClassifier.SIGNATURE_NAME);
    }
    
    /** 
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlClassifier#getRelationships(java.lang.Object)
     * @since 5.0.2
     */
    @Override
	public Collection getRelationships(Object theObject) {
        return Collections.EMPTY_LIST;
    }

    /** 
     * @see org.teiid.designer.xsd.aspects.uml.AbstractXsdEntityAspect#getSignature(java.lang.Object, int)
     * @since 5.0.2
     */
    @Override
	public String getSignature(Object theObject,
                               int theShowMask) {
        XSDSimpleTypeDefinition type = assertEnumeratedType(theObject);
        StringBuffer result = new StringBuffer();

        switch (theShowMask) {
            case UmlProperty.SIGNATURE_NAME: {
                // Name
                String name = type.getName();
                
                if (CoreStringUtil.isEmpty(name)) {
                    EObject parent = type.eContainer();
                    
                    if (parent instanceof XSDNamedComponent) {
                        name = ((XSDNamedComponent)parent).getName();
                    }
                }

                result.append(name);
                break;
            } case UmlProperty.SIGNATURE_STEROTYPE: {
                // Stereotype
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(theObject));
                result.append(">>"); //$NON-NLS-1$
                break;
            } case 3: {
                // Name and Stereotype
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(theObject));
                result.append(">> "); //$NON-NLS-1$                
                result.append(getSignature(theObject, UmlProperty.SIGNATURE_NAME));
                break;
            } default: {
                String msg = Util.getString(I18nUtil.getPropertyPrefix(EnumeratedTypeAspect.class) + "invalidShowMask", new Integer(theShowMask)); //$NON-NLS-1$
                throw new TeiidDesignerRuntimeException(msg);
            }
        }

        return result.toString();
    }
    
    /** 
     * @see org.teiid.designer.xsd.aspects.uml.AbstractXsdEntityAspect#getStereotype(java.lang.Object)
     * @since 5.0.2
     */
    @Override
	public String getStereotype(Object theObject) {
        final String PREFIX = I18nUtil.getPropertyPrefix(EnumeratedTypeAspect.class);
        XSDSimpleTypeDefinition type = assertEnumeratedType(theObject);
        DatatypeManager dm = ModelerCore.getDatatypeManager(type);
        type = (XSDSimpleTypeDefinition)dm.getBuiltInPrimitiveType(type);

        String result = null;
        XSDLengthFacet lengthFacet = type.getLengthFacet();
        
        if (lengthFacet == null) {
            result = Util.getString(PREFIX + "stereoType", type.getName()); //$NON-NLS-1$
        } else {
            result = Util.getString(PREFIX + "stereoTypeWithLength", new Object[] {type.getName(), lengthFacet.getEffectiveValue()}); //$NON-NLS-1$
        }

        return result;
    }
    
    /** 
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlClassifier#getSupertypes(java.lang.Object)
     * @since 5.0.2
     */
    @Override
	public Collection getSupertypes(Object theObject) {
        return Collections.EMPTY_LIST;
    }
    
    /** 
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlClassifier#isAbstract(java.lang.Object)
     * @since 5.0.2
     */
    @Override
	public boolean isAbstract(Object theObject) {
        return false;
    }
    
    /** 
     * @see org.teiid.designer.xsd.aspects.uml.AbstractXsdEntityAspect#setSignature(java.lang.Object, java.lang.String)
     * @since 5.0.2
     */
    @Override
	public IStatus setSignature(Object theObject,
                                String theNewSignature) {
        // cannot change a type name
        return Status.OK_STATUS;
    }
    
}
