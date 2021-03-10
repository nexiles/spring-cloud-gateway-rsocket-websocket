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


const keepAlive = 60000;
const lifetime = 180000;
const dataMimeType = 'application/json';
const metadataMimeType = MESSAGE_RSOCKET_COMPOSITE_METADATA.string;

export default async ({Vue}) => {

  Vue.prototype.$rsocketclient = new RSocketClient({
    setup: {
      dataMimeType,
      keepAlive,
      lifetime,
      metadataMimeType,
      payload: {
        data: undefined,
        metadata: undefined,
      },
    },
    transport: new RSocketWebSocketClient({
        debug: true,
        url: 'ws://localhost:8080/rsocket',
      },
      BufferEncoders,
    ),
  });

  Vue.prototype.$encodersocketroute = encodersocketroute;
}

function encodersocketroute(route) {
  console.log("encode")
  return encodeCompositeMetadata([
    [TEXT_PLAIN, Buffer.from('Hello World')],
    [MESSAGE_RSOCKET_ROUTING, encodeRoute(route)]
  ])
}
