<template>
  <q-layout view="lHh Lpr lFf">
    <q-header elevated>
      <q-toolbar>
        <q-btn
          flat
          dense
          round
          icon="menu"
          aria-label="Menu"
          @click="leftDrawerOpen = !leftDrawerOpen"
        />

        <q-toolbar-title>
          RSocket Example
        </q-toolbar-title>

        <div>Quasar v{{ $q.version }}</div>
      </q-toolbar>
    </q-header>

    <q-drawer
      v-model="leftDrawerOpen"
      bordered
      content-class="bg-grey-1"
    >
      <q-list>
        <q-item-label
          header
          class="text-grey-8"
        >
          Essential Links
        </q-item-label>
        <EssentialLink
          v-for="link in quasarLinks"
          :key="link.title"
          v-bind="link"
        />
      </q-list>
      <q-separator/>

      <q-list>
        <q-item-label
          header
          class="text-grey-8"
        >
          RSocket Links
        </q-item-label>
        <EssentialLink
          v-for="link in rsocketLinks"
          :key="link.title"
          v-bind="link"
        />
      </q-list>
      <q-separator/>

    </q-drawer>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script>
import EssentialLink from 'components/EssentialLink.vue'

const quasarLinksData = [
  {
    title: 'Docs',
    caption: 'quasar.dev',
    icon: 'school',
    link: 'https://quasar.dev'
  },
  {
    title: 'Github',
    caption: 'github.com/quasarframework',
    icon: 'code',
    link: 'https://github.com/quasarframework'
  },
  {
    title: 'Forum',
    caption: 'forum.quasar.dev',
    icon: 'record_voice_over',
    link: 'https://forum.quasar.dev'
  },
  {
    title: 'Quasar Awesome',
    caption: 'Community Quasar projects',
    icon: 'favorite',
    link: 'https://awesome.quasar.dev'
  }
];

const rsocketLinksData  = [
  {
    title: 'Homepage',
    caption: 'rsocket.io',
    icon: 'school',
    link: 'https://rsocket.io/'
  },
  {
    title: 'RSocket Java',
    caption: 'github.com/rsocket/rsocket-java',
    icon: 'code',
    link: 'https://github.com/rsocket/rsocket-java'
  },
  {
    title: 'RSocket Spring',
    caption: 'RSocket Spring Docs',
    icon: 'book',
    link: 'https://docs.spring.io/spring-integration/docs/current/reference/html/rsocket.html'
  },
  {
    title: 'RSocket JS',
    caption: 'RSocket JS Client',
    icon: 'book',
    link: 'https://github.com/rsocket/rsocket-js'
  }
];

export default {
  name: 'MainLayout',
  components: { EssentialLink },
  data () {
    return {
      leftDrawerOpen: false,
      quasarLinks: quasarLinksData,
      rsocketLinks: rsocketLinksData,
    }
  }
}
</script>
