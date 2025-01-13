package ord.example.LegalManagementSystem.exceptions;

public class LawsuitNotFoundException extends RuntimeException {
    public LawsuitNotFoundException(String message) {
        super(message);
    }
}