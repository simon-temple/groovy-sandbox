package com.amalto.groovy;

import com.sun.xml.internal.ws.org.objectweb.asm.Opcodes;
import groovyjarjarasm.asm.MethodVisitor;
import org.apache.log4j.Logger;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.*;
import org.codehaus.groovy.classgen.BytecodeExpression;
import org.codehaus.groovy.control.SourceUnit;

import java.util.ArrayList;
import java.util.List;

public class ScriptScanningVisitor extends ClassCodeVisitorSupport {

    private final static Logger LOG = Logger.getLogger( ScriptScanningVisitor.class );

    private SourceUnit src;

    public ScriptScanningVisitor( SourceUnit src ) {
        this.src = src;
    }

    private Expression toArgumentArray( Expression arguments ) {

        List<Expression> argumentList;

        if ( arguments instanceof NamedArgumentListExpression ) {

            argumentList = new ArrayList<Expression>();
            argumentList.add( arguments );
        } else {
            TupleExpression tuple = ( TupleExpression ) arguments;
            argumentList = tuple.getExpressions();
        }

        // Disallow SpreadExpressions
        for ( Expression exp : argumentList ) {
            if ( exp instanceof SpreadExpression ) {
                LOG.error( src.getName() + ": SpreadExpression encountered and disallowed!" );
                return null;
            }
        }

        return new ArrayExpression( ClassHelper.OBJECT_TYPE, argumentList );
    }


    private StaticMethodCallExpression proxyWrapCall( Expression exp, String sourceName, int lineNumber ) {

        MethodCallExpression call = ( MethodCallExpression ) exp;

        if ( LOG.isTraceEnabled() ) {
            LOG.trace( "Wrapping method call: " + call.getMethodAsString() + " args: " + call.getArguments() );
        }

        ArgumentListExpression methArgs = new ArgumentListExpression();

        if ( call.getObjectExpression() instanceof VariableExpression && call.getObjectExpression().getText().equals( "this" ) ) {

            // Target: Closure resolved
            methArgs.addExpression( new BytecodeExpression() {
                @Override
                public void visit( MethodVisitor methodVisitor ) {
                    methodVisitor.visitVarInsn( Opcodes.ALOAD, 0 );
                }
            } );

        } else {

            // Target
            methArgs.addExpression( call.getObjectExpression() );
        }

        // Method
        methArgs.addExpression( call.getMethod() );

        // Source Name
        methArgs.addExpression( new ConstantExpression( sourceName ) );

        // Line Number
        methArgs.addExpression( new ConstantExpression( lineNumber ) );

        // Method Args
        methArgs.addExpression( toArgumentArray( call.getArguments() ) );

        return new StaticMethodCallExpression( new ClassNode( RuntimeScriptInterceptor.class ), "invokeMethod", methArgs );

    }

    @Override
    public void visitExpressionStatement( ExpressionStatement statement ) {

        if ( statement.getExpression() instanceof MethodCallExpression ) {

            statement.setExpression( proxyWrapCall( statement.getExpression(), this.src.getName(), statement.getLineNumber() ) );
        }
        super.visitExpressionStatement( statement );
    }

    @Override
    protected SourceUnit getSourceUnit() {
        return this.src;
    }

}