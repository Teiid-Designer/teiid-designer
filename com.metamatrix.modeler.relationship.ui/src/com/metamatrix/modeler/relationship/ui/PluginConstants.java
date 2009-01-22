/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui;

import org.eclipse.swt.graphics.RGB;

import com.metamatrix.ui.PreferenceKeyAndDefaultValue;
/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface PluginConstants {
	public static final String RELATIONSHIP_DIAGRAM_TYPE_ID  = "relationshipDiagramType";     //$NON-NLS-1$
	public static final String CUSTOM_RELATIONSHIP_DIAGRAM_TYPE_ID  = "customRelationshipDiagramType";     //$NON-NLS-1$
	
	public interface Images {
		String WARNING_ICON = "icons/full/ovr16/warning_co.gif"; //$NON-NLS-1$
		String ERROR_ICON = "icons/full/ovr16/error_co.gif"; //$NON-NLS-1$
		String RELATIONSHIP_DIAGRAM_ICON = "icons/full/obj16/RelationshipDiagram.gif"; //$NON-NLS-1$
		String CUSTOM_RELATIONSHIP_DIAGRAM_ICON = "icons/full/cview16/CustomRelationshipDiagram.gif"; //$NON-NLS-1$
        String LEFT_ARROW_ICON = "icons/full/cview16/left.gif"; //$NON-NLS-1$
        String RIGHT_ARROW_ICON = "icons/full/cview16/right.gif"; //$NON-NLS-1$
        String GENERATE_SQL_REL_ICON = "icons/full/obj16/RelationshipDiagram.gif"; //$NON-NLS-1$
	}
    
	public interface Prefs {
                
		// Appearance Preferences
		interface Appearance {
			class PC {
				private static final String PREFIX = "modeler.preference.diagram."; //$NON-NLS-1$
			}

			public static final String RELATIONSHIP_BKGD_COLOR = PC.PREFIX + "relationship.backgroundcolor"; //$NON-NLS-1$
			public static final String CUSTOM_RELATIONSHIP_BKGD_COLOR = PC.PREFIX + "customRelationship.backgroundcolor"; //$NON-NLS-1$
    
			public static final PreferenceKeyAndDefaultValue[] PREFERENCES = 
					new PreferenceKeyAndDefaultValue[] {
						new PreferenceKeyAndDefaultValue(RELATIONSHIP_BKGD_COLOR,
								new RGB(0, 200, 200)),
						new PreferenceKeyAndDefaultValue(CUSTOM_RELATIONSHIP_BKGD_COLOR,
								new RGB(0, 200, 240)),
					};
		}
	}
    
    /**
     * Constants used by the DragAdapter and DropEditParts for Relationship DND work. 
     * @since 4.2
     */
    interface Drop {
        int NOTHING = 0;
        int SOURCE_ROLE = 1;
        int TARGET_ROLE = 2;
    }
}
