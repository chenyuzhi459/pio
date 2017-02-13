// need lodash and jquery

function $when(deferreds) {
    if (deferreds.length === 1) {
        return deferreds[0].then(function (result) {
            return [Array.prototype.slice.call(arguments)]
        })
    } else {
        return $.when.apply($, deferreds).then(function () {
            return Array.prototype.slice.call(arguments)
        })
    }
}