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
            userId: window.localStorage.getItem("userId") || '',
            filmData: {},
            relatedFilms: [],
            fpFilms: []
        },
        methods: {
            checkOut: function() {
                window.localStorage.setItem("userId", "")
            },

            queryRelated: function(id) {
                var this0 = this
                $.ajax({
                    url: '/pio/query/detail',
                    beforeSend: function(req) {
                        req.setRequestHeader('Content-Type', 'application/json')
                    },
                    data: JSON.stringify({
                        'item_id': id, 'num': 5, 'type': 'detail_query'
                    }),
                    dataType: 'json',
                    type: 'post'
                }).then(function (data) {
                    var defs = data.related_item_id.map(this0.queryMovieInfo)
                    return $when(defs).then(function (results) {
                        this0.relatedFilms = results.map(function (movieInfo, idx) {
                            return _.assign({id: data.related_item_id[idx]}, movieInfo[0])
                        })
                    })
                })
            },

            queryFp: function(id) {
                var this0 = this
                $.ajax({
                    url: '/pio/query/itemfp',
                    beforeSend: function(req) {
                        req.setRequestHeader('Content-Type', 'application/json')
                    },
                    data: JSON.stringify({
                        'item_id': id, 'num': 8, 'type': 'fp_query'
                    }),
                    dataType: 'json',
                    type: 'post'
                }).then(function (data) {
                    var defs = data.item_id.map(this0.queryMovieInfo)
                    return $when(defs).then(function (results) {
                        this0.fpFilms = results.map(function (movieInfo, idx) {
                            return _.assign({id: data.item_id[idx]}, movieInfo[0])
                        })
                    })
                })
            },

            clickPost: function(item_id) {
                var that = this
                $.ajax({
                    url: '/pio/query/click/submit',
                    beforeSend: function(req) {
                        req.setRequestHeader('Content-Type', 'application/json')
                    },
                    data: JSON.stringify({
                        'userId': that.userId, 'itemId': item_id
                    }),
                    dataType: 'json',
                    type: 'post',
                    success: function(data) {
                        console.log("click behavior")
                    }
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

    detailUR.queryMovieInfo(qs('id')).then(function (data) {
       detailUR.$data.filmData = data
    })
    detailUR.queryRelated(qs("id"))
    detailUR.clickPost(qs("id"))
    detailUR.queryFp(qs("id"))
});
