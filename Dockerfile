FROM node:14-slim

RUN sed -i 's/deb.debian.org/mirrors.ustc.edu.cn/g' /etc/apt/sources.list

RUN apt-get update && \
    apt-get install -y wget unzip python build-essential && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

RUN npm install embedme

RUN wget -O /tmp/frontend.zip https://github.com/hstreamdb/hstreamdb-docs-frontend/archive/refs/heads/main.zip && \
    unzip /tmp/frontend.zip -d /tmp && \
    mv /tmp/hstreamdb-docs-frontend-main /app && \
    rm -rf /tmp/frontend.zip && \
    cd /app && yarn

WORKDIR /app
CMD ["yarn", "dev"]
