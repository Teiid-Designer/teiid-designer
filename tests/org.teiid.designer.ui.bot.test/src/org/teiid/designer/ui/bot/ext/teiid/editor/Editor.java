package org.teiid.designer.ui.bot.ext.teiid.editor;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.jboss.tools.ui.bot.ext.SWTBotExt;
import org.jboss.tools.ui.bot.ext.SWTBotFactory;

/**
 * Represents an editor and provides basic functionality. 
 * 
 * @author Lucia Jelinkova
 *
 */
public class Editor {

	private String name;
	
	protected Editor(String name){
		this.name = name;
	}
	
	public static Editor getInstance(String name){
		return new Editor(name);
	}
	
	public void show(){
		getEditor().show();
		getEditor().setFocus();
	}
	
	public void save(){
		getEditor().save();
	}
	
	public void close(){
		getEditor().close();
	}
	
	public boolean isActive(){
		return getEditor().isActive();
	}
	
	protected SWTBotEditor getEditor(){
		return getBot().editorByTitle(name);
	}
	
	protected SWTBotExt getBot(){
		return SWTBotFactory.getBot();
	}
}
