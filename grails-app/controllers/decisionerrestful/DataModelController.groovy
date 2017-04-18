package decisionerrestful

import dsl.*
import grails.rest.*
import grails.converters.*

class DataModelController {
	static responseFormats = ['json', 'xml']
    def ctx = grailsApplication.mainContext

    def index() {
        def data = [status: 'ok']
        respond data
    }

    def show() {
        def id = params.id
        def file = ctx.getResource("dsl/"+id+".groovy").file
        def data = [:]

        if(file.exists()){
            if(id == 'main'){
                def dsl = new DecisionerDSL("dsl/"+id+".groovy", grailsApplication.mainContext)
                data = dsl.data
            }
            else if(id == 'datatypes'){
                def dsl = new UIDSL("dsl/"+id+".groovy", grailsApplication.mainContext)
                data = dsl.data
            }
            else{
                data['error'] = 'File processing error'
            }
        }
        else{
            data['error'] = 'No file found'
        }

        respond data
    }
}
