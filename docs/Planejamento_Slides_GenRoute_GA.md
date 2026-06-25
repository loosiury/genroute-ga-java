# Planejamento de slides - GenRoute GA

Tempo alvo: 30 a 35 minutos

Foco da apresentação: uso do Algoritmo Genético como técnica de otimização.

Observação importante: o Problema do Caixeiro Viajante é o cenário aplicado. O centro do seminário deve ser o Algoritmo Genético, seus operadores e sua evolução ao longo das gerações.

## Slide 1 - Título

Título:

GenRoute GA - Algoritmo Genético aplicado à Otimização de Rotas

Conteúdo:

- Discente: Iury Figueiredo Lopes
- Docente: Danilo Dias da Silva
- Tema: Algoritmo Genético em problema de otimização combinatória

Fala:

> Neste seminário, vou apresentar um projeto em Java que usa Algoritmo Genético para otimizar rotas. O foco principal não é apenas a rota em si, mas como o Algoritmo Genético é aplicado, executado e analisado.

Tempo: 1 min

## Slide 2 - Contextualização

Título:

Por que otimizar rotas?

Conteúdo:

- Entregas
- Visitas técnicas
- Coleta de materiais
- Transporte
- Logística urbana
- Planejamento de equipes

Visual sugerido:

Imagem ou ícones de mapa, entregas e pontos conectados.

Fala:

> Muitas decisões reais envolvem escolher a melhor ordem para visitar vários pontos. Uma rota ruim pode gerar atraso, maior custo e desperdício de recursos.

Tempo: 2 min

## Slide 3 - Delimitação do foco

Título:

Foco do projeto

Conteúdo:

- O foco é o Algoritmo Genético.
- O TSP é o problema usado como teste.
- A rota é a solução candidata.
- O gráfico mostra a evolução do AG.

Frase destaque:

> TSP = cenário aplicado. Algoritmo Genético = método central.

Fala:

> O Problema do Caixeiro Viajante foi escolhido porque permite visualizar bem todas as etapas do Algoritmo Genético: população, fitness, seleção, cruzamento, mutação e elitismo.

Tempo: 2 min

## Slide 4 - Problema trabalhado

Título:

Problema do Caixeiro Viajante

Conteúdo:

- Visitar todos os pontos uma única vez.
- Retornar ao ponto inicial.
- Minimizar a distância total.

Exemplo visual:

```text
0 -> 3 -> 5 -> 2 -> 1 -> 4 -> 0
```

Fala:

> Cada ordem de visita representa uma rota diferente. O objetivo é encontrar uma ordem que reduza a distância total percorrida.

Tempo: 2 min

## Slide 5 - Importância prática

Título:

Por que esse teste importa?

Conteúdo:

- Reduz distância percorrida.
- Reduz custo operacional.
- Ajuda no cumprimento de prazos.
- Melhora o planejamento de equipes.
- Apoia decisões em projetos reais.

Fala:

> Em um projeto real, uma rota mal planejada pode aumentar custos e dificultar a execução. Por isso, testar e comparar soluções antes de aplicar em escala maior é importante.

Tempo: 2 min

## Slide 6 - Crescimento combinatório

Título:

Por que não testar todas as rotas?

Conteúdo:

```text
Possibilidades aproximadas = (n - 1)!
```

Tabela:

| Pontos | Possibilidades |
| --- | --- |
| 10 | 9! = 362.880 |
| 20 | 19! = muito grande |
| 30 | 29! = inviável |

Fala:

> O número de possibilidades cresce muito rápido. Por isso, o Algoritmo Genético é usado como busca heurística, tentando encontrar boas soluções sem testar todas as rotas.

Tempo: 3 min

## Slide 7 - Modelagem matemática

Título:

Modelagem do problema

Conteúdo:

```text
V = {1, 2, 3, ..., n}
cidade i = (x_i, y_i)
d(i,j) = sqrt((x_i - x_j)^2 + (y_i - y_j)^2)
```

Função objetivo:

```text
Minimizar Z = soma das distâncias + retorno ao início
```

Fala:

> Cada cidade tem coordenadas. A distância entre duas cidades é calculada pela distância euclidiana. A função objetivo é minimizar a soma das distâncias da rota.

