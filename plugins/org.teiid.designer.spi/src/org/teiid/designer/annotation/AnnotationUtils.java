/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import org.eclipse.core.runtime.Assert;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion;

/**
 *
 */
public class AnnotationUtils {

    private AnnotationUtils() {}

    /**
     * @param accessibleObject
     * @param annotationClass
     *
     * @return given accessibleObject has the given annotation
     */
    public static boolean hasAnnotation(AccessibleObject accessibleObject, Class<? extends Annotation> annotationClass) {
        return accessibleObject.isAnnotationPresent(annotationClass);
    }

    /**
     * @param enumValue
     * @param annotationClass
     *
     * @return given enumValue has the given annotation on its field
     *
     * @throws Exception
     */
    public static boolean hasAnnotation(Enum<?> enumValue, Class<? extends Annotation> annotationClass) throws Exception {
        Field enumField = enumValue.getClass().getField(enumValue.name());
        return hasAnnotation(enumField, annotationClass);
    }

    /**
     * @param accessibleObject
     * @param annotationClass
     *
     * @return the annotation of the given class from the given accessibleObject
     */
    public static <T extends Annotation> T getAnnotation(AccessibleObject accessibleObject, Class<T> annotationClass) {
        return accessibleObject.getAnnotation(annotationClass);
    }

    /**
     * @param enumValue
     * @param annotationClass
     *
     * @return the annotation of the given class from the given enumValue
     *
     * @throws Exception
     */
    public static <T extends Annotation> T getAnnotation(Enum<?> enumValue, Class<T> annotationClass) throws Exception {
        Field enumField = enumValue.getClass().getField(enumValue.name());
        return getAnnotation(enumField, annotationClass);
    }

    /**
     * @param testVersion
     * @param currentVersion
     *
     * @return testVersion is greater than or equal to the currentVersion's minimum version
     */
    private static boolean isGreaterOrEqualThan(ITeiidServerVersion testVersion, ITeiidServerVersion currentVersion) {
        // Removed annotation versiion should NOT contain wildcards
        Assert.isLegal(! testVersion.hasWildCards());

        // Ensure we have no wildcards in the current version
        currentVersion = currentVersion.getMinimumVersion();
        if (currentVersion.equals(testVersion) || currentVersion.isGreaterThan(testVersion))
            return true;

        return false;
    }

    /**
     * <p>
     * Tests the given {@link Removed} annotation's version against
     * the given version. If the latter is greater than or equal to the
     * former than the method returns true, false otherwise.
     * <p>
     * If removed contains wildcards then its a programming error.
     * <p>
     * The current version can contain wildcards as these are
     * eliminated using {@link ITeiidServerVersion#getMinimumVersion()}
     * <p>
     * If the removed annotation is null then a value of false is returned
     * since they is no annotation to test against and the most preferred
     * outcome of uses of this test would be including the element that
     * lacks the annotation.
     *
     * @param removed annotation
     * @param currentVersion
     *
     * @return currentVersion >= removed value
     */
    public static boolean isGreaterThanOrEqualTo(Removed removed, ITeiidServerVersion currentVersion) {
        if (removed == null || currentVersion == null)
            return false;

        return isGreaterOrEqualThan(new TeiidServerVersion(removed.value()), currentVersion);
    }

    /**
     * <p>
     * Tests the given {@link Since} annotation's version against
     * the given version. If the latter is greater than or equal to the
     * former then the method returns true false otherwise.
     * <p>
     * If since contains wildcards then its a programming error.
     * <p>
     * The current version can contain wildcards as these are
     * eliminated using {@link ITeiidServerVersion#getMinimumVersion()}
     * <p>
     * If the since annotation is null then a value of true is returned
     * since they is no annotation to test against and the most preferred
     * outcome of uses of this test would be including the element that
     * lacks the annotation.
     *
     * @param since annotation
     * @param currentVersion
     *
     * @return currentVersion >= since value
     */
    public static boolean isGreaterThanOrEqualTo(Since since, ITeiidServerVersion currentVersion) {
        if (since == null || currentVersion == null)
            return true;

        return isGreaterOrEqualThan(new TeiidServerVersion(since.value()), currentVersion);
    }
}
