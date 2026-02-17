package lugus.service.user;

public interface PasswordRecoveryNotifier {

	void sendRecoveryToken(String login, String email, String token, long expiresInSeconds);

}