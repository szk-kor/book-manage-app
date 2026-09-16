package manage.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import manage.entity.BookStatus;

public record BookForm(
		@NotBlank String title, 
		@NotBlank String author,
		@NotNull BookStatus status
		) {
}