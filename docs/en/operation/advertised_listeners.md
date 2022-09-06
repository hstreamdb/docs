# Advertised Listeners

The deployment and usage in production could involve a complex network setting.
For example, if the server cluster is hosted internally, it would require an
external IP address for clients to connect to the cluster.

The use of docker and cloud-hosting can make the situation even more
complicated. To ensure that clients from different networks can interact with
the cluster, starting from v0.9.0 HStreamDB provides configurations for
advertised listeners.

With advertised listeners configured, servers can return the corresponding
address for different clients, according to the port to which the client sent
the request.

## Advertised Listeners Configuration

For example， if we set the address of hserver to be some public IP address.
However, we would also want some local client to be able to connect to the
cluster. In this case, we can set the advertised listeners to be the local IP
address.

```yaml
hserver:
  advertised-listeners:
    private:
      - address: "127.0.0.1"
        port: 6580
````
