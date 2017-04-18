package dsl

import org.springframework.context.ApplicationContext

/**
 * Created by john on 4/17/17.
 */
class UIDSL extends DSL{
    UIDSL(String filename, ApplicationContext applicationContext){
        super(filename, applicationContext)
    }

    def dataType(Map attrs, String id){
        def uri = k.toURI(id)
        if(!data[0])
            data.push([:])
        data[0][uri] = attrs.widget
    }
}
