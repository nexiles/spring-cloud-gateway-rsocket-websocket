<template>
  <q-page class="flex">
    <div class="col-1 overflow-auto" style="background: #3d3e4b">
      <q-btn
        class="row q-ma-sm control-button"
        color="blue"
        label="Connect & Subscribe"
        @click="connectRSocket"
        v-show="!connected"
      />
      <q-btn
        class="row q-ma-sm control-button"
        color="green"
        icon-right="send"
        label="Create new order"
        @click="createNewOrder"
      />
    </div>
    <q-table
      class="col"
      style="width: 100%; height: 100vh"
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
export default {
  name: 'RSocket',
  data() {
    return {
      data: [],
      connected: false,
      rowsPerPage: [0],
      route: "orders",
      javaMaxInteger: 2147483647,
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
  methods: {
    createNewOrder() {
      this.axios.get("/server/orders/new")
    },
    connectRSocket() {

      this.$rsocketclient.connect().then(
        socket => {

          socket.connectionStatus().subscribe(event => {
            const kind = event.kind;
            console.log(kind);
            if (kind === "CONNECTED")
              this.connected = true;
            else if (kind === "CLOSED")
              this.connected = false;
          });

          socket
            .requestStream({
              data: this.$encodejson({jsclient:"request"}),
              metadata: this.$encodersocketroute(this.route),
            })
            .subscribe({
              onComplete: () =>
                console.log('Request-stream completed'),
              onError: error =>{
                console.error(`Request-stream error:${error.message}`)
                const details = JSON.stringify(error.source);
                console.log("ErrorDetails: " + details)
              },
              onNext: value => {
                console.log('Data: %s - Metadata: %s', value.data, value.metadata);
                this.data.push(JSON.parse(value.data))
              },
              onSubscribe: sub => {
                console.log('Request-stream subscribe to: ' + this.javaMaxInteger + " messages");
                sub.request(this.javaMaxInteger);
              }
            });
        },
        error => {
          console.log('error:', error);
        },
      );
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

.control-button
  width: calc(100% - 20px)
  max-height: 60px

</style>
