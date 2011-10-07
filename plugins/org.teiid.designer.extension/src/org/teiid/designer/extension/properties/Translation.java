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
import org.teiid.designer.extension.Messages;

import com.metamatrix.core.util.CoreArgCheck;

/**
 * A translation related to a specific {@link Locale}.
 */
public class Translation {

    private Locale locale;
    private String translation;

    public Translation( Locale locale,
                        String translation ) {
        CoreArgCheck.isNotNull(locale, "locale is null"); //$NON-NLS-1$
        this.locale = locale;
        this.translation = translation;
    }

    /**
     * @return the locale of the translation
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * @return the translation
     */
    public String getTranslation() {
        return translation;
    }

    /**
     * If the new locale is <code>null</code> the current locale is not changed.
     * 
     * @param newLocale the new locale
     */
    public void setLocale( Locale newLocale ) {
        if ((newLocale != null) && !this.locale.equals(newLocale)) {
            this.locale = newLocale;
        }
    }

    /**
     * @param newTranslation the new translation
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
