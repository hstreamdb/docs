# Overview

Considering performance and convenience,
hstream will not enable security features(encryption, authentication, etc.) by default,
but if your clients communicate with hstream servers by an insecure network,
you should enable them.

hstream supported security features:
+ Encryption: to prevent eavesdropping and tampering by man-in-the-middle attacks between clients and servers.
+ Authentication: to provide a mechanism that servers can authenticate trusted clients and an interface for authorization.
