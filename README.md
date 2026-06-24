# GenRoute GA - Otimizacao de Rotas com Algoritmo Genetico em Java

Projeto academico em Java que aplica Algoritmo Genetico ao Problema do Caixeiro Viajante. A aplicacao encontra uma rota curta que visita todos os pontos de um problema e retorna ao ponto inicial, simulando cenarios de entregas, transporte, coleta, atendimento tecnico e planejamento de visitas.

## Tema

Aplicacao de Algoritmo Genetico na otimizacao de rotas: uma abordagem computacional para o Problema do Caixeiro Viajante.

## Como a solucao e representada

Cada individuo da populacao representa uma rota possivel. O ponto `0` e mantido como ponto inicial para facilitar a leitura no seminario.

Exemplo:

```text
[0, 3, 5, 2, 1, 4]
```

Essa rota significa:

```text
Ponto 0 -> Ponto 3 -> Ponto 5 -> Ponto 2 -> Ponto 1 -> Ponto 4 -> retorno ao Ponto 0
```

## Modelagem matematica

Dado um conjunto de pontos:

```text
V = {1, 2, 3, ..., n}
```

Cada ponto possui coordenadas `(x, y)`.

A distancia entre dois pontos e calculada pela distancia euclidiana:

```text
d(i,j) = sqrt((xi - xj)^2 + (yi - yj)^2)
```

O objetivo e minimizar a distancia total da rota:

```text
Minimizar Z = soma das distancias entre pontos consecutivos + retorno ao ponto inicial
```

## O que o projeto mostra

- Rota inicial gerada aleatoriamente.
- Melhor rota encontrada pelo Algoritmo Genetico.
- Distancia inicial.
- Distancia final.
- Percentual de melhoria.
- Grafico animado da evolucao da distancia ao longo das geracoes.
- Comparacao visual entre rota inicial e rota otimizada.

## Estrutura

```text
genroute-ga-java/
|-- pom.xml
|-- README.md
|-- data/
|   `-- problemas/
|       |-- cidades_10.csv
|       |-- cidades_20.csv
|       `-- cidades_30.csv
`-- src/
    `-- main/
        |-- java/
        |   `-- br/
        |       `-- com/
        |           `-- genroute/
        |               |-- App.java
        |               |-- ga/
        |               |   |-- GeneticAlgorithm.java
        |               |   |-- GeneticParameters.java
        |               |   `-- GeneticResult.java
        |               |-- io/
        |               |   `-- ProblemRepository.java
        |               |-- model/
        |               |   |-- City.java
        |               |   |-- ProblemInstance.java
        |               |   `-- Route.java
        |               `-- visual/
        |                   `-- RouteCanvas.java
        `-- resources/
            `-- styles/
                `-- app.css
```

## Como cadastrar novos problemas

Crie um arquivo `.csv` dentro de `data/problemas`.

Formato:

```csv
nome,x,y
Deposito,20,30
Cliente 1,80,42
Cliente 2,55,90
```

A primeira cidade do arquivo sera o ponto inicial da rota.

## Como executar

Com Java e Maven instalados no sistema:

```bash
mvn javafx:run
```

Tambem existe uma instalacao portatil para este projeto em `.tools/`, com JDK 21 e Maven. Nesse caso, execute pelo PowerShell:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\run.ps1
```

Para rodar qualquer comando Maven usando o Java portatil:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\mvn.ps1 -q -DskipTests package
```

Os scripts configuram `JAVA_HOME`, `MAVEN_HOME` e um repositorio Maven local em `.m2/repository`, tudo dentro do projeto.

## Tecnologias

- Java
- Maven
- JavaFX
- Git/GitHub

## Autor

Iury Figueiredo Lopes
