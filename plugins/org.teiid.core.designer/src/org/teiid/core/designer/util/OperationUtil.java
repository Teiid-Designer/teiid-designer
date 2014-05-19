/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.core.designer.util;


/**
 * 
 *
 * @since 8.0
 */
public final class OperationUtil {

    public static <T> T perform( final ReturningUnreliable<T> unreliable ) throws Exception {
        Throwable significantError = null;
        try {
            return unreliable.tryToDo();
        } catch (final Throwable error) {
            significantError = error;
            unreliable.doIfFails();
        } finally {
            try {
                unreliable.finallyDo();
            } catch (final Throwable error) {
                if (significantError == null) significantError = error;
            }

            if (significantError != null)
                throw new Exception(significantError);
        }
        return null; // Unreachable
    }

    public static void perform( final Unreliable unreliable ) throws Exception {
        Throwable significantError = null;
        try {
            unreliable.tryToDo();
        } catch (final Throwable error) {
            significantError = error;
            unreliable.doIfFails();
        } finally {
            try {
                unreliable.finallyDo();
            } catch (final Throwable error) {
                if (significantError == null) significantError = error;
            }
        }

        if (significantError != null)
            throw new Exception(significantError);
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
