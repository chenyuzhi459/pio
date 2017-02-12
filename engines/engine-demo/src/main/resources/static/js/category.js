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
            popItemsSortCol: 'imdbRating',
            popItemsSortDirect: 'desc'
        },
        computed: {
            popItemsSorted: function () {
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
                var that = this
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
                            that.queryOmdb(data.item_id[m], temp)
                        }
                        that.popItems = temp
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
