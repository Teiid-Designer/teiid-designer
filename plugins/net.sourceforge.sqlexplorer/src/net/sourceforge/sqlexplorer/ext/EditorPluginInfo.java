package net.sourceforge.sqlexplorer.ext;

/**
 * @author Mazzolini
 *
 */
public class EditorPluginInfo extends PluginInfo{
	public EditorPluginInfo(PluginInfo pi) throws IllegalArgumentException {
		super(getPassedPluginClassName(pi));
		assignFrom(pi);
	}

	public IEditorPlugin getEditorPlugin() {
		return (IEditorPlugin)getPlugin();
	}

	@Override
    void setPlugin(IPlugin value) throws IllegalArgumentException {
		if (value == null) {
			throw new IllegalArgumentException("Null IPlugin passed"); //$NON-NLS-1$
		}
		if (!(value instanceof IEditorPlugin)) {
			throw new IllegalArgumentException("Plugin not an IEditorPlugin"); //$NON-NLS-1$
		}
		super.setPlugin(value);
	}


	private static String getPassedPluginClassName(PluginInfo pi)
			throws IllegalArgumentException {
		if (pi == null) {
			throw new IllegalArgumentException("Null PluginInfo passed"); //$NON-NLS-1$
		}
		return pi.getPluginClassName();
	}
}
