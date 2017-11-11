library(plotly)

pathName = "/home/joao/Área de Trabalho/Temp/Java/tests2/output/"
problem = "125" # 46, 78, 125
mag = "0.75" # 0.1, 0.5, 0.75
freq = "500" # 500, 1000

printBsfAdj(pathName, problem,mag,freq)
printBsf(pathName, problem,mag,freq)
printMean(pathName, problem,mag,freq)
printBest(pathName, problem,mag,freq)
printWorst(pathName, problem,mag,freq)
printDiv(pathName, problem,mag,freq)

printBsf <- function(path, problem, mag, freq) {
    data <- read.csv(file = paste(pathName, "MMAS_TSP-", problem, "_MAG-", mag, "_FREQ-", freq, "_PERIOD-4.csv", sep = ""))
    dataMem <- read.csv(file = paste(pathName, "MMAS_MEM_TSP-", problem, "_MAG-", mag, "_FREQ-", freq, "_PERIOD-4.csv", sep = ""))
    dataMiaco <- read.csv(file = paste(pathName, "MIACO_TSP-", problem, "_MAG-", mag, "_FREQ-", freq, "_PERIOD-4.csv", sep = ""))
    
    data$bsf2 <- dataMem$bsf
    data$bsf3 <- dataMiaco$bsf
    plot_ly(data, x = ~iteration, y = ~bsf, type = "scatter", mode = "lines", name = "MMAS") %>%
        add_trace(y = ~bsf2, name = "MMAS_MEM", mode = "lines") %>%
        add_trace(y = ~bsf3, name = "MIACO", mode = "lines")
}

printBsfAdj <- function(path, problem, mag, freq) {
    data <- read.csv(file = paste(pathName, "MMAS_TSP-", problem, "_MAG-", mag, "_FREQ-", freq, "_PERIOD-4.csv", sep = ""))
    dataMem <- read.csv(file = paste(pathName, "MMAS_MEM_TSP-", problem, "_MAG-", mag, "_FREQ-", freq, "_PERIOD-4.csv", sep = ""))
    dataMiaco <- read.csv(file = paste(pathName, "MIACO_TSP-", problem, "_MAG-", mag, "_FREQ-", freq, "_PERIOD-4.csv", sep = ""))
    title = paste("Vertices =", problem, "Frequencia =", freq, "Magnitude = ",mag)
    data$bsf2 <- dataMem$bsf
    data$bsf3 <- dataMiaco$bsf
    plot_ly(data, x = ~iteration, y = ~bsf, type = "scatter", mode = "lines", name = "MMAS") %>%
        layout(title = title, yaxis = list(title = "Fitness"), xaxis = list(title = "Iteração")) %>%
        add_trace(y = ~bsf2, name = "MMAS_MEM", mode = "lines")# %>%
        #add_trace(y = ~bsfAdj3, name = "MIACO", mode = "lines")
}

printMean <- function(path, problem, mag, freq) {
    data <- read.csv(file = paste(pathName, "MMAS_TSP-", problem, "_MAG-", mag, "_FREQ-", freq, "_PERIOD-4.csv", sep = ""))
    dataMem <- read.csv(file = paste(pathName, "MMAS_MEM_TSP-", problem, "_MAG-", mag, "_FREQ-", freq, "_PERIOD-4.csv", sep = ""))
    dataMiaco <- read.csv(file = paste(pathName, "MIACO_TSP-", problem, "_MAG-", mag, "_FREQ-", freq, "_PERIOD-4.csv", sep = ""))
    
    data$mean2 <- dataMem$mean
    data$mean3 <- dataMiaco$mean
    plot_ly(data, x = ~iteration, y = ~mean, type = "scatter", mode = "lines", name = "MMAS") %>%
        add_trace(y = ~mean2, name = "MMAS_MEM", mode = "lines") %>%
        add_trace(y = ~mean3, name = "MIACO", mode = "lines")
}

printBest <- function(path, problem, mag, freq) {
    data <- read.csv(file = paste(pathName, "MMAS_TSP-", problem, "_MAG-", mag, "_FREQ-", freq, "_PERIOD-4.csv", sep = ""))
    dataMem <- read.csv(file = paste(pathName, "MMAS_MEM_TSP-", problem, "_MAG-", mag, "_FREQ-", freq, "_PERIOD-4.csv", sep = ""))
    dataMiaco <- read.csv(file = paste(pathName, "MIACO_TSP-", problem, "_MAG-", mag, "_FREQ-", freq, "_PERIOD-4.csv", sep = ""))
    
    data$best2 <- dataMem$best
    data$best3 <- dataMiaco$best
    plot_ly(data, x = ~iteration, y = ~best, type = "scatter", mode = "lines", name = "MMAS") %>%
        add_trace(y = ~best2, name = "MMAS_MEM", mode = "lines") %>%
        add_trace(y = ~best3, name = "MIACO", mode = "lines")
}

