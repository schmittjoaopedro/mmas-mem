library(plotly)

pathName = "/home/joao/projects/master-degree/aco-dynamic-tsp-algorithm/output/"
fileType = "TSP"
fileSimulated = "true"
fileNVertices = "100"
fileFrequency = "500"
fileMagnitude = "0.75"
fileSeed = "1"

plotGraph("bsf")
plotGraph("best")
plotGraph("mean")
plotGraph("worst")
plotGraph("div")

printStats()
executeHypothesisTest()

#####################################

plotGraph <- function(property) {
    title = "Problema:"
    if(fileType == "NORMAL") {
        title = paste(title, " Joinville (", fileNVertices, ") ", sep = "")
    } else {
        title = paste(title, " TSP (", fileNVertices, ") ", sep = "")
    }
    if(fileSimulated == "true") {
        title = paste(title,"com movimento /")   
    } else {
        title = paste(title,"sem movimento /")   
    }
    title = paste(title, "Mag:", fileMagnitude, " / Freq:", fileFrequency)   
        
    dataMMAS <- read.csv(file = paste(pathName, fileType, "_", fileSimulated, "_MMAS_", fileMagnitude, "_", fileFrequency, "_true_4_", fileNVertices, "_", fileSeed, ".csv", sep = ""))
    dataMMASMEM <- read.csv(file = paste(pathName, fileType, "_", fileSimulated, "_MMAS_MEM_", fileMagnitude, "_", fileFrequency, "_true_4_", fileNVertices, "_", fileSeed, ".csv", sep = ""))
    data <- data.frame(iteration = dataMMAS$iteration, mmas = dataMMAS[,property], mmas_mem = dataMMASMEM[,property])
    
    plot_ly(x = data$iteration, y = data$mmas, type = "scatter", mode = "lines", name = "MMAS") %>%
        layout(title = title, yaxis = list(title = "Fitness"), xaxis = list(title = "Iteração")) %>%
        add_trace(y = data$mmas_mem, name = "MMAS_MEM", mode = "lines")
}


#####################################

sdStats <- function(data) {
    colSd <- c()
    for(i in 1:dim(data)[1]) {
        colSd <- c(colSd, sd(data[i,8:37]))
    }
    mean(colSd)
}

printStats <- function() {
    dataMMAS <- read.csv(file = paste(pathName, fileType, "_", fileSimulated, "_MMAS_", fileMagnitude, "_", fileFrequency, "_true_4_", fileNVertices, "_", fileSeed, ".csv", sep = ""))
    dataMMASMEM <- read.csv(file = paste(pathName, fileType, "_", fileSimulated, "_MMAS_MEM_", fileMagnitude, "_", fileFrequency, "_true_4_", fileNVertices, "_", fileSeed, ".csv", sep = ""))
    
    print(paste("Problem", fileNVertices, "Magnitude", fileMagnitude, "Frequency", fileFrequency))
    print(paste("MMAS     mean", mean(dataMMAS$bsf), "sd", sdStats(dataMMAS)))
    print(paste("MMAS_MEM mean", mean(dataMMASMEM$bsf), "sd", sdStats(dataMMASMEM)))
}


#####################################

executeHypothesisTest <- function() {
    # http://data.library.virginia.edu/the-wilcoxon-rank-sum-test/
    dataMMAS <- read.csv(file = paste(pathName, fileType, "_", fileSimulated, "_MMAS_", fileMagnitude, "_", fileFrequency, "_true_4_", fileNVertices, "_", fileSeed, ".csv", sep = ""))
    dataMMASMEM <- read.csv(file = paste(pathName, fileType, "_", fileSimulated, "_MMAS_MEM_", fileMagnitude, "_", fileFrequency, "_true_4_", fileNVertices, "_", fileSeed, ".csv", sep = ""))
    
    parametricData <- data.frame(data = dataMMAS$bsf, cat = "MMAS")
    parametricData <- rbind(parametricData, data.frame(data = dataMMASMEM$bsf, cat = "MMAS-MEM"))
    wilcox.test(data ~ cat, parametricData, conf.int = TRUE)
}