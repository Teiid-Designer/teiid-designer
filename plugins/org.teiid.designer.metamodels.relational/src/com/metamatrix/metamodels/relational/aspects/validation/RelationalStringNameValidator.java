package com.metamatrix.metamodels.relational.aspects.validation;

import com.metamatrix.metamodels.relational.RelationalPlugin;
import com.metamatrix.modeler.core.validation.rules.StringNameValidator;


/**
 * This class provides the RelationlStringNameRule the ability to relax the valid characters allowed for relational names.
 * 
 *
 */
public class RelationalStringNameValidator extends StringNameValidator {
	
	boolean isTable = false;
	boolean restrictChars = false;

	public RelationalStringNameValidator(boolean isTable, boolean restrictChars) {
		super(new char[] {UNDERSCORE_CHARACTER});
		this.isTable = isTable;
		this.restrictChars = restrictChars;
	}

	@Override
	public String getValidNonLetterOrDigitMessageSuffix() {
		if( isTable ) {
			return RelationalPlugin.Util.getString("RelationalStringNameValidator.or_other_valid_table_characters"); //$NON-NLS-1$
		}
		return super.getValidNonLetterOrDigitMessageSuffix();
	}
	
	@Override
	public boolean isValidNonLetterOrDigit(char c) {
		if( !restrictChars ) {
			if( isTable  ) {
				return true;
			} else if( c != '.' ) {
				return true;
			}
		}
        return super.isValidNonLetterOrDigit(c);
	}

}
