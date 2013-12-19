package com.amalto.groovy;

import com.amalto.groovy.annotations.ScriptScanner;
import com.amalto.groovy.interception.InterceptorRegistry;
import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.apache.commons.io.FileUtils;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer;
import org.junit.Test;

import java.io.File;

public class SimpleSandboxTest {

    @Test
    public void runOne() throws Exception {

        String scriptName = "MyScript.groovy";

        // Register a validator - using regex or explicit script name above
        InterceptorRegistry.getInstance().register( ".*.groovy", new TestValidator() );

        // Add the custom scanning AST transformation
        CompilerConfiguration config = new CompilerConfiguration( CompilerConfiguration.DEFAULT );
        config.addCompilationCustomizers( new ASTTransformationCustomizer( ScriptScanner.class ) );

        // Load the script text
        GroovyShell shell = new GroovyShell( config );
        String theScript = FileUtils.readFileToString( new File( "src/test/resources/GroovyScript.txt" ) );
        GroovyCodeSource cs = new GroovyCodeSource( theScript, scriptName, "/groovy/sandbox" );

        System.out.println( "COMPILE." );

        Script parsedScript = shell.parse( cs );

        System.out.println( "RUN." );

        parsedScript.run();

    }
}
