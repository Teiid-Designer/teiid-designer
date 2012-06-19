package org.teiid.designer.ui.bot.ext.teiid;


import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.Result;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.widgets.AbstractSWTBotControl;

import com.metamatrix.modeler.diagram.ui.notation.uml.figure.UmlClassifierFigure;
import com.metamatrix.modeler.diagram.ui.notation.uml.figure.UmlClassifierHeader;
import com.metamatrix.modeler.transformation.ui.figure.TransformationFigure;



/**
 * 
 * @author psrna
 *
 */
public class SWTBotTeiidCanvas extends AbstractSWTBotControl<FigureCanvas> {
	
	Logger log = Logger.getLogger(FigureCanvas.class);
	
	
	public SWTBotTeiidCanvas(FigureCanvas canvas) {
		super(canvas);
	}

	public SWTBotTeiidUmlFigure figure(final String name){
		
		UmlClassifierFigure figure = UIThreadRunnable
		.syncExec(new Result<UmlClassifierFigure>() {

			@SuppressWarnings("unchecked")
			private UmlClassifierFigure find(List<IFigure> figures){
				UmlClassifierFigure result;
				
				for(IFigure f : figures){
					
					if(f instanceof UmlClassifierFigure){
						if(((UmlClassifierFigure) f).getLabelFigure()
								                    .getText().equals(name)){
							return (UmlClassifierFigure) f;
						}
					}
					result = find(f.getChildren());
					
					if(result != null)
						return result;
				}
				return null;
			}
			
			@SuppressWarnings("unchecked")
			@Override
			public UmlClassifierFigure run() {
				return find(widget.getContents().getChildren());
			}
			
		});
		if(figure != null)
			return new SWTBotTeiidUmlFigure(figure, widget);
		else
			throw new WidgetNotFoundException("Cannot find figure with the name:" + name);
	}
	
	public SWTBotTeiidTFigure tFigure(){
		
		TransformationFigure figure = UIThreadRunnable
				.syncExec(new Result<TransformationFigure>() {

					
					@SuppressWarnings("unchecked")
					private TransformationFigure find(List<IFigure> figures){
						TransformationFigure result;
						
						for(IFigure f : figures){
							
							if(f instanceof TransformationFigure)
								return (TransformationFigure) f;
							
							result = find(f.getChildren());
							
							if(result != null)
								return result;
						}
						return null;
					}
					
					@SuppressWarnings("unchecked")
					@Override
					public TransformationFigure run() {
						return find(widget.getContents().getChildren());
					}
					
				});
		if(figure != null)
			return new SWTBotTeiidTFigure(figure, widget);
		else
			throw new WidgetNotFoundException("Cannot find Transformation Figure");
	}
	
	
	
	/**
	 * Debug method
	 */
	public void debugCanvas(){
		UIThreadRunnable.syncExec(new VoidResult() {

			
			@SuppressWarnings("unchecked")
			public void printAllChildren(List<IFigure> figures, final int level){
				
				for(int i=0; i<level; i++){
					System.out.print("   ");
				}
				for(IFigure f : figures){
					log.info("Child:" + f.getClass() + " {{SUPERCLASS:" + f.getClass().getSuperclass() + "}}");
					printAllChildren(f.getChildren(), level+1);
				}
			}

			@SuppressWarnings("unchecked")
			public void printUmlClassifierFigures(List<IFigure> figures){
				for(IFigure f : figures){
					if(f instanceof UmlClassifierFigure)
						log.info("UMLFIGURE:" + ((UmlClassifierFigure) f).getLabelFigure().getText());
					printUmlClassifierFigures(f.getChildren());
				}
			}
			
			@SuppressWarnings("unchecked")
			public void printUmlClassifierHeaderFigures(List<IFigure> figures){
				for(IFigure f : figures){
					if(f instanceof UmlClassifierHeader)
						log.info("UMLHEADER:" + ((UmlClassifierHeader) f).getNameLabel().getText());
					printUmlClassifierFigures(f.getChildren());
				}
			}
			
			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				log.info("*****************************************************************************");
				FigureCanvas c = widget;
				//printAllChildren(c.getContents().getChildren(), 0);
				log.info("UmlClassifierFigure:");
				printUmlClassifierFigures(c.getContents().getChildren());
				
				log.info("-----------------------------------------------------------------------------");
				
				log.info("UmlClassifierHeaders:");
				printUmlClassifierHeaderFigures(c.getContents().getChildren());
				
				log.info("*****************************************************************************");
				
			}
		});
		
		
	}

}
