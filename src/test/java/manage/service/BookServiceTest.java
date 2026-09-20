package manage.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import manage.persistence.entity.BookEntity;
import manage.persistence.entity.BookStatus;
import manage.persistence.repository.BookRepository;

public class BookServiceTest {
	BookService bookService;
	BookRepository bookRepository;
	
	@BeforeEach
	void setUp() {
		//BookRepositoryのモックを作成
		bookRepository=mock(BookRepository.class);
		//BookRepositoryのモックをBookServiceに代入
		bookService=new BookService(bookRepository);
	}
	
	@Test
	@DisplayName("本の新規登録する際にcreatedAtが生成・設定される")
		void insertBook(){
		BookEntity book = new BookEntity();
		//タイトル、著者、ステータスが入った本がServiceに渡されたことにしてる
		book.setTitle("星降り山荘の殺人");
		book.setAuthor("倉知淳");
		book.setStatus(BookStatus.UNREAD);
		//返る挙動の設定
		doReturn(book)
			.when(bookRepository)
			.insert(any(BookEntity.class));
		bookService.insert(book);
		assertNotNull(book.getCreatedAt()); 
	}
}