Tempo: 3 min

## Slide 8 - Como o AG representa a solução

Título:

Representação genética

Conteúdo:

| Conceito do AG | No projeto |
| --- | --- |
| Indivíduo | Uma rota |
| Gene | Uma cidade |
| Cromossomo | Sequência de cidades |
| População | Conjunto de rotas |
| Fitness | Distância total |

Exemplo:

```text
[0, 3, 5, 2, 1, 4]
```

Fala:

> O algoritmo não trabalha com uma única rota. Ele trabalha com uma população de rotas. Cada rota é uma possível solução para o problema.

Tempo: 3 min

## Slide 9 - Ciclo do Algoritmo Genético

Título:

Ciclo evolutivo aplicado

Conteúdo:

1. Criar população inicial.
2. Avaliar distância de cada rota.
3. Selecionar rotas melhores.
4. Cruzar rotas selecionadas.
5. Aplicar mutação.
6. Preservar elite.
7. Repetir por várias gerações.

Visual sugerido:

Fluxograma circular com as etapas.

Fala:

> Esse ciclo é repetido várias vezes. A cada geração, o algoritmo tenta melhorar a qualidade das rotas, mantendo boas soluções e criando novas combinações.

Tempo: 3 min

## Slide 10 - Operadores genéticos

Título:

Operadores usados no projeto

Conteúdo:

- Seleção por torneio
- Ordered Crossover
- Mutação por troca
- Elitismo

Fala:

> A seleção escolhe rotas melhores. O cruzamento combina partes de duas rotas. A mutação troca duas cidades de posição. O elitismo preserva a melhor rota encontrada.

Tempo: 3 min

## Slide 11 - Código essencial: distância

Título:

Cálculo da distância

Código para o slide:

```java
public double distanceTo(City other) {
    double deltaX = x - other.x;
    double deltaY = y - other.y;
    return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
}
```

Arquivo:

`src/main/java/br/com/genroute/model/City.java`

Fala:

> Esse trecho implementa a fórmula matemática da distância euclidiana dentro do código.

Tempo: 2 min

## Slide 12 - Código essencial: rota total

Título:

Função objetivo no código

Código para o slide:

```java
private double calculateDistance() {
    double total = 0.0;
    for (int index = 0; index < order.size(); index++) {
        City current = cities.get(order.get(index));
        City next = cities.get(order.get((index + 1) % order.size()));
        total += current.distanceTo(next);
    }
    return total;
}
```

Arquivo:

`src/main/java/br/com/genroute/model/Route.java`

Fala:

> Aqui a rota inteira é avaliada. O uso do módulo faz a última cidade voltar para a primeira.

Tempo: 3 min

## Slide 13 - Código essencial: evolução

Título:

Evolução das gerações

Código reduzido para o slide:

```java
for (int generation = 0; generation <= parameters.generations(); generation++) {
    population.sort(Comparator.comparingDouble(Route::distance));
    Route generationBest = population.get(0);

    if (generationBest.distance() < bestOverall.distance()) {
        bestOverall = generationBest;
    }

    bestDistanceHistory.add(bestOverall.distance());
    averageDistanceHistory.add(averageDistance(population));
    bestRouteHistory.add(bestOverall);

    population = nextGeneration(population, cities);
}
```

Arquivo:

`src/main/java/br/com/genroute/ga/GeneticAlgorithm.java`

Fala:

> Este é o coração do projeto. A população é ordenada pela distância, a melhor rota é guardada e o histórico é salvo para gerar o gráfico.

Tempo: 4 min

## Slide 14 - Interface e visualização

Título:

Visualização do processo

Conteúdo:

- Rota inicial.
- Melhor rota encontrada.
- Distância inicial.
- Distância final.
- Percentual de melhoria.
- Gráfico de evolução.

Visual sugerido:

Print da aplicação JavaFX ou demonstração ao vivo.

Fala:

> A visualização ajuda a entender o comportamento do Algoritmo Genético. Não vemos apenas o resultado final, mas também a evolução das gerações.

Tempo: 2 min

## Slide 15 - Testes com quantidades diferentes de cidades

