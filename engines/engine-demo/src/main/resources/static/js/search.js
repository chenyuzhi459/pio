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
            onlineItems: [],
            userId: window.localStorage.getItem("userId") || ''
        },
        methods: {
            checkOut: function() {
                window.localStorage.setItem("userId", "")
            },
            search: function (text) {
                var this0 = this
                $.ajax({
                    url: '/pio/query/itemSearch',
                    beforeSend: function(req) {
                        req.setRequestHeader('Content-Type', 'application/json')
                    },
                    data: JSON.stringify({
                        'item_name': text, 'num': 30, 'type': 'search_query'
                    }),
                    dataType: 'json',
                    type: 'post'
                }).then(function (data) {
                    var defs = data.item_id.map(this0.queryMovieInfo)
                    return $.when.apply(null, defs).then(function () {
                        this0.searchItems = Array.prototype.slice.call(arguments).map(function (movieInfo, idx) {
                            return _.assign({id: data.item_id[idx]}, movieInfo[0])
                        })

                        var dtd = $.Deferred();
                        this0.$nextTick(function () { dtd.resolve(); })
                        return dtd
                    })
                })
            },

            userSearch: function (text) {
                var this0 = this
                if (!this0.userId) {
                    return
                }

                $.ajax({
                    url: '/pio/query/userSearch',
                    beforeSend: function(req) {
                        req.setRequestHeader('Content-Type', 'application/json')
                    },
                    data: JSON.stringify({
                        'user_id': this0.userId, 'item_name': text, 'num': 8, 'type': 'userHistory_query'
                    }),
                    dataType: 'json',
                    type: 'post'
                }).then(function (data) {
                    var defs = data.item_id.map(this0.queryMovieInfo)
                    return $.when.apply(null, defs).then(function () {
                        this0.userSearchItems = Array.prototype.slice.call(arguments).map(function (movieInfo, idx) {
                            return _.assign({id: data.item_id[idx]}, movieInfo[0])
                        })

                        var dtd = $.Deferred();
                        this0.$nextTick(function () { dtd.resolve(); })
                        return dtd
                    })
                })
            },

            online: function () {
                var this0 = this
                if (!this0.userId) {
                    return
                }

                return $.ajax({
                    url: '/pio/query/als',
                    beforeSend: function(req) {
                        req.setRequestHeader('Content-Type', 'application/json')
                    },
                    data: JSON.stringify({
                        'user_id': this0.userId, 'num': 5, "type":"als_query"
                    }),
                    dataType: 'json',
                    type: 'post'
                }).then(function (data) {
                    var cacheItems = data.item_id
                    return $.ajax({
                        url: '/pio/query/click/request',
                        beforeSend: function(req) {
                            req.setRequestHeader('Content-Type', 'application/json')
                        },
                        data: JSON.stringify({
                            'userId': this0.userId, "items": cacheItems
                        }),
                        dataType: 'json',
                        type: 'post'
                    })
                }).then(function (data) {
                    var defs = data.item_id.map(this0.queryMovieInfo)
                    return $.when.apply(null, defs).then(function () {
                        this0.onlineItems = Array.prototype.slice.call(arguments).map(function (movieInfo, idx) {
                            return _.assign({id: data.item_id[idx]}, movieInfo[0])
                        })

                        var dtd = $.Deferred();
                        this0.$nextTick(function () { dtd.resolve(); })
                        return dtd
                    })
                })
            },

            queryMovieInfo: function (id) {
                return $.ajax({
                    url: '/pio/query/movie/infoByMovId/' + id,
                    dataType: 'json',
                    type: 'get'
                })
            }
        }
    })
    detailUR.search(qs("text"))
    detailUR.userSearch(qs("text"))
    detailUR.online()

});

