# SpringCloud Gateway RSocket Example

## What is RSocket ? 

Check out the official docs: [RSocket Docs](https://rsocket.io/docs/)

> Most interesting:
>>Network communication is asynchronous. The RSocket protocol embraces 
>>this and models all communication as multiplexed streams of messages 
>>over a single network connection, and never synchronously blocks while 
>>waiting for a response

In this example I am using a SpringCloud Gateway (reactive) to demonstrate how
RSocket communication works over **WebSocket**.

## Motivation

We'd like to use a **single** websocket *endpoint/broker* for multiple services.
The *normal* spring integration with *STOMP over WebSocket* cannot be used on the
reactive stack. Therefore, I had to take a deeper look into RSocket.

## The Example

The server is creating scheduled *order* events which are added to a stream, those can 
be retrieved using a *HTTP GET* or *RSocket Request Stream*.

There are two *kind* of order, LOTR (Lord of the Rings) order and GOT (game of Thrones) order,
to demonstrate how *routes* (& security) can be handled. 

The order event is a *order* with random *kind*.

## Test it

Either use the checked in IntelliJ run-configuration in *.run* or launch it manually.

**Server:** (Maven v3.6.3)

```shell
$ mvn clean package
$ cd target/
$ java -jar {{appname-version}}.jar
```

**Client:** (Node v14.16.0 & NPM v6.14.11)

```shell
$ cd frontend
$ npm install
$ npm run dev
```

The web browser will open automatically, and you are ready to test it.

#### Ports used

*Server:* 8070

*Client:* 8080

### Test with other tools

#### RSocket

For RSocket development is used [RSC](https://github.com/making/rsc) installed using
`brew install making/tap/rsc`.

Now you are ready to go. Make sure the server is running and fire:

```shell
# Basic
$ rsc --stream --route=orders.all --debug ws://localhost:8070/server/rsocket
$ rsc --stream --route=orders.lotr --debug ws://localhost:8070/server/rsocket
$ rsc --stream --route=orders.got --debug ws://localhost:8070/server/rsocket

# With data (payload)
$ rsc --stream --route=orders.all --debug --data='{"rscclient":"request"}' ws://localhost:8070/server/rsocket

# With metadata
$ rsc --stream --route=orders.all --debug --metadata='{"data":"custom metadata value from rsc"}' ws://localhost:8070/server/rsocket
```

This will produce output like:

```text

2021-03-12 13:20:57.654 DEBUG 81691 --- [actor-tcp-nio-2] io.rsocket.FrameLogger                   : receiving ->
Frame => Stream ID: 1 Type: NEXT Flags: 0b100000 Length: 194
Data:
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 7b 22 64 61 74 65 54 69 6d 65 22 3a 22 32 30 32 |{"dateTime":"202|
|00000010| 31 2d 30 33 2d 31 32 54 31 33 3a 32 30 3a 35 37 |1-03-12T13:20:57|
|00000020| 2e 36 35 33 35 37 32 2b 30 31 3a 30 30 22 2c 22 |.653572+01:00","|
|00000030| 65 6e 74 72 79 22 3a 37 37 35 2c 22 6e 75 6d 62 |entry":775,"numb|
|00000040| 65 72 22 3a 33 2c 22 6e 61 6d 65 22 3a 22 47 69 |er":3,"name":"Gi|
|00000050| 6e 67 65 72 20 53 6e 61 70 70 22 2c 22 61 64 64 |nger Snapp","add|
|00000060| 72 65 73 73 22 3a 22 39 30 38 33 38 20 59 6f 6c |ress":"90838 Yol|
|00000070| 61 6e 64 61 20 53 74 61 74 69 6f 6e 2c 20 4c 61 |anda Station, La|
|00000080| 6b 65 20 4d 61 75 72 69 63 65 2c 20 4d 41 20 30 |ke Maurice, MA 0|
|00000090| 36 37 31 32 2d 30 36 33 38 22 2c 22 6b 69 6e 64 |6712-0638","kind|
|000000a0| 22 3a 22 4c 4f 54 52 22 2c 22 69 74 65 6d 22 3a |":"LOTR","item":|
|000000b0| 22 53 68 61 64 6f 77 66 61 78 22 7d             |"Shadowfax"}    |
+--------+-------------------------------------------------+----------------+
{"dateTime":"2021-03-12T13:20:57.653572+01:00","entry":775,"number":3,"name":"Ginger Snapp",
"address":"90838 Yolanda Station, Lake Maurice, MA 06712-0638","kind":"LOTR","item":"Shadowfax"}
2021-03-12 13:21:02.658 DEBUG 81691 --- [actor-tcp-nio-2] io.rsocket.FrameLogger                   : receiving ->
Frame => Stream ID: 1 Type: NEXT Flags: 0b100000 Length: 204
Data:
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 7b 22 64 61 74 65 54 69 6d 65 22 3a 22 32 30 32 |{"dateTime":"202|
|00000010| 31 2d 30 33 2d 31 32 54 31 33 3a 32 31 3a 30 32 |1-03-12T13:21:02|
|00000020| 2e 36 35 37 36 35 33 2b 30 31 3a 30 30 22 2c 22 |.657653+01:00","|
|00000030| 65 6e 74 72 79 22 3a 37 37 36 2c 22 6e 75 6d 62 |entry":776,"numb|
|00000040| 65 72 22 3a 38 2c 22 6e 61 6d 65 22 3a 22 52 79 |er":8,"name":"Ry|
|00000050| 61 6e 20 43 61 72 6e 61 74 69 6f 6e 22 2c 22 61 |an Carnation","a|
|00000060| 64 64 72 65 73 73 22 3a 22 53 75 69 74 65 20 38 |ddress":"Suite 8|
|00000070| 37 33 20 32 32 38 37 20 45 66 66 65 72 74 7a 20 |73 2287 Effertz |
|00000080| 53 70 75 72 2c 20 49 67 6e 61 63 69 61 76 69 65 |Spur, Ignaciavie|
|00000090| 77 2c 20 4e 48 20 31 38 36 35 39 2d 39 31 32 30 |w, NH 18659-9120|
|000000a0| 22 2c 22 6b 69 6e 64 22 3a 22 4c 4f 54 52 22 2c |","kind":"LOTR",|
|000000b0| 22 69 74 65 6d 22 3a 22 54 6f 6d 20 42 6f 6d 62 |"item":"Tom Bomb|
|000000c0| 61 64 69 6c 22 7d                               |adil"}          |
+--------+-------------------------------------------------+----------------+
{"dateTime":"2021-03-12T13:21:02.657653+01:00","entry":776,"number":8,"name":"Ryan Carnation",
"address":"Suite 873 2287 Effertz Spur, Ignaciaview, NH 18659-9120","kind":"LOTR","item":"Tom Bombadil"}
2021-03-12 13:21:07.663 DEBUG 81691 --- [actor-tcp-nio-2] io.rsocket.FrameLogger                   : receiving ->
Frame => Stream ID: 1 Type: NEXT Flags: 0b100000 Length: 202
Data:
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 7b 22 64 61 74 65 54 69 6d 65 22 3a 22 32 30 32 |{"dateTime":"202|
|00000010| 31 2d 30 33 2d 31 32 54 31 33 3a 32 31 3a 30 37 |1-03-12T13:21:07|
|00000020| 2e 36 36 32 38 33 31 2b 30 31 3a 30 30 22 2c 22 |.662831+01:00","|
|00000030| 65 6e 74 72 79 22 3a 37 37 37 2c 22 6e 75 6d 62 |entry":777,"numb|
|00000040| 65 72 22 3a 34 2c 22 6e 61 6d 65 22 3a 22 48 65 |er":4,"name":"He|
|00000050| 61 74 68 65 72 20 4e 2e 20 59 6f 6e 6e 22 2c 22 |ather N. Yonn","|
|00000060| 61 64 64 72 65 73 73 22 3a 22 33 33 34 20 5a 61 |address":"334 Za|
|00000070| 63 68 61 72 79 20 4d 6f 75 6e 74 61 69 6e 2c 20 |chary Mountain, |
|00000080| 43 72 75 69 63 6b 73 68 61 6e 6b 6c 61 6e 64 2c |Cruickshankland,|
|00000090| 20 41 5a 20 34 33 32 32 37 22 2c 22 6b 69 6e 64 | AZ 43227","kind|
|000000a0| 22 3a 22 4c 4f 54 52 22 2c 22 69 74 65 6d 22 3a |":"LOTR","item":|
|000000b0| 22 47 72 c3 ac 6d 61 20 57 6f 72 6d 74 6f 6e 67 |"Gr..ma Wormtong|
|000000c0| 75 65 22 7d                                     |ue"}            |
+--------+-------------------------------------------------+----------------+
{"dateTime":"2021-03-12T13:21:07.662831+01:00","entry":777,"number":4,"name":"Heather N. Yonn",
"address":"334 Zachary Mountain, Cruickshankland, AZ 43227","kind":"LOTR","item":"Grìma Wormtongue"}
...

```

**Note:** The whole frames sent are visible, also check out the server logs.

#### HTTP

To retrieve the stream using *HTTP* I used [HTTPIE](https://httpie.io/) using:

````shell
http :8070/server/orders --stream
````

This will produce output like:

```json
{
  "address": "456 Herman Turnpike, Sporerchester, VT 49469-0588",
  "dateTime": "2021-03-12T13:23:07.754996+01:00",
  "entry": 801,
  "item": "Bethany Bracken",
  "kind": "GOT",
  "name": "Val Lay",
  "number": 6
}

{
  "address": "73538 Morar Well, West Cristobal, OH 28237",
  "dateTime": "2021-03-12T13:23:12.760175+01:00",
  "entry": 802,
  "item": "Kyle Condon",
  "kind": "GOT",
  "name": "Eli Ondefloor",
  "number": 0
}

{
  "address": "Suite 931 381 Bernita Lane, Lake Benjamin, TX 93575",
  "dateTime": "2021-03-12T13:23:17.765894+01:00",
  "entry": 803,
  "item": "Samwise Gamgee",
  "kind": "LOTR",
  "name": "Robin DeCraydle",
  "number": 6
}

{
  "address": "922 Julene Hollow, Bookermouth, WA 68785-6036",
  "dateTime": "2021-03-12T13:23:22.769958+01:00",
  "entry": 804,
  "item": "Arwen Evenstar",
  "kind": "LOTR",
  "name": "Ryan Coke",
  "number": 5
}

{
  "address": "Suite 063 41363 Omer Square, Machellemouth, NE 92997",
  "dateTime": "2021-03-12T13:23:27.771213+01:00",
  "entry": 805,
  "item": "Qezza",
  "kind": "GOT",
  "name": "Terry Achey",
  "number": 3
}

...

```

Use *HTTPie* to create order:

```shell
# Random
http :8070/server/orders/new
# Specific
http :8070/server/orders/new kind==lotr
http :8070/server/orders/new kind==got
```

## Next up

- [X] Payload integration
- [X] Custom metadata
- [X] Route with variable
- [ ] SpringSecurity
- [ ] Multiple routes
