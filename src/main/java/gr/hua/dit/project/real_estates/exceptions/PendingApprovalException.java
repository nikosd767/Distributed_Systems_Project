package gr.hua.dit.project.real_estates.exceptions;

import org.springframework.security.core.AuthenticationException;

// Custom exception for handling authentication attempts by users with pending approval status
public class PendingApprovalException extends AuthenticationException {
    public PendingApprovalException(String msg) {
        super(msg);
    }
}
