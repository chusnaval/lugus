package lugus.api;

public record ChangePasswordDTO(
	    String currentPassword,
	    String newPassword
	) {}
