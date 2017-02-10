function qs(key) {
    key = key.replace(/[*+?^$.\[\]{}()|\\\/]/g, "\\$&") // escape RegEx meta chars
    var match = location.search.match(new RegExp("[?&]" + key + "=([^&]+)(&|$)"))
    return match && decodeURIComponent(match[1].replace(/\+/g, " "))
}

$(document).ready(function() {
    $().UItoTop({ easingType: 'easeOutQuart' });

    var detailUR = new Vue({
        el: '#app',
        data: {
            searchItems: [],
            userSearchItems: [],
            userId: window.localStorage.getItem("userId") || ''
        },
        methods: {
            checkOut: function() {
                window.localStorage.setItem("userId", "")
            },
            search: function (text) {
                var that = this
                $.ajax({
                    url: '/pio/query/itemSearch',
                    beforeSend: function(req) {
                        req.setRequestHeader('Content-Type', 'application/json')
                    },
                    data: JSON.stringify({
                        'item_name': text, 'num': 30, 'type': 'search_query'
                    }),
                    dataType: 'json',
                    type: 'post',
                    success: function(data) {
                        var temp = []
                        for (var m = 0; m < data.item_id.length; m++) {
                            that.queryOmdb(data.item_id[m], temp)
                        }
                        that.searchItems = temp
                    }
                })
            },

            userSearch: function (text) {
                var that = this
                $.ajax({
                    url: '/pio/query/userSearch',
                    beforeSend: function(req) {
                        req.setRequestHeader('Content-Type', 'application/json')
                    },
                    data: JSON.stringify({
                        'user_id': that.userId, 'item_name': text, 'num': 8, 'type': 'userHistory_query'
                    }),
                    dataType: 'json',
                    type: 'post',
                    success: function(data) {
                        var temp = []
                        for (var m = 0; m < data.item_id.length; m++) {
                            that.queryOmdb(data.item_id[m], temp)
                        }
                        that.userSearchItems = temp
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
    detailUR.search(qs("text"))
    detailUR.userSearch(qs("text"))
});

