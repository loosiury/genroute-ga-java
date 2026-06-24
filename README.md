GenRoute GA - Otimização de Rotas com Algoritmo Genético em Java
Sobre o projeto

O GenRoute GA é um projeto acadêmico desenvolvido em Java para aplicar Algoritmos Genéticos na resolução de um problema clássico de otimização combinatória: o Problema do Caixeiro Viajante.

O objetivo é encontrar uma rota de menor distância que visite todos os pontos definidos e retorne ao ponto inicial, simulando situações reais como logística, entregas, transporte e planejamento de rotas.

Tema

Aplicação de Algoritmo Genético na Otimização de Rotas: uma abordagem computacional para o Problema do Caixeiro Viajante.

Problema trabalhado

O problema consiste em definir a melhor ordem de visita entre vários pontos, buscando minimizar a distância total percorrida.

Esse tipo de problema aparece em situações como:

rotas de entrega;
transporte urbano;
coleta de materiais;
logística empresarial;
atendimento técnico em campo;
planejamento de visitas.
Por que usar Algoritmo Genético?

O número de rotas possíveis cresce rapidamente conforme a quantidade de pontos aumenta. Por isso, testar todas as combinações possíveis se torna inviável.

O Algoritmo Genético busca boas soluções por meio de um processo inspirado na evolução natural, utilizando:

população inicial;
função de aptidão;
seleção;
cruzamento;
mutação;
elitismo.
Modelagem matemática

Dado um conjunto de pontos:

V = {1, 2, 3, ..., n}

Cada ponto possui coordenadas:

(x, y)

A distância entre dois pontos é calculada pela distância euclidiana:

d(i,j) = sqrt((xi - xj)² + (yi - yj)²)

O objetivo é minimizar a distância total da rota:

Minimizar Z = soma das distâncias entre pontos consecutivos + retorno ao ponto inicial
Como o algoritmo representa uma solução?

Cada indivíduo da população representa uma rota possível.

Exemplo:

[0, 3, 5, 2, 1, 4]

Essa rota representa a sequência:

Ponto 0 → Ponto 3 → Ponto 5 → Ponto 2 → Ponto 1 → Ponto 4 → Retorno ao início
Tecnologias utilizadas
Java
Maven
JavaFX
VS Code
Git/GitHub
Estrutura do projeto
genroute-ga-java/
│
├── pom.xml
├── README.md
├── data/
│   └── cidades_30.csv
│
└── src/
    └── main/
        └── java/
            └── br/
                └── com/
                    └── genroute/
                        ├── App.java
                        ├── model/
                        │   ├── City.java
                        │   └── Route.java
                        └── ga/
                            ├── GeneticAlgorithm.java
                            └── GeneticResult.java
Visualizações esperadas

O projeto pretende apresentar visualmente:

rota inicial gerada aleatoriamente;
melhor rota encontrada pelo Algoritmo Genético;
distância inicial;
distância final;
percentual de melhoria;
gráfico de evolução da distância ao longo das gerações.
Exemplo de resultado esperado
Distância inicial: 850.42
Distância final: 374.18
Melhoria: 56.00%
Como executar o projeto

Clone o repositório:

git clone https://github.com/seu-usuario/genroute-ga-java.git

Entre na pasta do projeto:

cd genroute-ga-java

Execute com Maven:

mvn javafx:run
Objetivo acadêmico

Este projeto foi desenvolvido como parte de um seminário de Matemática Computacional, com foco em demonstrar uma aplicação prática de Algoritmos Genéticos em problemas de otimização combinatória.

A proposta busca relacionar teoria, modelagem matemática, implementação computacional e visualização gráfica dos resultados.

Autor

Iury Figueiredo Lopes
