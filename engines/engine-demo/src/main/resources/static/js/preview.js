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
                var that = this
                $.ajax({
                    url: '/pio/query/detail',
                    beforeSend: function(req) {
                        req.setRequestHeader('Content-Type', 'application/json')
                    },
                    data: JSON.stringify({
                        'item_id': id, 'num': 5, 'type': 'detail_query'
                    }),
                    dataType: 'json',
                    type: 'post',
                    success: function(data) {
                        var temp = []
                        for (var m = 0; m < data.related_item_id.length; m++) {
                            that.queryOmdbByMovId(data.related_item_id[m], temp)
                        }
                        that.relatedFilms = temp
                    }
                })
            },

            queryFp: function(id) {
                var that = this
                $.ajax({
                    url: '/pio/query/itemfp',
                    beforeSend: function(req) {
                        req.setRequestHeader('Content-Type', 'application/json')
                    },
                    data: JSON.stringify({
                        'item_id': id, 'num': 8, 'type': 'fp_query'
                    }),
                    dataType: 'json',
                    type: 'post',
                    success: function(data) {
                        var temp = []
                        for (var m = 0; m < data.item_id.length; m++) {
                            that.queryOmdbByMovId(data.item_id[m], temp)
                        }
                        that.fpFilms = temp
                    }
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

            queryOmdbById: function (imdbId) {
                var that = this
                $.ajax({
                    url: '/pio/query/movie/infoById/' + imdbId,
                    dataType: 'json',
                    type: 'get',
                    success: function(data) {
                        that.filmData = data
                    }
                })
            },

            getFilmData: function (id) {
                var that = this
                $.ajax({
                    url: '/pio/query/movie/infoByMovId/' + id,
                    dataType: 'json',
                    type: 'get',
                    success: function(data) {
                        that.filmData = data
                    }
                })
            },

            queryOmdbByMovId: function (id, temp) {
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

    detailUR.getFilmData(qs("id"))
    detailUR.queryRelated(qs("id"))
    detailUR.clickPost(qs("id"))
    detailUR.queryFp(qs("id"))
});
