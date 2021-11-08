# Architecture Overview

The figure below shows the overall architecture of HStreamDB. A single HStreamDB node consists of two core components, HStream Server (HSQL) and HStream Storage (HStorage). And an HStream cluster consists of several peer-to-peer HStreamDB nodes. Clients can connect to any HStreamDB node in the cluster and perform stream processing and analysis through your familiar SQL language.

![](https://static.emqx.net/images/faab4a8b1d02f14bc5a4153fe37f21ca.png)

<center>HStreamDB Structure Overview</center>
