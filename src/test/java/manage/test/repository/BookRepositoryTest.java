package manage.test.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.transaction.annotation.Transactional;

import manage.entity.BookEntity;
import manage.entity.BookStatus;
import manage.repository.BookRepository;

@SpringBootTest(properties = { "spring.datasource.url=jdbc:h2:mem:booktest;DB_CLOSE_DELAY=-1",
		"spring.datasource.username=sa", "spring.datasource.password=" })
@Transactional
class BookRepositoryTest {

	@Autowired
	BookRepository bookRepository;

	@Autowired
	JdbcClient jdbcClient;

	@BeforeEach // DBへのテストデータ登録
	void setUp() {

		// 自動採番のため
		jdbcClient.sql("""
				TRUNCATE TABLE book RESTART IDENTITY
				""").update();

		jdbcClient.sql("""
				INSERT INTO book(title, author, status, createdAt)
				VALUES
				('星降り山荘の殺人', '倉知淳', 'READING', CURRENT_TIMESTAMP),
				('白夜行', '綾辻行人', 'READING', CURRENT_TIMESTAMP),
				('十角館の殺人', '東野圭吾', 'READ', CURRENT_TIMESTAMP),
				('慟哭', '貫井徳郎', 'UNREAD', CURRENT_TIMESTAMP),
				('仮面山荘殺人事件', '東野圭吾', 'READ', CURRENT_TIMESTAMP),
				('葉桜の季節に君を想うということ', '歌野晶午', 'READ', CURRENT_TIMESTAMP)
				""").update();
	}

//全件取得のテストもする
	@Nested
	@DisplayName("findAll()")
	class FindAllTest {
		@Test
		@DisplayName("本一覧すべてを取得する")
		void allBookExists() {
			List<BookEntity> actual = bookRepository.findAll();
			Set<String> actualTitles = actual.stream().map(BookEntity::getTitle).collect(Collectors.toSet());
			assertAll(() -> assertEquals(6, actual.size()),
					() -> assertEquals(Set.of("白夜行", "慟哭", "葉桜の季節に君を想うということ", "星降り山荘の殺人", "十角館の殺人", "仮面山荘殺人事件"),
							actualTitles));
		}
	}

	@Nested
	@DisplayName("searchById()")

	class SearchByIdTest {
		@Test
		@DisplayName("該当するIDがあれば対象の本を返す")
		void exists() {
			BookEntity actual = bookRepository.searchById(1).orElseThrow();
			assertAll(() -> assertEquals(1, actual.getId()), () -> assertEquals("星降り山荘の殺人", actual.getTitle()),
					() -> assertEquals("倉知淳", actual.getAuthor()),
					() -> assertEquals(BookStatus.READING, actual.getStatus()));
		}

		@Test
		@DisplayName("該当するIDがなければ空を返す")
		void notExists() {
			Optional<BookEntity> actual = bookRepository.searchById(999);
			assertTrue(actual.isEmpty());
		}
	}

	@Nested
	@DisplayName("searchByKeyword")
	class SearchByKeywordTest {
		@Test
		@DisplayName("タイトルに部分一致するものがある本を複数取得する")
		void titleExists() {
			List<BookEntity> actual = bookRepository.searchByKeyword("殺人");
			Set<String> actualTitles = actual.stream().map(BookEntity::getTitle).collect(Collectors.toSet());
			assertAll(() -> assertEquals(3, actual.size()),
					() -> assertEquals(Set.of("星降り山荘の殺人", "十角館の殺人", "仮面山荘殺人事件"), actualTitles));
		}

		@Test
		@DisplayName("著者に部分一致するものがある本を複数取得する")
		void authorExists() {
			List<BookEntity> actual = bookRepository.searchByKeyword("野");
			Set<String> actualAuthors = actual.stream().map(BookEntity::getAuthor).collect(Collectors.toSet());
			assertAll(() -> assertEquals(3, actual.size()), () -> assertEquals(Set.of("東野圭吾", "歌野晶午"), actualAuthors));

		}

		@Test
		@DisplayName("タイトルと著者どちらも該当ない場合は空のListを返す")
		void keywordNotExists() {
			List<BookEntity> actual = bookRepository.searchByKeyword("一致しないキーワード");
			assertTrue(actual.isEmpty());

		}
	}

	@Nested
	@DisplayName("searchByStatus")
	class SearchByStatusTest {
		@ParameterizedTest
		@DisplayName("ステータス一致する本を複数取得する")
		@CsvSource({ "UNREAD, 1", "READING, 2", "READ, 3" })
		void statusExists(BookStatus status, int expectedSize) {
			List<BookEntity> actual = bookRepository.searchByStatus(status);
			assertAll(() -> assertEquals(expectedSize, actual.size()),
					() -> assertTrue(actual.stream().allMatch(book -> book.getStatus() == status)));

		}
	}

	@Nested
	@DisplayName("updateBook")
	class UpdateBookTest {
		@Test
		@DisplayName("登録済みの本の情報を更新する") // 任意の情報でDB更新後、再取得で確認
		void updateBook() {
			BookEntity book = bookRepository.searchById(1).orElseThrow();
			LocalDateTime orgCreatedAt = book.getCreatedAt();
			book.setTitle("更新後のタイトル");
			book.setAuthor("更新後の著者");
			book.setStatus(BookStatus.READING);
			bookRepository.updateBook(book);
			BookEntity actual = bookRepository.searchById(1).orElseThrow();
			assertAll(() -> assertEquals(1, actual.getId()), () -> assertEquals("更新後のタイトル", actual.getTitle()),
					() -> assertEquals("更新後の著者", actual.getAuthor()),
					() -> assertEquals(BookStatus.READING, actual.getStatus()),
					() -> assertEquals(orgCreatedAt, actual.getCreatedAt()));
		}
	}

	@Nested
	@DisplayName("deleteById")
	class DeleteBookTest {
		@Test
		@DisplayName("登録済みの本を削除する") // 任意の本を削除した後DB全体を取得し全体の数をカウント
		void deleteBook() {
			int actual = bookRepository.deleteById(1);
			Optional<BookEntity> deletedBook = bookRepository.searchById(1);
			assertAll(() -> assertEquals(1, actual), () -> assertTrue(deletedBook.isEmpty()));
		}
	}

	@Nested
	@DisplayName("insert()")
	class InsertBookTest {
		@Test
		@DisplayName("本を新規登録する") // 新規登録したのち、該当する本を検索することで登録されていることを確認する
		void insertBook() {
			// 登録する本の内容
			BookEntity inserted = bookRepository
					.insert(new BookEntity(null, "新規登録本", "新規登録著者", BookStatus.UNREAD, LocalDateTime.now()));
			assertAll(() -> assertEquals("新規登録本", inserted.getTitle()),
					() -> assertEquals("新規登録著者", inserted.getAuthor()),
					() -> assertEquals(BookStatus.UNREAD, inserted.getStatus()));
//新規登録された本が登録した本と中身が一致するか確認する（タイトル、著者、ステータスの要素のみ）
		}
	}
}
