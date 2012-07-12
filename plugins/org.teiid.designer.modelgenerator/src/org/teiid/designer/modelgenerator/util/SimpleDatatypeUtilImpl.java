/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.util;

import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.types.DatatypeManager;



/**
 * SimpleDatatypeUtilImpl
 */
public class SimpleDatatypeUtilImpl implements SimpleDatatypeUtil {
    
    private static final DatatypeManager MANAGER = ModelerCore.getWorkspaceDatatypeManager();

    /**
     * Construct an instance of SimpleDatatypeUtilImpl.
     * 
     */
    public SimpleDatatypeUtilImpl() {
        super();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.modelgenerator.util.SimpleDatatypeUtil#isSimpleDatatypeBinary(org.eclipse.emf.ecore.EObject)
     */
    public boolean isSimpleDatatypeString(EObject datatype) {
        return MANAGER.isCharacter(datatype);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.modelgenerator.util.SimpleDatatypeUtil#isSimpleDatatypeNumeric(org.eclipse.emf.ecore.EObject)
     */
    public boolean isSimpleDatatypeNumeric(EObject datatype) {
        return MANAGER.isNumeric(datatype);
    }

}
