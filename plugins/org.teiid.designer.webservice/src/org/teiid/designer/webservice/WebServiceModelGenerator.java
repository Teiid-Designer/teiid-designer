/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.webservice;

import java.util.ArrayList;
import java.util.List;

import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.compare.ModelGenerator;
import org.teiid.designer.compare.ModelProducer;
import org.teiid.designer.compare.generator.BasicModelGenerator;
import org.teiid.designer.compare.generator.CompositeModelGenerator;
import org.teiid.designer.compare.selector.ModelResourceSelector;
import org.teiid.designer.compare.selector.ModelSelector;
import org.teiid.designer.core.compare.CoreMatcherFactory;
import org.teiid.designer.core.compare.EcoreMatcherFactory;
import org.teiid.designer.core.compare.UuidMatcherFactory;
import org.teiid.designer.core.compare.diagram.DiagramMatcherFactory;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.metamodels.webservice.compare.WebServiceMatcherFactory;
import org.teiid.designer.metamodels.xml.compare.XmlMatcherFactory;
import org.teiid.designer.transformation.compare.TransformationMatcherFactory;



/** 
 * This generator is the component that actually does the work of creating the Web Service
 * and XML Document models from the prescribed input information.
 * @since 8.0
 */
public class WebServiceModelGenerator extends CompositeModelGenerator {

    private final IWebServiceModelBuilder builder;
    
    /** 
     * 
     * @since 4.2
     */
    public WebServiceModelGenerator( final IWebServiceModelBuilder builder, 
                                      final ModelSelector wsSelector, final ModelSelector xmlSelector ) {
        super(new ArrayList());
        CoreArgCheck.isNotNull(builder);
        this.builder = builder;
        
        // Add the other generator(s) ...
        final WebServiceModelProducer wsProducer = new WebServiceModelProducer(builder);
        final List wsMatcherFactories = new ArrayList();
        wsMatcherFactories.add(new UuidMatcherFactory());
        wsMatcherFactories.add(new CoreMatcherFactory());
        wsMatcherFactories.add(new WebServiceMatcherFactory());
        wsMatcherFactories.add(new TransformationMatcherFactory());
        wsMatcherFactories.add(new DiagramMatcherFactory());
        wsMatcherFactories.add(new EcoreMatcherFactory());
        final ModelGenerator wsGen = new SavingModelGenerator(wsSelector,wsProducer,wsMatcherFactories);

        final ModelProducer xmlProducer = new XmlDocumentModelProducer(builder,wsProducer);
        final List xmlMatcherFactories = new ArrayList();
        xmlMatcherFactories.add(new UuidMatcherFactory());
        xmlMatcherFactories.add(new CoreMatcherFactory());
        xmlMatcherFactories.add(new XmlMatcherFactory());
        xmlMatcherFactories.add(new TransformationMatcherFactory());
        xmlMatcherFactories.add(new DiagramMatcherFactory());
        xmlMatcherFactories.add(new EcoreMatcherFactory());
        final ModelGenerator xmlGen = new SavingModelGenerator(xmlSelector,xmlProducer,xmlMatcherFactories);
        
        super.getModelGenerators().add(wsGen);
        super.getModelGenerators().add(xmlGen);
    }
    
    public IWebServiceModelBuilder getWebServiceModelBuilder() {
        return this.builder;
    }
    
    protected class SavingModelGenerator extends BasicModelGenerator {
        public SavingModelGenerator(ModelSelector original, ModelProducer outputProducer, List mappingAdapters) {
            super(original, outputProducer, mappingAdapters);
        }
        
        @Override
        protected void doPostMerge() {
            super.doPostMerge();
            // Defect 23340
            // DO NOT SAVE resource here if !isSaveAllBeforeFinish() because the model imports may not have been re-organized.
            // Saving too early was resulting in a model with an incomplete set of imports, even though the "Workspace" model
            // indicated they were all there.
            if( !isSaveAllBeforeFinish() ) {
                // Save the XML model ...
                final ModelSelector selector = this.getOriginalModelSelector();
                if ( selector instanceof ModelResourceSelector ) {
                    try {
                        ((ModelResourceSelector)selector).getModelResource().save(null,true);
                    } catch (ModelWorkspaceException err) {
                        WebServicePlugin.Util.log(err);
                    }
                }
            }
        }
        
    }

}
