package com.googlecode.shawty;

import java.util.List

import javax.xml.parsers.SAXParserFactory
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMResult
import javax.xml.transform.sax.SAXSource
import javax.xml.xpath.*

import org.apache.commons.jxpath.JXPathContext
import org.apache.commons.jxpath.Pointer
import org.xml.sax.InputSource
import org.xml.sax.XMLReader

class XPathExtractor {
    
    /**
     * Required - XPath expression that should produce a list of nodes that can 
     * be iterated over.  If the document doesn't have a collection then just
     * set this "/". 
     */
    String forEach = "/"
    
    /**
     * Required - map/table of fields and the XPath expressions that will be
     * used to populate them.
     */
    Map<String, String> fieldMappings = [:]
    
    /**
     * Optional - if your document tags are namespace'd then define them in this
     * map.
     */
    Map<String, String> namespaces
    
    /**
     * A list of components that massage the input prior to extraction.
     */
    List<Preprocessor> preprocessors = []
    
    /**
     * An implementation of XMLReader that may be specified if you don't want to
     * use the built-in JRE impl.
     */
    String xmlReaderClazz
    
    /**
     * Extracts a list of maps, populated based on fieldMapping rules and such.
     * @param input Well- or mal-formed markup
     * @return
     */
    List<Map<String, List<String>>> extract(String input) {

        List<Map<String, List<String>>> extracts = []
        
        JXPathContext rootContext = JXPathContext.newContext(toDom(input))

        namespaces.each { prefix, uri -> 
            rootContext.registerNamespace(prefix, uri)
        }
        
        for (Pointer pointer : rootContext.iteratePointers(forEach)) {
            JXPathContext xpath = JXPathContext.newContext(
                rootContext, pointer.getNode())
            xpath.setLenient(true)
            
            Map<String,List<String>> extract = [:]
            fieldMappings.each { field, xPathExpr ->
                extract.put(field, 
                    xpath.iterate(xPathExpr)?.toList()?.join(' ')?.trim())
            }
            
            extracts << extract
        }
    
        return extracts
    }
    
    TransformerFactory transformerFactory = TransformerFactory.newInstance()

    /**
     * Generate a DOM object graph from the supplied xml.
     * @param string The string of hopefully well-formed xml.
     * @return The top-level DOM node.
     */
    org.w3c.dom.Node toDom(String string) {
        preprocessors.each { pre ->
            string = pre.process(string)
        }
                
        Transformer transformer = transformerFactory.newTransformer()
        XMLReader reader = getXmlReader()
        DOMResult result = new DOMResult()
        InputSource source = new InputSource(new StringReader(string))
        transformer.transform(new SAXSource(reader, source), result)
        return result.getNode()
    }

    SAXParserFactory saxParserFactory = SAXParserFactory.newInstance()
            
    /**
     * Get an XMLReader impl, using the JRE built-in if xmlReaderClazz is not set. 
     * @return An XMLReader
     */
    XMLReader getXmlReader() {
        XMLReader reader = null
        if (xmlReaderClazz) {
            reader = Class.forName(xmlReaderClazz).newInstance()
        } else {
            reader = saxParserFactory.newSAXParser().getXMLReader()
        }
        reader.setFeature("http://xml.org/sax/features/validation", false)
        if (namespaces) {
            reader.setFeature("http://xml.org/sax/features/namespaces", true)
            reader.setFeature("http://xml.org/sax/features/namespace-prefixes", true)
        } else {
            reader.setFeature("http://xml.org/sax/features/namespaces", false)
            reader.setFeature("http://xml.org/sax/features/namespace-prefixes", false)
        }
        return reader
    }
    
}
