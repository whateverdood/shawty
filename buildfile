# Generated by Buildr 1.3.5, change to your liking
# Version number for this release
VERSION_NUMBER = "0.9.0"
# Group identifier for your projects
GROUP = "shawty"
COPYRIGHT = ""

require 'buildr/groovy'

# Specify Maven 2.0 remote repositories here, like this:
repositories.remote << "http://www.ibiblio.org/maven2/"

desc "The Shawty text extractor."

#local_task :test_groovies

Java.classpath << "junit:junit:jar:4.8.1"

define "shawty" do
  #compile.with 'junit:junit:jar:4.8.1'
  manifest["Implementation-Vendor"] = COPYRIGHT
  project.version = VERSION_NUMBER
  project.group = GROUP
  test.resources
  #test.include 'com.google.shawty.*'
  #test.using :junit4 => true
end