/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.core.designer.util;

import java.io.File;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.teiid.core.designer.util.FileUtils.UnreliableCopy;

/**
 * 
 *
 * @since 8.0
 */
public final class ZipUtil {

    public static void copy( final File source,
                             final ZipEntry entry,
                             final ZipOutputStream destination ) throws Exception {
        OperationUtil.perform(new UnreliableCopy(source, destination) {

            @Override
            public void finallyDo() throws Exception {
                super.finallyDo();
                destination.closeEntry();
            }

            @Override
            public void tryToDo() throws Exception {
                destination.putNextEntry(entry);
                super.tryToDo();
            }
        });
    }

    private ZipUtil() {
    }
}
