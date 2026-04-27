const API = window.location.origin;
const algos = ["heap", "bucket", "quick"];
const names = {
  heap: "heapsort",
  bucket: "bucketsort",
  quick: "quicksort",
};

const prefixMap = { heap: "h", bucket: "bu", quick: "q" };
const cardMap = { heap: "Heap", bucket: "Bucket", quick: "Quick" };

let algo = "heap";
let steps = [];
let idx = 0;
let playing = false;
let timer = null;
let arr = [];
let t0 = null;
let loading = false;

function spd() {
  const base = [500, 300, 160, 80, 35][
    +document.getElementById("spd").value - 1
  ];
  if (arr.length > 200) return Math.min(base, 10);
  if (arr.length > 50) return Math.min(base, 35);
  return base;
}

// ── Bloquear / desbloquear botones ────────────────────────────────────────────
function setButtonsDisabled(disabled) {
  ["btnPlay", "btnStep", "btnAlgo"].forEach((id) => {
    const el = document.getElementById(id);
    if (el) el.disabled = disabled;
  });
  const sizeInput = document.getElementById("arrSize");
  if (sizeInput) sizeInput.disabled = disabled;
}

// ── Nuevo arreglo ─────────────────────────────────────────────────────────────
async function nuevoArreglo() {
  if (loading) return;
  resetAll();
  const size = Math.min(
    1000,
    Math.max(2, +document.getElementById("arrSize").value || 14),
  );
  try {
    const r = await fetch(API + "/nuevo?size=" + size);
    const d = await r.json();
    arr = d.arr;
    renderBars(
      arr,
      arr.map(() => "#B4B2A9"),
    );
    log("nuevo arreglo de " + arr.length + " elementos");
    clearErr();
  } catch (e) {
    showErr("No se puede conectar al servidor. ¿Está corriendo?");
  }
}

// ── Cargar pasos ──────────────────────────────────────────────────────────────
async function cargarPasos() {
  if (loading) return false;
  loading = true;
  setButtonsDisabled(true);
  document.getElementById("btnPlay").textContent = "⏳ cargando...";
  try {
    const r = await fetch(API + "/pasos?algo=" + algo + "&size=" + arr.length);
    steps = await r.json();
    idx = 0;
    clearErr();
    return true;
  } catch (e) {
    showErr("Error al obtener pasos del servidor.");
    return false;
  } finally {
    loading = false;
    setButtonsDisabled(false);
    document.getElementById("btnPlay").textContent = "▶ iniciar";
  }
}

// ── Play / Pause ──────────────────────────────────────────────────────────────
async function togglePlay() {
  if (playing) {
    stopPlay();
    return;
  }
  if (loading) return;
  if (!steps.length) {
    const ok = await cargarPasos();
    if (!ok) return;
  }
  playing = true;
  t0 = performance.now();
  document.getElementById("btnPlay").textContent = "⏸ pausar";
  document.getElementById("btnPlay").classList.add("active");
  tick();
}

function tick() {
  if (!playing || idx >= steps.length) {
    stopPlay();
    return;
  }
  applyStep(steps[idx++]);
  timer = setTimeout(tick, spd());
}

function stopPlay() {
  playing = false;
  clearTimeout(timer);
  document.getElementById("btnPlay").textContent = "▶ iniciar";
  document.getElementById("btnPlay").classList.remove("active");
}

// ── Paso a paso ───────────────────────────────────────────────────────────────
async function stepOne() {
  if (playing || loading) return;
  if (!steps.length) {
    const ok = await cargarPasos();
    if (!ok) return;
    t0 = performance.now();
  }
  if (idx < steps.length) applyStep(steps[idx++]);
}

// ── Reiniciar ─────────────────────────────────────────────────────────────────
function resetAll() {
  stopPlay();
  steps = [];
  idx = 0;
  t0 = null;
  setButtonsDisabled(false);
  ["Heap", "Bucket", "Quick"].forEach((id) => setCard(id, ""));
  if (arr.length)
    renderBars(
      arr,
      arr.map(() => "#B4B2A9"),
    );
  setLabel(names[algo] + " — listo");
  ["h", "bu", "q"].forEach((p) => setStat(p, "—", "—", "—"));
  showActiveCard();
}

// ── Ciclar algoritmo ──────────────────────────────────────────────────────────
function cycleAlgo() {
  if (loading) return;
  algo = algos[(algos.indexOf(algo) + 1) % algos.length];
  document.getElementById("btnAlgo").textContent = names[algo];
  resetAll();
  showActiveCard();
}

// ── Aplicar paso ──────────────────────────────────────────────────────────────
function applyStep(s) {
  renderBars(s.arr, s.colors);
  const prefix = prefixMap[algo];
  const cardId = cardMap[algo];
  setStat(
    prefix,
    s.cmp,
    s.swp,
    s.done && t0 ? Math.round(performance.now() - t0) + "ms" : "—",
  );
  setCard(cardId, s.done ? "done" : "active");
  const phase =
    s.phase === 1
      ? " [fase 1: distribución]"
      : s.phase === 2
        ? " [fase 2: reconstrucción]"
        : "";
  setLabel(names[algo] + phase + (s.done ? " ✓" : ""));
  const tag = s.phase === 1 ? "<fase 1> " : s.phase === 2 ? "<fase 2> " : "";
  log(tag + s.msg, s.done);
}

// ── Render barras ─────────────────────────────────────────────────────────────
function renderBars(a, colors) {
  const wrap = document.getElementById("barsWrap");
  const mx = Math.max(...a) || 1;
  wrap.innerHTML = "";
  const showLabel = a.length <= 30;
  a.forEach((v, i) => {
    const bar = document.createElement("div");
    bar.className = "bar";
    bar.style.height = Math.round((v / mx) * 185 + 8) + "px";
    bar.style.background = colors[i] || "#B4B2A9";
    if (showLabel) {
      const lbl = document.createElement("div");
      lbl.className = "bval";
      lbl.textContent = v;
      bar.appendChild(lbl);
    }
    wrap.appendChild(bar);
  });
}

// ── UI helpers ────────────────────────────────────────────────────────────────
function setLabel(t) {
  document.getElementById("algoLabel").textContent = t;
}
function showErr(msg) {
  document.getElementById("errBox").textContent = msg;
}
function clearErr() {
  document.getElementById("errBox").textContent = "";
}

function showActiveCard() {
  ["Heap", "Bucket", "Quick"].forEach((id) => {
    const el = document.getElementById("card" + id);
    if (el) el.className = "card";
  });
  const el = document.getElementById("card" + cardMap[algo]);
  if (el) el.classList.add("active");
}

function setStat(p, cmp, swp, time) {
  const el = (id) => document.getElementById(id);
  if (el(p + "cmp")) el(p + "cmp").textContent = cmp;
  if (el(p + "swp")) el(p + "swp").textContent = swp;
  if (el(p + "time")) el(p + "time").textContent = time;
}

function setCard(id, state) {
  const el = document.getElementById("card" + id);
  if (el) el.className = "card" + (state ? " " + state : "");
}

function log(msg, hl = false) {
  const box = document.getElementById("logBox");
  const line = document.createElement("div");
  if (hl) line.className = "hl";
  line.textContent = msg;
  box.appendChild(line);
  box.scrollTop = box.scrollHeight;
  while (box.children.length > 25) box.removeChild(box.firstChild);
}

// ── Inicio ────────────────────────────────────────────────────────────────────
nuevoArreglo();
showActiveCard();
