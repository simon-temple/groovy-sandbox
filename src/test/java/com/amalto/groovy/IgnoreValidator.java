package com.amalto.groovy;

import com.amalto.groovy.interception.InterceptorValidator;
import com.amalto.groovy.interception.SandboxSecurityException;
import org.apache.log4j.Logger;

public class IgnoreValidator implements InterceptorValidator {

    private final static Logger LOG = Logger.getLogger( IgnoreValidator.class );

    @Override
    public boolean canInvoke( String sourceName, int lineNumber, String className, String methodName, boolean isGroovyObject ) throws SandboxSecurityException {

        if ( className.equals( "java.lang.ProcessBuilder" ) ) {
            LOG.warn( "Use of the ProcessBuilder class not allowed at line: " + lineNumber + ". Method Ignored!" );
            return false;
        }

        if ( className.equals( "java.lang.ClassLoader" ) ) {
            LOG.warn( "Use of the Classloader class not allowed at line: " + lineNumber + ". Method Ignored!" );
            return false;
        }

        if ( className.equals( "java.lang.Shutdown" ) ) {
            LOG.warn( "Use of the Shutdown class not allowed at line: " + lineNumber + ". Method Ignored!" );
            return false;
        }

        if ( className.equals( "java.lang.Runtime" ) ) {
            LOG.warn( "Use of the Runtime class not allowed at line: " + lineNumber + ". Method Ignored!" );
            return false;
        }

        if ( className.equals( "java.lang.System" ) && methodName.equals( "exit" ) ) {
            LOG.warn( "System.exit() not allowed at line: " + lineNumber + ". Method Ignored!" );
            return false;
        }

        if ( className.equals( "groovy.util.Eval" ) ) {
            LOG.warn( "Evaluation of sub scripts not allowed at line: " + lineNumber + ". Method Ignored!" );
            return false;
        }

        if ( className.equals( "java.lang.reflect.Method" ) && methodName.equals( "invoke" ) ) {
            LOG.warn( "Invocation via reflection not allowed at line: " + lineNumber + ". Method Ignored!" );
            return false;
        }

        if ( className.equals( "groovy.lang.GroovyShell" ) && methodName.equals( "evaluate" ) ) {
            LOG.warn( "Evaluation of sub scripts not allowed at line: " + lineNumber + ". Method Ignored!" );
            return false;
        }

        if ( methodName.equals( "evaluate" ) && ( isGroovyObject || className.equals( "groovy.lang.GroovyShell" ) )) {
            LOG.warn( "Any evaluate() method not allowed at line: " + lineNumber + ". Method Ignored!" );
            return false;
        }

        return true;
    }
}
