package com.amalto.groovy;

import com.amalto.groovy.interception.InterceptorRegistry;
import groovy.lang.Closure;
import groovy.lang.GroovyObjectSupport;
import groovy.lang.GroovySystem;
import org.apache.log4j.Logger;

public class RuntimeScriptInterceptor {

    private final static Logger LOG = Logger.getLogger( RuntimeScriptInterceptor.class );

    public static void invokeMethod( Object target, String method, String sourceName, int lineNumber, Object[] args ) throws Throwable {

        Object theTarget = target;

        // If it's a closure then find the delegate for future class name checks
        while ( theTarget instanceof Closure ) {

            Closure closure = ( Closure ) theTarget;
            theTarget = closure.getDelegate();
        }

        Class theClazz = theTarget.getClass();

        String className = theClazz.getName();
        if ( theTarget instanceof Class ) {
            className = ( ( Class ) theTarget ).getName();
        }

        if ( LOG.isTraceEnabled() ) {
            LOG.trace( "** Groovy Runtime ** (" + sourceName + ") Method: " + method + " Class: " + className + " Line: " + lineNumber );
        }

        if( ! InterceptorRegistry.getInstance().isValid( sourceName, lineNumber, className, method ) ){
            return;
        }

        if ( target instanceof Class ) {

            GroovySystem.getMetaClassRegistry().getMetaClass( ( Class ) target ).invokeStaticMethod( target, method, args );

        } else {

            // Groovy object such as a builder?
            if ( target instanceof GroovyObjectSupport ) {

                GroovyObjectSupport gos = ( GroovyObjectSupport ) target;
                gos.invokeMethod( method, args );

            } else {

                GroovySystem.getMetaClassRegistry().getMetaClass( target.getClass() ).invokeMethod( target, method, args );
            }
        }
    }
}

