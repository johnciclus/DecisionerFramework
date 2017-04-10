tab ':SustainabilityIndicator', {
	
	feature ':EnvironmentalIndicator', 'extraFeatures': true

    feature ':EconomicIndicator', 'extraFeatures': true

    feature ':SocialIndicator', 'extraFeatures': true
    
}

tab ':Variable', {
	
	feature ':EnvironmentalIndicator', 'extraFeatures': true

    feature ':EconomicIndicator', 'extraFeatures': true

    feature ':SocialIndicator', 'extraFeatures': true
	
    feature ':ProductionEfficiencyFeature'

    feature ':TechnologicalEfficiencyFeature', {
        conditional ":ProductionUnit", 'http://dbpedia.org/ontology/Provider', {
            include ':TechnologicalEfficiencyInTheField'
        }
        conditional ":ProductionUnit", 'http://dbpedia.org/resource/PhysicalPlant', {
            include ':TechnologicalEfficiencyInTheField', ':TechnologicalEfficiencyInTheIndustrial'
        }
    }
    
}