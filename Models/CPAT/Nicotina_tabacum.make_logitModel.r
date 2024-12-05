data <- read.table(file="Nicotina_tabacum.feature.xls",sep="\t",header=T)
attach(data)
mylogit <- glm(Label ~ mRNA + ORF + Fickett + Hexamer, family=binomial(link="logit"), na.action=na.pass)
save.image("Nicotina_tabacum.logit.RData")
