/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.modelgenerator.util;

import org.eclipse.emf.ecore.EObject;

/**
 * NullSimpleDatatypeUtil
 */
public class NullSimpleDatatypeUtil implements SimpleDatatypeUtil{

    /**
     * Construct an instance of NullSimpleDatatypeUtil.
     * 
     */
    public NullSimpleDatatypeUtil() {
        super();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.modelgenerator.util.SimpleDatatypeUtil#isSimpleDatatypeNumeric(org.eclipse.emf.ecore.EObject)
     */
    public boolean isSimpleDatatypeNumeric(EObject datatype) {
        return false;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.modelgenerator.util.SimpleDatatypeUtil#isSimpleDatatypeBinary(org.eclipse.emf.ecore.EObject)
     */
    public boolean isSimpleDatatypeString(EObject datatype) {
        return false;
    }

}
