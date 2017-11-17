# Wilcoxon Rank Sum Test
#
# É a versão não paramétrica do teste de hipótese t-test. Assim, para comparar medidas de valores
# contínuos é recomendado o uso do testes Wilcoxon.
#
# Para o teste Wilcoxon as premissas são de quqe  variância é igual e existe indepêndencia entre 
# os grupos amostrais. Esse teste não assume que os dois grupos amostrais tenham uma distribuição
# conhecida, como as distribuição são fórmulas matemáticas e essas fórmulas parâmetras possuem
# parâmetros de configuração isso faz com que o Wilcoxon por não usar fórmulas de distribuição
# seja tido como teste não paramétrico.
#
# A hipótese nula para o teste Wilcoxon é de que a mediana é igual para ambos os grupos, outra
# forma de pensa na hipótese nula é de que os dois grupos possuem distribuições com de mesma
# mediana. Quando ignoramos a hipótese nula estamos assumindado que as duas distribuições são
# diferentes e de que uma distribuição está a direita ou esquerda da outra distribuição.
#
# O exemplo a seguir considera o peso da embalagem de duas companias diferentes competindo com
# o mesmo produto. Existem 8 observações para cada uma das companias A e B. Nós gostariamos de
# saber se a distribuição de peso é a mesma para ambas as companias. Um rápido box plot revela
# de que a dispersão dos dados é similar mas pode ser inclinada e não normal. Assim com uma pequena
# amostra pode ser perigoso assumir normalidade.

A <- c(117.1, 121.3, 127.8, 121.9, 117.4, 124.5, 119.5, 115.1)
B <- c(123.5, 125.3, 126.5, 127.9, 122.1, 125.6, 129.8, 117.2)
dat <- data.frame(weight = c(A,B), 
                  company = rep(c("A","B"), each=8))
boxplot(weight ~ company, data = dat)

# Agora vamos rodar o teste Wilcoxon Rank Sum Test usando a fórmula wilcox.test. Novamente, a hipótese
# nula é de que as distribuições são iguais e com mesma mediana. A alternativa é de dois lados, ou seja,
# não temos ideia se uma das distribuição está à direita ou à esquerda da outra.

wilcox.test(weight ~ company, data = dat)

# Primeiramente, o valor de p-value é menor que 0.05. Baseado nisso podemos concluir que a mediana
# entre essas duas distribuições é diferente. A hipótese alternativa é de que o deslocamento entre
# as distribuições não é zero. Dizendo de outro jeito, a distribuição de uma das populações está
# deslocada a direita ou à esquerda da outra distribuição, que implica, em medianas diferentes.
# A estatística W de Wilcoxon retornada foi de 13. Isto NÃO é uma estimativa da diferença entre
# as medianas. Este é na verdade a quantidade de vezes que o peso de um pacote da empresa B foi
# menor que o peso de um pacote da empresa A, podemos calcular isso na mão também (não é como 
# Wilcoxon faz):

W <- 0
for(i in 1:length(B)) {
    for(j in 1:length(A)) {
        if(B[i] < A[j]) W <- W + 1
    }
}
W

# Outra forma de executar a mesma tarefa é usando a função outer que retorna uma matrix de NxN
# de true e false, e usando a soma de matrizes temos:

sum(outer(B, A, "<"))

# Claro, podemos fazer a comparação inversa também

sum(outer(A, B, "<"))

# Neste caso se nós renivelarmos nossas variáveis do exemplo anterior veremos o mesmo resultado
dat$company <- relevel(dat$company, ref = "B")
wilcox.test(weight ~ company, data = dat)

# Por que contar pares? Como esse é um teste não paramétrico, não estamos estimando parâmetros como
# a média. Estamos unicamente tentando provar que uma das distribuição está deslocada para direita
# ou para a esquerda da outra. Outra forma de pensar se as distribuições são as mesmas é considerar
# a probabilidade de selecionarmos randomicamente um valor da compania A que é menor do que de
# selecionar um valor randomicamente da compania B, P(A < B). Nós podemos estimar essa probabilidade
# como o número de pares de A que é menor que B divido pelo número total de pares. Que no nosso caso
# é dado por 51/(8x8) ou 51/64, ou também, a probabilidade de que B < A que é dado por 13/64. Assim
# vemos que a estatística W é o númerador na estimativa de probabilidade.

# O valor exato do p-value é determinado da distribuição Wilcoxon Rank Sum Statistics. Nós dizemos
# "exato" porque a distribuições de Wolcoxon Rank Sum Statistics é discreta. Ela é parâmetrizada
# pelos dois tamanhos das amostras que estamos comparando.

