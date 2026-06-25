# Roteiro de apresentação - GenRoute GA

Tempo sugerido: 35 minutos

Tema: Algoritmo Genético aplicado à otimização de rotas

Projeto: GenRoute GA - Problema do Caixeiro Viajante em Java

Autor: Iury Figueiredo Lopes

## 1. Abertura e contextualização - 3 minutos

Fala sugerida:

> Neste seminário eu vou apresentar o GenRoute GA, um projeto desenvolvido em Java para demonstrar como um Algoritmo Genético pode ser usado na otimização de rotas. Antes de falar diretamente do algoritmo, é importante entender o contexto: muitas decisões do dia a dia envolvem escolher a melhor ordem para visitar vários pontos.

Exemplos para citar:

- uma empresa que faz entregas em vários endereços;
- um técnico que precisa visitar clientes em bairros diferentes;
- uma equipe de coleta que precisa passar por vários locais;
- uma transportadora que precisa reduzir tempo, distância e combustível;
- uma equipe de vendas que precisa organizar visitas durante o dia.

Ponto principal:

> O problema não é apenas chegar a um lugar. O problema é decidir a melhor ordem de visita entre vários lugares.

Transição:

> Quando existem poucos pontos, até conseguimos pensar manualmente em uma boa rota. Mas, conforme a quantidade de cidades aumenta, o número de combinações cresce muito rápido.

## 2. Justificativa para criação do projeto - 4 minutos

Fala sugerida:

> A justificativa para criar este projeto é mostrar uma aplicação prática da Matemática Computacional. O Algoritmo Genético não fica apenas como teoria. Ele é usado para resolver um problema realista, visual e fácil de entender: reduzir a distância de uma rota.

Motivos para o projeto existir:

- aproxima teoria e prática;
- permite visualizar a evolução da solução;
- mostra a dificuldade da otimização combinatória;
- usa uma situação real de logística;
- demonstra como matemática, programação e gráfico podem trabalhar juntos;
- serve como base para problemas maiores, como roteirização de veículos.

Fala para destacar:

> O projeto foi criado porque o Problema do Caixeiro Viajante é simples de explicar, mas muito forte matematicamente. Ele permite mostrar por que testar todas as possibilidades pode ser inviável e por que técnicas heurísticas, como Algoritmos Genéticos, são úteis.

## 3. Apresentação do problema - 5 minutos

Problema trabalhado:

> O problema central é o Problema do Caixeiro Viajante, ou TSP. Dado um conjunto de cidades, queremos encontrar a menor rota que visite todas exatamente uma vez e volte ao ponto inicial.

Exemplo para falar:

> Imagine que o ponto 0 é o depósito. A empresa precisa sair do depósito, visitar todos os clientes e voltar ao depósito. A pergunta é: em qual ordem os clientes devem ser visitados para reduzir a distância total?

Representação de uma rota:

```text
[0, 3, 5, 2, 1, 4]
```

Explicação:

```text
Ponto 0 -> Ponto 3 -> Ponto 5 -> Ponto 2 -> Ponto 1 -> Ponto 4 -> Ponto 0
```

Importância prática:

- reduz distância percorrida;
- reduz gasto com combustível;
- reduz tempo de atendimento;
- melhora organização da equipe;
- ajuda na execução de projetos com prazos e deslocamentos;
- permite comparar decisões por indicadores, como distância inicial, distância final e melhoria percentual.

Fala sugerida:

> Em projetos reais, uma rota ruim não é apenas um detalhe técnico. Ela pode gerar atraso, aumentar custo e prejudicar a execução da operação. Por isso, testar e comparar rotas é importante antes de aplicar uma solução em escala maior.

## 4. Modelagem matemática para escrever no quadro - 6 minutos

Use esta parte no quadro enquanto explica.

### 4.1 Conjunto de pontos

Escrever:

```text
V = {1, 2, 3, ..., n}
```

Explicação:

> V é o conjunto de pontos ou cidades. Cada ponto representa um cliente, uma entrega ou uma parada.

### 4.2 Coordenadas

Escrever:

