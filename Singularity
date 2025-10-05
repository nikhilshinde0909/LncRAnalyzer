BootStrap: library
From: ubuntu:latest

%labels
    authors="sd1172@srmist.edu.in"
    description="Singularity image containing all requirements for the LncRAnalyzer pipeline"

%environment
    export DEBIAN_FRONTEND=noninteractive
    export PATH=/opt/miniforge/bin:$PATH
    export LANG=en_US.UTF-8
    export LC_ALL=en_US.UTF-8
    export LC_CTYPE=en_US.UTF-8
    export R_LIBS_USER=/opt/miniforge/lib/R/library
       
%files
    . /opt/LncRAnalyzer/
    
%post
    # Install dependencies
    apt-get -o Acquire::Check-Valid-Until=false -o Acquire::Check-Date=false update && \
    apt-get install --allow-unauthenticated -y \
        build-essential \
        libcurl4-openssl-dev \
        libssl-dev \
        libxml2-dev \
        locales \
        bash \
        git \
        curl \
        wget \
        bzip2 \
        ca-certificates \
        gcc && \
    locale-gen en_US.UTF-8 && \
    update-locale LANG=en_US.UTF-8 LC_ALL=en_US.UTF-8 && \
    rm -rf /var/lib/apt/list
    
    # Install miniforge
    curl -L https://github.com/conda-forge/miniforge/releases/latest/download/Miniforge3-Linux-x86_64.sh -o miniforge.sh && \
    chmod +x miniforge.sh && \
    bash miniforge.sh -b -p /opt/miniforge && \
    rm miniforge.sh 
     
    # Install Conda environments
    /opt/miniforge/bin/conda env update --file /opt/LncRAnalyzer/LncRAnalyzer.yml && /opt/miniforge/bin/conda clean -a
    export PATH=/opt/miniforge/bin:$PATH
    
    /opt/miniforge/bin/R -e 'options(timeout=600); install.packages(c("LncFinder", "seqinr"), repos="https://cran.r-project.org")'

    /opt/miniforge/bin/conda env create --file /opt/LncRAnalyzer/cpc2-cpat-slncky.yml && /opt/miniforge/bin/conda clean -a

    /opt/miniforge/bin/conda env create --file /opt/LncRAnalyzer/rnasamba.yml && /opt/miniforge/bin/conda clean -a

    /opt/miniforge/bin/conda env create --file /opt/LncRAnalyzer/FEELnc.yml && /opt/miniforge/bin/conda clean -a

    # Install Slncky to path
    cp /opt/LncRAnalyzer/utils/slncky/slncky.v1.0 /opt/miniforge/envs/cpc2-cpat-slncky/bin/
    cp /opt/LncRAnalyzer/utils/slncky/alignTranscripts1.0 /opt/miniforge/envs/cpc2-cpat-slncky/bin/
    chmod +x /opt/miniforge/envs/cpc2-cpat-slncky/bin/slncky.v1.0
    chmod +x /opt/miniforge/envs/cpc2-cpat-slncky/bin/alignTranscripts1.0
    chmod -R 777 /opt/miniforge/envs/cpc2-cpat-slncky/lib/python2.7

    # Install HMMER=3.1b1 from source code
    curl -L http://eddylab.org/software/hmmer/hmmer-3.1b1.tar.gz -o /opt/miniforge/hmmer-3.1b1.tar.gz
    tar -zxvf /opt/miniforge/hmmer-3.1b1.tar.gz -C /opt/miniforge/
    rm /opt/miniforge/hmmer-3.1b1.tar.gz
    cd /opt/miniforge/hmmer-3.1b1 && \
    ./configure && \
    make && \
    ln -sf $(pwd)/src/* /opt/miniforge/bin/
    
    # pipeline dir
    mkdir -p /pipeline
    
    # move lncRAnalyzer
    mv /opt/LncRAnalyzer /pipeline/

    # Run the script to add paths for tools
    chmod +x /pipeline/LncRAnalyzer/utils/add_paths_for_tools.sh && bash /pipeline/LncRAnalyzer/utils/add_paths_for_tools.sh > /pipeline/LncRAnalyzer/tools.groovy

%runscript
    exec /bin/bash
