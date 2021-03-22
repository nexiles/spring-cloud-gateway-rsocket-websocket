<template>
  <q-page class="flex row">
    <div class="col-2 overflow-auto" style="background: #3d3e4b">
      <q-card dark bordered flat square class="bg-grey-9 my-card">
        <q-card-section>
          <div class="text-h6 text-center">Identity Provider</div>
        </q-card-section>

        <q-separator dark inset=""/>

        <q-card-section>
          <q-btn-toggle
            v-model="identityProvider"
            spread
            no-caps
            disable
            toggle-color="orange"
            color="white"
            text-color="black"
            :options="identityProviders"
          />
        </q-card-section>
      </q-card>
      <q-input class="q-ma-sm"
               dark v-model="user"
               filled hint="Username"
               v-show="!authenticated && !isIdentityProviderKeyCloak"
      />
      <q-input class="q-ma-sm"
               dark v-model="password"
               filled
               :type="showPassword ? 'password' : 'text'"
               hint="Password"
               v-show="!authenticated && !isIdentityProviderKeyCloak"
      >
        <template v-slot:append>
          <q-icon
            :name="showPassword ? 'visibility_off' : 'visibility'"
            class="cursor-pointer"
            @click="showPassword = !showPassword"
          />
        </template>
      </q-input>
      <q-btn
        class="row q-ma-sm control-width"
        color="black"
        label="Login"
        @click="isAuthenticated"
        v-show="!authenticated && !isIdentityProviderKeyCloak && user && password"
      />
      <q-separator v-show="!connected" dark/>
      <q-btn
        class="row q-ma-sm control-width"
        color="blue"
        label="Connect & Subscribe"
        @click="connectRSocket"
        v-show="!connected && authenticated"
      />
      <q-btn
        class="row q-ma-sm control-width"
        color="orange"
        label="Subscribe to ALL"
        @click="subscribeAll"
        v-show="connected && !allSubscribed && !lotrSubscribed && !gotSubscribed"
      />
      <q-btn
        class="row q-ma-sm control-width"
        color="orange"
        label="Subscribe to LOTR"
        @click="subscribeLOTR"
        v-show="connected && !allSubscribed && !lotrSubscribed"
      />
      <q-btn
        class="row q-ma-sm control-width"
        color="orange"
        label="Subscribe to GOT"
        @click="subscribeGOT"
        v-show="connected && !allSubscribed && !gotSubscribed"
      />
      <q-separator v-show="connected" dark/>
      <q-btn
        class="row q-ma-sm control-width"
        color="green"
        label="Create any order"
        @click="createNewOrder"
        v-show="connected"
        icon-right="send"
      />
      <q-btn
        class="row q-ma-sm control-width"
        color="green"
        label="Create LOTR order"
        @click="createNewLOTROrder"
        v-show="connected"
        icon-right="send"
      />
      <q-btn
        class="row q-ma-sm control-width"
        color="green"
        label="Create GOT order"
        @click="createNewGOTOrder"
        v-show="connected"
        icon-right="send"
      />
    </div>
    <q-table
      class="col"
      style="width: 100%; height: calc(100vh - 50px)"
      title="New Orders"
      :data="data"
      :columns="columns"
      color="primary"
      row-key="name"
      dark
      hide-pagination
      :loading="connected"
      :rows-per-page-options=rowsPerPage
      virtual-scroll
    >
      <template v-slot:top-right>
        <q-checkbox size="xl" color="teal" v-model="connected" label="RSocket connected" disable/>
      </template>
    </q-table>
  </q-page>
</template>

<script>
import {orderKind} from "../js/Order"
import {User} from "../js/User"

