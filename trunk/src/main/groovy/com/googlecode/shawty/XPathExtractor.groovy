package com.googlecode.shawty;

import java.util.List;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.xpath.*;

class XPathExtractor {
    
    /**
     * Required - XPath expression that should produce a list of nodes that can 
     * be iterated over.  If the document doesn't have a collection then just
     * set this "/". 
     */
    def forEach = "/"
    
    /**
     * Required - map/table of fields and the XPath expressions that will be
     * used to populate them.
     */
    def fieldMappings = [:]
    
    /**
     * Optional - if your document tags are namespace'd then define them in this
     * map.
     */
    def namespaces = [:]
    
    /**
     * A list of components that massage the input prior to extraction.
     */
    def preprocessors = []
    
    /**
     * An implementation of XMLReader that may be specified if you don't want to
     * use the built-in JRE impl.
     */
    def xmlReaderClazz
    
    /**
     * Extracts a list of maps, populated based on fieldMapping rules and such.
     * @param string
     * @return
     */
    List extract(input) {
        
        def extracts = []
        
        JXPathContext rootContext = JXPathContext.newContext(toDom(input))

        namespaces.each { prefix, uri -> 
            rootContext.registerNamespace(prefix, uri)
        }
        
        for (Pointer pointer : rootContext.iteratePointers(forEach)) {
            JXPathContext xpath = JXPathContext.newContext(
                rootContext, pointer.getNode())
            xpath.setLenient(true)
            
            def extract = [:]
            fieldMappings.each { field, xPathExpr ->
                extract.put(field, 
                    xpath.iterate(xPathExpr)?.toList()?.join(' ')?.trim())
            }
            
            extracts << extract
        }
    
        extracts
    }
    
    /**
     * Generate a DOM object graph from the supplied xml.
     * @param string The string of hopefully well-formed xml.
     * @return The top-level DOM node.
     */
    org.w3c.dom.Node toDom(string) {
        Transformer transformer = TransformerFactory.newInstance().newTransformer()
        XMLReader reader = getXmlReader()
        DOMResult result = new DOMResult()
        transformer.transform(new SAXSource(reader, 
            new InputSource(new ByteArrayInputStream(string.bytes))), result)
        return result.node
    }
    
    /**
     * Get an XMLReader impl, using the JRE built-in if xmlReaderClazz is not set. 
     * @return An XMLReader
     */
    XMLReader getXmlReader() {
        if (xmlReaderClazz) {
            return Class.forName(xmlReaderClazz).newInstance()
        } else {
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance()
            saxParserFactory.setNamespaceAware(true)
            saxParserFactory.setValidating(false)
            return saxParserFactory.newSAXParser().getXMLReader()
        }
    }
    
}
