$.ajax({
    url: '/components/card.html',
    type: 'get'
}).then(function (data) {
    Vue.component('movie-card', {
        template: data,
        props: {
            movieId: String,
            movieInfoInit: Object,
            displayGenre: Boolean
        },
        data: function () {
            return {
                movieInfo: {}
            }
        },
        mounted: function () {
            if (this.movieInfoInit) {
                this.movieInfo = this.movieInfoInit
                return
            }
            var this0 = this
            $.ajax({
                url: '/pio/query/movie/infoByMovId/' + this.movieId,
                dataType: 'json',
                type: 'get'
            }).then(function (data) {
                this0.movieInfo = data
            })
        },
        watch: {
            movieInfoInit: function (val, oldVal) {
                this.movieInfo = val
            }
        }
    })
})

