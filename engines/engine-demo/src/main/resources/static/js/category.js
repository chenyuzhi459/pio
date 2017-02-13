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

function qs(key) {
    key = key.replace(/[*+?^$.\[\]{}()|\\\/]/g, "\\$&") // escape RegEx meta chars
    var match = location.search.match(new RegExp("[?&]" + key + "=([^&]+)(&|$)"))
    return match && decodeURIComponent(match[1].replace(/\+/g, " "))
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
            cacheItems: [],
            userId: window.localStorage.getItem("userId") || '',
            popItemsSortCol: '',
            popItemsSortDirect: '',
            currCategory: ''
        },
        computed: {
            popItemsSorted: function () {
                if (!this.popItemsSortCol || !this.popItemsSortDirect) {
                    return this.popItems
                }
                var col = this.popItemsSortCol
                var comparator = col === 'Released'
                    ? function (movie) { return new Date(movie[col]).getTime() }
                    : function (movie) { return movie[col] * 1 }
                return _.orderBy(this.popItems, comparator, this.popItemsSortDirect)
            }
        },
        methods: {
            checkOut: function() {
                window.localStorage.setItem("userId", "")
            },

            setPopItemsSortConfig: function (col, direct) {
                this.popItemsSortCol = col
                this.popItemsSortDirect = direct
            },

            pop: function (category) {
                var this0 = this
                this0.currCategory = category

                $.ajax({
                    url: '/pio/query/itempop',
                    beforeSend: function(req) {
                        req.setRequestHeader('Content-Type', 'application/json')
                    },
                    data: JSON.stringify({
                        'category': category, 'num': 15, 'type': 'pop_query'
                    }),
                    dataType: 'json',
                    type: 'post',
                    success: function(data) {
                        var temp = []
                        for (var m = 0; m < data.item_id.length; m++) {
                            this0.queryOmdb(data.item_id[m], temp)
                        }
                        this0.popItems = temp
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
    detailUR.pop(qs("category") || 'Action')


    $('.search-submit').click(function(ev){
        ev.stopPropagation()
        ev.preventDefault()
        location.href = '/search.html?text=' + $('.search-keyword').val()
    })
})
