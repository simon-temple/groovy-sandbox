package com.amalto.groovy;

import org.apache.log4j.Logger;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.ASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
public class ScanTransform implements ASTTransformation {

    private final static Logger LOG = Logger.getLogger( ScanTransform.class );

    public void visit( ASTNode[] nodes, SourceUnit source ) {

        if ( nodes.length < 2 || !( nodes[ 1 ] instanceof ClassNode ) ) {
            throw new RuntimeException( "Internal error.  Not enough nodes or incorrect node type!" );
        }

        GroovyCodeVisitor viz = new ScriptScanningVisitor( source );
        ClassNode node = ( ClassNode ) nodes[ 1 ];

        for ( MethodNode mn : node.getMethods() ) {

            if ( mn.getName().equals( "main" ) )
                continue;

            if ( LOG.isTraceEnabled() ) {
                LOG.trace( "Visiting methods of: " + mn.getName() );
            }

            mn.getCode().visit( viz );
        }

    }
}
