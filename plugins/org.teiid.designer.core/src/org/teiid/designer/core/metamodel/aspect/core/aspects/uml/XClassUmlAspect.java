/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.metamodel.aspect.core.aspects.uml;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.teiid.core.designer.TeiidDesignerRuntimeException;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.uml.UmlClassifier;
import org.teiid.designer.metamodels.core.extension.XClass;


/**
 * TableAspect
 *
 * @since 8.0
 */
public class XClassUmlAspect extends AbstractExtensionUmlAspect implements UmlClassifier {
    /**
     * Construct an instance of TableAspect.
     * @param entity
     */
    public XClassUmlAspect(MetamodelEntity entity){
        super();
        setMetamodelEntity(entity);
    }
    
    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodels.aspects.UmlDiagramAspect#getStereotype(java.lang.Object)
     */
    @Override
	public String getStereotype(Object eObject) {
        final XClass xclass = assertXClass(eObject);
        final EClass extendedClass = xclass.getExtendedClass();
        if ( extendedClass != null ) {
            final EPackage epkg = extendedClass.getEPackage();
            final Object[] params = new Object[]{epkg.getName(),extendedClass.getName()};
            final String sterotype = ModelerCore.Util.getString("XClassUmlAspect.Extension_of",params); //$NON-NLS-1$
            return sterotype;
        }
        return ModelerCore.Util.getString("XClassUmlAspect.Stereotype_UnknownExtensionClass"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodels.aspects.UmlDiagramAspect#setSignature(java.lang.Object, java.lang.String)
     */
    @Override
	public IStatus setSignature(Object eObject, String newSignature) {
        try {
            final XClass xclass = assertXClass(eObject);
            xclass.setName(newSignature);
        } catch (Throwable e) {
            return new Status(IStatus.ERROR, ModelerCore.PLUGIN_ID, 0, e.getMessage(), e);
        }
        
        return new Status(IStatus.OK, ModelerCore.PLUGIN_ID, 0, ModelerCore.Util.getString("XClassUmlAspect.Signature_changed"), null); //$NON-NLS-1$
    }

    @Override
	public Collection getRelationships(Object eObject) {
        return new ArrayList();
//        final XClass xclass = assertXClass(eObject);
//        Collection results = new ArrayList();
//        results.addAll(table.getForeignKeys() );
//        return results;
    }

    @Override
	public Collection getSupertypes(Object eObject) {
        return new ArrayList();
    }

    @Override
	public String getSignature(Object eObject, int showMask) {
        final XClass xclass = assertXClass(eObject);
        StringBuffer result = new StringBuffer();
        switch (showMask) {
            case 1 :
                //Name
                result.append(xclass.getName() );
                break;
            case 2 :
                //Stereotype
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(eObject) );
                result.append(">>"); //$NON-NLS-1$
                break;
            case 3 :
                //Name and Stereotype
                result.append("<<"); //$NON-NLS-1$
                result.append(getStereotype(eObject) );
                result.append(">> "); //$NON-NLS-1$                
                result.append(xclass.getName() );        
                break;
            default :
                throw new TeiidDesignerRuntimeException(ModelerCore.Util.getString("XClassUmlAspect.Invalid_showMask_for_getSignature") + showMask ); //$NON-NLS-1$
        }
        return result.toString();
    }

    @Override
	public String getEditableSignature(Object eObject) {
        return getSignature(eObject, UmlClassifier.SIGNATURE_NAME);
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.uml.UmlClassifier#isAbstract(java.lang.Object)
     */
    @Override
	public boolean isAbstract(Object eObject) {
        return false;
    }

    protected XClass assertXClass(Object eObject) {
        CoreArgCheck.isInstanceOf(XClass.class, eObject);
    
        return (XClass)eObject;
    }

}
