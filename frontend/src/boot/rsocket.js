import {
  BufferEncoders,
  encodeCompositeMetadata,
  encodeSimpleAuthMetadata,
  encodeBearerAuthMetadata,
  encodeRoute,
  APPLICATION_JSON,
  MESSAGE_RSOCKET_COMPOSITE_METADATA,
  MESSAGE_RSOCKET_AUTHENTICATION,
  MESSAGE_RSOCKET_ROUTING,
  RSocketClient,
  JsonSerializer
} from 'rsocket-core';
import {authentication} from "src/js/Auth";
import RSocketWebSocketClient from 'rsocket-websocket-client';
import {Buffer} from "rsocket-core/build/LiteBuffer";

const url = 'ws://localhost:8070/server/rsocket';

const keepAlive = 60000;
const lifetime = 180000;
const dataMimeType = 'application/json';
const metadataMimeType = MESSAGE_RSOCKET_COMPOSITE_METADATA.string;

function setupClient(auth) {
  return new RSocketClient({
    setup: {
      dataMimeType,
      keepAlive,
      lifetime,
      metadataMimeType,
      payload: {
        data: undefined,
        metadata: encodeMetaData(auth),
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

function encodeMetaData(auth, route, customMetadata) {
  const metadata = [];

  console.debug("Auth: " + JSON.stringify(auth) + " Route: " + route + " Meta: " + customMetadata)

  if (auth) {
    if (auth.authType === authentication.BEARER) {
      metadata.push([MESSAGE_RSOCKET_AUTHENTICATION, encodeBearerAuthMetadata(auth.value)]);
    }
    else if (auth.authType === authentication.BASIC) {
      const user = auth.value;
      metadata.push([MESSAGE_RSOCKET_AUTHENTICATION, encodeSimpleAuthMetadata(user.username, user.password)]);
    }
  }

  if (route)
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