```text
cidade i = (x_i, y_i)
```

Explicação:

> No projeto, cada cidade tem coordenadas x e y. Essas coordenadas permitem calcular a distância entre duas cidades.

### 4.3 Distância euclidiana

Escrever:

```text
d(i,j) = sqrt((x_i - x_j)^2 + (y_i - y_j)^2)
```

Fala:

> Essa fórmula calcula a distância em linha reta entre dois pontos. No código, ela aparece dentro da classe City.

Código para mostrar:

Arquivo: `src/main/java/br/com/genroute/model/City.java`

```java
public double distanceTo(City other) {
    double deltaX = x - other.x;
    double deltaY = y - other.y;
    return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
}
```

### 4.4 Função objetivo

Escrever:

```text
Minimizar Z = d(r1,r2) + d(r2,r3) + ... + d(rn,r1)
```

Ou:

```text
Z = soma das distâncias entre pontos consecutivos + retorno ao início
```

Explicação:

> A função objetivo é minimizar a distância total da rota. A última cidade também precisa voltar para a primeira.

Código para mostrar:

Arquivo: `src/main/java/br/com/genroute/model/Route.java`

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

Ponto importante:

> O uso de `(index + 1) % order.size()` faz a última cidade se conectar novamente com a primeira.

### 4.5 Crescimento combinatório

Escrever:

```text
Possibilidades aproximadas = (n - 1)!
```

Explicação:

> Como o ponto inicial fica fixo no projeto, a quantidade de ordens possíveis pode ser vista como aproximadamente `(n - 1)!`. Isso cresce muito rápido.

Tabela para escrever no quadro:

| Pontos | Possibilidades aproximadas com ponto inicial fixo |
| --- | --- |
| 10 | 9! = 362.880 |
| 20 | 19! = número extremamente grande |
| 30 | 29! = inviável testar manualmente |

Fala:

> Essa tabela justifica o uso de Algoritmo Genético. O objetivo não é testar tudo, mas encontrar uma boa solução em tempo viável.

### 4.6 Melhoria percentual

Escrever:

```text
Melhoria (%) = ((distância inicial - distância final) / distância inicial) * 100
```

Explicação:

> Essa fórmula ajuda a transformar o resultado em uma informação fácil de entender. Em vez de olhar só os números absolutos, conseguimos dizer quanto a rota melhorou.

## 5. Explicação do Algoritmo Genético no projeto - 6 minutos

Fala:

> No Algoritmo Genético, cada rota é tratada como um indivíduo. A população é um conjunto de rotas. O algoritmo gera rotas aleatórias, avalia as melhores, combina rotas boas e aplica pequenas mutações para buscar novas possibilidades.

Fala mais completa para explicar a aplicação no projeto:

> Neste projeto, o Algoritmo Genético está sendo aplicado diretamente sobre o Problema do Caixeiro Viajante. Cada possível solução do problema é uma rota. Então, em vez de o algoritmo trabalhar com números soltos, ele trabalha com sequências de cidades. Por exemplo, uma rota como `[0, 3, 5, 2, 1, 4]` representa uma solução candidata.

> A primeira etapa é criar uma população inicial. Essa população é formada por várias rotas aleatórias. Isso significa que o algoritmo começa com várias tentativas diferentes de resolver o problema, mesmo que essas tentativas ainda não sejam boas.

> Depois disso, cada rota é avaliada pela distância total percorrida. Quanto menor a distância da rota, melhor é aquela solução. Essa avaliação funciona como a função de aptidão, ou fitness, do Algoritmo Genético. No nosso caso, uma rota é considerada melhor quando percorre uma distância menor.

> Em cada geração, o algoritmo seleciona rotas melhores para gerar novas rotas. A seleção usada no projeto é a seleção por torneio. Ela escolhe algumas rotas da população e fica com a melhor entre elas. Isso faz com que boas soluções tenham mais chance de continuar no processo, mas sem eliminar totalmente a diversidade.

