package groovy.text;

/**
 * A custom exception class to flag template execution errors
 */
public class TemplateExecutionException extends Exception  {
  public TemplateExecutionException() {
    super();
  }

  public TemplateExecutionException(String message) {
    super(message);
  }

  public TemplateExecutionException(String message, Throwable cause) {
    super(message, cause);
  }

  public TemplateExecutionException(Throwable cause) {
    super(cause);
  }
}
