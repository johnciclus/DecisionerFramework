package semantics

/**
 * Created by john on 4/10/17.
 */
class Node {
    String URI
    static Know k
    Map patterns = [:]

    Node(Know k, String uri = null){
        this.k = k
        this.URI = uri

        //rdf:Property
        this.patterns['type']       = " rdf:type ?type. "
        this.patterns['superClass'] = " rdfs:subClassOf ?superClass. "

        //owl:ObjectProperty
        this.patterns['range']      = " rdfs:range ?range. "
        this.patterns['hasRole']    = " ui:hasRole ?hasRole. "
        this.patterns['appliedTo']  = " :appliedTo ?appliedTo. "
        this.patterns['hasOwner']   = " :hasOwner ?hasOwner. "

        //owl:DatatypeProperty
        this.patterns['mapa']       = " <http://dbpedia.org/property/pt/mapa> ?mapa. "
        this.patterns['createAt']   = " ui:createAt ?createAt. "
        this.patterns['updateAt']   = " ui:updateAt ?updateAt. "
        this.patterns['hasUsername']= " ui:hasUsername ?hasUsername. "
        this.patterns['hasPassword']= " ui:hasPassword ?hasPassword. "
        this.patterns['harvestYear']= " :harvestYear ?harvestYear. "

        //owl:TransitiveProperty

        // owl:AnnotationProperty
        this.patterns['label']      = " rdfs:label ?label. "
        this.patterns['comment']    = " rdfs:comment ?comment. "
        this.patterns['weight']     = " :weight ?weight. "
        this.patterns['description']= " dcterm:description ?description. "
        this.patterns['title']      = " dcterm:title ?title. "
        this.patterns['creator']    = " dc:creator ?creator. "
    }

    /**
     * Get atributes from node using the patterns or a URI.
     *
     * @param args
     * @param params
     * @return
     */
    def getAttr(String args='', Map params = [:]) {
        def argsList = args.tokenize(' ?')
        def select = ''
        def query = ''
        def order = ''
        def result

        argsList.each{
            if(argsList.contains(it))
                query += "<$URI>"+patterns[it]
        }

        if(params.containsKey('FILTER')){           //(?type != :ProductionUnit && ?type != ui:EvaluationObject && !isBlank(?type) )"
            query += "FILTER(" + params['FILTER'] + ")"
        }

        // By default orginizes by label or if there is only one argument, it uses it
        if(argsList.contains('label')){
            order = 'order by ?label'
        }
        else if(argsList.size()==1){
            order = 'order by ?'+argsList[0]
        }

        select = 'distinct '
        argsList.each{
            select += '?'+it
        }

        result = k.select(select).query(query, order)

        //println result

        if(argsList.size()==1)
            result = result.collect{ it[argsList[0]]}

        (result.size()==1)? result[0] : result
    }

    /**
     * Get attributes of a node by subscript operator
     * @param name
     * @return
     */
    def getAt(String name){
        getAttr(name)
    }

    /**
     * Get label as an attribute
     * @param params
     * @return
     */
    def getLabel(Map params = [:]){
        getAttr('?label', params)
    }

    /**
     * Get subclasses of a class
     * @param args
     * @return
     */
    def getSubClass(String args=''){
        def argsList = args.tokenize(' ?')
        def query = ''

        argsList.each{
            if(argsList.contains('label'))
                query += "?id rdfs:label ?label."
        }

        query += "?id rdfs:subClassOf <$URI>." +
                "?subClass rdfs:subClassOf ?id."+
                "FILTER(?id != <$URI> && ?id != ?subClass)"

        def arg = ''

        if(argsList.size()>0){
            arg = "?" + ['label', 'id'].join(" ?")
        }

        k.select('distinct ?id '+arg).query(query)
    }

    /**
     * Get all individuals of type this node that have the properties in args.
     * @param args
     * @return
     */
    def getIndividuals(String args){
        def argsList = (args).tokenize(' ?')
        def select = ''
        def query = "?id a <$URI>. "
        def order = ''
        def result

        //println argsList
        argsList.each{
            if(argsList.contains(it))
                query +=  "?id"+patterns[it]
        }

        if(argsList.contains('label')){
            order = 'order by ?label'
        }
        else if(argsList.size()==1){
            order = 'order by ?'+argsList[0]
        }

        select = 'distinct '
        argsList.add('id')
        argsList.each{
            select += '?'+it
        }

        result = k.select(select).query(query, order)

        if(argsList.size()==1){
            result = result.collect{ it[argsList[0]]}
        }

        (result.size()==1)? result[0] : result
    }
}
