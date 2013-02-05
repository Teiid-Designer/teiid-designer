/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.ui.common;

/**
 *
 */
public interface UiLabelConstants {

    /**
     * ID's for common buttons
     * @since 4.0
     */
	
	interface LABEL_IDS {
		int	ELIPSIS = 0;
		int	ADD = ELIPSIS + 1;
		int	DELETE = ADD + 1;
		int	REMOVE = DELETE + 1;
		int	MOVE_UP = REMOVE + 1;
		int	MOVE_DOWN = MOVE_UP + 1;
		int	PROPERTIES = MOVE_DOWN + 1;
		int	DESCRIPTION = PROPERTIES + 1;
		int	LENGTH = DESCRIPTION + 1;
		int	INCLUDE = LENGTH + 1;
		int	CHANGE = INCLUDE + 1;
		int	CHANGE_ELIPSIS = CHANGE + 1;
		int	EDIT = CHANGE_ELIPSIS + 1;
		int	EDIT_ELIPSIS = EDIT + 1;
		int NAME = EDIT_ELIPSIS + 1;
	}
}
