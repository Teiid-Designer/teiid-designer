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
 * Used for decorating classes, fields and methods defining
 * updated values for teiid versions.
 *
 * The fields act as pairs where with each new version the
 * value in 'replaced' was replaced. 
 *  
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Inherited
public @interface Updated {

    /**
     * @return list of versions where item was updated
     */
    Version[] version();

    /**
     * @return list of values replaced when item was updated
     */
    String[] replaces() default "";

}
