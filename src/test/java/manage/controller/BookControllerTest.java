package manage.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import manage.persistence.entity.BookEntity;
import manage.service.BookService;

@SpringBootTest
@AutoConfigureMockMvc
class BookControllerTest {
	@MockitoBean
	BookService bookService;

	@Autowired
	MockMvc mvc;

	@Nested
	@DisplayName("書籍一覧") //✓
	class IndexTest {
		@Test
		@DisplayName("一覧画面にアクセスすると200 OK")
		void success() throws Exception {
			mvc.perform(get("/"))
					.andExpect(status().isOk())
					.andExpect(view().name("book/index"));
		}
	}

	@Nested
	@DisplayName("書籍追加") //✓
	class AddBookMainTest {
		@Test
		@DisplayName("追加画面にアクセスすると200 OK")
		void success() throws Exception {
			mvc.perform(get("/book/addBook"))
					.andExpect(status().isOk())
					.andExpect(view().name("book/addBook"));
		}
	}

	@Nested
	@DisplayName("新規追加") //✓
	class AddBookTest {
		@Test
		@DisplayName("正しい入力で一覧にリダイレクトする")
		void redirectToIndex() throws Exception {
			mvc.perform(post("/book/addBook")
					.param("title", "慟哭")
					.param("author", "貫井徳郎")
					.param("status", "UNREAD"))
					.andExpect(status().is3xxRedirection())
					.andExpect(redirectedUrl("/"));
		}

		@Test
		@DisplayName("空で入力すると、追加画面に戻る")
		void returnToAddBook() throws Exception {
			mvc.perform(post("/book/addBook")
					.param("title", "")
					.param("author", "")
					.param("status", ""))
					.andExpect(status().isOk())
					.andExpect(view().name("book/addBook"));
		}
	}

	@Nested
	@DisplayName("編集画面") //✓
	class UpdateBookMainTest {
		BookEntity book;

		@BeforeEach
		void setUp() {
			book = new BookEntity();
		}

		@Test
		@DisplayName("編集画面にアクセスすると200 OK")
		void redirectToIndex() throws Exception {
			doReturn(Optional.of(book)).when(bookService)
					.searchById(anyInt());
			mvc.perform(get("/book/updateBook/1"))
					.andExpect(status().isOk())
					.andExpect(view().name("book/updateBook"));
		}

		@Test
		@DisplayName("存在しないIDを指定して修正画面にアクセスするとエラー画面に遷移する")
		void notExistMain() throws Exception {
			doReturn(Optional.empty()).when(bookService).searchById(anyInt());
			mvc.perform(get("/book/updateBook/999"))
					.andExpect(status().isOk())
					.andExpect(view().name("error"));
		}
	}

	@Nested
	@DisplayName("編集の実行") //✓
	class UpdateBookTest {
		BookEntity book;

		@BeforeEach
		void setUp() {
			book = new BookEntity();
		}

		@Test
		@DisplayName("正しい情報を入力すると、書籍一覧画面にリダイレクト")
		void redirectToIndex() throws Exception {
			doReturn(Optional.of(book))
					.when(bookService).searchById(anyInt());
			mvc.perform(post("/book/updateBook/1")
					.param("title", "仮面病棟")
					.param("author", "知念実希人")
					.param("status", "READING"))
					.andExpect(status().is3xxRedirection())
					.andExpect(redirectedUrl("/"));
		}

		@Test
		@DisplayName("空で入力すると、編集画面に戻る")
		void empty() throws Exception {
			doReturn(Optional.of(book))
					.when(bookService).searchById(anyInt());
			mvc.perform(post("/book/updateBook/1")
					.param("title", "")
					.param("author", "")
					.param("status", ""))
					.andExpect(status().isOk())
					.andExpect(view().name("book/updateBook"))
					.andExpect(model().attribute("id", 1));
		}

		@Test
		@DisplayName("存在しない本を指定して修正を行うとエラー画面に遷移する")
		void notExistUpdateBook() throws Exception {

			doReturn(Optional.empty())
					.when(bookService).searchById(anyInt());

			mvc.perform(post("/book/updateBook/999")
					.param("title", "向日葵の咲かない夏")
					.param("author", "道尾秀介")
					.param("status", "READ"))
					.andExpect(status().isOk())
					.andExpect(view().name("error"));
		}
	}

	@Nested
	@DisplayName("削除") //✓
	class DeleteBook {
		BookEntity book;

		@BeforeEach
		void setUp() {
			book = new BookEntity();
		}

		@Test
		@DisplayName("削除すると書籍一覧にリダイレクトする")
		void success() throws Exception {
			doReturn(Optional.of(book)).when(bookService).searchById(anyInt());
			mvc.perform(post("/book/deleteBook/1"))
					.andExpect(status().is3xxRedirection())
					.andExpect(redirectedUrl("/"));
		}

		@Test
		@DisplayName("存在しないIDを指定して削除すると、エラー画面に遷移する")
		void notExistDeleteBook() throws Exception {
			doReturn(Optional.empty()).when(bookService).searchById(anyInt());
			mvc.perform(post("/book/deleteBook/999"))
					.andExpect(status().isOk())
					.andExpect(view().name("error"));
		}
	}

	@Nested
	@DisplayName("ステータス検索")
	class SearchByStatus {

		@Test
		@DisplayName("「読書中」を指定すると200 OK")
		void success() throws Exception {
			mvc.perform(get("/book/searchByStatus")
					.queryParam("status", "READING"))
					.andExpect(status().isOk())
					.andExpect(view().name("book/index"));

		}

		@Test
		@DisplayName("ステータスを指定しない場合、全件を表示する")
		void noParam() throws Exception {
			mvc.perform(get("/book/searchByStatus"))
					.andExpect(status().isOk())
					.andExpect(view().name("book/index"));
		}

		@Test
		@DisplayName("存在しないステータスを指定すると400 Bad Request")
		//Controllerに入る前に弾かれるからerrorを表示するのは違う気がする
		void notExistStatus() throws Exception {
			mvc.perform(get("/book/searchByStatus")
					.queryParam("status", "FINISHED"))
					.andExpect(status().isBadRequest());

		}
	}

	@Nested
	@DisplayName("キーワード検索")
	class SearchByKeyword {
		@Test
		@DisplayName("「野」を指定すると検索結果を一覧画面に表示する")
		void success() throws Exception {
			mvc.perform(get("/book/searchByKeyword")
					.queryParam("keyword", "野"))
					.andExpect(status().isOk())
					.andExpect(view().name("book/index"));
		}

		@Test
		@DisplayName("キーワードを指定しない場合、全件を一覧画面に表示する")
		void noParam() throws Exception {
			mvc.perform(get("/book/searchByKeyword")
					.queryParam("keyword", ""))
					.andExpect(status().isOk())
					.andExpect(view().name("book/index"));
		}

		@Test
		@DisplayName("存在しないキーワードでも一覧画面を表示する")
		void notExistKeyword() throws Exception {
			mvc.perform(get("/book/searchByKeyword")
					.queryParam("keyword", "鈴木"))
					.andExpect(status().isOk())
					.andExpect(view().name("book/index"));
		}
	}
}
