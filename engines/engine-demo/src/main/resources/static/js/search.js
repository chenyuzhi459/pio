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
            searchItems: [],
            userSearchItems: [],
            userId: window.localStorage.getItem("userId") || ''
        },
        methods: {
            checkOut: function() {
                window.localStorage.setItem("userId", "")
            },
            search: function (text) {
                var this0 = this
                $.ajax({
                    url: '/pio/query/itemSearch',
                    beforeSend: function(req) {
                        req.setRequestHeader('Content-Type', 'application/json')
                    },
                    data: JSON.stringify({
                        'item_name': text, 'num': 18, 'type': 'search_query'
                    }),
                    dataType: 'json',
                    type: 'post'
                }).then(function (data) {
                    this0.searchItems = data.item_id
                })
            },

            userSearch: function (text) {
                var this0 = this
                if (!this0.userId) {
                    return
                }

                $.ajax({
                    url: '/pio/query/userSearch',
                    beforeSend: function(req) {
                        req.setRequestHeader('Content-Type', 'application/json')
                    },
                    data: JSON.stringify({
                        'user_id': this0.userId, 'item_name': text, 'num': 6, 'type': 'userHistory_query'
                    }),
                    dataType: 'json',
                    type: 'post'
                }).then(function (data) {
                    this0.userSearchItems = data.item_id
                })
            }
        }
    })
    detailUR.search(qs("text"))
    detailUR.userSearch(qs("text"))

});
