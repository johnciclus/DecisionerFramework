package dsl

import org.springframework.context.ApplicationContext
import semantics.*

/**
 * Created by john on 4/6/17.
 */
class DecisionerDSL extends DSL{

    private UIDSL ui
    private parentNode = null

    DecisionerDSL(String filename, ApplicationContext applicationContext){
        super(filename, applicationContext)
    }

    def addNodeToData(node, closure){
        if(parentNode == null)
            data.push(node)
        else
            parentNode.children.push(node)

        def tmpNode = parentNode
        parentNode = node
        closure()
        parentNode = tmpNode
    }

    def group(String id, Closure closure = {}){
        def featureURI = k.toURI('ui:Feature')
        def uri = k.toURI(id)
        def kNode = new Node(k, uri)
        def node = [id: uri, label: kNode['label'], children: []]

        if(parentNode == null || parentNode.type != featureURI){
            createParentNode(featureURI)
        }
        addNodeToData(node, closure)
    }

    def feature(Map attrs, String id, Closure closure = {}){
        def featureURI = k.toURI('ui:Feature')
        def uri = k.toURI(id)
        def feature = new Feature(uri, attrs, context)
        def kNode = new Node(k, uri)
        def tmpNode

        def children = []

        def subClasses = kNode.getSubClass('?label')

        def grandChildren = kNode.getGrandchildren('?id ?label ?subClass ?relevance ?category ?weight ?weightLabel')

        if(parentNode == null || parentNode.type != featureURI){
            createParentNode(featureURI)
        }

        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = feature

        def divs = []
        def widgets = []
        def radios

        subClasses.each{ subClass ->
            subClass['widget'] = 'h5'
            grandChildren.each{
                if(subClass.id == it.subClass) {
                    tmpNode = new Node(k, it.id)

                    def valueTypes = tmpNode.collectionIndividualsTypes
                    def categoryIndividuals = tmpNode.collectionIndividuals.capitalizeLabels()
                    def weightIndividuals

                    if(it.weight){
                        weightIndividuals = tmpNode.weightIndividuals.capitalizeLabels()
                    }

                    widgets.push([widget: 'label', id: it.id, label: it.label])

                    if(valueTypes.contains('http://purl.org/biodiv/semanticUI#Boolean') || valueTypes.contains('http://purl.org/biodiv/semanticUI#Categorical')){
                        radios = []
                        categoryIndividuals.each{ option ->
                            radios.push([widget: 'paper-radio-button', name: option.id, label: option.label])
                        }
                        widgets.push([widget: 'paper-radio-group', 'aria-labelledby': it.id, children: radios])

                    }else{
                        widgets.push([widget: 'input', type: 'text'])
                    }

                    divs.push([widget: 'div', children: widgets])
                    widgets = []

                    //println valueTypes
                    //println categoryIndividuals

                }
            }
            def fieldSet = [widget: 'fieldset', id: subClass.id, children: [[widget: 'legend', children: [subClass]]]+divs]
            divs = []
            children.push(fieldSet)
        }

        def div = attrs + [widget: 'div', children: children]
        def node = [id: uri, label: kNode.label, children: [div]]
        addNodeToData(node, closure)
    }

    def feature(String id, Closure closure = {}){
        feature([:], id, closure)
    }

    def createParentNode(String type){
        def node = [type: type, children: []]
        addNodeToData(node, {})
        parentNode = node
    }
}
