package mcmaster.avero.posfetch;

import java.io.IOException;

class PosDataException extends RuntimeException {

  PosDataException(String message, IOException cause) {
    super(message, cause);
  }
}
