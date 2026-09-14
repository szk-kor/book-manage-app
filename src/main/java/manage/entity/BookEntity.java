package manage.entity;

import java.time.LocalDateTime;

public class BookEntity {

	private Integer id;
	private String title;
	private String author;
	private BookStatus status;
	private LocalDateTime createdAt;

	public BookEntity() {
	}

	public BookEntity(Integer id, String title, String author, BookStatus status, LocalDateTime createdAt) {

		this.id = id;
		this.title = title;
		this.author = author;
		this.status = status;
		this.createdAt = createdAt;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public BookStatus getStatus() {
		return status;
	}

	public void setStatus(BookStatus status) {
		this.status = status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}