$(function() {
    $().UItoTop({ easingType: 'easeOutQuart' });

    var labelUR = new Vue({
        el: '#app',
        data: {
            categories: [
                        "准备怀孕圈", "难孕难育圈", "孕8-10月圈", "生男生女圈",
                        "宝宝取名圈", "宝宝营养辅食", "宝宝常见病圈", "早教幼教圈"
            ],
            title: '',
            content: '',
            hint: '',
            result: '',
            labels:[],
            userId: window.localStorage.getItem("userId") || ''
        },
        methods: {
            getlabel: function () {
                var this0 = this
                var title = this0.title
                return $.ajax({
                    url: '/pio/query/artclu',
                    beforeSend: function(req) {
                        req.setRequestHeader('Content-Type', 'application/json')
                    },
                    data: JSON.stringify({
                        'title': title, 'content': this0.content, 'type': 'artiCluster_query'
                    }),
                    dataType: 'json',
                    type: 'post'
                }).then(function (data) {
                    this0.hint = title + ' 的预测结果: '
                    this0.result = data.label
                })
            },

        }
    })
    labelUR.getlabel

})
