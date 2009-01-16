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

package com.metamatrix.modeler.core.workspace;

/**
 * Status codes used with model status objects.
 * <p>
 * This interface declares constants only; it is not intended to be implemented
 * or extended.
 * </p>
 *
 * @see org.eclipse.core.runtime.IStatus#getCode()
 */
public interface ModelStatusConstants {

    /**
     * Status constant indicating a core exception occurred.
     * Use <code>getException</code> to retrieve a <code>CoreException</code>.
     */
    public static final int CORE_EXCEPTION = 966;
    
    /**
     * Status constant indicating one or more of the items
     * supplied are not of a valid type for the operation to
     * process. 
     * The item(s) can be retrieved using <code>getElements</code> on the status object.
     */
    public static final int INVALID_ITEM_TYPES = 967;

    /**
     * Status constant indicating that no items were
     * provided to the operation for processing.
     */
    public static final int NO_ITEMS_TO_PROCESS = 968;

    /**
     * Status constant indicating that one or more items
     * supplied do not exist. 
     * The item(s) can be retrieved using {@link ModelStatus#getModelWorkspaceItems()}
     * on the status object.
     *
     * @see ModelStatus#isDoesNotExist()
     */
    public static final int ITEM_DOES_NOT_EXIST = 969;

    /**
     * Status constant indicating that a <code>null</code> path was
     * supplied to the operation.
     */
    public static final int NULL_PATH = 970;
    
    /**
     * Status constant indicating that a path outside of the
     * project was supplied to the operation. The path can be retrieved using 
     * <code>getPath</code> on the status object.
     */
    public static final int PATH_OUTSIDE_PROJECT = 971;
    
    /**
     * Status constant indicating that a relative path 
     * was supplied to the operation when an absolute path is
     * required. The path can be retrieved using <code>getPath</code> on the
     * status object.
     */
    public static final int RELATIVE_PATH = 972;
    
    /**
     * Status constant indicating that a path specifying a device
     * was supplied to the operation when a path with no device is
     * required. The path can be retrieved using <code>getPath</code> on the
     * status object.
     */
    public static final int DEVICE_PATH = 973;
    
    /**
     * Status constant indicating that a string
     * was supplied to the operation that was <code>null</code>.
     */
    public static final int NULL_STRING = 974;
    
    /**
     * Status constant indicating that the operation encountered
     * a read-only item.
     * The item(s) can be retrieved using <code>getElements</code> on the status object.
     */
    public static final int READ_ONLY = 976;
    
    /**
     * Status constant indicating that a naming collision would occur
     * if the operation proceeded.
     */
    public static final int NAME_COLLISION = 977;
    
    /**
     * Status constant indicating that a destination provided for a copy/move/rename operation 
     * is invalid. 
     * The destination item can be retrieved using <code>getElements</code> on the status object.
     */
    public static final int INVALID_DESTINATION = 978;
    
    /**
     * Status constant indicating that a path provided to an operation 
     * is invalid. The path can be retrieved using <code>getPath</code> on the
     * status object.
     */
    public static final int INVALID_PATH = 979;
    
    /**
     * Status constant indicating the given source position is out of bounds.
     */
    public static final int INDEX_OUT_OF_BOUNDS = 980;
    
    /**
     * Status constant indicating there is an update conflict
     * for a working copy. The compilation unit on which the
     * working copy is based has changed since the working copy
     * was created.
     */
    public static final int UPDATE_CONFLICT = 981;

    /**
     * Status constant indicating that <code>null</code> was specified
     * as a name argument.
     */
    public static final int NULL_NAME = 982;

    /**
     * Status constant indicating that a name provided is not syntactically correct.
     * The name can be retrieved from <code>getString</code>.
     */
    public static final int INVALID_NAME = 983;

    /**
     * Status constant indicating that the specified contents
     * are not valid.
     */
    public static final int INVALID_CONTENTS = 984;

    /**
     * Status constant indicating that an <code>java.io.IOException</code>
     * occurred. 
     */
    public static final int IO_EXCEPTION = 985;

    /**
     * Status constant indicating that a <code>DOMException</code>
     * occurred. 
     */
    public static final int DOM_EXCEPTION = 986;

    /**
     * Status constant indicating that a <code>TargetException</code>
     * occurred. 
     */
    public static final int TARGET_EXCEPTION = 987;

    /**
     * Status constant indicating that the Java builder
     * could not be initialized.
     */
    public static final int BUILDER_INITIALIZATION_ERROR = 990;

    /**
     * Status constant indicating that the Java builder's last built state
     * could not be serialized or deserialized.
     */
    public static final int BUILDER_SERIALIZATION_ERROR = 991;

    /**
     * Status constant indicating that a sibling specified is not valid.
     */
    public static final int INVALID_SIBLING = 993;

    /**
     * Status indicating that a model workspace item could not be created because
     * the underlying resource is invalid.
     */
     public static final int INVALID_RESOURCE = 995;

    /**
     * Status indicating that a model workspace item could not be created because
     * the underlying resource is not of an appropriate type.
     */
     public static final int INVALID_RESOURCE_TYPE = 996;

    /**
     * Status indicating that a model workspace item could not be created because
     * the project owning underlying resource does not have the Model nature.
     * @see ModelerCore#NATURE_ID
     */
     public static final int INVALID_PROJECT = 997;

    /**
     * Status indicating that the corresponding resource has no local contents yet.
     * This might happen when attempting to use a resource before its contents
     * has been made locally available.
     */
     public static final int NO_LOCAL_CONTENTS = 999;
     
    /**
     * Status indicating that a .modelpath file is ill-formed, and thus cannot
     * be read/written successfully.
     * @since 2.1
     */
    public static final int INVALID_MODELPATH_FILE_FORMAT = 1000;     
}
