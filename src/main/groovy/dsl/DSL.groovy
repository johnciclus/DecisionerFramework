package dsl

import org.springframework.context.ApplicationContext
import org.codehaus.groovy.control.CompilerConfiguration
import org.kohsuke.groovy.sandbox.SandboxTransformer

/**
 * Created by john on 4/6/17.
 */
class DSL {
    private _ctx
    private _shell
    private _sandbox
    private _script

    private viewMap = []
    private parentNode = null

    DSL(String filename, ApplicationContext applicationContext){
        _ctx = applicationContext;

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

    def tab(String id, Closure closure = {}){
        //String uri = _k.toURI(id)
        //def tab = new Tab(uri, closure, _ctx)

        //closure.delegate = tab
        //System.out.println(closure())

        //_tabMap[uri] = tab

        def node = [id: id, type: 'tab', children: []]

        if(parentNode == null)
            viewMap.push(node)
        else
            parentNode.children.push(node)

        def tmpNode = parentNode
        parentNode = node
        closure()
        parentNode = tmpNode
    }

    def feature(Map attrs, String id, Closure closure = {}){
        //String uri = _k.toURI(id)
        def feature = new Feature(id, attrs, _ctx)

        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = feature

        //_featureMap[uri] = feature

        def node = attrs + [id: id, type: 'feature', children: []]

        if(parentNode == null)
            viewMap.push(node)
        else
            parentNode.children.push(node)

        def tmpNode = parentNode
        parentNode = node
        closure()
        parentNode = tmpNode
    }

    def feature(String id, Closure closure = {}){
        feature([:], id, closure)
    }

    def getViewMap(){
        return viewMap
    }
}
