import {
  BufferEncoders,
  encodeCompositeMetadata,
  encodeRoute,
  TEXT_PLAIN,
  MESSAGE_RSOCKET_COMPOSITE_METADATA,
  MESSAGE_RSOCKET_ROUTING,
  RSocketClient,
  JsonSerializer,
} from 'rsocket-core';
import RSocketWebSocketClient from 'rsocket-websocket-client';


const maxRSocketRequestN = 2147483647;
const keepAlive = 60000;
const lifetime = 180000;
const dataMimeType = 'application/json';
const metadataMimeType = MESSAGE_RSOCKET_COMPOSITE_METADATA.string;
const route = 'orders';

export default async ({Vue}) => {

  const routeMetadata = encodersroute(route);

  console.log("Route MetaData: " + routeMetadata)

  Vue.prototype.$rsocketclient = new RSocketClient({
    setup: {
      dataMimeType,
      keepAlive,
      lifetime,
      metadataMimeType,
      payload: {
        data: undefined,
        metadata: routeMetadata,
      },
    },
    transport: new RSocketWebSocketClient({
        debug: true,
        url: 'ws://localhost:8080/rsocket',
      },
      BufferEncoders,
    ),
  });

  Vue.prototype.$encoderoute = encodersroute;


  // const metadataMimeType = MESSAGE_RSOCKET_ROUTING.string;
  // const client = new RSocketClient({
  //   // send/receive JSON objects instead of strings/buffers
  //   serializers: {
  //     data: JsonSerializer,
  //     metadata: IdentitySerializer
  //   },
  //   setup: {
  //     // ms btw sending keepalive to server
  //     keepAlive: 60000,
  //     // ms timeout if no keepalive response
  //     lifetime: 180000,
  //     // format of `data`
  //     dataMimeType: "application/json",
  //     // format of `metadata`
  //     metadataMimeType: metadataMimeType,
  //   },
  //   transport: new RSocketWebSocketClient({
  //     url: "ws://localhost:8080/rsocket"
  //   })
  // });
  //
  // Vue.prototype.$rsocketclient = client;
  //
  // Vue.prototype.$encoderoute = encodeRoute;
  //
  // Vue.prototype.$rsocketroutemetadata = function (route) {
  //   const length = Buffer.byteLength(route, 'utf8');
  //   const buffer = Buffer.alloc(1);
  //   buffer.writeInt8(length);
  //   const encodedRoute = Buffer.concat([buffer, Buffer.from(route, 'utf8')]);
  //   return encodeAndAddWellKnownMetadata(Buffer.alloc(0), MESSAGE_RSOCKET_ROUTING, encodedRoute);
  // }
}

function encodersroute(route) {
  console.log("encode")
  return encodeCompositeMetadata([
    [TEXT_PLAIN, Buffer.from('Hello World')],
    [MESSAGE_RSOCKET_ROUTING, encodeRoute(route)]
  ])
}
