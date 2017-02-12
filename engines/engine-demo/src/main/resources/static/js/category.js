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
