package swapper9.moviedatabase;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Log4j2
public class ExceptionHandlerController {

  @ExceptionHandler(RestException.class)
  public @ResponseBody
  String handleException(RestException e) {
    log.error("Ошибка: " + e.getMessage(), e);
    return "Ошибка: " + e.getMessage();
  }
}
