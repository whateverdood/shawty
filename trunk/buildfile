VERSION_NUMBER = "0.9.2"
GROUP = "shawty"
COPYRIGHT = "Covered by the Apache Software License"

repositories.remote << "http://www.ibiblio.org/maven2/"
repositories.remote << "http://repo1.maven.org/maven2/"

desc "The Shawty text extractor."

require 'buildr/groovy'

define "shawty" do
  compile.with 'org.ccil.cowan.tagsoup:tagsoup:jar:1.2'
  compile.with 'commons-jxpath:commons-jxpath:jar:1.3'
  manifest["Implementation-Vendor"] = COPYRIGHT
  project.version = VERSION_NUMBER
  project.group = GROUP
  test.resources
  package :jar
end
