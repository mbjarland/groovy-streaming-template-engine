package groovy.text

import spock.lang.*

import static StreamingTemplateEngineSpecification.EngineType.*

/**
 * http://code.google.com/p/spock/wiki/SpockBasics
 */
class StreamingTemplateEngineSpecification extends Specification {
  enum EngineType { 
    STREAMING('StreamingTemplateEngine'),
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
      case STREAMING:
        engine = new StreamingTemplateEngine()
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

  @Unroll
  def "#testName - #engineType should evaluate '#data' to '#expectedResult' using binding '#binding'"() {
    expect: 
      template(engineType, data, binding) == expectedResult

    where:
      data                   | expectedResult      | engineType     | binding        | testName
      ''                     | ''                  | STREAMING      | null           | 'emptyStringNoBinding'
      ''                     | ''                  | STREAMING      | defaultBinding | 'emptyStringWithBinding'
      'bob'                  | 'bob'               | STREAMING      | null           | 'noExpressionsNoBinding'
      'bob'                  | 'bob'               | STREAMING      | defaultBinding | 'noExpressionsWithBinding'

      '\\Hello World'        | '\\Hello World'     | STREAMING      | null           | 'noExpressionsNoBindingEscapingAtStart'
      '\\Hello World'        | '\\Hello World'     | STREAMING      | defaultBinding | 'noExpressionsWithBindingEscapingAtStart'
      '\\\\Hello World'      | '\\\\Hello World'   | STREAMING      | null           | 'noExpressionsNoBindingDoubleEscapingAtStart'
      '\\\\Hello World'      | '\\\\Hello World'   | STREAMING      | defaultBinding | 'noExpressionsWithBindingDoubleEscapingAtStart'
      '\\\\\\Hello World'    | '\\\\\\Hello World' | STREAMING      | null           | 'noExpressionsNoBindingTripleEscapingAtStart'
      '\\\\\\Hello World'    | '\\\\\\Hello World' | STREAMING      | defaultBinding | 'noExpressionsWithBindingTripleEscapingAtStart'

      'Hello World\\'        | 'Hello World\\'     | STREAMING      | null           | 'noExpressionsNoBindingEscapingAtEnd'
      'Hello World\\'        | 'Hello World\\'     | STREAMING      | defaultBinding | 'noExpressionsWithBindingEscapingAtEnd'
      'Hello World\\\\'      | 'Hello World\\\\'   | STREAMING      | null           | 'noExpressionsNoBindingDoubleEscapingAtEnd'
      'Hello World\\\\'      | 'Hello World\\\\'   | STREAMING      | defaultBinding | 'noExpressionsWithBindingDoubleEscapingAtEnd'
      'Hello World\\\\\\'    | 'Hello World\\\\\\' | STREAMING      | null           | 'noExpressionsNoBindingTripleEscapingAtEnd'
      'Hello World\\\\\\'    | 'Hello World\\\\\\' | STREAMING      | defaultBinding | 'noExpressionsWithBindingTripleEscapingAtEnd'


      /*
      'bob'                  | 'bob'              | STREAMING        | defaultBinding | 'noExpressionWithBinding'
      'bob'                  | 'bob'              | STREAMING        | null           | 'noExpressionsNoBinding'
      'bob'                  | 'bob'              | STREAMING        | defaultBinding | 'noExpressionWithBinding'
*/
  }
}
