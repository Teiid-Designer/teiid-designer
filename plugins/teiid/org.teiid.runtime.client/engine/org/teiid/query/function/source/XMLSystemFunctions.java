/*
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.teiid.query.function.source;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.EventFilter;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.Location;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.EventReaderDelegate;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPathExpressionException;
import net.sf.saxon.expr.JPConverter;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.Name11Checker;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.om.StandardNames;
import net.sf.saxon.sxpath.XPathEvaluator;
import net.sf.saxon.sxpath.XPathExpression;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.AtomicValue;
import net.sf.saxon.value.DateTimeValue;
import net.sf.saxon.value.DateValue;
import net.sf.saxon.value.DayTimeDurationValue;
import net.sf.saxon.value.TimeValue;
import org.teiid.common.buffer.FileStore;
import org.teiid.common.buffer.FileStoreInputStreamFactory;
import org.teiid.common.buffer.impl.MemoryStorageManager;
import org.teiid.core.types.BinaryType;
import org.teiid.core.types.BlobImpl;
import org.teiid.core.types.BlobType;
import org.teiid.core.types.ClobImpl;
import org.teiid.core.types.ClobType;
import org.teiid.core.types.DataTypeManagerService;
import org.teiid.core.types.InputStreamFactory;
import org.teiid.core.types.SQLXMLImpl;
import org.teiid.core.types.StandardXMLTranslator;
import org.teiid.core.types.Streamable;
import org.teiid.core.types.XMLTranslator;
import org.teiid.core.types.XMLType;
import org.teiid.core.types.XMLType.Type;
import org.teiid.core.util.ObjectConverterUtil;
import org.teiid.core.util.ReaderInputStream;
import org.teiid.json.simple.ContentHandler;
import org.teiid.json.simple.JSONParser;
import org.teiid.json.simple.ParseException;
import org.teiid.query.eval.Evaluator;
import org.teiid.query.eval.Evaluator.NameValuePair;
import org.teiid.query.function.CharsetUtils;
import org.teiid.query.sql.symbol.XMLSerialize;
import org.teiid.query.util.CommandContext;
import org.teiid.runtime.client.Messages;
import org.teiid.runtime.client.TeiidClientException;
import org.teiid.util.StAXSQLXML;
import org.teiid.util.StAXSQLXML.StAXSourceProvider;


/** 
 * This class contains scalar system functions supporting for XML manipulation.
 * 
 * @since 4.2
 */
public class XMLSystemFunctions {
	
	private static final Charset UTF_32BE = Charset.forName("UTF-32BE"); //$NON-NLS-1$
	private static final Charset UTF_16BE = Charset.forName("UTF-16BE"); //$NON-NLS-1$
	private static final Charset UTF_32LE = Charset.forName("UTF-32LE"); //$NON-NLS-1$
	private static final Charset UTF_16LE = Charset.forName("UTF-16LE"); //$NON-NLS-1$
	private static final Charset UTF_8 = Charset.forName("UTF-8"); //$NON-NLS-1$

	private static final Location dummyLocation = new Location() {
		@Override
		public String getSystemId() {
			return null;
		}

		@Override
		public String getPublicId() {
			return null;
		}

		@Override
		public int getLineNumber() {
			return -1;
		}

		@Override
		public int getColumnNumber() {
			return -1;
		}

		@Override
		public int getCharacterOffset() {
			return -1;
		}
	};

	private static final EventFilter declarationOmittingFilter = new EventFilter() {
		@Override
		public boolean accept(XMLEvent event) {
			return !event.isStartDocument() && !event.isEndDocument();
		}
	};

	private static final class DeclarationStaxSourceProvider implements
			StAXSourceProvider {
		private final XMLEvent start;
		private XMLType value;

		private DeclarationStaxSourceProvider(XMLEvent start, XMLType value) {
			this.start = start;
			this.value = value;
		}

		@Override
		public StAXSource getStaxSource() throws SQLException {
			StAXSource source = value.getSource(StAXSource.class);
			try {
				XMLEventReader reader = getXMLEventReader(source);
				reader = new EventReaderDelegate(reader) {
					@Override
					public XMLEvent nextEvent() throws XMLStreamException {
						return replaceStart(super.nextEvent());
					}
					
					@Override
					public XMLEvent peek() throws XMLStreamException {
						return replaceStart(super.peek());
					}
	
					private XMLEvent replaceStart(XMLEvent event) {
						if (event != null && event.getEventType() == XMLEvent.START_DOCUMENT) {
							return start;
						}
						return event;
					}
					
					@Override
					public Object next() {
						try {
							return nextEvent();
						} catch (XMLStreamException e) {
							throw new NoSuchElementException();
						}
					}
				};
				return new StAXSource(reader);
			} catch (XMLStreamException e) {
				throw new SQLException(e);
			}
		}

	}

