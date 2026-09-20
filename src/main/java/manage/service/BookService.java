package manage.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import manage.persistence.entity.BookEntity;
import manage.persistence.entity.BookStatus;
import manage.persistence.repository.BookRepository;

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
	public List<BookEntity> searchByKeyword(String keyword) {
		List<BookEntity> bookList = bookRepository.searchByKeyword(keyword);
		return bookList;
	}

	@Transactional(readOnly = true)
	public List<BookEntity> searchByStatus(BookStatus status) {
		List<BookEntity> bookList = bookRepository.searchByStatus(status);
		return bookList;
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
