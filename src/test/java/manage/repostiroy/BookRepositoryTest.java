package manage.repostiroy;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.transaction.annotation.Transactional;

import manage.entity.BookEntity;
import manage.entity.BookStatus;
import manage.repository.BookRepository;

@SpringBootTest
@Transactional
public class BookRepositoryTest {

	@Autowired
	BookRepository bookRepository;

	@Autowired
	JdbcClient jdbcClient;

	@BeforeEach // DBへのテストデータ登録
	void dbSetUp() {
		jdbcClient.sql("""
				INSERT INTO book(id, title, author, status, createdAt)
				VALUES(1, '本', '著者', 'UNREAD', CURRENT_TIMESTAMP),
				(2, 'book', 'author', 'READING', CURRENT_TIMESTAMP),
				(3, 'buch', 'autor', 'READ', CURRENT_TIMESTAMP),
				(4, '本1', 'author1', 'UNREAD', CURRENT_TIMESTAMP),
				(5, 'book1', 'autor1', 'READING', CURRENT_TIMESTAMP)
				""").update();
	}

//全件取得のテストもする
	@Nested
	@DisplayName("seaechById()")

	class SearchByIdTest {

		@Test
		@DisplayName("該当するIDがあれば対象の本を返す")
		void exists() {
			BookEntity actual = bookRepository.searchById(1).orElseThrow();
			assertAll(() -> assertEquals(1, actual.getId()), () -> assertEquals("本", actual.getTitle()),
					() -> assertEquals("著者", actual.getAuthor()),
					() -> assertEquals(BookStatus.UNREAD, actual.getStatus()));
		}

		@Test
		@DisplayName("該当するIDがなければ空を返す")
		void notExists() {
			Optional<BookEntity> actual = bookRepository.searchById(999);
			assertTrue(actual.isEmpty());
		}
	}

	@Nested
	@DisplayName("searchByKeyWord")
	class SearchByKeyword {
		@Test
		@DisplayName("タイトルに一致するものがある本を複数取得する")
		void titleExists() {
			List<BookEntity> actual = bookRepository.searchByKeyword("本");
			Set<String> actualTitles = actual.stream().map(BookEntity::getTitle).collect(Collectors.toSet());
			assertAll(() -> assertEquals(2, actual.size()), () -> assertEquals(Set.of("本", "本1"), actualTitles));
		}

		@Test
		@DisplayName("著者に一致するものがある本を複数取得する")
		void authorExists() {
			List<BookEntity> actual = bookRepository.searchByKeyword("author");
			Set<String> actualAuthors = actual.stream().map(BookEntity::getAuthor).collect(Collectors.toSet());
			assertAll(() -> assertEquals(2, actual.size()),
					() -> assertEquals(Set.of("author", "author1"), actualAuthors));

		}

		@Test
		@DisplayName("タイトルと著者どちらも該当ない場合は空のListを返す")
		void KeywordNotExists() {
			List<BookEntity> actual = bookRepository.searchByKeyword("一致しないキーワード");
			assertTrue(actual.isEmpty());

		}
	}
	/*
	 * @Nested
	 * 
	 * @DisplayName("searchByStatus") class SearchByStatus {
	 * 
	 * @ParameterizedTest
	 * 
	 * @DisplayName("ステータス一致する本を複数取得する")
	 * 
	 * @CsvSource({ "UNREAD, 2", "READING, 2", "READ, 1" })
	 * 
	 * void statusExists(BookStatus status, int expectedSize) { List<BookEntity>
	 * actual = bookRepository.searchByStatus(status);
	 * 
	 * assertAll(() -> assertEquals(expectedSize, actual.size()), () ->
	 * assertTrue(actual.stream().allMatch(book -> book.getStatus() == status)));
	 * 
	 * } }
	 */
}