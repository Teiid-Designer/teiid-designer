/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.datatools.views;

import java.io.IOException;
import java.io.StringReader;
import java.util.Stack;
import org.apache.xerces.parsers.SAXParser;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.teiid.datatools.connectivity.ui.Activator;
import org.teiid.datatools.connectivity.ui.Messages;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.LocatorImpl;

/**
 * 
 */
public class ExecutionPlanParser {

    public static final String VALIDATION_FEATURE = "http://xml.org/sax/features/validation"; //$NON-NLS-1$

    public static final String VALUE_ELEM = "value"; //$NON-NLS-1$
    public static final String PROPERTY_ELEM = "property"; //$NON-NLS-1$
    public static final String NODE_ELEM = "node"; //$NON-NLS-1$
    public static final String NAME_ATTR = "name"; //$NON-NLS-1$

    private ExecutionPlan executionPlan;

    public ExecutionPlanParser() {

    }

    public IStatus parse( String xmlString ) {
        InputSource inputSource = new InputSource(new StringReader(xmlString));
        return internalParse(inputSource);
    }

    private IStatus internalParse( InputSource inputSource ) {
        boolean parseError = false;
        executionPlan = null;

        Handler contentHandler = new Handler();

        XMLReader reader = new SAXParser();
        reader.setErrorHandler(null);
        reader.setContentHandler(contentHandler);
        try {
            reader.setFeature(VALIDATION_FEATURE, true);
            reader.parse(inputSource);
        } catch (SAXNotRecognizedException e) {
            parseError = true;
            String message = Messages.getString("ExecutionPlanParser.parseError"); //$NON-NLS-1$
            IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, message, e);
            Activator.getDefault().getLog().log(status);
        } catch (SAXNotSupportedException e) {
            parseError = true;
            String message = Messages.getString("ExecutionPlanParser.parseError"); //$NON-NLS-1$
            IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, message, e);
            Activator.getDefault().getLog().log(status);
        } catch (IOException e) {
            parseError = true;
            String message = Messages.getString("ExecutionPlanParser.ioError"); //$NON-NLS-1$
            IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, message, e);
            Activator.getDefault().getLog().log(status);
        } catch (SAXException e) {
            parseError = true;
            String message = Messages.getString("ExecutionPlanParser.parseError"); //$NON-NLS-1$
            IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, message, e);
            Activator.getDefault().getLog().log(status);
        }

        if (parseError) {
            executionPlan = new ExecutionPlan(null);
            String message = Messages.getString("ExecutionPlanParser.parseError"); //$NON-NLS-1$
            return new Status(IStatus.ERROR, Activator.PLUGIN_ID, message);
        }
        executionPlan = new ExecutionPlan(contentHandler.getRootElement());
        String message = Messages.getString("ExecutionPlanParser.parseSuccessful"); //$NON-NLS-1$
        return new Status(IStatus.OK, Activator.PLUGIN_ID, message);
    }

    public ExecutionPlan getExecutionPlan() {
        return this.executionPlan;
    }

    /**
     * The handler used by the parser. Each instance should be only used to parse one file.
     */
    class Handler extends DefaultHandler {

        private static final String VALUE_ELEM = "value"; //$NON-NLS-1$

        private StringBuffer sbuffer = new StringBuffer();
        private Stack<String> elements = new Stack<String>();
        private PlanElement rootElement;
        private PlanElement parentElement;

        public Handler() {
            super();
            setDocumentLocator(new LocatorImpl());
        }

        @Override
        public void startElement( String uri,
                                  String lName,
                                  String qName,
                                  Attributes attributes ) throws SAXException {

            this.elements.push(qName);

            if (rootElement == null) {
                rootElement = new PlanElement(qName);
                rootElement.setRoot(true);
                if (attributes != null) {
                    int attributeLength = attributes.getLength();
                    for (int i = 0; i < attributeLength; i++) {
                        String value = attributes.getValue(i);
                        String localName = attributes.getLocalName(i);

                        rootElement.addChildAttribute(new PlanAttribute(localName, value));
                    }
                }
                parentElement = rootElement;
            } else {
                PlanElement newElement = new PlanElement(qName);
                if (attributes != null) {
                    int attributeLength = attributes.getLength();
                    for (int i = 0; i < attributeLength; i++) {
                        String value = attributes.getValue(i);
                        String localName = attributes.getLocalName(i);

                        newElement.addChildAttribute(new PlanAttribute(localName, value));
                    }
                }
                parentElement.addChildElement(newElement);
                parentElement = newElement;
            }

            sbuffer.setLength(0);
            super.startElement(uri, lName, qName, attributes);
        }

        @Override
        public void endElement( String uri,
                                String localName,
                                String qName ) throws SAXException {
            String currentElem = getCurrentElement();
            if (VALUE_ELEM.equals(currentElem)) {
                parentElement.setValue(sbuffer.toString());
            }
            parentElement = parentElement.getParent();
            this.elements.pop();

            super.endElement(uri, localName, qName);
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
         */
        @Override
        public void characters( char[] ch,
                                int start,
                                int length ) throws SAXException {
            sbuffer.append(ch, start, length);
            super.characters(ch, start, length);
        }

        /**
         * @return the element currently being parsed
         */
        private String getCurrentElement() {
            if (this.elements.empty()) {
                return null;
            }

            return this.elements.peek();
        }

        public PlanElement getRootElement() {
            return rootElement;
        }

    }

}
