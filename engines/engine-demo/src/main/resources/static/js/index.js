$(function() {
    $().UItoTop({ easingType: 'easeOutQuart' });

    var detailUR = new Vue({
        el: '#app',
        data: {
            categories: [
                "Action", "Adventure", "Animation", "Children's", "Comedy", "Crime", "Documentary", "Drama", "Fantasy",
                "Film-Noir", "Horror", "Musical", "Mystery", "Romance", "Sci-Fi", "Thriller", "War", "Western"
            ],
            popItems:[],
            customItems: [],
            onlineItems: [],
            globalRecommends: [],
            userId: window.localStorage.getItem("userId") || ''
        },
        methods: {
            checkOut: function() {
                window.localStorage.setItem("userId", "")
            },

            pop: function (category) {
                var this0 = this
                return $.ajax({
                    url: '/pio/query/itempop',
                    beforeSend: function(req) {
                        req.setRequestHeader('Content-Type', 'application/json')
                    },
                    data: JSON.stringify({
                        'category': category, 'num': 5, 'type': 'pop_query'
                    }),
                    dataType: 'json',
                    type: 'post'
                }).then(function (data) {
                    var defs = data.item_id.map(this0.queryMovieInfo)
                    return $when(defs).then(function (results) {
                        this0.popItems = results.map(function (movieInfo, idx) {
                            return _.assign({id: data.item_id[idx]}, movieInfo[0])
                        })

                        var dtd = $.Deferred();
                        this0.$nextTick(function () { dtd.resolve(); })
                        return dtd
                    })
                })
            },

            loadGlobalRecommends: function () {
                var this0 = this
                if (this0.userId) {
                    return
                }

                var category = _.sample(this.categories)

                var this0 = this
                $.ajax({
                    url: '/pio/query/itempop',
                    beforeSend: function(req) {
                        req.setRequestHeader('Content-Type', 'application/json')
                    },
                    data: JSON.stringify({
                        'category': category, 'num': 5, 'type': 'pop_query'
                    }),
                    dataType: 'json',
                    type: 'post'
                }).then(function (data) {
                    var defs = data.item_id.map(this0.queryMovieInfo)
                    return $when(defs).then(function (results) {
                        this0.globalRecommends = results.map(function (movieInfo, idx) {
                            return _.assign({id: data.item_id[idx]}, movieInfo[0])
                        })
                    })
                })
            },

            custom: function () {
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
                    var defs = data.item_id.map(this0.queryMovieInfo)
                    return $when(defs).then(function (results) {
                        this0.customItems = results.map(function (movieInfo, idx) {
                            return _.assign({id: data.item_id[idx]}, movieInfo[0])
                        })
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
                    return $when(defs).then(function (results) {
                        this0.onlineItems = results.map(function (movieInfo, idx) {
                            return _.assign({id: data.item_id[idx]}, movieInfo[0])
                        })
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
    detailUR.pop("Action").then(function() { $('#slider').nivoSlider() })
    detailUR.loadGlobalRecommends()
    detailUR.custom()
    detailUR.online()


    $('.search-submit').click(function(ev){
        ev.stopPropagation()
        ev.preventDefault()
        location.href = '/search.html?text=' + $('.search-keyword').val()
    })
})
