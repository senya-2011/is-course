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

window.submitImport = async function(event) {
    event.preventDefault();
    var fileInput = document.getElementById('file');
    if (!fileInput || !fileInput.files || !fileInput.files.length) {
        alert('Choose a .json file');
        return;
    }
    var fd = new FormData();
    fd.append('file', fileInput.files[0], fileInput.files[0].name);
    try {
        var resp = await fetch('/import/humans', { method: 'POST', body: fd });
        var data = await resp.json();
        if (resp.ok) {
            alert('Imported: ' + (data.imported ?? 0));
            location.reload();
        } else {
            const errorMsg = data.message || data.error || 'Import failed';
            alert('Error: ' + errorMsg);
        }
    } catch (e) {
        alert(e.message);
    }
};
