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
  },
   methods:{
    loadGraph:function(data){
      var canvas = document.getElementById("barChart");
var ctx = canvas.getContext('2d');
// We are only changing the chart type, so let's make that a global variable along with the chart object:
var chartType = 'bar';
var myBarChart;

// Global Options:
Chart.defaults.global.defaultFontColor = 'grey';
Chart.defaults.global.defaultFontSize = 16;

var data = {
  labels: ["MAÑANA", "TARDE", "NOCHE", "2008", "2009", "2010", "2011", "2012", "2013", "2014", "2015", "2016"],
  datasets: [{
    label: "UFO Sightings",
    fill: true,
    lineTension: 0.1,
    backgroundColor: "rgba(0,255,0,0.4)",
    borderColor: "green", // The main line color
    borderCapStyle: 'square',
    pointBorderColor: "white",
    pointBackgroundColor: "green",
    pointBorderWidth: 1,
    pointHoverRadius: 8,
    pointHoverBackgroundColor: "yellow",
    pointHoverBorderColor: "green",
    pointHoverBorderWidth: 2,
    pointRadius: 4,
    pointHitRadius: 10,
    data: [10, 13, 17, 12, 30, 47, 60, 120, 230, 300, 310, 400],
    spanGaps: true,
  }]
};

// Notice the scaleLabel at the same level as Ticks
var options = {
  scales: {
    yAxes: [{
      ticks: {
        beginAtZero: true
      }
    }]
  },
  title: {
    fontSize: 18,
    display: true,
    text: 'I want to believe !',
    position: 'bottom'
  }
};

      
  }
},
//We add an init function down here after the chart options are declared.



}


