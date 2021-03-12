import {
  BufferEncoders,
  encodeCompositeMetadata,
  encodeSimpleAuthMetadata,
  encodeRoute,
  APPLICATION_JSON,
  MESSAGE_RSOCKET_COMPOSITE_METADATA,
  MESSAGE_RSOCKET_AUTHENTICATION,
  MESSAGE_RSOCKET_ROUTING,
  RSocketClient,
  JsonSerializer
} from 'rsocket-core';
import RSocketWebSocketClient from 'rsocket-websocket-client';
import {Buffer} from "rsocket-core/build/LiteBuffer";

const url = 'ws://localhost:8080/server/rsocket';

const keepAlive = 60000;
const lifetime = 180000;
const dataMimeType = 'application/json';
const metadataMimeType = MESSAGE_RSOCKET_COMPOSITE_METADATA.string;

function setupClient(user) {
  return new RSocketClient({
    setup: {
      dataMimeType,
      keepAlive,
      lifetime,
      metadataMimeType,
      payload: {
        data: undefined,
        metadata: encodeMetaData(user),
      },
    },
    transport: new RSocketWebSocketClient({
        debug: true,
        url: url,
      },
      BufferEncoders,
    ),
  });
}

function encodeData(data) {
  return Buffer.from(JsonSerializer.serialize(data))
}

function encodeMetaData(user, route, customMetadata) {
  const metadata = [[MESSAGE_RSOCKET_AUTHENTICATION, encodeSimpleAuthMetadata(user.username, user.password)]];

  if(route)
    metadata.push([MESSAGE_RSOCKET_ROUTING, encodeRoute(route)]);

  if (customMetadata)
    metadata.push([APPLICATION_JSON, Buffer.from(JsonSerializer.serialize(customMetadata))])

  return encodeCompositeMetadata(metadata);
}

export default async ({Vue}) => {

  // noinspection JSUnusedGlobalSymbols
  Vue.prototype.$setuprsocketclient = setupClient;
  // noinspection JSUnusedGlobalSymbols
  Vue.prototype.$encodemetadata = encodeMetaData;
  // noinspection JSUnusedGlobalSymbols
  Vue.prototype.$encodedata = encodeData;
}

