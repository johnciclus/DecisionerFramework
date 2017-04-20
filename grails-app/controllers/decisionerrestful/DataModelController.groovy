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
        //def file = ctx.getResource("dsl/"+id+".groovy").file
        def data = [:]
        def uri = k.toURI('inds:'+'analysis01')

        //if(file.exists()){
            if(id == 'input'){
                //def dsl = new DecisionerDSL("dsl/"+id+".groovy", grailsApplication.mainContext)
                data = dsl.data
            }
            else if(id == 'report'){
                dsl.setData(new DataReader(k, uri))
                dsl.report = []
                dsl.runReport()
                data = dsl.report
            }
            else if(id == 'datatypes'){
                data = ui.data
            }
            else{
                data['error'] = 'File processing error'
            }
        /*}
        else{
            data['error'] = 'No file found'
        }*/

        respond data
    }
}
