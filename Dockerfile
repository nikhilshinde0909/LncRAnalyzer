FROM ubuntu:latest

ENV DEBIAN_FRONTEND=noninteractive
LABEL authors="sd1172@srmist.edu.in" \
      description="Docker image containing all requirements for the LncRAnalyzer pipeline"

# Install dependencies
RUN apt-get -o Acquire::Check-Valid-Until=false -o Acquire::Check-Date=false update && apt-get install -y \
    build-essential \
    bash \
    git \
    curl \
    wget \
    bzip2 \
    ca-certificates && \
    rm -rf /var/lib/apt/lists/*

# Install miniforge
RUN curl -L https://github.com/conda-forge/miniforge/releases/latest/download/Miniforge3-Linux-x86_64.sh -o miniforge.sh \
    && chmod +x miniforge.sh \
    && bash miniforge.sh -b -p /opt/miniforge \
    && rm miniforge.sh

# Set environment variables for miniforge
ENV PATH="/opt/miniforge/bin:${PATH}"

# Install Conda environments
COPY LncRAnalyzer.yml /tmp/
RUN conda env update --file /tmp/LncRAnalyzer.yml && conda clean -a
RUN R -e 'options(timeout=600); install.packages(c("LncFinder", "seqinr"),repos="https://cran.r-project.org")'

COPY cpc2-cpat-slncky.yml /tmp/
RUN conda env create --file /tmp/cpc2-cpat-slncky.yml && conda clean -a

COPY rnasamba.yml /tmp/
RUN conda env create --file /tmp/rnasamba.yml && conda clean -a

COPY FEELnc.yml /tmp/
RUN conda env create --file /tmp/FEELnc.yml && conda clean -a

# Install Slncky to path
COPY utils/slncky/slncky.v1.0 utils/slncky/alignTranscripts1.0 /opt/miniforge/envs/cpc2-cpat-slncky/bin/
RUN chmod +x /opt/miniforge/envs/cpc2-cpat-slncky/bin/slncky.v1.0 \
    && chmod +x /opt/miniforge/envs/cpc2-cpat-slncky/bin/alignTranscripts1.0 \
    && chmod -R 777 /opt/miniforge/envs/cpc2-cpat-slncky/lib/python2.7

# Install HMMER=3.1b1 from source code  
WORKDIR /opt/miniforge/  
RUN curl -L http://eddylab.org/software/hmmer/hmmer-3.1b1.tar.gz -o hmmer-3.1b1.tar.gz \
    && tar -zxvf hmmer-3.1b1.tar.gz \
    && rm hmmer-3.1b1.tar.gz

RUN cd hmmer-3.1b1 \
    && ./configure \
    && make \
    && ln -sf $(pwd)/src/* /opt/miniforge/bin/

# Create a directory for LncRAnalyzer
WORKDIR /pipeline
RUN mkdir LncRAnalyzer

# Copy LncRAnalyzer
COPY . /pipeline/LncRAnalyzer

# Copy and run the script to add paths for tools
COPY utils/add_paths_for_tools.sh /tmp/
RUN chmod +x /tmp/add_paths_for_tools.sh && bash /tmp/add_paths_for_tools.sh > $(pwd)/LncRAnalyzer/tools.groovy

# Default command to start a bash shell
CMD ["bash"]
