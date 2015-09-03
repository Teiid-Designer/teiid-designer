/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.ui.common;

import org.teiid.core.designer.util.StringConstants;

/**
 *
 */
public class UILabelUtil implements UiLabelConstants {

	/**
	 * @param buttonId the integer button or label ID
	 * @return
	 */
	public static String getLabel(int id) {
		switch(id) {
			case LABEL_IDS.ADD: {
				return Messages.addLabel;
			}
			case LABEL_IDS.CHANGE: {
				return Messages.changeLabel;
			}
			case LABEL_IDS.CHANGE_ELIPSIS: {
				return Messages.changeElipsisLabel;
			}
			case LABEL_IDS.DELETE: {
				return Messages.deleteLabel;
			}
			case LABEL_IDS.DESCRIPTION: {
				return Messages.descriptionLabel;
			}
			case LABEL_IDS.EDIT: {
				return Messages.editLabel;
			}
			case LABEL_IDS.EDIT_ELIPSIS: {
				return Messages.editElipsisLabel;
			}
			case LABEL_IDS.ELIPSIS: {
				return Messages.elipsisLabel;
			}
			case LABEL_IDS.INCLUDE: {
				return Messages.includeLabel;
			}
			case LABEL_IDS.LENGTH: {
				return Messages.lengthLabel;
			}
			case LABEL_IDS.MOVE_DOWN: {
				return Messages.moveDownLabel;
			}
			case LABEL_IDS.MOVE_UP: {
				return Messages.moveUpLabel;
			}
			case LABEL_IDS.NAME: {
				return Messages.nameLabel;
			}
			case LABEL_IDS.PROPERTIES: {
				return Messages.propertiesLabel;
			}
			
		}
		
		return StringConstants.EMPTY_STRING;
	}
}
