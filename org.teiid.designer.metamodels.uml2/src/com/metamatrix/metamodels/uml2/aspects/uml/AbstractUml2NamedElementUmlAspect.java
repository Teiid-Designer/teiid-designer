/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.uml2.aspects.uml;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.uml2.uml.NamedElement;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.metamodels.uml2.Uml2Plugin;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlClassifier;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlProperty;

/**
 * ColumnAspect
 */
public abstract class AbstractUml2NamedElementUmlAspect extends AbstractUml2ElementUmlAspect {
    /**
     * Construct an instance of ColumnAspect.
     * 
     * @param entity
     */
    public AbstractUml2NamedElementUmlAspect( MetamodelEntity entity ) {
        super(entity);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodels.aspects.UmlDiagramAspect#setSignature(java.lang.Object, java.lang.String)
     */
    public IStatus setSignature( Object eObject,
                                 String newSignature ) {
        try {
            final NamedElement element = assertNamedElement(eObject);
            element.setName(newSignature);
        } catch (Throwable e) {
            return new Status(IStatus.ERROR, ModelerCore.PLUGIN_ID, 0, e.getMessage(), e);
        }

        final String msg = Uml2Plugin.Util.getString("AbstractUml2NamedElementUmlAspect.Signature_changed_1"); //$NON-NLS-1$
        return new Status(IStatus.OK, ModelerCore.PLUGIN_ID, 0, msg, null);
    }

    public String getEditableSignature( Object eObject ) {
        return getSignature(eObject, UmlProperty.SIGNATURE_NAME);
    }

    protected NamedElement assertNamedElement( Object eObject ) {
        CoreArgCheck.isInstanceOf(NamedElement.class, eObject);
        return (NamedElement)eObject;
    }

    protected void appendName( final NamedElement namedElement,
                               final StringBuffer sb ) {
        this.appendName(namedElement, sb, false);
    }

    protected void appendName( final NamedElement namedElement,
                               final StringBuffer sb,
                               final boolean upperCaseFirstLetter ) {
        String name = namedElement.getName();
        if (upperCaseFirstLetter) {
            char firstChar = Character.toUpperCase(name.charAt(0));
            sb.append(firstChar);
            sb.append(name.substring(1));
        } else {
            sb.append(name);
        }
    }

    public String getName( Object eObject ) { // NO_UCD
        return getSignature(eObject, UmlClassifier.SIGNATURE_NAME);
    }

}
