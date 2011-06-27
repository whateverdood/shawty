package com.googlecode.shawty;

import org.junit.Test;

import com.googlecode.shawty.DoctypeEliminator;
import com.googlecode.shawty.EmptyNamespaceEliminator;
import com.googlecode.shawty.Preprocessor;

import static org.junit.Assert.*;

class PreprocessorTests {
    
    @Test
    void testEmptyNamespaceEliminator() {
        Preprocessor pp = new EmptyNamespaceEliminator()
        String eliminated = pp.process('''<?xml version="1.0"?>
            <foo xmlns="">
                <bar xmlns="">bar</bar>
                <baz>baz</baz>
            </foo>''')
        assertEquals '''<?xml version="1.0"?>
            <foo >
                <bar >bar</bar>
                <baz>baz</baz>
            </foo>''', eliminated
    }

    @Test
    void testDoctypeEliminator() {
        Preprocessor pp = new DoctypeEliminator()
        String eliminated = pp.process('''<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
            <html xmlns="http://www.w3.org/1999/xhtml">
            <head>
            </head>
            <body>
            </body>
            </html>''')
        assertEquals '''
            <html xmlns="http://www.w3.org/1999/xhtml">
            <head>
            </head>
            <body>
            </body>
            </html>''', eliminated 
    }
    
}
