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

package com.metamatrix.modeler.core.metamodel.aspect.sql;

import java.util.List;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.modeler.core.index.IndexingContext;
import com.metamatrix.modeler.core.metadata.runtime.TransformationRecord;

/**
 * SqlTransformationAspect
 */
public interface SqlTransformationAspect extends SqlAspect {

    interface Types {
        public static final String MAPPING            = TransformationRecord.Types.MAPPING;
        public static final String SELECT             = TransformationRecord.Types.SELECT;
        public static final String INSERT             = TransformationRecord.Types.INSERT;
        public static final String UPDATE             = TransformationRecord.Types.UPDATE;
        public static final String DELETE             = TransformationRecord.Types.DELETE;
        public static final String PROCEDURE          = TransformationRecord.Types.PROCEDURE;
    }
    
    /**
     * Get the OR-ed set of types supported by this transformation definition.
     * @see Types.
     * @param eObject The <code>EObject</code> that defines this transformation 
     * @return the type constant for this transformation.
     * @see SqlTransformationAspect.Types
     */
    String[] getTransformationTypes(EObject eObject);

    /**
     * Get the transformation definition, which is typically an XML document containing the
     * tree of query nodes.
     * @param eObject The <code>EObject</code> that defines this transformation 
     * @return the string containing the definition of the transformation.
     */
    String getTransformation(EObject eObject, String type);
    
    /**
     * Get the object that is transformed by this transformation.
     * The transformed object has either an {@link SqlTableAspect} or an {@link SqlProcedureAspect},
     * depending upon the type of transformation. 
     * @param eObject The <code>EObject</code> that defines this transformation 
     * @return an identifier for the transformed object
     */    
    Object getTransformedObject(EObject eObject);
    
    /**
     * Get the objects that are inputs to this transformation.
     * The input objects are either a {@link SqlTableAspect} or a {@link SqlProcedureAspect},
     * depending upon the type of transformation. 
     * @param eObject The <code>EObject</code> that defines this transformation 
     * @return list of EObject instances
     */    
    List getInputObjects(EObject eObject);
    
    /**
     * Get the objects that are the nested inputs to this transformation.
     * The nested input objects are either a {@link SqlColumnAspect} or a {@link SqlProcedureParameterAspect},
     * depending upon the type of transformation. 
     * @param eObject The <code>EObject</code> that defines this transformation 
     * @return list of EObject instances
     */    
    List getNestedInputObjects(EObject eObject);
    
    /**
     * Get the objects that are the nested inputs to this transformation.
     * The nested input objects are either a {@link SqlColumnAspect} or a {@link SqlProcedureParameterAspect},
     * depending upon the type of transformation. 
     * @param eObject The <code>EObject</code> that defines this transformation 
     * @return list of EObject instances
     */    
    List getNestedInputsForOutput(EObject eObject, EObject output);
    
    /**
     * Get the objects that are output of this transformation.
     * The output objects are either a {@link SqlTableAspect} or a {@link SqlProcedureAspect},
     * depending upon the type of transformation. 
     * @param eObject The <code>EObject</code> that defines this transformation 
     * @return list of EObject instances
     */    
    List getOutputObjects(EObject eObject);
    
    /**
     * Get the objects that are the nested outputs to this transformation.
     * The nested output objects are either a {@link SqlColumnAspect} or a {@link SqlProcedureParameterAspect},
     * depending upon the type of transformation. 
     * @param eObject The <code>EObject</code> that defines this transformation 
     * @return list of EObject instances
     */    
    List getNestedOutputObjects(EObject eObject);
    
    /**
     * Get the objects that are the nested inputs to this transformation.
     * The nested input objects are either a {@link SqlColumnAspect} or a {@link SqlProcedureParameterAspect},
     * depending upon the type of transformation. 
     * @param eObject The <code>EObject</code> that defines this transformation 
     * @return list of EObject instances
     */    
    List getNestedOutputsForInput(EObject eObject, EObject input);

    /**
     * Get the transformation definition, which is typically sql definning the virtual group
     * and any binding information the the sql may contain.
     * @param eObject The <code>EObject</code> that defines this transformation 
     * @param context The context to use; may be null;
     * @return SqlTransformationInfo object that contains transformation string and any
     * references to bindings.
     */    
    SqlTransformationInfo getTransformationInfo(EObject eObject, IndexingContext context, String type);

    /**
     * Check if the transformation allows inserts. 
     * @return true if insert is allowed, else false
     * @since 4.3
     */
    boolean isInsertAllowed(EObject eObject);

    /**
     * Check if the transformation allows updates.
     * @return true if insert is allowed, else false
     * @since 4.3
     */
    boolean isUpdateAllowed(EObject eObject);

    /**
     * Check if the transformation allows deletes.
     * @return true if insert is allowed, else false
     * @since 4.3
     */
    boolean isDeleteAllowed(EObject eObject);
}
