ACO

* numberAnts = 25;
* alpha = 1.0;
* beta = 2.0;
* rho = 0.02;
* maxTime = 10.0;
* branchFactor = 1.0001;
* lambda = 0.05;
* iteration = 0;or
* restartFoundBestIteration = 0;
* foundBestIteration = 0;
* uGb = 25;
* nnListSize = 20;
* q0 = 0.0;
* if(isMIACO()) {
*    beta = 5.0;
*    numberAnts = 50;
* }


MEMORY

* if(_globals.isMMAS_MEM()) {
*     shortMemorySize = 4;
*     longMemorySize = 4;
*     immigrantRate = 0.4;
*     pMi = 0.01;
* }
* if(_globals.isMIACO()) {
*     shortMemorySize = 10;
*     longMemorySize = 4;
*     immigrantRate = 0.4;
*     pMi = 0.01;
* }