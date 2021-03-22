import axios from 'axios'
import VueAxios from 'vue-axios'

export default async ({Vue}) => {
  Vue.use(VueAxios, axios);
  axios.interceptors.response.use(function (response) {
    return response;
  }, function (error) {
    return error;
  })
}
