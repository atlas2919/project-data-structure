const API   = window.location.origin;
const algos = ['heap', 'bubble', 'select'];
const names = { heap: 'heapsort', bubble: 'bubblesort', select: 'selectionsort' };

let algo    = 'heap';
let steps   = [];
let idx     = 0;
let playing = false;
let timer   = null;
let arr     = [];
let t0      = null;

function spd() {
    return [500, 300, 160, 80, 35][+document.getElementById('spd').value - 1];
}

async function nuevoArreglo() {
    resetAll();
    try {
        const r = await fetch(API + '/nuevo');
        const d = await r.json();
        arr = d.arr;
        renderBars(arr, arr.map(() => '#B4B2A9'));
        log('nuevo arreglo: [' + arr.join(', ') + ']');
        clearErr();
    } catch (e) {
        showErr('No se puede conectar al servidor. ¿Está corriendo?');
    }
}

async function cargarPasos() {
    try {
        const r = await fetch(API + '/pasos?algo=' + algo);
        steps = await r.json();
        idx = 0;
        clearErr();
        return true;
    } catch (e) {
        showErr('Error al obtener pasos del servidor.');
        return false;
    }
}

async function togglePlay() {
    if (playing) { stopPlay(); return; }
    if (!steps.length) {
        const ok = await cargarPasos();
        if (!ok) return;
    }
    playing = true;
    t0 = performance.now();
    document.getElementById('btnPlay').textContent = '⏸ pausar';
    document.getElementById('btnPlay').classList.add('active');
    tick();
}

function tick() {
    if (!playing || idx >= steps.length) { stopPlay(); return; }
    applyStep(steps[idx++]);
    timer = setTimeout(tick, spd());
}

function stopPlay() {
    playing = false;
    clearTimeout(timer);
    document.getElementById('btnPlay').textContent = '▶ iniciar';
    document.getElementById('btnPlay').classList.remove('active');
}

async function stepOne() {
    if (!steps.length) {
        const ok = await cargarPasos();
        if (!ok) return;
        t0 = performance.now();
    }
    if (idx < steps.length) applyStep(steps[idx++]);
}

function resetAll() {
    stopPlay();
    steps = []; idx = 0; t0 = null;
    ['Heap', 'Bubble', 'Select'].forEach(id => setCard(id, ''));
    if (arr.length) renderBars(arr, arr.map(() => '#B4B2A9'));
    setLabel(names[algo] + ' — listo');
    setStat('h', '—', '—', '—');
    setStat('b', '—', '—', '—');
    setStat('s', '—', '—', '—');
}

function cycleAlgo() {
    algo = algos[(algos.indexOf(algo) + 1) % algos.length];
    document.getElementById('btnAlgo').textContent = names[algo];
    resetAll();
}

function applyStep(s) {
    renderBars(s.arr, s.colors);
    const prefix = algo === 'heap' ? 'h' : algo === 'bubble' ? 'b' : 's';
    const cardId = algo === 'heap' ? 'Heap' : algo === 'bubble' ? 'Bubble' : 'Select';
    setStat(prefix, s.cmp, s.swp,
            s.done && t0 ? Math.round(performance.now() - t0) + 'ms' : '—');
    setCard(cardId, s.done ? 'done' : 'active');
    const phase = s.phase === 1 ? ' [fase 1: heapify]'
                : s.phase === 2 ? ' [fase 2: extracción]' : '';
    setLabel(names[algo] + phase + (s.done ? ' ✓' : ''));
    const tag = s.phase === 1 ? '<heapify> '
              : s.phase === 2 ? '<extracción> ' : '';
    log(tag + s.msg, s.done);
}

function renderBars(a, colors) {
    const wrap = document.getElementById('barsWrap');
    const mx = Math.max(...a) || 1;
    wrap.innerHTML = '';
    a.forEach((v, i) => {
        const bar = document.createElement('div');
        bar.className = 'bar';
        bar.style.height = Math.round((v / mx) * 185 + 8) + 'px';
        bar.style.background = colors[i] || '#B4B2A9';
        const lbl = document.createElement('div');
        lbl.className = 'bval';
        lbl.textContent = v;
        bar.appendChild(lbl);
        wrap.appendChild(bar);
    });
}

function setLabel(t)  { document.getElementById('algoLabel').textContent = t; }
function showErr(msg) { document.getElementById('errBox').textContent = msg; }
function clearErr()   { document.getElementById('errBox').textContent = ''; }

function setStat(p, cmp, swp, time) {
    const el = (id) => document.getElementById(id);
    if (el(p+'cmp'))  el(p+'cmp').textContent  = cmp;
    if (el(p+'swp'))  el(p+'swp').textContent  = swp;
    if (el(p+'time')) el(p+'time').textContent = time;
}

function setCard(id, state) {
    const el = document.getElementById('card' + id);
    if (el) el.className = 'card' + (state ? ' ' + state : '');
}

function log(msg, hl = false) {
    const box = document.getElementById('logBox');
    const line = document.createElement('div');
    if (hl) line.className = 'hl';
    line.textContent = msg;
    box.appendChild(line);
    box.scrollTop = box.scrollHeight;
    while (box.children.length > 25) box.removeChild(box.firstChild);
}

nuevoArreglo();