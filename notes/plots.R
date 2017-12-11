plotGraph <- function(property) {
    title = ""
    if(fileType == "NORMAL") {
        title = paste(title, "Joinville-", fileNVertices, " ", sep = "")
    } else {
        title = paste(title, "KroA", fileNVertices, " ", sep = "")
    }
    if(fileSimulated == "true") {
        title = paste(title," / movement = yes /")   
    } else {
        title = paste(title," / movement = no /")   
    }
    title = paste(title, "Mag:", fileMagnitude, " / Freq:", fileFrequency)   
    
    dataMMAS <- read.csv(file = paste(pathName, fileType, "_", fileSimulated, "_MMAS_", fileMagnitude, "_", fileFrequency, "_true_4_", fileNVertices, ".csv", sep = ""))
    dataMMASMEM <- read.csv(file = paste(pathName, fileType, "_", fileSimulated, "_MMAS_MEM_", fileMagnitude, "_", fileFrequency, "_true_4_", fileNVertices, ".csv", sep = ""))
    data <- data.frame(iteration = dataMMAS$iteration, mmas = dataMMAS[,property], mmas_mem = dataMMASMEM[,property])
    
    if(plotType == "line") {
        fSize = 1.2
        plot(data$iteration, data$mmas, type = "l", col = "black", main = title, xlab = "Iteration", ylab = "Fitness", 
             cex.lab=fSize, cex.axis=fSize, cex.main=fSize, lwd = .5)
        lines(data$iteration, data$mmas_mem, col = "black", lty=10, lwd=2)
        legend("topleft", legend = c("MMAS","MMAS_MEM"), lty=c(1, 10), lwd=c(.5, 2))   
    } else {
        data <- data.frame(data = dataMMAS$bsf, cat = rep("MMAS", dim(dataMMAS)[1]))
        data <- rbind(data, data.frame(data = dataMMASMEM$bsf, cat = rep("MMAS-MEM", dim(dataMMASMEM)[1])))
        boxplot(data ~ cat, data = data, main = title, xlab = "Iteration", ylab = "Algorithm")
    }
}

#####################################

pathName = "/home/joao/Área de Trabalho/Temp/Java/tests/"
fileType = "TSP"
fileSimulated = "true"
fileNVertices = "200"
filePlot = "bsf"
plotType = "line"

dev.off()
filename = paste(fileType, "_", fileSimulated, "_", fileNVertices, ".png", sep = "")
#png(filename, width = 12, height = 5, units = 'in', res = 300)
par(mar=c(3,3,2,1))
par(mfrow = c(2,3))
par(mgp=c(2, 1, 0))


fileFrequency = "10"
fileMagnitude = "0.1"
plotGraph(filePlot)

fileFrequency = "10"
fileMagnitude = "0.5"
plotGraph(filePlot)

fileFrequency = "10"
fileMagnitude = "0.75"
plotGraph(filePlot)

fileFrequency = "100"
fileMagnitude = "0.1"
plotGraph(filePlot)

fileFrequency = "100"
fileMagnitude = "0.5"
plotGraph(filePlot)

fileFrequency = "100"
fileMagnitude = "0.75"
plotGraph(filePlot)

#dev.off()