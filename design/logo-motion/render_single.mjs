import { createRequire } from 'module';
const require = createRequire('/home/zen/github-readme-terminal/GitAscii/');
const { chromium } = require('playwright-core');
import { readFileSync, mkdirSync } from 'fs';
import { execSync } from 'child_process';

const browser = await chromium.launch({
  executablePath: '/usr/bin/google-chrome',
  args: ['--no-sandbox', '--disable-setuid-sandbox']
});
const page = await browser.newPage();
await page.setViewportSize({ width: 360, height: 460 });

const svgText = readFileSync('/home/zen/Ana/design/logo-motion/states/1-broke-flies.svg', 'utf8');
const html = `<!DOCTYPE html><html><head><meta charset="utf8"><style>
  html,body{margin:0;padding:0;width:100%;height:100%;background:#080c0b;overflow:hidden;display:grid;place-items:center}
  svg{width:82%;height:auto;filter:drop-shadow(0 0 16px rgba(72,254,189,.35))}
</style></head><body>${svgText}</body></html>`;

await page.setContent(html);

mkdirSync('/tmp/render_anim', { recursive: true });
console.log('Capturing 45 frames...');
for (let i = 0; i < 45; i++) {
  // Advance document timeline
  await page.evaluate((timeMs) => {
    document.getAnimations().forEach(anim => {
      anim.currentTime = timeMs;
    });
  }, (i / 12.5) * 1000);
  await page.screenshot({ path: `/tmp/render_anim/frame_${String(i).padStart(3, '0')}.png` });
}
await browser.close();

console.log('Encoding GIF with ffmpeg...');
execSync(`ffmpeg -y -framerate 12.5 -i "/tmp/render_anim/frame_%03d.png" -vf "split[s0][s1];[s0]palettegen=max_colors=192[p];[s1][p]paletteuse" /home/zen/.gemini/antigravity/brain/bfd15df1-9e89-4a79-9ff9-390ad5c87ad1/over_live_physics.gif`);
console.log('SUCCESS! Rendered to /home/zen/.gemini/antigravity/brain/bfd15df1-9e89-4a79-9ff9-390ad5c87ad1/over_live_physics.gif');
