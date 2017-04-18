package decisionerrestful

import grails.rest.*
import grails.converters.*
import dsl.*

class DSLController {
	static responseFormats = ['json', 'xml']
    def ctx = grailsApplication.mainContext;

    def index() {
        def dsls = ctx.getResource("dsl/").file
        def data = []
        dsls.eachFile { file ->
            data.push(file.getName().replaceFirst(~/\.[^\.]+$/, ''))
        }

        respond data
    }

    def show() {
        def file = ctx.getResource("dsl/"+params.id+".groovy").file
        def data = [:]

        if(file.exists()){
            data['dsl'] = file.text
        }
        else{
            data['error'] = 'No file found'
        }

        respond data
    }

    def save(){
        def input = request.JSON;
        def file = ctx.getResource("dsl/"+input.id+".groovy").file
        def data = [:]

        if(file.exists()){
            file.write(input.dsl)
            data['message'] = 'DSL Saved'
        }
        else{
            data['error'] = 'No file found'
        }

        respond data
    }
}
