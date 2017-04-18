package dsl

import org.codehaus.groovy.control.CompilerConfiguration
import org.kohsuke.groovy.sandbox.SandboxTransformer
import org.springframework.context.ApplicationContext
import semantics.Know

/**
 * Created by john on 4/17/17.
 */
class UIDSL {
    private _ctx
    private Know _k
    private _shell
    private _sandbox
    private _script

    private dataTypes = [:]

    UIDSL(String filename, ApplicationContext applicationContext){
        _ctx = applicationContext;
        _k = _ctx.getBean('k')

        def _cc = new CompilerConfiguration()
        _cc.addCompilationCustomizers(new SandboxTransformer())
        _cc.setScriptBaseClass(DelegatingScript.class.name)

        _shell = new GroovyShell(new Binding(), _cc)
        _sandbox = new DSLSandbox()

        _script = (DelegatingScript) _shell.parse(_ctx.getResource(filename).file)

        _script.setDelegate(this)

        try {
            _script.run()
        }
        finally {
            _sandbox.unregister()
        }
    }

    def dataType(Map attrs, String id){
        def uri = _k.toURI(id)
        dataTypes[uri] = attrs.widget
    }

    def getdataTypes(){
        return dataTypes
    }
}
