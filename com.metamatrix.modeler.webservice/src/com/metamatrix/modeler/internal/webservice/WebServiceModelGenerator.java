/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.webservice;

import java.util.ArrayList;
import java.util.List;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.webservice.compare.WebServiceMatcherFactory;
import com.metamatrix.metamodels.xml.compare.XmlMatcherFactory;
import com.metamatrix.modeler.compare.ModelGenerator;
import com.metamatrix.modeler.compare.ModelProducer;
import com.metamatrix.modeler.compare.generator.BasicModelGenerator;
import com.metamatrix.modeler.compare.generator.CompositeModelGenerator;
import com.metamatrix.modeler.compare.selector.ModelResourceSelector;
import com.metamatrix.modeler.compare.selector.ModelSelector;
import com.metamatrix.modeler.core.compare.CoreMatcherFactory;
import com.metamatrix.modeler.core.compare.EcoreMatcherFactory;
import com.metamatrix.modeler.core.compare.UuidMatcherFactory;
import com.metamatrix.modeler.core.compare.diagram.DiagramMatcherFactory;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.transformation.compare.TransformationMatcherFactory;
import com.metamatrix.modeler.webservice.IWebServiceModelBuilder;
import com.metamatrix.modeler.webservice.WebServicePlugin;


/** 
 * This generator is the component that actually does the work of creating the Web Service
 * and XML Document models from the prescribed input information.
 * @since 4.2
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
        ArgCheck.isNotNull(builder);
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
