/*
 *
 */
package net.sourceforge.sqlexplorer.plugin;

import net.sourceforge.sqlexplorer.IConstants;
import net.sourceforge.sqlexplorer.sqleditor.ISQLColorConstants;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.graphics.RGB;

/**
 * @author John Verhaeg
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/**
	 * <p>
	 * {@inheritDoc}
	 * </p>
	 *
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
		IEclipsePreferences node = new DefaultScope().getNode("net.sourceforge.sqlexplorer"); //$NON-NLS-1$
		setDefaultColorPreference(node, ISQLColorConstants.SQL_KEYWORD, 0, 0, 255);
		setDefaultColorPreference(node, ISQLColorConstants.SQL_MULTILINE_COMMENT, 0, 100, 0);
		setDefaultColorPreference(node, ISQLColorConstants.SQL_SINGLE_LINE_COMMENT, 0, 100, 0);
		setDefaultColorPreference(node, ISQLColorConstants.SQL_STRING, 255, 0, 0);
		setDefaultColorPreference(node, ISQLColorConstants.SQL_DEFAULT, 0, 0, 0);
		setDefaultColorPreference(node, ISQLColorConstants.SQL_TABLE, 0, 100, 255);
		setDefaultColorPreference(node, ISQLColorConstants.SQL_COLUMS, 100, 0, 255);
		setDefaultColorPreference(node, ISQLColorConstants.SQL_KEYWORD, 0, 0, 255);
		node.put(IConstants.FONT, StringConverter.asString(JFaceResources.getTextFont().getFontData()));
		node.putInt(IConstants.PRE_ROW_COUNT, 80);
		node.putInt(IConstants.MAX_SQL_ROWS, 2000);
		node.putInt(IConstants.XML_CHAR_LIMIT, 100000);
		node.putBoolean(IConstants.AUTO_COMMIT, true);
		node.putBoolean(IConstants.COMMIT_ON_CLOSE, false);
		node.putBoolean(IConstants.SQL_ASSIST, true);
		node.putBoolean(IConstants.CLIP_EXPORT_COLUMNS, false);
        node.putBoolean(IConstants.SHOW_QUERY_PLAN, true);
		node.put(IConstants.CLIP_EXPORT_SEPARATOR, ";"); //$NON-NLS-1$
	}

	private void setDefaultColorPreference( IEclipsePreferences node,
	                                        String preference,
	                                        int red,
	                                        int green,
	                                        int blue ) {
		node.put(preference, StringConverter.asString(new RGB(red, green, blue)));
	}
}
