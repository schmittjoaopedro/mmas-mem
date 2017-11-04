library(plotly)

data <- read.csv(file = "/home/joao/projects/master-degree/aco-dynamic-tsp-algorithm/output/MMAS.csv")
dataMem <- read.csv(file = "/home/joao/projects/master-degree/aco-dynamic-tsp-algorithm/output/MMAS-MEM.csv")

data$mean2 <- dataMem$mean
plot_ly(data, x = ~iteration, y = ~mean, type = "scatter", mode = "lines", name = "MMAS") %>%
    add_trace(y = ~mean2, name = "MMAS-MEM", mode = "lines")

data$best2 <- dataMem$best
plot_ly(data, x = ~iteration, y = ~best, type = "scatter", mode = "lines", name = "MMAS") %>%
    add_trace(y = ~best2, name = "MMAS-MEM", mode = "lines")

data$worst2 <- dataMem$worst
plot_ly(data, x = ~iteration, y = ~worst, type = "scatter", mode = "lines", name = "MMAS") %>%
    add_trace(y = ~worst2, name = "MMAS-MEM", mode = "lines")

data$bsf2 <- dataMem$bsf
plot_ly(data, x = ~iteration, y = ~bsf, type = "scatter", mode = "lines", name = "MMAS") %>%
    add_trace(y = ~bsf2, name = "MMAS-MEM", mode = "lines")

data$bsfAdj2 <- dataMem$bsfAdj
plot_ly(data, x = ~iteration, y = ~bsfAdj, type = "scatter", mode = "lines", name = "MMAS") %>%
    add_trace(y = ~bsfAdj2, name = "MMAS-MEM", mode = "lines")

data$div2 <- dataMem$div
plot_ly(data, x = ~iteration, y = ~div, type = "scatter", mode = "lines", name = "MMAS") %>%
    add_trace(y = ~div2, name = "MMAS-MEM", mode = "lines")