package manage.web.exception.handler;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import manage.web.exception.BookNotFoundException;

@ControllerAdvice
public class WebExceptionHandler {
	@ExceptionHandler
	public String handleBookNotFound(BookNotFoundException e, Model model) {
		Integer id=e.getBookId();
		model.addAttribute("message","ID="+"の本は見つかりません。");
				return "error";
	}
}
