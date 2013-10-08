@GrabResolver(name='groovy-template', root='http://artifacts.iteego.com/artifactory/public-release-local')
@Grab('org.codehaus.groovy:groovy-streaming-template-engine:1.5')
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
