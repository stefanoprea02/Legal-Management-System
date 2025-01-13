package ord.example.LegalManagementSystem.exceptions;

public class LawyerNotFoundException extends RuntimeException {
    public LawyerNotFoundException(String message) {
        super(message);
    }
}
