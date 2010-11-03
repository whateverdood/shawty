VERSION_NUMBER = "0.9.1"
GROUP = "shawty"
COPYRIGHT = "Covered by the Apache Software License"

repositories.remote << "http://www.ibiblio.org/maven2/"

desc "The Shawty text extractor."

require 'buildr/groovy'

define "shawty" do
  compile.with 'org.ccil.cowan.tagsoup:tagsoup:jar:1.2'
  manifest["Implementation-Vendor"] = COPYRIGHT
  project.version = VERSION_NUMBER
  project.group = GROUP
  test.resources
  package :jar
end
