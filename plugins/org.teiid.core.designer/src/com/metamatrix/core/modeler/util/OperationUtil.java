/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core.modeler.util;

import com.metamatrix.core.modeler.CoreModelerPlugin;

/**
 * 
 */
public final class OperationUtil {

    public static <T> T perform( final ReturningUnreliable<T> unreliable ) {
        Exception significantError = null;
        try {
            return unreliable.tryToDo();
        } catch (final Exception error) {
            significantError = error;
            unreliable.doIfFails();
        } finally {
            try {
                unreliable.finallyDo();
            } catch (final Exception ignored) {
            }
            CoreModelerPlugin.throwRuntimeException(significantError);
        }
        return null; // Unreachable
    }

    public static void perform( final Unreliable unreliable ) {
        Exception significantError = null;
        try {
            unreliable.tryToDo();
        } catch (final Exception error) {
            significantError = error;
            unreliable.doIfFails();
        } finally {
            try {
                unreliable.finallyDo();
            } catch (final Exception ignored) {
            }
            CoreModelerPlugin.throwRuntimeException(significantError);
        }
    }

    private OperationUtil() {
    }

    public static interface ReturningUnreliable<T> {

        void doIfFails();

        void finallyDo() throws Exception;

        T tryToDo() throws Exception;
    }

    public static interface Unreliable {

        void doIfFails();

        void finallyDo() throws Exception;

        void tryToDo() throws Exception;
    }
}
