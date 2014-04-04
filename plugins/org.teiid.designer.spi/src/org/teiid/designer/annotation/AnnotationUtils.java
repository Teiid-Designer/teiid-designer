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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.Assert;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;

/**
 *
 */
public class AnnotationUtils {

    private AnnotationUtils() {}

    /**
     * @param klazz
     * @param annotationClass
     *
     * @return given class has the given annotation
     */
    public static boolean hasAnnotation(Class<?> klazz, Class<? extends Annotation> annotationClass) {
        return klazz.isAnnotationPresent(annotationClass);
    }

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
     */
    public static boolean hasAnnotation(Enum<?> enumValue, Class<? extends Annotation> annotationClass) {
        try {
            Field enumField = enumValue.getClass().getField(enumValue.name());
            return hasAnnotation(enumField, annotationClass);
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * @param klazz
     * @param annotationClass
     *
     * @return the annotation of the given class from the given class
     */
    public static <T extends Annotation> T getAnnotation(Class<?> klazz, Class<T> annotationClass) {
        return klazz.getAnnotation(annotationClass);
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
     */
    public static <T extends Annotation> T getAnnotation(Enum<?> enumValue, Class<T> annotationClass) {
        try {
            Field enumField = enumValue.getClass().getField(enumValue.name());
            return getAnnotation(enumField, annotationClass);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
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

        return isGreaterOrEqualThan(removed.value().get(), currentVersion);
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

        return isGreaterOrEqualThan(since.value().get(), currentVersion);
    }

    /**
     * Convenience function that draws on the other functions to give a single
     * answer of whether the given object is applicable for the given teiid version
     *
     * @param obj
     * @param currentVersion
     * @return true if the given object is applicable for the given teiid version
     */
    public static boolean isApplicable(Class<?> obj, ITeiidServerVersion currentVersion) {
        if (hasAnnotation(obj, Removed.class)) {
            Removed removed = getAnnotation(obj, Removed.class);
            if (isGreaterThanOrEqualTo(removed, currentVersion)) {
                return false;
            }
        }

        if (hasAnnotation(obj, Since.class)) {
            Since since = getAnnotation(obj, Since.class);
            if (!isGreaterThanOrEqualTo(since, currentVersion)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Convenience function that draws on the other functions to give a single
     * answer of whether the given object is applicable for the given teiid version
     *
     * @param obj
     * @param currentVersion
     * @return true if the given object is applicable for the given teiid version
     */
    public static boolean isApplicable(AccessibleObject obj, ITeiidServerVersion currentVersion) {
        if (hasAnnotation(obj, Removed.class)) {
            Removed removed = getAnnotation(obj, Removed.class);
            if (isGreaterThanOrEqualTo(removed, currentVersion)) {
                return false;
            }
        }

        if (hasAnnotation(obj, Since.class)) {
            Since since = getAnnotation(obj, Since.class);
            if (!isGreaterThanOrEqualTo(since, currentVersion)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Convenience function that draws on the other functions to give a single
     * answer of whether the given object is applicable for the given teiid version
     *
     * @param obj
     * @param currentVersion
     * @return true if the given object is applicable for the given teiid version
     */
    public static boolean isApplicable(Enum<?> obj, ITeiidServerVersion currentVersion) {
        try {
            if (hasAnnotation(obj, Removed.class)) {
                Removed removed = getAnnotation(obj, Removed.class);
                if (isGreaterThanOrEqualTo(removed, currentVersion)) {
                    return false;
                }
            }
        } catch (Exception ex) {
            return false;
        }

        try {
            if (hasAnnotation(obj, Since.class)) {
                Since since = getAnnotation(obj, Since.class);
                if (!isGreaterThanOrEqualTo(since, currentVersion)) {
                    return false;
                }
            }
        } catch (Exception ex) {
            return false;
        }

        return true;
    }

    private static class UpdateVersionPair implements Comparable<UpdateVersionPair>{

        private final ITeiidServerVersion version;

        private final String replacedValue;

        public UpdateVersionPair(ITeiidServerVersion version, String replacedValue) {
            this.version = version;
            this.replacedValue = replacedValue;
        }

        /**
         * @return the version
         */
        public ITeiidServerVersion getVersion() {
            return this.version;
        }

        /**
         * @return the replacedValue
         */
        public String getReplacedValue() {
            return this.replacedValue;
        }

        @Override
        public int compareTo(UpdateVersionPair other) {
            if (getVersion().equals(other.getVersion()))
                return 0;
            else if (getVersion().isLessThan(other.getVersion()))
                return -1;
            else
                return 1;
        }
    }

    /**
     * @param enumValue
     * @param updatedName
     * @param currentVersion
     *
     * @return old name from {@link Updated} name or given updated name depending on teiid version
     */
    public static String getUpdatedName(Enum<?> enumValue, String updatedName, ITeiidServerVersion currentVersion) {
        if (! hasAnnotation(enumValue, Updated.class))
            return updatedName;

        Updated updated = getAnnotation(enumValue, Updated.class);
        Version[] versions = updated.version();
        String[] replacements = updated.replaces();

        List<UpdateVersionPair> pairs = new ArrayList<UpdateVersionPair>();
        for (int i = 0; i < versions.length; ++i) {
            Version version = versions[i];
            if (i >= replacements.length)
                break;

            String replaced = replacements[i];
            pairs.add(new UpdateVersionPair(version.get(), replaced));
        }

        Collections.sort(pairs);

        // Check biggest version and if current version is greater than
        // it then returned the updated name since the latest value is
        // the correct one.
        UpdateVersionPair maxPair = pairs.get(pairs.size() - 1);
        if (currentVersion.isGreaterThanOrEqualTo(maxPair.getVersion()))
            return updatedName;

        UpdateVersionPair rangePair = null;
        for (UpdateVersionPair pair : pairs) {
            if (rangePair == null) {
                rangePair = pair;
                continue;
            }

            if (currentVersion.isLessThan(pair.getVersion())) {
                // the last rangePair will have been selected
                break;
            }

            rangePair = pair;
            if (currentVersion.equals(rangePair.getVersion())) {
                // the new rangePair is the correct version 
                break;
            }

            // current version is greater than range pair so keep iterating
        }

        // Found appropriate version pair so return its replaced value
        return rangePair.getReplacedValue();
    }
}