# Para W = 13, P(W <= 13)
pwilcox(q = 13, m = 8, n = 8) * 2

# Para W = 51, P(W >= 51), nós temos de pegar P(W <= 50) e subtrair 1 para ter P(W >= 51)
(1 - pwilcox(q = 51 - 1, m = 8, n = 8)) * 2

# Por padrão wilcox.test irá calcular o valor exato do p-value se a distribuição contém menos de 50
# valores finitos e não há amarrações (ties) nos valores. Caso contrário uma aproximação normal é 
# usada. Para forcar a aproximação normal use exact = FALSE

dat$company <- relevel(dat$company, ref = "A")
wilcox.test(weight ~ company, data = dat, exact = FALSE)

# Quando usamos uma aproximação normal a prase "Wilcoxon rank sum test with continuity correction" 
# é adicionada ao nome do teste. A correção de continuidade é um ajuste feito quando uma distribuição
# discreta é aproximada por um distribuição contínua. A aproximação é muito boa e computacionalmente
# mais rápida para amostras maiores do que 50.

# Agora falando sobre as "amarrações (ties)". O que faz e significa? Primeiro vamos considerar o nome
# "Wilcoxon Rank Sum test". O nome é devido ao fato do teste ser calculado como a soma dos ranks dos
# valores. Em outras palavras, tomando todos os valores de ambos os grupos, ordene os valores do menor
# para o maior de acordo com o valor, e então some os valores dos ranks para cada um dos grupos.
# Podemos fazer isso em R da seguinte forma:

sum(rank(dat$weight)[dat$company == "A"])

# Acima foram rankeados todos os valores, selecionados todos os valores somente da compania A e somados.
# Esta é a forma clássica de calcular "Wilcoxon Rank Sum test statistic". Note que a estatística
# não bate com o valor do teste, que foi 13. Isso porque R está usando uma função diferente dado
# por Mann e Whitney. Essa estatística, algumas vezes chamada de U, é uma função linear da soma
# original do rank da estatística, usalmente chamada de W
#
# U = W - ((n2*(n2 - 1)) / 2)
#
# Onde n2 é o número de observações no outro grupo onde os ranks não foram somados. Podemos verificar
# esse relacionamento para nossos dados:

sum(rank(dat$weight)[dat$company == "A"]) - (8*9/2)

# Essa é a forma de fato de como Wilcox.test calcula a estatísca de teste, embore ele nomeie como W
# ao invés de U.

# O rank dos valores deve ser modificado na eventualidade de amarrações (ties). Por exemplo, nos dados
# a seguir existem duas aparições do valor 7. Poderiamos ranker 7 como 3 e 4. Mas um está sendo
# rankeado maior do que o outro e isso não é correto, poderiamos rankear os dois como 3 ou 4 porém
# estariamos deixando o seu rank maior ou menor em relação a outros valores. A melhor forma de tratar
# isso é usando a média (3+4)/2 = 3.5, R faz isso automaticamente

vals <- c(2, 4, 7, 7, 12)
rank(vals)

# O impacto das amarrações significa que Wilcoxon test não pode ser usado para calcular os valores
# exados do p-value. Assim se temos menos de 50 dados e amarrações ocorrem, a função wilcox.test
# retorna uma aproximação normal do p-value junto de uma mensagem de aviso “cannot compute exact 
# p-value with ties”.

# Se exato ou aproximado, o p-value não nos diz nada sobre o quão diferente essas duas distribuições
# são. Para o teste Wilcoxon, o p-value nos dá a distribuição de probabilidade de pegar um valor
# menor ou maior de um grupo em relação ao outro. Em adição ao teste estatística podemos estimar
# alguma medida de quão diferente são as duas distribuições. A função wilcox.test prove alguns
# parãmetros que nos permite fazer isso

wilcox.test(weight ~ company, data = dat, conf.int = TRUE)

# Este nos retorna uma "diferença na localização" medida como -4.65. A documentação do wilcox.test
# diz que essa diferença não representa a diferença entre as medianas, mas sim a mediana da diferença
# entre tomar uma amostra de x e uma amostra de y.

# Novamente podemos calcular esse valor usando a função outer. Primeiro nós calculamos a diferença
# entre todos os pares e então calculamos a mediana entre todas as diferenças.

median(outer(A, B, "-"))

# O intervalo de confiança é bastante amplo devido ao pequeno número de amostras, mas parece que 
# podemos dizer com segurança que o peso médio das embalagens da empresa A é pelo menos -0,1 
# menos do que o peso médio das embalagens da empresa B.