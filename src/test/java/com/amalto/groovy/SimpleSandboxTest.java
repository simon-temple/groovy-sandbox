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
    public void runInline() throws Exception {

        // The name of my script
        String scriptName = "MyScript.groovy";

        // Register a validator - using regex or explicit script name above
        InterceptorRegistry.getInstance().register( scriptName, new TestValidator() );

        // Add the custom scanning AST transformation
        CompilerConfiguration config = new CompilerConfiguration( CompilerConfiguration.DEFAULT );
        config.addCompilationCustomizers( new ASTTransformationCustomizer( ScriptScanner.class ) );

        // Load the script text
        GroovyShell shell = new GroovyShell( config );
        String theScript = "println 'hello from MyScript';System.exit(0)";
        GroovyCodeSource cs = new GroovyCodeSource( theScript, scriptName, "/groovy/sandbox" );

        // Compile
        Script parsedScript = shell.parse( cs );
        // and run...
        parsedScript.run();

    }

    @Test
    public void runScriptOne() throws Exception {

        // The name of my script
        String scriptName = "MyScript.groovy";

        // Register a validator - using regex or explicit script name above
        InterceptorRegistry.getInstance().register( ".*.groovy", new TestValidator() );

        // Add the custom scanning AST transformation
        CompilerConfiguration config = new CompilerConfiguration( CompilerConfiguration.DEFAULT );
        config.addCompilationCustomizers( new ASTTransformationCustomizer( ScriptScanner.class ) );

        // Load the script text
        GroovyShell shell = new GroovyShell( config );
        String theScript = FileUtils.readFileToString( new File( "src/test/resources/GroovyScript1.txt" ) );
        GroovyCodeSource cs = new GroovyCodeSource( theScript, scriptName, "/groovy/sandbox" );

        System.out.println( "COMPILE." );

        Script parsedScript = shell.parse( cs );

        System.out.println( "RUN." );

        parsedScript.run();

    }

    @Test
    public void runScriptTwo() throws Exception {

        // Register a validator - using regex or explicit script name above
        InterceptorRegistry.getInstance().register( ".*.groovy", new IgnoreValidator() );

        // Add the custom scanning AST transformation
        CompilerConfiguration config = new CompilerConfiguration( CompilerConfiguration.DEFAULT );
        config.addCompilationCustomizers( new ASTTransformationCustomizer( ScriptScanner.class ) );

        // Load the script text
        GroovyShell shell = new GroovyShell( config );
        String theScript = FileUtils.readFileToString( new File( "src/test/resources/GroovyScript2.txt" ) );
        GroovyCodeSource cs = new GroovyCodeSource( theScript, "test2.groovy", "/groovy/sandbox" );

        shell.parse( cs ).run();

    }
}
