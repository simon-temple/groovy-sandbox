package com.amalto.groovy.interception;

public interface InterceptorValidator {

    /**
     * Called before each method call to validate the method call.  Could be used to implement black or white lists.
     *
     * @param sourceName  The name of the source code
     * @param lineNumber The line number
     * @param className The class name
     * @param methodName  the method name
     * @param isGroovyObject true if the target is a Groovy object
     * @return true to allow invocation else false to skip the execution of this method
     * @throws SandboxSecurityException Use to break the execution of the script
     */
    public boolean canInvoke( final String sourceName, final int lineNumber, final String className, final String methodName, final boolean isGroovyObject ) throws SandboxSecurityException ;
}
