library(plotly)

pathName = "/home/joao/projects/master-degree/aco-dynamic-tsp-algorithm/output/"
problem = "46" # 46, 78, 125
mag = "0.75" # 0.1, 0.5, 0.75
freq = "500" # 500, 1000

data <- read.csv(file = paste(pathName, "MMAS_TSP-", problem, "_MAG-", mag, "_FREQ-", freq, "_PERIOD-4.csv", sep = ""))
dataMem <- read.csv(file = paste(pathName, "MMAS_MEM_TSP-", problem, "_MAG-", mag, "_FREQ-", freq, "_PERIOD-4.csv", sep = ""))
dataMiaco <- read.csv(file = paste(pathName, "MIACO_TSP-", problem, "_MAG-", mag, "_FREQ-", freq, "_PERIOD-4.csv", sep = ""))

data$bsfAdj2 <- dataMem$bsfAdj
data$bsfAdj3 <- dataMiaco$bsfAdj
plot_ly(data, x = ~iteration, y = ~bsfAdj, type = "scatter", mode = "lines", name = "MMAS") %>%
    add_trace(y = ~bsfAdj2, name = "MMAS_MEM", mode = "lines") %>%
    add_trace(y = ~bsfAdj3, name = "MIACO", mode = "lines")

data$bsf2 <- dataMem$bsf
data$bsf3 <- dataMiaco$bsf
plot_ly(data, x = ~iteration, y = ~bsf, type = "scatter", mode = "lines", name = "MMAS") %>%
    add_trace(y = ~bsf2, name = "MMAS_MEM", mode = "lines") %>%
    add_trace(y = ~bsf3, name = "MIACO", mode = "lines")

data$mean2 <- dataMem$mean
data$mean3 <- dataMiaco$mean
plot_ly(data, x = ~iteration, y = ~mean, type = "scatter", mode = "lines", name = "MMAS") %>%
    add_trace(y = ~mean2, name = "MMAS_MEM", mode = "lines") %>%
    add_trace(y = ~mean3, name = "MIACO", mode = "lines")

data$best2 <- dataMem$best
data$best3 <- dataMiaco$best
plot_ly(data, x = ~iteration, y = ~best, type = "scatter", mode = "lines", name = "MMAS") %>%
    add_trace(y = ~best2, name = "MMAS_MEM", mode = "lines") %>%
    add_trace(y = ~best3, name = "MIACO", mode = "lines")

data$worst2 <- dataMem$worst
data$worst3 <- dataMiaco$worst
plot_ly(data, x = ~iteration, y = ~worst, type = "scatter", mode = "lines", name = "MMAS") %>%
    add_trace(y = ~worst2, name = "MMAS_MEM", mode = "lines") %>%
    add_trace(y = ~worst3, name = "MIACO", mode = "lines")

data$div2 <- dataMem$div
data$div3 <- dataMiaco$div
plot_ly(data, x = ~iteration, y = ~div, type = "scatter", mode = "lines", name = "MMAS") %>%
    add_trace(y = ~div2, name = "MMAS_MEM", mode = "lines") %>%
    add_trace(y = ~div3, name = "MIACO", mode = "lines")


#####################################


printStats <- function(problem, mag, freq) {
    pathName = "/home/joao/projects/master-degree/aco-dynamic-tsp-algorithm/output/"
    
    data <- read.csv(file = paste(pathName, "MMAS_TSP-", problem, "_MAG-", mag, "_FREQ-", freq, "_PERIOD-4.csv", sep = ""))
    dataMem <- read.csv(file = paste(pathName, "MMAS_MEM_TSP-", problem, "_MAG-", mag, "_FREQ-", freq, "_PERIOD-4.csv", sep = ""))
    dataMiaco <- read.csv(file = paste(pathName, "MIACO_TSP-", problem, "_MAG-", mag, "_FREQ-", freq, "_PERIOD-4.csv", sep = ""))
    
    print(paste("Problem", problem, "Magnitude", mag, "Frequency", freq))
    print(paste("MMAS     mean", mean(data$bsfAdj), "sd", sd(data$bsfAdj)))
    print(paste("MMAS_MEM mean", mean(dataMem$bsfAdj), "sd", sd(dataMem$bsfAdj)))
    print(paste("MIACO    mean", mean(dataMiaco$bsfAdj), "sd", sd(dataMiaco$bsfAdj)))
}

# 46, 78, 125
# 0.1, 0.5, 0.75
# 500, 1000
printStats("46","0.1","500")
printStats("46","0.1","1000")
printStats("46","0.5","500")
printStats("46","0.5","1000")
printStats("46","0.75","500")
printStats("46","0.75","1000")

printStats("78","0.1","500")
printStats("78","0.1","1000")
printStats("78","0.5","500")
printStats("78","0.5","1000")
printStats("78","0.75","500")
printStats("78","0.75","1000")

printStats("125","0.1","500")
printStats("125","0.1","1000")
printStats("125","0.5","500")
printStats("125","0.5","1000")
printStats("125","0.75","500")
printStats("125","0.75","1000")