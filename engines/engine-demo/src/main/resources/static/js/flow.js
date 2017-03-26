$(function() {
    $().UItoTop({ easingType: 'easeOutQuart' });

    var flowFun = new Vue({
        el: '#app',
        data: {
            totalFlow: '',
            flowPackage: '[1, 150, 300, 500, 1024, 1228, 1433, 1638, 2048]',
            price:       '[1.06, 6.88, 10.58, 15.88, 31.75, 34.93, 38.1, 41.28, 47.63]',
            minPrice: '',
            bestCombin:''
        },
        methods: {
            getGroup: function () {
                var this0 = this
                return $.ajax({
                    url: '/pio/query/flow',
                    beforeSend: function(req) {
                        req.setRequestHeader('Content-Type', 'application/json')
                    },
                    data: JSON.stringify({
                        'totalFlow': JSON.parse(this0.totalFlow), 'flowPackage': JSON.parse(this0.flowPackage), 'price':JSON.parse(this0.price), 'type': 'flow_query'
                    }),
                    dataType: 'json',
                    type: 'post'
                }).then(function (data) {
                    this0.minPrice = data.minPrice
                    this0.bestCombin = data.bestCombin
                })
            },
        }
    })
    flowFun.getGroup

})
