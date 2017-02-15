var dict = {
    "Action": "动作类",
    "Adventure": "冒险类",
    "Animation": "动画类",
    "Children's": "儿童类",
    "Comedy": "喜剧类",
    "Crime": "犯罪类",
    "Documentary": "记录类",
    "Drama": "剧情类",
    "Fantasy": "幻想类",
    "Film-Noir": "黑色类",
    "Horror": "惊悚类",
    "Musical": "歌舞类",
    "Mystery": "推理类",
    "Romance": "浪漫类",
    "Sci-Fi": "科幻类",
    "Thriller": "惊悚类",
    "War": "战争类",
    "Western": "西部类"
}


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

            translate: function (ca) {
                return dict[ca] || ca
            },

            pop: function (category) {
                var this0 = this
                return $.ajax({
                    url: '/pio/query/itempop',
                    beforeSend: function(req) {
                        req.setRequestHeader('Content-Type', 'application/json')
                    },
                    data: JSON.stringify({
                        'category': category, 'num': 10, 'type': 'pop_query'
                    }),
                    dataType: 'json',
                    type: 'post'
                }).then(function (data) {
                    var defs = data.item_id.map(this0.queryMovieInfo)
                    return $when(defs).then(function (results) {
                        this0.popItems = results.map(function (movieInfo, idx) {
                            return movieInfo && _.assign({id: data.item_id[idx]}, movieInfo)
                        }).filter(_.identity)

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
                        'category': category, 'num': 6, 'type': 'pop_query'
                    }),
                    dataType: 'json',
                    type: 'post'
                }).then(function (data) {
                    this0.globalRecommends = data.item_id
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
                        'user_id': this0.userId, 'num': 6, "type":"als_query"
                    }),
                    dataType: 'json',
                    type: 'post'
                }).then(function (data) {
                    this0.customItems = data.item_id
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
                        'user_id': this0.userId, 'num': 6, "type":"als_query"
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
                    this0.onlineItems = data.item_id
                })
            },

            queryMovieInfo: function (id) {
                return $.ajax({
                    url: '/pio/query/movie/infoByMovId/' + id,
                    dataType: 'text',
                    type: 'get'
                }).then(function (str) {
                    try {
                        return JSON.parse(str)
                    } catch (e) {
                        return null
                    }
                })
            }
        }
    })
    detailUR.pop("Action")
    detailUR.loadGlobalRecommends()
    detailUR.custom()
    detailUR.online()


    $('.search-submit').click(function(ev){
        ev.stopPropagation()
        ev.preventDefault()
        location.href = '/search.html?text=' + $('.search-keyword').val()
    })
})
