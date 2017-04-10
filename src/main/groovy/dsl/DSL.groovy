package dsl

import org.springframework.context.ApplicationContext
import org.codehaus.groovy.control.CompilerConfiguration
import org.kohsuke.groovy.sandbox.SandboxTransformer
import semantics.*

/**
 * Created by john on 4/6/17.
 */
class DSL {
    private _ctx
    private Know _k
    private _shell
    private _sandbox
    private _script

    private viewMap = []
    private parentNode = null

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

        try {
            _script.run()
        }
        finally {
            _sandbox.unregister()
        }
    }

    def addNodeToViewMap(node, closure){
        if(parentNode == null)
            viewMap.push(node)
        else
            parentNode.children.push(node)

        def tmpNode = parentNode
        parentNode = node
        closure()
        parentNode = tmpNode
    }

    def tab(String id, Closure closure = {}){
        def uri = _k.toURI(id)
        def kNode = new Node(_k, uri)
        def node = [id: id, label: kNode['label'], type: 'tab', children: []]

        addNodeToViewMap(node, closure)
    }

    def feature(Map attrs, String id, Closure closure = {}){
        String uri = _k.toURI(id)
        def feature = new Feature(id, attrs, _ctx)

        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = feature

        def kNode = new Node(_k, uri)
        def subClasses = kNode.getSubClass('?label')
        def children = []

        subClasses.each{ cls ->
            cls['type'] = 'class'
            cls['children'] = []
            children.push(cls)
            println children
        }

        def node = attrs + [id: id, label: kNode.label, type: 'feature', children: children]
        addNodeToViewMap(node, closure)
    }

    def feature(String id, Closure closure = {}){
        feature([:], id, closure)
    }

    def getViewMap(){
        return viewMap
    }
}
