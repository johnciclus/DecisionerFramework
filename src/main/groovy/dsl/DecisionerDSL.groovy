package dsl

import org.springframework.context.ApplicationContext
import semantics.*

/**
 * Created by john on 4/6/17.
 */
class DecisionerDSL extends DSL{

    private parentNode = null
    private _props = [:]
    private _dataName
    private _program
    private report = []

    DecisionerDSL(String filename, ApplicationContext applicationContext){
        super(filename, applicationContext)
    }

    private def createParentNode(String type){
        def node = [type: type, children: []]
        addNodeToData(node, {})
        parentNode = node
    }

    private def addNodeToData(node, closure){
        if(parentNode == null)
            data.push(node)
        else
            parentNode.children.push(node)

        def tmpNode = parentNode
        parentNode = node
        closure()
        parentNode = tmpNode
    }

    def featureGroup(String id, Closure closure = {}){
        def featureGroup = k.toURI('ui:FeatureGroup')
        def uri = k.toURI(id)
        def kNode = new Node(k, uri)
        def node = [id: uri, label: kNode['label'], children: []]

        if(parentNode == null || parentNode.type != featureGroup){
            createParentNode(featureGroup)
        }
        addNodeToData(node, closure)
    }

    def feature(Map attrs, String id, Closure closure = {}){
        def featureURI = k.toURI('ui:Feature')
        def featureGroup = k.toURI('ui:FeatureGroup')
        def categoricalURI = k.toURI('ui:Categorical')
        def IndicatorURI = k.toURI(':Indicator')
        def uri = k.toURI(id)
        def feature = new Feature(uri, attrs, context)
        def kNode = new Node(k, uri)
        def tmpNode

        def children = []

        def subClasses = kNode.getSubClass('?label')

        def superClasses = kNode.getSuperClass('?label')

        def typeURI = featureURI

        superClasses.each{
            if(it.id != IndicatorURI && it.id != featureURI){
                typeURI = it.id
            }
        }

        def grandChildren = kNode.getGrandchildren('?id ?label ?subClass ?relevance ?category ?weight ?weightLabel')

        if(parentNode == null || parentNode.type != featureGroup){
            createParentNode(featureGroup)
        }

        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = feature

        def features = []
        def options

        subClasses.each{ subClass ->
            grandChildren.each{
                if(subClass.id == it.subClass) {
                    tmpNode = new Node(k, it.id)

                    def valueTypes = tmpNode.collectionIndividualsTypes
                    def categoryIndividuals = tmpNode.collectionIndividuals.capitalizeLabels()
                    def weightIndividuals

                    if(it.weight){
                        weightIndividuals = tmpNode.weightIndividuals.capitalizeLabels()
                    }

                    if(valueTypes.contains('http://purl.org/biodiv/semanticUI#Boolean') || valueTypes.contains(categoricalURI)){
                        options = []
                        categoryIndividuals.each{ option ->
                            options.push([type: categoricalURI, name: option.id, label: option.label])
                        }
                        features.push([type: featureURI, 'id': it.id, label: it.label, children: options])

                    }else{
                        println it
                        features.push([type: featureURI]) //text
                    }
                }
            }
            def fieldSet = [type: uri, id: subClass.id, label: subClass.label, children: features]
            features = []
            children.push(fieldSet)
        }

        def node = [type: typeURI, id: uri, label: kNode.label, children: children ] + attrs
        addNodeToData(node, closure)
    }

    def feature(String id, Closure closure = {}){
        feature([:], id, closure)
    }

    def data(String str){
        _dataName = str
    }

    def setData(obj){
        _props[_dataName]= obj
    }

    def getData(String key){
        _props[key]
    }

    def report(Closure c){
        _program = c
    }

    def runReport(){
        _program()
    }

    def weightedSum(obj){
        float val = 0
        float value
        float weight

        println obj

        if(obj in ArrayList) {
            obj.each {
                value = (it.value in Boolean ? (it.value ? 1 : -1) : it.value)
                if (it.relevance)
                    weight = (it.relevance in Boolean ? (it.relevance ? 1 : -1) : it.relevance)
                else if (it.weight)
                    weight = it.weight
                else
                    weight = 1.0
                val += (float) value * weight
            }
        }
        return val
    }

    def methodMissing(String id, attrs){
        def elementURI = k.toURI('ui:'+id)
        def attrsTmp = attrs.clone()
        def tmp

        if(attrs in Object[]){
            def container = []
            def element = null

            report.push([type: elementURI])
        }
    }

    def getReport(){
        return report
    }
}
