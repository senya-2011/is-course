(function() {
    if (!('EventSource' in window)) return;
    try {
        var es = new EventSource('/sse');
        es.addEventListener('hello', function() { });
        es.addEventListener('data_changed', function() {
            location.reload();
        });
        es.onerror = function() { };
    } catch (e) {
        console.error('SSE connection error:', e);
    }
})();
