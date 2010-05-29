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
    
    def forEach = "/" // sensible null-safe default?
    
    def fieldMappings = [:]
    
    def namespaces = [:]
    
    List extract(string) {
        
        def extracts = []
        
        def dom = toDom(stripDefaultNamespace(string))
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
     * Removes the default xml namespace if one exists b/c XPath is dumb.
     * @param string
     * @return
     */
    def stripDefaultNamespace(string) {
        string.replaceAll(/xmlns *= *["'].[^"'>]*["']/, "")
    }
    
    /**
     * Generate a DOM object graph from the supplied xml.
     * @param string The string of hopefully well-formed xml.
     * @return The top-level DOM node.
     */
    org.w3c.dom.Node toDom(string) {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance()
        saxParserFactory.setNamespaceAware(true)
        XMLReader reader = saxParserFactory.newSAXParser().getXMLReader()
        
        Transformer transformer = TransformerFactory.newInstance().newTransformer()
        DOMResult result = new DOMResult()
        transformer.transform(new SAXSource(reader, 
        new InputSource(new ByteArrayInputStream(string.bytes))), result)
        result.node
    }
    
    /**
     * Creates a new XPath that uses the supplied namespace info.
     * @return
     */
    XPath newNSAwareXPath() {
        def xpath = XPathFactory.newInstance().newXPath()
        
        final ns = namespaces
        
        xpath.setNamespaceContext([getNamespaceURI: { 
            prefix ->
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
        xpath
    }
    
    /**
     * Do it, first looking for a String and falling back on a NodeSet if
     * necessary.
     * @param xpath The XPath used.
     * @param path The path to evaluate.
     * @param node The DOM node to search
     * @return
     */
    def evaluate(xpath, path, node) {
        def value = xpath.evaluate(path, node)?.trim()
        
        if (!value || "".equals(value)) {
            def nodes = xpath.evaluate(path, node, XPathConstants.NODESET)
            for (def i = 0; i < nodes.length; i++) {
                def n = nodes.item(i)
                value += n.textContent?.trim() ?: " "
            }
        }
        
        value
    }
    
}
