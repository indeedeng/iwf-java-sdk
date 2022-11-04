FROM golang:latest

RUN apt-get update \
  && DEBIAN_FRONTEND=noninteractive apt-get install -y \
    net-tools \
  && apt-get clean \
  && rm -rf /var/lib/apt/lists/*
RUN mkdir /dependencies &&  \
    cd /dependencies &&  \
    git clone https://github.com/indeedeng/iwf.git &&  \
    (cd iwf/ && make bins) &&  \
    wget https://github.com/temporalio/temporalite/releases/download/v0.2.0/temporalite_0.2.0_linux_amd64.tar.gz -P . &&  \
    tar -xf temporalite_0.2.0_linux_amd64.tar.gz

WORKDIR /dependencies/iwf
COPY start-deps.sh /dependencies/iwf

RUN chmod +x /dependencies/iwf/start-deps.sh

EXPOSE 7233 8233 8801
ENTRYPOINT ["./start-deps.sh"]
