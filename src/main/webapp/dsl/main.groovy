//                          Decisioner

// Esta DSL descreve como o aplicativo será gerado. Ele é em inglês pois
// facilita na hora de publicarmos os papers. Porém, o aplicativo pode ser
// em português ou inglês. Isso vai depender da ontologia, nela teremos as
// definições nas duas linguas.

// Informações que serão lidas antes dos indicadores. No exemplo serão
// mostradas todas as culturas, tecnologias e meios de produção que
// estiverem na ontologia
// Mantemos crop, mas se a única que estiver na ontologia for Cana,
// a interface só mostra uma opção (sem possibilidade de escolha)
// Na ontologia, location definiria as microregiões do IBGE.
// Se a fazenda ficar em mais de uma micro-região?

// Caracterização dos sistemas produtivos no Centro-Sul
evaluationObject ':ProductionUnit', {
    //type or instance

    // Production unit name
    instance 'ui:hasName', label: ['en': 'Production unit or farm name', 'pt': 'Nome da unidade produtiva ou fazenda']

    // Agricultural production system
    instance ':hasAgriculturalProductionSystem', label: ['en': 'Agricultural production system' , 'pt': "Sistema de produção agrícola"]

    // Production unit type
    // Tipo de organização (Greenfiled, usinas tradicionais, familiares...?).
    type label: ['en': "Production unit type", 'pt': "Tipo da unidade produtiva"], header: ['en': 'Options', 'pt': "Opções"]


    // Origem da cana (própria, fornecedor, arrendamento)
    instance  ':hasSugarcaneSource', label: ['en': 'Sugarcane source', 'pt': "Origem da cana"], header: ['en': 'Options', 'pt': "Opções"], multipleSelection: true, required: true

    // Estado da unidade produtiva
    instance 'dbp:state', label: ['en': 'State', 'pt': 'Estado'], header: ['en': 'States', 'pt': 'Estados']

}

featureGroup ':SustainabilityIndicator', {

    feature ':EnvironmentalIndicator', 'extraFeatures': true

    feature ':EconomicIndicator', 'extraFeatures': true

    feature ':SocialIndicator', 'extraFeatures': true
}

featureGroup ':Variable', {

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

data 'data'

report {
    /*
    environment =   weightedSum(data.':EnvironmentalIndicator')
    economic    =   weightedSum(data.':EconomicIndicator')
    social      =   weightedSum(data.':SocialIndicator')

    sustainability = (environment + social + economic)/3

    cost_production_efficiency = sum(data.':ProductionEfficiencyFeature')

    technologicalEfficiencyInTheField = 0.8*weightedSum(data.':TechnologicalEfficiencyInTheField')  //.equation({value*weight}))
    technologicalEfficiencyInTheIndustrial = 0.2*weightedSum(data.':TechnologicalEfficiencyInTheIndustrial')

    efficiency = Math.abs(cost_production_efficiency) *
            (technologicalEfficiencyInTheField+technologicalEfficiencyInTheIndustrial)
    */

    sustainability = 40

    efficiency = 100

    sustainabilityMatrix    x: sustainability,
            y: efficiency,
            label_x: ['en': 'Sustainability Index', 'pt': 'Índice de Sustentabilidade'],
            label_y: ['en': 'Efficiency index', 'pt': 'Índice de Eficiência'],
            range_x: [-43,43],
            range_y: [-160,800],
            quadrants: [4,3],
            recomendations: [   "Avaliação da eficiência: balanço da eficiência ‘tecnológica – produção – custo’ desfavorável ao sistema de produção de cana / sistema de produção de cana na fase inicial de implementação (avaliação de sustentabilidade comprometida) ou com muito baixa sustentabilidade – sistema de produção de cana não recomendado.",
                                "Avaliação da eficiência: balanço da eficiência ‘tecnológica – produção – custo’ desfavorável ao sistema de produção de cana / sistema de produção de cana com baixa sustentabilidade – recomendam-se ações corretivas.",
                                "Avaliação da eficiência: balanço da eficiência ‘tecnológica – produção – custo’ desfavorável ao sistema de produção de cana / Avaliação da sustentabilidade com médio desempenho – recomenda-se acompanhamento com restrições.",
                                "Avaliação da eficiência: balanço da eficiência ‘tecnológica – produção – custo’ desfavorável ao sistema de produção de cana / Avaliação da sustentabilidade com bom desempenho – sistema de produção de cana recomendado.",
                                "Avaliação da eficiência: balanço da eficiência ‘tecnológica – produção – custo’ propícia para o sistema cana / Avaliação da sustentabilidade: sistema de produção de cana na fase inicial de implementação ou com muito baixa sustentabilidade – gerenciamento recomendado com restrições.",
                                "Avaliação da eficiência: balanço da eficiência ‘tecnológica – produção – custo’ propícia para o sistema cana / Avaliação da sustentabilidade: com baixo desempenho - recomenda-se ações corretivas.",
                                "Avaliação da eficiência: balanço da eficiência ‘tecnológica – produção – custo’ propícia para o sistema cana / Avaliação da sustentabilidade: com médio desempenho - monitoramento e gerenciamento recomendado.",
                                "Avaliação da eficiência: balanço da eficiência ‘tecnológica – produção – custo’ propícia para o sistema cana / Avaliação da sustentabilidade: com bom desempenho – sistema de produção de cana recomendado.",
                                "Avaliação da eficiência: balanço da eficiência ‘tecnológica – produção – custo’ favorável para o sistema cana – recomenda-se investimentos no sistema avaliado / Avaliação da sustentabilidade: com muito baixo desempenho – recomenda-se ações corretivas.",
                                "Avaliação da eficiência: balanço da eficiência ‘tecnológica – produção – custo’ favorável para o sistema cana – recomenda-se investimentos no sistema avaliado / Avaliação da sustentabilidade: com baixo desempenho - gerenciamento recomendado.",
                                "Avaliação da eficiência: balanço da eficiência ‘tecnológica – produção – custo’ favorável para o sistema cana – recomenda-se investimentos no sistema avaliado / Avaliação da sustentabilidade: com médio desempenho – monitoramento recomendado.",
                                "Avaliação da eficiência: balanço da eficiência ‘tecnológica – produção – custo’ favorável para o sistema cana – recomenda-se investimentos no sistema avaliado / Avaliação da sustentabilidade: com bom desempenho - sistema de produção de cana fortemente recomendado."]

    sustainabilitySemaphore value: sustainability,
            label: ['en': 'Sustainability Level', 'pt': 'Índice da sustentabilidade geral'],
            legend: [['en': 'Lower sustainability', 'pt': 'Menos sustentável'],
                     ['en': 'Negative changes', 'pt': 'Alterações negativas'],
                     ['en': 'Irrelevant changes', 'pt': 'Sem alteração'],
                     ['en': 'Positive changes', 'pt': 'Alterações positivas'],
                     ['en': 'Higher sustainability', 'pt': 'Mais sustentável']],
            range: [-43,43]

}