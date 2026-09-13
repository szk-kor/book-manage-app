package manage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import manage.service.BookService;



@Controller

public class BookController{
	private final BookService bookService;
	
	public BookController(BookService bookService) {
		this.bookService = bookService;
	}

@GetMapping("/")
public String index() {
	return "book/index";
}

}