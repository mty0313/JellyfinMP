package top.mty.common;

public class JellyfinMPException extends Exception {

  public JellyfinMPException() {
    super();
  }

  public JellyfinMPException(String message) {
    super(message);
  }

  public JellyfinMPException(String message, Throwable cause) {
    super(message, cause);
  }

  public JellyfinMPException(Throwable cause) {
    super(cause);
  }

  protected JellyfinMPException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
