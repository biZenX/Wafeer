import { createRequire } from 'module';
const require = createRequire('/home/zen/github-readme-terminal/GitAscii/');
const { chromium } = require('playwright-core');
import { mkdirSync } from 'fs';
import { execSync } from 'child_process';

const browser = await chromium.launch({
  executablePath: '/usr/bin/google-chrome',
  args: ['--no-sandbox', '--disable-setuid-sandbox']
});
const page = await browser.newPage();
await page.setViewportSize({ width: 340, height: 400 });
await page.goto('file:///home/zen/Ana/design/logo-motion/states/1-broke-flies.html');

mkdirSync('/tmp/clean_frames', { recursive: true });
console.log('Recording 45 frames of pure 3D coin flip & 40Hz wings...');
for (let i = 0; i < 45; i++) {
  await page.screenshot({ path: `/tmp/clean_frames/f_${String(i).padStart(3, '0')}.png` });
  await new Promise(r => setTimeout(r, 55));
}
await browser.close();

console.log('Encoding GIF...');
execSync(`ffmpeg -y -framerate 18 -i "/tmp/clean_frames/f_%03d.png" -vf "split[s0][s1];[s0]palettegen=max_colors=192[p];[s1][p]paletteuse" /home/zen/.gemini/antigravity/brain/bfd15df1-9e89-4a79-9ff9-390ad5c87ad1/wafeer_3d_coin_flies.gif`);

console.log('Encoding MP4/WebM video...');
execSync(`ffmpeg -y -framerate 25 -i "/tmp/clean_frames/f_%03d.png" -c:v libvpx-vp9 -b:v 1M -pix_fmt yuv420p /home/zen/.gemini/antigravity/brain/bfd15df1-9e89-4a79-9ff9-390ad5c87ad1/wafeer_3d_coin_flies.webm`);
console.log('COMPLETE!');
