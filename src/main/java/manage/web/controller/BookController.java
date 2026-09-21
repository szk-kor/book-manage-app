package manage.web.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import manage.persistence.entity.BookEntity;
import manage.persistence.entity.BookStatus;
import manage.service.BookService;
import manage.web.exception.BookNotFoundException;
import manage.web.form.BookForm;

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

	// 本の追加画面の取得
	@GetMapping("/book/addBook")
	public String addBookPage(Model model) {
		model.addAttribute("bookForm", new BookForm("", "", null));
		return "book/addBook";
	}

	// 追加
	@PostMapping("/book/addBook")
	public String addBook(@Validated @ModelAttribute("bookForm") BookForm bookForm, BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			return "book/addBook";
		}

		BookEntity bookEntity = new BookEntity();
		bookEntity.setTitle(bookForm.title());
		bookEntity.setAuthor(bookForm.author());
		bookEntity.setStatus(bookForm.status());

		bookService.insert(bookEntity);

		return "redirect:/";
	}

	// 本の編集画面の取得
	@GetMapping("/book/updateBook/{id}")
	public String showUpdateForm(@PathVariable Integer id, Model model) {
		BookEntity book = bookService.searchById(id).orElseThrow(() -> new BookNotFoundException(id));
		BookForm bookForm = new BookForm(book.getTitle(), book.getAuthor(), book.getStatus());
		model.addAttribute("bookForm", bookForm);
		model.addAttribute("id", id);
		return "book/updateBook";
	}

	// 編集
	@PostMapping("/book/updateBook/{id}")
	public String updateBookPage(@PathVariable Integer id, @Validated @ModelAttribute("bookForm") BookForm bookForm,
			BindingResult bindingResult, Model model) {
		if (bookService.searchById(id).isEmpty()) {
		    throw new BookNotFoundException(id);
		}
		if (bindingResult.hasErrors()) {
			model.addAttribute("id", id);
			return "book/updateBook";
		}
		BookEntity bookEntity = new BookEntity();
		bookEntity.setId(id);
		bookEntity.setTitle(bookForm.title());
		bookEntity.setAuthor(bookForm.author());
		bookEntity.setStatus(bookForm.status());
		bookService.updateBook(bookEntity);
		return "redirect:/";
	}

	// 削除
	@PostMapping("/book/deleteBook/{id}")
	public String deleteBook(@PathVariable Integer id) {
		if (bookService.searchById(id).isEmpty()) {
		    throw new BookNotFoundException(id);
		}
		bookService.deleteById(id);
		return "redirect:/";
	}

	// ステータス検索
	@GetMapping("/book/searchByStatus")
	public String searchByStatus(@RequestParam(required = false) BookStatus status, Model model) {
		List<BookEntity> bookList;
		if (status == null) {
			bookList = bookService.findAll();
		} else {
			bookList = bookService.searchByStatus(status);
		}
		model.addAttribute("bookList", bookList);
		return "book/index";
	}

	// キーワード検索
	@GetMapping("/book/searchByKeyword")
	public String searchByKeyword(@RequestParam(required = false) String keyword, Model model) {
		List<BookEntity> bookList;
		if (keyword != null && !keyword.isBlank()) {
			bookList = bookService.searchByKeyword(keyword);
		} else {
			bookList = bookService.findAll();
		}
		model.addAttribute("bookList", bookList);
		return "book/index";
	}

}