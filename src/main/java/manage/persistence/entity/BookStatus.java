package manage.persistence.entity;

public enum BookStatus {

	UNREAD("未読"), READING("読書中"), READ("読了");

	private final String label;

	BookStatus(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}