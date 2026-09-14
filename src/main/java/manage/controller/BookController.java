package manage.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import manage.entity.BookEntity;
import manage.service.BookService;

@Controller
public class BookController {

	private final BookService bookService;

	public BookController(BookService bookService) {
		this.bookService = bookService;
	}

	@GetMapping("/")
	public String index(Model model) {

		List<BookEntity> bookList = bookService.findAll();

		model.addAttribute("bookList", bookList);

		return "book/index";
	}

	@GetMapping("/book/addBook")
	public String addBookPage() {
		return "book/addBook";
	}

	@PostMapping("/book/addBook")
	public String addBook(BookEntity bookEntity) {

		bookService.insert(bookEntity);

		return "redirect:/";
	}
}