package Adopet.project.Adopet.User.Exceptions;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String message)
    {
        super(message);
    }
    
}
