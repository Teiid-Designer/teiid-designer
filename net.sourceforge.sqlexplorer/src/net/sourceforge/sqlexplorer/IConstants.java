package net.sourceforge.sqlexplorer;

import net.sourceforge.sqlexplorer.plugin.editors.SQLEditor;

/*
 * Copyright (C) 2002-2004 Andrea Mazzolini
 * andreamazzolini@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

public interface IConstants {
	String CLIP_EXPORT_COLUMNS ="clip_export_columns"; //$NON-NLS-1$
	String CLIP_EXPORT_SEPARATOR ="clip_export_separator"; //$NON-NLS-1$
	String AUTO_COMMIT="autocommit"; //$NON-NLS-1$
	String COMMIT_ON_CLOSE="commitonclose"; //$NON-NLS-1$
	String MAX_SQL_ROWS="maxSQLRows"; //$NON-NLS-1$
	String PRE_ROW_COUNT="preRowCount"; //$NON-NLS-1$
    String XML_CHAR_LIMIT="xmlCharacterLimit"; //$NON-NLS-1$
	String FONT="font"; //$NON-NLS-1$
	String SQL_ASSIST="assist"; //$NON-NLS-1$
    String SHOW_QUERY_PLAN = "showQueryPlan"; //$NON-NLS-1$
	String SQL_EDITOR_CLASS = SQLEditor.class.getName();
    
    interface Extensions {
        /**
         * {@link org.eclipse.ui.IViewPart} identifiers. 
         */
        interface Views {
            String SQL_DB_VIEW = "net.sourceforge.sqlexplorer.plugin.views.DBView"; //$NON-NLS-1$
            String SQL_HISTORY_VIEW = "net.sourceforge.sqlexplorer.plugin.views.SQLHistoryView"; //$NON-NLS-1$
            String SQL_RESULTS_VIEW = "net.sourceforge.sqlexplorer.plugin.views.SqlResultsView"; //$NON-NLS-1$
        }
    }

    /**
     * Extension points defined by the SQLExplorerPlugin. 
     */
    interface ExtensionPoints {
        /**
         * Constants for the ResultSet Processor extension point.
         */
        interface ResultSetProcessor {
            String ID = "resultSetProcessor"; //$NON-NLS-1$
            String CLASS_ELEMENT = "class";  //$NON-NLS-1$
            String CLASS_NAME = "name"; //$NON-NLS-1$
        }

        /**
         * Constants for the Request Document Generator extension point.
         */
        interface RequestDocGenerator {
            String ID = "requestDocGenerator"; //$NON-NLS-1$
            String CLASS_ELEMENT = "class";  //$NON-NLS-1$
            String CLASS_NAME = "name"; //$NON-NLS-1$
        }
            
        /**
         * Constants for the SqlExecVetoListener extension point.
         */
        interface SqlExecVetoListener {
            String ID = "sqlExecVetoListener"; //$NON-NLS-1$
            String CLASS_ELEMENT = "class";  //$NON-NLS-1$
            String CLASS_NAME = "name"; //$NON-NLS-1$
        }
    }
    
}
