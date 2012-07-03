package com.googlecode.shawty;


import org.ccil.cowan.tagsoup.Parser;
import org.junit.Test

import com.googlecode.shawty.XPathExtractor;
import static org.junit.Assert.*

class XPathExtractorTests {
    
    @Test
    void testExtract() {
        
        def xml = '''<html>
                <head>
                    <title>Sample Page</title>
                    <meta content="Hege Refsnes" name="author">
                    <meta content="2010/06/20" name="revised">
                </head>
                <body>
                    <p>By eight o'clock Passepartout had packed the modest carpet-bag.</p>
                    <p>Mr. Fogg was quite ready.</p>
                    <p>"You have forgotten nothing?" asked he.</p>
                    <a href="http://www.w3schools.com">This is a link</a>
                </body>
            </html>'''

        def forEach = "/html"
        def xpaths = ["author": "head/meta[@name='author']/@content", 
            "title": "head/title",
            "date": "head/meta[@name='revised']/@content",
            "links": "//@href",
            "text": "body//text()"]
        
        XPathExtractor extractor = new XPathExtractor(
            forEach: forEach, fieldMappings: xpaths, 
            xmlReaderClazz: "org.ccil.cowan.tagsoup.Parser")
        def actuals = extractor.extract(xml)
        
        assertEquals "One set of fields should have been parsed out.", 1, actuals.size()
        
        def actual = actuals.get(0)
        
        def expected = ["title": "Sample Page", 
            "author": "Hege Refsnes", 
            "date": "2010/06/20",
            "text": ["By eight o'clock Passepartout had packed the modest carpet-bag.", 
                "Mr. Fogg was quite ready.",
                "\"You have forgotten nothing?\" asked he.",
                "This is a link"]]
        
        assertEquals "Invalid title: ${actual.title}", expected.title, actual.title
        assertEquals "Invalid author: ${actual.author}", expected.author, actual.author
        assertEquals "Invalid date: ${actual.date}", expected.date, actual.date
        
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
        
        def feed = '''<?xml version="1.0" encoding="utf-8"?>
            <feed xmlns="http://www.w3.org/2005/Atom">
                <title>Example Feed</title>
                <subtitle>A subtitle.</subtitle>
                <link href="http://example.org/feed/" rel="self" />
                <link href="http://example.org/" />
                <id>urn:uuid:60a76c80-d399-11d9-b91C-0003939e0af6</id>
                <updated>2003-12-13T18:30:02Z</updated>
                <author>
                    <name>John Doe</name>
                    <email>johndoe@example.com</email>
                </author>
                <entry>
                    <title>Atom-Powered Robots Run Amok</title>
                    <link href="http://example.org/2003/12/13/atom03" />
                    <link rel="alternate" type="text/html" href="http://example.org/2003/12/13/atom03.html"/>
                    <link rel="edit" href="http://example.org/2003/12/13/atom03/edit"/>
                    <id>urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a</id>
                    <updated>2003-12-13T18:30:02Z</updated>
                    <summary>Some text.</summary>
                </entry>
                <entry>
                    <title>Foo</title>
                    <link href="http://example.org/2003/12/14/foo" />
                    <link rel="alternate" type="text/html" href="http://example.org/2003/12/14/foo.html"/>
                    <link rel="edit" href="http://example.org/2003/12/14/foo/edit"/>
                    <id>urn:uuid:1225c695-cfb8-4ebb-bbbb-80da344efa6a</id>
                    <updated>2003-12-14T18:30:02Z</updated>
                    <summary>Some different text.</summary>
                </entry>
            </feed>'''
        
        def xpaths = ["title": "title", 
            "author": "../author/name", 
            "updated": "updated", 
            "content": "summary"]
        
        doExtract(feed, "/feed/entry", null, xpaths) { List<Map> extracted ->
            println extracted
            assertEquals "Wrong number of entries extracted", 2, extracted.size()
            // TODO: add more assertions
        }
    }
    
    /**
     * Use Tagsoup to transform "extreme" HTML into XHTML so we can extract
     * all paragraph content.
     */
    @Test
    public void textExtractsHtml() throws Exception {
        
        URL resource = this.class.getResource("/extreme.html")

        Map<String, String> namespaces = ["h": "http://www.w3.org/1999/xhtml"]
        Map<String, String> xpaths = ["content": "//h:p/text()"]

        doExtract(resource.text, "/", namespaces, xpaths) { List<Map> extracted ->
            assertEquals("Incorrect # of extracted field sets", 1, extracted.size())
            assertEquals("Incorrect extracted text", '''ABC  xyz
QRS''', extracted.get(0).get("content")) // raw content is ugly so content is too
        }
        
        doExtract(resource.text, "/", null, ["content": "//p/text()"]) { List<Map> extracted ->
            assertEquals("Incorrect # of extracted field sets", 1, extracted.size())
            assertEquals("Incorrect extracted text", '''ABC  xyz
QRS''', extracted.get(0).get("content"))
        }
    }
    
    void doExtract(String text, 
        String forEach,
        Map<String, String> ns,
        Map<String, String> xpaths, 
        Closure doAssertions) {
        
        XPathExtractor extractor = new XPathExtractor(namespaces: ns,
            forEach: forEach, fieldMappings: xpaths,
            xmlReaderClazz: "org.ccil.cowan.tagsoup.Parser")
        
        List<Map<String, List<String, String>>> extracted = extractor.extract(text)
        
        doAssertions(extracted)
    }
    
}
