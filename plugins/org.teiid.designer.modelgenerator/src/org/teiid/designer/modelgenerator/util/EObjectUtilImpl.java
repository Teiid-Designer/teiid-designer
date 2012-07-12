/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.util;

import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.core.ModelEditor;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.ModelerCoreException;


/**
 * EObjectUtilImpl
 */
public class EObjectUtilImpl implements EObjectUtil {

    /**
     * Construct an instance of EObjectUtilImpl.
     * 
     */
    public EObjectUtilImpl() {
        super();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.modelgenerator.util.EObjectUtil#clone(org.eclipse.emf.ecore.EObject)
     */
    public EObject clone(EObject object) throws ModelerCoreException{
        ModelEditor editor = ModelerCore.getModelEditor();
        return editor.clone(object);
    }

}
