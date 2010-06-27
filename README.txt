Overview
========
You've downloaded Shawty, a Groovy/Java based XPath text extractor.  For information on
how to use Shawty's XPathExtractor see XPathExtractorTests.groovy.

Dependencies
============
Groovy 1.6 or later and Buildr 1.4 or later.

Build
=====
To build Shawty, install Buildr (http://buildr.apache.org) and run:

$ buildr test package

Notes
=====
On Mac OS X Leopard, running Buildr 1.4 with Ruby 1.8 (32-bit) and Java 1.6 (64-bit) fails
the build with the following message.

-snip-
Buildr aborted!
RuntimeError : can't create Java VM
-snip-

If you have a 32-bit Java 1.5 JVM left-over (like I did) then you're fine.  Just reset
JAVA_HOME to point to the 1.5 VM, update your PATH just in case, and re-run "buildr test".