import datepicker from "vuejs-datepicker"
import Spinner from 'vue-simple-spinner'
import leyend from '../leyend/leyend.vue'


export default {
  data(){
    return{
      title:'Lugares',
      users:[]
    }
  },
  mounted:function(){
    console.log('Estadisticas.vue');
    // GET /someUrl
    this.$http.get('http://kamino.diinf.usach.cl/redes-0.0.1-SNAPSHOT/lugares/')
    .then(response=>{
       // get body data
      this.users = response.data;
     console.log('users',this.users)
    }, response=>{
       // error callback
       console.log('error cargando lista');
    })
  }
}

