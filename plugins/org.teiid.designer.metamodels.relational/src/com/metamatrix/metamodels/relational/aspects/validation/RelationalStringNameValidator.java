package com.metamatrix.metamodels.relational.aspects.validation;

import com.metamatrix.metamodels.relational.RelationalPlugin;
import com.metamatrix.modeler.core.validation.rules.StringNameValidator;


/**
 * This class provides the RelationlStringNameRule the ability to relax the valid characters allowed for relational names.
 * 
 *
 */
public class RelationalStringNameValidator extends StringNameValidator {
	
	private static char[] extraValidChars = {'-', '_', '#', '@', '&', '=', '+', '<', '>', '$', '%', UNDERSCORE_CHARACTER};
	private static char[] extraValidTableChars = {'.', '-', '_', '#', '@', '&', '=', '+', '<', '>', '$', '%', UNDERSCORE_CHARACTER};
	
	boolean isTable = false;
	boolean restrictChars = false;

	public RelationalStringNameValidator(char[] invalidCharacters, boolean isTable, boolean restrictChars) {
		super(invalidCharacters);
		this.isTable = isTable;
		this.restrictChars = restrictChars;
	}


	@Override
	public String getValidNonLetterOrDigitMessageSuffix() {
		if( isTable ) {
			return RelationalPlugin.Util.getString("RelationalStringNameValidator.or_other_valid_table_characters"); //$NON-NLS-1$
		}
		return RelationalPlugin.Util.getString("RelationalStringNameValidator.or_other_valid_characters"); //$NON-NLS-1$
	}
	
	@Override
	public boolean isValidNonLetterOrDigit(char c) {
		if( isTable  ) {
			if( !restrictChars ) {
				for( char next : extraValidTableChars ) {
					if( next == c ) {
						return true;
					}
				}
			}
		} else {
			if( !restrictChars ) {
				for( char next : extraValidChars ) {
					if( next == c ) {
						return true;
					}
				}
			}
		}
        return false;
	}

}