> Depois da seleção, acontece o cruzamento. O cruzamento combina partes de duas rotas para criar uma nova rota. Como estamos trabalhando com cidades, não podemos repetir uma cidade nem deixar alguma cidade de fora. Por isso o projeto usa Ordered Crossover, que mantém a ordem das cidades e evita rotas inválidas.

> Em seguida, pode ocorrer a mutação. A mutação troca duas cidades de posição dentro da rota. Ela é importante porque cria variação. Sem mutação, o algoritmo poderia ficar preso em rotas muito parecidas e parar de melhorar cedo.

> O projeto também usa elitismo. Isso significa que as melhores rotas de uma geração são preservadas para a próxima. Assim, se o algoritmo já encontrou uma boa rota, ele não perde essa solução nas próximas gerações.

> Ao longo das gerações, o algoritmo vai repetindo esse ciclo: avaliar rotas, selecionar as melhores, cruzar, mutar e preservar a elite. O resultado esperado é que a distância total da melhor rota diminua com o tempo. É exatamente isso que aparece no gráfico da interface.

Tabela para mostrar na fala:

| Conceito | No projeto |
| --- | --- |
| Indivíduo | Uma rota completa |
| Gene | Um ponto/cidade da rota |
| População | Conjunto de várias rotas |
| Fitness | Qualidade baseada na distância |
| Seleção | Torneio entre rotas |
| Cruzamento | Ordered Crossover |
| Mutação | Troca de duas cidades |
| Elitismo | Preserva melhores rotas |

Código essencial para mostrar:

Arquivo: `src/main/java/br/com/genroute/ga/GeneticAlgorithm.java`

```java
public GeneticResult solve(List<City> cities) {
    List<Route> population = createInitialPopulation(cities);
    Route initialRoute = population.get(0);
    Route bestOverall = initialRoute;

    List<Double> bestDistanceHistory = new ArrayList<>();
    List<Double> averageDistanceHistory = new ArrayList<>();
    List<Route> bestRouteHistory = new ArrayList<>();

    for (int generation = 0; generation <= parameters.generations(); generation++) {
        population.sort(Comparator.comparingDouble(Route::distance));
        Route generationBest = population.get(0);

        if (generationBest.distance() < bestOverall.distance()) {
            bestOverall = generationBest;
        }

        bestDistanceHistory.add(bestOverall.distance());
        averageDistanceHistory.add(averageDistance(population));
        bestRouteHistory.add(bestOverall);

        if (generation == parameters.generations()) {
            break;
        }

        population = nextGeneration(population, cities);
    }

    return new GeneticResult(
            initialRoute,
            bestOverall,
            bestDistanceHistory,
            averageDistanceHistory,
            bestRouteHistory,
            parameters);
}
```

Como explicar esse código:

- cria população inicial;
- pega uma rota inicial para comparação;
- ordena a população pela menor distância;
- guarda a melhor rota de cada geração;
- guarda a média da população;
- cria a próxima geração;
- retorna os dados para a interface mostrar o gráfico.

Fala:

> Esse método é o coração do projeto. Ele conecta a parte matemática com a visualização, porque guarda o histórico das distâncias. Sem esse histórico, eu teria apenas o resultado final, mas não conseguiria mostrar a evolução no gráfico.

Fala curta, caso precise resumir:

> Resumindo: o Algoritmo Genético começa com várias rotas aleatórias, mede a distância de cada uma, escolhe as melhores, combina partes dessas rotas, aplica pequenas alterações e repete o processo por várias gerações. No final, ele retorna a melhor rota encontrada e o histórico usado para montar o gráfico.

## 6. Operadores genéticos essenciais - 4 minutos

### 6.1 Seleção por torneio

Código:

```java
private Route tournamentSelection(List<Route> population) {
    Route best = null;
    for (int index = 0; index < parameters.tournamentSize(); index++) {
        Route candidate = population.get(random.nextInt(population.size()));
        if (best == null || candidate.distance() < best.distance()) {
            best = candidate;
        }
    }
    return best;
}
```

Fala:

> A seleção por torneio escolhe algumas rotas aleatórias e fica com a melhor entre elas. Isso dá vantagem às rotas boas, mas ainda mantém diversidade.

