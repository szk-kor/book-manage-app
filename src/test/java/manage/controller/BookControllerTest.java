package manage.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import manage.persistence.entity.BookEntity;
import manage.persistence.entity.BookStatus;
import manage.service.BookService;

@SpringBootTest
@AutoConfigureMockMvc
class BookControllerTest {
	@MockitoBean
	BookService bookService;

	@Autowired
	MockMvc mvc;

	@Nested
	@DisplayName("書籍一覧")
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
	@DisplayName("書籍追加")
	class AddBookMainTest {
		@Test
		@DisplayName("追加画面にアクセスすると200 OK")
		void success() throws Exception {
			mvc.perform(get("/book/addBook"))
					.andExpect(status().isOk())
					.andExpect(view().name("/book/addBook"));
		}
	}

	@Nested
	@DisplayName("新規追加")
	class AddBookTest {
		@Test
		@DisplayName("正しい入力で一覧にリダイレクトする")
		void redirectToIndex() throws Exception{
			mvc.perform(post("/book/addBook")
					.param("title", "慟哭")
					.param("author","貫井徳郎")
					.param("status","UNREAD"))
					.andExpect(status().is3xxRedirection())
					.andExpect(redirectedUrl("/"));
		}
	
		@Test
		@DisplayName("空で入力すると、追加画面に戻る")
		void redirectToAddBook() throws Exception{
			mvc.perform(post("book/addBook")
					.param("title","")
					.param("author", "")
					.param("status", ""))
					.andExpect(status().isOk())
					.andExpect(view().name("/book/addBook"));
		}
	}
	@Nested
	@DisplayName("編集画面")
		class UpdateBookTest{
		@Test
		@DisplayName("編集画面にアクセスすると200 OK")
		void redirectToIndex() throws Exception{
			doReturn(Optional.of(
			        new BookEntity(
			            1,
			            "medium",
			            "相沢",
			            BookStatus.UNREAD,
			            LocalDateTime.now()
			        )))
			    .when(bookService)
			    .searchById(anyInt());
				mvc.perform(get("/book/{id}"))
				.andExpect(status().isOk())
				.andExpect(view().name("book/updateBook"));
			}
		}
}	


