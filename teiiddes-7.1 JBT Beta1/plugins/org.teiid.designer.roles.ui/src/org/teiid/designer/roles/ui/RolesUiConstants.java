/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.roles.ui;

public interface RolesUiConstants {
	String PACKAGE_ID = RolesUiConstants.class.getPackage().getName();
	

    class PC {
        public static final String I18N_NAME = PACKAGE_ID + ".i18n"; //$NON-NLS-1$

        public static final String ICON_PATH = "icons/full/"; //$NON-NLS-1$

        public static final String CVIEW16 = ICON_PATH + "cview16/"; //$NON-NLS-1$

        public static final String CTOOL16 = ICON_PATH + "ctool16/"; //$NON-NLS-1$

        public static final String OBJ16 = ICON_PATH + "obj16/"; //$NON-NLS-1$

        public static final String WIZBAN = ICON_PATH + "wizban/"; //$NON-NLS-1$
    }

    interface Images {
    	public static final String CHECKED_BOX_ICON = PC.CVIEW16 + "checked_box.png"; //$NON-NLS-1$
        public static final String UNCHECKED_BOX_ICON = PC.CVIEW16 + "unchecked_box.png"; //$NON-NLS-1$
        public static final String GRAY_CHECKED_BOX_ICON = PC.CVIEW16 + "gray_checked_box.png"; //$NON-NLS-1$
        public static final String GRAY_UNCHECKED_BOX_ICON = PC.CVIEW16 + "gray_unchecked_box.png"; //$NON-NLS-1$
    }
}
