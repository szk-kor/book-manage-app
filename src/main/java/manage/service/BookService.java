package manage.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import manage.entity.BookEntity;
import manage.entity.BookStatus;
import manage.repository.BookRepository;

@Service
public class BookService {
	private final BookRepository bookRepository;

	public BookService(BookRepository bookRepository) {
		this.bookRepository = bookRepository;
	}

	@Transactional(readOnly = true)
	public List<BookEntity> findAll() {
		return bookRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Optional<BookEntity> searchById(Integer id) {
		Optional<BookEntity> bookOptional = bookRepository.searchById(id);
		return bookOptional;
	}

	@Transactional(readOnly = true)
	public List<BookEntity> searchByTitle(String titleKeyword) {
		List<BookEntity> bookList = bookRepository.searchByTitle(titleKeyword);
		return bookList;
	}

	@Transactional(readOnly = true)
	public List<BookEntity> searchByAuthor(String authorKeyword) {
		List<BookEntity> bookList = bookRepository.searchByAuthor(authorKeyword);
		return bookList;
	}

	@Transactional(readOnly = true)
	public List<BookEntity> serachByStatus(BookStatus status) {
		List<BookEntity> bookList = bookRepository.searchByStatus(status);
		return bookList;
	}

	@Transactional(readOnly = true)
	public int countById(Integer id) {
		int count = bookRepository.countById(id);
		return count;
	}

	@Transactional(readOnly = false)
	public int updateBook(BookEntity book) {
		int rows = bookRepository.updateBook(book);
		return rows;
	}

	@Transactional(readOnly = false)
	public int deleteById(Integer id) {
		int rows = bookRepository.deleteById(id);
		return rows;
	}

	@Transactional(readOnly = false)
	public BookEntity insert(BookEntity book) {
		book.setCreatedAt(LocalDateTime.now());
		BookEntity newBook = bookRepository.insert(book);
		return newBook;
	}
}
