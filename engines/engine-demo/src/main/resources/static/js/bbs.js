$(function() {
    $().UItoTop({ easingType: 'easeOutQuart' });

    var itemUR = new Vue({
        el: '#app',
        data: {
            title: '',
            content: '',
            hint: '',
            result: [],
            artres: []
//            userId: window.localStorage.getItem("userId") || ''
        },
        methods: {
            getitems: function () {
                var this0 = this
                var title = this0.title
                return $.ajax({
                    url: '/pio/query/bbs',
                    beforeSend: function(req) {
                        req.setRequestHeader('Content-Type', 'application/json')
                    },
                    data: JSON.stringify({
                        'title': title, 'content': this0.content, 'type': 'bbs_query'
                    }),
                    dataType: 'json',
                    type: 'post'
                }).then(function (data) {
                    this0.hint = title + ' 的预测结果: '
                    this0.result = data.items
                    this0.artres = data.articles
                })
            },

        }
    })
    itemUR.getitems

})
