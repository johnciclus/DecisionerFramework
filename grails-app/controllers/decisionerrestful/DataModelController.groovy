package decisionerrestful

import dsl.*
import grails.rest.*
import grails.converters.*
import semantics.DataReader

class DataModelController {
	static responseFormats = ['json', 'xml']
    def ctx = grailsApplication.mainContext
    def k
    def ui
    def dsl

    def index() {
        def data = [status: 'ok']
        respond data
    }

    def show() {
        def id = params.id
        def data = [:]
        def uri = k.toURI('inds:'+'analysis01')

        switch(id){
            case 'main':
                data = dsl.dataModel['input']
                break
            case 'instance':
                data = dsl.dataModel['instance']
                break
            case 'input':
                data = dsl.dataModel[id]
                break
            case 'report':
                dsl.setData(new DataReader(k, uri))
                dsl.dataModel[id] = []
                dsl.runReport()
                data = dsl.dataModel[id]
                break
            case 'datatypes':
                data = ui.dataModel
                break
            default:
                data['error'] = 'File processing error'
                break
        }

        respond data
    }
}
