package mcmaster.avero.posfetch;

import java.io.IOException;

/**
 * Exception thrown when an error occurs while fetching data from the POS system or parsing the JSON
 * into domain objects.
 */
class PosDataException extends RuntimeException {

  PosDataException(String message, IOException cause) {
    super(message, cause);
  }
}
