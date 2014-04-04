/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.annotation;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;

/**
 * Used for decorating classes, fields and methods defining their
 * removal at a specific teiid server version
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Inherited
public @interface Removed {

    /**
     * The version of the Teiid Server that the class,
     * field or method is no longer applicable for, ie. has been removed.
     * The value is one of the values of the enum {@link Version}.
     *
     * The default minimum version is considered
     * {@link Version#TEIID_8_0}.
     */
    Version value() default Version.TEIID_8_0;
}
