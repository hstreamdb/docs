# HStream Server

HStream Server (HSQL), the core computation component of HStreamDB, is designed to be stateless.
The primary responsibility of HSQL is to support client connection management, security authentication, SQL parsing and optimization,
and operations for stream computation such as task creation, scheduling, execution, management, etc.

## HStream Server (HSQL) top-down layered structures

### Access Layer

It is in charge of protocol processing, connection management, security authentication,
and access control for client requests.

### SQL layer

To perform most stream processing and real-time analysis tasks, clients interact with HStreamDB through SQL statements.
This layer is mainly responsible for compiling these SQL statements into logical data flow diagrams.
Like the classic database system model, it contains two core sub-components: SQL parser and SQL optimizer.
The SQL parser deals with the lexical and syntactic analysis and the compilation from SQL statements to relational algebraic expressions;
the SQL optimizer will optimize the generated execution plan based on various rules and contexts.

### Stream Layer

Stream layer includes the implementation of various stream processing operators, the data structures and DSL to express data flow diagrams,
and the support for user-defined functions as processing operators.
So, it is responsible for selecting the corresponding operator and optimization to generate the executable data flow diagram.

### Runtime Layer

It is the layer responsible for executing the computation task of data flow diagrams and returning the results.
The main components of the layer include task scheduler, state manager, and execution optimizer.
The schedule takes care of the tasks scheduling between available computation resources,
such as multiple threads of a single process, multiple processors of a single machine,
and multiple machines or containers of a distributed cluster.
