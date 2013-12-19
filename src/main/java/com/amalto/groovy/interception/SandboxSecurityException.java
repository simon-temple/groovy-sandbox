package com.amalto.groovy.interception;

public class SandboxSecurityException extends Exception {

    private static final long serialVersionUID = 1L;

    public SandboxSecurityException() {
        super();
    }

    public SandboxSecurityException( String message ) {
        super( message );
    }

    public SandboxSecurityException( String message, Throwable cause ) {
        super( message, cause );
    }

    public SandboxSecurityException( Throwable cause ) {
        super( cause );
    }
}
