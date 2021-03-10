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

*Server:* 8070 <br/>
*Client:* 8080

### Test with other tools

#### RSocket

For RSocket development is used [RSC](https://github.com/making/rsc) installed using
`brew install making/tap/rsc`.

Now you are ready to go. Make sure the server is running and fire:

```shell
$ rsc --stream --route=orders --debug ws://localhost:8070/rsocket
```

This will produce output like:

```text

2021-03-10 11:23:59.373 DEBUG 64993 --- [actor-tcp-nio-2] io.rsocket.FrameLogger                   : receiving ->
Frame => Stream ID: 1 Type: NEXT Flags: 0b100000 Length: 181
Data:
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 7b 22 64 61 74 65 54 69 6d 65 22 3a 22 32 30 32 |{"dateTime":"202|
|00000010| 31 2d 30 33 2d 31 30 54 31 31 3a 32 33 3a 35 39 |1-03-10T11:23:59|
|00000020| 2e 33 37 32 33 33 35 2b 30 31 3a 30 30 22 2c 22 |.372335+01:00","|
|00000030| 65 6e 74 72 79 22 3a 31 34 31 39 2c 22 6e 75 6d |entry":1419,"num|
|00000040| 62 65 72 22 3a 33 2c 22 6e 61 6d 65 22 3a 22 44 |ber":3,"name":"D|
|00000050| 61 6e 20 44 2e 20 4c 79 6f 6e 22 2c 22 61 64 64 |an D. Lyon","add|
|00000060| 72 65 73 73 22 3a 22 41 70 74 2e 20 34 36 35 20 |ress":"Apt. 465 |
|00000070| 34 38 30 20 53 70 65 6e 63 65 72 20 53 71 75 61 |480 Spencer Squa|
|00000080| 72 65 2c 20 42 6c 69 63 6b 66 75 72 74 2c 20 4e |re, Blickfurt, N|
|00000090| 59 20 35 31 38 38 31 2d 38 33 38 38 22 2c 22 69 |Y 51881-8388","i|
|000000a0| 74 65 6d 22 3a 22 42 6f 72 6f 6d 69 72 22 7d    |tem":"Boromir"} |
+--------+-------------------------------------------------+----------------+
{"dateTime":"2021-03-10T11:23:59.372335+01:00","entry":1419,"number":3,"name":"Dan D. Lyon","address":"Apt. 465 480 Spencer Square, Blickfurt, NY 51881-8388","item":"Boromir"}
2021-03-10 11:24:04.379 DEBUG 64993 --- [actor-tcp-nio-2] io.rsocket.FrameLogger                   : receiving ->
Frame => Stream ID: 1 Type: NEXT Flags: 0b100000 Length: 197
Data:
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 7b 22 64 61 74 65 54 69 6d 65 22 3a 22 32 30 32 |{"dateTime":"202|
|00000010| 31 2d 30 33 2d 31 30 54 31 31 3a 32 34 3a 30 34 |1-03-10T11:24:04|
|00000020| 2e 33 37 38 33 30 32 2b 30 31 3a 30 30 22 2c 22 |.378302+01:00","|
|00000030| 65 6e 74 72 79 22 3a 31 34 32 30 2c 22 6e 75 6d |entry":1420,"num|
|00000040| 62 65 72 22 3a 32 2c 22 6e 61 6d 65 22 3a 22 53 |ber":2,"name":"S|
|00000050| 79 20 42 75 72 6e 65 74 74 65 22 2c 22 61 64 64 |y Burnette","add|
|00000060| 72 65 73 73 22 3a 22 53 75 69 74 65 20 32 36 30 |ress":"Suite 260|
|00000070| 20 35 36 34 20 44 6f 6e 6e 69 65 20 46 6f 72 6b | 564 Donnie Fork|
|00000080| 73 2c 20 45 61 73 74 20 44 65 6e 76 65 72 62 75 |s, East Denverbu|
|00000090| 72 67 68 2c 20 4c 41 20 31 37 38 34 32 2d 39 36 |rgh, LA 17842-96|
|000000a0| 39 32 22 2c 22 69 74 65 6d 22 3a 22 47 72 c3 ac |92","item":"Gr..|
|000000b0| 6d 61 20 57 6f 72 6d 74 6f 6e 67 75 65 22 7d    |ma Wormtongue"} |
+--------+-------------------------------------------------+----------------+
{"dateTime":"2021-03-10T11:24:04.378302+01:00","entry":1420,"number":2,"name":"Sy Burnette","address":"Suite 260 564 Donnie Forks, East Denverburgh, LA 17842-9692","item":"Grìma Wormtongue"}
2021-03-10 11:24:09.384 DEBUG 64993 --- [actor-tcp-nio-2] io.rsocket.FrameLogger                   : receiving ->
Frame => Stream ID: 1 Type: NEXT Flags: 0b100000 Length: 169
Data:
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 7b 22 64 61 74 65 54 69 6d 65 22 3a 22 32 30 32 |{"dateTime":"202|
|00000010| 31 2d 30 33 2d 31 30 54 31 31 3a 32 34 3a 30 39 |1-03-10T11:24:09|
|00000020| 2e 33 38 33 34 32 31 2b 30 31 3a 30 30 22 2c 22 |.383421+01:00","|
|00000030| 65 6e 74 72 79 22 3a 31 34 32 31 2c 22 6e 75 6d |entry":1421,"num|
|00000040| 62 65 72 22 3a 33 2c 22 6e 61 6d 65 22 3a 22 42 |ber":3,"name":"B|
|00000050| 2e 20 41 2e 20 57 61 72 65 22 2c 22 61 64 64 72 |. A. Ware","addr|
|00000060| 65 73 73 22 3a 22 38 39 32 20 53 77 61 6e 69 61 |ess":"892 Swania|
|00000070| 77 73 6b 69 20 42 72 6f 6f 6b 2c 20 53 6f 75 74 |wski Brook, Sout|
|00000080| 68 20 53 69 64 6e 65 79 2c 20 4e 44 20 31 30 34 |h Sidney, ND 104|
|00000090| 39 35 22 2c 22 69 74 65 6d 22 3a 22 47 69 6d 6c |95","item":"Giml|
|000000a0| 69 22 7d                                        |i"}             |
+--------+-------------------------------------------------+----------------+
{"dateTime":"2021-03-10T11:24:09.383421+01:00","entry":1421,"number":3,"name":"B. A. Ware","address":"892 Swaniawski Brook, South Sidney, ND 10495","item":"Gimli"}

...

```

**Note:** The whole frames sent are visible.

#### HTTP

To retrieve the stream using *HTTP* I used [HTTPIE](https://httpie.io/) using:

````shell
http :8070/orders --stream
````

This will produce output like:

```json
{
    "address": "07260 Theo Track, Katrinaview, CT 38594",
    "dateTime": "2021-03-10T11:26:54.497103+01:00",
    "entry": 1454,
    "item": "Shelob",
    "name": "Stu Pitt",
    "number": 2
}

{
    "address": "8839 Connelly Fords, Lake Elliotborough, SD 66346-6567",
    "dateTime": "2021-03-10T11:26:59.501662+01:00",
    "entry": 1455,
    "item": "Théoden",
    "name": "Val Crow",
    "number": 3
}

{
    "address": "469 Bode Roads, Port Damionfort, NV 19437",
    "dateTime": "2021-03-10T11:27:04.503566+01:00",
    "entry": 1456,
    "item": "Bilbo Baggins",
    "name": "Rhea Pollster",
    "number": 3
}

{
    "address": "Apt. 250 04131 Harvey Isle, East Ellis, AL 83667",
    "dateTime": "2021-03-10T11:27:09.508454+01:00",
    "entry": 1457,
    "item": "Arwen Evenstar",
    "name": "Justin Thyme",
    "number": 9
}

{
    "address": "Suite 173 7852 Warner Locks, Francescaburgh, VT 03381",
    "dateTime": "2021-03-10T11:27:14.511492+01:00",
    "entry": 1458,
    "item": "Samwise Gamgee",
    "name": "Lily Pond",
    "number": 9
}

...

```
## Next up

- [ ] Payload integration
- [ ] Multiple routes
- [ ] SpringSecurity
