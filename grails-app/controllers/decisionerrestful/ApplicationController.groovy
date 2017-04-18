package decisionerrestful

import grails.converters.JSON
import grails.core.GrailsApplication
import grails.plugins.*

class ApplicationController implements PluginManagerAware {

    GrailsApplication grailsApplication
    GrailsPluginManager pluginManager

    def index() {
        //[grailsApplication: grailsApplication, pluginManager: pluginManager]
        def result = [status: 'ok']

        render result as JSON
    }
}
