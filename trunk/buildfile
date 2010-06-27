VERSION_NUMBER = "0.9.1"
GROUP = "shawty"
COPYRIGHT = "Covered by the Apache Software License"

repositories.remote << "http://www.ibiblio.org/maven2/"

desc "The Shawty text extractor."

require 'buildr/groovy'

# If I don't do this test.compile fails :( 
#Java.classpath << "junit:junit:jar:4.8.1"

define "shawty" do
  manifest["Implementation-Vendor"] = COPYRIGHT
  project.version = VERSION_NUMBER
  project.group = GROUP
  test.resources
  package :jar
end
