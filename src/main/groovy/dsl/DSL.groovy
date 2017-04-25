package dsl

import org.springframework.context.ApplicationContext
import org.codehaus.groovy.control.CompilerConfiguration
import org.kohsuke.groovy.sandbox.SandboxTransformer
import semantics.Know

/**
 * Created by john on 4/18/17.
 */
class DSL {
    private _ctx
    private Know _k
    private _shell
    private _sandbox
    private _script

    def dataModel

    DSL(String filename, ApplicationContext applicationContext){
        _ctx = applicationContext;
        _k = _ctx.getBean('k')

        def _cc = new CompilerConfiguration()
        _cc.addCompilationCustomizers(new SandboxTransformer())
        _cc.setScriptBaseClass(DelegatingScript.class.name)

        _shell = new GroovyShell(new Binding(), _cc)
        _sandbox = new DSLSandbox()

        _script = (DelegatingScript) _shell.parse(_ctx.getResource(filename).file)

        _script.setDelegate(this)

        dataModel = [:]

        try {
            def duration = benchmark(_script)
            println "File '"+filename+"' execution took ${duration} ms"
        }
        finally {
            _sandbox.unregister()
        }
    }

    def getK(){
        return _k
    }

    def getDataModel(){
        //if(id == ''){
        return this.dataModel
        /*}
        else{
            return dataModel[id]
        }*/
    }

    def getContext(){
        return _ctx
    }

    def benchmark(script) {
        def start = System.currentTimeMillis()
        script.run()
        def now = System.currentTimeMillis()
        now - start
    }
}
