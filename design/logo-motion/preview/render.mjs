import { createRequire } from 'module';
const require = createRequire('/home/zen/github-readme-terminal/GitAscii/');
const { chromium } = require('playwright-core');
import { readFileSync, writeFileSync, mkdirSync, rmSync, existsSync } from 'fs';
import { execSync } from 'child_process';
import path from 'path';

const HERE = '/home/zen/Ana/design/logo-motion';
const FRAMES = path.join(HERE, 'preview/frames');
const FPS = 12.5;
const DUR = 3.6;          // كل حالة بنفس الدورة الرئيسية — إطار إلى إطار متطابقة
const STEPS = Math.round(DUR * FPS);   // 45 إطاراً

const STATES = [
  ['over',      'states/1-broke-flies.svg'],
  ['crack',     'states/2-overspend-crack.svg'],
  ['shatter',   'states/3-broken-halves.svg'],
  ['abundance', 'states/4-abundance-fountain.svg'],
  ['pace',      'states/5-pace-tilt.svg'],
];

function wrapper(svgText) {
  return `<!DOCTYPE html><html><head><meta charset="utf8"><style>
    html,body{margin:0;padding:0;width:100%;height:100%;background:#080c0b;overflow:hidden}
    body{display:grid;place-items:center}
    svg{width:250px;height:auto}
  </style></head><body>${svgText}</body></html>`;
}

rmSync(FRAMES, { recursive: true, force: true });
for (const [name] of STATES) mkdirSync(path.join(FRAMES, name), { recursive: true });
mkdirSync(path.join(FRAMES, 'sizzle'), { recursive: true });

const browser = await chromium.launch({ executablePath: '/usr/bin/google-chrome', args: ['--no-sandbox'] });
const page = await browser.newPage({ viewport: { width: 320, height: 420 } });
const allFrames = [];

for (const [name, file] of STATES) {
  const html = wrapper(readFileSync(path.join(HERE, file), 'utf8'));
  const url = 'data:text/html;charset=utf-8,' + encodeURIComponent(html);
  await page.goto(url, { waitUntil: 'load' });
  await page.evaluate(() => {
    document.getAnimations({ subtree: true }).forEach(a => { a.pause(); a.currentTime = 0; });
  });
  for (let i = 0; i < STEPS; i++) {
    const t = i / FPS * 1000;
    await page.evaluate((tt) => { document.getAnimations({ subtree: true }).forEach(a => { a.currentTime = tt; }); }, t);
    await page.screenshot({ path: path.join(FRAMES, name, String(i).padStart(3, '0') + '.png') });
  }
  console.log(name, 'frames done:', STEPS);
}

await browser.close();

/* GIF لكل حالة — باليتة مخصّصة للخلفية الداكنة */
for (const [name] of STATES) {
  const dir = path.join(FRAMES, name);
  execSync(`ffmpeg -y -loglevel error -framerate ${FPS} -i "${dir}/%03d.png" -vf "palettegen=max_colors=192" -frames:v 1 /tmp/opencode/pal_${name}.png`);
  execSync(`ffmpeg -y -loglevel error -loop 1 -framerate ${FPS} -i "${dir}/%03d.png" -i /tmp/opencode/pal_${name}.png -lavfi "[0:v][1:v]paletteuse=dither=bayer" -t ${DUR} "${HERE}/preview/${name}.gif"`);
  console.log('GIF:', name);
}

/* MP4 عريض للعرض — كل الحالات بالترتيب */
const sizzleList = STATES.map(([n]) => path.join(FRAMES, n)).join(':');
execSync(`ffmpeg -y -loglevel error ${STATES.map(([n]) => `-i "${path.join(FRAMES, n)}/%03d.png"`).join(' ')} -filter_complex "${STATES.map((_, i) => `[${i}:v]fps=${FPS},scale=320:420,setsar=1,format=yuv420p[v${i}]`).join(';')};${STATES.map((_, i) => `[v${i}]`).join('')}concat=n=${STATES.length}:v=1:a=0" -c:v libopenh264 -b:v 2M -pix_fmt yuv420p -movflags +faststart "${HERE}/preview/wafeer-logo-states.mp4"`);
console.log('MP4 done');
