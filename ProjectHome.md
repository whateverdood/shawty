# Welcome to Shawty #
Shawty is an XPath-based text extractor written in Groovy.  The idea is to extract a Map/Table of fields from any marked-up source, like X/HTML, XML, SGML, etc.

## Building ##
You'll need [Groovy](http://groovy.codehaus.org/) 1.6+, Java 1.5+, and [Maven](http://maven.apache.org) 3.  Once you've downloaded and installed all that stuff, running:

`$ mvn test package`

...will produce `shawty-{version}.jar` in the "target" sub-directory of the Shawty tree.

## Example Usage ##
See `$SHAWTY_DIR/src/test/groovy/com/google/shawty/XPathExtractorTests.groovy` for details, but in a nutshell, `XPathExtractor` is what you'll primarily use, say, to extract some text from a web page:

```
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
        
        assertEquals "Invalid title: ${actual}", expected.title, actual.title
        assertEquals "Invalid author: ${actual}", expected.author, actual.author
        assertEquals "Invalid date: ${actual}", expected.date, actual.date
        
        expected.text.each { bit ->
            assertTrue "Invalid text: ${actual}", actual.text.contains(bit)
        }
```

XPathExtractor also understands namespaced input.  Have a look at the Test fixture referred to above.  You may optionally add a list of Preprocessor impls to the XPathExtractor ctor if you want to massage the input.

# Thanks... #
...for looking at Shawty!  I hope you find it useful.