printWorst <- function(path, problem, mag, freq) {
    data <- read.csv(file = paste(pathName, "MMAS_TSP-", problem, "_MAG-", mag, "_FREQ-", freq, "_PERIOD-4.csv", sep = ""))
    dataMem <- read.csv(file = paste(pathName, "MMAS_MEM_TSP-", problem, "_MAG-", mag, "_FREQ-", freq, "_PERIOD-4.csv", sep = ""))
    dataMiaco <- read.csv(file = paste(pathName, "MIACO_TSP-", problem, "_MAG-", mag, "_FREQ-", freq, "_PERIOD-4.csv", sep = ""))
    
    data$worst2 <- dataMem$worst
    data$worst3 <- dataMiaco$worst
    plot_ly(data, x = ~iteration, y = ~worst, type = "scatter", mode = "lines", name = "MMAS") %>%
        add_trace(y = ~worst2, name = "MMAS_MEM", mode = "lines") %>%
        add_trace(y = ~worst3, name = "MIACO", mode = "lines")
}

printDiv <- function(path, problem, mag, freq) {
    data <- read.csv(file = paste(pathName, "MMAS_TSP-", problem, "_MAG-", mag, "_FREQ-", freq, "_PERIOD-4.csv", sep = ""))
    dataMem <- read.csv(file = paste(pathName, "MMAS_MEM_TSP-", problem, "_MAG-", mag, "_FREQ-", freq, "_PERIOD-4.csv", sep = ""))
    dataMiaco <- read.csv(file = paste(pathName, "MIACO_TSP-", problem, "_MAG-", mag, "_FREQ-", freq, "_PERIOD-4.csv", sep = ""))
    
    data$div2 <- dataMem$div
    data$div3 <- dataMiaco$div
    plot_ly(data, x = ~iteration, y = ~div, type = "scatter", mode = "lines", name = "MMAS") %>%
        add_trace(y = ~div2, name = "MMAS_MEM", mode = "lines") %>%
        add_trace(y = ~div3, name = "MIACO", mode = "lines")
}




#####################################

sdStats <- function(data) {
    colSd <- c()
    for(i in 1:dim(data)[1]) {
        colSd <- c(colSd, sd(data[i,8:37]))
    }
    mean(colSd)
}

printStats <- function(problem, mag, freq) {
    data <- read.csv(file = paste(pathName, "MMAS_TSP-", problem, "_MAG-", mag, "_FREQ-", freq, "_PERIOD-4.csv", sep = ""))
    dataMem <- read.csv(file = paste(pathName, "MMAS_MEM_TSP-", problem, "_MAG-", mag, "_FREQ-", freq, "_PERIOD-4.csv", sep = ""))
    dataMiaco <- read.csv(file = paste(pathName, "MIACO_TSP-", problem, "_MAG-", mag, "_FREQ-", freq, "_PERIOD-4.csv", sep = ""))
    
    print(paste("Problem", problem, "Magnitude", mag, "Frequency", freq))
    print(paste("MMAS     mean", mean(data$bsf), "sd", sdStats(data)))
    print(paste("MMAS_MEM mean", mean(dataMem$bsf), "sd", sdStats(dataMem)))
    print(paste("MIACO    mean", mean(dataMiaco$bsf), "sd", sdStats(dataMiaco)))
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


##########################################################

executeHypothesisTest <- function(problem, mag, freq) {
    # http://data.library.virginia.edu/the-wilcoxon-rank-sum-test/
    data <- read.csv(file = paste(pathName, "MMAS_TSP-", problem, "_MAG-", mag, "_FREQ-", freq, "_PERIOD-4.csv", sep = ""))
    dataMem <- read.csv(file = paste(pathName, "MMAS_MEM_TSP-", problem, "_MAG-", mag, "_FREQ-", freq, "_PERIOD-4.csv", sep = ""))
    parametricData <- data.frame(data = data$bsf, cat = "MMAS")
    parametricData <- rbind(parametricData, data.frame(data = dataMem$bsf, cat = "MMAS-MEM"))
    wilcox.test(data ~ cat, parametricData, conf.int = TRUE)
}

# 46, 78, 125
# 0.1, 0.5, 0.75
# 500, 1000
executeHypothesisTest("46","0.1","500")
executeHypothesisTest("46","0.1","1000")
executeHypothesisTest("46","0.5","500")
executeHypothesisTest("46","0.5","1000")
executeHypothesisTest("46","0.75","500")
executeHypothesisTest("46","0.75","1000")

executeHypothesisTest("78","0.1","500")
executeHypothesisTest("78","0.1","1000")
executeHypothesisTest("78","0.5","500")
executeHypothesisTest("78","0.5","1000")
executeHypothesisTest("78","0.75","500")
executeHypothesisTest("78","0.75","1000")

executeHypothesisTest("125","0.1","500")
executeHypothesisTest("125","0.1","1000")
executeHypothesisTest("125","0.5","500")
executeHypothesisTest("125","0.5","1000")
executeHypothesisTest("125","0.75","500")
executeHypothesisTest("125","0.75","1000")
