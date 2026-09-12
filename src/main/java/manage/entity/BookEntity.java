package manage.entity;

import java.time.LocalDateTime;

public class BookEntity {
	private Integer id;
	private String title;
	private String author;
	private BookStatus status;
	private LocalDateTime createdAt;
	
	public BookEntity(int newId, String title, String author, BookStatus status, LocalDateTime createdAt) {
	}

	public Integer getId() {
		return id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public BookStatus getStatus() {
		return status;
	}
	
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
};
