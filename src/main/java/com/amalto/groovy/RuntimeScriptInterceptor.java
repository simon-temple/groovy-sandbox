package com.amalto.groovy;

import com.amalto.groovy.interception.InterceptorRegistry;
import groovy.lang.*;
import org.apache.log4j.Logger;
import org.codehaus.groovy.runtime.HandleMetaClass;
import org.codehaus.groovy.runtime.MethodClosure;

public class RuntimeScriptInterceptor {

    private final static Logger LOG = Logger.getLogger( RuntimeScriptInterceptor.class );

    public static void invokeMethod( final Object target, final String method, final String sourceName, final int lineNumber, final Object[] args ) throws Throwable {

        Object theTarget = target;
        String theMethod = method;

        // If it's a closure then find the delegate for future class name checks
        while ( theTarget instanceof Closure ) {

            Closure closure = ( Closure ) theTarget;

            if ( theTarget instanceof MethodClosure ) {
                // Method closure so grab the target method name
                MethodClosure mclosure = ( MethodClosure ) theTarget;
                theMethod = mclosure.getMethod();
            }

            theTarget = closure.getDelegate();
        }

        Class theClazz = theTarget.getClass();

        String className = theClazz.getName();
        if ( theTarget instanceof Class ) {
            className = ( ( Class ) theTarget ).getName();
        }

        if ( LOG.isTraceEnabled() ) {
            LOG.trace( "** Groovy Runtime ** (" + sourceName + ") Method: " + theMethod + " Class: " + className + " Line: " + lineNumber );
        }

        if ( target instanceof Class ) {

            if ( !InterceptorRegistry.getInstance().isValid( sourceName, lineNumber, className, theMethod, false ) ) {
                return;
            }

            GroovySystem.getMetaClassRegistry().getMetaClass( ( Class ) target ).invokeStaticMethod( target, theMethod, args );

        } else {

            if ( theTarget instanceof GroovyObjectSupport ) {
                // Groovy object such as a builder?

                if ( !InterceptorRegistry.getInstance().isValid( sourceName, lineNumber, className, theMethod, true ) ) {
                    return;
                }

                GroovyObjectSupport gos = ( GroovyObjectSupport ) target;
                gos.invokeMethod( theMethod, args );

            } else if ( theTarget instanceof HandleMetaClass ) {
                // Meta Class support

                HandleMetaClass xmc = ( HandleMetaClass ) target;
                Object[] nargs = new Object[ args.length - 1 ];
                System.arraycopy( args, 1, nargs, 0, args.length - 1 );
                MetaMethod mm = xmc.getMetaMethod( ( String ) args[ 0 ], nargs );
                String cn = ( ( HandleMetaClass ) target ).getTheClass().getName();

                if ( !InterceptorRegistry.getInstance().isValid( sourceName, lineNumber, cn, mm.getName(), false ) ) {
                    return;
                }

                mm.invoke( target, nargs );

            } else {

                if ( !InterceptorRegistry.getInstance().isValid( sourceName, lineNumber, className, theMethod, false ) ) {
                    return;
                }

                GroovySystem.getMetaClassRegistry().getMetaClass( target.getClass() ).invokeMethod( target, theMethod, args );
            }
        }
    }
}