### 6.2 Cruzamento

Fala:

> O cruzamento combina partes de duas rotas. No TSP, é preciso cuidado para não repetir cidades. Por isso foi usado Ordered Crossover, que preserva um trecho de um pai e completa o restante com a ordem do outro.

Código para mostrar rapidamente:

```java
List<Integer> childOrder = orderedCrossover(parentA.order(), parentB.order());
```

### 6.3 Mutação

Código:

```java
private void mutate(List<Integer> order) {
    if (random.nextDouble() > parameters.mutationRate()) {
        return;
    }

    int first = 1 + random.nextInt(order.size() - 1);
    int second = 1 + random.nextInt(order.size() - 1);
    while (first == second) {
        second = 1 + random.nextInt(order.size() - 1);
    }

    int temp = order.get(first);
    order.set(first, order.get(second));
    order.set(second, temp);
}
```

Fala:

> A mutação troca duas cidades de lugar. Ela é importante porque cria variação e ajuda o algoritmo a escapar de rotas ruins ou repetidas.

Ponto para destacar:

> O índice começa em 1 porque o ponto 0 fica fixo como ponto inicial.

## 7. Demonstração prática no aplicativo - 6 minutos

Comando para abrir:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\run.ps1
```

Ordem da demonstração:

1. Abrir o aplicativo.
2. Selecionar `cidades_10.csv`.
3. Executar o algoritmo.
4. Mostrar rota inicial, rota final, distância inicial, distância final e melhoria.
5. Observar o gráfico.
6. Repetir com `cidades_20.csv`.
7. Repetir com `cidades_30.csv`.

Fala enquanto mostra o gráfico:

> A linha do gráfico mostra a distância melhorando ao longo das gerações. No início, a queda costuma ser mais forte, porque o algoritmo encontra melhorias evidentes. Depois, a curva tende a estabilizar, porque as melhorias ficam menores e mais difíceis.

Fala pronta durante a execução:

> Neste momento, o algoritmo está executando várias gerações. Em cada geração, ele compara as rotas da população, guarda a melhor, seleciona rotas promissoras, faz cruzamento entre elas e aplica mutação em algumas soluções. A rota desenhada na tela representa a melhor solução encontrada até aquele momento.

> Quando o gráfico desce, significa que o algoritmo encontrou uma rota com distância menor. Quando ele fica mais estável, não significa que o algoritmo parou de funcionar. Significa que ele já encontrou boas rotas e agora as melhorias são menores e mais difíceis.

> A diferença entre testar 10, 20 e 30 cidades é importante. Com mais cidades, existem muito mais combinações possíveis. Por isso, o gráfico pode demorar mais para estabilizar ou apresentar uma evolução mais gradual.

Tabela para preencher durante a execução:

| Problema | Pontos | Distância inicial | Distância final | Melhoria | Reação do gráfico |
| --- | ---: | ---: | ---: | ---: | --- |
| cidades_10.csv | 10 | preencher ao vivo | preencher ao vivo | preencher ao vivo | queda rápida |
| cidades_20.csv | 20 | preencher ao vivo | preencher ao vivo | preencher ao vivo | queda com mais variação |
| cidades_30.csv | 30 | preencher ao vivo | preencher ao vivo | preencher ao vivo | convergência mais lenta |

Como explicar a reação do gráfico:

- com 10 cidades, o problema é menor, então o algoritmo encontra boas rotas rapidamente;
- com 20 cidades, há mais combinações, então o gráfico pode oscilar mais na média da população;
- com 30 cidades, o espaço de busca é muito maior, então a evolução tende a precisar de mais gerações;
- a melhor distância normalmente diminui ou fica estável, porque o elitismo preserva a melhor solução;
- a média da população pode variar, porque novas rotas são criadas por cruzamento e mutação.

Fala importante:

> O gráfico não serve só para deixar a apresentação bonita. Ele mostra o comportamento do algoritmo. Se a curva para de melhorar muito cedo, talvez seja necessário aumentar gerações, população ou ajustar a taxa de mutação.

## 8. Outros meios em que Algoritmos Genéticos podem ser usados - 4 minutos

Fala:

> Apesar de o projeto usar rotas, Algoritmos Genéticos não se limitam ao TSP. Eles podem ser aplicados em problemas em que existe um conjunto grande de combinações e queremos encontrar uma solução boa.

Exemplos:

- montagem de horários escolares ou universitários;
- escala de funcionários;
- alocação de salas;
- planejamento de produção;
- otimização de cortes de materiais;
- seleção de investimentos;
- ajuste de parâmetros em sistemas;
- criação de estratégias em jogos;
- design de peças ou estruturas;
- roteirização com vários veículos;
- problemas de entrega com restrições de horário;
- escolha de caminhos em redes.

Fala:

> O ponto comum entre esses exemplos é que todos envolvem muitas possibilidades. O Algoritmo Genético é útil quando testar todas as combinações seria caro ou demorado demais.

## 9. Fechamento - 3 minutos

Resumo para falar:

> O GenRoute GA mostra como um problema real pode ser modelado matematicamente, implementado em Java e visualizado de forma gráfica. O projeto usa o Problema do Caixeiro Viajante para demonstrar a importância da otimização de rotas em logística, entregas e planejamento de serviços.

Pontos finais:

- o problema é simples de entender, mas difícil de resolver por força bruta;
- o Algoritmo Genético busca boas soluções por evolução;
- a visualização ajuda a entender a melhoria da rota;
- o gráfico mostra a reação do algoritmo ao longo das gerações;
- quanto mais cidades, maior a dificuldade combinatória;
- a técnica pode ser expandida para problemas reais mais complexos.

Frase de encerramento:

> A principal ideia do projeto é mostrar que a Matemática Computacional pode apoiar decisões práticas. Em vez de testar todas as rotas possíveis, o Algoritmo Genético busca uma rota eficiente e permite acompanhar a evolução da solução de forma visual.

## 10. Ordem recomendada de arquivos para abrir no VS Code

1. `data/problemas/cidades_10.csv`
2. `src/main/java/br/com/genroute/model/City.java`
3. `src/main/java/br/com/genroute/model/Route.java`
4. `src/main/java/br/com/genroute/ga/GeneticAlgorithm.java`
5. `src/main/java/br/com/genroute/ga/GeneticResult.java`
6. `src/main/java/br/com/genroute/visual/RouteCanvas.java`
7. `src/main/java/br/com/genroute/App.java`

## 11. Cola rápida para perguntas

Pergunta: O algoritmo sempre encontra a melhor rota?

Resposta:

> Não necessariamente. Ele busca uma boa solução, muitas vezes próxima do ótimo, mas não garante o ótimo global em todos os casos.

Pergunta: Por que não testar todas as rotas?

Resposta:

> Porque o número de combinações cresce muito rápido. Com o ponto inicial fixo, temos aproximadamente `(n - 1)!` possibilidades.

Pergunta: Por que usar mutação?

Resposta:

> Para criar variação e evitar que a população fique presa em soluções parecidas.

Pergunta: Por que o gráfico às vezes estabiliza?

Resposta:

> Porque o algoritmo já encontrou boas soluções e as melhorias restantes ficam mais difíceis. Isso é comum em processos de otimização.

Pergunta: Qual é a relação com projetos reais?

Resposta:

> Projetos com entrega, transporte, coleta ou visitas técnicas dependem de planejamento de rotas. Melhorar a rota pode reduzir custo, tempo e desperdício.

## 12. Plano de tempo resumido

| Parte | Tempo |
| --- | ---: |
| Abertura e contextualização | 3 min |
| Justificativa do projeto | 4 min |
| Problema trabalhado | 5 min |
| Modelagem matemática no quadro | 6 min |
| Algoritmo Genético no código | 6 min |
| Operadores genéticos | 4 min |
| Demonstração no aplicativo | 6 min |
| Outros usos e fechamento | 5 min |
| Total | 39 min |

Se precisar reduzir para 30 minutos, corte exemplos secundários e faça apenas dois testes no app: `cidades_10.csv` e `cidades_30.csv`.
