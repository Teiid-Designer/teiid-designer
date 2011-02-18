/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.util;

import com.metamatrix.core.util.IOperation;
import com.metamatrix.core.util.IReturningOperation;
import com.metamatrix.core.util.ISafeOperation;
import com.metamatrix.core.util.ISafeReturningOperation;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.modeler.core.ModelerCore;

/**
 * A collection of utilities for executing operations within a transaction.
 * 
 * @since 5.0.2
 */
public class TransactionUtil implements
                            CoreStringUtil.Constants {

    // ===========================================================================================================================
    // Static Methods

    /**
     * Executes the specified operation within an undoable transaction. The specified description will be used by UI components
     * such as in the description of what will be undone shown in an undo menu option.
     * 
     * @param operation
     * @param source
     * @param description
     * @throws Exception
     * @since 5.0.2
     */
    public static void execute(IOperation operation,
                               Object source,
                               String description) throws Exception {
        execute(operation, source, true, description, true);
    }

    /**
     * Executes the specified operation within a safe (i.e., no checked exceptions are thrown), undoable transaction. The
     * specified description will be used by UI components such as in the description of what will be undone shown in an undo menu
     * option.
     * 
     * @param operation
     * @param source
     * @param description
     * @since 5.0.2
     */
    public static void execute(ISafeOperation operation,
                               Object source,
                               String description) {
        execute(operation, source, true, description, true);
    }

    /**
     * Executes the specified operation within an undoable transaction and returns the result. The specified description will be
     * used by UI components such as in the description of what will be undone shown in an undo menu option.
     * 
     * @param operation
     * @param source
     * @param description
     * @return The result of the specified operation.
     * @throws Exception
     * @since 5.0.2
     */
    public static Object execute(IReturningOperation operation,
                                 Object source,
                                 String description) throws Exception {
        return execute(operation, source, true, description, true);
    }

    /**
     * Executes the specified operation within a safe (i.e., no checked exceptions are thrown), undoable transaction and returns
     * the result. The specified description will be used by UI components such as in the description of what will be undone shown
     * in an undo menu option.
     * 
     * @param operation
     * @param source
     * @param description
     * @return The result of the specified operation.
     * @since 5.0.2
     */
    public static Object execute(ISafeReturningOperation operation,
                                 Object source,
                                 String description) {
        return execute(operation, source, true, description, true);
    }

    private static void execute(IOperation operation,
                                Object source,
                                boolean undoable,
                                String description,
                                boolean significant) throws Exception {
        boolean started = ModelerCore.startTxn(undoable, significant, description, source);
        boolean succeeded = false;
        try {
            operation.execute();
            succeeded = true;
        } finally {
            if (started) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
    }

    private static void execute(ISafeOperation operation,
                                Object source,
                                boolean undoable,
                                String description,
                                boolean significant) {
        boolean started = ModelerCore.startTxn(undoable, significant, description, source);
        boolean succeeded = false;
        try {
            operation.execute();
            succeeded = true;
        } finally {
            if (started) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
    }

    private static Object execute(IReturningOperation operation,
                                  Object source,
                                  boolean undoable,
                                  String description,
                                  boolean significant) throws Exception {
        boolean started = ModelerCore.startTxn(undoable, significant, description, source);
        boolean succeeded = false;
        try {
            Object result = operation.execute();
            succeeded = true;
            return result;
        } finally {
            if (started) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
    }

    private static Object execute(ISafeReturningOperation operation,
                                  Object source,
                                  boolean undoable,
                                  String description,
                                  boolean significant) {
        boolean started = ModelerCore.startTxn(undoable, significant, description, source);
        boolean succeeded = false;
        try {
            Object result = operation.execute();
            succeeded = true;
            return result;
        } finally {
            if (started) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
    }

    /**
     * Executes the specified operation within an insignificant transaction, meaning the operation can be undone, but only within
     * the context of a significant encompassing operation.
     * 
     * @param operation
     * @param source
     * @throws Exception
     * @since 5.0.2
     */
    public static void executeInsignificant(IOperation operation,
                                            Object source) throws Exception {
        execute(operation, source, true, EMPTY_STRING, false);
    }

    /**
     * Executes the specified operation within a safe (i.e., no checked exceptions are thrown), insignificant transaction, meaning
     * the operation can be undone, but only within the context of a significant encompassing operation.
     * 
     * @param operation
     * @param source
     * @since 5.0.2
     */
    public static void executeInsignificant(ISafeOperation operation,
                                            Object source) {
        execute(operation, source, true, EMPTY_STRING, false);
    }

    /**
     * Executes the specified operation and returns the result within an insignificant transaction, meaning the operation can be
     * undone, but only within the context of a significant encompassing operation.
     * 
     * @param operation
     * @param source
     * @return The result of the specified operation.
     * @throws Exception
     * @since 5.0.2
     */
    public static Object executeInsignificant(IReturningOperation operation,
                                              Object source) throws Exception {
        return execute(operation, source, true, EMPTY_STRING, false);
    }

    /**
     * Executes the specified operation and returns the result within a safe (i.e., no checked exceptions are thrown),
     * insignificant transaction, meaning the operation can be undone, but only within the context of a significant encompassing
     * operation.
     * 
     * @param operation
     * @param source
     * @return The result of the specified operation.
     * @since 5.0.2
     */
    public static Object executeInsignificant(ISafeReturningOperation operation,
                                              Object source) {
        return execute(operation, source, true, EMPTY_STRING, false);
    }

    /**
     * Executes the specified operation within a non-undoable transaction.
     * 
     * @param operation
     * @param source
     * @throws Exception
     * @since 5.0.2
     */
    public static void executeNonUndoable(IOperation operation,
                                          Object source) throws Exception {
        execute(operation, source, false, EMPTY_STRING, false);
    }

    /**
     * Executes the specified operation within a safe (i.e., no checked exceptions are thrown), non-undoable transaction.
     * 
     * @param operation
     * @param source
     * @since 5.0.2
     */
    public static void executeNonUndoable(ISafeOperation operation,
                                          Object source) {
        execute(operation, source, false, EMPTY_STRING, false);
    }

    /**
     * Executes the specified operation within a non-undoable transaction and returns the result.
     * 
     * @param operation
     * @param source
     * @return The result of the specified operation.
     * @throws Exception
     * @since 5.0.2
     */
    public static Object executeNonUndoable(IReturningOperation operation,
                                            Object source) throws Exception {
        return execute(operation, source, false, EMPTY_STRING, false);
    }

    /**
     * Executes the specified operation within a safe (i.e., no checked exceptions are thrown), non-undoable transaction and
     * returns the result.
     * 
     * @param operation
     * @param source
     * @return The result of the specified operation.
     * @since 5.0.2
     */
    public static Object executeNonUndoable(ISafeReturningOperation operation,
                                            Object source) {
        return execute(operation, source, false, EMPTY_STRING, false);
    }

    // ===========================================================================================================================
    // Constructors

    /**
     * @since 5.0.2
     */
    private TransactionUtil() {
    }
}
