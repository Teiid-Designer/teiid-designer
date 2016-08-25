/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.extension.ui.editors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.eclipse.swt.custom.CCombo;
import org.teiid.designer.extension.ExtensionConstants;

/**
 * Provides matching locales based on a pattern.
 */
class LocaleProposalProvider extends CComboProposalProvider {

    private final char[] firstChars;
    private List<Locale> locales;

    /**
     * @param combo the combo whose proposals are being requested (cannot be <code>null</code>)
     */
    LocaleProposalProvider(final CCombo combo) {
        super(combo);

        List<Locale> tmpLocals = new ArrayList<Locale>(Arrays.asList(Locale.getAvailableLocales()));
        this.locales = new ArrayList<Locale>();
        for( Locale locale : tmpLocals) {
        	if( locale != null && locale.getDisplayName() != null && locale.getDisplayName().length() > 0 ) {
        		this.locales.add(locale);
        	}
        }
        
        Collections.sort(this.locales, ExtensionConstants.LOCALE_COMPARATOR);

        final Set<Character> temp = new HashSet<Character>();
        String chars = ""; //$NON-NLS-1$

        for (final Locale locale : this.locales) {
            char c = locale.getDisplayName().charAt(0);

            if (temp.add(c)) {
                chars += c;

                if (Character.isLetter(c)) {
                    if (Character.isUpperCase(c)) {
                        chars += Character.toLowerCase(c);
                    } else {
                        chars += Character.toUpperCase(c);
                    }
                }
            }
        }

        this.firstChars = chars.toCharArray();
    }

    /**
     * @see org.teiid.designer.extension.ui.editors.CComboProposalProvider#getActivationChars()
     */
    @Override
    protected char[] getActivationChars() {
        return this.firstChars;
    }

    /**
     * @see org.teiid.designer.extension.ui.editors.CComboProposalProvider#proposalsFor(java.lang.String)
     */
    @Override
    protected List<String> proposalsFor(final String pattern) {
        if ((pattern == null) || pattern.isEmpty()) {
            return Collections.emptyList();
        }

        final String lowerCasePattern = pattern.toLowerCase();
        final List<String> matches = new ArrayList<String>(6);
        boolean foundMatch = false;

        for (final Locale locale : this.locales) {
            final String displayName = locale.getDisplayName();
            final String lowerCaseDisplayName = displayName.toLowerCase();

            if (lowerCaseDisplayName.startsWith(lowerCasePattern)) {
                matches.add(displayName);
                foundMatch = true;
            } else if (foundMatch) {
                // since locales are sorted matches will be consecutive
                break;
            }
        }

        return matches;
    }

}
