package manage.repository;
import java.awt.print.Book;
import java.util.List; //複数件取得するとき用。
import java.util.Optional;//ID検索してその本が存在しないかもしれないとき用

import org.springframework.jdbc.core.DataClassRowMapper;//DBからとった1行をBookに変換する
import org.springframework.jdbc.core.simple.JdbcClient;//実際にSQLをDBに投げる担当
import org.springframework.stereotype.Repository;//この

@Repository

public class BookRepository{
	private final JdbcClient jdbcClient;
	public BookRepository(JdbcClient jdbcClient) {
		this.jdbcClient = jdbcClient;
	}
	
	public Optional<Book> searchById(Integer id){
		Optional<Book> bookOptional = jdbcClient.sql("""
				SELECT id, title,author, status, createdAt
				FROM Book WHERE id = :id
				""").param("id",id)
				.query(new DataClassRowMapper<>(Book.class))
				.optional();
		return bookOptional;
	}
	
	public List<Book> searchByTitle(String titleKeyword){
		List<Book> bookList=jdbcClient.sql("""
				SELECT id, title, author, status, createdAt
				FROM Book WHERE title LIKE :title ORDER BY id
				""")
				.param("title","%" + titleKeyword + "%")
				.query(new DataClassRowMapper<>(Book.class)).list();
		return bookList;
	}
	
	public List<Book> searchByAuthor(String authorKeyword){
		List<Book> bookList=jdbcClient.sql("""
				SELECT id, title, author, status, createdAt
				FROM Book WHERE author LIKE :author ORDER BY id
				""")
				.param("author","%"+authorKeyword+"%")
				.query(new DataClassRowMapper<>(Book.class)).list();
		return bookList;
	}
}