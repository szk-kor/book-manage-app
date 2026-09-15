package manage.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

	@GetMapping("/book/updateBook/{id}")
	public String showUpdateForm(@PathVariable Integer id, Model model) {
		BookEntity book = bookService.searchById(id).orElseThrow();
		model.addAttribute("book", book);
		return "book/updateBook";
	}

	@PostMapping("/book/updateBook/{id}")
	public String updateBookPage(@PathVariable Integer id, BookEntity bookEntity) {

		bookEntity.setId(id);
		bookService.updateBook(bookEntity);

		return "redirect:/";
	}

	@PostMapping("/book/deleteBook/{id}")
	public String deleteBook(@PathVariable Integer id) {
		bookService.deleteById(id);
		return "redirect:/";
	}
}