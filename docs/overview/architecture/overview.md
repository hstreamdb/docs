# HStream Database Overview

**HStreamDB is a streaming database designed for streaming data, with complete lifecycle management for accessing, storing, processing, and distributing large-scale real-time data streams**. It uses standard SQL (and its stream extensions) as the primary interface language, with real-time as the main feature, and aims to simplify the operation and management of data streams and the development of real-time applications.

## Architecture

The figure below shows the overall architecture of HStreamDB. A single HStreamDB node consists of two core components, HStream Server (HSQL) and HStream Storage (HStorage). And an HStream cluster consists of several peer-to-peer HStreamDB nodes. Clients can connect to any HStreamDB node in the cluster and perform stream processing and analysis through your familiar SQL language.

![](https://static.emqx.net/images/faab4a8b1d02f14bc5a4153fe37f21ca.png)

<center>HStreamDB Structure Overview</center>
