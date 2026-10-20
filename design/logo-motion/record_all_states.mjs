import { createRequire } from 'module';
const require = createRequire('/home/zen/github-readme-terminal/GitAscii/');
const { chromium } = require('playwright-core');
import { mkdirSync, rmSync } from 'fs';
import { execSync } from 'child_process';

const ARTIFACT_DIR = '/home/zen/.gemini/antigravity/brain/bfd15df1-9e89-4a79-9ff9-390ad5c87ad1';

const states = [
  { name: '1_broke', file: '1-broke-flies.html', frames: 40, delay: 55, fps: 18 },
  { name: '2_crack', file: '2-overspend-crack.html', frames: 45, delay: 55, fps: 18 },
  { name: '3_shatter', file: '3-broken-halves.html', frames: 45, delay: 55, fps: 18 },
  { name: '4_abundance', file: '4-abundance-fountain.html', frames: 45, delay: 55, fps: 18 },
  { name: '5_pace', file: '5-pace-tilt.html', frames: 40, delay: 55, fps: 18 },
];

const browser = await chromium.launch({
  executablePath: '/usr/bin/google-chrome',
  args: ['--no-sandbox', '--disable-setuid-sandbox']
});

for (const s of states) {
  console.log(`\n=== Recording State: ${s.name} (${s.file}) ===`);
  const page = await browser.newPage();
  await page.setViewportSize({ width: 340, height: 400 });
  await page.goto(`file:///home/zen/Ana/design/logo-motion/states/${s.file}`);
  await page.waitForTimeout(400);

  const tmpDir = `/tmp/record_${s.name}`;
  rmSync(tmpDir, { recursive: true, force: true });
  mkdirSync(tmpDir, { recursive: true });

  for (let i = 0; i < s.frames; i++) {
    await page.screenshot({ path: `${tmpDir}/f_${String(i).padStart(3, '0')}.png` });
    await new Promise(r => setTimeout(r, s.delay));
  }
  await page.close();

  console.log(`Encoding GIF for ${s.name}...`);
  const gifOut = `${ARTIFACT_DIR}/wafeer_mascot_${s.name}.gif`;
  execSync(`ffmpeg -y -framerate ${s.fps} -i "${tmpDir}/f_%03d.png" -vf "split[s0][s1];[s0]palettegen=max_colors=192[p];[s1][p]paletteuse" "${gifOut}"`);

  console.log(`Encoding WebM for ${s.name}...`);
  const webmOut = `${ARTIFACT_DIR}/wafeer_mascot_${s.name}.webm`;
  execSync(`ffmpeg -y -framerate 25 -i "${tmpDir}/f_%03d.png" -c:v libvpx-vp9 -b:v 1M -pix_fmt yuv420p "${webmOut}"`);

  console.log(`Saved: wafeer_mascot_${s.name}.gif and .webm`);
}

await browser.close();
console.log('\nALL 5 STATES RECORDED AND ENCODED SUCCESSFULLY!');
