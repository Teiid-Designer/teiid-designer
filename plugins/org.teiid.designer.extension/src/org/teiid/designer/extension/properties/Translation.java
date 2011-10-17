/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.properties;

import java.util.Locale;

import org.eclipse.osgi.util.NLS;
import org.teiid.core.HashCodeUtil;
import org.teiid.designer.extension.Messages;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;

/**
 * A translation related to a specific {@link Locale}.
 */
public class Translation implements Comparable<Translation>{

    public static Translation copy( Translation source ) {
        return new Translation(source.getLocale(), source.getTranslation());
    }

    private Locale locale;
    private String translation;

    public Translation( Locale locale,
                        String translation ) {
        CoreArgCheck.isNotNull(locale, "locale is null"); //$NON-NLS-1$
        this.locale = locale;
        this.translation = translation;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo( Translation that ) {
        return this.locale.toString().compareTo(that.locale.toString());
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object object ) {
        if (this == object) {
            return true;
        }

        if ((object == null) || !getClass().equals(object.getClass())) {
            return false;
        }

        Translation other = (Translation)object;
        return this.locale.equals(other.locale) && CoreStringUtil.equals(this.translation, other.translation);
    }

    /**
     * @return the locale of the translation
     */
    public Locale getLocale() {
        return this.locale;
    }

    /**
     * @return the translation (can be <code>null</code> or empty)
     */
    public String getTranslation() {
        return this.translation;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = HashCodeUtil.hashCode(0, this.locale);

        if (this.translation != null) {
            result = HashCodeUtil.hashCode(0, this.translation);
        }

        return result;
    }

    /**
     * Only set if the locale is different than the current locale.
     * 
     * @param newLocale the new locale (cannot be <code>null</code>)
     */
    public void setLocale( Locale newLocale ) {
        CoreArgCheck.isNotNull(newLocale, "newLocale is null"); //$NON-NLS-1$

        if (!this.locale.equals(newLocale)) {
            this.locale = newLocale;
        }
    }

    /**
     * @param newTranslation the new translation (can be <code>null</code> or empty)
     */
    public void setTranslation( String newTranslation ) {
        this.translation = newTranslation;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return NLS.bind(Messages.translationToString, this.locale.getDisplayLanguage());
    }

}
