/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

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