	private static final class JsonToXmlContentHandler implements
			ContentHandler, XMLEventReader {
		private Reader reader;
		private JSONParser parser;
		private XMLEventFactory eventFactory;

		private LinkedList<String> nameStack = new LinkedList<String>();
		private LinkedList<XMLEvent> eventStack = new LinkedList<XMLEvent>();
		
		private boolean rootArray;
		private boolean end;
		private boolean declaredNs;

		private JsonToXmlContentHandler(String rootName,
				Reader reader, JSONParser parser, XMLEventFactory eventFactory) {
			this.nameStack.push(escapeName(rootName, true));
			this.reader = reader;
			this.eventFactory = eventFactory;
			this.parser = parser;
		}

		@Override
		public boolean startObjectEntry(String key)
				throws ParseException, IOException {
			this.nameStack.push(escapeName(key, true));
			return false;
		}

		@Override
		public boolean startObject() throws org.teiid.json.simple.ParseException,
				IOException {
			start();
			return false;
		}

		private void start() {
			eventStack.add(eventFactory.createStartElement("", "", nameStack.peek())); //$NON-NLS-1$ //$NON-NLS-2$ 
			if (!declaredNs) {
				eventStack.add(eventFactory.createNamespace("xsi", XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI)); //$NON-NLS-1$
				declaredNs = true;
			}
		}

		@Override
		public void startJSON() throws org.teiid.json.simple.ParseException,
				IOException {
			//specify the defaults, since different providers emit/omit differently
			eventStack.add(eventFactory.createStartDocument("UTF-8", "1.0")); //$NON-NLS-1$ //$NON-NLS-2$
		}

		@Override
		public boolean startArray() throws org.teiid.json.simple.ParseException,
				IOException {
			if (this.nameStack.size() == 1) {
				this.rootArray = true;
				start();
			}
			return false;
		}

		@Override
		public boolean primitive(Object value)
				throws ParseException, IOException {
			start();
			if (value != null) {
				String type = "decimal"; //$NON-NLS-1$
				if (value instanceof String) {
					type = null;
				} else if (value instanceof Boolean) {
					type = "boolean"; //$NON-NLS-1$
				}
				if (type != null) {
					//we need to differentiate boolean/decimal entries from their string counter parts
					eventStack.add(eventFactory.createAttribute("xsi", XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "type", type)); //$NON-NLS-1$ //$NON-NLS-2$
				}
				eventStack.add(eventFactory.createCharacters(value.toString()));
			} else {
				eventStack.add(eventFactory.createAttribute("xsi", XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "nil", "true")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			end();
			return true; //return true, otherwise we don't get the endObjectEntry
		}

		private void end() {
			eventStack.add(eventFactory.createEndElement("", "", nameStack.peek())); //$NON-NLS-1$ //$NON-NLS-2$ 
		}

		@Override
		public boolean endObjectEntry()
				throws ParseException, IOException {
			this.nameStack.pop();
			return false;
		}

		@Override
		public boolean endObject() throws org.teiid.json.simple.ParseException,
				IOException {
			end();
			return false;
		}

		@Override
		public void endJSON() throws org.teiid.json.simple.ParseException,
				IOException {
			this.eventStack.add(eventFactory.createEndDocument());
			end = true;
		}

		@Override
		public boolean endArray() throws org.teiid.json.simple.ParseException,
				IOException {
			if (this.nameStack.size() == 1 && rootArray) {
				end();
			}
			return false;
		}

		@Override
		public void close() throws XMLStreamException {
			try {
				//this is explicitly against the javadoc, but
				//it's our only chance to close the reader
				this.reader.close();
			} catch (IOException e) {
			}
		}

		@Override
		public String getElementText() throws XMLStreamException {
			throw new UnsupportedOperationException();
		}

		@Override
		public Object getProperty(String name) throws IllegalArgumentException {
			return null;
		}

		@Override
		public boolean hasNext() {
			return !eventStack.isEmpty() || !end;
		}

		@Override
		public XMLEvent nextEvent() throws XMLStreamException {
			while (eventStack.isEmpty() && !end) {
				try {
					parser.parse(reader, this, true);
				} catch (IOException e) {
					throw new XMLStreamException(e);
				} catch (ParseException e) {
					throw new XMLStreamException(e);
				}
			}
			return eventStack.remove();
		}

		@Override
		public XMLEvent nextTag() throws XMLStreamException {
			throw new UnsupportedOperationException();
		}

		@Override
		public XMLEvent peek() throws XMLStreamException {
			if (hasNext()) {
				XMLEvent next = next();
				this.eventStack.push(next);
				return next;
			}
			return null;
		}

		@Override
		public XMLEvent next() {
			try {
				return nextEvent();
			} catch (XMLStreamException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();			
		}
	}
	
	static ThreadLocal<XMLOutputFactory> threadLocalOutputFactory = new ThreadLocal<XMLOutputFactory>() {
		protected XMLOutputFactory initialValue() {
			return newXmlOutputFactory();
		}
	};
	static ThreadLocal<XMLEventFactory> threadLocalEventtFactory = new ThreadLocal<XMLEventFactory>() {
		protected XMLEventFactory initialValue() {
			return XMLEventFactory.newFactory();
		}
		public XMLEventFactory get() {
			XMLEventFactory eventFactory = super.get();
			eventFactory.setLocation(null);
			return eventFactory;
		}
	};
	private static final String P_OUTPUT_VALIDATE_STRUCTURE = "com.ctc.wstx.outputValidateStructure"; //$NON-NLS-1$
	static XMLOutputFactory newXmlOutputFactory() throws FactoryConfigurationError {
		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		if (factory.isPropertySupported(P_OUTPUT_VALIDATE_STRUCTURE)) {
			factory.setProperty(P_OUTPUT_VALIDATE_STRUCTURE, false);
		}
		return factory;
	}
	static XMLOutputFactory xmlOutputFactory = newXmlOutputFactory();
	
	private static XMLEventReader getXMLEventReader(StAXSource source) throws XMLStreamException {
		XMLEventReader reader = source.getXMLEventReader();
		if (reader == null) {
			XMLInputFactory inputFactory = XMLType.getXmlInputFactory();
			reader = inputFactory.createXMLEventReader(source.getXMLStreamReader());
		}
		return reader;
	}
	
	public static XMLOutputFactory getOutputFactory() throws FactoryConfigurationError {
		if (XMLType.isThreadSafeXmlFactories()) {
			return xmlOutputFactory;
		}
		return threadLocalOutputFactory.get();
	}
	
	public static ClobType xslTransform(CommandContext context, Object xml, Object styleSheet) throws Exception {
    	Source styleSource = null; 
		Source xmlSource = null;
		try {
			styleSource = convertToSource(styleSheet);
			xmlSource = convertToSource(xml);
			final Source xmlParam = xmlSource;
			TransformerFactory factory = StandardXMLTranslator.getThreadLocalTransformerFactory();
            final Transformer transformer = factory.newTransformer(styleSource);
            
			//this creates a non-validated sqlxml - it may not be valid xml/root-less xml
			SQLXMLImpl result = saveToBufferManager(new XMLTranslator() {
				
				@Override
				public void translate(Writer writer) throws TransformerException {
	                //transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes"); //$NON-NLS-1$
	                // Feed the resultant I/O stream into the XSLT processor
					transformer.transform(xmlParam, new StreamResult(writer));
				}
			});
			return new ClobType(new ClobImpl(result.getStreamFactory(), -1));
		} finally {
			closeSource(styleSource);
			closeSource(xmlSource);
		}
    }

	public static XMLType xmlForest(final NameValuePair[] namespaces, final NameValuePair[] values) throws Exception {
		boolean valueExists = false;
		for (NameValuePair nameValuePair : values) {
			if (nameValuePair.value != null) {
				valueExists = true;
				break;
			}
		}
		if (!valueExists) {
			return null;
		}

		XMLType result = new XMLType(XMLSystemFunctions.saveToBufferManager(new XMLTranslator() {
			
			@Override
			public void translate(Writer writer) throws TransformerException,
					IOException {
				try {
					XMLOutputFactory factory = getOutputFactory();
					XMLEventWriter eventWriter = factory.createXMLEventWriter(writer);
					XMLEventFactory eventFactory = threadLocalEventtFactory.get();
					for (NameValuePair nameValuePair : values) {
						if (nameValuePair.value == null) {
							continue;
						}
						addElement(nameValuePair.name, writer, eventWriter, eventFactory, namespaces, null, Collections.singletonList(nameValuePair.value));
					}
					eventWriter.close();
				} catch (XMLStreamException e) {
					throw new TransformerException(e);
				} 
			}
		}));
		result.setType(Type.CONTENT);
		return result;
	}
	
	/**
	 * Basic support for xmlelement.  namespaces are not yet supported.
	 * @param context
	 * @param name
	 * @param contents
	 * @return
	 * @throws Exception
	 */
	public static XMLType xmlElement(final String name, 
			final NameValuePair<String>[] namespaces, final NameValuePair<?>[] attributes, final List<?> contents) throws Exception {
		    XMLType result = new XMLType(saveToBufferManager(new XMLTranslator() {
			
			@Override
			public void translate(Writer writer) throws TransformerException,
					IOException {
				try {
					XMLOutputFactory factory = getOutputFactory();
					XMLEventWriter eventWriter = factory.createXMLEventWriter(writer);
					XMLEventFactory eventFactory = threadLocalEventtFactory.get();
					addElement(name, writer, eventWriter, eventFactory, namespaces, attributes, contents);
					eventWriter.close();
				} catch (XMLStreamException e) {
					throw new TransformerException(e);
				} 
			}

		}));
		result.setType(Type.ELEMENT);
		return result;
	}
	
	private static void addElement(final String name, Writer writer, XMLEventWriter eventWriter, XMLEventFactory eventFactory,
			NameValuePair<String> namespaces[], NameValuePair<?> attributes[], List<?> contents) throws XMLStreamException, IOException, TransformerException {
		eventWriter.add(eventFactory.createStartElement("", null, name)); //$NON-NLS-1$
		if (namespaces != null) {
			for (NameValuePair<String> nameValuePair : namespaces) {
				if (nameValuePair.name == null) {
					if (nameValuePair.value == null) {
						eventWriter.add(eventFactory.createNamespace(XMLConstants.NULL_NS_URI));
					} else {
						eventWriter.add(eventFactory.createNamespace(nameValuePair.value));
					} 
				} else {
					eventWriter.add(eventFactory.createNamespace(nameValuePair.name, nameValuePair.value));
				}
			}
		}
		if (attributes != null) {
			for (NameValuePair<?> nameValuePair : attributes) {
				if (nameValuePair.value != null) {
					eventWriter.add(eventFactory.createAttribute(new QName(nameValuePair.name), convertToAtomicValue(nameValuePair.value).getStringValue()));
				}
			}
		}
		//add empty chars to close the start tag
		eventWriter.add(eventFactory.createCharacters("")); //$NON-NLS-1$ 
		for (Object object : contents) {
			convertValue(writer, eventWriter, eventFactory, object);
		}
		eventWriter.add(eventFactory.createEndElement("", null, name)); //$NON-NLS-1$
	}
	
	public static XMLType xmlConcat(CommandContext context, final XMLType xml, final Object... other) throws Exception {
		//determine if there is just a single xml value and return it
		XMLType singleValue = xml;
		XMLType.Type type = null;
		for (Object object : other) {
			if (object != null) {
				if (singleValue != null) {
					type = Type.CONTENT;
					break;
				}
				if (object instanceof XMLType) {
					singleValue = (XMLType)object;
				} else {
					type = Type.CONTENT;
					break;
				}
			}
		}
		if (type == null) {
			return singleValue;
		}
		
		XmlConcat concat = new XmlConcat();
		concat.addValue(xml);
		for (Object object : other) {
			concat.addValue(object);
		}
		return concat.close();
	}
	public static class XmlConcat {
		private XMLOutputFactory factory;
		private XMLEventWriter eventWriter;
		private Writer writer;
		private FileStoreInputStreamFactory fsisf;
		private FileStore fs;
		private Type type;
		
		public XmlConcat() throws TeiidClientException {
		    MemoryStorageManager manager = new MemoryStorageManager();
			fs = manager.createFileStore("xml"); //$NON-NLS-1$
			fsisf = new FileStoreInputStreamFactory(fs, Streamable.ENCODING);
		    writer = fsisf.getWriter();
			factory = getOutputFactory();
			try {
				eventWriter = factory.createXMLEventWriter(writer);
			} catch (XMLStreamException e) {
				fs.remove();
				 throw new TeiidClientException(e);
			}
		}
		
		public void addValue(Object object) throws TeiidClientException {
			if (type == null) {
				if (object instanceof XMLType) {
					type = ((XMLType)object).getType();
				}
			} else {
				type = Type.CONTENT;
			}
			try {
				convertValue(writer, eventWriter, threadLocalEventtFactory.get(), object);
			} catch (Exception e) {
				fs.remove();
				 throw new TeiidClientException(e);
			}			
		}
		
		public Writer getWriter() {
			return writer;
		}
		
		public XMLType close() throws TeiidClientException {
			try {
				eventWriter.flush();
				writer.close();
			} catch (XMLStreamException e) {
				fs.remove();
				 throw new TeiidClientException(e);
			} catch (IOException e) {
				fs.remove();
				 throw new TeiidClientException(e);
			}
	        XMLType result = new XMLType(new SQLXMLImpl(fsisf));
	        if (type == null) {
	        	result.setType(Type.CONTENT);
	        } else {
	        	result.setType(type);
	        }
	        return result;
		}
		
	}
	
	public static XMLType xmlPi(String name) {
		return xmlPi(name, ""); //$NON-NLS-1$
	}
	
	public static XMLType xmlPi(String name, String content) {
		int start = 0;
		char[] chars = content.toCharArray();
		while (start < chars.length && chars[start] == ' ') {
			start++;
		}
		XMLType result = new XMLType(new SQLXMLImpl("<?" + name + " " + content.substring(start) + "?>")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		result.setType(Type.PI);
		return result;
	}
	
	public static AtomicValue convertToAtomicValue(Object value) throws TransformerException {
		if (value instanceof java.util.Date) { //special handling for time types
        	java.util.Date d = (java.util.Date)value;
        	DateTimeValue tdv = DateTimeValue.fromJavaDate(d);
        	if (value instanceof Date) {
        		value = new DateValue(tdv.getYear(), tdv.getMonth(), tdv.getDay(), tdv.getTimezoneInMinutes());
        	} else if (value instanceof Time) {
        		value = new TimeValue(tdv.getHour(), tdv.getMinute(), tdv.getSecond(), tdv.getMicrosecond(), tdv.getTimezoneInMinutes());
        	} else if (value instanceof Timestamp) {
        		Timestamp ts = (Timestamp)value;
        		value = tdv.add(DayTimeDurationValue.fromMicroseconds(ts.getNanos() / 1000));
        	}
        	return (AtomicValue)value;
        }
		JPConverter converter = JPConverter.allocate(value.getClass(), null);
		return (AtomicValue)converter.convert(value, null);
	}
	
	static void convertValue(Writer writer, XMLEventWriter eventWriter, XMLEventFactory eventFactory, Object object) throws IOException,
			FactoryConfigurationError, XMLStreamException,
			TransformerException {
		if (object == null) {
			return;
		}
		Reader r = null;
		try {
			if (object instanceof XMLType) {
				XMLType xml = (XMLType)object;
				Type type = xml.getType();
				convertReader(writer, eventWriter, null, type, xml);
			} else if (object instanceof Clob) {
				Clob clob = (Clob)object;
				r = clob.getCharacterStream();
				convertReader(writer, eventWriter, r, Type.TEXT, null);
			} else {
				String val = convertToAtomicValue(object).getStringValue();
				eventWriter.add(eventFactory.createCharacters(val));
			}
		} catch (Exception e) {
			throw new IOException(e);
		} finally {
			if (r != null) {
				r.close();
			}
		}
		//TODO: blob - with base64 encoding
	}

	private static void convertReader(Writer writer,
			XMLEventWriter eventWriter, Reader r, Type type, XMLType xml)
			throws Exception {
		switch(type) {
		case CONTENT:
		case ELEMENT: 
		case PI:
		case COMMENT: {//write the value directly to the writer
			eventWriter.flush();
			char[] buf = new char[1 << 13];
			int read = -1;
			if (r == null) {
				r = xml.getCharacterStream();
			}
			while ((read = r.read(buf)) != -1) {
				writer.write(buf, 0, read);
			}
			break;
		}
		case UNKNOWN:  //assume a document
		case DOCUMENT: //filter the doc declaration
			XMLEventReader eventReader = null;
			XMLInputFactory inputFactory = XMLType.getXmlInputFactory();
			if (r != null) {
				if (!(r instanceof BufferedReader)) {
					r = new BufferedReader(r);
				}
				eventReader = inputFactory.createXMLEventReader(r);
			} else {
				StAXSource staxSource = xml.getSource(StAXSource.class);
				eventReader = staxSource.getXMLEventReader();
				if (eventReader == null) {
					eventReader = inputFactory.createXMLEventReader(staxSource.getXMLStreamReader());
				}
			}
			eventReader = inputFactory.createFilteredReader(eventReader, declarationOmittingFilter);
			eventWriter.add(eventReader);
			break;
		case TEXT:
			if (r == null) {
				r = xml.getCharacterStream();
			}
			XMLEventFactory eventFactory = threadLocalEventtFactory.get();
			char[] buf = new char[1 << 13];
			int read = -1;
			while ((read = r.read(buf)) != -1) {
				eventWriter.add(eventFactory.createCharacters(new String(buf, 0, read)));
			}
			break;
		}
	}
	
	public static XMLType xmlComment(String comment) {
		return new XMLType(new SQLXMLImpl("<!--" + comment + "-->")); //$NON-NLS-1$ //$NON-NLS-2$
	}

    public static Source convertToSource(Object value) throws Exception {
    	if (value == null) {
    		return null;
    	}
    	try {
	    	if (value instanceof SQLXML) {
				return ((SQLXML)value).getSource(null);
	    	}
	    	if (value instanceof Clob) {
	    		return new StreamSource(((Clob)value).getCharacterStream());
	    	}
	    	if (value instanceof Blob) {
	    		return new StreamSource(((Blob)value).getBinaryStream());
	    	}
	    	if (value instanceof String) {
	    		return new StreamSource(new StringReader((String)value));
	    	}
    	} catch (SQLException e) {
			 throw new TeiidClientException(e);
		}
    	throw new AssertionError("Unknown type"); //$NON-NLS-1$
    }

    public static void closeSource(final Source source) {
        if (!(source instanceof StreamSource)) {
            return;
        }
        
        StreamSource stream = (StreamSource)source;
        try {
            if (stream.getInputStream() != null) {
                stream.getInputStream().close();
            }
        } catch (IOException e) {
        }
        try {
            if (stream.getReader() != null) {
                stream.getReader().close();
            }
        } catch (IOException e) {
        }
    }

    public static String xpathValue(Object doc, String xpath) throws Exception {
    	Source s = null;
        try {
        	s = convertToSource(doc);
            XPathEvaluator eval = new XPathEvaluator();
            // Wrap the string() function to force a string return             
            XPathExpression expr = eval.createExpression(xpath);
            Object o = expr.evaluateSingle(s);
            
            if(o == null) {
                return null;
            }
            
            // Return string value of node type
            if(o instanceof Item) {
            	Item i = (Item)o;
            	if (isNull(i)) {
            		return null;
            	}
                return i.getStringValue();
            }  
            
            // Return string representation of non-node value
            return o.toString();
        } finally {
        	closeSource(s);
        }
    }
    
	public static boolean isNull(Item i) {
		if (i instanceof NodeInfo) {
			NodeInfo ni = (NodeInfo)i;
			return ni.getNodeKind() == net.sf.saxon.type.Type.ELEMENT && !ni.hasChildNodes() && Boolean.valueOf(ni.getAttributeValue(StandardNames.XSI_NIL));
			/* ideally we'd be able to check for nilled, but that doesn't work without validation
			 if (ni.isNilled()) {
				tuple.add(null);
				continue;
			}*/
		}
		return false;
	}
    
    /**
     * Validate whether the XPath is a valid XPath.  If not valid, an XPathExpressionException will be thrown.
     * @param xpath An xpath expression, for example: a/b/c/getText()
     * @throws XPathExpressionException 
     * @throws XPathException 
     */
    public static void validateXpath(String xpath) throws XPathException {
        if(xpath == null) { 
            return;
        }
        
        XPathEvaluator eval = new XPathEvaluator();
        eval.createExpression(xpath);
    }
    
    public static String escapeName(String name, boolean fully) {
    	StringBuilder sb = new StringBuilder();
    	char[] chars = name.toCharArray();
    	int i = 0;
    	if (fully && name.regionMatches(true, 0, "xml", 0, 3)) { //$NON-NLS-1$
			sb.append(escapeChar(name.charAt(0)));
			sb.append(chars, 1, 2);
			i = 3;
    	}
    	for (; i < chars.length; i++) {
    		char chr = chars[i];
    		switch (chr) {
    		case ':':
    			if (fully || i == 0) {
    				sb.append(escapeChar(chr));
    				continue;
    			} 
    			break;
    		case '_':
    			if (chars.length > i && chars[i+1] == 'x') {
    				sb.append(escapeChar(chr));
    				continue;
    			}
    			break;
    		default:
    			//TODO: there should be handling for surrogates
    			//      and invalid chars
    			if (i == 0) {
    				if (!Name11Checker.getInstance().isNCNameStartChar(chr)) {
    					sb.append(escapeChar(chr));
    					continue;
    				}
    			} else if (!Name11Checker.getInstance().isNCNameChar(chr)) {
    				sb.append(escapeChar(chr));
    				continue;
    			}
    			break;
    		}
			sb.append(chr);
		}
    	return sb.toString();
    }

	private static String escapeChar(char chr) {
		CharBuffer cb = CharBuffer.allocate(7);
		cb.append("_u");  //$NON-NLS-1$
		CharsetUtils.toHex(cb, (byte)(chr >> 8));
		CharsetUtils.toHex(cb, (byte)chr);
		return cb.append("_").flip().toString();  //$NON-NLS-1$
	}

    public static SQLXML jsonToXml(final String rootName, final Blob json) throws Exception {
    	return jsonToXml(rootName, json, false);
    }
    
    public static SQLXML jsonToXml(final String rootName, final Blob json, boolean stream) throws Exception {
		Reader r = getJsonReader(json);
		return jsonToXml(rootName, r, stream);
    }
	public static InputStreamReader getJsonReader(final Blob json) throws SQLException,
			IOException {
		InputStream is = json.getBinaryStream();
		PushbackInputStream pStream = new PushbackInputStream(is, 4);
		byte[] encoding = new byte[4];
		int read = pStream.read(encoding);
		pStream.unread(encoding, 0, read);
		Charset charset = UTF_8;
		if (read > 3) {
			if (encoding[0] == 0 && encoding[2] == 0) {
				if (encoding[1] == 0) {
					charset = UTF_32BE; 
				} else {
					charset = UTF_16BE;
				}
			} else if (encoding[1] == 0 && encoding[3] == 0) {
				if (encoding[2] == 0) {
					charset = UTF_32LE; 
				} else {
					charset = UTF_16LE;
				}
			}
		}
		return new InputStreamReader(pStream, charset);
	}

	public static SQLXML jsonToXml(final String rootName, final Clob json) throws Exception {
        return jsonToXml(rootName, json, false);
    }
    
    public static SQLXML jsonToXml(final String rootName, final Clob json, boolean stream) throws Exception {
        return jsonToXml(rootName, json.getCharacterStream(), stream);
    }
    
    private static SQLXML jsonToXml(
            final String rootName, final Reader r, boolean stream) throws Exception {
        JSONParser parser = new JSONParser();
        final JsonToXmlContentHandler reader = new JsonToXmlContentHandler(rootName, r, parser, threadLocalEventtFactory.get());

        SQLXMLImpl sqlXml = null;
        if (stream) {
            try {
                //jre 1.7 event logic does not set a dummy location and throws an NPE in StAXSource, so we explicitly set a location
                //the streaming result will be directly consumed, so there's no danger that we're stepping on another location
                reader.eventFactory.setLocation(dummyLocation);
                sqlXml = new StAXSQLXML(new StAXSource(reader));
            } catch (XMLStreamException e) {
                throw new TeiidClientException(e);
            }
        } else {
            sqlXml = saveToBufferManager(new XMLTranslator() {
                
                @Override
                public void translate(Writer writer) throws TransformerException,
                        IOException {
                    try {
                        XMLOutputFactory factory = getOutputFactory();
                        final XMLEventWriter streamWriter = factory.createXMLEventWriter(writer);
    
                        streamWriter.add(reader);
                        streamWriter.flush(); //woodstox needs a flush rather than a close
                    } catch (XMLStreamException e) {
                        throw new TransformerException(e);
                    } finally {
                        try {
                            r.close();
                        } catch (IOException e) {
                            
                        }
                    }
                }
            });
        }
        XMLType result = new XMLType(sqlXml);
        result.setType(Type.DOCUMENT);
        return result;
    }

    /**
     * This method saves the given XML object to the buffer manager's disk process
     * Documents less than the maxMemorySize will be held directly in memory
     */
    public static SQLXMLImpl saveToBufferManager(XMLTranslator translator) throws Exception {        
        boolean success = false;
        MemoryStorageManager manager = new MemoryStorageManager();
        final FileStore lobBuffer = manager.createFileStore("xml"); //$NON-NLS-1$
        FileStoreInputStreamFactory fsisf = new FileStoreInputStreamFactory(lobBuffer, Streamable.ENCODING);
        try{  
            Writer writer = fsisf.getWriter();
            translator.translate(writer);
            writer.close();
            success = true;
            return new SQLXMLImpl(fsisf);
        } catch(Exception e) {
             throw new TeiidClientException(e);
        } finally {
            if (!success && lobBuffer != null) {
                lobBuffer.remove();
            }
        }
    }

	public static Object serialize(XMLSerialize xs, XMLType value) throws Exception {
		Type type = value.getType();
		final Charset encoding;
		if (xs.getEncoding() != null) {
			encoding = Charset.forName(xs.getEncoding());					
		} else {
			encoding = UTF_8;
		}
		if (Boolean.TRUE.equals(xs.getDeclaration())) {
			//need to replace existing/default declaration
			if (type == Type.ELEMENT || type == Type.DOCUMENT) {
				XMLEventFactory xmlEventFactory = threadLocalEventtFactory.get();
				xmlEventFactory.setLocation(dummyLocation);
				XMLEvent start = null;
				if (xs.getVersion() != null) {
					start = xmlEventFactory.createStartDocument(encoding.name(), xs.getVersion());
				} else if (xs.getEncoding() != null) {
					start = xmlEventFactory.createStartDocument(encoding.name());
				} else {
					start = xmlEventFactory.createStartDocument();
				}
				StAXSourceProvider sourceProvider = new DeclarationStaxSourceProvider(start, value);
				value = new XMLType(new StAXSQLXML(sourceProvider, encoding));
				value.setType(type);
			}
			//else just ignore, since the result is likely invalid
		} else if (type == Type.DOCUMENT && Boolean.FALSE.equals(xs.getDeclaration())){
			final XMLType v = value;
			StAXSourceProvider sourceProvider = new StAXSourceProvider() {
				@Override
				public StAXSource getStaxSource() throws SQLException {
					try {
						XMLEventReader eventReader = getXMLEventReader(v.getSource(StAXSource.class));
						eventReader = XMLType.getXmlInputFactory().createFilteredReader(eventReader, declarationOmittingFilter);
						return new StAXSource(eventReader);
					} catch (XMLStreamException e) {
						throw new SQLException(e);
					}
				}
			};
			value = new XMLType(new StAXSQLXML(sourceProvider, encoding));
			value.setType(Type.DOCUMENT);
		}
		if (xs.getType() == DataTypeManagerService.DefaultDataTypes.STRING.getTypeClass()) {
			return DataTypeManagerService.getInstance(xs.getTeiidVersion()).transformValue(value, xs.getType());
		}
		if (xs.getType() == DataTypeManagerService.DefaultDataTypes.CLOB.getTypeClass()) {
			InputStreamFactory isf = Evaluator.getInputStreamFactory(value);
			return new ClobType(new ClobImpl(isf, -1));
		}
		if (xs.getType() == DataTypeManagerService.DefaultDataTypes.VARBINARY.getTypeClass()) {
			try {
				InputStream is = null;
				if (!Charset.forName(value.getEncoding()).equals(encoding)) {
					is = new ReaderInputStream(value.getCharacterStream(), encoding);
				} else {
					is = value.getBinaryStream();					
				}
				byte[] bytes = ObjectConverterUtil.convertToByteArray(is, DataTypeManagerService.MAX_LOB_MEMORY_BYTES);
				return new BinaryType(bytes);
			} catch (SQLException e) {
				throw new TeiidClientException(e, Messages.gs(Messages.TEIID.TEIID10080, "XML", "VARBINARY")); //$NON-NLS-1$ //$NON-NLS-2$ 
		    } catch (IOException e) {
		    	throw new TeiidClientException(e, Messages.gs(Messages.TEIID.TEIID10080, "XML", "VARBINARY")); //$NON-NLS-1$ //$NON-NLS-2$
		    }
		}
		InputStreamFactory isf = null;
		if (!Charset.forName(value.getEncoding()).equals(encoding)) {
			//create a wrapper for the input stream
			isf = new InputStreamFactory.SQLXMLInputStreamFactory(value) {
				public InputStream getInputStream() throws IOException {
					try {
						return new ReaderInputStream(sqlxml.getCharacterStream(), encoding);
					} catch (SQLException e) {
						throw new IOException(e);
					}
				}
			};
		} else {
			isf = Evaluator.getInputStreamFactory(value);
		}
		return new BlobType(new BlobImpl(isf));
	}
    
}
