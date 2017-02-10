
$(function() {
    $('#slider').nivoSlider();

    $().UItoTop({ easingType: 'easeOutQuart' });

    var detailUR = new Vue({
        el: '#app',
        data: {
            popItems:[],
            customItems: [],
            cacheItems: [],
            onlineItems: [],
            userId: window.localStorage.getItem("userId") || ''
        },
        methods: {
            checkOut: function() {
                window.localStorage.setItem("userId", "")
            },

            pop: function (category) {
                var that = this
                $.ajax({
                    url: '/pio/query/itempop',
                    beforeSend: function(req) {
                        req.setRequestHeader('Content-Type', 'application/json')
                    },
                    data: JSON.stringify({
                        'category': category, 'num': 14, 'type': 'pop_query'
                    }),
                    dataType: 'json',
                    type: 'post',
                    success: function(data) {
                        var temp = []
                        for (var m = 0; m < data.item_id.length; m++) {
                            that.queryOmdb(data.item_id[m], temp)
                        }
                        that.popItems = temp
                    }
                })
            },

            custom: function () {
                var that = this
                if (!that.userId) {
                    return
                }

                $.ajax({
                    url: '/pio/query/als',
                    beforeSend: function(req) {
                        req.setRequestHeader('Content-Type', 'application/json')
                    },
                    data: JSON.stringify({
                        'user_id': that.userId, 'num': 14, "type":"als_query"
                    }),
                    dataType: 'json',
                    type: 'post',
                    success: function(data) {
                        var temp = []
                        for (var m = 0; m < data.item_id.length; m++) {
                            that.queryOmdb(data.item_id[m], temp)
                        }
                        that.customItems = temp
                    }
                })
            },

            online: function () {
                var that = this
                if (!that.userId) {
                    return
                }

                $.ajax({
                    url: '/pio/query/als',
                    beforeSend: function(req) {
                        req.setRequestHeader('Content-Type', 'application/json')
                    },
                    data: JSON.stringify({
                        'user_id': that.userId, 'num': 14, "type":"als_query"
                    }),
                    dataType: 'json',
                    type: 'post',
                    success: function(data) {
                        var temp = []
                        for (var m = 0; m < data.item_id.length; m++) {
                            that.cacheItems.push(data.item_id[m])
                        }
                        $.ajax({
                            url: '/pio/query/click/request',
                            beforeSend: function(req) {
                                req.setRequestHeader('Content-Type', 'application/json')
                            },
                            data: JSON.stringify({
                                'userId': that.userId, "items":that.cacheItems,
                            }),
                            dataType: 'json',
                            type: 'post',
                            success: function(data) {
                                var temp = []
                                console.log(that.cacheItems)
                                for (var m = 0; m < data.item_id.length; m++) {
                                    that.queryOmdb(data.item_id[m], temp)
                                }
                                that.onlineItems = temp
                            }
                        })

                    }
                })
            },

            queryOmdb: function (id, temp) {
                $.ajax({
                    url: '/pio/query/movie/infoByMovId/' + escape(id),
                    dataType: 'json',
                    type: 'get',
                    success: function(data) {
                        data.id = id
                        temp.push(data)
                    }
                })
            }
        }
    })
    detailUR.pop("Action")
    detailUR.custom()
    detailUR.online()


    $('.search-submit').click(function(ev){
        ev.stopPropagation()
        ev.preventDefault()
        location.href = '/search.html?text=' + $('.search-keyword').val()
    })
})
