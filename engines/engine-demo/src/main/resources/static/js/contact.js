$(function () {
    var detailUR = new Vue({
        el: '#app',
        data: {
            userId: ""
        },
        methods: {
            submit: function (e) {
                window.localStorage.setItem('userId', this.userId)
            }
        }
    })
})

$(document).ready(function() {
    $().UItoTop({ easingType: 'easeOutQuart' });

});