export default {
  name: 'RSocket',
  data() {
    return {
      //setup
      rsocket: undefined,
      data: [],
      connected: false,
      //auth
      identityProviders: [
        {label: 'SpringSecurity', value: 'SpringSecurity'},
        {label: 'KeyCloak', value: 'KeyCloak'}
      ],
      identityProvider: undefined,
      currentUser: undefined,
      user: undefined,
      password: undefined,
      showPassword: true,
      authenticated: false,
      //subscription
      route: "orders",
      allSubscribed: false,
      lotrSubscribed: false,
      gotSubscribed: false,
      customMetadataKey: "custom-meta",
      javaMaxInteger: 2147483647,
      // table
      rowsPerPage: [0],
      columns: [
        {
          name: 'dateTime',
          required: true,
          label: 'DateTime',
          align: 'center',
          field: row => new Date(row.dateTime).toString(),
          sortable: true
        },
        {
          name: 'entry',
          required: true,
          label: 'Entry',
          align: 'center',
          field: row => row.entry,
          sortable: true
        },
        {
          name: 'number',
          required: true,
          label: 'Number',
          align: 'center',
          field: row => row.number,
          sortable: true
        },
        {
          name: 'kind',
          required: true,
          label: 'Kind',
          align: 'center',
          field: row => row.kind,
          sortable: true
        },
        {
          name: 'name',
          required: true,
          label: 'Name',
          align: 'center',
          field: row => row.name,
          sortable: true
        },
        {
          name: 'item',
          required: true,
          label: 'Item',
          align: 'center',
          field: row => row.item,
          sortable: true
        },
        {
          name: 'address',
          required: true,
          label: 'Address',
          align: 'left',
          field: row => row.address,
          sortable: true
        },
      ],
    }
  },
  computed: {
    currentUserName: function () {
      return this.currentUser?.username ?? "Not set";
    },
    isIdentityProviderKeyCloak: function () {
      return this.identityProvider === "KeyCloak"
    }
  },
  async mounted() {
    const identityProvider = await this.axios.get("/provider");
    this.identityProvider = identityProvider.data;
    console.log("IdentityProvider: " + this.identityProvider)
    this.authenticated = this.isIdentityProviderKeyCloak;
  },
  methods: {
    getUser() {
      if (this.user && this.password)
        return new User(this.user, this.password);
      return undefined;
    },
    async isAuthenticated() {
      const authenticated = await this.axios.get("/security/authenticated", {auth: this.getUser()});
      if (authenticated?.status === 200) {
        this.authenticated = true;
        this.$q.notify({
          message: "Successfully authenticated",
          type: "positive",
          icon: 'announcement'
        })
      } else {
        this.$q.notify({
          type: "negative",
          icon: 'announcement'
        })
      }
    },
    createNewOrder() {
      this.axios.get("/server/orders/new", {auth: this.getUser()})
    },
    createNewLOTROrder() {
      this.axios.get("/server/orders/new", {auth: this.getUser(), params: {kind: orderKind.LOTR}})
    },
    createNewGOTOrder() {
      this.axios.get("/server/orders/new", {auth: this.getUser(), params: {kind: orderKind.GOT}})
    },
    connectRSocket() {

      this.$setuprsocketclient(this.getUser())
        .connect().then(
        socket => {

          socket.connectionStatus().subscribe(event => {
            const kind = event.kind;
            console.log(kind);
            if (kind === "CONNECTED") {

              if (!this.connected) { // Fires twice
                this.connected = true;
                this.$q.notify({
                  spinner: true,
                  message: 'RSocket connected',
                  timeout: 2000
                })
              }
            } else if (kind === "CLOSED") {
              this.connected = false;
              this.$q.notify({
                type: 'warning',
                message: 'Connection closed'
              })
            } else if (kind === "ERROR") {
              this.$q.notify({
                type: 'negative',
                message: 'Connection error'
              })
            }
          });

          this.rsocket = socket;

        },
        error => {
          console.log('error:', error);
          this.$q.notify({
            type: 'negative',
            message: `Connection error. Authenticated? `
          })
        },
      );
    },
    subscribeAll() {
      this.rsocket
        .requestStream({
          data: this.$encodedata({jsclient: "request all"}),
          metadata: this.$encodemetadata(this.getUser(), this.routeWithIdentifier(orderKind.ALL), {data: "custom metadata value from js"}),
        })
        //.subscribe(subscription => this.subscriptionHandler(subscription))
        .subscribe(this.subscriptionHandler(orderKind.ALL));
    },
    subscribeLOTR() {
      this.rsocket
        .requestStream({
          data: this.$encodedata({jsclient: "request lotr"}),
          metadata: this.$encodemetadata(this.getUser(), this.routeWithIdentifier(orderKind.LOTR), {data: "custom metadata value from js"}),
        })
        //.subscribe(subscription => this.subscriptionHandler(subscription))
        .subscribe(this.subscriptionHandler(orderKind.LOTR));
    },
    subscribeGOT() {
      this.rsocket
        .requestStream({
          data: this.$encodedata({jsclient: "request got"}),
          metadata: this.$encodemetadata(this.getUser(), this.routeWithIdentifier(orderKind.GOT), {data: "custom metadata value from js"}),
        })
        //.subscribe(subscription => this.subscriptionHandler(subscription))
        .subscribe(this.subscriptionHandler(orderKind.GOT));
    },
    routeWithIdentifier(identifier) {
      return this.route + "." + identifier;
    },
    subscriptionHandler(kind) {
      // noinspection JSUnusedGlobalSymbols
      return {
        onComplete: () =>
          console.log('Request-stream completed'),
        onError: error => {
          console.error(`Request-stream error:${error.message}`)
          const details = JSON.stringify(error.source);
          console.log("ErrorDetails: " + details)
        },
        onNext: value => {
          console.log('Data: %s - Metadata: %s', value.data, value.metadata);
          this.data.push(JSON.parse(value.data))
        },
        onSubscribe: sub => {
          console.log('Request-stream subscribe to: ' + this.javaMaxInteger + " '" + kind + "' messages");

          this.$q.notify({
            message: "Subscribed to '" + kind + "' orders",
            icon: 'announcement'
          })

          switch (kind) {
            case orderKind.ALL: {
              this.allSubscribed = true;
              break;
            }
            case orderKind.LOTR: {
              this.lotrSubscribed = true;
              break;
            }
            case orderKind.GOT: {
              this.gotSubscribed = true;
              break;
            }
          }

          sub.request(this.javaMaxInteger);
        }
      };
    },
  }
}
</script>

<style lang="sass">

.my-sticky-header-table
  /* height or max-height is important */
  height: 500px

  .q-table__top,
  .q-table__bottom,
  thead tr:first-child th
    /* bg color is important for th; just specify one */
    background-color: #c1f4cd

  thead tr th
    position: sticky
    z-index: 1

  thead tr:first-child th
    top: 0

  /* this is when the loading indicator appears */




  &.q-table--loading thead tr:last-child th
    /* height of all previous header rows */
    top: 48px

.control-width
  width: calc(100% - 15px)
  max-height: 60px

</style>
