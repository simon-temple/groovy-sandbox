package com.amalto.groovy.interception;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InterceptorRegistry {

    private final Map<String, InterceptorValidator> registry = new ConcurrentHashMap<String, InterceptorValidator>();

    private static class LazyHolder {
        private static final InterceptorRegistry INSTANCE = new InterceptorRegistry();
    }

    public static InterceptorRegistry getInstance() {
        return LazyHolder.INSTANCE;
    }

    public void register( String sourceMatch, InterceptorValidator validator ) {
        registry.put( sourceMatch, validator );
    }

    public InterceptorValidator unregister( String sourceMatch ) {
        return registry.remove( sourceMatch );
    }

    public boolean isValid( String sourceName, int lineNumber, String className, String methodName, boolean isGroovyObject ) throws SandboxSecurityException {
        boolean ret = true;
        InterceptorValidator validator = registry.get( sourceName );
        if ( null != validator ) {
            // Direct match
            ret = validator.canInvoke( sourceName, lineNumber, className, methodName, isGroovyObject );
        } else {
            // Try regex matches
            for ( Map.Entry<String, InterceptorValidator> ent : this.registry.entrySet() ) {
                if ( sourceName.matches( ent.getKey() ) ) {
                    ret = ent.getValue().canInvoke( sourceName, lineNumber, className, methodName, isGroovyObject );
                    break;
                }
            }
        }

        return ret;
    }
}
