data <- read.table(file="Amborella_trichopoda.feature.xls",sep="\t",header=T)
attach(data)
mylogit <- glm(Label ~ mRNA + ORF + Fickett + Hexamer, family=binomial(link="logit"), na.action=na.pass)
save.image("Amborella_trichopoda.logit.RData")
