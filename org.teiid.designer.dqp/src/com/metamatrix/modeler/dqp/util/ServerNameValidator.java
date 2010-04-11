/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.dqp.util;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.validation.rules.StringNameValidator;

/**
 * 
 */
public class ServerNameValidator extends StringNameValidator {

    /**
     * @param minLength
     * @param maxLength
     * @param caseSensitive
     * @param replacementCharacter
     * @param invalidCharacters
     */
    public ServerNameValidator( int minLength,
                                int maxLength,
                                char[] invalidCharacters ) {
        super(minLength, maxLength, invalidCharacters);

    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.core.validation.rules.StringNameValidator#checkNameCharacters(java.lang.String)
     */
    @Override
    public String checkNameCharacters( String name ) {
        CoreArgCheck.isNotNull(name);

        // Go through the string and ensure that each character is valid ...
        CharacterIterator charIter = new StringCharacterIterator(name);
        char c = charIter.first();
        int index = 1;

        // The first character must be an alphabetic character ...
        if (c != CharacterIterator.DONE) {
            if (!Character.isLetter(c)) {
                final Object[] params = new Object[] {new Character(c)};
                final String msg = ModelerCore.Util.getString("StringNameValidator.The_first_character_of_the_name_({0})_must_be_an_alphabetic_character", params); //$NON-NLS-1$
                return msg;
            }
            if (!isValidCharacter(c)) {
                final Object[] params = new Object[] {new Character(c)};
                final String msg = ModelerCore.Util.getString("StringNameValidator.The_first_character_of_the_name_({0})_is_not_a_valid_character", params); //$NON-NLS-1$
                return msg;
            }
            c = charIter.next();
            ++index;
        }

        // The remaining characters must be either alphabetic, digit or underscore character ...
        while (c != CharacterIterator.DONE) {
            if (c != '.' && c != '-') {
                if (!(Character.isUnicodeIdentifierPart(c) || Character.isLetterOrDigit(c) || c == UNDERSCORE_CHARACTER)) {
                    final Object[] params = new Object[] {new Character(c), new Integer(index)};
                    final String msg = ModelerCore.Util.getString("StringNameValidator.The_character___{0}___(at_position_{1})_is_not_allowed;_only_alphabetic,_digit_or_underscore", params); //$NON-NLS-1$
                    return msg;
                }
                if (!isValidCharacter(c)) {
                    final Object[] params = new Object[] {new Character(c), new Integer(index)};
                    final String msg = ModelerCore.Util.getString("StringNameValidator.The_character___{0}___(at_position_{1})_is_not_a_valid_character", params); //$NON-NLS-1$
                    return msg;
                }
            }
            char lastC = c;
            c = charIter.next();
            if (lastC == '.' && c == lastC) {
                final Object[] params = new Object[] {new Character(c), new Integer(index)};
                final String msg = ModelerCore.Util.getString("ServerNameValidator.The_character___{0}___(at_position_{1})_is_not_a_valid_character", params); //$NON-NLS-1$
                return msg;
            }
            ++index;
        }

        // Valid, so return no error message
        return null;

    }

}
