library(plotly)

pathName = "/home/joao/projects/master-degree/aco-dynamic-tsp-algorithm/output/"
#pathName = "/home/joao/Área de Trabalho/Temp/Java/tests/"
fileType = "TSP"
fileSimulated = "true"
fileNVertices = "100"
fileFrequency = "100"
fileMagnitude = "0.75"

executeHypothesisTest()
printStats()
plotGraph("bsf")

plotGraph("best")
plotGraph("mean")
plotGraph("worst")
plotGraph("div")


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
        
    dataMMAS <- read.csv(file = paste(pathName, fileType, "_", fileSimulated, "_MMAS_", fileMagnitude, "_", fileFrequency, "_true_4_", fileNVertices, ".csv", sep = ""))
    dataEIACO <- read.csv(file = paste(pathName, fileType, "_", fileSimulated, "_EIACO_", fileMagnitude, "_", fileFrequency, "_true_4_", fileNVertices, ".csv", sep = ""))
    dataMMASMEM <- read.csv(file = paste(pathName, fileType, "_", fileSimulated, "_MMAS_MEM_", fileMagnitude, "_", fileFrequency, "_true_4_", fileNVertices, ".csv", sep = ""))
    data <- data.frame(iteration = dataMMAS$iteration, mmas = dataMMAS[,property], mmas_mem = dataMMASMEM[,property], eiaco = dataEIACO[,property])
    
    plot_ly(x = data$iteration, y = data$mmas, type = "scatter", mode = "lines", name = "MMAS") %>%
        layout(title = title, yaxis = list(title = "Fitness"), xaxis = list(title = "Iteração")) %>%
        add_trace(y = data$mmas_mem, name = "MMAS_MEM", mode = "lines") %>%
        add_trace(y = data$eiaco, name = "EIACO", mode = "lines")
}


#####################################

sdStats <- function(data) {
    if(dim(data)[2] < 30) return(0)
    colSd <- c()
    for(i in 1:dim(data)[1]) {
        colSd <- c(colSd, sd(data[i,8:37]))
    }
    mean(colSd)
}

printStats <- function() {
    dataMMAS <- read.csv(file = paste(pathName, fileType, "_", fileSimulated, "_MMAS_", fileMagnitude, "_", fileFrequency, "_true_4_", fileNVertices, ".csv", sep = ""))
    dataMMASMEM <- read.csv(file = paste(pathName, fileType, "_", fileSimulated, "_MMAS_MEM_", fileMagnitude, "_", fileFrequency, "_true_4_", fileNVertices, ".csv", sep = ""))
    dataEIACO <- read.csv(file = paste(pathName, fileType, "_", fileSimulated, "_EIACO_", fileMagnitude, "_", fileFrequency, "_true_4_", fileNVertices, ".csv", sep = ""))
    
    print(paste("Problem", fileNVertices, "Magnitude", fileMagnitude, "Frequency", fileFrequency))
    print(paste("MMAS     mean", round(mean(dataMMAS$bsf), digits = 2), "sd", round(sdStats(dataMMAS), digits = 2), "median", round(median(dataMMAS$bsf), digits = 2)))
    print(paste("MMAS_MEM mean", round(mean(dataMMASMEM$bsf), digits = 2), "sd", round(sdStats(dataMMASMEM), digits = 2), "median", round(median(dataMMASMEM$bsf), digits = 2)))
    print(paste("EIACO    mean", round(mean(dataEIACO$bsf), digits = 2), "sd", round(sdStats(dataEIACO), digits = 2), "median", round(median(dataEIACO$bsf), digits = 2)))
}

#####################################

executeHypothesisTest <- function() {
    # http://data.library.virginia.edu/the-wilcoxon-rank-sum-test/
    dataMMAS <- read.csv(file = paste(pathName, fileType, "_", fileSimulated, "_MMAS_", fileMagnitude, "_", fileFrequency, "_true_4_", fileNVertices, ".csv", sep = ""))
    dataMMASMEM <- read.csv(file = paste(pathName, fileType, "_", fileSimulated, "_MMAS_MEM_", fileMagnitude, "_", fileFrequency, "_true_4_", fileNVertices, ".csv", sep = ""))
    dataEIACO <- read.csv(file = paste(pathName, fileType, "_", fileSimulated, "_EIACO_", fileMagnitude, "_", fileFrequency, "_true_4_", fileNVertices, ".csv", sep = ""))
    
    parametricData <- data.frame(data = dataMMAS$bsf, cat = "MMAS")
    parametricData <- rbind(parametricData, data.frame(data = dataMMASMEM$bsf, cat = "MMAS-MEM"))
    parametricData <- rbind(parametricData, data.frame(data = dataEIACO$bsf, cat = "EIACO"))
    #wilcox.test(data ~ cat, parametricData, conf.int = TRUE)
    kruskal.test(data ~ cat, data = parametricData)
}
