package groovy.text

import spock.lang.*
import java.util.zip.ZipFile
import java.util.zip.ZipEntry
import java.util.jar.Attributes
import static groovy.text.SMARTTemplateEngineSpecification.EngineType.*

/**
 * http://code.google.com/p/spock/wiki/SpockBasics
 */
class SMARTTemplateEngineSpecification extends Specification {
  enum EngineType { 
    SMART('SmartTemplateEngine'), 
    SIMPLE('SimpleTemplateEngine'), 
    GSTRING('GStringTemplateEngine')
    String displayString
    
    EngineType(displayString) {
      this.displayString = displayString
    }
    
    String toString() {
      displayString      
    }
  }
  @Shared String SIXTY_FOUR_K_OF_A
  @Shared int SIXTY_FOUR_K = 64*1024
  @Shared Map defaultBinding = [alice: 'Alice', rabbit: 'Rabbit', queen: 'Queen', desk: 'writing desk']

  // run before the first feature method
  def setupSpec() {     
    StringBuilder b = new StringBuilder()
    def sixtyFourAs = "a"*64
    (1..1024).each {
      b.append(sixtyFourAs)
    }
    SIXTY_FOUR_K_OF_A = b.toString()    
  }  
  def cleanupSpec() {}   // run after the last feature method
  def setup() {}          // run before every feature method
  def cleanup() {}        // run after every feature method

  private String template(EngineType type, String data, Map binding=null) {
    TemplateEngine engine
    switch (type) {
      case SMART: 
        engine = new SmartTemplateEngine()
        break
      case SIMPLE: 
        engine = new SimpleTemplateEngine()
        break
      case GSTRING: 
        engine = new GStringTemplateEngine()
        break
    }
    Template template = engine.createTemplate(data)

    Writable writable = (binding ? template.make(binding) : template.make())
    StringWriter sw = new StringWriter()
    writable.writeTo(sw)

    sw
  }

  @Unroll("#testName - #engineType should evaluate '#data' to '#expectedResult' using binding '#binding'")
  def "Check templating that should work"() {
    expect: 
      template(engineType, data, binding) == expectedResult

    where:
      data                   | expectedResult     | engineType   | binding        | testName
      ''                     | ''                 | SMART        | null           | 'testEmptyStringNoBinding'
      ''                     | ''                 | SMART        | defaultBinding | 'testEmptyStringWithBinding'
      'bob'                  | 'bob'              | SMART        | null           | 'noExpressionsNoBinding'
      'bob'                  | 'bob'              | SMART        | defaultBinding | 'noExpressionWithBinding'

      'Hello World\\'        | 'Hello World\\'    | SMART        | null           | 'noExpressionsNoBindingEscapingAtEnd'
      'Hello World\\'        | 'Hello World\\'    | SMART        | defaultBinding | 'noExpressionsWithBindingEscapingAtEnd'
      'Hello World\\\\'      | 'Hello World\\\\'  | SMART        | null           | 'noExpressionsNoBindingDoubleEscapingAtEnd'
      'Hello World\\\\'      | 'Hello World\\\\'  | SMART        | defaultBinding | 'noExpressionsWithBindingDoubleEscapingAtEnd'
      /*
      'bob'                  | 'bob'              | SMART        | defaultBinding | 'noExpressionWithBinding'
      'bob'                  | 'bob'              | SMART        | null           | 'noExpressionsNoBinding'
      'bob'                  | 'bob'              | SMART        | defaultBinding | 'noExpressionWithBinding'

*/



  }
}
