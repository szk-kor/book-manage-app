package manage.repository;
import java.util.List; //複数件取得するとき用。
import java.util.Optional;//ID検索してその本が存在しないかもしれないとき用

import org.springframework.jdbc.core.DataClassRowMapper;//DBからとった1行をBookに変換する
import org.springframework.jdbc.core.simple.JdbcClient;//実際にSQLをDBに投げる担当
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;//この

import manage.entity.BookEntity;
import manage.entity.BookStatus;
@Repository
public class BookRepository{
	private final JdbcClient jdbcClient;
	public BookRepository(JdbcClient jdbcClient) {
		this.jdbcClient = jdbcClient;
	}
	
	//ID検索
	public Optional<BookEntity> searchById(Integer id){
		Optional<BookEntity> bookOptional = jdbcClient.sql("""
				SELECT id, title,author, status, createdAt
				FROM Book WHERE id = :id
				""").param("id",id)
				.query(new DataClassRowMapper<>(BookEntity.class))
				.optional();
		return bookOptional;
	}
	
	
	//タイトル検索
	public List<BookEntity> searchByTitle(String titleKeyword){
		List<BookEntity> bookList=jdbcClient.sql("""
				SELECT id, title, author, status, createdAt
				FROM Book WHERE title LIKE :title ORDER BY id
				""")
				.param("title","%" + titleKeyword + "%")
				.query(new DataClassRowMapper<>(BookEntity.class)).list();
		return bookList;
	}
	
	//著者検索
	public List<BookEntity> searchByAuthor(String authorKeyword){
		List<BookEntity> bookList=jdbcClient.sql("""
				SELECT id, title, author, status, createdAt
				FROM book WHERE author LIKE :author ORDER BY id
				""")
				.param("author","%"+authorKeyword+"%")
				.query(new DataClassRowMapper<>(BookEntity.class)).list();
		return bookList;
	}
	
	//ステータス検索
	public List<BookEntity> searchByStatus(BookStatus status){
		List<BookEntity> bookList = jdbcClient.sql("""
				SELECT id, title, author, status, createdAt
				FROM book WHERE status = :status ORDER BY id
				""")
				.param("status",status)
				.query(new DataClassRowMapper<>(BookEntity.class)).list();
		return bookList;
	}
	
	 //指定したIDが何件あるか数えるメソッド
	public int countById(Integer id) {
		int count = jdbcClient.sql("""
				SELECT COUNT(*) FROM book
				WHERE id = :id
				""")
				.param("id",id)
				.query(Integer.class)
				.single();
				return count;
	}
	
	//登録済み本の内容の編集
	public int update(BookEntity book) { 
		int rows = jdbcClient.sql("""
				UPDATE book
				SET title = :title, author= :author, status = :status
				WHERE id = :id
				""")
				.param("title",book.getTitle())
			.param("author",book.getAuthor())
			.param("status",book.getStatus())
			.update();
		return rows;
	}
	
	//削除
	public int delete(Integer id) { 
		int rows = jdbcClient.sql("""
				DELETE FROM book WHERE id = :id
				""")
				.param("id",id)
				.update();
return rows;
	}
	
	// DBが作るIDを受け取る箱の用意
	public BookEntity insert (BookEntity book) {
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder(); 
		jdbcClient.sql("""
			 INSERT INTO book(title, author, status, createdAt)
		VALUES(:title, :author, :status, :createdAt)
		""")
	.param("title",book.getTitle())
	.param("author", book.getAuthor())
		.param("status",book.getStatus())
		.param("createdAt",book.getCreatedAt())
		.update(keyHolder,"id");
	int newId = keyHolder.getKey().intValue();
	return new BookEntity(newId, book.getTitle(),book.getAuthor(),book.getStatus(),book.getCreatedAt());
	}
}