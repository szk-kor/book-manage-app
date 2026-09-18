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
public class BookRepository {
	private final JdbcClient jdbcClient;

	public BookRepository(JdbcClient jdbcClient) {
		this.jdbcClient = jdbcClient;
	}

	// 全件表示
	public List<BookEntity> findAll() {
		return jdbcClient.sql("""
				SELECT id, title, author, status, createdAt
				FROM book
				ORDER BY id
				""").query(new DataClassRowMapper<>(BookEntity.class)).list();
	}

	// ID検索
	public Optional<BookEntity> searchById(Integer id) {
		return jdbcClient.sql("""
				SELECT id, title,author, status, createdAt
				FROM Book WHERE id = :id
				ORDER BY id
				""").param("id", id).query(new DataClassRowMapper<>(BookEntity.class)).optional();

	}

	// 著者とタイトル両方検索
	public List<BookEntity> searchByKeyword(String keyword) {
		return jdbcClient.sql("""
				SELECT id, title, author, status, createdAt
				FROM book
				WHERE title LIKE :keyword
				OR author LIKE :keyword
				ORDER BY id
				""").param("keyword", "%" + keyword + "%").query(new DataClassRowMapper<>(BookEntity.class)).list();
	}

	// ステータス検索
	public List<BookEntity> searchByStatus(BookStatus status) {
		return jdbcClient.sql("""
				SELECT id, title, author, status, createdAt
				FROM book WHERE status = :status
				ORDER BY id
				""").param("status", status.name()).query(new DataClassRowMapper<>(BookEntity.class)).list();
	}

	/*
	 * // 指定したIDが何件あるか数えるメソッド public int countById(Integer id) { return
	 * jdbcClient.sql(""" SELECT COUNT(*) FROM book WHERE id = :id """).param("id",
	 * id).query(Integer.class).single(); }
	 */

	// 登録済み本の内容の編集
	public int updateBook(BookEntity book) {
		return jdbcClient.sql("""
				UPDATE book
				SET title = :title, author= :author, status = :status
				WHERE id = :id
				""").param("id", book.getId()).param("title", book.getTitle()).param("author", book.getAuthor())
				.param("status", book.getStatus().name()).update();

	}

	// 削除
	public int deleteById(Integer id) {
		return jdbcClient.sql("""
				DELETE FROM book WHERE id = :id
				""").param("id", id).update();
	}

	// 新規登録
	public BookEntity insert(BookEntity book) {
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcClient.sql("""
					 INSERT INTO book(title, author, status, createdAt)
				VALUES(:title, :author, :status, :createdAt)
				""").param("title", book.getTitle()).param("author", book.getAuthor())
				.param("status", book.getStatus().name()).param("createdAt", book.getCreatedAt())
				.update(keyHolder, "id");
		// DBが作るIDを受け取る箱の用意
		int newId = keyHolder.getKey().intValue();
		// 発行されたIDと一緒に新しい本の情報を返す
		return new BookEntity(newId, book.getTitle(), book.getAuthor(), book.getStatus(), book.getCreatedAt());
	}
}