package com.google.shawty;

import java.util.List;

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
        
        preprocessors.each { pp ->
            input = pp.process(input)
        }
        
        def dom = toDom(input)
        def xpath = newNSAwareXPath()
        
        def docs = xpath.evaluate(forEach, dom, XPathConstants.NODESET)
        
        docs.length.times { i ->
            def doc = docs.item(i)
            def extract = [:]
            fieldMappings.each { field, path ->
                extract.put(field, evaluate(xpath, path, doc)?.trim())
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
        result.node
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
    
    /**
     * Creates a new XPath that uses the supplied namespace info.
     * @return
     */
    XPath newNSAwareXPath() {
        def xpath = XPathFactory.newInstance().newXPath()
        
        final ns = namespaces
        
        xpath.setNamespaceContext([getNamespaceURI: {  prefix ->
            ns.get(prefix)
        }, 
        getPrefix: { namespaceURI ->
            ns.each { key, value ->
                if (namespaceURI == value) {
                    return key
                }
            }
        }, 
        getPrefixes: { namespaceURI ->
            return [getPrefix(namespaceURI)]
        }] as NamespaceContext)

        return xpath
    }
    
    /**
     * Extract the XPath text contents, first looking for both String values as well as NodeSet
     * @param xpath The XPath used.
     * @param path The path to evaluate.
     * @param node The DOM node to search
     * @return
     */
    def evaluate(xpath, path, node) {
        def values = new HashSet()
        values << xpath.evaluate(path, node)?.trim()
        
        try {
            def nodes = xpath.evaluate(path, node, XPathConstants.NODESET)
            for (def i = 0; i < nodes.length; i++) {
                def n = nodes.item(i)
                values << n.textContent?.trim()
            }
        } catch (Exception ignored) {}
        
        return values.join(" ")
    }
    
}
