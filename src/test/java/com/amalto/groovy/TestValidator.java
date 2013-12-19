package com.amalto.groovy;

import com.amalto.groovy.interception.InterceptorValidator;
import com.amalto.groovy.interception.SandboxSecurityException;

public class TestValidator implements InterceptorValidator {

    @Override
    public boolean canInvoke( String sourceName, int lineNumber, String className, String methodName ) throws SandboxSecurityException {

        if ( className.equals( "java.lang.ProcessBuilder" ) ) {
            throw new SandboxSecurityException( "Use of the ProcessBuilder class not allowed at line: " + lineNumber );
        }

        if ( className.equals( "java.lang.ClassLoader" ) ) {
            throw new SandboxSecurityException( "Use of the Classloader class not allowed at line: " + lineNumber );
        }

        if ( className.equals( "java.lang.Shutdown" ) ) {
            throw new SandboxSecurityException( "Use of the Shutdown class not allowed at line: " + lineNumber );
        }

        if ( className.equals( "java.lang.Runtime" ) ) {
            throw new SandboxSecurityException( "Use of the Runtime class not allowed at line: " + lineNumber );
        }

        if ( className.equals( "java.lang.System" ) && methodName.equals( "exit" ) ) {
            throw new SandboxSecurityException( "System.exit() not allowed at line: " + lineNumber );
        }

        if ( className.equals( "groovy.util.Eval" ) ) {
            throw new SandboxSecurityException( "Evaluation of sub scripts not allowed at line: " + lineNumber );
        }

        if ( className.equals( "java.lang.reflect.Method" ) && methodName.equals( "invoke" ) ) {
            throw new SandboxSecurityException( "Invocation via reflection not allowed at line: " + lineNumber );
        }

        if ( className.equals( "groovy.lang.GroovyShell" )  && methodName.equals( "evaluate" ) ) {
            throw new SandboxSecurityException( "Evaluation of sub scripts not allowed at line: " + lineNumber );
        }

        return true;
    }
}
