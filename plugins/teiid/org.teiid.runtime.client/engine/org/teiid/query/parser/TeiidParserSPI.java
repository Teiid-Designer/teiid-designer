package org.teiid.query.parser;

import java.util.Set;

import org.teiid.query.sql.lang.Comment;
import org.teiid.query.sql.lang.LanguageObject;

/**
 * Provides service methods that should not be generally available
 * on the {@link TeiidParser} interface
 */
public interface TeiidParserSPI extends TeiidParser {

	/**
	 * Tells the parser what the current node being parsed is
	 *
	 * @param langObject
	 */
	void setCurrentNode(LanguageObject langObject);

}
