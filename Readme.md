# Streaming Temlpate Engine for Groovy

## What is this?
This repository contains a templating engine for the groovy programming language. It is 
an alternative and drop in replacement for the existing [SimpleTemplateEngine](http://groovy.codehaus.org/gapi/groovy/text/SimpleTemplateEngine.html) 
and [GStringTemplateEngine](http://groovy.codehaus.org/gapi/groovy/text/GStringTemplateEngine.html) engines included in the groovy libraries. 

The name is slightly misleading as the code is not exactly based on streaming, but 
the term 'streaming' captures the essense of the idea and was the best I could come up
with at the time. The engine was initially called SmartTemplateEngine but somebody once told 
me that calling your baby smart is obnoxious so I renamed it. 

## Why another groovy template engine?
The existing groovy template engines 
[SimpleTemplateEngine](http://groovy.codehaus.org/gapi/groovy/text/SimpleTemplateEngine.html) and 
[GStringTemplateEngine](http://groovy.codehaus.org/gapi/groovy/text/GStringTemplateEngine.html) 
can not handle template strings larger than 64k ([unit tests proving that fact](https://github.com/mbjarland/groovy-streaming-template-engine/blob/master/src/test/groovy/groovy/text/StreamingTemplateEngineTest.groovy#L201-219)). 
They throw the following exceptions when asked to template a string of 64k+1 character: 

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

## Usage
The interface for this template engine is identical to the existing groovy template engines. 

Example code (tested with groovy 2.1.6 or below): 

```groovy
@GrabResolver(name='groovy-template', root='http://artifacts.iteego.com/artifactory/public-release-local')
@Grab('org.codehaus.groovy:groovy-streaming-template-engine:1.6-SNAPSHOT')
@Grab('org.codehaus.groovy:groovy-templates:2.1.6')

import groovy.text.StreamingTemplateEngine
import groovy.text.SimpleTemplateEngine
import groovy.text.Template

StreamingTemplateEngine engine = new StreamingTemplateEngine()

def binding = [bird: 'raven', subject: 'writing desk',  flag: true]
String data = '''Alice, why is a ${bird} <%= flag ? 'like' : 'not like' %> a <%= subject %><% out << '?' %>'''

Template template = engine.createTemplate(data)
Writable writable = template.make(binding)
StringWriter sw = new StringWriter()
writable.writeTo(sw)

println "RESULT: $sw"

//now test with a data set that breaks the existing template engines
StringBuilder b = new StringBuilder()
def sixtyFourAs = "a" * 64
1024.times { b << sixtyFourAs }
def sixtyFourKAs = b.toString()

println "LENGTH: ${sixtyFourKAs.length()}"

engine.createTemplate(sixtyFourKAs).make()

boolean threwException = false
try { 
  new SimpleTemplateEngine().createTemplate(sixtyFourKAs).make()
} catch (GroovyRuntimeException e) {  
  println "Simple template engine fails..."
  threwException = e.message.contains('String too long')
}
assert threwException
```

executing this gives: 

    $ groovy sample_usage.groovy 
    RESULT: Alice, why is a raven like a writing desk?
    LENGTH: 65536
    Simple template engine fails...

(this code can be found in the samlpe_usage.groovy file in the root of the repo)

Normally, the template data would probably be pulled from a file (ala jsp, gsp, asp, xsp) but the above 
demonstrates using the template engine on a string, is self contained, and gives a feel for what this 
repo is about. 

## Limitations
The engine in this repo can handle templates into the hundreds of megabytes. 

This does however not mean the engine is fool proof. There are still limitations inherited 
from the bytecode file format on the jvm...and any potential bugs caused by mental 
stumbling on my part.  

As an example of such a limitation, creating a 300M template 
string with a template expression (i.e. '${bird}') every one kilobyte breaks the code with a 
"method too large" exception from the jvm. 

## Alternatives
Morten Kjetland has implemented a [replacement groovy template engine](https://github.com/mbknor/gt-engine) for 
the play framework. This looks very promising. For details, check out his [blog post](http://kjetland.com/blog/2011/11/playframework-new-faster-groovy-template-engine/)
about the release. 

I have not performed any tests on play framework engine and I dont' know if it has size limitations 
similar to the built in groovy ones or how it compares to the one in this repo. However, considering the complexity 
of Mortens implementation and the competence of the author I would make a qualified guess that it is very capable. 

The play framework engine uses a template language slightly different 
from the templage language used in the built in groovy template engines and the 
engine in this git repo. 

## Why is this not part of the groovy libraries?
Perhaps one day it might be. I am certainly open to it if there is interest from the groovy 
maintainers. I think either fixing the existing engines or adding a new one which can handle 
arbitrary template sizes is essential for a modern, dynamic language such as groovy. 

## History 
I initially wrote this template engine a number of years ago, but omitted to make it publicly 
available as I assumed that such a glaring limitation would be addressed by the groovy 
community and/or creators soon enough to make sharing the repository pointless. Years passed and 
about once a year I went back to my repo, ran my [negative unit tests](https://github.com/mbjarland/groovy-streaming-template-engine/blob/master/src/test/groovy/groovy/text/StreamingTemplateEngineTest.groovy#L201-219) (which prove that the current 
groovy version still breaks for strings > 64k), shook my head and went back to my day job. 

As templates can be used for a large number of arbitrary programming tasks ranging from code generation, 
rendering of various text based formats, rendering HTML etc etc and as a number of years have passed 
with no fix the 64k limitation in sight, I figured this code might save somebody some time. 

Could also be there is another template engine out there and I just failed to find it...if so, please shoot me 
an email and I will adjust accordingly. 

## Disclaimer
This repo does contain a fairly thought through set of unit tests and I believe the 
engine does what it is supposed to do (at least to a degree comparable to the existing template engines). 

That being said, this code has _not_ been tested in production and no guarantees are
made as for the validity of the code or the robustness of the templating logic. 

## About the Author
name: Matias Bjarland - coordinates: Gothenburg, Sweden

I run a software consultancy firm ([http://iteego.com](http://iteego.com)) which specializes in large scale e-commerce 
implementations and enterprise software integrations. 

I spend some of my spare cycles with groovy, gradle, grails, lisp, reading philosophy, learning new 
languages, rock climbing, marveling over the beauty of the universe, and barefoot running. 

Feel free to ping me via email at mbjarland@gmail.com.
