plotGraph <- function(property) {
    title = ""
    if(fileType == "NORMAL") {
        title = paste(title, "Joinville", fileNVertices, " ", sep = "")
    } else {
        title = paste(title, "KroA", fileNVertices, " ", sep = "")
    }
    if(fileSimulated == "true") {
        title = paste(title,"/ Mov.: yes /")   
    } else {
        title = paste(title,"/ Mov.: no /")   
    }
    title = paste(title, "Mag:", fileMagnitude, " / Freq:", fileFrequency)   
    
    dataMMAS <- read.csv(file = paste(pathName, fileType, "_", fileSimulated, "_MMAS_", fileMagnitude, "_", fileFrequency, "_true_4_", fileNVertices, ".csv", sep = ""))
    dataMMASMEM <- read.csv(file = paste(pathName, fileType, "_", fileSimulated, "_MMAS_MEM_", fileMagnitude, "_", fileFrequency, "_true_4_", fileNVertices, ".csv", sep = ""))
    data <- data.frame(iteration = dataMMAS$iteration, mmas = dataMMAS[,property], mmas_mem = dataMMASMEM[,property])
    
    ylabText = ""
    if(fileFrequency == "10")
        ylabText = "POFF"
    xlabText= ""
    if(fileMagnitude == "0.75")
    xlabText = "ITERATION"
    
    if(plotType == "line") {
        fSize = 1.2
        dataLimMin = c(min(data$mmas), min(data$mmas_mem))
        dataLimMax = c(max(data$mmas), max(data$mmas_mem))
        dataLim = c(min(dataLimMin) - 1000, min(dataLimMax) + 1000)
        print(dataLim)
        plot(data$iteration, data$mmas, type = "l", col = "black", main = title, xlab = xlabText, ylab = ylabText, 
             cex.lab=2, cex.axis=2, cex.main=2, lty=3, lwd = 1, ylim = dataLim)
        lines(data$iteration, data$mmas_mem, col = "black", lty=1, lwd=1.5)
        legend("topleft", legend = c("MMAS","MMAS_MEM"), lty=c(3, 1), lwd=c(1, 1.5), cex=1.4)
    } else {
        data <- data.frame(data = dataMMAS$bsf, cat = rep("MMAS", dim(dataMMAS)[1]))
        data <- rbind(data, data.frame(data = dataMMASMEM$bsf, cat = rep("MMAS-MEM", dim(dataMMASMEM)[1])))
        boxplot(data ~ cat, data = data, main = title, xlab = "Iteration", ylab = "Algorithm")
    }
}

#####################################

pathName = "/home/joao/projects/master-degree/aco-dynamic-tsp-algorithm/output/StatisticsPaper2/"
fileType = "TSP"
fileSimulated = "true"
fileNVertices = "150"
filePlot = "bsf"
plotType = "line"

#dev.off()
#filename = paste(fileType, "_", fileSimulated, "_", fileNVertices, ".png", sep = "")
#png(filename, width = 12, height = 5, units = 'in', res = 300)
par(mar=c(4,4,2,1))
par(mfrow = c(3,2))
par(mgp=c(2.4, 0.8, 0))


fileFrequency = "10"
fileMagnitude = "0.1"
plotGraph(filePlot)

fileFrequency = "100"
fileMagnitude = "0.1"
plotGraph(filePlot)

fileFrequency = "10"
fileMagnitude = "0.5"
plotGraph(filePlot)

fileFrequency = "100"
fileMagnitude = "0.5"
plotGraph(filePlot)

fileFrequency = "10"
fileMagnitude = "0.75"
plotGraph(filePlot)

fileFrequency = "100"
fileMagnitude = "0.75"
plotGraph(filePlot)

#dev.off()