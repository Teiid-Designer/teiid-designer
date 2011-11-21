/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.relational.ui.util;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.graphics.Image;
import org.teiid.designer.relational.RelationalConstants;

import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.modeler.relational.ui.UiConstants;
import com.metamatrix.modeler.relational.ui.UiPlugin;

public class RelationalUiUtil implements RelationalConstants {
	private static int OK = IStatus.OK;
	private static int WARNING = IStatus.WARNING;
	private static int ERROR = IStatus.ERROR;
	
	public static Image getRelationalImage(int objectType, int modelType, IStatus status) {
		int severity = status.getSeverity();
		switch(objectType) {
			case TYPES.AP: {
				if( severity == ERROR ) return UiPlugin.getDefault().getImage(UiConstants.Images.AP_ERROR_ICON);
				if( severity == WARNING ) return UiPlugin.getDefault().getImage(UiConstants.Images.AP_WARNING_ICON);
				return UiPlugin.getDefault().getImage(UiConstants.Images.AP_ICON);
			}
			case TYPES.COLUMN: {
				if( severity == ERROR ) return UiPlugin.getDefault().getImage(UiConstants.Images.COLUMN_ERROR_ICON);
				if( severity == WARNING ) return UiPlugin.getDefault().getImage(UiConstants.Images.COLUMN_WARNING_ICON);
				return UiPlugin.getDefault().getImage(UiConstants.Images.COLUMN_ICON);
			}
			case TYPES.FK: {
				if( severity == ERROR ) return UiPlugin.getDefault().getImage(UiConstants.Images.FK_ERROR_ICON);
				if( severity == WARNING ) return UiPlugin.getDefault().getImage(UiConstants.Images.FK_WARNING_ICON);
				return UiPlugin.getDefault().getImage(UiConstants.Images.FK_ICON);
			}
			case TYPES.INDEX: {
				if( severity == ERROR ) return UiPlugin.getDefault().getImage(UiConstants.Images.INDEX_ERROR_ICON);
				if( severity == WARNING ) return UiPlugin.getDefault().getImage(UiConstants.Images.INDEX_WARNING_ICON);
				return UiPlugin.getDefault().getImage(UiConstants.Images.INDEX_ICON);
			}
//			case TYPES.PARAMETER: {
//				if( severity == ERROR ) return UiPlugin.getDefault().getImage(UiConstants.Images.PARAMETER_ERROR_ICON);
//				if( severity == WARNING ) return UiPlugin.getDefault().getImage(UiConstants.Images.PARAMETER_WARNING_ICON);
//				return UiPlugin.getDefault().getImage(UiConstants.Images.PARAMETER_ICON);
//			}
			case TYPES.PK: {
				if( severity == ERROR ) return UiPlugin.getDefault().getImage(UiConstants.Images.PK_ERROR_ICON);
				if( severity == WARNING ) return UiPlugin.getDefault().getImage(UiConstants.Images.PK_WARNING_ICON);
				return UiPlugin.getDefault().getImage(UiConstants.Images.PK_ICON);
			}
			case TYPES.PROCEDURE: {
				if( modelType == ModelType.PHYSICAL) {
					if( severity == ERROR ) return UiPlugin.getDefault().getImage(UiConstants.Images.PROCEDURE_ERROR_ICON);
					if( severity == WARNING ) return UiPlugin.getDefault().getImage(UiConstants.Images.PROCEDURE_WARNING_ICON);
					return UiPlugin.getDefault().getImage(UiConstants.Images.PROCEDURE_ICON);
				}
				if( severity == ERROR ) return UiPlugin.getDefault().getImage(UiConstants.Images.VIRTUAL_PROCEDURE_ERROR_ICON);
				if( severity == WARNING ) return UiPlugin.getDefault().getImage(UiConstants.Images.VIRTUAL_PROCEDURE_WARNING_ICON);
				return UiPlugin.getDefault().getImage(UiConstants.Images.VIRTUAL_PROCEDURE_ICON);
			}
//			case TYPES.RESULT_SET: {
//				if( severity == ERROR ) return UiPlugin.getDefault().getImage(UiConstants.Images.RESULT_SET_ERROR_ICON);
//				if( severity == WARNING ) return UiPlugin.getDefault().getImage(UiConstants.Images.RESULT_SET_WARNING_ICON);
//				return UiPlugin.getDefault().getImage(UiConstants.Images.RESULT_SET_ICON);
//			}
			case TYPES.TABLE: {
				if( modelType == ModelType.PHYSICAL) {
					if( severity == ERROR ) return UiPlugin.getDefault().getImage(UiConstants.Images.TABLE_ERROR_ICON);
					if( severity == WARNING ) return UiPlugin.getDefault().getImage(UiConstants.Images.TABLE_WARNING_ICON);
					return UiPlugin.getDefault().getImage(UiConstants.Images.TABLE_ICON);
				}
				if( severity == ERROR ) return UiPlugin.getDefault().getImage(UiConstants.Images.VIRTUAL_TABLE_ERROR_ICON);
				if( severity == WARNING ) return UiPlugin.getDefault().getImage(UiConstants.Images.VIRTUAL_TABLE_WARNING_ICON);
				return UiPlugin.getDefault().getImage(UiConstants.Images.VIRTUAL_TABLE_ICON);
			}
			case TYPES.UC: {
				if( severity == ERROR ) return UiPlugin.getDefault().getImage(UiConstants.Images.UC_ERROR_ICON);
				if( severity == WARNING ) return UiPlugin.getDefault().getImage(UiConstants.Images.UC_WARNING_ICON);
				return UiPlugin.getDefault().getImage(UiConstants.Images.UC_ICON);
			}
//			case TYPES.VIEW: {
//				if( severity == ERROR ) return UiPlugin.getDefault().getImage(UiConstants.Images.VIEW_ERROR_ICON);
//				if( severity == WARNING ) return UiPlugin.getDefault().getImage(UiConstants.Images.VIEW_WARNING_ICON);
//				return UiPlugin.getDefault().getImage(UiConstants.Images.VIEW_ICON);
//			}
			

		}
		
		return null;
	}
}
