# Streaming Temlpate Engine for Groovy

## What is this?
This repository contains a templating engine for the groovy programming language. It is 
an alternative and drop in replacement for the existing GStringTemplateEngine and 
SimpleTemplateEngine engines included in the groovy libraries. 

The name is slightly misleading as the code is not exactly based on streaming, but 
the term 'streaming' captures the essense of the idea and was the best I could come up
with. 

## Why another groovy template engine?
The existing groovy template engines 
[SimpleTemplateEngine](http://groovy.codehaus.org/gapi/groovy/text/SimpleTemplateEngine.html) and 
[GStringTemplateEngine](http://groovy.codehaus.org/gapi/groovy/text/GStringTemplateEngine.html) can 
not handle template strings larger than 64k. They throw the following exceptions when asked 
to template a string of 64k+1 character: 

_GStringTemplateEngine_

     groovy.lang.GroovyRuntimeException: Failed to parse template script (your template may contain an error or be trying to use expressions not currently supported): startup failed:
    GStringTemplateScript1.groovy: 2: String too long. The given string is 65536 Unicode code units long, but only a maximum of 65535 is allowed.
     @ line 2, column 79.
       new Binding(delegate); out << """...
                                                         ^

_SimpleTemplateEngine_

    groovy.lang.GroovyRuntimeException: Failed to parse template script (your template may contain an error or be trying to use expressions not currently supported): startup failed:
    SimpleTemplateScript1.groovy: 1: String too long. The given string is 65536 Unicode code units long, but only a maximum of 65535 is allowed.
     @ line 1, column 11.
       out.print("""...
                      ^

in my experience, templates larger than 64k are not all that uncommon (I certainly run into them 
all the time) and this limitation seems artificial and unnecessary. 

## Why is this not part of the groovy language?
Perhaps one day it might be. I am certainly open to it if there is interest from the groovy 
maintainers. I think either fixing the existing engines or adding a new one which can handle 
arbitrary template sizes is essential for a modern, dynamic language such as groovy. 

## Usage
The interface for this template engine is identical to the existing groovy template engines. 

Example code: 

    SimpleTemplateEngine engine = new SimpleTemplateEngine()
    
    def binding = [bird: 'raven', subject: 'writing desk',  flag: true]
    String data = "Alice, why is a ${bird}, <% flag ? 'like' : 'not like' %> a <%= subject %> <% out << '!' %>"
    
    Template template = engine.createTemplate(data)
    Writable writable = template.make()
    StringWriter sw = new StringWriter()
    writable.writeTo(sw)
    
    println "RESULT: $sw"

Normally, the temlate data would probably be pulled from a file (ala jsp, gsp, asp, xsp) but the above 
demonstrates using the template engine on a string and is self contained. 

## History 
I initially wrote this template engine a number of years ago, but omitted to make it publicly 
available as I assumed that such a glaring limitation would be addressed by the groovy 
community and/or creators soon enough to make sharing the repository pointless. Years passed and 
about once a year I went back to my repo, ran my negative unit tests (which prove that the current 
groovy version still breaks for strings > 64k), shook my head and went back to my day job. 

As templates can be used for a large number of arbitrary programming tasks ranging from code generation, 
rendering of various text based formats, rendering HTML etc etc and as a number of years have passed 
with no fix the 64k limitation in sight, I figured it was time to make this code publicly available. 
