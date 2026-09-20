package manage.web.exception;

public class BookNotFoundException extends RuntimeException{
	private final Integer id;
	public BookNotFoundException(Integer id) {
		super();
		this.id=id;
	}
	public Integer getBookId() {
		return id;
	}
}
