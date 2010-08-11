package com.google.shawty;

import org.junit.Test
import static org.junit.Assert.*

class XPathExtractorTests {
    
    @Test
    void testExtract() {
        
        def xml = '''<Inventory>
            <Book year="2000">
                <Title>Snow Crash</Title>
                <Author>Neal Stephenson</Author>
                <Publisher>Spectra</Publisher>
                <ISBN>0553380958</ISBN>
                <Price>14.95</Price>
            </Book>
        </Inventory>'''
        
        def forEach = "/Inventory/Book"
        def xpaths = ["title": "Title", 
            "author": "Author",
            "isbn": "ISBN",
            "published": "@year",
            "text": "descendant::text()"]
        
        XPathExtractor extractor = new XPathExtractor(forEach: forEach, fieldMappings: xpaths)
        def actuals = extractor.extract(xml)
        
        assertEquals "One set of fields should have been parsed out.", 1, actuals.size()
        
        def actual = actuals.get(0)
        
        def expected = ["title": "Snow Crash", 
            "author": "Neal Stephenson", 
            "isbn": "0553380958", 
            "text": ["Snow Crash", "Neal Stephenson", "0553380958", "14.95"]]
        
        assertEquals "Invalid title: ${actual}", expected.title, actual.title
        assertEquals "Invalid author: ${actual}", expected.author, actual.author
        assertEquals "Invalid isbn: ${actual}", expected.isbn, actual.isbn
        
        expected.text.each { bit ->
            assertTrue "Invalid text: ${actual}", actual.text.contains(bit)
        }
    }
    
    @Test
    void testExtractsRss() {
        
        def xml = '''<rss version="2.0" 
                xmlns:content="http://purl.org/rss/1.0/modules/content/"
                xmlns:wfw="http://wellformedweb.org/CommentAPI/"
                xmlns:dc="http://purl.org/dc/elements/1.1/"
                xmlns:atom="http://www.w3.org/2005/Atom" 
                xmlns:sy="http://purl.org/rss/1.0/modules/syndication/"
                xmlns:slash="http://purl.org/rss/1.0/modules/slash/" 
                xmlns:georss="http://www.georss.org/georss"
                xmlns:geo="http://www.w3.org/2003/01/geo/wgs84_pos#" 
                xmlns:media="http://search.yahoo.com/mrss/">
                <channel>
                    <title>Feed title</title>
                    <atom:link href="http://alleightllc.wordpress.com/feed/" rel="self"
                        type="application/rss+xml" />
                    <link>http://alleightllc.wordpress.com</link>
                    <description>Feed description</description>
                    <lastBuildDate>Tue, 01 Dec 2009 03:01:11 +0000</lastBuildDate>
                    <item>
                        <title>Post 1 title</title>
                        <link>Post 1 link</link>
                        <comments>Post 1 comments link</comments>
                        <pubDate>Tue, 01 Dec 2009 03:01:11 +0000</pubDate>
                        <dc:creator>Author</dc:creator>
                        <category>Post 1 cat</category>
                        <guid isPermaLink="true">guid-1</guid>
                        <content:encoded>Post 1 content</content:encoded>
                    </item>
                    <item>
                        <title>Post 2 title</title>
                        <link>Post 2 link</link>
                        <comments>Post 2 comments link</comments>
                        <pubDate>Tue, 02 Dec 2009 03:01:11 +0000</pubDate>
                        <dc:creator>Author</dc:creator>
                        <category>Post 2 cat</category>
                        <guid isPermaLink="true">guid-2</guid>
                        <content:encoded>Post 2 content</content:encoded>
                    </item>
                </channel>
            </rss>'''
        
        
        def forEach = "/rss/channel/item"
        def xpaths = ["title": "title", 
            "author": "dc:creator",
            "published": "pubDate",
            "content": "content:encoded"]
        def namespaces = ["dc": "http://purl.org/dc/elements/1.1/", 
            "content": "http://purl.org/rss/1.0/modules/content/",
            "atom": "http://purl.org/rss/1.0/modules/atom"]
        
        XPathExtractor extractor = new XPathExtractor(forEach: forEach, fieldMappings: xpaths, namespaces: namespaces)
        def actuals = extractor.extract(xml)
        
        def expected1 = ["title": "Post 1 title", "author": "Author", "content": "Post 1 content"]
        def expected2 = ["title": "Post 2 title", "author": "Author", "content": "Post 2 content"]
        
        assertEquals "Two sets of fields should have been parsed out.", 2, actuals.size()
        
        def actual = actuals.get(0)
        
        assertEquals "Invalid title: ${actual}", expected1.title, actual.title
        assertEquals "Invalid author: ${actual}", expected1.author, actual.author
        assertEquals "Invalid isbn: ${actual}", expected1.isbn, actual.isbn        
    }
    
    @Test
    void testExtractsAtom() {
        // requires atom.xml on the classpath *sigh*
        def forEach = "/feed/entry"
        def xpaths = ["title": "title[type='text']", 
            "author": "author/name", 
            "published": "published", 
            "content": "content"]
        
        XPathExtractor extractor = new XPathExtractor(forEach: forEach, fieldMappings: xpaths)
        def actuals = extractor.extract(extractor.class.classLoader.getResourceAsStream("atom.xml").text)
        
        assertEquals "Wrong number of entries extracted", 19, actuals.size()
        // TODO: add more assertions
    }
    
}
