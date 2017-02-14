var cache = {}

function getMovieInfo(id) {
    if (id in cache) {
        return $.when(cache[id])
    }
    return $.ajax({
        url: '/pio/query/movie/infoByMovId/' + id,
        dataType: 'json',
        type: 'get'
    }).then(function (data) {
        cache[id] = data
        return data
    })
}

$.ajax({
    url: '/components/card.html',
    type: 'get'
}).then(function (data) {
    Vue.component('movie-card', {
        template: data,
        props: {
            movieId: String,
            displayGenre: Boolean
        },
        data: function () {
            return {
                movieInfo: {}
            }
        },
        mounted: function () {
            var this0 = this
            getMovieInfo(this.movieId).then(function (data) {
                this0.movieInfo = data
            })
        },
        watch: {
            movieId: function (val, oldVal) {
                var this0 = this
                getMovieInfo(val).then(function (data) {
                    this0.movieInfo = data
                })
            }
        }
    })
})

