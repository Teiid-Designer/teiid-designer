package org.teiid.designer.ui.bot.ext.teiid.perspective;

import java.util.Collections;
import java.util.List;

import org.jboss.tools.ui.bot.ext.SWTBotFactory;
import org.jboss.tools.ui.bot.ext.gen.IPerspective;

/**
 * 
 * Perspective tasks. 
 * 
 * @author Lucia Jelinkova
 *
 */
public class Perspective {

	private IPerspective perspective;
	
	public Perspective(String name) {
		super();
		this.perspective = createPerspective(name);
	}
	
	public Perspective(IPerspective perspective) {
		super();
		this.perspective = perspective;
	}

	public void open(){
		SWTBotFactory.getOpen().perspective(perspective);
	}
	
	private IPerspective createPerspective(final String name){
		return new IPerspective() {
			
			@Override
			public String getName() {
				return name;
			}
			
			@Override
			public List<String> getGroupPath() {
				return Collections.emptyList();
			}
		};
	}
}

