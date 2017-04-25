import com.github.slugify.Slugify
import dsl.DecisionerDSL
import dsl.UIDSL
import semantics.Know

beans = {
    k(Know, 'http://127.0.0.1:9999/blazegraph/namespace/kb/sparql')
    ui(UIDSL, "dsl/dataTypes.groovy", grailsApplication.mainContext)
    dsl(DecisionerDSL, "dsl/main.groovy", grailsApplication.mainContext)
    slugify(Slugify)
}