Título:

Comparação dos cenários de teste

Tabela para o slide:

| Problema | Pontos | O que observar |
| --- | ---: | --- |
| cidades_10.csv | 10 | melhora rápida |
| cidades_20.csv | 20 | evolução mais visível |
| cidades_30.csv | 30 | maior dificuldade combinatória |

Fala:

> Conforme a quantidade de cidades aumenta, o espaço de busca cresce. Isso afeta o comportamento do gráfico e mostra por que o AG é útil.

Tempo: 2 min

## Slide 16 - Reação do gráfico

Título:

Como interpretar o gráfico?

Conteúdo:

- Queda da linha: rota melhor encontrada.
- Linha estável: melhorias ficaram mais difíceis.
- Melhor distância tende a diminuir ou ficar igual.
- Média da população pode variar.
- Mais cidades exigem mais busca.

Fala:

> O gráfico mostra a reação do algoritmo. Quando a distância cai, o AG encontrou uma solução melhor. Quando estabiliza, ele pode ter chegado perto de uma boa solução local.

Tempo: 3 min

## Slide 17 - Outros usos de Algoritmos Genéticos

Título:

Outras aplicações de AG

Conteúdo:

- Escala de funcionários.
- Montagem de horários.
- Planejamento de produção.
- Seleção de investimentos.
- Otimização de cortes.
- Ajuste de parâmetros.
- Estratégias em jogos.
- Roteirização com múltiplos veículos.

Fala:

> O mesmo raciocínio pode ser aplicado em outros problemas com muitas combinações possíveis. O ponto central é buscar boas soluções sem testar tudo.

Tempo: 2 min

## Slide 18 - Referências e fundamentação

Título:

Base teórica

Conteúdo:

- Holland: adaptação em sistemas naturais e artificiais.
- Goldberg: AG em busca, otimização e aprendizado.
- Materiais introdutórios sobre AG.
- TSPLIB como referência do problema de teste.
- JavaFX como tecnologia visual.

Fala:

> As referências principais sustentam o Algoritmo Genético. As referências do TSP e JavaFX servem como apoio para o problema aplicado e a visualização.

Tempo: 2 min

## Slide 19 - Conclusão

Título:

Conclusão

Conteúdo:

- O AG foi aplicado a um problema de otimização combinatória.
- Cada rota foi tratada como indivíduo.
- A distância foi usada como aptidão.
- O algoritmo evoluiu soluções por gerações.
- O gráfico mostrou a melhoria da solução.
- O projeto relacionou teoria, código e visualização.

Fala:

> O projeto mostra que o Algoritmo Genético pode ser usado para encontrar boas soluções em problemas com muitas combinações. O TSP foi usado como cenário, mas o foco foi demonstrar o funcionamento do AG.

Tempo: 2 min

## Slide 20 - Perguntas

Título:

Perguntas

Conteúdo:

- O algoritmo sempre encontra a melhor rota?
- Por que usar mutação?
- Por que não testar todas as rotas?
- Como o gráfico deve ser interpretado?

Fala:

> Essas são algumas perguntas comuns sobre o projeto e sobre o comportamento do Algoritmo Genético.

Tempo: 2 min

## Versão reduzida para 30 minutos

Se precisar reduzir a apresentação, use estes slides:

1. Título
2. Contextualização
3. Delimitação do foco
4. Problema trabalhado
6. Crescimento combinatório
7. Modelagem matemática
8. Representação genética
9. Ciclo do Algoritmo Genético
10. Operadores genéticos
11. Código: distância
12. Código: rota total
13. Código: evolução
14. Interface e visualização
15. Testes com cidades diferentes
16. Reação do gráfico
17. Outros usos
19. Conclusão

## Observações para montagem visual

- Use pouco texto por slide.
- Deixe as falas completas no campo de anotações do apresentador.
- Use prints do aplicativo nos slides 14, 15 e 16.
- Use o código somente nos slides 11, 12 e 13.
- Use a modelagem matemática em fonte grande no slide 7.
- No slide 3, destaque em negrito: "o foco é o Algoritmo Genético".
- Evite transformar a apresentação em uma explicação apenas de logística.
